/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author Guo Jing Wu
 */
public class Obstacles extends Node {

    public static Material mat;
    public Geometry ball;
    public Node meteor;

    protected Obstacles(SimpleApplication sa, Game game) {
        initMaterial(sa);
        initGeometry();
        meteor = new Node();
        meteor.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        setNewPosition(game);
        meteor.attachChild(ball);
        meteor.setLocalTranslation(game.worldSphere.getWorldTranslation());
        game.worldSphere.sphere.attachChild(meteor);
    }

    private void initGeometry() {
        Quaternion roll90 = new Quaternion();
        roll90.fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0));
        
        Sphere sphere = new Sphere(5, 7, 2.0f);
        sphere.setTextureMode(Sphere.TextureMode.Projected);
        ball = new Geometry("Sphere", sphere);
        ball.setLocalRotation(roll90);
        ball.setMaterial(mat);
    }

    private void initMaterial(SimpleApplication sa) {
        mat = new Material(sa.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", sa.getAssetManager().loadTexture("Textures/Planets/Meteor.jpg"));
    }
    
    public void setNewPosition(Game game) {
        float x = (float) FastMath.nextRandomInt(-14, 14);
        float y = game.worldSphere.sphere.getWorldTranslation().y;
        Quaternion randy = new Quaternion();
        float z = (float) FastMath.nextRandomInt(30,320);
        randy.fromAngleAxis(FastMath.PI*z/180, new Vector3f(1, 0, 0));
        ball.setLocalTranslation(x, (-y)-2.5f, 0);
        meteor.setLocalRotation(randy);
    }
}
