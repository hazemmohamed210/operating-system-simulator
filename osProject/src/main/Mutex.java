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
	private String userInputOwner;
	private String userOutputOwner;
	private String fileOwner;
	
	public Mutex() {
		userInputBlockedQueue = new ConcurrentLinkedQueue<>();
		userOutputBlockedQueue = new ConcurrentLinkedQueue<>();
		fileBlockedQueue = new ConcurrentLinkedQueue<>();
		userInput = true;
		userOutput = true;
		file = true;
		this.userInputOwner = "none";
		this.userOutputOwner = "none";
		this.fileOwner = "none";
	}
	
	public String getUserInputOwner() {
		return userInputOwner;
	}

	public void setUserInputOwner(String userInputOwner) {
		this.userInputOwner = userInputOwner;
	}

	public String getUserOutputOwner() {
		return userOutputOwner;
	}

	public void setUserOutputOwner(String userOutputOwner) {
		this.userOutputOwner = userOutputOwner;
	}

	public String getFileOwner() {
		return fileOwner;
	}

	public void setFileOwner(String fileOwner) {
		this.fileOwner = fileOwner;
	}

	public void semWaitUserInput(int pId) {
		if(!this.userInput && !this.userInputOwner.equals(pId+"")) this.userInputBlockedQueue.offer(pId);
		this.userInput = false;
	}
	
	public void semWaitUserOutput(int pId) {
		if(!this.userOutput && !this.userOutputOwner.equals(pId+"")) this.userOutputBlockedQueue.offer(pId);
		this.userOutput = false;
	}
	
	public void semWaitFile(int pId) {
		if(!this.file && !this.fileOwner.equals(pId+"")) this.fileBlockedQueue.offer(pId);
		this.file = false;
	}
	
	public Object semSignalUserInput() {
		if(this.userInputBlockedQueue.size() == 0) {
			this.userInputOwner = "none";
			this.userInput = true;
		}
		return this.userInputBlockedQueue.poll();
	}
	
	public Object semSignalUserOutput() {
		if(this.userOutputBlockedQueue.size() == 0) {
			this.userOutputOwner = "none";
			this.userOutput = true;
		}
		return this.userOutputBlockedQueue.poll();
	}
	
	public Object semSignalFile() {
		if(this.fileBlockedQueue.size() == 0) {
			this.fileOwner = "none";
			this.file = true;
		}
		return this.fileBlockedQueue.poll();
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
		System.out.println("---------------------- MUTEX -----------------------");
		System.out.println("userInput mutex: "+this.userInput+", Owner: process "+this.userInputOwner);
		System.out.println("userInput blocked queue: "+this.userInputBlockedQueue);
		System.out.println("userOutput mutex: "+this.userOutput+", Owner: process "+this.userOutputOwner);
		System.out.println("userOutput blocked queue: "+this.userOutputBlockedQueue);
		System.out.println("file mutex: "+this.file+", Owner: process "+this.fileOwner);
		System.out.println("file blocked queue: "+this.fileBlockedQueue);
		System.out.println("-------------------- MUTEX END ---------------------");
	}
}
