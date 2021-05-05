package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	//Distanza media minima percorsa
	//Grafo: semplice, non orientato e pesato
	//Vertice: aeroporti; Archi:(almeno1)volo tra due aeroporti
	//Peso=distanza media percorsa
	List<Airport> aeroporti;
	List<Flight> flights;
	List<Airline> airlines;
	ExtFlightDelaysDAO dao;
	Graph<Airport,DefaultEdge> grafo;
	Map<Integer,Airport> mapa;
	
	
	
	
	public Graph<Airport, DefaultEdge> getGrafo() {
		return grafo;
	}

	public Model() {
		 dao= new ExtFlightDelaysDAO();
		aeroporti = new ArrayList(this.dao.loadAllAirports());
		mapa = new HashMap<Integer,Airport>();
		mapaAeroporti();
		flights = new ArrayList(this.dao.loadAllFlights());
		airlines = new ArrayList(this.dao.loadAllAirlines());
	}
	
	public void mapaAeroporti() {
		for(Airport a : aeroporti ) {
			if(a!=null) {
				mapa.put(a.getId(), a);
			}
		}
	}
	public void creaGrafo(int x) { //x=distanza minima
		
		this.grafo = new SimpleWeightedGraph(DefaultEdge.class);
		Graphs.addAllVertices(this.grafo, aeroporti);
		
		for(Flight f : flights) {
			int distanza =f.getDistance();
			if(f!=null && distanza>x) {
				int originAirportID = f.getOriginAirportId();
				int destinationAirportID = f.getDestinationAirportId();
				Airport a1 = mapa.get(originAirportID);
				Airport a2 = mapa.get(destinationAirportID);
				if(mapa.containsKey(originAirportID) && mapa.containsKey(destinationAirportID) && !grafo.containsEdge(a1, a2) && !grafo.containsEdge(a2,a1) && a1!=a2) {
					grafo.setEdgeWeight(grafo.addEdge(a1, a2), getDistance(a1,a2));
					//System.out.println("Aggiunto "+a1.getAirportName()+"("+a1.getId()+")"+" "+a2.getAirportName()+"("+a2.getId()+")"+"\n");
					System.out.println(a1.getAirportName()+" - "+a2.getAirportName()+" : "+f.getDistance());
				}
			}
		}
		//System.out.format("Grafo creato con %d vertici e %d archi\n",
		//		this.grafo.vertexSet().size(), this.grafo.edgeSet().size()) ;
		
	}
	
	
	/**
	 * Returns the Distance between airports 
	 * @param a1
	 * @param a2
	 * @return -1 if this edge doesn't exist; 
	 * Distance (int) x
	 */
	public int getDistance(Airport a1, Airport a2) {
		if(!grafo.containsEdge(a1, a2) || !grafo.containsEdge(a2, a1)) {
			return -1;	
		} 
		int x =0;
		if(mapa.containsKey(a1.getId())) {
			for(Flight f : flights ) {
				if(f!=null && f.getDestinationAirportId()==a1.getId()) {
					if(f.getOriginAirportId()==a2.getId()) {
						x=f.getDistance();
					}
				}
			}
		} else if (mapa.containsKey(a2.getId())) {
			for(Flight f2 : flights) {
				if(f2!=null && f2.getDestinationAirportId()==a2.getId()) {
					if(f2.getOriginAirportId()==a1.getId()) {
						x=f2.getDistance();
					}
				}
			}
		}
		return x;
	}
	
	public String printGraph(Graph grafo1) {
		String result ="";
		Set<Airport> vertice = new HashSet( grafo1.vertexSet());
		Set<DefaultEdge> edge = new HashSet(grafo1.edgeSet());
		
		for(Flight f : flights) {
			Airport a1 = mapa.get(f.getOriginAirportId());
			Airport a2 = mapa.get(f.getDestinationAirportId());
			int distance = f.getDistance();
			if(edge.contains(grafo1.getEdge(a1, a2))) {
					edge.remove(grafo1.getEdge(a1, a2));
					result+=a1.getAirportName()+" - "+a2.getAirportName()+"  : "+distance+"\n";
			}else if (edge.contains(grafo.getEdge(a2, a1))) {
				edge.remove(grafo1.getEdge(a2, a1));
			result+=a2.getAirportName()+" - "+a1.getAirportName()+"  : "+distance+"\n";
			}
				
		}
		
		return result;
		
		
		
	}
	
	
	
}
