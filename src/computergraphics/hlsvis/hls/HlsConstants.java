package computergraphics.hlsvis.hls;

/**
 * Constants in the JSON protocols.
 * 
 * @author Philipp Jenke
 *
 */
public interface HlsConstants {
	public static final String SENDUNGSNUMMER = "SendungsNr";
	public static final String AUFTRAGSNUMMER = "AuftragsNr";
	public static final String STARTLOKATION = "StartLokation";
	public static final String ZIELLOKATION = "ZielLokation";
	public static final String STARTZEIT = "Startzeit";
	public static final String ENDEZEIT = "Endezeit";
	public static final String TRANSPORTBEZIEHUNGEN = "Transportbeziehungen";
	public static final String DAUER = "Dauer";

	/**
	 * Queue names: 'Frachtaufräge'
	 */
	public static final String FRACHTAUFTRAG_QUEUE = 
			"HLS.Queue.Frachtauftrag.CGTeam14";
	//HLS.Queue.Frachtauftrag.SwPTeam5.CGTeam14

	/**
	 * Queue names: 'Sendungsereignisse'
	 */
	public static final String SENDUNGSEREIGNIS_QUEUE = 
			"HLS.Queue.Sendungsereignis.CGTeam14";
	//HLS.Queue.Sendungsereignis.SwPTeam5.CGTeam14

	/**
	 * Queue names: 'Transportbeziehungen'
	 */
	public static final String TRANSPORZBEZIEHUNGEN_QUEUE = 
	        "HLS.Queue.Transportbeziehungen.CGTeam14";
	//HLS.Queue.Transportbeziehungen.SwPTeam5.CGTeam14

	/**
	 * Number of minutes each visualizaition tick represents
	 */
	public static final int MINUTES_PER_TICK = 5;

	/**
	 * Number of minutes between two "ON-WAY" events to the queue.
	 */
	public static final int ON_WAY_EVENT_INTERVAL = 15;

	/**
	 * Number of minutes until the HLS simulator creates a new order.
	 */
	public static final int NEW_ORDER_INTERVAL = 30;
	
	
	public static final String MQ_PASSWORD = "Rwj9joAi";
	// public static final String MQ_PASSWORD = "guest";
	public static final String MQ_USERNAME = "CGTeams";
	// public static final String MQ_USERNAME = "guest";
	public static final String MQ_SERVER_URL = "win-devel.informatik.haw-hamburg.de";
	// public static final String MQ_SERVER_URL = "localhost";

}
