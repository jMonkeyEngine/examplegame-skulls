/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.ui;

import com.bruynhuis.galago.ui.button.ToggleButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class OnOffButton extends ToggleButton {
    
    public OnOffButton(Panel panel, String id) {
        super(panel, id, "Interface/largebutton.png", 254, 54);
        setFontSize(22);
        addEffect(new TouchEffect(this));
        setTextColor(ColorRGBA.LightGray);

    }
}
