package computergraphics.hlsvis.hls;

/**
 * Representation of a city in the transport network
 * 
 * @author Philipp Jenke
 *
 */
public class City {
	/**
	 * List of existing city IDs (=names).
	 */
	public static enum Location {
		Berlin, Bremen, Dortmund, Duesseldorf, Essen, Frankfurt, Hamburg, Koeln, Muenchen, Stuttgart
	};

	/**
	 * Id if the city.
	 */
	private final Location id;

	/**
	 * Coordinates on the map in [0,1]^2
	 */
	private final double[] coords;

	/**
	 * Construktor
	 */
	public City(Location id, double coordX, double coordZ) {
		this.id = id;
		this.coords = new double[] { coordX, coordZ };
	}

	/**
	 * Getter.
	 */
	public Location getId() {
		return id;
	}

	/**
	 * Getter.
	 */
	public double[] getCoords() {
		return coords;
	}
}
