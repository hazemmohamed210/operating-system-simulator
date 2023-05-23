package main;

//import java.util.Collection;
//import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Mutex {
	private boolean userInput;
	private Queue<Integer> userInputBlockedQueue;
	private boolean userOutput;
	private Queue<Integer> userOutputBlockedQueue;
	private boolean file;
	private Queue<Integer> fileBlockedQueue;
	
	public Mutex() {
		userInputBlockedQueue = new ConcurrentLinkedQueue<>();
		userOutputBlockedQueue = new ConcurrentLinkedQueue<>();
		fileBlockedQueue = new ConcurrentLinkedQueue<>();
		userInput = true;
		userOutput = true;
		file = true;
	}
	
	public void semWaitUserInput(int pId) {
		if(!this.userInput) this.userInputBlockedQueue.offer(pId);
		this.userInput = false;
	}
	
	public void semWaitUserOutput(int pId) {
		if(!this.userOutput) this.userOutputBlockedQueue.offer(pId);
		this.userOutput = false;
	}
	
	public void semWaitFile(int pId) {
		if(!this.file) this.fileBlockedQueue.offer(pId);
		this.file = false;
	}
	
	public void semSignalUserInput() {
		if(this.userInputBlockedQueue.size() == 0) {
			this.userInput = true;
		}
		this.userInputBlockedQueue.poll();
	}
	
	public void semSignalUserOutput() {
		if(this.userOutputBlockedQueue.size() == 0) {
			this.userInput = true;
		}
		this.userOutputBlockedQueue.poll();
	}
	
	public void semSignalFile() {
		if(this.fileBlockedQueue.size() == 0) {
			this.file = true;
		}
		this.fileBlockedQueue.poll();
	}

	public Queue<Integer> getUserInputBlockedQueue() {
		return userInputBlockedQueue;
	}

	public Queue<Integer> getUserOutputBlockedQueue() {
		return userOutputBlockedQueue;
	}

	public Queue<Integer> getFileBlockedQueue() {
		return fileBlockedQueue;
	}
	
	public void printMutex() {
		System.out.println("userInput mutex: "+this.userInput);
		System.out.println("userInput blocked queue: "+this.userInputBlockedQueue);
		System.out.println("userOutput mutex: "+this.userOutput);
		System.out.println("userOutput blocked queue: "+this.userOutputBlockedQueue);
		System.out.println("file mutex: "+this.file);
		System.out.println("file blocked queue: "+this.fileBlockedQueue);
	}
}
