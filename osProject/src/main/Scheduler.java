package main;

public class Scheduler {
	private int clockCycle;
	private int timeSlice;
	
	public Scheduler(int timeSlice) {
		this.timeSlice = timeSlice;
		this.clockCycle = 0;
	}
	public int getClockCycle() {
		return clockCycle;
	}

	public void setClockCycle(int clockCycle) {
		this.clockCycle = clockCycle;
	}

	public int getTimeSlice() {
		return timeSlice;
	}

	public void setTimeSlice(int timeSlice) {
		this.timeSlice = timeSlice;
	}

	public void Schedule(Interpreter i, String[] programs, int[] arrivalTimes) {
		int j = 0;
		int finished = 0;
		int timeSliceCounter = 0;
		while(true) {
			System.out.println("------------------------------------ clock cycle: "+this.clockCycle+" ------------------------------------");
			if(j < arrivalTimes.length && this.clockCycle == arrivalTimes[j]) {
				System.out.println(programs[j]+" arrived at time "+this.clockCycle);
				i.createProcess(programs[j], j+1);
				if(i.getReadyQueue().size() == 0 && i.getCurrRunning().equals("none")) {
					i.setCurrRunning((j+1)+"");
					((Pair)(i.getMemory().get(2))).setValue(ProcessState.RUNNING);
				} else {
					i.getReadyQueue().offer(j+1);
				}
			} else {
				j--;
			} 
			// increment time slice counter
			if(timeSliceCounter >= this.timeSlice) {
				System.out.println("time slice finished !!!");
				if(i.getReadyQueue().isEmpty()) timeSliceCounter = 0;
				else {
					timeSliceCounter = 0;
					String prevRunning = i.getCurrRunning();
//					System.out.println("hello "+i.getCurrRunning());
//					i.writeToDisk(Integer.parseInt(i.getCurrRunning()));
					String process = i.getReadyQueue().poll()+"";
					i.setCurrRunning(process);
					System.out.println(i.getReadyQueue() + "<----------------------------------------");
					if(i.getInDisk().equals(process)) {
						i.diskToMemory();
					}
					else if (((Pair)(i.getMemory().get( i.getMemory().getProcessBounds(Integer.parseInt(prevRunning))[0]+2))).getValue() != ProcessState.FINISHED) {
						i.getReadyQueue().offer(Integer.parseInt(prevRunning));
						((Pair)(i.getMemory().get( i.getMemory().getProcessBounds(Integer.parseInt(prevRunning))[0]+2))).setValue(ProcessState.READY);
					}
					System.out.println(i.getReadyQueue() + "<----------------------------------------");
//					System.out.println(i.getInDisk()+" kkkkkkkkkkkkkkkk");
//					System.out.println(i.getCurrRunning()+" kkkkkkkkkkkkkkkk");
					((Pair)(i.getMemory().get( i.getMemory().getProcessBounds(Integer.parseInt(i.getCurrRunning()))[0]+2))).setValue(ProcessState.RUNNING);
					System.out.println(i.getReadyQueue() + "<----------------------------------------");
				}
			}
			// execute an instruction
			if (finished < programs.length) {
				System.out.println(i.getReadyQueue() + "<<----------------------------------------");
				System.out.println("Process currently running: "+"program_"+i.getCurrRunning());
				System.out.println("execute instruction: "+i.fetch(Integer.parseInt(i.getCurrRunning())));
				String prevRunning = i.getCurrRunning();
//				System.out.println("CURRENTLY RUNNING ---------------------------------- "+i.getCurrRunning());
				System.out.println(i.getReadyQueue() +" " + i.getCurrRunning()+ "<**----------------------------------------");
				i.decodeAndExecute(i.fetch(Integer.parseInt(i.getCurrRunning())));
				System.out.println(i.getReadyQueue() +" " + i.getCurrRunning()+ "<**----------------------------------------");
				boolean blockOccurred = false;
				if(i.getBlockedQueue().contains(Integer.parseInt(prevRunning))) {
					timeSliceCounter = this.timeSlice;
					blockOccurred = true;
					if(i.getInDisk().equals(i.getCurrRunning())) {
						i.diskToMemory();
					}
					System.out.println(i.getReadyQueue() + "<<<----------------------------------------");
//					System.out.println(i.getCurrRunning());
					((Pair)(i.getMemory().get( i.getMemory().getProcessBounds(Integer.parseInt(i.getCurrRunning()))[0]+2))).setValue(ProcessState.RUNNING);
				} else {
					blockOccurred = false;
				}
				if(!blockOccurred) i.getMemory().updateProcessPC(Integer.parseInt(i.getCurrRunning()));
			}
			if(i.fetch(Integer.parseInt(i.getCurrRunning())).equals("")) {
				finished++;
				timeSliceCounter = this.timeSlice;
				((Pair)(i.getMemory().get(i.getMemory().getProcessBounds(Integer.parseInt(i.getCurrRunning()))[0]+2))).setValue(ProcessState.FINISHED);
				if (i.getReadyQueue().contains(Integer.parseInt(i.getCurrRunning()))) i.getReadyQueue().remove(Integer.parseInt(i.getCurrRunning()));
			} 
			i.getMutex().printMutex();
			System.out.println("ready queue: "+i.getReadyQueue());
			System.out.println("blocked queue: "+i.getBlockedQueue());
//			if (this.clockCycle == 1) i.writeToDisk(j);
			System.out.println(i.getMemory());
			System.out.println("last index: "+i.getMemory().getLastIndex());
			timeSliceCounter++;
			this.clockCycle++;
			System.out.println();
			j++;
			if(arrivalTimes.length != programs.length || programs.length == 0 || finished == programs.length) { //  || this.clockCycle == 12
				System.out.println("all programs finished execution successfully :)");
				break;
			}
		}
	}
	
	public static void main(String[] args) {
//		Interpreter i = new Interpreter(2);
	}

}
