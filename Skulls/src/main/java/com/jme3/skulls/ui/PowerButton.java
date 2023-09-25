/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.util.Timer;
import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author nidebruyn
 */
public class PowerButton extends TouchButton {

    private Timer regenTimer = new Timer(10);
    private int counter = 0;
    private int MAX_COUNT = 4;
    
    public PowerButton(Panel panel, String id, String image, int regenSeconds, int startCount) {
        super(panel, id, image, 60, 60);
        setFontSize(20);
        setText(" ");
        setTextColor(ColorRGBA.White);

        regenTimer.setMaxTime(regenSeconds*100f); //The timer that counts how long before new power is generated.
        counter = startCount;

        bitmapText.setLocalTranslation(5, 36, 0);
        setTextAlignment(BitmapFont.Align.Right);
        this.addEffect(new TouchEffect(this));

        //The update loop to help with the timer and regen
        widgetNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                
                if (counter <= MAX_COUNT - 1) {
                    regenTimer.update(tpf);
                    if (regenTimer.finished()) {
                        counter++;
                        setText("" + counter);

                        if (counter >= MAX_COUNT) {
                            regenTimer.stop();
                        } else {
                            regenTimer.reset();
                        }
                    }

                }
//                
//                if (counter <= 0) {
//                    setEnabled(false);
//                } else {
//                    setEnabled(true);
//                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

    }

    public void use() {
        if (counter > 0) {
            counter--;
            regenTimer.reset();
            setText("" + counter);
        }
    }

    public void reset() {
        counter = 0;
        setEnabled(true);
        regenTimer.start();
        setText("" + counter);

    }

    public String getId() {
        return id;
    }

    @Override
    public boolean isEnabled() {
        return counter > 0;
    }
    
    
}
