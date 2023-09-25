package com.jme3.skulls.ui;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.field.VerticalProgressBar;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.panel.VPanel;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.skulls.game.Game;
import com.jme3.skulls.game.Player;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;

/**
 * 
 * This class will handle the show of in game tools and hud
 * The score and powers will be shown here.
 *
 * @author nidebruyn
 */
public class ScoreDialog extends PopupDialog {
    
    private VPanel powersPanel;
    private VerticalProgressBar enemiesProgressBar;
    private Image icon;
    private Label enemiesLabel;
    
    public ScoreDialog(Window window) {
        super(window, null, window.getWidth(), window.getHeight());
        
        setTitle("Score: 0");
        setTitleColor(ColorRGBA.White);
        setTitleSize(26);
        title.centerTop(0, 2);
        
        powersPanel = new VPanel(window, this, null, 80, 560);
        powersPanel.leftCenter(0, 0);
        this.add(powersPanel);
        
        powersPanel = new VPanel(window, this, null, 80, window.getHeight());
        powersPanel.rightCenter(0, 0);
        this.add(powersPanel);
        
        createPowerButton("Interface/icons/barrel-bomb.png", Player.POWER_BOMB, 3);
        createPowerButton("Interface/icons/acid-gas.png", Player.POWER_GAS, 15);
        createPowerButton("Interface/icons/curse.png", Player.POWER_CURSE, 6);
        createPowerButton("Interface/icons/poison.png", Player.POWER_POISON, 10);
        createPowerButton("Interface/icons/type-b.png", Player.POWER_SWITCH_TO_X, 10);
        createPowerButton("Interface/icons/type-a.png", Player.POWER_SWITCH_TO_Y, 10);
        createPowerButton("Interface/icons/blockade.png", Player.POWER_STOP, 6);
        createPowerButton("Interface/icons/mutant.png", Player.POWER_MUTANT, 25);        
        
        powersPanel.layout();
        
        enemiesProgressBar = new VerticalProgressBar(this, "Interface/progressborder.png", "Interface/progressinner.png", 50, 400);
        enemiesProgressBar.leftCenter(10, 0);
        enemiesProgressBar.setProgress(0.5f);
        
        icon = new Image(this, "Interface/icons/skull-counter.png", 50, 50);
        icon.leftCenter(10, -230);
        
        enemiesLabel = new Label(this, "0", 24, 50,50);
        enemiesLabel.setAlignment(BitmapFont.Align.Center);
        enemiesLabel.leftCenter(10, 230);
        
    }
    
    protected void createPowerButton(String image, String id, int sec) {
        PowerButton button = new PowerButton(powersPanel, id, image, sec, 0);
        
    }
    
    public void addPowerButtonListener(TouchButtonListener buttonListener) {
        for (int i = 0; i < powersPanel.getWidgets().size(); i++) {
            PowerButton powerButton = (PowerButton)powersPanel.getWidgets().get(i);
            powerButton.addTouchButtonListener(buttonListener);
        }
    }
    
    public void setEnemyCount(int count) {
        if (count < 0) count = 0;
        
        enemiesLabel.setText(count + "");
        enemiesProgressBar.setProgress((float)count/(float)Game.MAX_ENEMIES);
    }
    
    public void setScore(int score) {
        setTitle("Score: " + score);
    }
    
    public void usePower(String powerStr) {
        for (int i = 0; i < powersPanel.getWidgets().size(); i++) {
            PowerButton powerButton = (PowerButton)powersPanel.getWidgets().get(i);
            if (powerButton.getId() != null && powerButton.getId().equals(powerStr) && powerButton.isEnabled()) {
                powerButton.use();
                break;
            }
            
        }
        
    }
    
    public void resetPowers() {
        for (int i = 0; i < powersPanel.getWidgets().size(); i++) {
            PowerButton powerButton = (PowerButton)powersPanel.getWidgets().get(i);
            powerButton.reset();            
        }
    }

    @Override
    public void show() {
        
        setEnemyCount(0);
        setScore(0);
        
        super.show(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
