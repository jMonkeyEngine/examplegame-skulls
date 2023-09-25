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
public class PowerButton1 extends TouchButton {

    private Timer timer = new Timer(10);
    private boolean once = false;
    private int regenerationSeconds;
    private int countSeconds;
    private boolean disabled = false;

    public PowerButton1(Panel panel, String id, String image, int regenSeconds) {
        super(panel, id, image, 60, 60);
        setFontSize(20);
        setText(" ");
        setTextColor(ColorRGBA.White);
        this.regenerationSeconds = regenSeconds;
        bitmapText.setLocalTranslation(5, 36, 0);
        setTextAlignment(BitmapFont.Align.Right);
        this.addEffect(new TouchEffect(this));

        if (regenSeconds == 0) {
            once = true;
        } else {
            timer.setMaxTime(100f);
            once = false;
        }

        if (!once) {
            widgetNode.addControl(new AbstractControl() {
                @Override
                protected void controlUpdate(float tpf) {

                    if (disabled) {
//                        System.out.println("count = " + countSeconds);
                        if (countSeconds > 0) {
                            timer.update(tpf);
                            if (timer.finished()) {
                                timer.reset();
                                countSeconds--;
                                setText("" + countSeconds);

                            }

                        } else {
                            reset();
                        }
                    }



                }

                @Override
                protected void controlRender(RenderManager rm, ViewPort vp) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
        }

    }

    public void use() {
        disabled = true;
        setEnabled(false);
        countSeconds = regenerationSeconds;
        timer.reset();

    }

    public void reset() {
        setText("");
        disabled = false;
        setEnabled(true);
        timer.stop();
    }

    public String getId() {
        return id;
    }
}
