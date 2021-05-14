package com.localvariabletypeinference;

import java.util.List;

public class Typeinferences {

	public static void main(String[] args) {
		List<String> names1 = List.of("laptop", "HP");
		List<String> names2 = List.of("mobile", "oneplus");
		// List<List<String>> names = List.of(names1, names2);
		var names = List.of(names1, names2);
		names.stream().forEach(System.out::println);

	}

}
