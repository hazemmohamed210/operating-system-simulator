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
	private boolean doublecycle;
	private String assignVal;
	private String doubleCycleProcess;
	
	public Interpreter(int timeSlice, String[] programs, int[] arrivalTimes) {
		this.readyQueue = new ConcurrentLinkedQueue<>();
		this.blockedQueue = new ConcurrentLinkedQueue<>();
		this.mutex = new Mutex();
		this.memory = new Memory();
		this.scheduler = new Scheduler(timeSlice);
		this.currRunning = "none";
		this.inDisk = "none";
		this.doublecycle = false;
		this.assignVal = "";
		this.doubleCycleProcess = "none";
		this.scheduler.Schedule(this, programs, arrivalTimes);
	}
	
	public String getDoubleCycleProcess() {
		return doubleCycleProcess;
	}

	public void setDoubleCycleProcess(String doubleCycleProcess) {
		this.doubleCycleProcess = doubleCycleProcess;
	}

	public boolean isDoublecycle() {
		return doublecycle;
	}

	public void setDoublecycle(boolean doublecycle) {
		this.doublecycle = doublecycle;
	}

	public String getAssignVal() {
		return assignVal;
	}

	public void setAssignVal(String assignVal) {
		this.assignVal = assignVal;
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

	public void createProcess(String programName, int id) {
		try {
            File file = new File("src/"+programName+".txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            int start = (this.memory.getLastIndex() == 40)? 24:this.memory.getLastIndex();
            if(this.memory.getLastIndex() == 40) {
            	int outId = -1000;
            	if((ProcessState)((Pair)this.memory.get(2)).getValue() != ProcessState.RUNNING) {
            		outId = (int)((Pair)this.memory.get(0)).getValue();
            		this.memory.setKernelSpaceIndex(0);
            	} else {
            		outId = (int)((Pair)this.memory.get(4)).getValue();
            		this.memory.setKernelSpaceIndex(4);
            	}
            	int[] bounds = this.writeToDisk(outId);
            	this.inDisk = outId+"";
            	while ((line = bufferedReader.readLine()) != null) {
                	if(this.memory.getLastIndex() == 40) {
                		this.memory.setLastIndex(bounds[2]);
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
            	this.memory.setLastIndex(40);
            	int[] boundss = {bounds[2], bounds[3]};
            	this.memory.addPCB(new PCB(id, bounds[2]+3, ProcessState.READY, boundss));
            	this.memory.setKernelSpaceIndex(8);
            	System.out.println("swapping: process "+id+" going in memory and process "+outId+" going out to the hard disk");
            } else {
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
	
	// ------------------------------------------- System Calls --------------------------------------------------------------------------
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
			e.printStackTrace();
		}
		return res;

	}
	
	public String fetch(int pId) {
		if(this.memory.get(0) != null && (int)((Pair)this.memory.get(0)).getValue() == pId) {
			int limit = ((int[])((Pair)this.memory.get(3)).getValue())[1]+1;
			int pc = (int)((Pair)this.memory.get(1)).getValue();
			if(pc > limit) return "none";
			if(this.memory.get(pc) != null && ((Pair)this.memory.get(pc)).getName().equals("instruction")) return (String)((Pair)this.memory.get(pc)).getValue();
		} else if (this.memory.get(4) != null && (int)((Pair)this.memory.get(4)).getValue() == pId) {
			int limit = ((int[])((Pair)this.memory.get(7)).getValue())[1]+1;
			int pc = (int)((Pair)this.memory.get(5)).getValue();
			if(pc > limit) return "none";
			if(this.memory.get(pc) != null && ((Pair)this.memory.get(pc)).getName().equals("instruction")) return (String)((Pair)this.memory.get(pc)).getValue();
		}
		return "";
	}
	
	public static boolean canParseToInt(String input) {
	    try {
	        Integer.parseInt(input);
	        return true;
	    } catch (NumberFormatException e) {
	        return false;
	    }
	}
	
	public void assign(String instr, String s) {
		if(instr.split(" ")[1].equals("a")) {
			if(canParseToInt(s)) this.getMemory().setVar(Integer.parseInt(this.currRunning), "a", Integer.parseInt(s));
			else this.getMemory().setVar(Integer.parseInt(this.currRunning), "a", s);
		}
		else if(instr.split(" ")[1].equals("b")) {
			if(canParseToInt(s)) this.getMemory().setVar(Integer.parseInt(this.currRunning), "b", Integer.parseInt(s));
			else this.getMemory().setVar(Integer.parseInt(this.currRunning), "b", s);
		}
		else {
			if(canParseToInt(s)) this.getMemory().setVar(Integer.parseInt(this.currRunning), "tmp", Integer.parseInt(s));
			else this.getMemory().setVar(Integer.parseInt(this.currRunning), "tmp", s);
		}
	}
	
	public void decodeAndExecute(String instr) {
		switch(instr.split(" ")[0]) {
		case "semWait": if(instr.split(" ")[1].equals("userInput")) {
							this.mutex.semWaitUserInput(Integer.parseInt(this.currRunning));
							if(this.getMutex().getUserInputBlockedQueue().contains(Integer.parseInt(this.currRunning))) {
								((Pair)(this.getMemory().get(this.getMemory().getProcessBounds(Integer.parseInt(this.getCurrRunning()))[0]+2))).setValue(ProcessState.BLOCKED);
								this.blockedQueue.offer(Integer.parseInt(this.currRunning));
								this.getMemory().updateProcessPC(Integer.parseInt(this.getCurrRunning()));
								this.setCurrRunning(this.readyQueue.poll()+"");
							}
							this.mutex.setUserInputOwner(this.currRunning+"");
						}
						else if (instr.split(" ")[1].equals("userOutput")) {
							this.mutex.semWaitUserOutput(Integer.parseInt(this.currRunning));
							if(this.getMutex().getUserOutputBlockedQueue().contains(Integer.parseInt(this.currRunning))) {
								((Pair)(this.getMemory().get(this.getMemory().getProcessBounds(Integer.parseInt(this.getCurrRunning()))[0]+2))).setValue(ProcessState.BLOCKED);
								this.blockedQueue.offer(Integer.parseInt(this.currRunning));
								this.getMemory().updateProcessPC(Integer.parseInt(this.getCurrRunning()));
								this.setCurrRunning(this.readyQueue.poll()+"");
							}
							this.mutex.setUserOutputOwner(this.currRunning+"");
						} 
						else {
							this.mutex.semWaitFile(Integer.parseInt(this.currRunning));
							if(this.getMutex().getFileBlockedQueue().contains(Integer.parseInt(this.currRunning))) {
								((Pair)(this.getMemory().get(this.getMemory().getProcessBounds(Integer.parseInt(this.getCurrRunning()))[0]+2))).setValue(ProcessState.BLOCKED);
								this.blockedQueue.offer(Integer.parseInt(this.currRunning));
								this.getMemory().updateProcessPC(Integer.parseInt(this.getCurrRunning()));
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
								if (!inDisk.equals(pId+"")) ((Pair)(this.getMemory().get(this.getMemory().getProcessBounds((int)pId)[0]+2))).setValue(ProcessState.READY);
								this.mutex.setUserOutputOwner(pId+"");
							}
						}
						else {
							Object pId = this.mutex.semSignalFile();
							if(pId != null) {
								this.readyQueue.offer((int)pId);
								this.blockedQueue.poll();
								if (!inDisk.equals(pId+"")) ((Pair)(this.getMemory().get(this.getMemory().getProcessBounds((int)pId)[0]+2))).setValue(ProcessState.READY);
								this.mutex.setFileOwner(pId+"");	
							}
						} break;
		case "printFromTo": this.printFromTo(this.memory.getVar(Integer.parseInt(this.currRunning), instr.split(" ")[1]), this.memory.getVar(Integer.parseInt(this.currRunning), instr.split(" ")[2])); 
		break;
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
							this.assignVal = x;
							this.doublecycle = true;
							this.doubleCycleProcess = ""+this.currRunning;
						} else if (instr.split(" ")[2].equals("readFile")) {
							String s = this.readData(instr.split(" ")[3]);
							this.assignVal = s;
							this.doublecycle = true;
							this.doubleCycleProcess = ""+this.currRunning;
						}
		case "": return;
		}
	}
	
	public int[] writeToDisk(int pId) {
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
		int pId = -1;
		int[] memBounds = new int[2];
		if(this.memory.get(2) != null && (ProcessState)((Pair)this.memory.get(2)).getValue() != ProcessState.RUNNING) {
			pId = (int)((Pair)this.memory.get(0)).getValue();
			memBounds = (int[])((Pair)this.memory.get(3)).getValue();
			this.writeToDisk(pId);
			this.setInDisk(pId+"");
			if(processLines.size() == 0) return;
			int pc = Integer.parseInt(processLines.get(1).split(": ")[1]);
			if(pc < 23) {
				pc = pc - 11;
			} else {
				pc = pc - 27;
			}
			pc = pc + memBounds[0] + 3;
			this.memory.set(0, new Pair("processId", Integer.parseInt(processLines.get(0).split(": ")[1])));
			this.memory.set(1, new Pair("PC", pc));
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
			System.out.println("swapping: process "+Integer.parseInt(processLines.get(0).split(": ")[1])+" going in memory and process "+pId+" going out to the hard disk");
			
		} else if (this.memory.get(6) != null && (ProcessState)((Pair)this.memory.get(6)).getValue() != ProcessState.RUNNING) {
			pId = (int)((Pair)this.memory.get(4)).getValue();
			memBounds = (int[])((Pair)this.memory.get(7)).getValue();
			this.writeToDisk(pId);
			this.setInDisk(pId+"");
			int pc = Integer.parseInt(processLines.get(1).split(": ")[1]);
			if(pc < 23) {
				pc = pc - 11;
			} else {
				pc = pc - 27;
			}
			pc = pc + memBounds[0] + 3;
			if(processLines.size() == 0) return;
			this.memory.set(4, new Pair("processId", Integer.parseInt(processLines.get(0).split(": ")[1])));
			this.memory.set(5, new Pair("PC", pc));
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
			System.out.println("swapping: process "+Integer.parseInt(processLines.get(0).split(": ")[1])+" going in memory and process "+pId+" going out to the hard disk");
		}
	}
	
	public static void main(String[] args) {
		String[] progs = {"program_1","program_2","program_3"};
		int[] arr = {0,1,4};
		Interpreter i = new Interpreter(3, progs, arr);
	}
	

}
