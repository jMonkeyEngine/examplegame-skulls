/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.ui;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class LargeButton extends TouchButton {
    
    private static final float scale = 1.0f;
    
    public LargeButton(Panel panel, String id, String text) {
        super(panel, id, "Interface/largebutton.png", 254*scale, 56*scale);
        setFontSize(25);
        setText(text);
        addEffect(new TouchEffect(this));
        setTextColor(ColorRGBA.LightGray);
    }
}
