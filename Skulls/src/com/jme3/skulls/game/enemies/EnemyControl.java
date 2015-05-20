/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.game.enemies;

import com.bruynhuis.galago.control.AnimationControl;
import com.bruynhuis.galago.listener.AnimationListener;
import com.bruynhuis.galago.util.Timer;
import static com.jme3.skulls.game.Game.TILE_SIZE;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.skulls.game.Game;
import com.jme3.skulls.game.Tile;

/**
 * The EnemyControl will control all movement and behavior of the enemies
 * running around.
 *
 * @author nidebruyn
 */
public abstract class EnemyControl extends AbstractControl implements AnimationListener {

    protected Game game;
    protected float moveSpeed;
    protected float turnSpeed;
        
    protected boolean move = false;
    protected boolean alive = false;
    protected boolean turning = false;
    protected boolean idle = false;
    
    protected Timer gasKillTimer;
    protected Timer gasClearTimer = new Timer(50);
    protected boolean underGasTile = false;
    
    protected Vector3f targetPosition;
    protected Tile nextTile;
    protected Tile targetTile;
    protected Tile fromTile;
//    protected AnimationControl animationControl;
    protected float animationSpeed = 1f;

    public EnemyControl(Game game, Tile tile) {
        this.game = game;
        this.targetTile = tile;

    }
    
    protected abstract void initEnemy();

    private void init() {
        initEnemy();
        
        targetPosition = new Vector3f(targetTile.getxPos() * TILE_SIZE, spatial.getWorldTranslation().y, targetTile.getzPos() * TILE_SIZE);

    }
    
    protected abstract void updateEnemy(float tpf);

    @Override
    protected void controlUpdate(float tpf) {

        //Check if game has started
        if (game.isStarted() && !game.isPaused()) {

            /*
             * Check to make this creature alive
             * This method will only excecute once.
             */
            if (!alive) {

//                if (animationControl == null) {
//                    animationControl = new AnimationControl();
//                    Spatial animSpatial = ((Node) spatial).getChild("body");
//                    animSpatial.addControl(animationControl);
//                    animationControl.addAnimationListener(EnemyControl.this);
//
//                }

                init();

                alive = true;
                move = true;

                turn();
                
                Spatial shadow = game.getBaseApplication().getModelManager().getModel("Models/enemies/shadow.j3o");
                ((Node)spatial).attachChild(shadow);

            }
            
            updateEnemy(tpf);

            /*
             * Every movement and actions will depend on these conditions.
             */
            if (alive && move) {
                
//                animationControl.play("walk", true, false, animationSpeed);

                /*
                 * Here we handle all kinds of movements
                 * We will check if it is time to turn to next tile or just move to next tile
                 */
                if (!idle && !turning) {
                    if (spatial.getLocalTranslation().distance(targetPosition) < 0.1f) {
                        turn();

                    } else {
                        spatial.lookAt(targetPosition, Vector3f.UNIT_Y);
                        moveForward(tpf);

                    }
                }

            }
            
            //Check if this enemy is under a gas tile
            if (alive && gasKillTimer != null) {
                gasKillTimer.update(tpf);
                if (gasKillTimer.finished()) {
                    if (underGasTile) {
                        doDie();
                        game.addScore(10);
                    }
                    gasKillTimer.stop();
                    gasKillTimer = null;
                }                
                
                gasClearTimer.update(tpf);
                if (gasClearTimer.finished()) {
                    underGasTile = false;
                    gasClearTimer.reset();
                }
                
            }

        }

    }

    /**
     * Move the enemy forward.
     *
     * @param tpf
     */
    protected void moveForward(float tpf) {
        spatial.move(spatial.getWorldRotation().clone().getRotationColumn(2).mult(tpf * moveSpeed));
    }

    /**
     * Will make the enemy turn just there.
     */
    public void turn() {
        nextTile = game.getNextAdjacentTile(targetTile, fromTile);
        fromTile = targetTile;
        targetTile = nextTile;
        targetPosition = new Vector3f(targetTile.getxPos() * TILE_SIZE, spatial.getWorldTranslation().y, targetTile.getzPos() * TILE_SIZE);

    }
    
    /**
     * This will turn the enemy around in its tracks
     */
    public void turnBack() {
        nextTile = fromTile;
        fromTile = targetTile;
        targetTile = nextTile;
        
        if (targetTile != null) {
            targetPosition = new Vector3f(targetTile.getxPos() * TILE_SIZE, spatial.getWorldTranslation().y, targetTile.getzPos() * TILE_SIZE);
        } else {
            turn();
            
        }        
    
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    /**
     * This method will change the enemy to type x enemy
     */
    public void changeToTypeX() {
        game.loadTypeX(targetTile);
        game.removeEnemy(this);
        spatial.removeFromParent();
    }

    /**
     * This method will change the enemy to type y enemy
     */
    public void changeToTypeY() {
        game.loadTypeY(targetTile);
        game.removeEnemy(this);
        spatial.removeFromParent();

    }


    /**
     * For debugging
     *
     * @param text
     */
    protected void log(String text) {
        System.out.println(text);
    }

    /**
     * This method must be called when the enemy has died.
     */
    public void doDie() {
        game.getBaseApplication().getSoundManager().playSound("death");
        game.getBaseApplication().getEffectManager().doEffect("die", spatial.getWorldTranslation());
        game.removeEnemy(this);
        spatial.removeFromParent();

    }

    public void doAnimationDone(String animationName) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This will set the enemy under the influence of the gas
     * @param underGasTile 
     */
    public void setUnderGasTile(boolean underGasTile) {
                
        if (gasKillTimer == null) {
            gasKillTimer = new Timer(200);
            gasKillTimer.start();
            gasClearTimer.start();
            
        }
        
        this.underGasTile = underGasTile;
    }    
    
}
