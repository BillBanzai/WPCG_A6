/**
 * Prof. Philipp Jenke
 * Hochschule für Angewandte Wissenschaften (HAW), Hamburg
 * Lecture demo program.
 */
package computergraphics.applications;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import computergraphics.datastructures.ITriangleMesh;
import computergraphics.datastructures.ObjIO;
import computergraphics.datastructures.TriangleMesh;
import computergraphics.framework.AbstractCGFrame;
import computergraphics.hlsvis.hls.City;
import computergraphics.hlsvis.hls.Connections;
import computergraphics.hlsvis.hls.HlsConstants;
import computergraphics.hlsvis.hls.HlsSimulator;
import computergraphics.hlsvis.hls.TransportEvent;
import computergraphics.hlsvis.hls.TransportNetwork;
import computergraphics.hlsvis.hls.TransportOrder;
import computergraphics.hlsvis.rabbitmq.IMessageCallback;
import computergraphics.hlsvis.rabbitmq.RabbitMqCommunication;
import computergraphics.math.Vector3;
import computergraphics.scenegraph.ColorNode;
import computergraphics.scenegraph.GroupNode;
import computergraphics.scenegraph.TranslationNode;
import computergraphics.scenegraph.TriangleMeshNode;
import computergraphics.scenegraph.TriangleMeshNodeTexture;
import computergraphics.util.Heightfield;
import computergraphics.scenegraph.MovableObject;

/**
 * Application for the first exercise.
 * 
 * @author Philipp Jenke
 * 
 */
public class CGFrame extends AbstractCGFrame {

	private static final int EVERY_N_MINUTES = 15;
	private static final String STARTING_TIME = "2014-12-08 00:00:00";
	private static final String HEIGHTMAP_PATH = "img/hoehenkarte_deutschland.png";
	private static final String COLOR_PATH = "img/karte_deutschland.jpg";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4257130065274995543L;
	/* "[...] beispielsweise weiß entspricht einem y‐Wert von 0.1."*/
	private static final double MAX_HEIGHT = 0.05;
	private static final String CUBE_PATH = "meshes/cube.obj";
	private static final String PLANE_PATH = "meshes/mesh_airplane_blue/airplane_blue_mesh.obj";
	private static final Vector3 SCALE_FROM_RESOLUTION = new Vector3(1.0/64d,1.0/64d,1.0/64d);
	private static final Vector3 PLANE_SCALE = new Vector3(1.0/128d,1.0/128d,1.0/128d);
	private static final int DEFAULT_RESOLUTION = 1006; //8x8
	private static final int ELAPSED_MINUTES_AFTER_TICK = 5;
	private static final long MILLIS_TO_MINUTES = 1000*60;
    
    //3. Die wegpunkte für die kugel erzeugen
    // Im uhrzeigersinn 
    
	
    private RabbitMqCommunication mqCommTransportLanes;
    private Date currentTime;
    
    private List<TransportOrder> rememberedOrders = new ArrayList<>();
	private TranslationNode translationNodeMobs;
	private RabbitMqCommunication mqCommEvents;
	
	
	private RabbitMqCommunication mqCommFreightContracts;
	/** Assoziation zwischen dem graphischen objekt, und dem Frachtauftrag, das
	 *  durch dieses MovableObject visualisiert wird. */
	private IdentityHashMap<MovableObject,TransportOrder> mobToOrderMap = 
			new IdentityHashMap<>();
	{
		mqCommFreightContracts = new RabbitMqCommunication(
				HlsConstants.FRACHTAUFTRAG_QUEUE, HlsConstants.MQ_SERVER_URL, 
				HlsConstants.MQ_USERNAME, HlsConstants.MQ_PASSWORD);
		/* diese Connection einmal aufmachen und erst bei programmende wieder
		 * schließen. TODO: Wo sollen wir diese queue wieder schließen?
		 */
		mqCommFreightContracts.connect();
	}
	
	//Non-production Code
	private HlsSimulator simulator = new HlsSimulator();

