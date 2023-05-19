package main;

public class PCB {
	private int processId;
	private int pc;
	private ProcessState processState;
	private int[] memBoundries = new int[2];

	public PCB(int processId, int pc, ProcessState processState, int[] memBoundries) {
		super();
		this.processId = processId;
		this.pc = pc;
		this.processState = processState;
		this.memBoundries = memBoundries;
	}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public ProcessState getProcessState() {
		return processState;
	}
	public void setProcessState(ProcessState processState) {
		this.processState = processState;
	}
	public int getProcessId() {
		return processId;
	}
	public int[] getMemBoundries() {
		return memBoundries;
	}
	
	public String toString() {
		return "(processId: "+this.processId+", PC: "+this.pc+", processState: "+this.processState+", memoryBounds: ("+memBoundries[0]+","+memBoundries[1]+"))";
	}
	
	
}
