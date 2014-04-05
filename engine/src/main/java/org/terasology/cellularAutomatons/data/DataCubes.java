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

import com.google.common.math.IntMath;

/**
 * Created by Linus on 3/31/14.
 */
public class DataCubes {
    public static final int WRITE_DATA_BLOCK_SIZE = 8;
    public static final int READ_DATA_BLOCK_SIZE  = WRITE_DATA_BLOCK_SIZE + 2;

    public static final int READ_DATA_BLOCK_SIZE_SQR = IntMath.pow(READ_DATA_BLOCK_SIZE, 2);

    public static final int NR_OF_READABLE_ELEMENTS = IntMath.pow(READ_DATA_BLOCK_SIZE, 3);
}
