package it.polito.tdp.seriea.model;

public class Classifica  implements Comparable<Classifica>{
	private Team team;
	private int punteggio;
	public Classifica(Team team, int punteggio) {
		super();
		this.team = team;
		this.punteggio = punteggio;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Classifica other = (Classifica) obj;
		if (team == null) {
			if (other.team != null)
				return false;
		} else if (!team.equals(other.team))
			return false;
		return true;
	}
	public int getPunteggio() {
		return punteggio;
	}
	
	public void vittoria(){
		this.punteggio+=3;
	}
	public void pareggio(){
		this.punteggio+=1;
	}
	public void setPunteggio(int punteggio) {
		this.punteggio = punteggio;
	}
	public Team getTeam() {
		return team;
	}
	@Override
	public String toString() {
		return "\n"+ team + " punteggio=" + punteggio;
	}
	@Override
	public int compareTo(Classifica b) {
		
		return -(this.getPunteggio()-b.getPunteggio());
	}
	
	
	

}
