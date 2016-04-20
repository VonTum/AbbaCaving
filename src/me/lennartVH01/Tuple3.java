package me.lennartVH01;

public class Tuple3<S, U, V>{
	S arg1;
	U arg2;
	V arg3;
	public Tuple3(S arg1, U arg2, V arg3){
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
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
	public V getArg3() {
		return arg3;
	}
	public void setArg3(V arg3) {
		this.arg3 = arg3;
	}
}
