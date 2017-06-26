/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author Guo Jing Wu
 */
public class Oto {

    Game game;
    SimpleApplication sa;
    public AnimChannel channel;
    private AnimControl control;
    private String requestedState = "";
    Node otoNode;
    Control otoControl;
    AudioNode audio_boom;
    private int count;
    private float groundSpeed = 0.0f;
    private float stateTime;
    private boolean pause = false;
    protected boolean isDemo;
    private MyCombinedListener combinedListener = new MyCombinedListener();

    //
    // -------------------------------------------------------------------------
    // the key action listener: set requested state
    private class MyCombinedListener implements AnalogListener, ActionListener {

        public void onAction(String name, boolean keyPressed, float tpf) {
            if (keyPressed) {
                requestedState = name;
            }
        }

        public void onAnalog(String name, float value, float tpf) {
            stateTime += tpf;
            if ((!game.isDemo) && (!pause)) {
                if (name.equals("Right")) {
                    // move right
                    float posx = (0.05f);
                    otoNode.move(posx, 0, 0);
                    //
                    if ((otoNode.getLocalTranslation().x) >= 14.0f) {
                        otoNode.setLocalTranslation(14, 0, 0);
                    }
                }
                if (name.equals("Left")) {
                    // move left
                    float negx = -(0.05f);
                    otoNode.move(negx, 0, 0);
                    //
                    if ((otoNode.getLocalTranslation().x) <= -14.0f) {
                        otoNode.setLocalTranslation(-14, 0, 0);
                    }
                }
            }
        }
    };
    // -------------------------------------------------------------------------

    public Oto(SimpleApplication sa, Game game, boolean iDemoMode) {
        this.sa = sa;
        this.game = game;
        isDemo = iDemoMode;
        initKeys();
        initModel();
        initSound();
    }

    // -------------------------------------------------------------------------  
    // set ground speed. Used for walking adjustment in control.
    public void setGroundSpeed(float spd) {
        this.groundSpeed = spd;
    }

    // -------------------------------------------------------------------------  
    // Custom Keybindings: Mapping a named action to a key input.
    private void initKeys() {
        sa.getInputManager().addMapping("Push", new KeyTrigger(KeyInput.KEY_P));
        sa.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        sa.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        sa.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        sa.getInputManager().addListener(combinedListener, new String[]{"Push", "Jump", "Left", "Right"});
    }

    // -------------------------------------------------------------------------
    // init model
    // load a model that contains animation
    private void initModel() {
        Quaternion roll180 = new Quaternion();
        roll180.fromAngleAxis(FastMath.PI, new Vector3f(0, 1, 0));

        otoNode = (Node) sa.getAssetManager().loadModel("Models/Oto/Oto.mesh.xml");
        otoNode.setLocalScale(0.5f);
        otoNode.setLocalRotation(roll180);
        otoNode.setShadowMode(RenderQueue.ShadowMode.Cast);
        sa.getRootNode().attachChild(otoNode);
        //
        // Create a controller and channels.
        control = otoNode.getControl(AnimControl.class);
        channel = control.createChannel();
        channel.setAnim("stand");
        //
        // add control
        otoControl = new Oto.OtoControl();
        otoNode.addControl(otoControl);
    }

    // -------------------------------------------------------------------------
    // OtoControl
    class OtoControl extends AbstractControl {

        private final int STATE_WALK = 0;
        private final int STATE_STAND = 1;
        private final int STATE_JUMP = 2;
        private int state;
        private boolean stateIsInitialized = false;

        public OtoControl() {
            switchState(STATE_STAND);
        }

        // ---------------------------------------------------------------------
        @Override
        protected void controlUpdate(float tpf) {
            stateTime += tpf;
            // state machine
            String reqState = requestedState;
            requestedState = "";

            // just for debugging purpose: toggle ground speed
            if (reqState.equals("Push")) {
                groundSpeed = groundSpeed > 0 ? 0 : 1.0f;
                if (pause == false) {
                    pause = true;
                } else {
                    pause = false;
                }
            }

            // collision detection
            CollisionResults results = new CollisionResults();
            for (Obstacles o : game.rockNodes) {
                BoundingVolume bv = o.ball.getWorldBound();
                otoNode.collideWith(bv, results);
                if (results.size() > 0) {
                    count++;
                    new SingleBurstParticleEmitter(sa, otoNode, Vector3f.ZERO);
                    if (!isDemo) {
                        audio_boom.playInstance();
                    }
                    o.setNewPosition(game);
                    results.clear();
                }
            }

            // ----------------------------------------
            switch (state) {
                case (STATE_STAND):
                    if (!stateIsInitialized) {
                        stateIsInitialized = true;
                        channel.setAnim("stand", 0.0f);
                    }
                    if (reqState.equals("Jump") && !pause) {
                        switchState(STATE_JUMP);
                    }//
                    // if the earth spins, immediately switch to walk.
                    else if (groundSpeed > 0.0f) {
                        switchState(STATE_WALK);
                    }
                    break;
                case (STATE_JUMP):
                    if (!stateIsInitialized) {
                        stateIsInitialized = true;
                        channel.setAnim("pull");
                    }
                    // Jump
                    float y = 5 * FastMath.sin(stateTime * 2);
                    float x = otoNode.getLocalTranslation().x;
                    otoNode.setLocalTranslation(x, y, 0);
                    if (pause){
                        stateTime-=tpf;
                        float stopy = 5 * FastMath.sin(stateTime * 2);
                        otoNode.setLocalTranslation(0, stopy, 0);
                    }
                    //
                    // end of state?
                    if (y <= 0.0f) {
                        otoNode.setLocalTranslation(x, 0, 0);
                        switchState(STATE_STAND);
                    }
                    break;
                case (STATE_WALK):
                    if (!stateIsInitialized) {
                        stateIsInitialized = true;
                        channel.setAnim("Walk");
                        channel.setSpeed(groundSpeed);
                    }
                    // state action: adjust to groundspeed
                    channel.setSpeed(groundSpeed);
                    //
                    // end of state?
                    if (groundSpeed == 0.0f) {
                        switchState(STATE_STAND);
                    }
                    if (reqState.equals("Jump")) {
                        switchState(STATE_JUMP);
                    }
                    break;
            }
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
        }

        // ---------------------------------------------------------------------
        private void switchState(int state) {
            stateIsInitialized = false;
            this.state = state;
            stateTime = 0.0f;
        }
    }

    public int getHits() {
        return count;
    }

    private void initSound() {
        audio_boom = new AudioNode(game.main.getAssetManager(), "Sound/Effects/Gun.wav", false);
        audio_boom.setPositional(false);
        audio_boom.setLooping(false);
        audio_boom.setVolume(10);
        game.main.getRootNode().attachChild(audio_boom);
    }
}
