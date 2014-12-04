package computergraphics.hlsvis.hls;

import java.util.Date;
import java.util.List;

import computergraphics.hlsvis.graph.Node;
import computergraphics.hlsvis.graph.ShortestPathDijkstra;
import computergraphics.hlsvis.rabbitmq.RabbitMqCommunication;

/**
 * Simulator class for the HLS system.
 * 
 * @author Philipp Jenke
 *
 */
public class HlsSimulator {

	/**
	 * List of possible routes.
	 */
	private Connections connections;

	/**
	 * RabbitMQ-Communiction
	 */
	private RabbitMqCommunication transportOrderQueue = new RabbitMqCommunication(
			HlsConstants.FRACHTAUFTRAG_QUEUE, "127.0.0.1", "hls", "hls");

	private Date lastOrderTime;

	/**
	 * Constructor.
	 */
	public HlsSimulator() {
		connections = new Connections();
		connections.initWithAllConnections();
	}

	/**
	 * Tick event.
	 */
	public void tick(Date currentDate) {

		if (lastOrderTime == null) {
			lastOrderTime = new Date(currentDate.getTime()
					- HlsConstants.NEW_ORDER_INTERVAL * 60 * 1000);
		}

		// Create new order
		long timeToNexOrder = (lastOrderTime.getTime()
				+ HlsConstants.NEW_ORDER_INTERVAL * 60 * 1000 - currentDate
					.getTime()) / (60 * 1000);
		if (timeToNexOrder < 0 && connections.getNumberOfConnections() > 0) {

			int city1Index = (int) (Math.random() * TransportNetwork
					.getTransportNetwork().getNumberOfNodes());
			int city2Index = (int) (Math.random() * TransportNetwork
					.getTransportNetwork().getNumberOfNodes());
			City startCity = TransportNetwork.getTransportNetwork()
					.getNode(city1Index).getElement();
			City destinationCity = TransportNetwork.getTransportNetwork()
					.getNode(city2Index).getElement();
			int oneHour = 1 * 60 * 60 * 1000;
			Date startTime = new Date(currentDate.getTime() + oneHour);

			// Create shortest path
			ShortestPathDijkstra<City> dijkstra = new ShortestPathDijkstra<City>();
			List<Node<City>> path = dijkstra.computeShortestPath(
					TransportNetwork.getTransportNetwork(),
					TransportNetwork.getNode(startCity.getId()),
					TransportNetwork.getNode(destinationCity.getId()));

			System.out.println("Generated path with length " + path.size()
					+ " from " + startCity.getId() + " -> "
					+ destinationCity.getId());

			// Create an order for each segment
			for (int i = 1; i < path.size(); i++) {
				City city1 = path.get(i - 1).getElement();
				City city2 = path.get(i).getElement();
				Date endTime = new Date(startTime.getTime()
						+ TransportNetwork.getDistance(city1, city2) * 60
						* 1000);
				TransportOrder order = new TransportOrder(
						generierePackageNumber(), generateOrderNumber(),
						city1.getId(), city2.getId(), startTime, endTime);
				String jsonNachricht = order.toJson();

				transportOrderQueue.connect();
				transportOrderQueue.sendMessage(jsonNachricht);
				transportOrderQueue.disconnect();

				System.out.println("Frachtauftrag gesendet: "
						+ order.getDeliveryNumber());

				startTime.setTime(endTime.getTime());
			}

			lastOrderTime.setTime(currentDate.getTime());
		}
	}

	/**
	 * Generate random oder number.
	 */
	private int generateOrderNumber() {
		return (int) (Math.random() * 10000);
	}

	/**
	 * Generate random package number.
	 */
	private int generierePackageNumber() {
		return (int) (Math.random() * 10000);
	}

	/**
	 * Getter.
	 */
	public Connections getConnections() {
		return connections;
	}
}
