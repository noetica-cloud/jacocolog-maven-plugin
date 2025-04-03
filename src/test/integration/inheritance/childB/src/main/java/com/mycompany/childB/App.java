package com.mycompany.childB;

/**
 * Hello world!
 */
public class App {

    private static final String MESSAGE = "Sum : ";

    public App() {}

    public static void main(String[] args) {
        System.out.println(MESSAGE + String.format("%d + %d = %d", 5, 2,sum(5, 2)));
    }

    public static int sum(int a, int b) {
        return a + b;
    }
}
