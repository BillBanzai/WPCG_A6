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

/**
 * Diese Klasse erm�glicht es, Rotationen darzustellen.
 *
 */
public class RotationNodeMatrix extends Node {
	
	private Matrix3 matrix;

	/**
	 * Sorgt daf�r, dass alle Kindknoten dieses Knotens relativ zu einer 
	 * Rotationsachse um einen Winkel rotiert dargestellt werden.
	 */
	@Override
	public void drawGl(GL2 gl) {
		
		// Remember current state of the render system
	    gl.glPushMatrix();
	    
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
