package main;

import java.io.BufferedReader;
import java.io.*;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Interpreter {
	private Memory memory;
	private Queue<Integer> readyQueue;
	private Queue<Integer> blockedQueue;
	private Mutex mutex;
	private Scheduler scheduler;
	
	// initial commit
	public Interpreter(int timeSlice, String[] programs, int[] arrivalTimes) {
		this.readyQueue = new ConcurrentLinkedQueue<>();
		this.blockedQueue = new ConcurrentLinkedQueue<>();
		this.mutex = new Mutex();
		this.memory = new Memory();
		this.scheduler = new Scheduler(timeSlice);
		this.scheduler.Schedule(this, programs, arrivalTimes);
	}
	
	
	public Memory getMemory() {
		return memory;
	}


	public Queue<Integer> getReadyQueue() {
		return readyQueue;
	}


	public Queue<Integer> getBlockedQueue() {
		return blockedQueue;
	}


	public Mutex getMutex() {
		return mutex;
	}


	// CHECK THAT THE PROCESS THAT HAS THE RESOURCE IS THE ONE CALLING SEM SIGNAL
	public void createProcess(String programName) {
		try {
            File file = new File("src/"+programName+".txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            int start = (this.memory.getLastIndex() == 40)? 25:this.memory.getLastIndex();
            while ((line = bufferedReader.readLine()) != null) {
            	if(this.memory.getLastIndex() == 40) {
            		this.memory.setLastIndex(25);
            	}
               this.memory.add(new Pair("instruction", line));
            }
            if(this.memory.getLastIndex() <= 24) {
            	this.memory.setLastIndex(25);
            } else {
            	this.memory.setLastIndex(40);
            }
            int[] bounds = {start, this.memory.getLastIndex()-1};
            this.memory.addPCB(new Pair("PCB", new PCB(1, start, ProcessState.READY, bounds)));

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		String[] progs = {"program_1","program_2","program_3"};
		int[] arr = {0,2,4};
		Interpreter i = new Interpreter(2, progs, arr);
//		i.createProcess("program_2");
		// Interpreter i = new Interpreter();
		// for(int clk = 0; clk<7; clk++){
		// 	if(clk == 0) i.createProcess();
		// 	if(clk == 1) i.createProcess();
		// 	if(clk == 4) i.createProcess();
		//}
//		Memory mem = new Memory();
//		System.out.println(mem);
	}
	

}
