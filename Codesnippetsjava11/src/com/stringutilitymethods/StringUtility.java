package com.stringutilitymethods;

public class StringUtility {

	public static void main(String[] args) {

		// check any non white space characters present in string
		System.out.println("-------------------isBlank Method----------------------------");
		System.out.println(" ".isBlank());

		// strip method

		System.out.println("------------------strip Methods-------------------------");

		System.out.println(" HCL ".strip());
		System.out.println(" HCL ".stripLeading());
		System.out.println(" HCL ".stripTrailing());

//replace white spaces
		System.out.println("---------------------Replace spaces in strip method-------------------------------");
		System.out.println(" H CL ".strip().replace(" ", "@"));
		System.out.println(" HCL ".stripLeading().replace(" ", "TECHNOLOGIES"));
		System.out.println(" HCL ".stripTrailing().replace(" ", "@"));

		// split a string in to multiple lines
		System.out.println("-------------------------Lines method---------------------------------------------");
		"step1\nstep2\nstep3\nstep4\nstep5".lines().forEach(System.out::println);

	}

}
