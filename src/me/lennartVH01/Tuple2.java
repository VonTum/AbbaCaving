package me.lennartVH01;

public class Tuple2<S,U>{
	S arg1;
	U arg2;
	public Tuple2(S arg1, U arg2){
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	public S getArg1() {
		return arg1;
	}
	public void setArg1(S arg1) {
		this.arg1 = arg1;
	}
	public U getArg2() {
		return arg2;
	}
	public void setArg2(U arg2) {
		this.arg2 = arg2;
	}
}
