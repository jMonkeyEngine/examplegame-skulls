package com.jme3.skulls.screens;

import static com.bruynhuis.galago.games.tilemap.TileMapGame.BLANK;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.skulls.MainApplication;
import com.jme3.skulls.game.Game;
import com.jme3.skulls.ui.LargeButton;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.skulls.game.Tile;

/**
 * The edit screen is where the developer or level designer will setup levels for the game.
 * These levels are all saved under his user name directory on his file system and when a level is finished
 * these levels must be added to the game level setup.
 *
 * @author nidebruyn
 */
public class EditScreen extends AbstractScreen implements PickListener {

    private Game game;
    private float cameraHeight = 30f;
    private float anglePerZ = 0.006f;
    private TouchPickListener touchPickListener;
    private Vector3f vector = new Vector3f(0, 0, 5);
    private Vector3f focus = new Vector3f();
    private float dragSpeed = 30f;
    private GridPanel tilesPanel;
    private TouchButtonAdapter tilesListener;
    private Tile selectedTile;
    private LargeButton saveButton;
    private LargeButton clearButton;
    private LargeButton testButton;
    private LargeButton statsButton;
    private LargeButton exitButton;
    private LargeButton defaultButton;
    private Vector2f downPosition;
    private Node cameraJointNode;
    private CameraNode cameraNode;
    private Spatial selectedSpatial;
    private String selectedTileType = Game.BLANK;
    private Spatial marker;
    private int angle = 0;

