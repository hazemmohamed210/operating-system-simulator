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
	private String currRunning;
	
	// 2nd commit
	public Interpreter(int timeSlice, String[] programs, int[] arrivalTimes) {
		this.readyQueue = new ConcurrentLinkedQueue<>();
		this.blockedQueue = new ConcurrentLinkedQueue<>();
		this.mutex = new Mutex();
		this.memory = new Memory();
		this.scheduler = new Scheduler(timeSlice);
		this.currRunning = "none";
		this.scheduler.Schedule(this, programs, arrivalTimes);
	}
	
	
	public String getCurrRunning() {
		return currRunning;
	}

	public void setCurrRunning(String currRunning) {
		this.currRunning = currRunning;
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
            int start = (this.memory.getLastIndex() == 40)? 24:this.memory.getLastIndex();
            while ((line = bufferedReader.readLine()) != null) {
            	if(this.memory.getLastIndex() == 40) {
            		this.memory.setLastIndex(24);
            		this.memory.add(new Pair("var-a", null));
            		this.memory.add(new Pair("var-b", null));
            		this.memory.add(new Pair("var-tmp", null));
            	} else if (this.memory.getLastIndex() == 8 || this.memory.getLastIndex() == 24) {
            		this.memory.add(new Pair("var-a", null));
            		this.memory.add(new Pair("var-b", null));
            		this.memory.add(new Pair("var-tmp", null));
            	}
               this.memory.add(new Pair("instruction", line));
            }
            if(this.memory.getLastIndex() <= 23) {
            	this.memory.setLastIndex(24);
            } else {
            	this.memory.setLastIndex(40);
            }
            int[] bounds = {start, this.memory.getLastIndex()-1};
            this.memory.addPCB(new PCB(id, start+3, ProcessState.READY, bounds));

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
	
	public String fetch(int pId) {
		if(this.memory.get(0) != null && (int)((Pair)this.memory.get(0)).getValue() == pId) {
			int pc = (int)((Pair)this.memory.get(1)).getValue();
//			this.memory.set(1, new Pair("PC",pc+1));
			if(((Pair)this.memory.get(pc)).getName().equals("instruction")) return (String)((Pair)this.memory.get(pc)).getValue();
		} else if (this.memory.get(4) != null && (int)((Pair)this.memory.get(4)).getValue() == pId) {
			int pc = (int)((Pair)this.memory.get(5)).getValue();
			if(((Pair)this.memory.get(pc)).getName().equals("instruction")) return (String)((Pair)this.memory.get(pc)).getValue();
//			this.memory.set(5, new Pair("PC",pc+1));
		}
		return "";
	}
	
	public void decodeAndExecute(String instr) {
		switch(instr.split(" ")[0]) {
		case "semWait": if(instr.split(" ")[1].equals("userInput")) this.mutex.semWaitUserInput(Integer.parseInt(this.currRunning));
						else if (instr.split(" ")[1].equals("userOutput")) this.mutex.semWaitUserOutput(Integer.parseInt(this.currRunning));
						else this.mutex.semWaitFile(Integer.parseInt(this.currRunning)); break;
		case "semSignal": if(instr.split(" ")[1].equals("userInput")) this.mutex.semSignalUserInput();
						else if (instr.split(" ")[1].equals("userOutput")) this.mutex.semSignalUserOutput();
						else this.mutex.semSignalFile(); break;
		}
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
