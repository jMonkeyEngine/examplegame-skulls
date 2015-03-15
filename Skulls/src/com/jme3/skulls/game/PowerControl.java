/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.game;

import com.bruynhuis.galago.util.Timer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;

/**
 * The EnemyControl will control all movement and behavior of the enemies
 * running around.
 *
 *
 * @author nidebruyn
 */
public class PowerControl extends AbstractControl {

    protected Game game;
    protected String type;
    protected boolean active = true;
    protected Timer inactiveTimer = new Timer(50);
    protected int health = 4;
    protected Timer bombTickTimer = new Timer(300);
    protected Timer bombActiveTimer = new Timer(100);
    protected Timer bombCountDownTimer = new Timer(32);
    protected Timer sterilizeTimer = new Timer(600);
    protected Timer gasExpandTimer = new Timer(100);
    protected Timer gasTimer = new Timer(700);
    protected boolean started = false;
    protected Tile nextTile;
    protected Tile targetTile;
    protected Tile fromTile;
    protected float gasTime = -1f;
    protected boolean explosion = false;
    protected boolean redColor = false;
    protected float timerCountDecelerate = 3.2f;

    public PowerControl(Game game, String type, Tile targetTile) {
        this.game = game;
        this.type = type;
        this.targetTile = targetTile;

    }

    private void init() {

        if (type.equals(Player.POWER_POIZON)) {
            game.getBaseApplication().getSoundManager().playMusic("bubble");
        }

        if (type.equals(Player.POWER_BOMB)) {
            bombTickTimer.start();
            active = false;
            spatial.setLocalScale(1f);

            if (!explosion) {
                bombCountDownTimer.start();
            }

        }

        if (type.equals(Player.POWER_CURSE)) {
            sterilizeTimer.start();
            active = true;
            spatial.setLocalScale(0.02f);

        }

        if (type.equals(Player.POWER_GAS)) {
            gasTimer.start();
            gasExpandTimer.start();
            active = true;
            spatial.setLocalScale(1.1f);

            if (gasTime != -1f) {
                gasTimer.setCounterTo(gasTime);
            }

        }

    }

    public void setExplosion(boolean explosion) {
        this.explosion = explosion;
    }

    public void setGasTotalTime(float gasTime) {
        this.gasTime = gasTime;
    }

    public void setBombTickTime(float time) {
        this.bombTickTimer.setMaxTime(time);
    }

    public void setBombActiveTime(float time) {
        this.bombActiveTimer.setMaxTime(time);
    }

    public void setFromTile(Tile fromTile) {
        this.fromTile = fromTile;
    }

