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

import com.google.common.collect.ImmutableList;
import org.lwjgl.LWJGLException;
import org.lwjgl.opencl.*;

import org.terasology.engine.API;

import static com.google.common.base.Preconditions.checkState;
import static org.lwjgl.opencl.CL10.*;


/**
 * Created by Linus on 3/31/14.
 */
public final class OpenCL {

    private static final int DEVICE_TYPE_TO_USE = CL_DEVICE_TYPE_GPU;

    public static OpenCL getInstance() {
        if(instance == null) {
            instance = new OpenCL();
        }

        return instance;
    }

    private OpenCL() {
        checkState(instance == null, "OpenCL constructor called twice.");

        try {
            CL.create();
            clPlatform = CLPlatform.getPlatforms().get(0);
            clDevices = ImmutableList.copyOf(clPlatform.getDevices(DEVICE_TYPE_TO_USE));
            clContext = CLContext.create(clPlatform, clDevices, null, null, null);
            clCommandQueue = clCreateCommandQueue(clContext, clDevices.get(0), CL_QUEUE_PROFILING_ENABLE, null);
        } catch (LWJGLException e) {
            throw new Error(e);
        }
    }

    @Override
    public void finalize() {
        clReleaseCommandQueue(clCommandQueue);
        clReleaseContext(clContext);
        CL.destroy();

    }

    private static OpenCL instance = null;

    public final CLPlatform              clPlatform;
    public final ImmutableList<CLDevice> clDevices;
    public final CLContext               clContext;
    public final CLCommandQueue          clCommandQueue;
}
