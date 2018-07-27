package org.lwjglb.engine.graph.World;

import org.joml.Vector3f;
import org.lwjglb.engine.OpenSimplexNoise;
import org.lwjglb.engine.graph.Mesh;

import java.util.ArrayList;
import java.util.PrimitiveIterator;

public class WorldMesh {

    private final double FEATURE_SIZE;

    private final int vertecisPerTileSide;
    private final int tilesPerSide;

    private final OpenSimplexNoise noise;


    private float tileSize;

    private Mesh[][] tiles;


    public WorldMesh(float camPosX, float camPosY, float tileSize, int tilesPerSide, int verticesPerTileSide, long seed, int featursize) {

        this.FEATURE_SIZE = featursize;
        this.vertecisPerTileSide = verticesPerTileSide;
        this.tilesPerSide = tilesPerSide;

        this.noise = new OpenSimplexNoise(seed);

        this.tiles = new Mesh[tilesPerSide][tilesPerSide];

        this.tileSize = tileSize;

        float viewDistance = tileSize*tilesPerSide/2;

        float xStart = camPosX-viewDistance;

        float yStart = camPosY-viewDistance;

        for (int i = 0; i < tilesPerSide; i++) {

            float tempStrart = xStart;

            for (int y = 0; y < tilesPerSide; y++) {

                tiles[i][y] = PlaneGenerator.generateNoisePlane(tempStrart,yStart,tileSize,tileSize,verticesPerTileSide,verticesPerTileSide,noise,featursize);

                tempStrart+=tileSize;

            }
            yStart+=tileSize;
        }
    }

    public Mesh[][] getTiles() {
        return tiles;
    }

    public void setTiles(Mesh[][] tiles) {
        this.tiles = tiles;
    }
}