/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.skulls.game;

import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.games.platform.PlatformGame;
import com.bruynhuis.galago.games.simplecollision.SimpleCollisionGame;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
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
    public static final String WALL_LIGHT1 = "wall_light1";
    public static final String WALL_LIGHT2 = "wall_light2";
    public static final String WALL_END1 = "wall_end1";
    public static final String WALL_END2 = "wall_end2";
    public static final String WALL_END3 = "wall_end3";
    public static final String WALL_END4 = "wall_end4";
    public static final String WALL_CORNER1 = "wall_corner1";
    public static final String WALL_CORNER2 = "wall_corner2";
    public static final String WALL_CORNER3 = "wall_corner3";
    public static final String WALL_CORNER4 = "wall_corner4";
    public static final String WALL_TWOWAY1 = "wall_twoway1";
    public static final String WALL_TWOWAY2 = "wall_twoway2";
    public static final String WALL_THREEWAY1 = "wall_threeway1";
    public static final String WALL_THREEWAY2 = "wall_threeway2";
    public static final String WALL_THREEWAY3 = "wall_threeway3";
    public static final String WALL_THREEWAY4 = "wall_threeway4";
    public static final String WALL_FOURWAY = "wall_fourway";
    public static final String FLOOR = "floor";
    public static final String ENEMY = "enemy";
    public static float TILE_SIZE = 2;
    public static int MAP_SIZE = 24;
    private Tile map[][] = new Tile[MAP_SIZE][MAP_SIZE];
    private boolean edit = false;
    private float enemyHeight = 0.2f;

    /*
     * This property will keep track of all enemies in the level.
     */
    private ArrayList<EnemyControl> enemies = new ArrayList<EnemyControl>();
    public static int MAX_ENEMIES = 20;
    /**
     * This will be uses to place the initial enemies on the map
     */
    private int initialMaleCount = 2;
    private int initialFemaleCount = 2;
    private Node dungeonPack;
    private Material dungeonMaterial;
    private FilterPostProcessor fpp;
    private int score = 600;

    public Game(BaseApplication baseApplication, Node rootNode) {
        super(baseApplication, rootNode);

        initMap();

        dungeonPack = (Node) baseApplication.getModelManager().getModel("Models/static/dungeon.j3o");
        dungeonMaterial = baseApplication.getModelManager().getMaterial("Models/static/dungeon.j3m");
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
                map[i][j] = new Tile(BLANK, i, j, null);

            }
        }
    }

    @Override
    public void init() {

        //Special FX
        //load the filter
        if (baseApplication.getGameSaves().getGameData().isFxOn()) {
//            loadFilter();
        }

        //Load the surface for optimization
        if (edit) {
            Spatial surface = createSurface();
            surface.move(-TILE_SIZE * 0.5f, -0.02f, -TILE_SIZE * 0.5f);
            staticNode.attachChild(surface);
            log("Surface created");
        }

        //Load the static level models into the scene
        for (int r = 0; r < map.length; r++) {
            Tile[] row = map[r];
            for (int c = 0; c < row.length; c++) {
                Tile tile = row[c];
                updateTile(r, c, tile.getName());
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
//        Box quad = new Box(1, 1, 1);
        Geometry geometry = new Geometry(BLANK, quad);
        Material material = baseApplication.getModelManager().getMaterial("Materials/tile-blank.j3m");
        geometry.setMaterial(material);
        geometry.rotate(FastMath.DEG_TO_RAD * -90, 0, 0);
        tile.attachChild(geometry);
        geometry.center();
        return tile;
    }

    protected Spatial createSurface() {
        Quad quad = new Quad(MAP_SIZE * TILE_SIZE, MAP_SIZE * TILE_SIZE);
        Geometry geometry = new Geometry(BLANK, quad);
        quad.scaleTextureCoordinates(new Vector2f(MAP_SIZE, MAP_SIZE));
        Material material = baseApplication.getModelManager().getMaterial("Materials/tile-blank.j3m");
        geometry.setMaterial(material);
        geometry.rotate(FastMath.DEG_TO_RAD * 90, 0, 0);
        return geometry;
    }

    /**
     * Return an initial enemy start point spatial
     *
     * @return
     */
    protected Spatial getEnemyTile() {
        Node tile = new Node(ENEMY);
        Quad quad = new Quad(2, 2);
        Geometry geometry = new Geometry(ENEMY, quad);
        Material material = baseApplication.getModelManager().getMaterial("Materials/tile-enemy.j3m");
        geometry.setMaterial(material);
        geometry.rotate(FastMath.DEG_TO_RAD * -90, 0, 0);
        tile.attachChild(geometry);
        geometry.center();
        return tile;
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

        //If not a tile was selected we clear selection
//        if (!selectedTile.getName().equals(FLOOR)) {
//            selectedTile = null;
//        }

        return selectedTile;
    }

    /**
     * Update a selected tile at a position
     *
     * @param x
     * @param z
     * @param model
     */
    public void updateTile(int x, int z, String model) {
        Tile tile = map[x][z];
        tile.setName(model);


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
        } else if (WALL_END1.equals(model)) {
            spatial = dungeonPack.getChild("onewall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_END2.equals(model)) {
            spatial = dungeonPack.getChild("onewall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.rotate(0, FastMath.DEG_TO_RAD * 90f, 0);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_END3.equals(model)) {
            spatial = dungeonPack.getChild("onewall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.rotate(0, FastMath.DEG_TO_RAD * 180f, 0);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_END4.equals(model)) {
            spatial = dungeonPack.getChild("onewall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.rotate(0, FastMath.DEG_TO_RAD * 270f, 0);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_CORNER1.equals(model)) {
            spatial = dungeonPack.getChild("cornerwall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_CORNER2.equals(model)) {
            spatial = dungeonPack.getChild("cornerwall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.rotate(0, FastMath.DEG_TO_RAD * 90f, 0);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_CORNER3.equals(model)) {
            spatial = dungeonPack.getChild("cornerwall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.rotate(0, FastMath.DEG_TO_RAD * 180f, 0);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_CORNER4.equals(model)) {
            spatial = dungeonPack.getChild("cornerwall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.rotate(0, FastMath.DEG_TO_RAD * 270f, 0);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_TWOWAY1.equals(model)) {
            spatial = dungeonPack.getChild("twowall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_TWOWAY2.equals(model)) {
            spatial = dungeonPack.getChild("twowall").clone();
            spatial.rotate(0, FastMath.DEG_TO_RAD * 90f, 0);
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_LIGHT1.equals(model)) {
            spatial = dungeonPack.getChild("lightwall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_LIGHT2.equals(model)) {
            spatial = dungeonPack.getChild("lightwall").clone();
            spatial.rotate(0, FastMath.DEG_TO_RAD * 90f, 0);
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_THREEWAY1.equals(model)) {
            spatial = dungeonPack.getChild("threewall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_THREEWAY2.equals(model)) {
            spatial = dungeonPack.getChild("threewall").clone();
            spatial.rotate(0, FastMath.DEG_TO_RAD * 90f, 0);
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_THREEWAY3.equals(model)) {
            spatial = dungeonPack.getChild("threewall").clone();
            spatial.rotate(0, FastMath.DEG_TO_RAD * 180f, 0);
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_THREEWAY4.equals(model)) {
            spatial = dungeonPack.getChild("threewall").clone();
            spatial.rotate(0, FastMath.DEG_TO_RAD * 270f, 0);
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (WALL_FOURWAY.equals(model)) {
            spatial = dungeonPack.getChild("fourwall").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (FLOOR.equals(model)) {
            spatial = dungeonPack.getChild("floor").clone();
            spatial.setLocalTranslation(xPos, 0, zPos);
            spatial.setMaterial(dungeonMaterial);
            createStatic(spatial);

        } else if (ENEMY.equals(model)) {
            if (edit) {
                spatial = getEnemyTile();
                spatial.setLocalTranslation(xPos, 0, zPos);
                createStatic(spatial);

            } else {
                spatial = dungeonPack.getChild("floor").clone();
                spatial.setLocalTranslation(xPos, 0, zPos);
                spatial.setMaterial(dungeonMaterial);
                createStatic(spatial);
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
        if (initialMaleCount > 0) {
            //Load initial male onces
            loadMale(tile);
            initialMaleCount--;

        } else if (initialFemaleCount > 0) {
            //Load initial male onces
            loadFemale(tile);
            initialFemaleCount--;

        } else {
            //Load initial young onces
            loadYoung(tile);
        }

        if (enemies.size() >= MAX_ENEMIES) {
            fireGameOverListener();
        }

    }

    public void loadMale(Tile tile) {
        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/enemies/ghost/ghost.j3o");
        enemySpatial.setLocalTranslation(new Vector3f(tile.getxPos() * TILE_SIZE, enemyHeight, tile.getzPos() * TILE_SIZE)); //Position the model on top of floor position
        createEnemy(enemySpatial, Vector3f.UNIT_Y);

        EnemyControl ec = new EnemyControl(this, EnemyControl.TYPE_MALE, tile);
        enemySpatial.addControl(ec);

        enemies.add(ec);
    }

    public void loadFemale(Tile tile) {
        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/enemies/mummy/mummy.j3o");
        enemySpatial.setLocalTranslation(new Vector3f(tile.getxPos() * TILE_SIZE, enemyHeight, tile.getzPos() * TILE_SIZE)); //Position the model on top of floor position
        createEnemy(enemySpatial, Vector3f.UNIT_Y);

        EnemyControl ec = new EnemyControl(this, EnemyControl.TYPE_FEMALE, tile);
        enemySpatial.addControl(ec);

        enemies.add(ec);
    }

    public void loadYoung(Tile tile) {
        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/enemies/skeleton/skeleton.j3o");
        enemySpatial.setLocalTranslation(new Vector3f(tile.getxPos() * TILE_SIZE, enemyHeight, tile.getzPos() * TILE_SIZE)); //Position the model on top of floor position
        createEnemy(enemySpatial, Vector3f.UNIT_Y);

        EnemyControl ec = new EnemyControl(this, EnemyControl.TYPE_YOUNG, tile);
        enemySpatial.addControl(ec);

        enemies.add(ec);
    }

    /**
     * Load a mutated enemy
     *
     * @param tile
     */
    public void loadMutated(Tile tile) {
        Spatial enemySpatial = baseApplication.getModelManager().getModel("Models/enemies/zombie/zombie.j3o");
        enemySpatial.setLocalTranslation(new Vector3f(tile.getxPos() * TILE_SIZE, enemyHeight, tile.getzPos() * TILE_SIZE)); //Position the model on top of floor position
        createEnemy(enemySpatial, Vector3f.UNIT_Y);

        //Here we load specific type of enemies.
        EnemyControl ec = new EnemyControl(this, EnemyControl.TYPE_MUTATED, tile);
        enemySpatial.addControl(ec);
    }

    /**
     * This method will be called from a female enemy causing a young enemy to
     * spawn.
     *
     * @param position
     */
    public void spawnYoung(Tile tile) {
        initialFemaleCount = 0;
        initialMaleCount = 0;
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
     * Load a power to a tile position
     *
     * @param powerType
     * @param tile 
     */
    public PowerControl loadPower(String powerType, Tile tile) {
        Spatial spatial = null;

        if (powerType.equals(Player.POWER_POIZON)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/poison.j3o");

        } else if (powerType.equals(Player.POWER_FEMALE)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/female.j3o");

        } else if (powerType.equals(Player.POWER_MALE)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/male.j3o");

        } else if (powerType.equals(Player.POWER_STOP)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/stop.j3o");

        } else if (powerType.equals(Player.POWER_BOMB)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/bomb.j3o");

        } else if (powerType.equals(Player.POWER_STERILIZATION)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/sterilize.j3o");
            
        } else if (powerType.equals(Player.POWER_GAS)) {
            spatial = baseApplication.getModelManager().getModel("Models/powers/gas.j3o");

        } else if (powerType.equals(Player.POWER_MUTATION)) {
            loadMutated(tile);
            return null;
        }

        if (spatial == null) {
            throw new RuntimeException("Power, " + powerType + " not yet implemented.");
        }

        spatial.setLocalTranslation(new Vector3f(tile.getxPos() * TILE_SIZE, 0, tile.getzPos() * TILE_SIZE));
        spatial.setUserData("power", powerType);
        createObstacle(spatial);

        PowerControl pc = new PowerControl(this, powerType, tile);
        spatial.addControl(pc);
        
        return pc;

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

                if (i == 0 && j == 0) {
                    map[i][j] = new Tile(WALL_CORNER3, i, j, null);

                } else if (i == map.length - 1 && j == 0) {
                    map[i][j] = new Tile(WALL_CORNER2, i, j, null);

                } else if (i == map.length - 1 && j == map.length - 1) {
                    map[i][j] = new Tile(WALL_CORNER1, i, j, null);

                } else if (i == 0 && j == map.length - 1) {
                    map[i][j] = new Tile(WALL_CORNER4, i, j, null);

                } else if (i == 0 || i == map.length - 1 && j > 0) {
                    map[i][j] = new Tile(WALL_TWOWAY2, i, j, null);

                } else if (j == 0 || j == map.length - 1 && i > 0) {
                    map[i][j] = new Tile(WALL_TWOWAY1, i, j, null);

                } else {
                    map[i][j] = new Tile(FLOOR, i, j, null);
                }


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
                String value = levelProperties.getProperty(key);
                Tile tile = map[x][z];
                tile.setName(value);
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
                    levelProperties.put(r + "," + c, tile.getName());
                }
            }
        }
    }
}
