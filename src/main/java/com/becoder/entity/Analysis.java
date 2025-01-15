package com.becoder.entity;

public class Analysis {
	
	 public static int sum(int num1,int num2) {
		 return num1+num2;
	 }
	
public static void main(String[] args) {
	
Analysis analysis= new Analysis();
int total = analysis.sum(25, 26);
System.out.println(total);

}
}
