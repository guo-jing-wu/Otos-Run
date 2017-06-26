package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.AppSettings;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    static Dimension screen;

    public static void main(String[] args) {
        Main app = new Main();
        initAppScreen(app);
        app.start();
    }
    
    protected static void clearJMonkey(Main main) {
        main.guiNode.detachAllChildren();
        main.rootNode.detachAllChildren();
        main.inputManager.clearMappings();
    }

    @Override
    public void simpleInitApp() {
        initCam();
        initLightandShadow();
        initGUI();
        
        StartScreen s = new StartScreen();
        stateManager.attach(s);
    }

    private static void initAppScreen(SimpleApplication app) {
        AppSettings aps = new AppSettings(true);
        screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 0.75;
        screen.height *= 0.75;
        aps.setResolution(screen.width, screen.height);
        app.setSettings(aps);
        app.setShowSettings(false);
    }

    public AppSettings getSettings() {
        return (settings);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void initLightandShadow() {
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-1f, -1f, -1f).normalizeLocal());
        rootNode.addLight(dl);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f));
        rootNode.addLight(ambient);

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 1);
        dlsr.setLight(dl);
        viewPort.addProcessor(dlsr);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        SSAOFilter ssao = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
        fpp.addFilter(ssao);
        viewPort.addProcessor(fpp);
    }

    private void initCam() {
        flyCam.setEnabled(false);
        flyCam.setMoveSpeed(25);
        cam.setLocation(new Vector3f(0f, 15f, 15f));
        cam.lookAt(new Vector3f(0, 5f, 0), Vector3f.UNIT_Y);
    }
    
    private void initGUI() {
        setDisplayStatView(false);
        setDisplayFps(false);
    }
}