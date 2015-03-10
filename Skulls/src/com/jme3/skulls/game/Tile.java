/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.game;

import com.jme3.scene.Spatial;
import java.io.Serializable;

/**
 *
 * @author nidebruyn
 */
public class Tile implements Serializable {
    
    private String name;
    private int xPos;
    private int zPos;
    private transient Spatial spatial;

    public Tile(String name, int xPos, int zPos, Spatial spatial) {
        this.name = name;
        this.xPos = xPos;
        this.zPos = zPos;
        this.spatial = spatial;
    }

    public String getName() {
        return name;
    }

    public int getxPos() {
        return xPos;
    }

    public int getzPos() {
        return zPos;
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setzPos(int zPos) {
        this.zPos = zPos;
    }

    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }

    @Override
    public String toString() {
        return "Tile{" + "name=" + name + ", xPos=" + xPos + ", zPos=" + zPos + '}';
    }
    
}
