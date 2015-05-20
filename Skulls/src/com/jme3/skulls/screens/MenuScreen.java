/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.skulls.ui.LargeButton;

/**
 * This will be the menu screen. It is the first screen the player will see when the game is loaded.
 * This screen will have a Heading
 * It will have 3 button options (Play, Settings, Exit)
 * It might also have some background image.
 *
 * @author nidebruyn
 */
public class MenuScreen extends AbstractScreen {
     
    private Image heading;
    
    private LargeButton playButton;
    private LargeButton settingsButton;
    private LargeButton editButton;
    private LargeButton exitButton;
    private Spatial spatial1;
    private Spatial spatial2;

    @Override
    protected void init() {
        
        heading = new Image(hudPanel, "Interface/heading.png", 488, 206, true);
        heading.centerTop(0, 0);
        
        playButton = new LargeButton(hudPanel, "play_menu_button", "Play");
        playButton.centerAt(0, 50);
        playButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                //Check if the screen is finished loading and is showing
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("play");
                    
                }
            }
            
        });
        
        settingsButton = new LargeButton(hudPanel, "settings_menu_button", "Settings");
        settingsButton.centerAt(0, -50);
        settingsButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                //Check if the screen is finished loading and is showing
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("settings");
                    
                }
            }
            
        });
        
        exitButton = new LargeButton(hudPanel, "exit_menu_button", "Exit");
        exitButton.centerAt(0, -150);
        exitButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                //Check if the screen is finished loading and is showing
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    exitScreen();
                    
                }
            }
            
        });
        
        editButton = new LargeButton(hudPanel, "edit_menu_button", "Editor");
        editButton.rightBottom(0, 10);
        editButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                //Check if the screen is finished loading and is showing
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("edit");
                    
                }
            }
            
        });

    }

    @Override
    protected void load() {
        baseApplication.getSoundManager().playMusic("menu");
        
        spatial1 = baseApplication.getAssetManager().loadModel("Models/skulls/typeX.j3o");
        spatial1.setLocalTranslation(-2, 0, 0);
        spatial1.rotate(0, 20* FastMath.DEG_TO_RAD, 0);
        rootNode.attachChild(spatial1);
        
        spatial2 = baseApplication.getAssetManager().loadModel("Models/skulls/typeY.j3o");
        spatial2.setLocalTranslation(2, 0, 0);
        spatial2.rotate(0, -20* FastMath.DEG_TO_RAD, 0);
        rootNode.attachChild(spatial2);
        
        camera.setLocation(new Vector3f(0, -0.7f, 5));
        camera.lookAt(new Vector3f(0, -0.7f, 0), Vector3f.UNIT_Y);

    }

    @Override
    protected void show() {
        setPreviousScreen(null);

    }

    @Override
    protected void exit() {
        spatial1.removeFromParent();
        spatial2.removeFromParent();

    }

    @Override
    protected void pause() {

    }

    
}
