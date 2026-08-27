package com.eventmanagement.util;

import java.util.Scanner;

public class ConsoleHelper {

    private final Scanner scanner;

    public ConsoleHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public void printLine(String message) {
        System.out.println(message);
    }

    public void printSeparator() {
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
    }

    public void printHeading(String heading) {
        System.out.println("======================================================");
        System.out.println(heading);
        System.out.println("======================================================");
    }
}
