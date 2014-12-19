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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import computergraphics.datastructures.ITriangleMesh;
import computergraphics.datastructures.ObjIO;
import computergraphics.datastructures.TriangleMesh;
import computergraphics.framework.AbstractCGFrame;
import computergraphics.hlsvis.hls.Connections;
import computergraphics.hlsvis.hls.HlsConstants;
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
    
    //3. Die wegpunkte für die kugel erzeugen
    // Im uhrzeigersinn 
    
    // Pfad = Am Rand entlang über die 4 Eckpunkte:
    Vector3 upLeft = new Vector3(0, 0, 0);
    Vector3 upRight = new Vector3(0, 0 , 1);
    Vector3 downRight = new Vector3(1, 0 ,1);
    Vector3 downLeft = new Vector3(1, 0 ,0);
    
    // Pfad = Entlang dem Weg. siehe Pfad in Skizze (rot):
    Vector3 p1  = new Vector3(0.14,  0, 0.9);
    Vector3 p2  = new Vector3(0.14,  0, 0.36);
    Vector3 p3  = new Vector3(0.10,  0, 0.36);
    Vector3 p4  = new Vector3(0.10,  0, 0.22);
    Vector3 p5  = new Vector3(0.44,  0, 0.22);
    Vector3 p6  = new Vector3(0.44,  0, 0.24);
    Vector3 p7  = new Vector3(0.835, 0, 0.24);
    Vector3 p8  = new Vector3(0.835, 0, 0.84);
    Vector3 p9  = new Vector3(0.62 , 0, 0.84);
    Vector3 p10 = new Vector3(0.62 , 0, 0.9);

    // Pfad = Querfeldein über die Berge. siehe Pfad in Skizze (orange):
    Vector3 p1_  = new Vector3(0.14,  0, 0.9);
    Vector3 p5_  = new Vector3(0.44,  0, 0.22);
    Vector3 p10_ = new Vector3(0.62 , 0, 0.9);
    
    List<Vector3> waypoints_Rand = Arrays.asList(upLeft,upRight,
            new Vector3(0.5,0,0.5),downRight,downLeft);
    
    List<Vector3> waypoints_Pfad = Arrays.asList(p1, p2, p3, p4, p5, p6, p7,
    		p8, p9, p10);
    
    List<Vector3> waypoints_Berge = Arrays.asList(p1_, p5_, p10_);
	
	private MovableObject movableObject1;
	private MovableObject movableObject2;
	private MovableObject movableObject3;
    private RabbitMqCommunication mqComm;
    private Date currentTime;

	/**
	 * Constructor.
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public CGFrame(int timerInverval) throws IOException, ParseException {
		super(timerInverval);
		
		currentTime = parseDate("2014‐12­‐08 00:00:00");
		
		sendTransportationLanes();
		
		String colorPath = "img/karte_deutschland.jpg";
		String heightmapPath = "img/hoehenkarte_deutschland.png";
		
		
		//heightfield: aus bild
		ITriangleMesh heightfield = Heightfield.makeField(DEFAULT_RESOLUTION,
		        heightmapPath, colorPath, MAX_HEIGHT);
		
		TriangleMeshNode heightfieldNode = new TriangleMeshNode(heightfield); 
		
		TranslationNode translationNodeTerrain = 
		        new TranslationNode(new Vector3(-0.5,0,-0.5));
		TranslationNode translationNodeMobs = 
		        new TranslationNode(new Vector3(-0.5,0,-0.5));
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
		
		movableObject1 = makeMoveableObject(heightmapPath,MAX_HEIGHT, 
				waypoints_Rand, CUBE_PATH, SCALE_FROM_RESOLUTION);
		movableObject2 = makeMoveableObject(heightmapPath,MAX_HEIGHT, 
				waypoints_Pfad, PLANE_PATH, PLANE_SCALE);
		movableObject3 = makeMoveableObject(heightmapPath,MAX_HEIGHT, 
				waypoints_Berge, CUBE_PATH, SCALE_FROM_RESOLUTION);
		
		// Höhenfeld Knoten zusammenfügen
		getRoot().addChild(groupTerrain);
		groupTerrain.addChild(colorNode);
		colorNode.addChild(translationNodeTerrain);
		translationNodeTerrain.addChild(heightfieldNode);
		
		// Moveable Objects Knoten zusammenbauen
		getRoot().addChild(groupMobs);
		groupMobs.addChild(colorNodeMob);
		colorNodeMob.addChild(translationNodeMobs);
		translationNodeMobs.addChild(movableObject1);
		translationNodeMobs.addChild(movableObject2);
		translationNodeMobs.addChild(movableObject3);
	    
		registerForFreightContracts();
	}
    private Date parseDate(String string) throws ParseException {
        /* Erst das zu dem string "2014‐12­‐08 00:00:00" passende datumsformat
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
                }
                // "Falls nicht, verwerfen sie den Auftrag."
            }

            private void rememberOrder(TransportOrder order) {
                // TODO Auto-generated method stub
                
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
                "localhost", "guest", "guest");
        mqComm.connect();
        /* "Senden Sie bei Programmstart die Transportbeziehungen an die 
         * richtige RabbitMQ‐Queue."*/
        mqComm.sendMessage(transportationLanes.toJson());
    }

    private MovableObject makeMoveableObject(String heightmapPath,
            double maxHeight, List<Vector3> wegpunkt, String objPath, Vector3 scaleNode) throws IOException {
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
                rotAxis, rotAngle, wegpunkt,heightmapFile, maxHeight);
    }

    /*
	 * (nicht-Javadoc)
	 * 
	 * @see computergrafik.framework.ComputergrafikFrame#timerTick()
	 */
	@Override
	protected void timerTick() {
	    if(movableObject1 != null) { movableObject1.tick(); }
	    if(movableObject2 != null) { movableObject2.tick(); }
	    if(movableObject3 != null) { movableObject3.tick(); }
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
