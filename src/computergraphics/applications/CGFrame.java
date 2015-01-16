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
    
    //3. Die wegpunkte für die kugel erzeugen
    // Im uhrzeigersinn 
    
	
    private RabbitMqCommunication mqComm;
    private Date currentTime;
    
    private List<TransportOrder> rememberedOrders = new ArrayList<>();
	private TranslationNode translationNodeMobs;

	/**
	 * Constructor.
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public CGFrame(int timerInterval) throws IOException, ParseException {
		super(timerInterval);
		
		currentTime = parseDate("2014-12-08 00:00:00");
		
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
        mqComm.registerMessageReceiver(receiver);
    }
    private void sendTransportationLanes() {
        /* "Die Transportbeziehungen sind die Kanten im Graph." */
        Connections transportationLanes = new Connections();
        transportationLanes.initWithAllConnections();
        mqComm = new RabbitMqCommunication(
                HlsConstants.TRANSPORZBEZIEHUNGEN_QUEUE,
                "win-devel.informatik.haw-hamburg.de", "CGTeams", "Rwj9joAi");
        mqComm.connect();
        /* "Senden Sie bei Programmstart die Transportbeziehungen an die 
         * richtige RabbitMQ‐Queue."*/
        mqComm.sendMessage(transportationLanes.toJson());
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
		//1. Gemerkte aufträge nach Startzeit sortieren
		Collections.sort(rememberedOrders, 
				(TransportOrder o1, TransportOrder o2) -> 
		        o1.getStartTime().compareTo(o2.getStartTime())
		        );
		
		/* 2. Aktuellsten auftrag aus der queue rausholen, wenn es schon so weit
		 * ist, den Auftrag loszuschicken. */
		if (!rememberedOrders.isEmpty()) {
			if (rememberedOrders.get(0).getStartTime().equals(currentTime) || 
			    rememberedOrders.get(0).getStartTime().before(currentTime) ) {
				  //dann das objekt losschicken! 
				  /*TODO: Code so umschreiben, dass mehrere objekte pro tick los-
				   * geschickt werden können */
			      TransportOrder order = rememberedOrders.remove(0);
			      
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
					} catch (IOException e) {
						e.printStackTrace();
					}
			   }
		}
		
			tickAllMobs();
			
			/* currentTime erhöhen - was für ein frickel-code... */ 
			
			Calendar calendarInstance = Calendar.getInstance();
			
			//Zeit in kalender laden
			calendarInstance.setTime(currentTime);
			
			//Kalender inkrementieren
			calendarInstance.add(Calendar.MINUTE, ELAPSED_MINUTES_AFTER_TICK);
			
			//Inkrementierten Kalender wieder zu datum umwandeln
			currentTime = calendarInstance.getTime();
	}

	private void addToSceneGraph(MovableObject mob) {
		translationNodeMobs.addChild(mob);	
	}
	
	private void tickAllMobs() {
		for (int i = 0; i < translationNodeMobs.getNumberOfChildren(); i++) {
			((MovableObject)translationNodeMobs.getChildNode(i)).tick(); 
		}
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
