package com.jme3.skulls.game.powers;

import com.jme3.skulls.game.enemies.EnemyControl;
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
import com.jme3.skulls.MainApplication;
import com.jme3.skulls.game.Game;
import com.jme3.skulls.game.Player;
import com.jme3.skulls.game.Tile;
import com.jme3.skulls.game.enemies.EnemyXControl;
import com.jme3.skulls.game.enemies.EnemyYControl;
import java.util.ArrayList;

/**
 * The PowerControl will control all power behaviours that are used.
 * This control is attached to a power spatial.
 *
 *
 * @author nidebruyn
 */
public class PowerControl extends AbstractControl {

    protected Game game;
    protected String type;
    protected boolean active = false;
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

        if (type.equals(Player.POWER_POISON)) {
            active = true;
            game.getBaseApplication().getSoundManager().playSound("bubble");
        }

        if (type.equals(Player.POWER_BOMB)) {
            bombTickTimer.start();            
            spatial.setLocalScale(1f);

            if (!explosion) {
                bombCountDownTimer.start();
                active = false;
            } else {
                active = true;
            }

        }

        if (type.equals(Player.POWER_CURSE)) {
            sterilizeTimer.start();
            active = true;
            spatial.setLocalScale(0.02f);

        }

        if (type.equals(Player.POWER_GAS)) {
            game.getBaseApplication().getSoundManager().playSound("acid");
            gasTimer.start();
            gasExpandTimer.start();
            active = true;
            spatial.setLocalScale(1.1f);

            if (gasTime != -1f) {
                gasTimer.setCounterTo(gasTime);
            }

        }
        
        if (type.equals(Player.POWER_SWITCH_TO_Y)) {
            active = true;
        }
        
        if (type.equals(Player.POWER_SWITCH_TO_X)) {
            active = true;
        }
        
        if (type.equals(Player.POWER_MUTANT)) {
            active = true;
        }
        
        if (type.equals(Player.POWER_STOP)) {
            active = true;
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
                    
                    //Only if it is the first explosion
                    if (!explosion) {
                        spatial.setLocalScale(0);
                        bombCountDownTimer.stop();
                        game.getBaseApplication().getSoundManager().playSound("bomb");
                        ((MainApplication)game.getBaseApplication()).getPlayScreen().shakeCamera(0.08f, 60);
                    }
                    
                    active = true;
                    game.getBaseApplication().getEffectManager().doEffect("bomb", spatial.getWorldTranslation().add(0, 1, 0), 80);
                    bombTickTimer.stop();
                    bombActiveTimer.start();
                    

                    //get all tiles forward or sideways. it will not return the from tile
                    ArrayList<Tile> tiles = game.getAllAdjacentTile(targetTile, fromTile);
                    if (tiles != null && tiles.size() >= 1) {

                        //Reproduce new spread
                        if ((fromTile == null) || (fromTile != null)) {
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
                                    pc.setBombTickTime(8);
                                    sp.addControl(pc);

                                }

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

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    /**
     * This method will do something with the connected enemy
     *
     * @param enemyControl
     */
    public void doEffect(EnemyControl enemyControl) {

        if (type.equals(Player.POWER_POISON)) {
            enemyControl.doDie();
            game.addScore(10);            
            doDispose();

        } else if (type.equals(Player.POWER_SWITCH_TO_Y)) {
            game.getBaseApplication().getSoundManager().playSound("switch");
            enemyControl.changeToTypeY();
            doDispose();

        } else if (type.equals(Player.POWER_SWITCH_TO_X)) {
            game.getBaseApplication().getSoundManager().playSound("switch");
            enemyControl.changeToTypeX();
            doDispose();

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
            if (active && (enemyControl instanceof EnemyYControl)) {
                EnemyYControl enemyYControl = (EnemyYControl) enemyControl;
                if (!enemyYControl.isCursed()) {
                    game.getBaseApplication().getSoundManager().playSound("curse");
                }

                enemyYControl.setCursed(true);
                
            } else if (active && (enemyControl instanceof EnemyXControl)) {
                EnemyXControl enemyXControl = (EnemyXControl) enemyControl;
                if (!enemyXControl.isCursed()) {
                    game.getBaseApplication().getSoundManager().playSound("curse");
                }

                enemyXControl.setCursed(true);
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
