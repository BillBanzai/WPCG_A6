/**
 * Prof. Philipp Jenke
 * Hochschule für Angewandte Wissenschaften (HAW), Hamburg
 * Lecture demo program.
 */
package computergraphics.applications;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import computergraphics.datastructures.ITriangleMesh;
import computergraphics.datastructures.ObjIO;
import computergraphics.datastructures.TriangleMesh;
import computergraphics.framework.AbstractCGFrame;
import computergraphics.framework.Shaders;
import computergraphics.math.Vector3;
import computergraphics.scenegraph.ColorNode;
import computergraphics.scenegraph.TranslationNode;
import computergraphics.scenegraph.TriangleMeshNode;
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

	/**
	 * Constructor.
	 * @throws IOException 
	 */
	public CGFrame(int timerInverval) throws IOException {
		super(timerInverval);
		String colorPath = "img/karte_deutschland.jpg";
		String heightmapPath = "img/hoehenkarte_deutschland.png";
		
		
		//heightfield: aus bild
		ITriangleMesh heightfield = Heightfield.makeField(DEFAULT_RESOLUTION,
		        heightmapPath, colorPath, MAX_HEIGHT);
		
		TriangleMeshNode heightfieldNode = new TriangleMeshNode(heightfield); 
		
		TranslationNode translationNode = 
		        new TranslationNode(new Vector3(-0.5,0,-0.5));
		
		// Colornode erstellen für farbliche Darstellung
		ColorNode colorNode = new ColorNode(new Vector3(0, 0.5, 0));
		ColorNode colorNodeMob = new ColorNode(new Vector3(1, 1, 1),
				Shaders.Vertex.TEXTURE_SHADER,Shaders.Fragment.TEXTURE_SHADER);
		
		movableObject1 = makeMoveableObject(heightmapPath,MAX_HEIGHT, waypoints_Rand);
		movableObject2 = makeMoveableObject(heightmapPath,MAX_HEIGHT, waypoints_Pfad);
		movableObject3 = makeMoveableObject(heightmapPath,MAX_HEIGHT, waypoints_Berge);
		
		getRoot().addChild(translationNode);
		translationNode.addChild(colorNode);
		translationNode.addChild(colorNodeMob);
		colorNode.addChild(heightfieldNode);
		colorNodeMob.addChild(movableObject1);
		colorNodeMob.addChild(movableObject2);
		colorNodeMob.addChild(movableObject3);
	}
	
    private MovableObject makeMoveableObject(String heightmapPath,
            double maxHeight, List<Vector3> wegpunkt) throws IOException {
        //1. Die kugel erzeugen 
        ITriangleMesh cube = new TriangleMesh();
        
        ObjIO objIO = new ObjIO();
        objIO.einlesen("meshes/cube.obj", cube);
        
        ((TriangleMesh)cube).calculateAllNormals();
        
        //2a. Kugel in ein TriangleMeshNode stecken
        TriangleMeshNode ballNode = new TriangleMeshNode(cube);
        
        //2b. Skalierung der kugel von ScaleNode
//        Vector3 scaleFromResolution = new Vector3(3.0/256d,1.0/256d,1.0/256d);
        Vector3 scaleFromResolution = new Vector3(1.0/64d,1.0/64d,1.0/64d);
        
        //4. Höhenwerte bereitstellen durch einlesen
        BufferedImage heightmapFile = ImageIO.read(new File(heightmapPath));
        
        // 3 MoveableObject erzeugen mit einem der 3 obigen Pfaden
        return new MovableObject(ballNode, scaleFromResolution ,
                new Vector3(0,0,0), 0.0f, wegpunkt,heightmapFile, maxHeight);
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
	 */
	public static void main(String[] args) throws IOException {
		new CGFrame(50);
	}
}
