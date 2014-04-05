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
package org.terasology.cellularAutomatons.chunks;

import org.terasology.math.Vector3i;

/**
 * Created by Linus on 4/5/14.
 */
class ChunkImplementation implements Chunk{

    private final ChunkManager chunkManager;
    private final Vector3i position;

    private ChunkLoadState   loadState;
    private ChunkUpdateState updateState;

    public ChunkImplementation(ChunkManager manager, Vector3i position) {
        this.chunkManager = manager;
        this.position = new Vector3i(position);

        loadState = ChunkLoadState.UNLOADED;
        updateState = ChunkUpdateState.CLEAN;
    }

    @Override
    public ChunkLoadState getLoadState() {
        return loadState;
    }

    @Override
    public boolean isDirty() {
        return updateState != ChunkUpdateState.CLEAN;
    }

    @Override
    public void setChanged() {
        this.markAsDirty();
        up().markAsDirty();
        down().markAsDirty();
        south().markAsDirty();
        west().markAsDirty();
        south().markAsDirty();
        east().markAsDirty();
    }

    public Chunk up() {
        Vector3i above = new Vector3i(position);
        above.add(Vector3i.up());
        return chunkManager.getChunk(above);
    }

    public Chunk down() {
        Vector3i down = new Vector3i(position);
        down.add(Vector3i.down());
        return chunkManager.getChunk(down);
    }

    public Chunk north() {
        Vector3i north = new Vector3i(position);
        north.add(Vector3i.north());
        return chunkManager.getChunk(north);
    }

    public Chunk west() {
        Vector3i west = new Vector3i(position);
        west.add(Vector3i.west());
        return chunkManager.getChunk(west);
    }

    public Chunk south() {
        Vector3i south = new Vector3i(position);
        south.add(Vector3i.south());
        return chunkManager.getChunk(south);
    }

    public Chunk east() {
        Vector3i east = new Vector3i(position);
        east.add(Vector3i.east());
        return chunkManager.getChunk(east);
    }

        @Override
    public void markAsDirty() {
        updateState = ChunkUpdateState.DIRTY;
    }
}
