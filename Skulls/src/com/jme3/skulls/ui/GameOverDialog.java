package com.jme3.skulls.ui;

import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.window.Window;

/**
 * This is a Dialog that will be shown when a player has lost the game.
 * There will be 2 buttons, a retry button and a menu button.
 * Also some info in the middle of the dialog.
 * 
 * @author nidebruyn
 */
public class GameOverDialog extends AbstractDialog {
    
    private LargeButton retryButton;
    private LargeButton menuButton;
    private Label info;

    public GameOverDialog(Window window) {
        super("Game Over", window);
        
        info = new Label(this, "Oh no you lost the fight, you can give it another shot?", 20, 600, 80);
        info.center();
        
        retryButton = new LargeButton(this, "gameoverdialogplaybutton", "Retry");
        retryButton.rightBottom(20, 20);
        
        menuButton = new LargeButton(this, "gameoverdialogmenubutton", "Menu");
        menuButton.leftBottom(20, 20);
        
    }
    
    public void addRetryButtonTouchListener(TouchButtonAdapter touchButtonAdapter) {
        retryButton.addTouchButtonListener(touchButtonAdapter);
    }
    
    public void addMenuButtonTouchListener(TouchButtonAdapter touchButtonAdapter) {
        menuButton.addTouchButtonListener(touchButtonAdapter);
    }
}
