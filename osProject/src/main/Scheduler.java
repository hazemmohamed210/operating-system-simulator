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
			if(timeSliceCounter >= this.timeSlice) {
				System.out.println("time slice finished !!!");
				if(i.getReadyQueue().isEmpty()) timeSliceCounter = 0;
				else {
					timeSliceCounter = 0;
					String prevRunning = i.getCurrRunning();
					String process = i.getReadyQueue().poll()+"";
					i.setCurrRunning(process);
					if(i.getInDisk().equals(process)) {
						if(((Pair)(i.getMemory().get( i.getMemory().getProcessBounds(Integer.parseInt(prevRunning))[0]+2))).getValue() != ProcessState.FINISHED) {
							i.getReadyQueue().offer(Integer.parseInt(prevRunning));
							((Pair)(i.getMemory().get( i.getMemory().getProcessBounds(Integer.parseInt(prevRunning))[0]+2))).setValue(ProcessState.READY);
						}
						i.diskToMemory();
					}
					else if (((Pair)(i.getMemory().get( i.getMemory().getProcessBounds(Integer.parseInt(prevRunning))[0]+2))).getValue() != ProcessState.FINISHED) {
						i.getReadyQueue().offer(Integer.parseInt(prevRunning));
						((Pair)(i.getMemory().get( i.getMemory().getProcessBounds(Integer.parseInt(prevRunning))[0]+2))).setValue(ProcessState.READY);
					}
					((Pair)(i.getMemory().get( i.getMemory().getProcessBounds(Integer.parseInt(i.getCurrRunning()))[0]+2))).setValue(ProcessState.RUNNING);
				}
			}
			if (finished < programs.length) {
				String name = programs[Integer.parseInt(i.getCurrRunning())-1];
				System.out.println("Process currently running: "+name);
				System.out.println("executed instruction: "+i.fetch(Integer.parseInt(i.getCurrRunning())));
				String prevRunning = i.getCurrRunning();
				i.decodeAndExecute(i.fetch(Integer.parseInt(i.getCurrRunning())));
				boolean blockOccurred = false;
				if(i.getBlockedQueue().contains(Integer.parseInt(prevRunning))) {
					timeSliceCounter = -1;
					blockOccurred = true;
					if(i.getInDisk().equals(i.getCurrRunning())) {
						i.diskToMemory();
					}
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
			System.out.println(i.getMemory());
			timeSliceCounter++;
			this.clockCycle++;
			System.out.println();
			j++;
			if(arrivalTimes.length != programs.length || programs.length == 0 || finished == programs.length) {
				System.out.println("all programs finished execution successfully :)");
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		
	}

}
