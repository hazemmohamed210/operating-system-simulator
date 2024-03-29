package main;

public class Pair {
	private String name;
	private Object value;
	
	
	public Pair(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		return this.name+": "+this.value;
	}
}
