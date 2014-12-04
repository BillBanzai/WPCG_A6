package computergraphics.hlsvis.test;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import computergraphics.hlsvis.hls.TransportEvent;
import computergraphics.hlsvis.hls.TransportEvent.EventType;

/**
 * Testklasse for TransportEvents.
 * 
 * @author Philipp Jenke
 *
 */
public class TestTransportEvent {

	@Test
	public void testToJson() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = formatter.parse("2014-11-03 01:44:30");
		} catch (ParseException e) {
			System.out.println("Failed to parse date.");
			assertTrue(false);
		}
		double[] coords = new double[] { 0.729, 0.316 };
		TransportEvent ereignis = new TransportEvent(0, 1, date,
				EventType.ABGEFAHREN, coords);
		String jsonMessage = ereignis.toJson();
		String expected = "{\"SendungsNr\":0,\"AuftragsNr\":1,\"Timestamp\":\"2014-11-03 01:44:30\",\"Typ\":0,\"Gpskoords\":{\"X\":0.729,\"Y\":0.316}}";
		jsonMessage = jsonMessage.replace("\n", "");
		assertEquals(expected, jsonMessage);
	}
}
