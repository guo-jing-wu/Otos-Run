/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 *
 * @author Guo Jing Wu
 */
public class StartScreen extends AbstractAppState implements ActionListener {

    BitmapText text, startText, quitText;
    private Node cameraTarget;
    Main main;
    FilterPostProcessor fpp;
    AppStateManager asm;
    boolean realGameStarted = false;

    public void onAction(String name, boolean keyDown, float tpf) {
        if (keyDown) {
            if (name.equals("Start") && !realGameStarted) {
                asm.detach(this);
                Game game = new Game(false);
                asm.attach(game);
                realGameStarted = true;
            }
            if (name.equals("Quit")) {
                System.exit(0);
            }
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        main = (Main) app;
        asm = stateManager;
        Main.clearJMonkey(main);
        initText(app);
        
        initCam();
        InputManager inputManager = main.getInputManager();
        inputManager.clearMappings();
        inputManager.addMapping("Start", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Quit", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addListener(this, "Start", "Quit");
        Game demoGame = new Game(true);
        stateManager.attach(demoGame);
    }

    @Override
    public void cleanup() {
        Main.clearJMonkey(main);
    }

    @Override
    public void update(float tpf) {
        cameraTarget.rotate(0, tpf, 0);
    }

    private void initText(Application app) {
        AppSettings s = main.getSettings();
        BitmapFont bmf = app.getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        text = new BitmapText(bmf);
        text.setSize(bmf.getCharSet().getRenderedSize() * 10);
        text.setColor(ColorRGBA.Magenta);
        text.setText("Oto Rennt");
        float lineY = s.getHeight();
        float lineX = (s.getWidth() - text.getLineWidth()) / 2;
        text.setLocalTranslation(lineX, lineY, 0f);
        main.getGuiNode().attachChild(text);
        
        startText = new BitmapText(bmf);
        startText.setSize(bmf.getCharSet().getRenderedSize() * 3);
        startText.setColor(ColorRGBA.Yellow);
        startText.setText("Press SPACE to Start");
        lineY = s.getHeight() / 1.25f;
        lineX = (s.getWidth() - startText.getLineWidth()) / 2;
        startText.setLocalTranslation(lineX, lineY, 0f);
        main.getGuiNode().attachChild(startText);
        
        quitText = new BitmapText(bmf);
        quitText.setSize(bmf.getCharSet().getRenderedSize() * 3);
        quitText.setColor(ColorRGBA.Yellow);
        quitText.setText("Press [Q] to Quit");
        lineY = s.getHeight() / 1.35f;
        lineX = (s.getWidth() - quitText.getLineWidth()) / 2;
        quitText.setLocalTranslation(lineX, lineY, 0f);
        main.getGuiNode().attachChild(quitText);
    }

    private void initCam() {
        main.getFlyByCamera().setEnabled(false);
        cameraTarget = new Node();
        CameraNode camNode = new CameraNode("Camera Node", main.getCamera());
        camNode.setLocalTranslation(new Vector3f(0f, 6f, 15f));
        camNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cameraTarget.attachChild(camNode);
        main.getRootNode().attachChild(cameraTarget);
    }
}
