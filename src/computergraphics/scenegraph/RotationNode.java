/**
 * Praktikum WPCG, WS 14/15, Gruppe 2
 * Gruppe: Andreas Mauritz (andreas.mauritz@haw-hamburg.de)
 * 		   Christian Schirin (christian.schirin@haw-hamburg.de)
 * Aufgabe: Aufgabenblatt 1, Aufgabe 2b
 * Verwendete Quellen:
 *  
 */
package computergraphics.scenegraph;

import javax.media.opengl.GL2;

import computergraphics.math.Matrix3;
import computergraphics.math.Vector3;

/**
 * Diese Klasse erm�glicht es, Rotationen darzustellen.
 *
 */
public class RotationNode extends Node {
	
	private final Vector3 rotAxis;
	private final float angle;
	
	private Matrix3 matrix;
	
	/**
	 * Constructor.
	 * @param rotAxis Die Rotationsachse, um die gedreht werden soll
	 * @param angle Der Winkel im Bogenma� um wie viel gegen den Uhrzeigersinn
	 * 				rotiert werden soll.
	 */
	public RotationNode(Vector3 rotAxis, float angle) {
		this.rotAxis = rotAxis;
		this.angle = angle;
	}
	

	/**
	 * Sorgt daf�r, dass alle Kindknoten dieses Knotens relativ zu einer 
	 * Rotationsachse um einen Winkel rotiert dargestellt werden.
	 */
	@Override
	public void drawGl(GL2 gl) {
		
		// Remember current state of the render system
	    gl.glPushMatrix();

	    /*
		// cast von double auf float, um openGL-Funktion verwenden zu k�nnen. 
		//TODO entscheiden ob glRotatef oder glRotated
		gl.glRotatef(angle, (float) rotAxis.get(0), (float) rotAxis.get(1), (float) rotAxis.get(2));		
		*/
	    double[] fourXfourMatrix = new double[] {
	            matrix.get(0, 0), matrix.get(0, 1), matrix.get(0, 2), 0,
	            matrix.get(1, 0), matrix.get(1, 1), matrix.get(1, 2), 0,
	            matrix.get(2, 0), matrix.get(2, 1), matrix.get(2, 2), 0,
	            0, 0, 0, 1
	    };
	    gl.glMultMatrixd(fourXfourMatrix, 0);
	    
		// Draw all children
		for (int childIndex = 0; childIndex < getNumberOfChildren(); 
				childIndex++) {
			getChildNode(childIndex).drawGl(gl);
		}
		
		// Restore original state
		gl.glPopMatrix();
		
	}


    public void setMatrix(Matrix3 matrix) {
        this.matrix = matrix;
    }
	
	

}
