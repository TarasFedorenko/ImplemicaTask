package com.implemica.bracketTask;

import java.math.BigInteger;
import java.util.Scanner;

public class NumberBracketTask {

    // Main method to run the program
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt user to enter the number of bracket pairs
        System.out.print("Enter N (number of bracket pairs): ");
        int n = scanner.nextInt();

        // Validate that N is a non-negative integer
        if (n < 0) {
            System.out.println("N must be a non-negative integer.");
            return;
        }

        // Calculate and display the number of valid bracket expressions
        System.out.println("Number of valid bracket expressions: " + calculateCatalan(n));
    }

    // Method to calculate the n-th Catalan number
    private static BigInteger calculateCatalan(int n) {
        // Numerator is the factorial of 2n
        BigInteger numerator = factorial(2 * n);
        // Denominator is the factorial of (n+1) multiplied by the factorial of n
        BigInteger denominator = factorial(n + 1).multiply(factorial(n));

        // Catalan number is the division of the numerator by the denominator
        return numerator.divide(denominator);
    }

    // Method to calculate the factorial of a given number
    private static BigInteger factorial(int num) {
        BigInteger result = BigInteger.ONE;
        // Multiply numbers from 2 to num to calculate the factorial
        for (int i = 2; i <= num; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}

