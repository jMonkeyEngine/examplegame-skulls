/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.game;

import com.bruynhuis.galago.games.simplecollision.SimpleCollisionGame;
import com.bruynhuis.galago.games.simplecollision.SimpleCollisionPlayer;
import com.jme3.math.Vector3f;

/**
 * The player class will keep track of the current player data.
 * This will be things such as weapons, score, lives, etc.
 * 
 * @author nidebruyn
 */
public class Player extends SimpleCollisionPlayer {
    
    /**
     * These are statics defining the various power options
     * A player can select only one at a time.
     */
    public static final String POWER_BOMB = "power_bomb";
    public static final String POWER_GAS = "power_gas";
    public static final String POWER_STERILIZATION = "power_ster";
    public static final String POWER_POIZON = "power_poison";
    public static final String POWER_MALE = "power_male";
    public static final String POWER_FEMALE = "power_female";
    public static final String POWER_STOP = "power_stop";
    public static final String POWER_MUTATION = "power_mutate";
    
    private String selectedPower;

    public Player(SimpleCollisionGame basicGame) {
        super(basicGame);
    }

    @Override
    protected void init() {
        
    }

    @Override
    public Vector3f getPosition() {
        return playerNode.getWorldTranslation();
    }

    @Override
    public void doDie() {
        
    }
    
    public void setPosition(Vector3f position) {
        playerNode.setLocalTranslation(position);
    }

    public String getSelectedPower() {
        return selectedPower;
    }

    public void setSelectedPower(String selectedPower) {
        this.selectedPower = selectedPower;
    }
    
    
}
