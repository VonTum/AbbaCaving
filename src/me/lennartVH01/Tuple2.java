package me.lennartVH01;

public class Tuple2<T1,T2>{
	T1 arg1;
	T2 arg2;
	public Tuple2(T1 arg1, T2 arg2){
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	public T1 getArg1() {
		return arg1;
	}
	public void setArg1(T1 arg1) {
		this.arg1 = arg1;
	}
	public T2 getArg2() {
		return arg2;
	}
	public void setArg2(T2 arg2) {
		this.arg2 = arg2;
	}
}
