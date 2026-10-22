package com.burulai;

import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<Transaction> transactions = TransactionRepository.loadTransactions();

        int choice = 0;
       while (choice != 5) {
           System.out.println("===Finance Tracker===");
           System.out.println("1.Add income");
           System.out.println("2.Add expense");
           System.out.println("3. Show balance");
           System.out.println("4. Show transactions");
           System.out.println("5. Exit");


           System.out.print("Choose an option: ");

           try {
               choice = scanner.nextInt();
           } catch (java.util.InputMismatchException e) {
               System.out.println("Please enter a number from 1 to 5!");
               scanner.nextLine();
               continue;
           }

           switch (choice) {

               case 1 -> addTransaction(scanner, transactions, true);
               case 2 -> addTransaction(scanner, transactions, false);
               case 3 -> {
                   double balance = TransactionService.calculateBalance(transactions);
                   System.out.printf("Balance: %.2f%n", balance);
               }case 4 -> {
                   TransactionService.showTransactions(transactions);
               }
               case 5 -> System.out.println("Goodbye!");
               default -> System.out.println(
                       "Invalid option! Please choose from 1 to 5.");
           }
       }

    }



    public static void addTransaction(
            Scanner scanner,
            ArrayList<Transaction> transactions,
            boolean isIncome) {

        scanner.nextLine();

        if (isIncome) {
            System.out.print("Enter income description: ");
        } else {
            System.out.print("Enter expense description: ");
        }

        String description = scanner.nextLine();

        if (isIncome) {
            System.out.print("Enter income amount: ");
        } else {
            System.out.print("Enter expense amount: ");
        }


        double amount;

        while (true) {
            try {
                amount = scanner.nextDouble();
                scanner.nextLine();

                if (amount <= 0) {
                    System.out.println("Amount must be greater than 0!");
                    System.out.print("Enter amount again: ");
                    continue;
                }

                break;

            } catch (java.util.InputMismatchException e) {
                System.out.println("Please enter a valid number!");
                scanner.nextLine();
                System.out.print("Enter amount again: ");
            }
        }

        TransactionService.addTransaction(
                description, amount, isIncome, transactions);

    }
}