    @Override
    protected void init() {

        tilesPanel = new GridPanel(hudPanel, "Interface/vpanl.png", 70, window.getHeight());
        tilesPanel.leftTop(0, 0);
        hudPanel.add(tilesPanel);
        tilesListener = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    selectedTileType = uid;
                    updateSelection();
                }
            }
        };

        createTileButton(Game.BLANK, "Interface/poweroff.png", tilesPanel, tilesListener, 0);
        createTileButton(Game.FLOOR, "Interface/floor.png", tilesPanel, tilesListener, 0);
        createTileButton(Game.WALL_TWOWAY, "Interface/twoway.png", tilesPanel, tilesListener, 0);
        createTileButton(Game.WALL_END, "Interface/end.png", tilesPanel, tilesListener, 0);
        createTileButton(Game.WALL_CORNER, "Interface/corner.png", tilesPanel, tilesListener, 0);
        createTileButton(Game.WALL_THREEWAY, "Interface/threeway.png", tilesPanel, tilesListener, 0);
        createTileButton(Game.WALL_FOURWAY, "Interface/fourway.png", tilesPanel, tilesListener, 0);
        createTileButton(Game.ENEMY, "Textures/tile-enemy.png", tilesPanel, tilesListener, 0);

        tilesPanel.layout(10, 1);

        saveButton = new LargeButton(hudPanel, "edit_save_button", "Save");
        saveButton.rightTop(5, 5);
        saveButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    game.save();
                }
            }
        });

        clearButton = new LargeButton(hudPanel, "edit_clear_button", "Clear");
        clearButton.rightTop(265, 5);
        clearButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    game.clear();
                    loadCameraSettings();
                }
            }
        });

        testButton = new LargeButton(hudPanel, "edit_test_button", "Test");
        testButton.rightTop(524, 5);
        testButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    game.save();
                    ((MainApplication) baseApplication).getPlayScreen().setTest(true);
                    showScreen("play");
                }
            }
        });

        statsButton = new LargeButton(hudPanel, "edit_stats_button", "Stats");
        statsButton.rightTop(784, 5);
        statsButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    baseApplication.showStats();
                }
            }
        });

        defaultButton = new LargeButton(hudPanel, "edit_default_button", "Default");
        defaultButton.rightBottom(5, 5);
        defaultButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    game.clearToDefault();
                    loadCameraSettings();
                }
            }
        });

        exitButton = new LargeButton(hudPanel, "edit_exit_button", "Exit");
        exitButton.rightBottom(265, 5);
        exitButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    showPreviousScreen();
                }
            }
        });
    }

    protected void createTileButton(String id, String image, Panel parent, TouchButtonListener buttonListener, float angle) {
        TouchButton button = new TouchButton(parent, id, image, 60, 60);
        button.rotate(FastMath.DEG_TO_RAD * angle);
        button.addEffect(new TouchEffect(button));
        button.addTouchButtonListener(buttonListener);

    }

    protected void rotateTileRight() {
        angle += 90;
        if (angle == 0) {
            angle = 360;
        }

        updateSelection();
    }

    protected void rotateTileLeft() {
        angle -= 90;
        if (angle == 0) {
            angle = 360;
        }

        updateSelection();

    }

    protected void initMarker() {
        Material mat = baseApplication.getAssetManager().loadMaterial("Common/Materials/RedColor.j3m");
        WireBox box = new WireBox(Game.TILE_SIZE * 0.5f, 0.1f, Game.TILE_SIZE * 0.5f);
        box.setLineWidth(3);
        Geometry g = new Geometry(BLANK, box);
        g.setMaterial(mat);
        marker = g;
        rootNode.attachChild(marker);
    }

    /**
     * Method for refreshing the selection index.
     */
    protected void updateSelection() {
        if (selectedSpatial != null) {
            selectedSpatial.removeFromParent();
        }

        if (selectedTileType != null) {
            selectedSpatial = game.getTileModel(selectedTileType);
        }

        if (selectedTile != null) {
            if (selectedSpatial != null) {
                selectedSpatial.setLocalTranslation(selectedTile.getxPos() * Game.TILE_SIZE, 0.001f, selectedTile.getzPos() * Game.TILE_SIZE);
                selectedSpatial.setLocalRotation(selectedSpatial.getLocalRotation().fromAngleAxis(angle * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));

                rootNode.attachChild(selectedSpatial);
            }

            marker.setLocalTranslation(selectedTile.getxPos() * Game.TILE_SIZE, 0, selectedTile.getzPos() * Game.TILE_SIZE);
        }


    }

    @Override
    protected void load() {
        /*
         * The loadStart method is called very first when ever the user calls to go to this screen.
         * A black panel will by default be shown over the screen
         * One must normally load the level and player and inputs and camera stuff here.
         */
        game = new Game(baseApplication, rootNode);
        game.edit("skulls.properties");
        game.load();

        //Load the camera
        loadCameraSettings();

        selectedTile = null;
        selectedTileType = Game.BLANK;
        initMarker();
        updateSelection();

        //Load the inputs
        //Init the picker listener
        touchPickListener = new TouchPickListener(baseApplication.getCamera(), rootNode);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);


    }

    protected void loadCameraSettings() {        
        Vector3f centerPoint = new Vector3f((Game.MAP_SIZE * Game.TILE_SIZE) * 0.5f, 0, (Game.MAP_SIZE * Game.TILE_SIZE) * 0.5f);

        cameraJointNode = new Node("camerajoint");
        cameraJointNode.setLocalTranslation(centerPoint);
        rootNode.attachChild(cameraJointNode);

        cameraNode = new CameraNode("camnode", camera);
        cameraNode.setLocalTranslation(0, cameraHeight, cameraHeight * anglePerZ);
        cameraNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cameraJointNode.attachChild(cameraNode);
    }

    @Override
    protected void show() {
        setPreviousScreen("menu");
        /*
         * loadDone is called when ever the screen has finished loading the start stuff
         * and the window is visible
         */
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
        marker.removeFromParent();
        log("close game");

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
        if (isActive() && game != null) {

            if (!pickEvent.isKeyDown() && pickEvent.isLeftButton() && selectedSpatial != null
                    && pickEvent.getCursorPosition().x > (80 * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().x < window.getWidth() - (80 * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().y < window.getHeight() - (80 * window.getScaleFactorHeight())
                    && downPosition != null
                    && downPosition.distance(pickEvent.getCursorPosition()) < 5f) {

                Tile clickedTile = game.getTileFromContactPoint(pickEvent.getContactPoint().x, pickEvent.getContactPoint().z);

                if (clickedTile != null) {
                    log("----------------------------------------------------");
                    log("Clicked: " + pickEvent.getContactObject().getName());
                    log("Position: " + pickEvent.getContactObject().getWorldTranslation());
                    log("Tile: " + clickedTile.toString());
                    clickedTile.setName(selectedSpatial.getName());
                    clickedTile.setAngle(angle);

                    game.updateTile(clickedTile.getxPos(), clickedTile.getzPos(), clickedTile.getAngle(), clickedTile.getName());
                }

                downPosition = null;

            }

            //Rotate
            if (pickEvent.isKeyDown() && pickEvent.isRightButton()) {
                rotateTileRight();
            }

            //Store the old pick value
            if (pickEvent.isKeyDown()) {
                downPosition = pickEvent.getCursorPosition().clone();
            }
        }
    }

    @Override
    public void update(float tpf) {
        /**
         * We override the update loop so that we can move the camera position.
         */
        if (isActive() && game.isStarted() && !game.isPaused()) {
            
        }
    }

    public void drag(PickEvent pickEvent, float tpf) {
        if (isActive()) {


            if (pickEvent.isKeyDown()) {
                if (pickEvent.isRight()) {
                    cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(0).mult(-pickEvent.getAnalogValue() * dragSpeed));

                } else if (pickEvent.isUp()) {
                    cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(2).mult(pickEvent.getAnalogValue() * dragSpeed));

                } else if (pickEvent.isLeft()) {
                    cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(0).mult(pickEvent.getAnalogValue() * dragSpeed));

                } else if (pickEvent.isDown()) {
                    cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(2).mult(-pickEvent.getAnalogValue() * dragSpeed));

                }

                cameraNode.lookAt(cameraJointNode.getWorldTranslation(), Vector3f.UNIT_Y);

            } else {
                if (selectedSpatial != null) {
                    if (pickEvent.getContactPoint() != null) {
                        selectedTile = game.getTileFromContactPoint(pickEvent.getContactPoint().x, pickEvent.getContactPoint().z);
                        updateSelection();

                    }

                }

            }

            if (pickEvent.isZoomUp()) {
                cameraHeight += 1f;
                cameraNode.setLocalTranslation(0, cameraHeight, cameraHeight * anglePerZ);
//                rotateTileLeft();

            } else if (pickEvent.isZoomDown()) {
                cameraHeight -= 1f;
                cameraNode.setLocalTranslation(0, cameraHeight, cameraHeight * anglePerZ);
//                rotateTileRight();
            }

        }
    }
    
    /**
     * Make the camera pan to a direction
     * @param left
     * @param up 
     */
    protected void doPanCamera(float left, float up) {
        camera.getLeft().mult(left * dragSpeed, vector);
        vector.scaleAdd(up * dragSpeed, camera.getUp(), vector);
        vector.multLocal(camera.getLocation().distance(focus));
        camera.setLocation(camera.getLocation().add(vector));
        focus.addLocal(vector);
    }

    @Override
    protected void pause() {
    }
}
