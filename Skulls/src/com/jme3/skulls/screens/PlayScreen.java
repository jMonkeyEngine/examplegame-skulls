/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.screens;

import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.games.simplecollision.SimpleCollisionGameListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.skulls.game.EnemyControl;
import com.jme3.skulls.game.Game;
import com.jme3.skulls.game.Player;
import com.jme3.skulls.game.PowerControl;
import com.jme3.skulls.game.Tile;
import com.jme3.skulls.ui.GameOverDialog;
import com.jme3.skulls.ui.PauseDialog;
import com.jme3.skulls.ui.ScoreDialog;
import com.jme3.skulls.ui.StartGameDialog;
import com.jme3.skulls.ui.WinDialog;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;

/**
 * This will be the play screen. This screen will show when a player decided to
 * play a game. This screen will not have a Heading It will have one button for
 * pause and a toolbar for powers and info. It will not have some background
 * image.
 *
 * This screen will have some Dialogs. (StartGameDialog, PauseDialog,
 * GameoverDialog, WinDialog)
 *
 * @author nidebruyn
 */
public class PlayScreen extends AbstractScreen implements SimpleCollisionGameListener, PickListener {

    private StartGameDialog startGameDialog;
    private GameOverDialog gameOverDialog;
    private WinDialog winDialog;
    private PauseDialog pauseDialog;
    private ScoreDialog scoreDialog;
    private Game game;
    private Player player;
    private float cameraHeight = 20f;
    private TouchPickListener touchPickListener;
    private Vector3f vector = new Vector3f(0, 0, 5);
    private Vector3f focus = new Vector3f();
    private float dragSpeed = 30f;
    private boolean test = false;
    private Node cameraJointNode;
    private CameraNode cameraNode;
    private Timer secondsTimer = new Timer(100);
    private Geometry marker;

