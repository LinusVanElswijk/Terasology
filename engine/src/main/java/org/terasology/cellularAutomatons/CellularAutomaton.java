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

public abstract class CellularAutomaton {



    /*
    protected final UpdateRegionManager regionManager;

    @In
    protected BlockManager blockManager;

    @In
    protected WorldProvider world;

    private final int numberOfUpdateSteps;

    private final Logger logger = LoggerFactory.getLogger(CellularAutomaton.class);


    public CellularAutomaton(int numberOfUpdateSteps) {
        if(numberOfUpdateSteps < 0)
            throw new IllegalArgumentException(String.format("numberOfUpdateSteps must be positive, received %d", numberOfUpdateSteps));


        world = CoreRegistry.get(WorldProvider.class);

        this.numberOfUpdateSteps = numberOfUpdateSteps;
        regionManager = new UpdateRegionManager(world);
    }

    public CellularAutomaton() {
        this(1);
    }

    public abstract void initialise();

    public abstract void shutdown();

    public abstract void blockChanged(Vector3i position);

    public final int numberOfSteps() {
        return numberOfUpdateSteps;
    }

    public abstract BlockData nextState(Neighborhood neighborhood, final int step);

////////////////////////////////////////////////////////////////////////////////////////////
// Updating
////////////////////////////////////////////////////////////////////////////////////////////

    public void update(float delta) {

        if(world != null) {
            regionManager.activatePendingRegions();

            for(int step = 0; step < numberOfSteps(); step++) {
                update(step, delta);
                swapBlockDataBuffers();
            }

            writeStates();
        }
        else {
            world = CoreRegistry.get(WorldProvider.class);
        }
    }

    private void update(int step, float delta) {
        for(Iterator<UpdateRegion> regionIterator = regionManager.regionIterator(); regionIterator.hasNext();) {
            UpdateRegion region = regionIterator.next();
            Vector3i pos = region.getPosition();

            updateRegion(region, step, delta);
        }
    }

    private void swapBlockDataBuffers() {
        for(Iterator<UpdateRegion> regionIterator = regionManager.regionIterator(); regionIterator.hasNext();) {
            UpdateRegion region = regionIterator.next();
            region.swapBlockDataBuffers();
        }
    }

    private void updateRegion(UpdateRegion region, int step, float delta) {
        if(step == 0) {
            region.swapUpdateFlags();
        }

        ChunkView view  = world.getLocalView(region.getChunk());

        if(view != null && view.isValidView())
        {
            forAllBlocksDo( nextState(region, view, step) );
        }
    }

    private void writeStates() {
        for(Iterator<UpdateRegion> regionIterator = regionManager.regionIterator(); regionIterator.hasNext();) {
            UpdateRegion region = regionIterator.next();

            WriteStateFunction writeState = writeState(region);

            forAllBlocksDo(writeState);

            for(BlockUpdate update: writeState.updates)
            {
                //TODO add extra data (liquid data) and update multiple
                world.setBlock(update.getPosition(), update.getNewType());
            }

            if(region.needsUpdate()) {
                regionManager.setChanged(region.getPosition());
            }
        }
    }

    private void forAllBlocksDo(BlockFunction function) {
        for(int x = 0; x < UpdateRegion.SIZE_X; x++){
            for(int y = 0; y < UpdateRegion.SIZE_Y; y++){
                for(int z = 0; z < UpdateRegion.SIZE_Z; z++){
                    if(function.needsCall(x,y,z)) {
                        function.call(x, y, z);
                    }
                }
            }
        }
    }

    private abstract class BlockFunction {
        protected final UpdateRegion region;

        public BlockFunction(final UpdateRegion region) {
            this.region = region;
        }

        public abstract void call(int x, int y, int z);

        public boolean needsCall(int x, int y, int z) {
            return region.needsUpdate(x,y,z);
        }

        public int expectedCalls() {
            return region.blocksNeedingUpdate();
        }
    }

    private class WriteStateFunction extends BlockFunction {
        public final LinkedList<BlockUpdate> updates;

        public WriteStateFunction(final UpdateRegion region) {
            super(region);
            updates = new LinkedList<BlockUpdate>();
        }

        public void call(int x, int y, int z) {
            Vector3i worldPos = region.toWorldPos(x, y, z);

            boolean changed = false;

            if(region.blockDataBuffer().get(x, y, z) == null) return;

            if(region.blockDataBuffer().get(x, y, z).type != world.getBlock(worldPos)) {
                updates.add(new BlockUpdate(worldPos, region.blockDataBuffer().get(x, y, z).type, world.getBlock(worldPos)));
                region.setChanged(x,y,z);
                changed = true;
            }

            for(BlockUpdate update: updates)
            {
                world.setBlock(update.getPosition(), update.getNewType());
            }

            updates.clear();
			/*  todo reenable
			if(region.blockDataBuffer().get(x, y, z).extraData != world.getExtraData(worldPos)) {
				world.setExtraData(worldPos, region.blockDataBuffer().get(x, y, z).extraData,  world.getExtraData(worldPos));
				changed = true;
			}
			*
            if(changed) {
                region.setChanged(x, y, z);
            }
        }
    }

    private WriteStateFunction writeState(final UpdateRegion region) {
        return new WriteStateFunction(region);
    }

    private class NextStateFunction extends BlockFunction {
        protected final int step;
        protected final NeighborhoodProvider neighborhoodProvider;

        public NextStateFunction(final UpdateRegion region, final int step, NeighborhoodProvider neighborhoodProvider) {
            super(region);

            this.neighborhoodProvider = neighborhoodProvider;
            this.step = step;
        }

        public void call(int x, int y, int z) {
            final int viewX = x,
                    viewY = region.toWorldY(y),
                    viewZ = z;

            region.setData(x, y, z, nextState(neighborhoodProvider.getNeighborhood(viewX, viewY, viewZ), step));
        }
    }

    private NextStateFunction nextState(final UpdateRegion region, final ChunkView view, final int step) {
        NeighborhoodProvider neighborhoodProvider = (step == 0 ? new WorldNeighborhoodProvider(view) : new UpdateRegionNeighborhoodProvider(region, view));

        return new NextStateFunction(region, step, neighborhoodProvider);
    }

////////////////////////////////////////////////////////////////////////////////////////////
// NeighborhoodProviders
////////////////////////////////////////////////////////////////////////////////////////////

    private abstract class NeighborhoodProvider {
        public abstract Neighborhood getNeighborhood(final int worldX, final int worldY, final int worldZ);
    }

    private class UpdateRegionNeighborhoodProvider extends NeighborhoodProvider {

        private final UpdateRegion region;
        private final ChunkView view;

        public UpdateRegionNeighborhoodProvider(UpdateRegion region, ChunkView view) {
            this.region = region;
            this.view = view;
        }

        @Override
        public Neighborhood getNeighborhood(final int viewX, final int viewY, final int viewZ) {
            final Vector3i viewPos = new Vector3i(viewX, viewY, viewZ);
            final Vector3i worldPos = view.toWorldPos(viewPos);
            final Vector3i regionPos = UpdateRegion.toRelativePos(worldPos, region.getPosition());

            return new NeighborhoodArray() {
                @Override
                protected BlockData getNeighborhoodData(int dx, int dy, int dz) {
                    BlockData data = region.getData(regionPos.x + dx, regionPos.y + dy, regionPos.z + dz);

                    if(data == null) {
                        data = new BlockData(
                                view.getBlock(
                                        viewX + dx,
                                        viewY + dy,
                                        viewZ + dz
                                ),      0/* todo reenable
									view.getExtraData(
										viewX + dx,
										viewY + dy,
										viewZ + dz
									)         *
                        );
                    }

                    return data;
                }

            };
        }

    }

    private class WorldNeighborhoodProvider extends NeighborhoodProvider {

        private final ChunkView view;

        public WorldNeighborhoodProvider(ChunkView view) {
            this.view = view;
        }

        @Override
        public Neighborhood getNeighborhood(final int viewX, final int viewY, final int viewZ) {

            return new NeighborhoodArray() {
                @Override
                protected BlockData getNeighborhoodData(int dx, int dy, int dz) {
                    return new BlockData(
                            view.getBlock(
                                    viewX + dx,
                                    viewY + dy,
                                    viewZ + dz
                            ), 0        /*     todo reenable
						view.getExtraData(
							viewX + dx,
							viewY + dy,
							viewZ + dz
						)            *
                    );
                }

            };
        }
    }
    */
}