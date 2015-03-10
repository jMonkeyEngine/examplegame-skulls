package com.jme3.skulls.ui;

import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;

/**
 * This is a Dialog that will be shown when a player has won game.
 * There will be 3 buttons, a retry button a next button and a menu button.
 * Also some info in the middle of the dialog describing player achievements.
 * 
 * @author nidebruyn
 */
public class WinDialog extends AbstractDialog {
    
    private LargeButton retryButton;
    private LargeButton nextButton;
    private LargeButton menuButton;
    private Label info;
    private Label scoreLabel;
    private Label bestLabel;

    public WinDialog(Window window) {
        super("Game Won", window);
        
        info = new Label(this, "Well done, you have successfully killed all the creatures.", 22, 560, 80);
        info.centerAt(0, 110);
        
        
        Label l = new Label(this, "Score", 28, 120, 30);
        l.setTextColor(ColorRGBA.DarkGray);
        l.centerAt(-70, 20);
        l.setAlignment(BitmapFont.Align.Left);

        scoreLabel = new Label(this, "0", 26, 120, 30);
        scoreLabel.setTextColor(ColorRGBA.White);
        scoreLabel.centerAt(70, 20);
        scoreLabel.setAlignment(BitmapFont.Align.Right);
        
        l = new Label(this, "Best", 28, 120, 30);
        l.setTextColor(ColorRGBA.DarkGray);
        l.centerAt(-70, -30);
        l.setAlignment(BitmapFont.Align.Left);

        bestLabel = new Label(this, "0", 26, 120, 30);
        bestLabel.setTextColor(ColorRGBA.Yellow);
        bestLabel.centerAt(70, -30);
        bestLabel.setAlignment(BitmapFont.Align.Right);
        
        
        menuButton = new LargeButton(this, "windialogmenubutton", "Menu");
        menuButton.leftBottom(20, 20);
        
        retryButton = new LargeButton(this, "windialogplaybutton", "Retry");
        retryButton.centerBottom(0, 20);
        
        nextButton = new LargeButton(this, "windialognextbutton", "Next");
        nextButton.rightBottom(20, 20);
                
    }
    
    public void addRetryButtonTouchListener(TouchButtonAdapter touchButtonAdapter) {
        retryButton.addTouchButtonListener(touchButtonAdapter);
    }
    
    public void addMenuButtonTouchListener(TouchButtonAdapter touchButtonAdapter) {
        menuButton.addTouchButtonListener(touchButtonAdapter);
    }
    
    public void addNextButtonTouchListener(TouchButtonAdapter touchButtonAdapter) {
        nextButton.addTouchButtonListener(touchButtonAdapter);
    }
    
    public void show(int score, int oldScore) {
        scoreLabel.setText(score + "");
        bestLabel.setText(oldScore + "");        
        super.show();
    }
}
