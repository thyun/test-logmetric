package com.skp.testutil;

import java.util.ArrayList;
import java.util.List;

public class Box<T> {

    private T t;          

    public void set(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public <U extends Number> void inspect(U u){
        System.out.println("T: " + t.getClass().getName());
        System.out.println("U: " + u.getClass().getName());
    }
    
    public <U extends Number> void inspectList1(List<U> list){
    	for (U u : list) {
    		System.out.println("<inspectList1> has: " + u);
    	}
    }
    
    public void inspectList2(List<? extends Number> list){
    	for (Number n : list) {
    		System.out.println("<inspectList2> has: " + n);
    	}
    }

    public static void main(String[] args) {
    	System.out.println("running");
        Box<Integer> integerBox = new Box<Integer>();
        integerBox.set(new Integer(10));
        
        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);
        intList.add(3);
        integerBox.inspectList1(intList);
        integerBox.inspectList2(intList);
        
        Integer[] intArray = new Integer[10];
        for (Integer i : intArray) {
        	System.out.println("intArray has: " + i);
        }
    }
}
