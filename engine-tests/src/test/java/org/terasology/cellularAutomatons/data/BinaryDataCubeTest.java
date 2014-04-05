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

import org.terasology.TerasologyTestingEnvironment;
import org.terasology.cellularAutomatons.OpenCL;
import org.terasology.math.Vector3i;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Linus on 3/31/14.
 */
public class BinaryDataCubeTest extends TerasologyTestingEnvironment {

    private OpenCL openCL = OpenCL.getInstance();

    private final List<Vector3i> validWritePositions = new ArrayList<Vector3i>() {{
        add(new Vector3i(0, 0, 0));
        add(new Vector3i(3, 3, 3));
        add(new Vector3i(1, 2, 1));
    }};

    @Test
    public void testGetSet() {
        BinaryDataCube cube = new BinaryDataCube();
        final int MIN = -1,
                  MAX = DataCubes.WRITE_DATA_BLOCK_SIZE;

        cube.set(MAX, MIN, MIN, true);
        cube.set(MIN, MAX, MIN, true);
        cube.set(MIN, MIN, MAX, true);
        cube.set(1, 0, 3, true);

        assert(cube.get(MAX, MIN, MIN));
        assert(cube.get(MIN, MAX, MIN));
        assert(cube.get(MIN, MIN, MAX));
        assert(cube.get(1, 0, 3));

        cube.set(MAX, MIN, MIN, false);
        cube.set(1,0,3, false);

        assert(!cube.get(MAX, MIN, MIN));
        assert(cube.get(MIN, MAX, MIN));
        assert(cube.get(MIN, MIN, MAX));
        assert(!cube.get(1, 0, 3));
    }

    private String wrongPositionIndexMessage(int expected, int actual) {
        Vector3i expectedXYZ = BinaryDataCube.indexToReadXYZ(expected);
        Vector3i actualXYZ = BinaryDataCube.indexToReadXYZ(actual);
        return String.format(
            "Expected: %d =(x: %d, y: %d, z: %d)\n" +
            "Found:    %d =(x: %d, y: %d, z: %d)",
            expected, expectedXYZ.x, expectedXYZ.y, expectedXYZ.z,
            actual, actualXYZ.x, actualXYZ.y, actualXYZ.z
        );
    }

    @Test
    public void testIndexUp() {
        for(Vector3i position: validWritePositions) {
            int index = BinaryDataCube.readCoordsToIndex(position.x, position.y, position.z);

            Vector3i up = Vector3i.up();
            up.add(position.x, position.y, position.z);

            int expected = BinaryDataCube.readCoordsToIndex(up.x, up.y, up.z);
            int actual = BinaryDataCube.up(index);

            assertEquals(wrongPositionIndexMessage(expected, actual), expected, actual);
        }
    }

    @Test
    public void testIndexDown() {
        for(Vector3i position: validWritePositions) {
            int index = BinaryDataCube.readCoordsToIndex(position.x, position.y, position.z);

            Vector3i down = Vector3i.down();
            down.add(position.x, position.y, position.z);

            int expected = BinaryDataCube.readCoordsToIndex(down.x, down.y, down.z);
            int actual = BinaryDataCube.down(index);

            assertEquals(wrongPositionIndexMessage(expected, actual), expected, actual);
        }
    }

    @Test
    public void testIndexNorth() {
        for(Vector3i position: validWritePositions) {
            int index = BinaryDataCube.readCoordsToIndex(position.x, position.y, position.z);

            Vector3i north = Vector3i.north();
            north.add(position.x, position.y, position.z);

            int expected = BinaryDataCube.readCoordsToIndex(north.x, north.y, north.z);
            int actual = BinaryDataCube.north(index);

            assertEquals(wrongPositionIndexMessage(expected, actual), expected, actual);
        }
    }

    @Test
    public void testIndexWest() {
        for(Vector3i position: validWritePositions) {
            int index = BinaryDataCube.readCoordsToIndex(position.x, position.y, position.z);

            Vector3i west = Vector3i.west();
            west.add(position.x, position.y, position.z);

            int expected = BinaryDataCube.readCoordsToIndex(west.x, west.y, west.z);
            int actual = BinaryDataCube.west(index);

            assertEquals(wrongPositionIndexMessage(expected, actual), expected, actual);
        }
    }

    @Test
    public void testIndexSouth() {
        for(Vector3i position: validWritePositions) {
            int index = BinaryDataCube.readCoordsToIndex(position.x, position.y, position.z);

            Vector3i south = Vector3i.south();
            south.add(position.x, position.y, position.z);

            int expected = BinaryDataCube.readCoordsToIndex(south.x, south.y, south.z);
            int actual = BinaryDataCube.south(index);

            assertEquals(wrongPositionIndexMessage(expected, actual), expected, actual);
        }
    }

    @Test
    public void testIndexEast() {
        for(Vector3i position: validWritePositions) {
            int index = BinaryDataCube.readCoordsToIndex(position.x, position.y, position.z);

            Vector3i east = Vector3i.east();
            east.add(position.x, position.y, position.z);

            int expected = BinaryDataCube.readCoordsToIndex(east.x, east.y, east.z);
            int actual = BinaryDataCube.east(index);

            assertEquals(wrongPositionIndexMessage(expected, actual), expected, actual);
        }
    }

    @Test
    public void testIndexToAndFromXYZ() {
        BinaryDataCube cube = new BinaryDataCube();

        Vector3i input = new Vector3i(-1,-1,-1);
        Vector3i output = identity(input);
        assert(input.equals(output));

        input = new Vector3i(0,0,0);
        output = identity(input);
        assert(input.equals(output));

        input = new Vector3i(4,4,4);
        output = identity(input);
        assert(input.equals(output));
    }



    private Vector3i identity(Vector3i input) {
        return BinaryDataCube.indexToReadXYZ(BinaryDataCube.readCoordsToIndex(input.x, input.y, input.z));
    }
}
