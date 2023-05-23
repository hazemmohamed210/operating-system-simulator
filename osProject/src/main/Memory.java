package main;

public class Memory {
	private Object[] data = new Object[40];
	private int kernelSpaceIndex;
	private int lastIndex;

	public Memory() {
		this.lastIndex = 8;
		this.kernelSpaceIndex = 0;
	}
	public void addPCB(PCB pcb) {
		if(this.kernelSpaceIndex < 8) {
			this.data[this.kernelSpaceIndex] = new Pair("processId",pcb.getProcessId());
			this.data[this.kernelSpaceIndex+1] = new Pair("PC",pcb.getPc());
			this.data[this.kernelSpaceIndex+2] = new Pair("processState",pcb.getProcessState());
			this.data[this.kernelSpaceIndex+3] = new Pair("memoryBoundaries", pcb.getMemBoundries());
			this.kernelSpaceIndex+= 4;
		}
	}
	
	public void setKernelSpaceIndex(int kernelSpaceIndex) {
		this.kernelSpaceIndex = kernelSpaceIndex;
	}
	public int getKernelSpaceIndex() {
		return kernelSpaceIndex;
	}

	public Object get(int index) {
		return data[index];
	}
	
	public void set(int i, Object o) {
		this.data[i] = o;
	}
	
	public int getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}
	
	public void add(Pair p) {
		if(this.lastIndex < 40) {
			this.data[this.lastIndex] = p;
			this.lastIndex++;
		}
	}
	
	public void updateProcessPC(int pId) {
		if(this.data[0] != null && (int)((Pair)this.data[0]).getValue() == pId) {
			int pc = (int)((Pair)this.data[1]).getValue();
			this.data[1] = new Pair("PC",pc+1);
		} else if (this.data[4] != null && (int)((Pair)this.data[4]).getValue() == pId) {
			int pc = (int)((Pair)this.data[5]).getValue();
			this.data[5] = new Pair("PC",pc+1);
		}
	}
	
	public int[] getProcessBounds(int pId) {
		int[] prcs = new int[4];
		for(int i = 0; i<8; i++) {
			Pair p = (Pair)this.data[i];
//			System.out.println("I = "+i);
			if(p != null && p.getName().equals("processId") && (int)p.getValue() == pId) {
				prcs[0] = i;
				prcs[1] = i+4;
				int[] tmp = (int[])(((Pair)this.data[i+3]).getValue());
				prcs[2] = tmp[0];
				prcs[3] = tmp[1];
				return prcs;
			}
		}
		return null;
	}

	public String toString() {
		
		String r = "";
		r+= "-------------------------------------------------------------------------------------"+"\n"+"Memory"+"\n"+"------"+"\n";
		r+= "------------------------------------- kernel space -------------------------------------"+"\n";
		for(int i = 0; i<40; i++) {
			if (i == 8) r+= "------------------------------------- first space -------------------------------------"+"\n";
			if (i == 24) r+= "------------------------------------- second space ------------------------------------"+"\n";
			r+= i+": "+data[i]+"\n";
		}
		r+= "-------------------------------------------------------------------------------------";
		return r;
	}
	
	
}
