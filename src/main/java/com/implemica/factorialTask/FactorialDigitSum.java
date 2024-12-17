package com.implemica.factorialTask;

import java.math.BigInteger;

public class FactorialDigitSum {
    public static void main(String[] args) {
        // Compute 100!
        BigInteger factorial = BigInteger.ONE;
        for (int i = 2; i <= 100; i++) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }

        // Convert the result to a string and calculate the sum of its digits
        String factorialStr = factorial.toString();
        int digitSum = 0;
        for (char digit : factorialStr.toCharArray()) {
            digitSum += Character.getNumericValue(digit);
        }

        // Print the result
        System.out.println("The sum of the digits in 100! is: " + digitSum);
    }
}
