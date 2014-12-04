package computergraphics.hlsvis.hls;

import java.util.ArrayList;
import java.util.List;

import computergraphics.hlsvis.graph.GraphAdjacencyList;
import computergraphics.hlsvis.graph.IGraph;
import computergraphics.hlsvis.graph.Node;
import computergraphics.hlsvis.hls.City.Location;

/**
 * Container for all information about the transport network.
 * 
 * @author Philipp Jenke
 *
 */
public class TransportNetwork {

	/**
	 * The transport network is representes as a graph.
	 */
	private static IGraph<City> transportNetwork;

	/**
	 * Access to the singleton instance.
	 */
	public static IGraph<City> getTransportNetwork() {
		if (transportNetwork == null) {
			createNetwork();
		}
		return transportNetwork;
	}

	/**
	 * Generate the HLS test network (Germany cities).
	 */
	private static void createNetwork() {
		Node<City> berlin = new Node<City>(
				new City(Location.Berlin, 0.72, 0.31));
		Node<City> hamburg = new Node<City>(new City(Location.Hamburg, 0.46,
				0.19));
		Node<City> muenchen = new Node<City>(new City(Location.Muenchen, 0.59,
				0.88));
		Node<City> koeln = new Node<City>(new City(Location.Koeln, 0.21, 0.51));
		Node<City> frankfurt = new Node<City>(new City(Location.Frankfurt,
				0.35, 0.63));
		Node<City> stuttgart = new Node<City>(new City(Location.Stuttgart,
				0.39, 0.80));
		Node<City> duesseldorf = new Node<City>(new City(Location.Duesseldorf,
				0.20, 0.48));
		Node<City> dortmund = new Node<City>(new City(Location.Dortmund, 0.26,
				0.46));
		Node<City> essen = new Node<City>(new City(Location.Essen, 0.23, 0.44));
		Node<City> bremen = new Node<City>(
				new City(Location.Bremen, 0.37, 0.25));
		transportNetwork = new GraphAdjacencyList<City>();

		// Erzeuge Städte
		transportNetwork.addNode(berlin);
		transportNetwork.addNode(hamburg);
		transportNetwork.addNode(muenchen);
		transportNetwork.addNode(koeln);
		transportNetwork.addNode(frankfurt);
		transportNetwork.addNode(stuttgart);
		transportNetwork.addNode(duesseldorf);
		transportNetwork.addNode(dortmund);
		transportNetwork.addNode(essen);
		transportNetwork.addNode(bremen);

		// Erzeuge Verbindungen zwischen Städten
		transportNetwork.addEdge(hamburg, berlin, 150);
		transportNetwork.addEdge(hamburg, bremen, 55);
		transportNetwork.addEdge(hamburg, dortmund, 167);
		transportNetwork.addEdge(bremen, duesseldorf, 161);
		transportNetwork.addEdge(duesseldorf, essen, 32);
		transportNetwork.addEdge(duesseldorf, koeln, 30);
		transportNetwork.addEdge(essen, dortmund, 23);
		transportNetwork.addEdge(koeln, dortmund, 74);
		transportNetwork.addEdge(koeln, frankfurt, 81);
		transportNetwork.addEdge(frankfurt, stuttgart, 90);
		transportNetwork.addEdge(stuttgart, muenchen, 145);
		transportNetwork.addEdge(berlin, muenchen, 315);
		transportNetwork.addEdge(berlin, frankfurt, 255);
	}

	/**
	 * Find the node for a given location.
	 */
	static Node<City> getNode(Location location) {
		for (int i = 0; i < getTransportNetwork().getNumberOfNodes(); i++) {
			if (getTransportNetwork().getNode(i).getElement().getId() == location) {
				return getTransportNetwork().getNode(i);
			}
		}
		return null;
	}

	/**
	 * Return all available connections.
	 */
	public static List<Connection> getAllConnections() {
		List<Connection> connections = new ArrayList<Connection>();
		for (int i = 0; i < getTransportNetwork().getNumberOfNodes(); i++) {
			Node<City> city1 = getTransportNetwork().getNode(i);
			for (int j = i + 1; j < getTransportNetwork().getNumberOfNodes(); j++) {
				Node<City> city2 = getTransportNetwork().getNode(j);
				if (getTransportNetwork().edgeExistst(city1, city2)) {
					connections.add(new Connection(city1.getElement().getId(),
							city2.getElement().getId(), TransportNetwork
									.getDistance(city1.getElement(),
											city2.getElement())));
				}
			}
		}
		return connections;
	}

	/**
	 * Liefert die Entfernung zwischen den beiden Städten. Setzt voraus, dass
	 * beide Städte durch eine Kante direkz verbunden sind.
	 */
	public static int getDistance(City stadt1, City stadt2) {
		Node<City> startStadt = getNode(stadt1.getId());
		Node<City> zielStadt = getNode(stadt2.getId());
		return (int) getTransportNetwork().getEdgeWeight(startStadt, zielStadt);
	}

	/**
	 * Return distance between two locations.
	 */
	public static double getEntfernung(Location startLokation,
			Location zielLokation) {
		City stadt1 = getCity(startLokation);
		City stadt2 = getCity(zielLokation);
		return getDistance(stadt1, stadt2);
	}

	/**
	 * Return city object for a given location.
	 */
	public static City getCity(Location lokation) {
		for (int i = 0; i < getTransportNetwork().getNumberOfNodes(); i++) {
			if (getTransportNetwork().getNode(i).getElement().getId() == lokation) {
				return getTransportNetwork().getNode(i).getElement();
			}
		}
		return null;
	}
}
