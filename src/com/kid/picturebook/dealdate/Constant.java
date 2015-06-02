package com.kid.picturebook.dealdate;

public class Constant {
	public enum AudioType {
		BACKGROUND(0), CLICK(1);
		
		private AudioType(int i) {
			this.nativeInt = i;
		}
		
		public final int nativeInt;
		
	}
}
