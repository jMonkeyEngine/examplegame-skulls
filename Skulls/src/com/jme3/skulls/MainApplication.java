package com.jme3.skulls;

import com.bruynhuis.galago.app.Base3DApplication;
import static com.bruynhuis.galago.app.BaseApplication.BACKGROUND_COLOR;
import com.bruynhuis.galago.resource.EffectManager;
import com.bruynhuis.galago.resource.ModelManager;
import com.bruynhuis.galago.resource.ScreenManager;
import com.bruynhuis.galago.resource.SoundManager;
import com.bruynhuis.galago.resource.TextureManager;
import com.jme3.skulls.screens.EditScreen;
import com.jme3.skulls.screens.MenuScreen;
import com.jme3.skulls.screens.PlayScreen;
import com.jme3.skulls.screens.SettingsScreen;
import com.jme3.math.ColorRGBA;

/**
 * This is the main application class which will be excecuted when application
 * starts.
 *
 * @author Nicolaas de Bruyn
 */
public class MainApplication extends Base3DApplication {
    
     /**
     * This is the default constructor and must be called from the main()
     * method.
     */
    public MainApplication() {
        //This constructor is used to initialize app specific things
        //Title, native width, native height, gameSaveDataFile, font if not default, splash if needed, enablephysics, enable size selection
        super("Skulls", 1280, 720, "skulls.save", null, null, true);
    }

    public static void main(String[] args) {
        new MainApplication();
    }

    @Override
    protected void preInitApp() {
        
        //This method is called before any screen or game data is loaded
        BACKGROUND_COLOR = ColorRGBA.Black;
        splashInfoMessage = "jMonkeyEngine3 Example Game";
//        setRecordVideo(true);

    }

    @Override
    protected void postInitApp() {
        //This method is called after all screens and game data is loaded
        showScreen("menu");
        
    }

    @Override
    protected void initScreens(ScreenManager screenManager) {
        //This method will be called to load screen states defined
        screenManager.loadScreen("menu", new MenuScreen());
        screenManager.loadScreen("settings", new SettingsScreen());
        screenManager.loadScreen("play", new PlayScreen());
        screenManager.loadScreen("edit", new EditScreen());

    }

    @Override
    protected void initSound(SoundManager soundManager) {
        //Here we will preload sounds and music we wish to use in the game
        soundManager.loadSoundFx("mate", "Sounds/mate.ogg");
        soundManager.setSoundVolume("mate", 0.1f);

        soundManager.loadSoundFx("death", "Sounds/SFX/Misc/Death/skull-death.ogg");
        soundManager.setSoundVolume("death", 0.6f);

        soundManager.loadSoundFx("button", "Sounds/button.ogg");
        soundManager.setSoundVolume("button", 0.5f);
        
        soundManager.loadSoundFx("bomb", "Sounds/SFX/Powers/Barrel-bomb/explosion_deep_echo_01.ogg");
        soundManager.setSoundVolume("bomb", 0.7f);
        
        soundManager.loadSoundFx("timer", "Sounds/timer.ogg");
        soundManager.setSoundVolume("timer", 0.4f);
        
        soundManager.loadSoundFx("switch", "Sounds/SFX/Powers/Type-switch/161628__crazyfrog249__blop.ogg");
        soundManager.setSoundVolume("switch", 0.8f);
        
        soundManager.loadSoundFx("block", "Sounds/SFX/Powers/Blockade/211500__taira-komori__knocking-wall.ogg");
        soundManager.setSoundVolume("block", 0.8f);
        
        soundManager.loadSoundFx("curse", "Sounds/SFX/Powers/Curse/necro-death.ogg");
        soundManager.setSoundVolume("curse", 1f);
        
        soundManager.loadSoundFx("bubble", "Sounds/SFX/Powers/Poison/bubble3times.ogg");
        soundManager.setSoundVolume("bubble", 0.5f);
        
        soundManager.loadSoundFx("mutant", "Sounds/SFX/Powers/Mutant/217282__jarredgibb__zombie4.ogg");
        soundManager.setSoundVolume("mutant", 0.6f);
        
        soundManager.loadSoundFx("acid", "Sounds/SFX/Powers/Acid-gas/acid_burn_sizzle.ogg");
        soundManager.setSoundVolume("acid", 0.6f);
        
        soundManager.loadSoundFx("spawn", "Sounds/SFX/Misc/Spawn/spawn-miniskull.ogg");
        soundManager.setSoundVolume("spawn", 0.6f);
        
        
        //Music
        
        soundManager.loadMusic("menu", "Sounds/Music/Dark_Fun.ogg");
        soundManager.setMusicVolume("menu", 0.2f);

        soundManager.loadMusic("level", "Sounds/Music/haunted_village.ogg");
        soundManager.setMusicVolume("level", 0.2f);

    }

    @Override
    protected void initEffect(EffectManager effectManager) {
        //Here we preload particle effects that we wish to execute at runtime
        effectManager.loadEffect("love", "Models/effects/love.j3o");
        effectManager.loadEffect("die", "Models/effects/dust.j3o");
        effectManager.loadEffect("bomb", "Models/effects/explosion/explosion.j3o");

    }

    @Override
    public void initModelManager(ModelManager modelManager) {
        //Here we preload models and materials that might be shared between levels.
        //It is not needed one can still call the assetmanager to load models.

        modelManager.loadMaterial("Materials/tile-enemy.j3m");
        modelManager.loadMaterial("Materials/tile-floor.j3m");
        modelManager.loadMaterial("Materials/tile-wall.j3m");

        modelManager.loadModel("Models/skulls/typeX.j3o");
        modelManager.loadModel("Models/skulls/typeY.j3o");
        modelManager.loadModel("Models/skulls/infant.j3o");
        modelManager.loadModel("Models/skulls/mutant.j3o");
        
        modelManager.loadModel("Models/enemies/shadow.j3o");

        modelManager.loadModel("Models/powers/poison.j3o");
        modelManager.loadModel("Models/powers/plant2/plant2.j3o");
        modelManager.loadModel("Models/powers/plant1/plant1.j3o");
        modelManager.loadModel("Models/powers/stop.j3o");
        modelManager.loadModel("Models/powers/bomb.j3o");
        modelManager.loadModel("Models/powers/barrel.j3o");
        modelManager.loadModel("Models/powers/sterilize.j3o");
        modelManager.loadModel("Models/powers/gas.j3o");
        modelManager.loadModel("Models/powers/acid.j3o");
        modelManager.loadModel("Models/powers/lantern/lantern.j3o");

        modelManager.loadModel("Models/static/dungeon_pixel.j3o");
        modelManager.loadMaterial("Models/static/dungeon_pixel.j3m");

    }

    /**
     * Helper method which returns an instance of the play screen
     *
     * @return
     */
    public PlayScreen getPlayScreen() {
        return (PlayScreen) screenManager.getScreen("play");
    }

    @Override
    protected void initTextures(TextureManager textureManager) {
        textureManager.loadTexture("Interface/popup.png");
    }

    @Override
    protected boolean isPhysicsEnabled() {
        return false;
    }

}
