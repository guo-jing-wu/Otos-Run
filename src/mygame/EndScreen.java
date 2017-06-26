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
import com.jme3.math.FastMath;
import com.jme3.system.AppSettings;

public class EndScreen extends AbstractAppState implements ActionListener {

    private Main main;
    private BitmapText text, statsText, restartText;
    AppStateManager asm;
    protected double[] stats;

    // keyboard, scanning "space"
    // transition to start screen
    public void onAction(String name, boolean keyDown, float tpf) {
        if (name.equals("Start")) {
            Main.clearJMonkey(main);
            asm.detach(this);
            asm.attach(new StartScreen());
        }
    }
    
    protected void setStats(double []s){
        stats = s;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        main = (Main) app;
        asm = stateManager;

        // Text
        BitmapFont bmf = main.getAssetManager().loadFont("Interface/Fonts/Console.fnt");
        text = new BitmapText(bmf);
        text.setSize(bmf.getCharSet().getRenderedSize() * 10);
        text.setColor(ColorRGBA.Red);
        text.setText("Game Over");
        main.getGuiNode().attachChild(text);
        
        restartText = new BitmapText(bmf);
        restartText.setSize(bmf.getCharSet().getRenderedSize() * 4);
        restartText.setColor(ColorRGBA.White);
        restartText.setText("Press SPACE to restart");
        main.getGuiNode().attachChild(restartText);
        
        statsText = new BitmapText(bmf);
        statsText.setSize(bmf.getCharSet().getRenderedSize() * 3);
        statsText.setColor(ColorRGBA.Yellow);
        String s = String.format("Hits: %d \tTime: %3.1f", (int) stats[0], stats[1]);
        statsText.setText(s);
        main.getGuiNode().attachChild(statsText);

        // Keys
        InputManager inputManager = main.getInputManager();
        inputManager.addMapping("Start", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Start");
    }

    @Override
    public void update(float tpf) {
        AppSettings s = main.getSettings();
        float lineY = s.getHeight() / 2 + text.getLineHeight() + FastMath.rand.nextFloat() * 10 - 5;
        float lineX = (s.getWidth() - text.getLineWidth()) / 2 + FastMath.rand.nextFloat() * 10 - 5;
        text.setLocalTranslation(lineX, lineY, 0f);
        
        lineY = s.getHeight() / 2 - text.getLineHeight() ;
        lineX = (s.getWidth() - statsText.getLineWidth()) / 2 ;
        statsText.setLocalTranslation(lineX, lineY, 0f);
        
        lineY = s.getHeight() / 2.25f;
        lineX = (s.getWidth() - restartText.getLineWidth()) / 2;
        restartText.setLocalTranslation(lineX, lineY, 0f);
    }
    
    @Override
    public void cleanup(){
        Main.clearJMonkey(main);
    }
}
