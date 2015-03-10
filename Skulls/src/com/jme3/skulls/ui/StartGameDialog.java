package com.jme3.skulls.ui;

import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.window.Window;

/**
 * This is a Dialog that will be shown before a player starts a new game.
 * It will give game play instructions and have a play button and menu button.
 * @author nidebruyn
 */
public class StartGameDialog extends AbstractDialog {
    
    private LargeButton playButton;
    private LargeButton menuButton;
    private Label info;

    public StartGameDialog(Window window) {
        super("Start Game", window);
        
        info = new Label(this, "Are you ready to destroy them creatures?", 20, 600, 80);
        info.center();
        
        playButton = new LargeButton(this, "startdialogplaybutton", "Start");
        playButton.rightBottom(20, 20);
        
        menuButton = new LargeButton(this, "startdialogmenubutton", "Menu");
        menuButton.leftBottom(20, 20);
        
    }
    
    public void addPlayButtonTouchListener(TouchButtonAdapter touchButtonAdapter) {
        playButton.addTouchButtonListener(touchButtonAdapter);
    }
    
    public void addMenuButtonTouchListener(TouchButtonAdapter touchButtonAdapter) {
        menuButton.addTouchButtonListener(touchButtonAdapter);
    }
}
