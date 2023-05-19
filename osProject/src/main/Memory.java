package main;

public class Memory {
	private Object[] data = new Object[40];
	private int kernelSpaceIndex;
	private int lastIndex;

	public Memory() {
		this.lastIndex = 10;
		this.kernelSpaceIndex = 0;
	}
	public void addPCB(Pair pcb) {
		if(this.kernelSpaceIndex < 10) {
			this.data[this.kernelSpaceIndex] = pcb;
			this.kernelSpaceIndex++;
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
		for(int i = 0; i<10; i++) {
			if(((PCB) this.data[i]).getProcessId() == pId) {
				((PCB) this.data[i]).setPc(((PCB) this.data[i]).getPc()+1);
			}
		}
	}

	public String toString() {
		
		String r = "";
		r+= "-------------------------------------------------------------------------------------"+"\n"+"Memory"+"\n"+"------"+"\n";
		r+= "------------------------------------- kernel space -------------------------------------"+"\n";
		for(int i = 0; i<40; i++) {
			if (i == 10) r+= "------------------------------------- first space -------------------------------------"+"\n";
			if (i == 25) r+= "------------------------------------- second space ------------------------------------"+"\n";
			r+= i+": "+data[i]+"\n";
		}
		r+= "-------------------------------------------------------------------------------------";
		return r;
	}
	
	
}
