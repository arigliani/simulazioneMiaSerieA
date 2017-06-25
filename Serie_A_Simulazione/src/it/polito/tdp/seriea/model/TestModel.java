package it.polito.tdp.seriea.model;

import it.polito.tdp.seriea.db.SerieADAO;

public class TestModel {

	public static void main(String[] args) {
		Model m=new Model();
		SerieADAO dao= new SerieADAO();
		System.out.println(dao.listSeasons().get(3).getSeason());
		//m.creaGrafo(dao.listSeasons().get(3));
		m.concatenate(dao.listSeasons().get(3));

	}

}
