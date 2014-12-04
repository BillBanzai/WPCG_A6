package computergraphics.hlsvis.hls;

import computergraphics.hlsvis.hls.City.Location;

/**
 * A connection between to cities.
 * 
 * @author Philipp Jenke
 *
 */
public class Connection {

	/**
	 * First city.
	 */
	private final Location startLocation;

	/**
	 * Second city.
	 */
	private final Location targetLocation;

	/**
	 * Time to move a package from one city to the other in minutes..
	 */
	private final int distance;

	/**
	 * Construktor.
	 */
	public Connection(Location startLocation, Location targetLocation, int dauer) {
		this.startLocation = startLocation;
		this.targetLocation = targetLocation;
		this.distance = dauer;
	}

	/**
	 * Getter.
	 */
	public Location getStartLocation() {
		return startLocation;
	}

	/**
	 * Getter.
	 */
	public Location getTargetLocation() {
		return targetLocation;
	}

	/**
	 * Getter for the distance in minutes.
	 */
	public int getDistance() {
		return distance;
	}
}