	/**
	 * Constructor.
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public CGFrame(int timerInterval) throws IOException, ParseException {
		super(timerInterval);
		
		currentTime = parseDate(STARTING_TIME);
		
		sendTransportationLanes();
		
		String colorPath = COLOR_PATH;
		String heightmapPath = HEIGHTMAP_PATH;
		
		
		//heightfield: aus bild
		ITriangleMesh heightfield = Heightfield.makeField(DEFAULT_RESOLUTION,
		        heightmapPath, colorPath, MAX_HEIGHT);
		
		TriangleMeshNode heightfieldNode = new TriangleMeshNode(heightfield); 
		
		TranslationNode translationNodeTerrain = 
		        new TranslationNode(new Vector3(-0.5,0,-0.5));
		translationNodeMobs = new TranslationNode(new Vector3(-0.5,0,-0.5));
		GroupNode groupTerrain = new GroupNode();
		GroupNode groupMobs = new GroupNode();
		// Colornode erstellen für farbliche Darstellung für das Höhenfeld...
		ColorNode colorNode = new ColorNode(new Vector3(0, 0.5, 0),
		        "shader/vertex_shader_phong_shading.glsl",
		        "shader/fragment_shader_phong_shading.glsl");
		// ... für die movable objects.
		ColorNode colorNodeMob = new ColorNode(new Vector3(0.5,0.5,0.5),
		        "shader/vertex_shader_texture.glsl",
                "shader/fragment_shader_texture.glsl");
		
		// Höhenfeld Knoten zusammenfügen
		getRoot().addChild(groupTerrain);
		groupTerrain.addChild(colorNode);
		colorNode.addChild(translationNodeTerrain);
		translationNodeTerrain.addChild(heightfieldNode);
		
		// Moveable Objects Knoten zusammenbauen
		getRoot().addChild(groupMobs);
		groupMobs.addChild(colorNodeMob);
		colorNodeMob.addChild(translationNodeMobs);

		registerForFreightContracts();
		
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				timerTick();
			}
		}, timerInterval, timerInterval);
	}
    private Date parseDate(String string) throws ParseException {
        /* Erst das zu dem string "2014‐12‐08 00:00:00" passende datumsformat
           erstellen */
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(string);
    }
    private void registerForFreightContracts() {
        IMessageCallback receiver = new IMessageCallback() {
            
            @Override
            public void messageReceived(String message) {
                // TransportOrder erstellen
                TransportOrder order = new TransportOrder();
                // mit daten aus der JSON-Nachricht befüllen
                order.fromJson(message);
                /* "Wenn sie einen Frachtauftrag erhalten, prüfen sie zunächst,
                 *  prüfen sie zunächst, ob der Startzeitpunkt in der Zukunft 
                 *  liegt.  */
                Date startTime = order.getStartTime();
                if (startTime.after(currentTime)) {
                    // "Ansonsten merken sie sich den Auftrag." 
                    rememberOrder(order);
                } else {
                	// "Falls nicht, verwerfen sie den Auftrag."
                	System.out.println("Auftrag " + order + " verworfen!");
                	System.out.println("Datum des Auftrags: " + startTime);
                	System.out.println("Aktuelles Datum: " +  currentTime);
                }
            }

            private void rememberOrder(TransportOrder order) {
            	rememberedOrders.add(order);
            }
        };
        mqCommFreightContracts.registerMessageReceiver(receiver);
        mqCommFreightContracts.waitForMessages();
    }
    private void sendTransportationLanes() {
        /* "Die Transportbeziehungen sind die Kanten im Graph." */
        Connections transportationLanes = new Connections();
        transportationLanes.initWithAllConnections();
        mqCommTransportLanes = new RabbitMqCommunication(
                HlsConstants.TRANSPORZBEZIEHUNGEN_QUEUE,
                HlsConstants.MQ_SERVER_URL, HlsConstants.MQ_USERNAME,
                HlsConstants.MQ_PASSWORD);
        mqCommTransportLanes.connect();
        /* "Senden Sie bei Programmstart die Transportbeziehungen an die 
         * richtige RabbitMQ‐Queue."*/
        mqCommTransportLanes.sendMessage(transportationLanes.toJson());
        mqCommTransportLanes.disconnect();
    }

    private MovableObject makeMoveableObject(String heightmapPath,
            double maxHeight, List<Vector3> wegpunkt, String objPath,
            Vector3 scaleNode) throws IOException {
        //1. Das Object erzeugen 
        ITriangleMesh mObject = new TriangleMesh();
        
        ObjIO objIO = new ObjIO();
        objIO.einlesen(objPath, mObject);
        
        ((TriangleMesh)mObject).calculateAllNormals();
        
        //2a. Kugel in ein TriangleMeshNode stecken
        TriangleMeshNodeTexture mObjectNode = new TriangleMeshNodeTexture(mObject);
        
        //2b. Skalierung der kugel von ScaleNode
        Vector3 scale = scaleNode;
        
        //4. Höhenwerte bereitstellen durch einlesen
        BufferedImage heightmapFile = ImageIO.read(new File(heightmapPath));
        
        // 3 MoveableObject erzeugen mit einem der 3 obigen Pfaden
        // Da das Flugzeug-Model falsch orientiert ist, muss es einmal um 90° gedreht werden
        float rotAngle = 90.0f;
        Vector3 rotAxis = new Vector3(0,1,0);
        
        return new MovableObject(mObjectNode, scale ,
                rotAxis, rotAngle, wegpunkt,heightmapFile, maxHeight, translationNodeMobs);
    }

    /*
	 * (nicht-Javadoc)
	 * 
	 * @see computergrafik.framework.ComputergrafikFrame#timerTick()
	 */
	@Override
	protected void timerTick() {
		
		/* 1. Alle auftraege rausholen, für die es schon so weit
		 * ist, den Auftrag loszuschicken. */
		//iterator verwenden um sicher zu löschen
		Iterator<TransportOrder> ordersIterator = rememberedOrders.iterator();
		while (ordersIterator.hasNext()) {
			TransportOrder order = ordersIterator.next();
			if (order.getStartTime().equals(currentTime) || 
			    order.getStartTime().before(currentTime) ) {
				  // Dann den auftrag entfernen!
				  ordersIterator.remove();
				  
			      City startCity = TransportNetwork.getCity(
			    		    order.getStartLocation()
			    		  );
			      double[] startCoords = startCity.getCoords();
			      Vector3 startWaypoint = new Vector3(startCoords[0],0.0,
			    		  startCoords[1]);
			      
			      City endCity = TransportNetwork.getCity(
			    		  order.getTargetLocation()
			    		 );
			      double[] endCoords = endCity.getCoords();
			      Vector3 endWaypoint = new Vector3(endCoords[0],0.0,endCoords[1]);		
			      
			      List<Vector3> deliveryRoute = 
			    		  Arrays.asList(startWaypoint, endWaypoint);
			     
			      //Aus dem auftrag das paket in der visualisierung machen
			     
					try {
						MovableObject mob = makeMoveableObject(HEIGHTMAP_PATH,
								 MAX_HEIGHT, deliveryRoute, CUBE_PATH, 
								 SCALE_FROM_RESOLUTION);
						addToSceneGraph(mob);
						sendTransportEventDeparted(order,startCoords);
						
						mobToOrderMap.put(mob, order);
					} catch (IOException e) {
						e.printStackTrace();
					}
			   }
		}
			tickAllMobs(currentTime);
			
			/* currentTime erhöhen - was für ein frickel-code... */ 
			
			Calendar calendarInstance = Calendar.getInstance();
			
			//Zeit in kalender laden
			calendarInstance.setTime(currentTime);
			
			//Kalender inkrementieren
			calendarInstance.add(Calendar.MINUTE, ELAPSED_MINUTES_AFTER_TICK);
			
			//Inkrementierten Kalender wieder zu datum umwandeln
			currentTime = calendarInstance.getTime();
			
			simulator.tick(currentTime);
	}

	// Methoden, um Nachrichten an die RabbitMQ abzuschicken
	private void sendTransportEventDeparted(TransportOrder order, 
			double[] startCoords) 
	{
		mqCommEvents = 
				new RabbitMqCommunication(HlsConstants.SENDUNGSEREIGNIS_QUEUE, 
						HlsConstants.MQ_SERVER_URL, HlsConstants.MQ_USERNAME, 
						HlsConstants.MQ_PASSWORD);
		mqCommEvents.connect();
		
		TransportEvent transportEvent = new TransportEvent(
				order.getDeliveryNumber(), order.getOrderNumber(),
				currentTime, TransportEvent.EventType.ABGEFAHREN, startCoords);
		
		mqCommEvents.sendMessage(transportEvent.toJson());
		
		//TODO Möglicherweise queue nur einmal öffnen (wegen performance)
		mqCommEvents.disconnect();
	}
	
	private void sendTransportEventArrived(TransportOrder order,
			double[] endCoords)
	{
		mqCommEvents = 
				new RabbitMqCommunication(HlsConstants.SENDUNGSEREIGNIS_QUEUE, 
						HlsConstants.MQ_SERVER_URL, HlsConstants.MQ_USERNAME, 
						HlsConstants.MQ_PASSWORD);
		mqCommEvents.connect();
		
		TransportEvent transportEvent = new TransportEvent(
				order.getDeliveryNumber(), order.getOrderNumber(),
				currentTime, TransportEvent.EventType.ANGEKOMMEN, endCoords);
		
		mqCommEvents.sendMessage(transportEvent.toJson());
		
		//TODO Möglicherweise queue nur einmal öffnen (wegen performance)
		mqCommEvents.disconnect();
	}
	
	private void addToSceneGraph(MovableObject mob) {
		translationNodeMobs.addChild(mob);	
	}
	
	//Weitere hilfsmethoden 
	
	private void tickAllMobs(Date currentTime) {
		for (int i = 0; i < translationNodeMobs.getNumberOfChildren(); i++) {
			// alpha = (currentTime - startTime) / (endTime - startTime)
			MovableObject mob = (MovableObject) translationNodeMobs.getChildNode(i);
			TransportOrder order = mobToOrderMap.get(mob);
			
			Date startTime = order.getStartTime();
			Date endTime = order.getDeliveryTime();
					
			double alpha = (double)
					(currentTime.getTime() - startTime.getTime()) / 
					(double)(endTime.getTime() - startTime.getTime());
			
			if(statusUpdateNecessary(startTime,currentTime,EVERY_N_MINUTES)) {
				sendTransportEventEnRoute(order,mob);
			}
			
			if(alpha > 1.0){
				double[] endCoords = TransportNetwork
						.getCity(order.getTargetLocation())
						.getCoords();
				sendTransportEventArrived(order, endCoords);
			}
			
			mob.tick(alpha); 
		}
	}
	
	
	private void sendTransportEventEnRoute(TransportOrder order,
			MovableObject mob) {
		Vector3 position = mob.getPositionNow();
		
		double[] coords = {position.get(0), position.get(2)};
		
		
		mqCommEvents = 
				new RabbitMqCommunication(HlsConstants.SENDUNGSEREIGNIS_QUEUE, 
						HlsConstants.MQ_SERVER_URL, HlsConstants.MQ_USERNAME, 
						HlsConstants.MQ_PASSWORD);
		mqCommEvents.connect();
		
		TransportEvent transportEvent = new TransportEvent(
				order.getDeliveryNumber(), order.getOrderNumber(),
				currentTime, TransportEvent.EventType.UNTERWEGS, coords);
		
		mqCommEvents.sendMessage(transportEvent.toJson());
		
		//TODO Möglicherweise queue nur einmal öffnen (wegen performance)
		mqCommEvents.disconnect();
		
	}
	
	/**
	 * Prüft, ob der zeitstempel "now" n (oder ein vielfaches von n)
	 * minuten nach startTime ist
	 * @param startTime
	 * @param now
	 * @param nMinutes
	 * @return
	 */
	private boolean statusUpdateNecessary(Date startTime, Date now, int nMinutes) {
		
		//Von millisekunden nach Minuten umrechnen 
		long difference = now.getTime() - startTime.getTime();
		long differenceInMinutes = difference * MILLIS_TO_MINUTES;
		
		return differenceInMinutes % nMinutes == 0;
	}
	/**
	 * Program entry point.
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		new CGFrame(50);
	}
}
