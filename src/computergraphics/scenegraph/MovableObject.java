/**
 * Praktikum WPCG, WS 14/15, Gruppe 2
 * Gruppe: Andreas Mauritz (andreas.mauritz@haw-hamburg.de)
 *         Christian Schirin (christian.schirin@haw-hamburg.de)
 * Aufgabe: Aufgabenblatt 5, Aufgabe 1
 * Verwendete Quellen:
 *  
 */
package computergraphics.scenegraph;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import computergraphics.math.Vector3;

/** Diese Klasse ermöglicht es, bewegte Objekte im Szenengraph darzustellen.*/
public class MovableObject extends Node {
    
    public MovableObject(Node geometryNode,Vector3 scale, Vector3 rotAxis, 
            float rotAngle, List<Vector3> waypoints) {
        /* Den Objektgraphen aus geometryNode, scaleNode, rotationNode, 
           translationNode und this zusammenbauen */
        this.geometryNode = geometryNode;
        
        scaleNode = new ScaleNode(scale);
        scaleNode.addChild(this.geometryNode);
        
        rotationNode = new RotationNode(rotAxis, rotAngle);
        rotationNode.addChild(scaleNode);
        
        translationNode = new TranslationNode(new Vector3(0,0,0));
        translationNode.addChild(rotationNode);
        
        addChild(translationNode);
    }
    /** "Er beinhaltet auf unterster Ebene die Geometrie des Objektes." */
    private Node geometryNode; //TriangleMeshNode,SingleTriangleNode,etc.
    
    /** "Darüber benötigt er einen Skaliserungsknoten, falls die Geometrie 
     * verformt werden soll." */
    private ScaleNode scaleNode;
    
    /** "Darüber hat er einen Rotationsknoten." */
    private RotationNode rotationNode;
    
    /** "An oberster Stelle wird ein Translationsknoten benötigt." */
    private TranslationNode translationNode;
    
    /** "[...] Speichern Sie daher in jedem MovableObject eine Liste von 
     * Wegpunkten (jeweils als Vector3 ab)." */
    private List<Vector3> waypoints = new ArrayList<>();
    
    //position wird im Translations-Knoten gesetzt
    
    private double alpha = 0.0;
    
    /** "Damit Bewegung in die Szene kommt, muss sich der Wert von alpha zur
     * Laufzeit verändern. Implementieren Sie dazu in der Klasse MoveableObjects
     */
    public void tick() {
        //1.Aktuelle Position berechnen
        //Position p0: aktueller Wegpunkt waypoints.get(0)
        //Position p1: nächster Wegpunkt waypoints.get(1)
        
        //2.Berechnete position im Translationsknoten setzen
        
        //3.Alpha erhöhen
        
        /* Bei Bedarf alpha zurück auf 0 setzen und dann auch vordersten
         * Wegpunkt ans Ende der Liste setzen. */
        
    }

    @Override
    public void drawGl(GL2 gl) {
        translationNode.drawGl(gl);
    }
}