    @Override
    protected void controlUpdate(float tpf) {

        //Check if game has started
        if (game.isStarted() && !game.isPaused()) {

            if (!started) {
                init();
                started = true;
            }

            /**
             * A simple timer that will control how long a stop power stays
             * inactive.
             */
            inactiveTimer.update(tpf);
            if (inactiveTimer.finished()) {
                active = true;
                inactiveTimer.stop();
            }

            if (type.equals(Player.POWER_BOMB)) {
                //When bomb tick time is complete we have to activate it
                bombTickTimer.update(tpf);
                if (bombTickTimer.finished()) {
                    if (!explosion) {
                        bombCountDownTimer.stop();
                        game.getBaseApplication().getSoundManager().playSound("bomb");
                    }
                    active = true;
                    game.getBaseApplication().getEffectManager().doEffect("bomb", spatial.getWorldTranslation().add(0, 1, 0), bombActiveTimer.getMaxTime());
                    bombTickTimer.stop();
                    bombActiveTimer.start();
                    spatial.setLocalScale(0);

                    //get all tiles forward or sideways. it will not return the from tile
                    ArrayList<Tile> tiles = game.getAllAdjacentTile(targetTile, fromTile);
                    if (tiles != null && tiles.size() >= 1) {

                        float activeTime = bombActiveTimer.getMaxTime() - 50;
                        if (activeTime < 50) {
                            activeTime = 50;
                        }

                        //Reproduce new spread
                        if ((fromTile == null) || (fromTile != null && tiles.size() == 1)) {
                            for (int i = 0; i < tiles.size(); i++) {
                                Tile tile = tiles.get(i);
                                boolean addTile = true;

                                //Make sure the bomb power doesn't go around corners
                                if (fromTile != null && (fromTile.getxPos() != tile.getxPos() && fromTile.getzPos() != tile.getzPos())) {
                                    addTile = false;
                                }

                                if (addTile) {
                                    Spatial sp = game.getBaseApplication().getModelManager().getModel("Models/powers/bomb.j3o");
                                    sp.setLocalTranslation(new Vector3f(tile.getxPos() * Game.TILE_SIZE, 0, tile.getzPos() * Game.TILE_SIZE));
                                    sp.setUserData("power", type);
                                    game.addObstacle(sp);

                                    PowerControl pc = new PowerControl(game, type, tile);
                                    pc.setExplosion(true);
                                    pc.setFromTile(targetTile);
                                    pc.setBombTickTime(20);
                                    sp.addControl(pc);

                                }


//                                PowerControl pc = game.loadPower(type, tile);
//                                pc.setFromTile(targetTile);
//                                pc.setBombTickTime(10);
////                                pc.setBombActiveTime(activeTime);

                            }
                        }

                    }

                }

                //Now we tick down the active timer
                if (active) {
                    //If the bombActiveTimer timer is complete we have to dispose it.
                    bombActiveTimer.update(tpf);
                    if (bombActiveTimer.finished()) {
                        bombActiveTimer.stop();
                        active = false;
                        doDispose();

                    }
                }

                if (!explosion) {
                    bombCountDownTimer.update(tpf);
                    if (bombCountDownTimer.finished()) {
                        Geometry g = (Geometry) ((Node) spatial).getChild("barrel1");
                        redColor = !redColor;
                        if (redColor) {
                            game.getBaseApplication().getSoundManager().playSound("timer");
                            g.getMaterial().setColor("Color", ColorRGBA.Red);
                        } else {
                            g.getMaterial().setColor("Color", ColorRGBA.LightGray);
                        }


                        bombCountDownTimer.setMaxTime(bombCountDownTimer.getMaxTime() - timerCountDecelerate);
                        timerCountDecelerate -= 0.2f;
                        if (bombCountDownTimer.getMaxTime() <= 0f) {
                            bombCountDownTimer.stop();
                        } else {
                            bombCountDownTimer.reset();
                        }
                    }
                }

            }

            if (type.equals(Player.POWER_CURSE)) {
                sterilizeTimer.update(tpf);

                if (active) {
                    if (spatial.getLocalScale().x < 8) {
                        spatial.setLocalScale(spatial.getLocalScale().x + (tpf * 5f));
                    }
                }

                if (sterilizeTimer.finished()) {
                    active = false;
                    doDispose();
                }
            }

            if (type.equals(Player.POWER_GAS)) {
                //When total gas time is complete we have to dispose of it
                gasTimer.update(tpf);
                if (gasTimer.finished()) {
                    active = false;
                    doDispose();
                }

                //If the gasExpansion timer is complete we have to expand the gas to the next tiles.
                gasExpandTimer.update(tpf);
                if (gasExpandTimer.finished()) {
                    gasExpandTimer.stop();

                    //get all tiles forward or sideways. it will not return the from tile
                    ArrayList<Tile> tiles = game.getAllAdjacentTile(targetTile, fromTile);
                    if (tiles != null && tiles.size() > 0) {

                        for (int i = 0; i < tiles.size(); i++) {
                            Tile tile = tiles.get(i);
                            if (FastMath.nextRandomInt(0, 2) > 0) {
                                PowerControl pc = game.loadPower(type, tile);
//                                pc.setExplosion(true);
                                pc.setFromTile(targetTile);
                                pc.setGasTotalTime(gasTimer.getCounter());
                            }

                        }

                    }

                }
            }


        }

    }

//    /**
//     * Will make the enemy turn just there.
//     */
//    public void move() {
//        int size = game.getAllAdjacentTile(targetTile, fromTile).size();
//
//        if (size == 1) {
//            nextTile = game.getNextAdjacentTile(targetTile, fromTile);
//            fromTile = targetTile;
//            targetTile = nextTile;
//            spatial.setLocalTranslation(new Vector3f(targetTile.getxPos() * TILE_SIZE, 0, targetTile.getzPos() * TILE_SIZE));
//        } else {
//            doDispose();
//        }
//
//
//    }
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    /**
     * This method will do something with the connected enemy
     *
     * @param enemyControl
     */
    public void doEffect(EnemyControl enemyControl) {

        if (type.equals(Player.POWER_POIZON)) {
            enemyControl.doDie();
            game.addScore(10);
            game.getBaseApplication().getSoundManager().stopMusic("bubble");
            doDispose();

        } else if (type.equals(Player.POWER_FEMALE)) {
//            if (!enemyControl.isChild()) {
            game.getBaseApplication().getSoundManager().playSound("switch");
            enemyControl.changeToFemale();
            doDispose();
//            }


        } else if (type.equals(Player.POWER_MALE)) {
//            if (!enemyControl.isChild()) {
            game.getBaseApplication().getSoundManager().playSound("switch");
            enemyControl.changeToMale();
            doDispose();
//            }

        } else if (type.equals(Player.POWER_STOP)) {
            if (active) {
                game.getBaseApplication().getSoundManager().playSound("block");
                enemyControl.turnBack();
                inactiveTimer.reset();
                health--;
                active = false;

                if (health == 0) {
                    doDispose();
                }
            }

        } else if (type.equals(Player.POWER_BOMB)) {
            if (active) {
                enemyControl.doDie();
                game.addScore(10);
            }

        } else if (type.equals(Player.POWER_CURSE)) {
            if (active) {
                if (!enemyControl.isCursed()) {
                    game.getBaseApplication().getSoundManager().playSound("curse");
                }

                enemyControl.setCursed(true);
            }

        } else if (type.equals(Player.POWER_GAS)) {
            if (active) {
                enemyControl.setUnderGasTile(true);

            }

        }

    }

    public void doDispose() {
        spatial.removeFromParent();
    }
}
