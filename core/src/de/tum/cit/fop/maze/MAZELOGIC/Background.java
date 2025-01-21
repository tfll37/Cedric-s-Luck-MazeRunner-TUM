package de.tum.cit.fop.maze.MAZELOGIC;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Background {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private final SpriteBatch spriteBatch;
    private MazeLoader mazeLoader;
    private  TrapMNGR trapMNGR;
    private SpecialAreaHNDLR specialAreaHNDLR;

    public Background(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
    }

    public void loadTiledMap(String tmxPath, String propertiesPath) {
        // Load the base TMX map
        tiledMap = new TmxMapLoader().load(tmxPath);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, gameCONFIG.UNIT_SCALE, spriteBatch);

        // Load maze configuration
        mazeLoader = new MazeLoader(propertiesPath, tiledMap, trapMNGR);

        specialAreaHNDLR = new SpecialAreaHNDLR(tiledMap, mazeLoader);

        // Update maze layout with properties
        updateMazeLayout();
    }

    private void updateMazeLayout() {
        TiledMapTileLayer baseLayer = getBaseLayer();
        mazeLoader.setTiledMap(tiledMap);
        mazeLoader.orientWallTiles(baseLayer);
        updateLayer(baseLayer);

        TiledMapTileLayer secondLayer = getSecondLayer();
        if (secondLayer != null) {
            updateLayer(secondLayer);
            specialAreaHNDLR.createSpecialAreas(secondLayer);
            mazeLoader.orientWallTiles(secondLayer);
        }

        specialAreaHNDLR.createSpecialAreas(baseLayer);
    }

    private void updateLayer(TiledMapTileLayer layer) {
        // Get all overrides from the properties file
        var overrides = mazeLoader.getAllOverrides();

        // Apply overrides to the map
        for (var entry : overrides.entrySet()) {
            var pos = entry.getKey();
            int tileType = entry.getValue();

            // Convert tile type to actual tile ID
            int tileId = mazeLoader.getTileId(tileType);

            // Update the cell in the layer
            TiledMapTileLayer.Cell cell = layer.getCell(pos.x, pos.y);
            if (cell == null) {
                cell = new TiledMapTileLayer.Cell();
                layer.setCell(pos.x, pos.y, cell);
            }
            cell.setTile(tiledMap.getTileSets().getTile(tileId));
        }
    }

    public TiledMapTileLayer getBaseLayer() {
        return (TiledMapTileLayer) tiledMap.getLayers().get(0);
    }

    public TiledMapTileLayer getSecondLayer() {
        return tiledMap.getLayers().size() > 1 ?
                (TiledMapTileLayer) tiledMap.getLayers().get(1) : null;
    }


    public void centerTiledMap(OrthographicCamera camera) {
        if (tiledMap != null) {
            MapProperties props = tiledMap.getProperties();
            int mapWidth = props.get("width", Integer.class);
            int mapHeight = props.get("height", Integer.class);
            int tileWidth = props.get("tilewidth", Integer.class);
            int tileHeight = props.get("tileheight", Integer.class);

            float mapPixelWidth = mapWidth * tileWidth;
            float mapPixelHeight = mapHeight * tileHeight;

            camera.position.set(mapPixelWidth / 2f, mapPixelHeight / 2f, 0);
            camera.update();
        }
    }

    public void renderTiledMap(OrthographicCamera camera) {
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (tiledMapRenderer != null) {
            tiledMapRenderer.dispose();
        }
    }
}