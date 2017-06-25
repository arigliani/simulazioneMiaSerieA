package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	private SerieADAO dao;
	private List<Match> partite;
	private Map<Integer,Match> mapPartite= new HashMap<>();
	private List<Team> squadre;
	private List<Team> squadrePartecipi;
	private Map<Team,Classifica> classifica;
	private Season stagione;
	private DirectedWeightedMultigraph<Team,DefaultWeightedEdge> graph;
	
	
	public Model(){
		dao=new SerieADAO();
		this.squadrePartecipi= new LinkedList<>();
	}
	
	private void creaGrafo(Season s){
		this.graph=new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
		stagione=s;
		this.squadre=dao.listTeams();
		Graphs.addAllVertices(this.graph, this.squadre) ;
		this.partite=dao.listMatch(s);
		
		int peso=-10;
		int i=0;
		for(Match p: partite){
			mapPartite.put(p.getId(), p);
			if(p.getFthg()>p.getFtag()){
				peso=1;
			}else if(p.getFthg()<p.getFtag()){
				peso=-1;
			}else{
				peso=0;
			}
			
			Graphs.addEdgeWithVertices(graph, p.getHomeTeam(), p.getAwayTeam(), peso);
			this.squadrePartecipi.add(p.getHomeTeam());
			this.squadrePartecipi.add(p.getAwayTeam());
			
			
			if(this.classifica.containsKey(p.getHomeTeam())){
				if(peso==1){
					classifica.get(p.getHomeTeam()).vittoria();
				}else if(peso==0){
					classifica.get(p.getHomeTeam()).pareggio();
				} else if(peso==-1){
					if(this.classifica.containsKey(p.getAwayTeam())){
						classifica.get(p.getAwayTeam()).vittoria();
					}else{
						Classifica temp1= new Classifica(p.getAwayTeam(),0);
						this.classifica.put(p.getAwayTeam(), temp1);
						classifica.get(p.getAwayTeam()).vittoria();
					}
				}
			}else{
				Classifica temp= new Classifica(p.getHomeTeam(),0);
				this.classifica.put(p.getHomeTeam(), temp);
				if(peso==1){
					classifica.get(p.getHomeTeam()).vittoria();
				}else if(peso==0){
					classifica.get(p.getHomeTeam()).pareggio();
				}else if(peso==-1){
					if(this.classifica.containsKey(p.getAwayTeam())){
						classifica.get(p.getAwayTeam()).vittoria();
					}else{
						Classifica temp1= new Classifica(p.getAwayTeam(),0);
						this.classifica.put(p.getAwayTeam(), temp1);
						classifica.get(p.getAwayTeam()).vittoria();
					}
				}
				
			}
			
			
		}
		//System.out.println(this.graph);
		
		
		
	}
	
	public List<Classifica> classifica(Season s){
		this.classifica=new HashMap<>();
		this.creaGrafo(s);
		List<Classifica> temp= new LinkedList<>(this.classifica.values());
		Collections.sort(temp);
		return temp;
	
	}
	
	public List<Team> concatenate(Season s){
		this.classifica(s);
		riduciGrafo(18);
		List<Team> best=new ArrayList();
		List<Team> provvisoria=new ArrayList();
		for(int i=0; i<this.squadrePartecipi.size();i++){
		Team vertice= squadrePartecipi.get(i);
		best=this.itera(vertice, provvisoria, best);}
		
	//	Set<DefaultWeightedEdge> domino=this.getDomino(best);
		System.out.println("best" +best);
	//	System.out.println("domino" +domino);
		return best;
	}

	private Set<DefaultWeightedEdge> getDomino(List<Team> best) {
		Set<DefaultWeightedEdge> domino= new HashSet<>();
		for(Team t: best){
			domino.addAll(graph.outgoingEdgesOf(t));
		}
		return domino;
	}

	private List<Team> itera(Team vertice, List<Team> provvisoria, List<Team> best) {
		//System.out.println(vertice);
		if(provvisoria.size()>best.size()){
			best.clear();
			best.addAll(provvisoria);
		}
	   //  for(Team t: this.squadrePartecipi){
	    //	 vertice=t;
	    	 
			for(Team team: Graphs.neighborListOf(graph, vertice)){
				
				DefaultWeightedEdge e=graph.getEdge(vertice, team);
				if(graph.getEdgeWeight(e)==1){
					//Team temp=Graphs.getOppositeVertex(graph, e, vertice);
					if(!provvisoria.contains(team)){
					provvisoria.add(team);
					this.itera(team, provvisoria, best);
					provvisoria.remove(team);}
				}
			}
	   //  }
			return best;
		
	}
	
	private void riduciGrafo(int dim) {
		Set<Team> togliere = new HashSet<>() ;
		
		Iterator<Team> iter = graph.vertexSet().iterator() ;
		for(int i=0; i<graph.vertexSet().size()-dim; i++) {
			togliere.add(iter.next()) ;
		}
		graph.removeAllVertices(togliere) ;
		System.err.println("Attenzione: cancello dei vertici dal grafo");
		System.err.println("Vertici rimasti: "+graph.vertexSet().size()+"\n");
		this.squadrePartecipi.clear();
		this.squadrePartecipi.addAll(this.graph.vertexSet());
		System.out.println(graph);
	}

}
