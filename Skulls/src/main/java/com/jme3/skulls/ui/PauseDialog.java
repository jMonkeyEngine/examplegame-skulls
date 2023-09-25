package com.jme3.skulls.ui;

import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.window.Window;

/**
 * This is a Dialog that will be shown when a player paused the game.
 * There will be 3 buttons, a retry button a resume button and a menu button.
 * Also some info in the middle of the dialog describing player achievements.
 * 
 * @author nidebruyn
 */
public class PauseDialog extends AbstractDialog {
    
    private LargeButton retryButton;
    private LargeButton resumeButton;
    private LargeButton menuButton;
    private Label info;

    public PauseDialog(Window window) {
        super("Game Paused", window);
        
        info = new Label(this, "The game is paused, would you like to continue?", 20, 600, 80);
        info.center();
        
        menuButton = new LargeButton(this, "pausedialogmenubutton", "Menu");
        menuButton.leftBottom(20, 20);
        
        retryButton = new LargeButton(this, "pausedialogplaybutton", "Retry");
        retryButton.centerBottom(0, 20);
        
        resumeButton = new LargeButton(this, "pausedialogresumebutton", "Resume");
        resumeButton.rightBottom(20, 20);
                
    }
    
    public void addRetryButtonTouchListener(TouchButtonAdapter touchButtonAdapter) {
        retryButton.addTouchButtonListener(touchButtonAdapter);
    }
    
    public void addMenuButtonTouchListener(TouchButtonAdapter touchButtonAdapter) {
        menuButton.addTouchButtonListener(touchButtonAdapter);
    }
    
    public void addResumeButtonTouchListener(TouchButtonAdapter touchButtonAdapter) {
        resumeButton.addTouchButtonListener(touchButtonAdapter);
    }
}
