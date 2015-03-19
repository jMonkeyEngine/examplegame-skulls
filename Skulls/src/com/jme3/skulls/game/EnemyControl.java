/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.game;

import com.bruynhuis.galago.control.AnimationControl;
import com.bruynhuis.galago.listener.AnimationListener;
import com.bruynhuis.galago.util.Timer;
import static com.jme3.skulls.game.Game.TILE_SIZE;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * The EnemyControl will control all movement and behavior of the enemies
 * running around.
 *
 *
 * @author nidebruyn
 */
public class EnemyControl extends AbstractControl implements AnimationListener {

    public static final int TYPE_YOUNG = 1;
    public static final int TYPE_MALE = 2;
    public static final int TYPE_FEMALE = 3;
    public static final int TYPE_MUTATED = 4;
    protected Game game;
    protected int type;
    protected float moveSpeed;
    protected float turnSpeed;
    protected float growthTime;
    protected int kills;
    protected boolean pregnant = false;
    protected boolean mating = false;
    protected boolean cursed = false;
    private Timer cursedTimer = new Timer(200);
    private boolean move = false;
    private boolean alive = false;
    private boolean turning = false;
    private Timer growTimer;
    private Timer pregnantTimer = new Timer(200);
    private Timer matingTimer = new Timer(300);
    private Timer gasKillTimer;
    private Timer gasClearTimer = new Timer(50);
    private boolean underGasTile = false;
    private int spawnChildCount = 1;
    private Vector3f targetPosition;
    private Tile nextTile;
    private Tile targetTile;
    private Tile fromTile;
    private AnimationControl animationControl;
    private float animationSpeed = 1f;

    public EnemyControl(Game game, int type, Tile tile) {
        this.game = game;
        this.type = type;
        this.targetTile = tile;

    }

    private void init() {
        if (type == TYPE_YOUNG) {
            spatial.setLocalScale(0.8f);
            animationSpeed = 2.4f;
            moveSpeed = 3.3f;
            turnSpeed = 1f;
            growthTime = (float) FastMath.nextRandomInt(800, 1000);
            growTimer = new Timer(growthTime);

        } else if (type == TYPE_MALE) {
            spatial.setLocalScale(1f);
            animationSpeed = 1.8f;
            moveSpeed = 2.5f;
            turnSpeed = 1f;
            growthTime = 0f;
            growTimer = null;

        } else if (type == TYPE_FEMALE) {
            spatial.setLocalScale(1f);
            animationSpeed = 1.8f;
            moveSpeed = 2.5f;
            turnSpeed = 1f;
            growthTime = 0f;
            growTimer = null;

        } else if (type == TYPE_MUTATED) {
            animationSpeed = 2.4f;
            moveSpeed = 3.3f;
            turnSpeed = 1f;
            growthTime = 0f;
            growTimer = null;            

        }

        targetPosition = new Vector3f(targetTile.getxPos() * TILE_SIZE, spatial.getWorldTranslation().y, targetTile.getzPos() * TILE_SIZE);

    }

    @Override
    protected void controlUpdate(float tpf) {

        //Check if game has started
        if (game.isStarted() && !game.isPaused()) {

            /*
             * Check to make this creature alive
             * This method will only excecute once.
             */
            if (!alive) {

                if (animationControl == null) {
                    animationControl = new AnimationControl();
                    Spatial animSpatial = ((Node) spatial).getChild("body");
                    animSpatial.addControl(animationControl);
                    animationControl.addAnimationListener(EnemyControl.this);

                }

                init();

                alive = true;
                move = true;

                turn();
                
                Spatial shadow = game.getBaseApplication().getModelManager().getModel("Models/enemies/shadow.j3o");
//                shadow.move(0, 0.01f, 0);
                ((Node)spatial).attachChild(shadow);

                //Start to count the grow timer
                if (growTimer != null && type == TYPE_YOUNG) {
                    growTimer.start();
                }
            }

            /*
             * Every movement and actions will depend on these conditions.
             */
            if (alive && move) {
                
                animationControl.play("walk", true, false, animationSpeed);

                //Check when crow time is over
                if (growTimer != null) {
                    growTimer.update(tpf);
                    if (growTimer.finished()) {
                        growTimer.stop();
                        changeToAdult();
                    }
                }

                /*
                 * Check if this guy or girl is mating, if so count down
                 */
                if (mating) {
                    matingTimer.update(tpf);

                    //When mating is done follow in to the next out of action stepsgg
                    if (matingTimer.finished()) {
                        if (isFemale()) {
                            pregnant = true;
                            mating = false;
                            spawnChildCount = FastMath.nextRandomInt(1, 5);
                            pregnantTimer.reset();

                        } else {
                            mating = false;
                            cursed = true; //Enemy needs to wait for 2 seconds before he can mate again
                            cursedTimer.reset();
                        }
                    }

                }


                //Handle the pregnant enemy here
                if (pregnant) {
                    pregnantTimer.update(tpf);
                    if (pregnantTimer.finished()) {
                        spawnChildCount--;
                        game.spawnYoung(targetTile);

                        if (spawnChildCount > 0) {
                            pregnantTimer.reset();
                        } else {
                            pregnant = false;
                            pregnantTimer.stop();
                        }

                    }
                }

                //Handle or check if the mate timer has started
                if (cursed) {
                    cursedTimer.update(tpf);
                    if (cursedTimer.finished()) {
                        cursed = false;
                        cursedTimer.stop();
                    }
                }

                /*
                 * Here we handle all kinds of movements
                 * We will check if it is time to turn to next tile or just move to next tile
                 */
                if (!mating && !turning) {
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

    /**
     * This method will be called when the young enemy must change into a male
     * or female
     */
    protected void changeToAdult() {
        int i = FastMath.nextRandomInt(0, 1);
        if (i == 0) {
            game.loadMale(targetTile);
        } else {
            game.loadFemale(targetTile);
        }

        game.removeEnemy(this);
        spatial.removeFromParent();
    }

    /**
     * This method will change the enemy to a male
     */
    public void changeToMale() {
        game.loadMale(targetTile);
        game.removeEnemy(this);
        spatial.removeFromParent();

    }

    /**
     * This method will change the enemy to a male
     */
    public void changeToFemale() {
        game.loadFemale(targetTile);
        game.removeEnemy(this);
        spatial.removeFromParent();

    }

    /**
     * This method must be called to change a female to be pregnant.
     */
    public void startMating() {
        //Return out if this enemy is already mating
        if (mating || cursed || pregnant) {
            return;
        }

        mating = true;
        matingTimer.reset();

        if (isFemale()) {
            game.getBaseApplication().getEffectManager().doEffect("love", spatial.getWorldTranslation().add(0, 1, 0), 500f);
            game.getBaseApplication().getSoundManager().playSound("mate");
        }


    }

    /**
     * For debugging
     *
     * @param text
     */
    protected void log(String text) {
        System.out.println(text);
    }

    public boolean isMale() {
        return type == TYPE_MALE;
    }

    public boolean isFemale() {
        return type == TYPE_FEMALE;
    }

    public boolean isChild() {
        return type == TYPE_YOUNG;
    }

    public boolean isMutated() {
        return type == TYPE_MUTATED;
    }

    public boolean isPregnant() {
        return pregnant;
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

    public void doAnimationDone(String animationName) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setUnderGasTile(boolean underGasTile) {
                
        if (gasKillTimer == null) {
            gasKillTimer = new Timer(200);
            gasKillTimer.start();
            gasClearTimer.start();
            
        }
        
        this.underGasTile = underGasTile;
    }    
    
}
