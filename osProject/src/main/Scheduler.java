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
		while(true) {
			System.out.println("------------------------------------ clock cycle: "+this.clockCycle+" ------------------------------------");
			if(j < arrivalTimes.length && this.clockCycle == arrivalTimes[j]) {
				System.out.println(programs[j]+" arrived at time "+this.clockCycle);
				i.createProcess(programs[j]);
				i.getReadyQueue().offer(j);
			} else {
				j--;
			}
			System.out.println("ready queue: "+i.getReadyQueue());
			System.out.println("blocked queue: "+i.getBlockedQueue());
			System.out.println(i.getMemory());
			this.clockCycle++;
			System.out.println();
			j++;
			if(arrivalTimes.length != programs.length || programs.length == 0) {
				break;
			}
		}
	}
	
	public static void main(String[] args) {
//		Interpreter i = new Interpreter(2);
	}

}
