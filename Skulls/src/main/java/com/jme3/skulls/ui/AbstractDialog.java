package com.jme3.skulls.ui;

import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;

/**
 * 
 * This class must be extended by all dialogs that might be in the game.
 * It helps with a unique and equal look and feel.
 * Later one can add nice tween effects to it.
 *
 * @author nidebruyn
 */
public abstract class AbstractDialog extends PopupDialog {
    
    public AbstractDialog(String heading, Window window) {
        super(window, "Interface/popup.png", 814, 494);
        
        setTitle(heading);
        setTitleColor(ColorRGBA.White);
        setTitleSize(38);
        title.centerTop(0, 10);
    }
    
}
