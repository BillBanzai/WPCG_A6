/**
 * Praktikum WPCG, WS 14/15, Gruppe 2
 * Gruppe: Andreas Mauritz (andreas.mauritz@haw-hamburg.de)
 *         Christian Schirin (christian.schirin@haw-hamburg.de)
 * Aufgabe: Aufgabenblatt 5, Aufgabe 1,2,3
 * Verwendete Quellen:
 *  
 */
package computergraphics.scenegraph;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import computergraphics.math.Matrix3;
import computergraphics.math.Vector3;

/** Diese Klasse ermöglicht es, bewegte Objekte im Szenengraph darzustellen.*/
public class MovableObject extends Node {
    
    /*"Darin wird der Wert von α um einen festen Wert erhöht (z.B. 0.05)"*/
    private static final double INTERPOLATION_INCREMENT = 0.005;

    private static final Vector3 UP_VECTOR = new Vector3(0,1,0);

    public MovableObject(Node geometryNode,Vector3 scale, Vector3 rotAxis, 
            float rotAngle, List<Vector3> waypoints,BufferedImage terrainFile,
            double maxHeight) {
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
        
        this.waypoints.addAll(waypoints);
        this.terrainFile = terrainFile;
        this.maxHeight = maxHeight;
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

    /**Die Höheninformationen, auf denen das Höhenfeld basiert.*/
    private BufferedImage terrainFile;
    
    private double maxHeight;
    
    /** "Damit Bewegung in die Szene kommt, muss sich der Wert von alpha zur
     * Laufzeit verändern. Implementieren Sie dazu in der Klasse MoveableObjects
     * eine Methode tick()"
     */
    public void tick() {
        //1.Aktuelle Position berechnen
        //Position p0: aktueller Wegpunkt waypoints.get(0)
        Vector3 p0 = waypoints.get(0);
        //Position p1: nächster Wegpunkt waypoints.get(1)
        Vector3 p1 = waypoints.get(1);
        //(1-alpha)*p0 + alpha*p1
        Vector3 positionNow = p0.multiply(1-alpha).add(p1.multiply(alpha)); 
        
        //1b. y-Wert basierend auf x und z setzen 
        double x = positionNow.get(0),z = positionNow.get(2);
        
        double y = getHeight(x,z);
        
        /* Addition mit der y-komponente aus dem faktor aus scaleNode 
         * sorgt dafür, dass das MovableObject mit seinem untersten punkt 
         * die Oberfläche des terrains berührt. Durch diese maßnahme ist dafür
         * gesorgt, "dass sich die Objekte immer genau auf der Oberfläche des 
         * Höhenfelds befinden". */
        y += scaleNode.getScale().get(1); 
        
        positionNow.set(1, y);
        
        //2.Berechnete position im Translationsknoten setzen
        translationNode.setFactor(positionNow);
        
//        //3.Alpha erhöhen (Konstante Schrittweite)
//        alpha += (INTERPOLATION_INCREMENT);
        // 3. Alpha erhöhen (Konstante Geschwindigkeit)
        Vector3 directionVector = p0.subtract(p1);
        alpha += INTERPOLATION_INCREMENT*(1/directionVector.getNorm());
        
        /* Bei Bedarf alpha zurück auf 0 setzen und dann auch vordersten
         * Wegpunkt ans Ende der Liste setzen. */
        if(alpha > 1.0) {
            alpha = 0;
            waypoints.add(waypoints.remove(0));
        }
        
        //4. Entlang aktueller bewegungsrichtung ausrichten
        alignToMovement(p0,p1);
    }

    private void alignToMovement(Vector3 p0, Vector3 p1) {
        /* "Der x-Vektor des Koordinatensystems ist der Vektor von der ersten 
         * zur Zweitenwegmarke (also p1-p0)." */
        Vector3 x = p1.subtract(p0);
        // "Dieser Vektor muss noch normiert werden." 
        x.normalize();
        /* "Der y-Vektor ist konstant und zeigt immer "nach oben" 
         * (also (0,1,0))." */
        Vector3 y = UP_VECTOR; 
        /* "Der dritte Vektor muss senkrecht auf den ersten beiden stehen. Sie 
         * können ihn als Kreuzprodukt aus x und y berechnen." */
        Vector3 z = x.cross(y);
        
        Matrix3 coordinateSystem = new Matrix3(x, y, z);
        
        rotationNode.setMatrix(coordinateSystem);
    }

    private double getHeight(double x, double z) {
        int heightX = terrainFile.getHeight();
        int widthZ = terrainFile.getWidth();
        
         Color color = new Color(
                 terrainFile.getRGB(
                         Math.min((int)(x*heightX),heightX-1),
                         Math.min((int)(z*widthZ),widthZ-1)
                         )
                 );
         double heightValue = (color.getRed()/255.0)*maxHeight;
         
         return heightValue;
    }

    @Override
    public void drawGl(GL2 gl) {
        translationNode.drawGl(gl);
    }
}
