/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.core.world.generator.facetProviders;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.Component;
import org.terasology.math.Vector2i;
import org.terasology.rendering.assets.texture.Texture;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.ConfigurableFacetProvider;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.SeaLevelFacet;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

import com.google.common.math.IntMath;

@Produces(SurfaceHeightFacet.class)
@Requires(@Facet(SeaLevelFacet.class))
public class HeightMapSurfaceHeightProvider implements ConfigurableFacetProvider {

    private static final Logger logger = LoggerFactory.getLogger(HeightMapSurfaceHeightProvider.class);

    private float[][] heightmap;

    private int mapWidth;
    private int mapHeight;

    private HeightMapConfiguration configuration = new HeightMapConfiguration();

    @Override
    public void setSeed(long seed) {
        logger.info("Reading height map..");

        Texture texture = Assets.getTexture("core:platec_heightmap");
        ByteBuffer[] bb = texture.getData().getBuffers();
        IntBuffer intBuf = bb[0].asIntBuffer();

        mapWidth = texture.getWidth();
        mapHeight = texture.getHeight();

        heightmap = new float[mapWidth][mapHeight];
        while (intBuf.position() < intBuf.limit()) {
            int pos = intBuf.position();
            long val = intBuf.get() & 0xFFFFFFFFL;
            heightmap[pos % mapWidth][pos / mapWidth] = val / (256 * 256 * 256 * 256f);
        }

        heightmap = shiftArray(rotateArray(heightmap), -50, -100);

    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(SurfaceHeightFacet.class);
        SurfaceHeightFacet facet = new SurfaceHeightFacet(region.getRegion(), border);

        for (Vector2i pos : facet.getWorldRegion()) {
            int xzScale = configuration.terrainScale;

            int wrapX = IntMath.mod(pos.getX(), mapWidth * xzScale);
            int wrapZ = IntMath.mod(pos.getY(), mapHeight * xzScale);

            int mapX = wrapX / xzScale;
            int mapZ = wrapZ / xzScale;
            double p00 = heightmap[mapX][mapZ];
            double p10 = heightmap[(mapX - 1 + 512) % 512][(mapZ) % 512];
            double p11 = heightmap[(mapX - 1 + 512) % 512][(mapZ + 1 + 512) % 512];
            double p01 = heightmap[(mapX) % 512][(mapZ + 1 + 512) % 512];

            float relX = (wrapX % xzScale) / (float) xzScale;
            float relZ = (wrapZ % xzScale) / (float) xzScale;

            float interpolatedHeight = (float) lerp(relX, lerp(relZ, p10, p11), lerp(relZ, p00, p01));
            float height = configuration.heightOffset + configuration.heightScale * interpolatedHeight;

            facet.setWorld(pos, height);
        }

        region.setRegionFacet(SurfaceHeightFacet.class, facet);

    }

    //helper functions for the Mapdesign until real mapGen is in
    public static float[][] rotateArray(float[][] array) {
        float[][] newArray = new float[array[0].length][array.length];
        for (int i = 0; i < newArray.length; i++) {
            for (int j = 0; j < newArray[0].length; j++) {
                newArray[i][j] = array[j][array[j].length - i - 1];
            }
        }
        return newArray;
    }

    public static float[][] shiftArray(float[][] array, int x, int y) {
        int size = array.length;
        float[][] newArray = new float[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                newArray[i][j] = array[(i + x + size) % size][(j + y + size) % size];
            }
        }
        return newArray;
    }

    private static double lerp(double t, double a, double b) {
        return a + fade(t) * (b - a);  //not sure if i should fade t, needs a bit longer to generate chunks but is definately nicer
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    @Override
    public String getConfigurationName() {
        return "Height Map";
    }

    @Override
    public Component getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.configuration = (HeightMapConfiguration) configuration;
    }

    private static class HeightMapConfiguration implements Component {

        @Range(min = 0, max = 50f, increment = 1f, precision = 0, description = "Height Offset")
        private float heightOffset = 5;

        @Range(min = 10, max = 200f, increment = 10f, precision = 0, description = "Height Scale Factor")
        private float heightScale = 80;

        @Range(min = 1, max = 32, increment = 1, precision = 0, description = "Terrain Scale Factor")
        private int terrainScale = 4;
    }
}
