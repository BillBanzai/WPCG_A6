package computergraphics.hlsvis.hls;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Repräsentation of an event during a transport
 * 
 * @author Philipp Jenke
 *
 */
public class TransportEvent {

	/**
	 * Enum of possible events.
	 * 
	 * @author Philipp Jenke
	 *
	 */
	public static enum EventType {
		ABGEFAHREN, UNTERWEGS, ANGEKOMMEN
	}

	/**
	 * Package number. (Sendungsnummer)
	 */
	private int packageNumber;
	/**
	 * Oder number (Auftragsnummer)
	 */
	private int orderNumber;

	/**
	 * Time stamp
	 */
	private Date timestamp = new Date();

	/**
	 * Type of the event.
	 */
	private EventType type;

	/**
	 * Current coordinates on the map.
	 */
	private double[] gpsCoords = { 0, 0 };

	/**
	 * Construktor
	 */
	public TransportEvent(int packageNumber, int orderNumber, Date timestamp,
			EventType type, double[] gpsCoords) {
		this.packageNumber = packageNumber;
		this.orderNumber = orderNumber;
		this.timestamp.setTime(timestamp.getTime());
		this.type = type;
		this.gpsCoords[0] = gpsCoords[0];
		this.gpsCoords[1] = gpsCoords[1];
	}

	/**
	 * Create a JSON message for the event.
	 */
	public String toJson() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return "{\n\"SendungsNr\":" + packageNumber + ",\n\"AuftragsNr\":"
				+ orderNumber + ",\n\"Timestamp\":\""
				+ formatter.format(timestamp) + "\",\n\"Typ\":"
				+ type.ordinal() + ",\n\"Gpskoords\":\n{\"X\":" + gpsCoords[0]
				+ ",\n\"Y\":" + gpsCoords[1] + "\n}}";
	}
}
