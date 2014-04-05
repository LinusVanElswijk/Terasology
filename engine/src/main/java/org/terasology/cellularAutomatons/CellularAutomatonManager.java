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

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cellularAutomatons.data.BinaryDataCube;
import org.terasology.entitySystem.systems.*;

import java.nio.ByteBuffer;

import static org.lwjgl.opencl.CL10.*;


/**
 * System that handles updating cellular automata.
 *
 * @author Linus van Elswijk <linusvanelswijk@gmail.com>
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class CellularAutomatonManager extends BaseComponentSystem implements UpdateSubscriberSystem {

    static final String source =
            "kernel void sum(global const uchar *a, global uchar *answer) { " +
            "  unsigned int xid = get_global_id(0); " +
            "  answer[xid] = ~a[xid];" +
            "}";

    private OpenCL openCL;

    @Override
    public void update(float delta) {

    }

    private static final Logger logger = LoggerFactory.getLogger(CellularAutomatonManager.class);
    /*
    public static final int NUM_THREADS = 4;
    public static final float MAX_UPDATE_FREQUENCY = 0.125f;

    @In
    private BlockManager blockManager;

    private List<CellularAutomaton> automata;

    private BlockingQueue<LiquidSimulationTask> taskQueue;// = Queues.newLinkedBlockingQueue();
    private Executor executor;// = Executors.newFixedThreadPool(NUM_THREADS);

    private Block WATER,
    		      LIFE,
    			  BWATER,
    			  SOURCE;
    
    private float cumulDelta;
    */

    private void printByteArray(ByteBuffer buffer) {
        StringBuilder strB = new StringBuilder();

        for(int i = 0; i < buffer.capacity(); i++) {
            strB.append("|" + Byte.toString(buffer.get(i)));
        }

        logger.info(strB.toString());
    }

    private void runProgram() {


        BinaryDataCube cubeA = new BinaryDataCube();
        cubeA.set(0,0,0, true);
        cubeA.set(0,0,1, true);
        cubeA.set(2,2,2, true);

        ByteBuffer dataA = cubeA.toDirectByteBuffer();
        ByteBuffer dataR = ByteBuffer.allocateDirect(dataA.capacity());

        CLMem aMem = clCreateBuffer(openCL.clContext, CL10.CL_MEM_READ_ONLY | CL10.CL_MEM_COPY_HOST_PTR, dataA, null);
        clEnqueueWriteBuffer(openCL.clCommandQueue, aMem, 1, 0, dataA, null, null);
        CLMem answerMem = clCreateBuffer(openCL.clContext, CL10.CL_MEM_WRITE_ONLY | CL10.CL_MEM_COPY_HOST_PTR, dataR, null);
        clFinish(openCL.clCommandQueue);

// Create our program and kernel
        CLProgram program = clCreateProgramWithSource(openCL.clContext, source, null);
        Util.checkCLError(clBuildProgram(program, openCL.clDevices.get(0), "", null));
// sum has to match a kernel method name in the OpenCL source
        CLKernel kernel = clCreateKernel(program, "sum", null);

        PointerBuffer kernel1DGlobalWorkSize = BufferUtils.createPointerBuffer(1);
        kernel1DGlobalWorkSize.put(0, dataA.capacity());
        kernel.setArg(0, aMem);
        kernel.setArg(1, answerMem);
        clEnqueueNDRangeKernel(openCL.clCommandQueue, kernel, 1, null, kernel1DGlobalWorkSize, null, null, null);

        clEnqueueReadBuffer(openCL.clCommandQueue, answerMem, 1, 0, dataR, null, null);
        clFinish(openCL.clCommandQueue);

        BinaryDataCube cubeR = new BinaryDataCube(dataR);


        logger.info(String.format("A(0,0,0) = %s", cubeA.get(0,0,0) ? "TRUE" : "FALSE"));
        logger.info(String.format("A(0,0,0) = %s", cubeA.get(0,0,1) ? "TRUE" : "FALSE"));
        logger.info(String.format("A(0,0,0) = %s", cubeA.get(2,2,2) ? "TRUE" : "FALSE"));
        logger.info(String.format("A(0,0,0) = %s", cubeA.get(1,1,1) ? "TRUE" : "FALSE"));

        logger.info(String.format("R(0,0,0) = %s", cubeR.get(0,0,0) ? "TRUE" : "FALSE"));
        logger.info(String.format("R(0,0,0) = %s", cubeR.get(0,0,1) ? "TRUE" : "FALSE"));
        logger.info(String.format("R(0,0,0) = %s", cubeR.get(2,2,2) ? "TRUE" : "FALSE"));
        logger.info(String.format("R(0,0,0) = %s", cubeR.get(1,1,1) ? "TRUE" : "FALSE"));
    }

    @Override
    public void initialise() {
        logger.info("Initialising CA system...");
        openCL = OpenCL.getInstance();
        runProgram();
        logger.info("Done running prog");
        logger.info("CA system initialized");
    }

    @Override
    public void shutdown() {
        logger.info("Shutting down CA system...");
        openCL = null;
        logger.info("CA system shut down");
    }
    /*
    public void addCellularAutomaton(CellularAutomaton automaton)
    {
       automata.add(automaton);
    }

    private class SimulationThread implements Runnable
    {
    	@Override
        public void run() {
    		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            
    		while (true) {
                try {
                    //LiquidSimulationTask task = taskQueue.take();
                    logger.info("Testing");
                    Thread.sleep(1000);
                    /*if (task.shutdownThread()) {
                        break;
                    }
                    try (ThreadActivity ignored = ThreadMonitor.startThreadActivity(task.getName())) {
                        task.run();
                    }*
                } catch (InterruptedException e) {
                    ThreadMonitor.addError(e);
                    logger.debug("Interrupted");
                } catch (Exception e) {
                    ThreadMonitor.addError(e);
                    logger.error("Error in water simulation", e);
                }
            }
        }
    }
        

    
    @ReceiveEvent(components = BlockComponent.class)
    public void blockChanged(OnChangedBlock event, EntityRef blockEntity) {
    	for(CellularAutomaton automaton: automata)
        {
            automaton.blockChanged(event.getBlockPosition());
        }
    	/*System.out.println("event!");
    	
    	if(event.getNewType() == WATER) {
    		StringBuffer string = new StringBuffer("CAWater added to chunk ");
        	string.append(TeraMath.calcChunkPos(event.getBlockPosition()).toString());
        	
        	water.blockPlaced(event.getBlockPosition());
    	}
    	
    	if(event.getNewType() == LIFE) {
    		StringBuffer string = new StringBuffer("CALife added to chunk ");
        	string.append(TeraMath.calcChunkPos(event.getBlockPosition()).toString());
        	
        	life.blockPlaced(event.getBlockPosition());
    	}
    	
    	if(event.getNewType() == BWATER || event.getNewType() == SOURCE) {
    		StringBuffer string = new StringBuffer("CABWater added to chunk ");
        	string.append(TeraMath.calcChunkPos(event.getBlockPosition()).toString());
        	
        	logger.info(string.toString());
        	
        	bWater.blockPlaced(event.getBlockPosition());
    	}
    	
    	if( event.getNewType() == BlockManager.getAir() ) {
    		bWater.blockRemoved(event.getBlockPosition());
    	}*
    }
    
    @Override
	public void update(final float delta) {
		cumulDelta += delta;

		if(cumulDelta > MAX_UPDATE_FREQUENCY) {
			for(CellularAutomaton automaton: automata)
                automaton.update(MAX_UPDATE_FREQUENCY);
		}
	}
    */
}


