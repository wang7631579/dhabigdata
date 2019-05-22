package com.dha.dhabigdata.servlet;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.Test;

public class TestJava8 {

	@Test
	public void test() {
		Consumer<String> aa = new Consumer<String>() {

			@Override
			public void accept(String t) {
				System.out.println(t);
			}
		};
		aa.accept("1");
		Consumer<String> bb = str -> System.out.println(str);
		bb.accept("2");
		Consumer<String> cc = System.out::println;
		cc.accept("3");

		Supplier<String> dd = new Supplier<String>() {
			@Override
			public String get() {

				return "aaaaa";
			}
		};
		
		System.out.println("Supplier  "+dd.get());
		
		Supplier<String> mm= ()-> {return "dd";};
		System.out.println("Supplier  "+mm.get());
		Stu stu = new Stu();
		Supplier<String> hh= stu::getName;
		System.out.println("Supplier  "+hh.get());
		
		
		Supplier<Stu> ll = Stu::new;
		System.out.println("Supplier  "+ll.get());
	}
}

class Stu {
	String name;
	String age;

	public Stu() {
	}

	public Stu(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
