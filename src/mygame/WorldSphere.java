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
public class WorldSphere extends Node {

    public static Material mat;
    static Geometry ball;
    public Node sphere;

    protected WorldSphere(SimpleApplication sa) {
        initMaterial(sa);
        initGeometry();

        sphere = new Node();
        sphere.setLocalTranslation(0f, -202.25f, 0f);
        sphere.setShadowMode(RenderQueue.ShadowMode.Receive);
        sphere.attachChild(ball);
        sa.getRootNode().attachChild(sphere);
    }

    private void initGeometry() {
        Quaternion roll90 = new Quaternion();
        roll90.fromAngleAxis(FastMath.PI / 2, new Vector3f(0, 1, 0));

        Sphere largeSphere = new Sphere(64, 64, 200f);
        largeSphere.setTextureMode(Sphere.TextureMode.Projected);
        ball = new Geometry("Ball", largeSphere);
        ball.setLocalRotation(roll90);
        ball.setMaterial(mat);
    }

    private void initMaterial(SimpleApplication sa) {
        mat = new Material(sa.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", sa.getAssetManager().loadTexture("Textures/Planets/Earth.jpg"));
    }
}
