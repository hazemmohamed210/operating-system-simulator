package main;

import java.io.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Interpreter {
	private Memory memory;
	private Queue<Integer> readyQueue;
	private Queue<Integer> blockedQueue;
	private Mutex mutex;
	private Scheduler scheduler;
	private String currRunning;
	private String inDisk;
	
	// 2nd commit
	public Interpreter(int timeSlice, String[] programs, int[] arrivalTimes) {
		this.readyQueue = new ConcurrentLinkedQueue<>();
		this.blockedQueue = new ConcurrentLinkedQueue<>();
		this.mutex = new Mutex();
		this.memory = new Memory();
		this.scheduler = new Scheduler(timeSlice);
		this.currRunning = "none";
		this.inDisk = "none";
		this.scheduler.Schedule(this, programs, arrivalTimes);
	}

	public String getInDisk() {
		return inDisk;
	}

	public void setInDisk(String inDisk) {
		this.inDisk = inDisk;
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
//            if(this.memory.getLastIndex() == 40) {
//            	this.writeToDisk(id);
//            } // 0 4 8 23
            int start = (this.memory.getLastIndex() == 40)? 24:this.memory.getLastIndex();
            if(this.memory.getLastIndex() == 40) { // check if the memory is full
            	// ha3mel check gowa el pcb 3ala ely not running
            	int outId = -1000;
            	if((ProcessState)((Pair)this.memory.get(2)).getValue() != ProcessState.RUNNING) {
            		outId = (int)((Pair)this.memory.get(2)).getValue();
            		this.memory.setKernelSpaceIndex(0);
            	} else {
            		outId = (int)((Pair)this.memory.get(4)).getValue();
            		this.memory.setKernelSpaceIndex(4);
            	}
            	int[] bounds = this.writeToDisk(outId); // write to disk w bageeb el bounds bta3et ely wadeto el disk
            	System.out.println("bounds: "+bounds[0]+" "+bounds[1]+" "+bounds[2]+" "+bounds[3]);
            	this.inDisk = outId+"";
            	System.out.println("in disk: "+inDisk);
            	while ((line = bufferedReader.readLine()) != null) {
                	if(this.memory.getLastIndex() == 40) {
                		System.out.println("1st if");
                		this.memory.setLastIndex(bounds[2]); // 40
                		this.memory.add(new Pair("var-a", null));
                		this.memory.add(new Pair("var-b", null));
                		this.memory.add(new Pair("var-tmp", null));
                	} else if (this.memory.getLastIndex() == 8 || this.memory.getLastIndex() == 24) {
                		System.out.println("2nd if");
                		this.memory.add(new Pair("var-a", null));
                		this.memory.add(new Pair("var-b", null));
                		this.memory.add(new Pair("var-tmp", null));
                	}
                   this.memory.add(new Pair("instruction", line));
                   System.out.println("line: "+line);
                }
            	this.memory.setLastIndex(40);
            	int[] boundss = {bounds[2], bounds[3]};
            	this.memory.addPCB(new PCB(id, bounds[2]+3, ProcessState.READY, boundss));
            	this.memory.setKernelSpaceIndex(8);
            } else { // memory not full
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
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	
	public void print(String printed) {
		File file = new File("src/main/"+printed+".txt");
        FileReader fileReader;
		try {
			fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printFromTo(int a, int b) {
		for(int i = a; i<=b; i++) {
			System.out.print(i+" ");
		}
		System.out.println();
	}
	
	public void writeFile(String name, String data) throws IOException {
		File f = new File("src/main/"+name+".txt");
		if(!f.exists()) f.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/"+name+".txt"));
		writer.write(data);
		writer.newLine();
		writer.close();
		
	}
	
	public String readData(String path) {
		File file = new File("src/main/"+path+".txt");
        FileReader fileReader;
        String res = "";
		try {
			fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				res = line;
			}
			bufferedReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;

	}
	
	public String fetch(int pId) {
		if(this.memory.get(0) != null && (int)((Pair)this.memory.get(0)).getValue() == pId) {
			int pc = (int)((Pair)this.memory.get(1)).getValue();
//			this.memory.set(1, new Pair("PC",pc+1));
			if(this.memory.get(pc) != null && ((Pair)this.memory.get(pc)).getName().equals("instruction")) return (String)((Pair)this.memory.get(pc)).getValue();
		} else if (this.memory.get(4) != null && (int)((Pair)this.memory.get(4)).getValue() == pId) {
			int pc = (int)((Pair)this.memory.get(5)).getValue();
			if(this.memory.get(pc) != null && ((Pair)this.memory.get(pc)).getName().equals("instruction")) return (String)((Pair)this.memory.get(pc)).getValue();
//			this.memory.set(5, new Pair("PC",pc+1));
		}
		return "";
	}
	
	public void decodeAndExecute(String instr) {
		switch(instr.split(" ")[0]) {
		case "semWait": if(instr.split(" ")[1].equals("userInput")) {
							this.mutex.semWaitUserInput(Integer.parseInt(this.currRunning));
							if(this.getMutex().getUserInputBlockedQueue().contains(Integer.parseInt(this.currRunning))) {
								((Pair)(this.getMemory().get(this.getMemory().getProcessBounds(Integer.parseInt(this.getCurrRunning()))[0]+2))).setValue(ProcessState.BLOCKED);
								this.blockedQueue.offer(Integer.parseInt(this.currRunning));
								this.setCurrRunning(this.readyQueue.poll()+"");
							}
							this.mutex.setUserInputOwner(this.currRunning+"");
						}
						else if (instr.split(" ")[1].equals("userOutput")) {
							this.mutex.semWaitUserOutput(Integer.parseInt(this.currRunning));
							if(this.getMutex().getUserOutputBlockedQueue().contains(Integer.parseInt(this.currRunning))) {
								((Pair)(this.getMemory().get(this.getMemory().getProcessBounds(Integer.parseInt(this.getCurrRunning()))[0]+2))).setValue(ProcessState.BLOCKED);
								this.blockedQueue.offer(Integer.parseInt(this.currRunning));
								this.setCurrRunning(this.readyQueue.poll()+"");
							}
							this.mutex.setUserOutputOwner(this.currRunning+"");
						} 
						else {
							this.mutex.semWaitFile(Integer.parseInt(this.currRunning));
							if(this.getMutex().getFileBlockedQueue().contains(Integer.parseInt(this.currRunning))) {
								((Pair)(this.getMemory().get(this.getMemory().getProcessBounds(Integer.parseInt(this.getCurrRunning()))[0]+2))).setValue(ProcessState.BLOCKED);
								this.blockedQueue.offer(Integer.parseInt(this.currRunning));
								this.setCurrRunning(this.readyQueue.poll()+"");
							}
							this.mutex.setFileOwner(this.currRunning+"");
						} break;
		case "semSignal": if(instr.split(" ")[1].equals("userInput")) {
								Object pId = this.mutex.semSignalUserInput();
								if(pId != null) {
									this.readyQueue.offer((int)pId);
									this.blockedQueue.poll();
									if (!inDisk.equals(pId+"")) ((Pair)(this.getMemory().get(this.getMemory().getProcessBounds((int)pId)[0]+2))).setValue(ProcessState.READY);
									this.mutex.setUserInputOwner(pId+"");
								}
							}
						else if (instr.split(" ")[1].equals("userOutput")) {
							Object pId = this.mutex.semSignalUserOutput();
							if(pId != null) {
								this.readyQueue.offer((int)pId);
								this.blockedQueue.poll();
								((Pair)(this.getMemory().get(this.getMemory().getProcessBounds((int)pId)[0]+2))).setValue(ProcessState.READY);
								this.mutex.setUserOutputOwner(pId+"");
							}
						}
						else {
							Object pId = this.mutex.semSignalFile();
							if(pId != null) {
								this.readyQueue.offer((int)pId);
								this.blockedQueue.poll();
								((Pair)(this.getMemory().get(this.getMemory().getProcessBounds((int)pId)[0]+2))).setValue(ProcessState.READY);
								this.mutex.setFileOwner(pId+"");	
							}
						} break;
		case "printFromTo": this.printFromTo(this.memory.getVar(Integer.parseInt(this.currRunning), instr.split(" ")[1]), this.memory.getVar(Integer.parseInt(this.currRunning), instr.split(" ")[2])); break;
		case "print": this.print(instr.split(" ")[1]); break;
		case "writeFile": try {
				this.writeFile(instr.split(" ")[1], instr.split(" ")[2]);
			} catch (IOException e) {
				e.printStackTrace();
			} break;
		case "readFile": this.readData(instr.split(" ")[1]);
		case "assign": if (instr.split(" ")[2].equals("input")) {
							Scanner sc = new Scanner(System.in);
							System.out.println("Please enter a value");
							String x = sc.nextLine();
							if(instr.split(" ")[1].equals("a")) this.getMemory().setVar(Integer.parseInt(this.currRunning), "a", Integer.parseInt(x));
							else if(instr.split(" ")[1].equals("b")) this.getMemory().setVar(Integer.parseInt(this.currRunning), "b", Integer.parseInt(x));
							else this.getMemory().setVar(Integer.parseInt(this.currRunning), "tmp", Integer.parseInt(x));
						} else if (instr.split(" ")[2].equals("readFile")) {
							if(instr.split(" ")[1].equals("a")) this.getMemory().setVar(Integer.parseInt(this.currRunning), "a", Integer.parseInt(this.readData(instr.split(" ")[3])));
							else if(instr.split(" ")[1].equals("b")) this.getMemory().setVar(Integer.parseInt(this.currRunning), "b", Integer.parseInt(this.readData(instr.split(" ")[3])));
							else this.getMemory().setVar(Integer.parseInt(this.currRunning), "tmp", Integer.parseInt(this.readData(instr.split(" ")[3])));
						}
		case "": return;
		}
	}
	
	public int[] writeToDisk(int pId) { // writes process data to hard disk
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
		return bounds;
	}
	
	public void diskToMemory() {
		ArrayList<String> processLines = new ArrayList<>();
		FileReader fileReader;
		try {
			File f = new File("src/main/hardDisk.txt");
			fileReader = new FileReader(f);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				processLines.add(line);
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("lines: "+processLines);
//		System.out.println(this.memory);
		int pId = -1;
		int[] memBounds = new int[2];
		if(this.memory.get(2) != null && (ProcessState)((Pair)this.memory.get(2)).getValue() != ProcessState.RUNNING) {
			pId = (int)((Pair)this.memory.get(0)).getValue();
			memBounds = (int[])((Pair)this.memory.get(3)).getValue();
			this.writeToDisk(pId);
			this.setInDisk(pId+"");
//			System.out.println(this.memory.getLastIndex()+" <==");
			if(processLines.size() == 0) return;
			this.memory.set(0, new Pair("processId", Integer.parseInt(processLines.get(0).split(": ")[1])));
			this.memory.set(1, new Pair("PC", Integer.parseInt(processLines.get(1).split(": ")[1])));
			this.memory.set(2, new Pair("processState", ProcessState.READY));
			this.memory.set(3, new Pair("memoryBoundaries", memBounds));
			this.memory.set(memBounds[0], new Pair("var-a",processLines.get(4).split(": ")[1]));
			this.memory.set(memBounds[0]+1, new Pair("var-b",processLines.get(5).split(": ")[1]));
			this.memory.set(memBounds[0]+2, new Pair("var-tmp",processLines.get(6).split(": ")[1]));
			int j = 7;
			for(int i = memBounds[0]+3; i<=memBounds[1] && j < processLines.size(); i++) {
				this.memory.set(i, new Pair("instruction",processLines.get(j).split(": ")[1]));
				j++;
			}
			
		} else if (this.memory.get(6) != null && (ProcessState)((Pair)this.memory.get(6)).getValue() != ProcessState.RUNNING) {
//			System.out.println(this.memory.getLastIndex()+" <=");
			pId = (int)((Pair)this.memory.get(4)).getValue();
			memBounds = (int[])((Pair)this.memory.get(7)).getValue();
			this.writeToDisk(pId);
			this.setInDisk(pId+"");
			if(processLines.size() == 0) return;
			this.memory.set(4, new Pair("processId", Integer.parseInt(processLines.get(0).split(": ")[1])));
			this.memory.set(5, new Pair("PC", Integer.parseInt(processLines.get(1).split(": ")[1])));
			this.memory.set(6, new Pair("processState", ProcessState.READY));
			this.memory.set(7, new Pair("memoryBoundaries", memBounds));
			this.memory.set(memBounds[0], new Pair("var-a",processLines.get(4).split(": ")[1]));
			this.memory.set(memBounds[0]+1, new Pair("var-b",processLines.get(5).split(": ")[1]));
			this.memory.set(memBounds[0]+2, new Pair("var-tmp",processLines.get(6).split(": ")[1]));
			int j = 7;
			for(int i = memBounds[0]+3; i<=memBounds[1] && j < processLines.size(); i++) {
				this.memory.set(i, new Pair("instruction",processLines.get(j).split(": ")[1]));
				j++;
			}
		}
//		System.out.println(this.memory);
	}
	
	public static void main(String[] args) {
		String[] progs = {"program_1","program_2"};
		int[] arr = {0,1};
		Interpreter i = new Interpreter(2, progs, arr);
//		ArrayList<String> processLines = new ArrayList<>();
//		FileReader fileReader;
//		try {
//			File f = new File("src/main/hardDisk.txt");
//			fileReader = new FileReader(f);
//			BufferedReader bufferedReader = new BufferedReader(fileReader);
//			String line;
//			while ((line = bufferedReader.readLine()) != null) {
//				processLines.add(line);
//			}
//			bufferedReader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("lines: "+processLines);
	}
	

}
