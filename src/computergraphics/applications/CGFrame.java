/**
 * Prof. Philipp Jenke
 * Hochschule für Angewandte Wissenschaften (HAW), Hamburg
 * Lecture demo program.
 */
package computergraphics.applications;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import computergraphics.datastructures.ITriangleMesh;
import computergraphics.datastructures.ObjIO;
import computergraphics.datastructures.TriangleMesh;
import computergraphics.framework.AbstractCGFrame;
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
	private static final double MAX_HEIGHT = 0.3;
	private static final int DEFAULT_RESOLUTION = 800; //8x8
	
	private MovableObject movableObject;

	/**
	 * Constructor.
	 * @throws IOException 
	 */
	public CGFrame(int timerInverval) throws IOException {
		super(timerInverval);
		String colorPath = "img/color.png";
		String heightmapPath = "img/heightField.png";
		
		
		//heightfield: aus bild
		ITriangleMesh heightfield = Heightfield.makeField(DEFAULT_RESOLUTION,
		        heightmapPath, colorPath, MAX_HEIGHT);
		
		TriangleMeshNode heightfieldNode = new TriangleMeshNode(heightfield); 
		
		TranslationNode translationNode = 
		        new TranslationNode(new Vector3(-0.5,0,-0.5));
		
		// Colornode erstellen für farbliche Darstellung
		ColorNode colorNode = new ColorNode(new Vector3(0, 0.5, 0));
		
		movableObject = makeMoveableObject();
		
		getRoot().addChild(translationNode);
		translationNode.addChild(colorNode);
		colorNode.addChild(heightfieldNode);
		colorNode.addChild(movableObject);
	}
	
    private MovableObject makeMoveableObject() {
        //1. Die kugel erzeugen 
        ITriangleMesh ball = new TriangleMesh();
        
        ObjIO objIO = new ObjIO();
        objIO.einlesen("meshes/sphere.obj", ball);
        
        ((TriangleMesh)ball).calculateAllNormals();
        
        //2a. Kugel in ein TriangleMeshNode stecken
        TriangleMeshNode ballNode = new TriangleMeshNode(ball);
        
        //2b. Skalierung der kugel von ScaleNode
        Vector3 scaleFromResolution = new Vector3(1.0/64d,1.0/64d,1.0/64d);
        
        //3. Die wegpunkte für die kugel erzeugen
        // Im uhrzeigersinn 
        Vector3 upLeft = new Vector3(0,0,0);
        Vector3 upRight = new Vector3(0,0,1);
        Vector3 downRight = new Vector3(1,0,1);
        Vector3 downLeft = new Vector3(1,0,0);
        List<Vector3> waypoints = Arrays.asList(upLeft,upRight,downRight
                ,downLeft);
        waypoints = Arrays.asList(upLeft,downLeft,downRight
                ,upRight);
        
        // MoveableObject erzeugen
        return new MovableObject(ballNode, scaleFromResolution ,
                new Vector3(0,0,0), 0.0f, waypoints);
    }

    /*
	 * (nicht-Javadoc)
	 * 
	 * @see computergrafik.framework.ComputergrafikFrame#timerTick()
	 */
	@Override
	protected void timerTick() {
	    if(movableObject != null) { movableObject.tick(); }
	    System.out.println("Tick");
	}

	/**
	 * Program entry point.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new CGFrame(1000);
	}
}
