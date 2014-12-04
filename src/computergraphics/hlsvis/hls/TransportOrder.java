package computergraphics.hlsvis.hls;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import computergraphics.hlsvis.hls.City.Location;

/**
 * Order for a transportation (Frachtauftrag)
 * 
 * @author Philipp Jenke
 *
 */
public class TransportOrder {
	/**
	 * "Sendungsnummer".
	 */
	private int deliveryNumber;

	/**
	 * "Auftragsnummer".
	 */
	private int orderNumber;

	/**
	 * "StartLokation"
	 */
	private Location startLocation;

	/**
	 * "StartLokation"
	 */
	private Location targetLocation;

	/**
	 * "Startzeit"
	 */
	private Date startTime;

	/**
	 * "ZielZeit".
	 */
	private Date deliveryTime;

	/**
	 * Default constructor.
	 */
	public TransportOrder() {
	}

	/**
	 * Constructor
	 */
	public TransportOrder(int deliveryNumber, int orderNumber,
			Location startLocation, Location targetLocation, Date startTime,
			Date deliveryTime) {
		this.deliveryNumber = deliveryNumber;
		this.orderNumber = orderNumber;
		this.startLocation = startLocation;
		this.targetLocation = targetLocation;
		this.startTime = new Date(startTime.getTime());
		this.deliveryTime = new Date(deliveryTime.getTime());
	}

	/**
	 * Create a FrachtAuftrag from a JSON string representation.
	 */
	public void fromJson(String jsonString) {
		StringReader reader = new StringReader(jsonString);
		JsonParser parser = Json.createParser(reader);
		while (parser.hasNext()) {
			Event e = parser.next();
			if (e == Event.KEY_NAME
					&& parser.getString().equals(HlsConstants.SENDUNGSNUMMER)) {
				e = parser.next();
				deliveryNumber = Integer.parseInt(parser.getString());
			} else if (e == Event.KEY_NAME
					&& parser.getString().equals(HlsConstants.AUFTRAGSNUMMER)) {
				e = parser.next();
				orderNumber = Integer.parseInt(parser.getString());
			} else if (e == Event.KEY_NAME
					&& parser.getString().equals(HlsConstants.STARTLOKATION)) {
				e = parser.next();
				startLocation = Location.valueOf(parser.getString());
			} else if (e == Event.KEY_NAME
					&& parser.getString().equals(HlsConstants.ZIELLOKATION)) {
				e = parser.next();
				targetLocation = Location.valueOf(parser.getString());
			} else if (e == Event.KEY_NAME
					&& parser.getString().equals(HlsConstants.STARTZEIT)) {
				e = parser.next();
				try {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					startTime = formatter.parse(parser.getString());
				} catch (ParseException e1) {
					System.out.println("Failed parse date string.");
				}
			} else if (e == Event.KEY_NAME
					&& parser.getString().equals(HlsConstants.ENDEZEIT)) {
				e = parser.next();
				try {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					deliveryTime = formatter.parse(parser.getString());
				} catch (ParseException e1) {
					System.out.println("Failed parse date string.");
				}
			}
		}
	}

	public int getDeliveryNumber() {
		return deliveryNumber;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public Location getStartLocation() {
		return startLocation;
	}

	public Location getTargetLocation() {
		return targetLocation;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getDeliveryTime() {
		return deliveryTime;
	}

	/**
	 * Erzeuge Json-Nachricht auf dem Frachtauftrag.
	 */
	public String toJson() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return "{ \"SendungsNr\":" + getDeliveryNumber() + ",\n\"AuftragsNr\":"
				+ getOrderNumber() + ",\n\"StartLokation\":\""
				+ getStartLocation().toString() + "\",\n\"ZielLokation\":\""
				+ getTargetLocation().toString() + "\",\n\"Startzeit\":\""
				+ formatter.format(getStartTime()) + "\",\n\"Endezeit\":\""
				+ formatter.format(getDeliveryTime()) + "\"\n }";
	}

	@Override
	public String toString() {
		return startLocation + " -> " + targetLocation;
	}
}
