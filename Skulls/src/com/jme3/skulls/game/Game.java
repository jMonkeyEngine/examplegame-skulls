/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.game;

import com.jme3.skulls.game.powers.PowerControl;
import com.jme3.skulls.game.enemies.EnemyControl;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.games.platform.PlatformGame;
import com.bruynhuis.galago.games.simplecollision.SimpleCollisionGame;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.skulls.game.enemies.EnemyInfantControl;
import com.jme3.skulls.game.enemies.EnemyXControl;
import com.jme3.skulls.game.enemies.EnemyYControl;
import com.jme3.skulls.game.enemies.MutantControl;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * The game class will keep track of a given game being played. Here we load the
 * level models and any other models required such as
 * enemies/vegetation/statics/obstacles. We can do any jME scene stuff here.
 *
 * @author nidebruyn
 */
public class Game extends SimpleCollisionGame {

    public static final String propertiesExtension = ".properties";
    private Properties levelProperties;
    private String propertiesFile;
    private File file;
    public static final String BLANK = "blank";
    public static final String WALL_LIGHT = "walllight";
    public static final String WALL_END = "wallone";
    public static final String WALL_CORNER = "wallcorner";
    public static final String WALL_TWOWAY = "walltwo";
    public static final String WALL_THREEWAY = "wallthree";
    public static final String WALL_FOURWAY = "wallfour";
    public static final String FLOOR = "floor";
    public static final String ENEMY = "enemy";
    public static float TILE_SIZE = 2;
    public static int MAP_SIZE = 32;
    private Tile map[][] = new Tile[MAP_SIZE][MAP_SIZE];
    private boolean edit = false;
    private float enemyHeight = 0.25f;

    /*
     * This property will keep track of all enemies in the level.
     */
    private ArrayList<EnemyControl> enemies = new ArrayList<EnemyControl>();
    public static int MAX_ENEMIES = 40;
    /**
     * This will be uses to place the initial enemies on the map
     */
    private int initialTypeXCount = 5;
    private int initialTypeYCount = 5;
    private Node dungeonPack;
    private Material dungeonMaterial;
//    private Material enemiesMaterial;
    private FilterPostProcessor fpp;
    private int score = 600;

    public Game(BaseApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);

        initMap();

