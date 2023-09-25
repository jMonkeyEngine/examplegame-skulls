/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.game.enemies;

import com.jme3.skulls.game.Game;
import com.jme3.skulls.game.Tile;

/**
 *
 * @author nidebruyn
 */
public class MutantControl extends EnemyControl {

    protected int kills;

    public MutantControl(Game game, Tile tile) {
        super(game, tile);
    }

    @Override
    protected void initEnemy() {
        spatial.setLocalScale(1.2f);
        animationSpeed = 2.4f;
        moveSpeed = 3.3f;
        turnSpeed = 1f;
    }

    @Override
    protected void updateEnemy(float tpf) {
    }

    /**
     * Public method for keeping track of kills. This will be for mutated
     * enemies. if kills are 5 then terminate
     */
    public void doMakeKill() {
        game.getBaseApplication().getSoundManager().playSound("mutant");
        kills++;

        if (kills == 5) {
            doDie();
        }
    }
}
