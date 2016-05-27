package me.lennartVH01.util;

public class ValuedObject<T> {
	int value;
	T object;
	
	public ValuedObject(int value, T object){
		this.value = value;
		this.object = object;
	}
	public void setValue(int value){this.value = value;}
	public int getValue(){return value;}
	public void setObject(T object){this.object = object;}
	public T getObject(){return object;}
}
