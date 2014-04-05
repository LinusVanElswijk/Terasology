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
import org.terasology.cellularAutomatons.CoordinateSubSpace;
import org.terasology.math.Vector3i;

import static org.junit.Assert.assertEquals;

/**
 * Created by Linus on 4/4/14.
 */
public class CoordinateSubSpaceTest {

    @Test
    public void test() {
        CoordinateSubSpace subSpace = new CoordinateSubSpace(new Vector3i(10,10,10), new Vector3i(0, 0, 0));

        Vector3i world = new Vector3i(101, 1, 9);
        Vector3i local = subSpace.worldToLocal(world);
        Vector3i global = subSpace.worldToSubSpace(world);

        assertEquals(new Vector3i(10, 0, 0), global);
        assertEquals(new Vector3i(1, 1, 9), local);
        assertEquals(world, subSpace.localToWorld(global, local));

        world = new Vector3i(0, -101, -10);
        local = subSpace.worldToLocal(world);
        global = subSpace.worldToSubSpace(world);

        assertEquals(new Vector3i(0, -11, -1), global);
        assertEquals(new Vector3i(0, 9, 0), local);
        assertEquals(world, subSpace.localToWorld(global, local));

        subSpace = new CoordinateSubSpace(new Vector3i(3, 3, 3), new Vector3i(1, 20, -30));
        world = new Vector3i(68, 17, -1);
        local = subSpace.worldToLocal(world);
        global = subSpace.worldToSubSpace(world);

        assertEquals(new Vector3i(22, -1, 9), global);
        assertEquals(new Vector3i(1, 0, 2), local);
        assertEquals(world, subSpace.localToWorld(global, local));
    }
}
