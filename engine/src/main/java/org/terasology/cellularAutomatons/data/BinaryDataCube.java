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

import org.terasology.cellularAutomatons.OpenCL;
import org.terasology.math.Vector3i;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Random;

/**
 * Created by Linus on 3/31/14.
 */
public class BinaryDataCube implements RWDataCube<Boolean> {
    public final static int SIZE_IN_BYTES = (DataCubes.NR_OF_READABLE_ELEMENTS + Byte.SIZE - 1) / Byte.SIZE;

    public BinaryDataCube() {
        bits = new BitSet(DataCubes.NR_OF_READABLE_ELEMENTS);
    }

    public BinaryDataCube(ByteBuffer bytes) {
        bytes.rewind();
        bits = new BitSet(DataCubes.NR_OF_READABLE_ELEMENTS);
        bits.or(BitSet.valueOf(bytes));
    }

    public void randomize() {
        Random random = new Random();
        byte[] bytes = new byte[SIZE_IN_BYTES];
        random.nextBytes(bytes);
        bits.xor(BitSet.valueOf(bytes));
    }

    @Override
    public Boolean get(int x, int y, int z) {
        return bits.get(writeCoordsToIndex(x, y, z));
    }

    @Override
    public void set(int x, int y, int z, Boolean data) {
        bits.set(writeCoordsToIndex(x, y, z), data);
    }

    public static int up(final int index) {
        return index + DataCubes.READ_DATA_BLOCK_SIZE;
    }

    public static int down(final int index) {
        return index - DataCubes.READ_DATA_BLOCK_SIZE;
    }

    public static int north(final int index) {
        return index + 1;
    }

    public static int west(final int index) {
        return index + DataCubes.READ_DATA_BLOCK_SIZE_SQR;
    }

    public static int south(final int index) {
        return index - 1;
    }

    public static int east(final int index) {
        return index - DataCubes.READ_DATA_BLOCK_SIZE_SQR;
    }

    public ByteBuffer toDirectByteBuffer(){
        byte[] bytes = bits.toByteArray();

        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);
        buffer.flip();

        return buffer;
    }

    public static int readCoordsToIndex(final int x, final int y, final int z) {
        return x * DataCubes.READ_DATA_BLOCK_SIZE * DataCubes.READ_DATA_BLOCK_SIZE
             + y * DataCubes.READ_DATA_BLOCK_SIZE
             + z;
    }

    public static int writeCoordsToIndex(final int x, final int y, final int z) {
        return readCoordsToIndex(x + 1, y + 1, z + 1);
    }

    public static Vector3i indexToReadXYZ(int index) {
        final int x = index / (DataCubes.READ_DATA_BLOCK_SIZE * DataCubes.READ_DATA_BLOCK_SIZE);
        index %= (DataCubes.READ_DATA_BLOCK_SIZE * DataCubes.READ_DATA_BLOCK_SIZE);
        final int y = index / DataCubes.READ_DATA_BLOCK_SIZE;
        index %= DataCubes.READ_DATA_BLOCK_SIZE; // now equal to z

        return new Vector3i(x, y, index);
    }

    public static Vector3i indexToWriteXYZ(final int index) {
        final Vector3i xyz = indexToReadXYZ(index);
        xyz.sub(1, 1, 1);
        return xyz;
    }

    public void fallVector() {
        int index;
        for(int x = 0; x < DataCubes.WRITE_DATA_BLOCK_SIZE; x++) {
            for(int y = 0; y < DataCubes.WRITE_DATA_BLOCK_SIZE; y++) {
                for(int z = 0; z < DataCubes.WRITE_DATA_BLOCK_SIZE; z++) {
                    fallVector(writeCoordsToIndex(x, y, z));
                }
            }
        }
    }

    public void fallIndex() {
        int index;
        for(int x = 0; x < DataCubes.WRITE_DATA_BLOCK_SIZE; x++) {
            for(int y = 0; y < DataCubes.WRITE_DATA_BLOCK_SIZE; y++) {
                for(int z = 0; z < DataCubes.WRITE_DATA_BLOCK_SIZE; z++) {
                    fallIndex(writeCoordsToIndex(x, y, z));
                }
            }
        }
    }

    public void fallFast() {
        int index;
        for(int x = 0; x < DataCubes.WRITE_DATA_BLOCK_SIZE; x++) {
            for(int y = 0; y < DataCubes.WRITE_DATA_BLOCK_SIZE; y++) {
                for(int z = 0; z < DataCubes.WRITE_DATA_BLOCK_SIZE; z++) {
                    fallFast(writeCoordsToIndex(x, y, z));
                }
            }
        }
    }

    public void fallVector(int index) {
        Vector3i xyz = indexToReadXYZ(index);
        if( get(xyz.x, xyz.y, xyz.z) )
        {
            if(!get(xyz.x, xyz.y-1, xyz.z))
                set(xyz.x, xyz.y, xyz.z, false);
        }
        else
        {
            if(get(xyz.x, xyz.y+1, xyz.z))
                set(xyz.x, xyz.y, xyz.z, true);
        }
    }

    //fall index is fastest version on CPU
    public void fallIndex(int index) {

        if( bits.get(index) )
        {
            if(!bits.get(down(index)))
                bits.set(index, false);
        }
        else
        {
            if(bits.get(up(index)))
                bits.set(index, true);
        }
    }

    public void fallFast(int index) {
        bits.set(index, (boolean)(bits.get(index) & !bits.get(down(index)) | bits.get(up(index)) ));
    }
      
    private final BitSet bits;
}
