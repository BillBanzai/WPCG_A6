package computergraphics.hlsvis.test;

import static org.junit.Assert.*;

import org.junit.Test;

import computergraphics.hlsvis.hls.Connection;
import computergraphics.hlsvis.hls.Connections;
import computergraphics.hlsvis.hls.City.Location;

/**
 * JUnit-Testklasse für Transportbeziehungen
 * 
 * @author Philipp Jenke
 *
 */
public class TestTransportConnections {

	@Test
	public void testToJson() {
		Connections transportConnections = new Connections();
		transportConnections.addConnection(new Connection(
				Location.Berlin, Location.Hamburg, 196));
		transportConnections.addConnection(new Connection(
				Location.Hamburg, Location.Koeln, 256));
		String jSonMessage = transportConnections.toJson();
		String expected = "{\n\"Transportbeziehungen\":\n[\n{\"StartLokation\":\"Berlin\",\"ZielLokation\":\"Hamburg\",\"Dauer\":196},\n{\"StartLokation\":\"Hamburg\",\"ZielLokation\":\"Koeln\",\"Dauer\":256}\n]\n}";
		assertEquals(expected, jSonMessage);
	}
}
