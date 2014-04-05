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

import com.google.common.math.IntMath;
import org.terasology.math.Vector3i;

import java.math.RoundingMode;

/**
 * Created by Linus on 4/2/14.
 */
public class CoordinateSubSpace {
    private final Vector3i size, offset;

    public CoordinateSubSpace(final Vector3i size, final Vector3i offset) {
        this.size = new Vector3i(size);
        this.offset = new Vector3i(offset);
    }

    public Vector3i localToWorld(final Vector3i subSpace, final Vector3i local) {
        Vector3i world = subSpaceToWorld(subSpace);
        world.add(local);

        return world;
    }

    private Vector3i subSpaceToWorld(final Vector3i subSpace) {
        return new Vector3i(
            subSpace.x * size.x + offset.x,
            subSpace.y * size.y + offset.y,
            subSpace.z * size.z + offset.z
        );
    }


    public Vector3i worldToSubSpace(final Vector3i world) {
        return new Vector3i(
            IntMath.divide(world.x - offset.x, size.x, RoundingMode.FLOOR),
            IntMath.divide(world.y - offset.y, size.y, RoundingMode.FLOOR),
            IntMath.divide(world.z - offset.z, size.z, RoundingMode.FLOOR)
        );
    }


    public Vector3i worldToLocal(final Vector3i world) {
        return new Vector3i(
            IntMath.mod(world.x - offset.x, size.x),
            IntMath.mod(world.y - offset.y, size.y),
            IntMath.mod(world.z - offset.z, size.z)
        );
    }
}
