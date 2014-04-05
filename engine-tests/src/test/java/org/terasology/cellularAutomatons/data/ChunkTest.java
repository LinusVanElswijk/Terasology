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
package org.terasology.cellularAutomatons.data;

import org.junit.Test;
import org.terasology.cellularAutomatons.chunks.Chunk;
import org.terasology.cellularAutomatons.chunks.ChunkLoadState;
import org.terasology.cellularAutomatons.chunks.ChunkManager;
import org.terasology.math.Vector3i;

import static org.junit.Assert.assertEquals;

/**
 * Created by Linus on 4/5/14.
 */
public class ChunkTest {

    @Test
     public void testUpdateStates() {
        ChunkManager manager = new ChunkManager();

        Vector3i focus = new Vector3i(0,0,0);
        Chunk chunk = manager.getChunk(focus);

        assert(!chunk.isDirty());
        chunk.setChanged();

        assert(chunk.isDirty());
        assert(chunk.up().isDirty());
        assert(chunk.down().isDirty());
        assert(chunk.east().isDirty());
        assert(chunk.west().isDirty());
        assert(chunk.north().isDirty());
        assert(chunk.south().isDirty());
    }

    @Test
    public void testLoadStates() {
        ChunkManager manager = new ChunkManager();

        Vector3i focus = new Vector3i(0,0,0);
        Chunk chunk = manager.getChunk(focus);

        assertEquals(ChunkLoadState.UNLOADED, chunk.getLoadState());
    }
}