        dungeonPack = (Node) baseApplication.getModelManager().getModel("Models/static/dungeon_pixel.j3o");
        dungeonMaterial = baseApplication.getModelManager().getMaterial("Models/static/dungeon_pixel.j3m");
        
//        enemiesMaterial = baseApplication.getModelManager().getMaterial("Models/enemies/enemies.j3m");
    }

    private void loadFilter() {
        fpp = baseApplication.getAssetManager().loadFilter("Filters/default.j3f");
        baseApplication.getViewPort().addProcessor(fpp);
    }

    /**
     * Initialize a default map
     */
    protected void initMap() {
        for (int i = 0; i < map.length; i++) {
            Tile[] row = map[i];
            for (int j = 0; j < row.length; j++) {
                map[i][j] = new Tile(BLANK, i, j, 0, null);

            }
        }
    }

    @Override
    public void init() {

        //Special FX
        //load the filter
        if (!baseApplication.isMobileApp()) {
//            loadFilter();
        }

        //Load the surface
        Spatial surface = createSurface();
        surface.move(-TILE_SIZE * 0.5f, -0.02f, -TILE_SIZE * 0.5f);
        staticNode.attachChild(surface);

        //Load the static level models into the scene
        for (int r = 0; r < map.length; r++) {
            Tile[] row = map[r];
            for (int c = 0; c < row.length; c++) {
                Tile tile = row[c];
                updateTile(r, c, tile.getAngle(), tile.getName());
            }
        }

        //Load the light
        initLight(ColorRGBA.White.mult(2f), new Vector3f(0.5f, -0.5f, 0.5f));
    }

    /**
     * Get a blank tile spatial
     *
     * @return
     */
    protected Spatial getBlankTile() {
        Node tile = new Node(BLANK);
        Quad quad = new Quad(2, 2);
        Geometry geometry = new Geometry(BLANK, quad);
        Material material = baseApplication.getModelManager().getMaterial("Materials/tile-floor.j3m");
        geometry.setMaterial(material);
        geometry.rotate(FastMath.DEG_TO_RAD * -90, 0, 0);
        tile.attachChild(geometry);
        geometry.center();
        return tile;
    }

    /**
     * Create the surface quad
     * @return 
     */
    protected Spatial createSurface() {
        Quad quad = new Quad(MAP_SIZE * TILE_SIZE, MAP_SIZE * TILE_SIZE);
        Geometry geometry = new Geometry(BLANK, quad);
        quad.scaleTextureCoordinates(new Vector2f(MAP_SIZE, MAP_SIZE));
        Material material = baseApplication.getModelManager().getMaterial("Materials/tile-floor.j3m");
        geometry.setMaterial(material);
        geometry.rotate(FastMath.DEG_TO_RAD * 90, 0, 0);
        return geometry;
    }

    /**
     * Create a centered quad the size of a tile and apply a given material to it.
     *
     * @return
     */
    protected Spatial createTileWithMaterial(String name, Material material) {
        Node tile = new Node(name);
        Quad quad = new Quad(TILE_SIZE, TILE_SIZE);
        Geometry geometry = new Geometry(name, quad);
        geometry.setMaterial(material);
        geometry.rotate(FastMath.DEG_TO_RAD * -90, 0, 0);
        tile.attachChild(geometry);
        geometry.center();
        return tile;
    }

    /**
     * Search the tile pack to find a specific model.
     *
     * @param tileName
     * @return
     */
    public Spatial getTileModel(String tileName) {
        Spatial spatial = null;

        if (tileName.equals(ENEMY)) {
            spatial = createTileWithMaterial(ENEMY, baseApplication.getModelManager().getMaterial("Materials/tile-enemy.j3m"));

        } else if (tileName.equals(BLANK)) {
            spatial = createTileWithMaterial(BLANK, baseApplication.getModelManager().getMaterial("Materials/tile-floor.j3m"));

        } else {
            //find the spatial in the dungeon pack
            spatial = dungeonPack.getChild(tileName).clone();
            spatial.setMaterial(dungeonMaterial);

        }
        return spatial;
    }

    /**
     * Returns a tile at a specific position
     *
     * @param x
     * @param z
     * @return
     */
    public Tile getTile(int x, int z) {
        return map[x][z];
    }

    /**
     * Returns the closest tile to a given position in 3D space.
     * @param x
     * @param z
     * @return 
     */
    public Tile getClosestFloorTile(float x, float z) {
        Tile selectedTile = null;
        Vector3f clickPos = new Vector3f(x, 0, z);
        float distance = 100f;

        for (int r = 0; r < map.length; r++) {
            Tile[] row = map[r];
            for (int c = 0; c < row.length; c++) {
                Tile tile = row[c];

                Vector3f tilePos = new Vector3f(tile.getxPos() * TILE_SIZE, 0, tile.getzPos() * TILE_SIZE);
                float spatialToTileDistance = tilePos.distance(clickPos);

                if (spatialToTileDistance < distance) {
                    distance = spatialToTileDistance;
                    selectedTile = tile;
                }

            }
        }

        //If not a tile was selected we clear selection
        if (selectedTile != null && !selectedTile.getName().equals(FLOOR)) {
            selectedTile = null;
        }

        return selectedTile;
    }

    /**
     * Return the tile which is at a given contact point.
     * @param x
     * @param z
     * @return 
     */
    public Tile getTileFromContactPoint(float x, float z) {
        Tile selectedTile = null;
        Vector3f clickPos = new Vector3f(x, 0, z);
        float distance = 100f;

        for (int r = 0; r < map.length; r++) {
            Tile[] row = map[r];
            for (int c = 0; c < row.length; c++) {
                Tile tile = row[c];
                Vector3f tilePos = new Vector3f(tile.getxPos() * TILE_SIZE, 0, tile.getzPos() * TILE_SIZE);
                float spatialToTileDistance = tilePos.distance(clickPos);

                if (spatialToTileDistance < distance) {
                    distance = spatialToTileDistance;
                    selectedTile = tile;
                }

            }
        }

        return selectedTile;
    }

    /**
     * Update a selected tile at a position
     *
     * @param x
     * @param z
     * @param model
     */
    public void updateTile(int x, int z, int angle, String model) {
        Tile tile = map[x][z];
        tile.setName(model);
        tile.setAngle(angle);

        if (tile.getSpatial() != null) {
            tile.getSpatial().removeFromParent();
        }

        Spatial spatial = null;
        float xPos = x * TILE_SIZE;
        float zPos = z * TILE_SIZE;

        //Here we load the models;
        if (BLANK.equals(model)) {
//            spatial = getBlankTile();
//            spatial.setLocalTranslation(xPos, 0, zPos);
//
//            if (edit) {
//                createStatic(spatial);
//            }
        } else if (WALL_END.equals(model)) {
            spatial = dungeonPack.getChild("wallone").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setLocalRotation(new Quaternion().fromAngleAxis(tile.getAngle() * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_CORNER.equals(model)) {
            spatial = dungeonPack.getChild("wallcorner").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setLocalRotation(new Quaternion().fromAngleAxis(tile.getAngle() * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_TWOWAY.equals(model)) {
            spatial = dungeonPack.getChild("walltwo").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setLocalRotation(new Quaternion().fromAngleAxis(tile.getAngle() * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_LIGHT.equals(model)) {
            spatial = dungeonPack.getChild("walllight").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setLocalRotation(new Quaternion().fromAngleAxis(tile.getAngle() * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_THREEWAY.equals(model)) {
            spatial = dungeonPack.getChild("wallthree").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setLocalRotation(new Quaternion().fromAngleAxis(tile.getAngle() * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_FOURWAY.equals(model)) {
            spatial = dungeonPack.getChild("wallfour").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setLocalRotation(new Quaternion().fromAngleAxis(tile.getAngle() * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (FLOOR.equals(model)) {
            if (edit) {
                spatial = dungeonPack.getChild("floor").clone();
                spatial.setLocalTranslation(xPos, 0, zPos);
                spatial.setLocalRotation(new Quaternion().fromAngleAxis(tile.getAngle() * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));
                spatial.setMaterial(dungeonMaterial);
                createStatic(spatial);
            }


        } else if (ENEMY.equals(model)) {
            if (edit) {
                spatial = createTileWithMaterial(ENEMY, baseApplication.getModelManager().getMaterial("Materials/tile-enemy.j3m"));
                spatial.setLocalTranslation(xPos, 0, zPos);
                spatial.setLocalRotation(new Quaternion().fromAngleAxis(tile.getAngle() * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));
                createStatic(spatial);

            } else {
//                spatial = dungeonPack.getChild("floor").clone();
//                spatial.setLocalTranslation(xPos, 0, zPos);
//                spatial.setMaterial(dungeonMaterial);
//                createStatic(spatial);
                loadEnemy(tile);
                tile.setName(FLOOR);

            }


        }

        tile.setSpatial(spatial);


    }

    /**
     * This is a private helper method that will be used to load an enemy.
     *
     * @param position
     */
    protected void loadEnemy(Tile tile) {
        //Here we load specific type of enemies.
        if (initialTypeXCount > 0) {
            //Load initial male onces
            loadTypeX(tile);
            initialTypeXCount--;

        } else if (initialTypeYCount > 0) {
            //Load initial male onces
            loadTypeY(tile);
            initialTypeYCount--;

        } else {
            //Load initial infant onces
            loadInfant(tile);
        }

        if (enemies.size() >= MAX_ENEMIES) {
            fireGameOverListener();
        }

    }

    /**
     * Load the type X enemy and add its relavant controller.
     * @param tile 
     */
    public void loadTypeX(Tile tile) {
//        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/enemies/ghost.j3o");
//        enemySpatial.setMaterial(enemiesMaterial);
        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/skulls/typeX.j3o");
        enemySpatial.setLocalTranslation(new Vector3f(tile.getxPos() * TILE_SIZE, enemyHeight, tile.getzPos() * TILE_SIZE)); //Position the model on top of floor position
        createEnemy(enemySpatial, Vector3f.UNIT_XYZ);

        EnemyControl ec = new EnemyXControl(this, tile);
        enemySpatial.addControl(ec);

        enemies.add(ec);
    }

    /**
     * Load the type Y enemy and add its relavant controller.
     * @param tile 
     */
    public void loadTypeY(Tile tile) {
//        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/enemies/mummy.j3o");
//        enemySpatial.setMaterial(enemiesMaterial);        
        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/skulls/typeY.j3o");
        enemySpatial.setLocalTranslation(new Vector3f(tile.getxPos() * TILE_SIZE, enemyHeight, tile.getzPos() * TILE_SIZE)); //Position the model on top of floor position
        createEnemy(enemySpatial, Vector3f.UNIT_XYZ);

        EnemyControl ec = new EnemyYControl(this, tile);
        enemySpatial.addControl(ec);

        enemies.add(ec);
    }

    /**
     * Load the infant type enemy and add its relavant controller.
     * @param tile 
     */
    public void loadInfant(Tile tile) {
//        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/enemies/skeleton.j3o");
//        enemySpatial.setMaterial(enemiesMaterial);
        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/skulls/infant.j3o");
        enemySpatial.setLocalTranslation(new Vector3f(tile.getxPos() * TILE_SIZE, enemyHeight, tile.getzPos() * TILE_SIZE)); //Position the model on top of floor position
        createEnemy(enemySpatial, Vector3f.UNIT_XYZ);

        EnemyControl ec = new EnemyInfantControl(this, tile);
        enemySpatial.addControl(ec);

        enemies.add(ec);
    }

    /**
     * Load a mutant power
     *
     * @param tile
     */
    public void loadMutant(Tile tile) {
//        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/enemies/zombie.j3o");
//        enemySpatial.setMaterial(enemiesMaterial);
        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/skulls/mutant.j3o");
        enemySpatial.setLocalTranslation(new Vector3f(tile.getxPos() * TILE_SIZE, enemyHeight, tile.getzPos() * TILE_SIZE)); //Position the model on top of floor position
        createEnemy(enemySpatial, Vector3f.UNIT_XYZ);

        //Here we load specific type of enemies.
        EnemyControl ec = new MutantControl(this, tile);
        enemySpatial.addControl(ec);
    }

    /**
     * This method will be called from a type y enemy causing an infant enemy to
     * spawn.
     *
     * @param position
     */
    public void spawnInfant(Tile tile) {
        initialTypeYCount = 0;
        initialTypeXCount = 0;
        baseApplication.getSoundManager().playSound("spawn");
        loadEnemy(tile);

    }

    /**
     * Returns a list of all enemies
     *
     * @return
     */
    public ArrayList<EnemyControl> getEnemies() {
        return enemies;
    }

    /**
     * Remove an enemy
     *
     * @param control
     */
    public void removeEnemy(EnemyControl control) {
        enemies.remove(control);

        if (enemies.size() == 0) {
            fireGameCompletedListener();
        }
    }

    /**
     * Returns a spatial for a given power type. This is only used for placement of powers.
     * @param powerType
     * @return 
     */
    public Spatial getPowerModel(String powerType) {
        Spatial spatial = null;
        if (powerType.equals(Player.POWER_POISON)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/poison.j3o");

        } else if (powerType.equals(Player.POWER_SWITCH_TO_Y)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/plant1/plant1.j3o");

        } else if (powerType.equals(Player.POWER_SWITCH_TO_X)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/plant2/plant2.j3o");

        } else if (powerType.equals(Player.POWER_STOP)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/stop.j3o");

        } else if (powerType.equals(Player.POWER_BOMB)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/barrel.j3o");

        } else if (powerType.equals(Player.POWER_CURSE)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/lantern/lantern.j3o");

        } else if (powerType.equals(Player.POWER_GAS)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/acid.j3o");

        } else if (powerType.equals(Player.POWER_MUTANT)) {
//            spatial = baseApplication.getModelManager().getModel("Models/enemies/zombie.j3o");
//            spatial.setMaterial(enemiesMaterial);
            spatial = baseApplication.getModelManager().getModel("Models/skulls/mutant.j3o");

        }
        return spatial;
    }

    /**
     * Load a power to a tile position
     *
     * @param powerType
     * @param tile
     */
    public PowerControl loadPower(String powerType, Tile tile) {
        Spatial spatial = null;

        if (powerType.equals(Player.POWER_POISON)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/poison.j3o");

        } else if (powerType.equals(Player.POWER_SWITCH_TO_Y)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/plant1/plant1.j3o");

        } else if (powerType.equals(Player.POWER_SWITCH_TO_X)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/plant2/plant2.j3o");

        } else if (powerType.equals(Player.POWER_STOP)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/stop.j3o");

        } else if (powerType.equals(Player.POWER_BOMB)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/barrel.j3o");

        } else if (powerType.equals(Player.POWER_CURSE)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/sterilize.j3o");

        } else if (powerType.equals(Player.POWER_GAS)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/gas.j3o");

        } else if (powerType.equals(Player.POWER_MUTANT)) {
            loadMutant(tile);
            return null;
        }

        if (spatial == null) {
            throw new RuntimeException("Power, " + powerType + " not yet implemented.");
        }

//        spatial.setQueueBucket(RenderQueue.Bucket.Opaque);
        spatial.setLocalTranslation(new Vector3f(tile.getxPos() * TILE_SIZE, -0.02f, tile.getzPos() * TILE_SIZE));
        spatial.setUserData("power", powerType);
        createObstacle(spatial);

        PowerControl pc = new PowerControl(this, powerType, tile);
        spatial.addControl(pc);

        return pc;

    }

    public void addObstacle(Spatial spatial) {
        createObstacle(spatial);
    }

    /**
     * This method can be called to get a random adjacent tile.
     *
     * @param currentTile
     * @return
     */
    public Tile getNextAdjacentTile(Tile currentTile, Tile fromTile) {
        Tile tile = null;

        ArrayList<Tile> adjacentTiles = getAllAdjacentTile(currentTile, fromTile);
//        log("Number of adjacent tiles: " + adjacentTiles.size());

        //Get a random option
        if (adjacentTiles.size() > 0) {
            tile = adjacentTiles.get(FastMath.nextRandomInt(0, adjacentTiles.size() - 1));

        } else {
            //Stay on same tile
            tile = currentTile;

        }

        return tile;
    }

    /**
     * This method will return all tiles arround a given tile.
     * @param currentTile
     * @param fromTile
     * @return 
     */
    public ArrayList<Tile> getAllAdjacentTile(Tile currentTile, Tile fromTile) {

        ArrayList<Tile> adjacentTiles = new ArrayList<Tile>();
        if (hasFloor(0, -1, currentTile)) {
            adjacentTiles.add(map[currentTile.getxPos()][currentTile.getzPos() - 1]);
        }
        if (hasFloor(0, 1, currentTile)) {
            adjacentTiles.add(map[currentTile.getxPos()][currentTile.getzPos() + 1]);
        }
        if (hasFloor(1, 0, currentTile)) {
            adjacentTiles.add(map[currentTile.getxPos() + 1][currentTile.getzPos()]);
        }
        if (hasFloor(-1, 0, currentTile)) {
            adjacentTiles.add(map[currentTile.getxPos() - 1][currentTile.getzPos()]);
        }

        //This statement removes the previous position as an option when there are other options
        if (adjacentTiles.size() > 1 && fromTile != null && adjacentTiles.contains(fromTile)) {
            adjacentTiles.remove(fromTile);
        }

        return adjacentTiles;
    }

    /**
     * Check method if any floor tile exist with those parameters
     *
     * @param xOffset
     * @param zOffset
     * @param tile
     * @return
     */
    protected boolean hasFloor(int xOffset, int zOffset, Tile tile) {
        return (tile.getxPos() + xOffset) < MAP_SIZE
                && (tile.getxPos() + xOffset) > 0
                && (tile.getzPos() + zOffset) < MAP_SIZE
                && (tile.getzPos() + zOffset) > 0
                && map[tile.getxPos() + xOffset][tile.getzPos() + zOffset].getName().startsWith(FLOOR);
    }

    /**
     * A public helper method that can be called when the map or level needs to
     * be cleared.
     */
    public void clear() {

        if (fpp != null) {
            baseApplication.getViewPort().removeProcessor(fpp);
        }

        levelNode.detachAllChildren();

        staticNode = new Node(TYPE_STATIC);
        levelNode.attachChild(staticNode);

        initMap();

        init();

    }

    /**
     * A public helper method that can be called when the map or level needs to
     * be cleared.
     */
    public void clearToDefault() {

        if (fpp != null) {
            baseApplication.getViewPort().removeProcessor(fpp);
        }

        levelNode.detachAllChildren();

        staticNode = new Node(TYPE_STATIC);
        levelNode.attachChild(staticNode);

        //Make a default map
        for (int i = 0; i < map.length; i++) {
            Tile[] row = map[i];
            for (int j = 0; j < row.length; j++) {

//                if (i == 0 && j == 0) {
//                    map[i][j] = new Tile(WALL_CORNER3, i, j, null);
//
//                } else if (i == map.length - 1 && j == 0) {
//                    map[i][j] = new Tile(WALL_CORNER2, i, j, null);
//
//                } else if (i == map.length - 1 && j == map.length - 1) {
//                    map[i][j] = new Tile(WALL_CORNER1, i, j, null);
//
//                } else if (i == 0 && j == map.length - 1) {
//                    map[i][j] = new Tile(WALL_CORNER4, i, j, null);
//
//                } else if (i == 0 || i == map.length - 1 && j > 0) {
//                    map[i][j] = new Tile(WALL_TWOWAY2, i, j, null);
//
//                } else if (j == 0 || j == map.length - 1 && i > 0) {
//                    map[i][j] = new Tile(WALL_TWOWAY1, i, j, null);
//
//                } else {
//                    map[i][j] = new Tile(FLOOR, i, j, null);
//                }

                map[i][j] = new Tile(FLOOR, i, j, 0, null);

            }
        }


        init();

    }

    @Override
    public void close() {
        if (fpp != null) {
            baseApplication.getViewPort().removeProcessor(fpp);
        }
        super.close();
    }

    public int getScore() {
        return score;
    }

    public void addScore(int amount) {
        this.score += amount;
        fireScoreChangedListener(score);
    }

    public void removeScore(int amount) {
        this.score -= amount;
        if (score < 0) {
            score = 0;
        }
        fireScoreChangedListener(score);
    }

    /**
     *
     *
     * ################ ALL LEVEL PERSISTANCE GOES HERE
     *
     *
     *
     */
    public void edit(String levelfile) {
        setOptimize(false);
        edit = true;
        readEditFile(levelfile);
    }

    public void test(String levelfile) {
        setOptimize(true);
        edit = false;
        readEditFile(levelfile);
    }

    public void play(String levelfile) {
        setOptimize(true);
        edit = false;
        readAssetFile(levelfile);
    }

    /**
     * This method must be called when reading a level file and not when editing
     * it.
     *
     * @param levelfile
     */
    protected void readAssetFile(String levelfile) {

        //Read from the assets folder
        this.propertiesFile = levelfile;

        InputStream levelInputStream = null;
        Platform platform = JmeSystem.getPlatform();

        if (platform.compareTo(Platform.Android_ARM5) == 0 || platform.compareTo(Platform.Android_ARM6) == 0 || platform.compareTo(Platform.Android_ARM7) == 0) {
            levelInputStream = JmeSystem.getResourceAsStream("/assets/Levels/" + levelfile);

        } else {
            levelInputStream = JmeSystem.getResourceAsStream("/Levels/" + levelfile);
        }

        try {
            if (levelInputStream == null) {
                //Load a default
                log("Loading level properties");
                levelProperties = new Properties();

            } else {
                //Load the level.
                levelProperties = new Properties();
                log("Loading properties from file");
                levelProperties.load(levelInputStream);
                loadFromProperties();

            }

        } catch (IOException ex) {
            Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }

    /**
     * Read the current level that is designed.
     */
    protected void readEditFile(String levelfile) {

        this.propertiesFile = levelfile;
        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists()) {
            try {
                file = new File(folder.getAbsolutePath() + File.separator + levelfile);
                if (file.exists()) {
                    FileReader fileReader = new FileReader(file);
                    levelProperties = new Properties();
                    levelProperties.load(fileReader);
                    loadFromProperties();

                } else {
                    file.createNewFile();
                    levelProperties = new Properties();

                }

            } catch (IOException ex) {
                Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This is a helper method that can be used to save the new level.
     */
    public void save() {
        if (levelProperties != null) {
            File folder = JmeSystem.getStorageFolder();

            if (folder != null && folder.exists()) {
                if (file != null) {
                    PrintWriter printWriter = null;
                    try {
                        printWriter = new PrintWriter(file);
                        updateProperties();
                        levelProperties.store(printWriter, "");

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } finally {
                        printWriter.close();
                    }
                }
            }
        }

    }

    /**
     * A helper method that will be used to parse the properties into the Tile
     * array.
     */
    protected void loadFromProperties() {
        if (levelProperties != null) {
            //We need to loop over all the properties of the level and load the level.
            for (Iterator<Object> it = levelProperties.keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();

                //Try and parse the level properties
                String[] positionStr = key.split(",");
                int x = positionStr.length > 0 ? Integer.parseInt(positionStr[0].trim()) : 0;
                int z = positionStr.length > 1 ? Integer.parseInt(positionStr[1].trim()) : 0;
                int angle = positionStr.length > 2 ? Integer.parseInt(positionStr[2].trim()) : 0;
                String value = levelProperties.getProperty(key);
                Tile tile = map[x][z];
                tile.setName(value);
                tile.setAngle(angle);
            }
        }
    }

    /**
     * This method will translate the tile array to the properties file.
     */
    protected void updateProperties() {
        if (levelProperties != null) {
            levelProperties.clear();

            //We need to loop over all the tiles of the level and set the properties.
            for (int r = 0; r < map.length; r++) {
                Tile[] row = map[r];
                for (int c = 0; c < row.length; c++) {
                    Tile tile = row[c];
                    levelProperties.put(r + "," + c + "," + tile.getAngle(), tile.getName());
                }
            }
        }
    }
}
