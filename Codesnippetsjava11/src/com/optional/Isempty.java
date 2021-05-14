package com.optional;

import java.util.Optional;

public class Isempty {

	public static void main(String[] args) {

		Optional<String> str = Optional.empty();

		// in java8
		/*
		 * if(!str.isPresent()) {
		 * 
		 * }else {
		 * 
		 * }
		 */
		if (str.isEmpty()) {
			System.out.println("string is empty");

		}

	}
}