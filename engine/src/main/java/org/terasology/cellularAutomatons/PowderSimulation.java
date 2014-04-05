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
package org.terasology.cellularAutomatons;

import org.terasology.cellularAutomatons.data.BinaryDataCube;
import org.terasology.cellularAutomatons.data.DataCubes;
import org.terasology.math.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.internal.ChunkViewCore;

/**
 * Created by Linus on 4/1/14.
 */
public class PowderSimulation {

    @In
    public WorldProvider worldProvider;

    @In
    public BlockManager blockManager;

    public void simulate(Vector3i vector3i) {

        ChunkViewCore i = worldProvider.getWorldViewAround(vector3i);
        i.lock();
            BinaryDataCube powder = powderLocations(i);
            BinaryDataCube blocked = blockedLocations(i);
        i.unlock();
    }

    public BinaryDataCube powderLocations(ChunkViewCore i) {
        Block air = blockManager.getBlock("engine:air");
        Block sand = blockManager.getBlock("engine:sand");
        BinaryDataCube cube = new BinaryDataCube();

        for(int x = -1; x < DataCubes.READ_DATA_BLOCK_SIZE; x++){
            for(int y = -1; y < DataCubes.READ_DATA_BLOCK_SIZE; y++) {
                for(int z = -1; z < DataCubes.READ_DATA_BLOCK_SIZE; z++) {
                    Block block = i.getBlock(x+1, y+1, z+65);
                    cube.set(x,y,z, block == sand);
                }
            }
        }

        return cube;
    }


    private BinaryDataCube blockedLocations(ChunkViewCore i) {
        Block air = blockManager.getBlock("engine:air");
        BinaryDataCube cube = new BinaryDataCube();

        for(int x = -1; x < DataCubes.READ_DATA_BLOCK_SIZE; x++){
            for(int y = -1; y < DataCubes.READ_DATA_BLOCK_SIZE; y++) {
                for(int z = -1; z < DataCubes.READ_DATA_BLOCK_SIZE; z++) {
                    Block block = i.getBlock(x+1, y+1, z+65);
                    cube.set(x,y,z, block != air);
                }
            }
        }

        return cube;
    }

}
