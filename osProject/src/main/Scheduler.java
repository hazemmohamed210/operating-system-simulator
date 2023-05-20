package main;

// 1 clk cycle = 1 instruction 

// prog 1 arrives at 0
// prog 1 becomes running at 0
// prog 1 executes instr 1 at 0
// prog 2 arrives at 1
// prog 2 enters ready queue as prog 1 is running at 1
// prog 1 executes instr 2 at time 1
// prog 1 goes to the ready queue at 2 ---------------- process 1 to the disk file ----------------------------
// prog 2 becomes running at 2
// prog 2 executes instr 1 at 2



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
		while(true) {
			System.out.println("------------------------------------ clock cycle: "+this.clockCycle+" ------------------------------------");
			if(j < arrivalTimes.length && this.clockCycle == arrivalTimes[j]) {
				System.out.println(programs[j]+" arrived at time "+this.clockCycle);
				i.createProcess(programs[j], j);
				i.getReadyQueue().offer(j);
			} else {
				j--;
			}
			System.out.println("ready queue: "+i.getReadyQueue());
			System.out.println("blocked queue: "+i.getBlockedQueue());
			if (this.clockCycle == 1) i.writeToDisk(j);
			System.out.println(i.getMemory());
			this.clockCycle++;
			System.out.println();
			j++;
			if(arrivalTimes.length != programs.length || programs.length == 0 || this.clockCycle == 2) {
				break;
			}
		}
	}
	
	public static void main(String[] args) {
//		Interpreter i = new Interpreter(2);
	}

}
