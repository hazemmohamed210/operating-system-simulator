package main;

import java.io.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Interpreter {
	private Memory memory;
	private Queue<Integer> readyQueue;
	private Queue<Integer> blockedQueue;
	private Mutex mutex;
	private Scheduler scheduler;
	
	// 2nd commit
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
	public void createProcess(String programName, int id) {
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
            this.memory.addPCB(new PCB(id, start, ProcessState.READY, bounds));

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void print(String printed) {
		System.out.println(printed);
	}
	
	public void printFromTo(int a, int b) {
		for(int i = a; i<=b; i++) {
			System.out.print(i+" ");
		}
	}
	
	public ArrayList<String> readData(String path) {
		File file = new File("src/"+path+".txt");
        FileReader fileReader;
        ArrayList<String> result = new ArrayList<>();
		try {
			fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result.add(line);
			}
			bufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}
	
	public void writeToDisk(int pId) { // writes process data to hard disk
		int[] bounds = this.memory.getProcessBounds(pId);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/hardDisk.txt"))) {
			for(int i = bounds[0]; i<bounds[1] && this.memory.get(i) != null; i++) {
				writer.write(this.memory.get(i)+"");
	            writer.newLine();
	            this.memory.set(i, null);
			}
			for(int i = bounds[2]; i<bounds[3] && this.memory.get(i) != null; i++) {
				writer.write(this.memory.get(i)+"");
	            writer.newLine();
	            this.memory.set(i, null);
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		String[] progs = {"program_1","program_3"};
		int[] arr = {0,2};
		Interpreter i = new Interpreter(2, progs, arr);
	}
	

}
