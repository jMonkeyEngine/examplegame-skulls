/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.jme3.font.BitmapFont;
import com.jme3.skulls.ui.LargeButton;
import com.jme3.skulls.ui.OnOffButton;

/**
 * This will be the settings screen. The player can switch music on or off here.
 * This screen will have a Heading It will have 2 toggle button options (Music
 * :on/off, Sound : on/off) It will also have a back/menu button It might also
 * have some background image.
 *
 * @author nidebruyn
 */
public class SettingsScreen extends AbstractScreen {

    private Label heading;
    private Label soundLabel;
    private Label musicLabel;
    private Label fxLabel;
    private OnOffButton soundButton;
    private OnOffButton musicButton;
//    private OnOffButton fxButton;
    private GridPanel gridPanel;
    private LargeButton exitButton;

    @Override
    protected void init() {

        heading = new Label(hudPanel, "Settings", 62, 800, 100);
        heading.centerTop(0, 20);

        gridPanel = new GridPanel(hudPanel, 600, 200);
        hudPanel.add(gridPanel);

        soundLabel = new Label(gridPanel, "Sound", 28, 250, 60);
        soundLabel.setAlignment(BitmapFont.Align.Right);
        soundButton = new OnOffButton(gridPanel, "sound settings panel");
        soundButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");

                    if (baseApplication.getGameSaves().getGameData().isSoundOn()) {
                        baseApplication.getGameSaves().getGameData().setSoundOn(false);
                    } else {
                        baseApplication.getGameSaves().getGameData().setSoundOn(true);
                    }
                    baseApplication.getGameSaves().save();

                    baseApplication.getSoundManager().muteSound(!baseApplication.getGameSaves().getGameData().isSoundOn());
                }
            }
        });

        musicLabel = new Label(gridPanel, "Music", 28, 250, 60);
        musicLabel.setAlignment(BitmapFont.Align.Right);
        musicButton = new OnOffButton(gridPanel, "music settings panel");
        musicButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");

                    if (baseApplication.getGameSaves().getGameData().isMusicOn()) {
                        baseApplication.getGameSaves().getGameData().setMusicOn(false);
                    } else {
                        baseApplication.getGameSaves().getGameData().setMusicOn(true);
                    }
                    baseApplication.getGameSaves().save();
                    baseApplication.getSoundManager().muteMusic(!baseApplication.getGameSaves().getGameData().isMusicOn());

                    if (baseApplication.getGameSaves().getGameData().isMusicOn()) {
                        baseApplication.getSoundManager().playMusic("menu");
                    }
                }
            }
        });

//        fxLabel = new Label(gridPanel, "Special FX", 28, 250, 60);
//        fxLabel.setAlignment(BitmapFont.Align.Right);
//        fxButton = new OnOffButton(gridPanel, "fx settings panel");
//        fxButton.addTouchButtonListener(new TouchButtonAdapter() {
//            @Override
//            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
//                if (isActive()) {
//                    baseApplication.getSoundManager().playSound("button");
//
//                    if (baseApplication.getGameSaves().getGameData().isFxOn()) {
//                        baseApplication.getGameSaves().getGameData().setFxOn(false);
//                    } else {
//                        baseApplication.getGameSaves().getGameData().setFxOn(true);
//                    }
//                    
//                    baseApplication.getGameSaves().save();
//                    
//                }
//            }
//        });

        gridPanel.layout(2, 2);


        exitButton = new LargeButton(hudPanel, "exit_settings_button", "Menu");
        exitButton.leftBottom(10, 10);
        exitButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                //Check if the screen is finished loading and is showing
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("menu");

                }
            }
        });

    }

    @Override
    protected void load() {
       
        if (baseApplication.getGameSaves().getGameData().isSoundOn()) {
            soundButton.setSelected(true);
        } else {
            soundButton.setSelected(false);
        }
        
        if (baseApplication.getGameSaves().getGameData().isMusicOn()) {
            musicButton.setSelected(true);
        } else {
            musicButton.setSelected(false);
        }
        
//        if (baseApplication.getGameSaves().getGameData().isFxOn()) {
//            fxButton.setSelected(true);
//        } else {
//            fxButton.setSelected(false);
//        }

    }

    @Override
    protected void show() {
    }

    @Override
    protected void exit() {
    }

    @Override
    protected void pause() {

    }
    
}
