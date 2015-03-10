/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
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

    @Override
    protected void init() {
        
        heading = new Image(hudPanel, "Interface/heading.png", 1024, 256, true);
        heading.centerTop(0, 20);
        
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

    }

    @Override
    protected void show() {
        setPreviousScreen(null);

    }

    @Override
    protected void exit() {

    }

    @Override
    protected void pause() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
