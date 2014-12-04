package computergraphics.hlsvis.test;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;

import org.junit.Test;

import computergraphics.hlsvis.hls.TransportOrder;
import computergraphics.hlsvis.hls.City.Location;

/**
 * Testklasse für einen FrachtAuftrag.
 * 
 * @author Philipp Jenke
 *
 */
public class TestTransportOrder {

	@Test
	public void testFromJson() {
		TransportOrder order = new TransportOrder();
		order.fromJson("{ \"SendungsNr\":1,\n\"AuftragsNr\":23,\n\"StartLokation\":\"Hamburg\",\n\"ZielLokation\":\"Berlin\",\n\"Startzeit\":\"2014-11-03 01:37:47\",\n\"Endezeit\":\"2014-11-04 02:41:47\"\n }");
		assertEquals(1, order.getDeliveryNumber());
		assertEquals(23, order.getOrderNumber());
		assertEquals(Location.Hamburg, order.getStartLocation());
		assertEquals(Location.Berlin, order.getTargetLocation());
		assertEquals(new GregorianCalendar(2014, 10, 03, 01, 37, 47).getTime(),
				order.getStartTime());
		assertEquals(new GregorianCalendar(2014, 10, 04, 02, 41, 47).getTime(),
				order.getDeliveryTime());
	}
}