    @Override
    protected void init() {

        scoreDialog = new ScoreDialog(window);
        scoreDialog.center();
        scoreDialog.addPowerButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    log(uid);
                    player.setSelectedPower(uid);
                }
            }
        });

        /*
         * Here we initialize the startgamedialog and add all button listeners.
         */
        startGameDialog = new StartGameDialog(window);
        startGameDialog.addMenuButtonTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doExitGameAction();

                }
            }
        });

        startGameDialog.addPlayButtonTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && !game.isStarted()) {
                    doPlayGameAction();

                }
            }
        });


        /*
         * Here we initialize the pausedialog and add all button listeners.
         * This one must go over all other dialogs when the player pause or the phone
         * went into idle mode.
         */
        pauseDialog = new PauseDialog(window);
        pauseDialog.addMenuButtonTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doExitGameAction();
                }
            }
        });

        pauseDialog.addResumeButtonTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doResumeGameAction();
                }
            }
        });

        pauseDialog.addRetryButtonTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doRestartGameAction();
                }
            }
        });


        /*
         * Here we initialize the gameoverdialog and add all button listeners.
         */
        gameOverDialog = new GameOverDialog(window);
        gameOverDialog.addMenuButtonTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doExitGameAction();
                }
            }
        });

        gameOverDialog.addRetryButtonTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doRestartGameAction();
                }
            }
        });


        /*
         * Here we initialize the win dialog and add all button listeners.
         */
        winDialog = new WinDialog(window);
        winDialog.addMenuButtonTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doExitGameAction();
                }
            }
        });

        winDialog.addNextButtonTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doExitGameAction();
                }
            }
        });

        winDialog.addRetryButtonTouchListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    doRestartGameAction();
                }
            }
        });
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);

        /*
         * The loadStart method is called very first when ever the user calls to go to this screen.
         * A black panel will by default be shown over the screen
         * One must normally load the level and player and inputs and camera stuff here.
         */
        game = new Game(baseApplication, rootNode);
        if (test) {
            game.test("skulls.properties");
        } else {
            game.play("level1.properties");
        }

        game.load();

        player = new Player(game);
        player.load();

        game.addGameListener(this);

        //Load the camera
        loadCameraSettings();

        //Load the inputs
        //Init the picker listener
        touchPickListener = new TouchPickListener(baseApplication.getCamera(), rootNode);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);

        //Hide dialogs
        scoreDialog.setScore(0);
        scoreDialog.setEnemyCount(0);
        scoreDialog.hide();

        //create a market geometry
        WireBox wb = new WireBox(Game.TILE_SIZE*0.5f, 0.02f, Game.TILE_SIZE*0.5f);
        wb.setLineWidth(2);
        marker = new Geometry("MARKER", wb);
        marker.setMaterial(baseApplication.getAssetManager().loadMaterial("Common/Materials/RedColor.j3m"));
        rootNode.attachChild(marker);

    }

    protected void loadCameraSettings() {
        float anglePerX = 0.5f;
        float angleY = FastMath.DEG_TO_RAD * 0f;
        float anglePerZ = 0.7f;
        Vector3f centerPoint = new Vector3f((Game.MAP_SIZE * Game.TILE_SIZE) * 0.5f, 0, (Game.MAP_SIZE * Game.TILE_SIZE) * 0.5f);

        cameraJointNode = new Node("camerajoint");
        cameraJointNode.setLocalTranslation(centerPoint);
        cameraJointNode.rotate(0, angleY, 0);
        rootNode.attachChild(cameraJointNode);

        cameraNode = new CameraNode("camnode", camera);
        cameraNode.setLocalTranslation(0, cameraHeight, cameraHeight * anglePerZ);
        cameraNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cameraJointNode.attachChild(cameraNode);
    }

    @Override
    protected void show() {
        setPreviousScreen(null);
        /*
         * loadDone is called when ever the screen has finished loading the start stuff
         * and the window is visible
         */

        startGameDialog.show();


    }

    @Override
    protected void exit() {
        /*
         * This exitDone method is called when the user leaves the current screen.
         * I a screen such as the play screen one will normally close the level or remove all
         * spatials and controls and lights, etc from the rootNode
         */
        touchPickListener.unregisterInput();
        cameraJointNode.removeFromParent();
        game.close();
        test = false;
        baseApplication.getViewPort().setBackgroundColor(BaseApplication.BACKGROUND_COLOR);

    }

    /**
     * Called when the player wants to start playing the game
     */
    protected void doPlayGameAction() {
        if (isActive()) {
            baseApplication.getSoundManager().stopMusic("menu");
            baseApplication.getSoundManager().playSound("button");
            startGameDialog.hide();
            scoreDialog.resetPowers();
            scoreDialog.show();
            game.start(player);
            secondsTimer.start();
            scoreDialog.setScore(game.getScore());
            baseApplication.getSoundManager().playMusic("level");

        }
    }

    /**
     * Called when the must be resumed
     */
    protected void doResumeGameAction() {
        if (isActive()) {
            baseApplication.getSoundManager().playSound("button");
            game.resume();
            pauseDialog.hide();
            scoreDialog.show();
            gameOverDialog.hide();

        }
    }

    /**
     * Called when the game must be restarted
     */
    protected void doRestartGameAction() {
        if (isActive()) {
            gameOverDialog.hide();
            baseApplication.getSoundManager().playSound("button");
            showScreen("play");

        }
    }

    /**
     * Do the exit of the game
     */
    protected void doExitGameAction() {
        if (isActive()) {
            gameOverDialog.hide();
            baseApplication.getSoundManager().stopMusic("level");
            baseApplication.getSoundManager().playSound("button");
            showScreen("menu");

        }
    }

    /**
     * Will be called from an external source or pause button
     */
    public void doPauseGame() {
        if (isActive() && isEnabled() && isInitialized() && game != null && !game.isPaused() && game.isStarted()) {
            game.pause();
            scoreDialog.hide();
            pauseDialog.show();

        }
    }

    @Override
    public void doEscape(boolean touchEvent) {
        if (isActive() && isEnabled() && isInitialized() && !game.isPaused()) {
            if (isActive() && game.isStarted() && !game.isPaused()) {
                doPauseGame();

            } else if (isActive() && !game.isStarted() && startGameDialog.isVisible()) {
                doExitGameAction();

            } else if (isActive() && game.isStarted() && game.isPaused() && pauseDialog.isVisible()) {
                doResumeGameAction();

            } else if (isActive() && game.isPaused() && gameOverDialog.isVisible()) {
                doExitGameAction();
            }
        }


    }

    public void doGameOver() {
        game.pause();
        gameOverDialog.show();
    }

    public void doGameCompleted() {
        game.pause();
        winDialog.show(game.getScore(), 0);
    }

    public void doScoreChanged(int score) {
//        log("Game completed: " + score);
        scoreDialog.setEnemyCount(game.getEnemies().size());
        scoreDialog.setScore(score);
    }

    public void doCollisionPlayerWithStatic(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void doCollisionPlayerWithPickup(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void doCollisionPlayerWithEnemy(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void doCollisionEnemyWithEnemy(Spatial collided, Spatial collider) {
        if (collided.getControl(EnemyControl.class) != null) {
            EnemyControl ec1 = collided.getControl(EnemyControl.class);

            if (collider.getControl(EnemyControl.class) != null) {
                EnemyControl ec2 = collider.getControl(EnemyControl.class);

                //Check if it is a female with male collision
                if (ec1.isMale() && ec2.isFemale() && !ec2.isPregnant()) {
                    ec1.startMating();
                    ec2.startMating();

                } else if (ec2.isMale() && ec1.isFemale() && !ec1.isPregnant()) {
                    ec1.startMating();
                    ec2.startMating();

                } else if (ec1.isMutated() && !ec2.isMutated()) {
                    ec2.doDie();
                    ec1.doMakeKill();
                    game.addScore(5);

                } else if (ec2.isMutated() && !ec1.isMutated()) {
                    ec1.doDie();
                    ec2.doMakeKill();
                    game.addScore(5);

                }

            }
        }
    }

    public void doCollisionPlayerWithObstacle(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void doCollisionEnemyWithStatic(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        log(collided.getName() + " collided with " + collider.getName());
    }

    public void doCollisionEnemyWithObstacle(Spatial collided, Spatial collider) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        log(collided.getName() + " collided with " + collider.getName());

        //Here we must notify the enemy to change direction
        if (collided.getControl(EnemyControl.class) != null) {
            EnemyControl ec = collided.getControl(EnemyControl.class);

            if (collider.getControl(PowerControl.class) != null) {
                PowerControl pc = collider.getControl(PowerControl.class);
                //Here we must figure out what to do with the power

                pc.doEffect(ec);

            }
        }

    }

    /**
     * This method will be called when the player has touched the 3d spatials.
     *
     * @param contactPoint
     * @param contactObject
     * @param keyDown
     * @param cursorPosition
     */
    public void picked(PickEvent pickEvent, float tpf) {
        if (isActive() && game.isStarted() && !game.isPaused()) {

            if (!pickEvent.isKeyDown() && player.getSelectedPower() != null
                    && pickEvent.getContactPoint() != null
                    && pickEvent.getCursorPosition().x < window.getWidth() - (80 * window.getScaleFactorWidth())) {

                Tile selectedTile = game.getClosestFloorTile(pickEvent.getContactPoint().x, pickEvent.getContactPoint().z);

                //Only if a valid tile was selected
                if (selectedTile != null) {
                    game.loadPower(player.getSelectedPower(), selectedTile);
                    scoreDialog.usePower(player.getSelectedPower());
                    player.setSelectedPower(null);
                }

            }

        }
    }

    @Override
    public void update(float tpf) {
        /**
         * We override the update loop so that we can move the camera position.
         */
        if (isActive() && game.isStarted() && !game.isPaused()) {

//            //When the mouse cursor is over the tools dialog section show it
//            if (inputManager.getCursorPosition().x > window.getWidth() - (100 * window.getScaleFactorWidth())) {
//                scoreDialog.show();
//                
//            }

            scoreDialog.setEnemyCount(game.getEnemies().size());

            secondsTimer.update(tpf);
            if (secondsTimer.finished()) {
                game.removeScore(1);
                secondsTimer.reset();
            }

        }
    }

    public void drag(PickEvent pickEvent, float tpf) {
        if (isActive() && game.isStarted() && !game.isPaused() && player.getSelectedPower() == null && pickEvent.isKeyDown()) {

            if (pickEvent.isRight()) {
//                doPanCamera(pickEvent.getAnalogValue(), 0);
//                cameraJointNode.rotate(0, -pickEvent.getAnalogValue()*dragSpeed, 0);
                cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(0).mult(-pickEvent.getAnalogValue() * dragSpeed));

            } else if (pickEvent.isUp()) {
//                doPanCamera(0, -pickEvent.getAnalogValue());
                cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(2).mult(pickEvent.getAnalogValue() * dragSpeed));

            } else if (pickEvent.isLeft()) {
//                doPanCamera(-pickEvent.getAnalogValue(), 0);
//                cameraJointNode.rotate(0, pickEvent.getAnalogValue()*dragSpeed, 0);
                cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(0).mult(pickEvent.getAnalogValue() * dragSpeed));

            } else if (pickEvent.isDown()) {
//                doPanCamera(0, pickEvent.getAnalogValue());
                cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(2).mult(-pickEvent.getAnalogValue() * dragSpeed));

            }

            cameraNode.lookAt(cameraJointNode.getWorldTranslation(), Vector3f.UNIT_Y);

        }

        if (isActive() && game.isStarted() && !game.isPaused() && pickEvent.getContactPoint() != null) {
            log("Mouse at: " + pickEvent.getContactPoint());
            
            Tile selectedTile = game.getTileFromContactPoint(pickEvent.getContactPoint().x, pickEvent.getContactPoint().z);

            //Only if a valid tile was selected
            if (selectedTile != null) {
                marker.setCullHint(Spatial.CullHint.Never);
                marker.setLocalTranslation(new Vector3f(selectedTile.getxPos() * Game.TILE_SIZE, 0.1f, selectedTile.getzPos() * Game.TILE_SIZE));
                
                if (selectedTile.getName().equals(Game.FLOOR)) {
                    marker.getMaterial().setColor("Color", ColorRGBA.Green);
                } else {
                    marker.getMaterial().setColor("Color", ColorRGBA.Red);
                }
                
            } else {
                marker.setCullHint(Spatial.CullHint.Always);
            }
        }
    }
//    protected void doPanCamera(float left, float up) {
//        camera.getLeft().mult(left * dragSpeed, vector);
//        vector.scaleAdd(up * dragSpeed, camera.getUp(), vector);
//        vector.multLocal(camera.getLocation().distance(focus));
//        camera.setLocation(camera.getLocation().add(vector));
//        focus.addLocal(vector);
//    }

    @Override
    protected void pause() {
        doPauseGame();
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}