/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.game.enemies;

import com.bruynhuis.galago.util.Timer;
import com.jme3.math.FastMath;
import com.jme3.skulls.game.Game;
import com.jme3.skulls.game.Tile;

/**
 *
 * @author nidebruyn
 */
public class EnemyYControl extends EnemyControl {

    protected Timer ritualTimer = new Timer(200);
    protected Timer pregnantTimer = new Timer(200);
    protected Timer cursedTimer = new Timer(200);
    protected boolean ritualActive = false;
    protected boolean pregnant = false;
    protected boolean cursed = false;
    protected int spawnChildCount = 1;

    public EnemyYControl(Game game, Tile tile) {
        super(game, tile);
    }

    @Override
    protected void initEnemy() {
        spatial.setLocalScale(1.2f);
        animationSpeed = 1.8f;
        moveSpeed = 2.5f;
        turnSpeed = 1f;

    }

    @Override
    protected void updateEnemy(float tpf) {

        if (alive && move) {
            /*
             * Check if this enemy is in a ritual, if so count down
             */
            if (ritualActive) {
                ritualTimer.update(tpf);

                //When the ritual is done follow in to the next out of action stepsgg
                if (ritualTimer.finished()) {
                    
                    pregnant = true;
                    idle = false;
                    ritualActive = false;
                    spawnChildCount = FastMath.nextRandomInt(1, 5);
                    pregnantTimer.reset();
                }
            }

            //Handle or check if the enemy is cursed
            if (cursed) {
                cursedTimer.update(tpf);
                if (cursedTimer.finished()) {
                    cursed = false;
                    cursedTimer.stop();
                }
            }

            //Handle the pregnant enemy here
            if (pregnant) {
                pregnantTimer.update(tpf);
                if (pregnantTimer.finished()) {
                    spawnChildCount--;
                    game.spawnInfant(targetTile);

                    if (spawnChildCount > 0) {
                        pregnantTimer.reset();
                    } else {
                        pregnant = false;
                        pregnantTimer.stop();
                    }

                }
            }
        }
    }    

    public boolean isPregnant() {
        return pregnant;
    }
    

    /**
     * This method must be called to change a female to be pregnant.
     */
    public void startRitual() {
        //Return out if this enemy is already mating
        if (ritualActive || cursed || pregnant) {
            return;
        }

        idle = true;
        ritualActive = true;
        ritualTimer.reset();
        
        game.getBaseApplication().getEffectManager().doEffect("love", spatial.getWorldTranslation().add(0, 1, 0), 500f);
        game.getBaseApplication().getSoundManager().playSound("mate");

    }
    
    /**
     * Sets this enemy to be Cursed
     * @param disabled 
     */
    public void setCursed(boolean disabled) {        
        
        if (disabled && !cursed) {
            cursedTimer.setMaxTime(600);
            cursedTimer.reset();
            cursed = true;
            
        } else {
            cursed = false;
            cursedTimer.stop();
            cursedTimer.setMaxTime(200);
        }
        
    }

    public boolean isCursed() {
        return cursed;
    }
}
