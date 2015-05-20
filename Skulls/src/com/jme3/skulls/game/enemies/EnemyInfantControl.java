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
public class EnemyInfantControl extends EnemyControl {

    protected float growthTime;
    protected Timer growTimer;

    public EnemyInfantControl(Game game, Tile tile) {
        super(game, tile);
    }

    @Override
    protected void initEnemy() {
        spatial.setLocalScale(0.9f);
        animationSpeed = 2.4f;
        moveSpeed = 3.3f;
        turnSpeed = 1f;
        growthTime = (float) FastMath.nextRandomInt(800, 1000);
        growTimer = new Timer(growthTime);
    }

    @Override
    protected void updateEnemy(float tpf) {

        //Start to count the grow timer
        if (!alive) {            
            if (growTimer != null) {
                growTimer.start();
            }
        }

        //Check when crow time is over
        if (alive && move) {            
            if (growTimer != null) {
                growTimer.update(tpf);
                if (growTimer.finished()) {
                    growTimer.stop();
                    changeToTypeXorY();
                }
            }
        }
    }
    
    /**
     * This method will be called when the young enemy must change into a type X
     * or type Y enemy
     */
    protected void changeToTypeXorY() {
        int i = FastMath.nextRandomInt(0, 1);
        if (i == 0) {
            game.loadTypeX(targetTile);
        } else {
            game.loadTypeY(targetTile);
        }

        game.removeEnemy(this);
        spatial.removeFromParent();
    }

}
