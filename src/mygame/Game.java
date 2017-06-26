/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

/**
 *
 * @author Guo Jing Wu
 */
public class Game extends AbstractAppState implements ActionListener {

    Main main;
    AppStateManager asm;
    Oto oto;
    Obstacles[] rockNodes;
    WorldSphere worldSphere;
    BitmapText scoreText, waitText, pauseText, infoText1, infoText2;
    public static final int rocks = 100;
    float gameTime;
    float waitTime;
    private AudioNode audio_nature;
    boolean waitTextVisible = false;
    private final int WAIT = 0;
    private final int RUN = 1;
    private final int PAUSE = 2;
    private int state, stateMemory;
    private final int INITIALWAITTIME = 3;
    private final int MAXHITS = 5;
    private final int speed = 4;
    protected boolean isDemo;

    protected Game(boolean iDemoMode) {
        isDemo = iDemoMode;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        main = (Main) app;
        asm = stateManager;
        worldSphere = new WorldSphere(main);
        worldSphere.sphere.addControl(new Game.SphereControl());
        addRocks();
        oto = new Oto(main, this, isDemo);
        oto.setGroundSpeed(1.5f);
        gameTime = 0;
        state = WAIT;
        waitTime = INITIALWAITTIME;
        initText();
        InputManager inputManager = main.getInputManager();
        inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("Quit", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addListener(this, "Pause", "Quit");
        initCam();
        processor();
        initAudio();
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            if (name.equals("Pause")) {
                if (state == PAUSE) {
                    state = stateMemory;
                    main.getGuiNode().detachChild(pauseText);
                    main.getGuiNode().detachChild(infoText1);
                    main.getGuiNode().detachChild(infoText2);
                } else {
                    stateMemory = state;
                    state = PAUSE;
                    main.getGuiNode().attachChild(pauseText);
                    main.getGuiNode().attachChild(infoText1);
                    main.getGuiNode().attachChild(infoText2);
                }
            }
            if (name.equals("Quit")) {
                StartScreen s = new StartScreen();
                asm.detach(this);
                asm.attach(s);
            }
        }
    }

    private void addRocks() {
        rockNodes = new Obstacles[rocks];
        for (int i = 0; i < rocks; i++) {
            rockNodes[i] = new Obstacles(main, this);
            main.getRootNode().attachChild(rockNodes[i]);
        }
    }

    private void initText() {
        BitmapFont bmf = main.getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        scoreText = new BitmapText(bmf);
        scoreText.setSize(bmf.getCharSet().getRenderedSize() * 2);
        scoreText.setColor(ColorRGBA.White);
        scoreText.setText("");
        scoreText.setLocalTranslation(20, 20, 0f);
        main.getGuiNode().attachChild(scoreText);
        
        waitText = new BitmapText(bmf);
        waitText.setSize(bmf.getCharSet().getRenderedSize() * 10);
        waitText.setColor(ColorRGBA.White);
        waitText.setText("");
        AppSettings s = main.getSettings();
        float lineY = s.getHeight() / 2;
        float lineX = (s.getWidth() - waitText.getLineWidth()) / 2;
        waitText.setLocalTranslation(lineX, lineY, 0f);
        
        pauseText = new BitmapText(bmf);
        pauseText.setSize(bmf.getCharSet().getRenderedSize() * 10);
        pauseText.setColor(ColorRGBA.White);
        pauseText.setText("PAUSED");
        lineY = s.getHeight() / 2;
        lineX = (s.getWidth() - pauseText.getLineWidth()) / 2;
        pauseText.setLocalTranslation(lineX, lineY, 0f);
        
        infoText1 = new BitmapText(bmf);
        infoText1.setSize(bmf.getCharSet().getRenderedSize() * 3);
        infoText1.setColor(ColorRGBA.White);
        infoText1.setText("Move using the Arrow Keys");
        lineY = s.getHeight() / 5;
        lineX = (s.getWidth() - infoText1.getLineWidth()) / 2;
        infoText1.setLocalTranslation(lineX, lineY, 0f);
        
        infoText2 = new BitmapText(bmf);
        infoText2.setSize(bmf.getCharSet().getRenderedSize() * 3);
        infoText2.setColor(ColorRGBA.White);
        infoText2.setText("Jump by pressing SPACE");
        lineY = s.getHeight() / 8;
        lineX = (s.getWidth() - infoText2.getLineWidth()) / 2;
        infoText2.setLocalTranslation(lineX, lineY, 0f);
        
    }

    private void initCam() {
        main.getFlyByCamera().setEnabled(false);
        main.getFlyByCamera().setMoveSpeed(25);
        main.getCamera().setLocation(new Vector3f(0f, 15f, 15f));
        main.getCamera().lookAt(new Vector3f(0, 5f, 0), Vector3f.UNIT_Y);
    }

    public void processor() {
        FilterPostProcessor fpp = new FilterPostProcessor(main.getAssetManager());
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        main.getViewPort().addProcessor(fpp);
        Spatial sky = SkyFactory.createSky(main.getAssetManager(), "Textures/Planets/Stars.dds", false);
        sky.addControl(new Game.SkyControl());
        main.getRootNode().attachChild(sky);
    }

    class SkyControl extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {
            if (state == 1) {
                if (main.getRootNode().hasChild(oto.otoNode)) {
                    spatial.rotate(tpf / speed, 0, 0);
                }
            }
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    //Use AbstractControl
    class SphereControl extends AbstractControl {

        @Override
        protected void controlUpdate(float tpf) {
            if (state == 1) {
                if (main.getRootNode().hasChild(oto.otoNode)) {
                    spatial.rotate(tpf / speed, 0, 0);
                }
            }
        }

        @Override
        protected void controlRender(RenderManager rm, ViewPort vp) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void update(float tpf) {
        switch (state) {
            case WAIT:
                if (isDemo) {
                    state = RUN;
                    break;
                }
                if (!waitTextVisible) {
                    waitTextVisible = true;
                    main.getGuiNode().attachChild(waitText);
                }
                waitTime -= tpf;
                if (waitTime <= 0f) {
                    state = RUN;
                    if (waitTextVisible) {
                        waitTextVisible = false;
                        main.getGuiNode().detachChild(waitText);
                    }
                } else {
                    waitText.setText("" + ((int) waitTime + 1));
                }
                break;
            case RUN:
                stateRun(tpf);
                if (!isDemo && (oto.getHits() >= MAXHITS)) {
                    main.getRootNode().detachChild(oto.otoNode);
                    endGame();
                }
                break;
            case PAUSE:
                break;
        }
        if (audio_nature.getStatus() == AudioSource.Status.Stopped) {
            main.getRootNode().detachChild(audio_nature);
            initAudio();
        }
    }

    private void endGame() {
        EndScreen end = new EndScreen();
        double[] dummy = {oto.getHits(), gameTime};
        end.setStats(dummy);
        asm.detach(this);
        asm.attach(end);
    }

    private void stateRun(float tpf) {
        gameTime += tpf;
        if (!isDemo) {
            String t = String.format("Hits: %d \tTime: %3.1f", oto.getHits(), gameTime);
            scoreText.setText(t);
        }
    }

    private void initAudio() {
        audio_nature = new AudioNode(main.getAssetManager(), "Sounds/Bernard Herrmann The Day The Earth Stood Still - Prelude Radar.ogg", true);
        audio_nature.setPositional(false);
        audio_nature.setLooping(false);
        audio_nature.setVolume(1);
        main.getRootNode().attachChild(audio_nature);
        audio_nature.play();
    }
}
