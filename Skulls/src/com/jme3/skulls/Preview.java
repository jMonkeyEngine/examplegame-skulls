/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public class Preview extends SimpleApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Preview p = new Preview();
        p.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(50);
        cam.setLocation(new Vector3f(0, 0, 30));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        Spatial model = assetManager.loadModel("Models/enemies/mummy/mummy.j3o");
        rootNode.attachChild(model);
    }
}
