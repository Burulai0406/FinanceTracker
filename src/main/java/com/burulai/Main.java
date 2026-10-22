package com.burulai;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;



public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<Transaction> transactions = loadTransactions();

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

               case 1 -> {
                   addIncome(scanner,transactions);
               }
               case 2 -> {
                  addExpense(scanner, transactions);
               }
               case 3 -> {
                   double balance = calculateBalance(transactions);
                   System.out.println("Balance: " + balance);
               }case 4 -> {
                   showTransactions(transactions);
               }
               case 5 -> System.out.println("Goodbye!");
               default -> System.err.println("Invalid option!");
           }
       }



    }
    public static double calculateBalance(ArrayList<Transaction> transactions) {
        double balance = 0;

        for (Transaction transaction : transactions) {
            balance += transaction.getSignedAmount();
        }

        return balance;
    }

    public static void showTransactions(
            ArrayList<Transaction> transactions) {

        System.out.println("\n=== All Transactions ===");

        for (Transaction transaction : transactions) {
            System.out.println("Description: " + transaction.getDescription());
            System.out.println("Amount: " + transaction.getAmount());
            System.out.println("Type: " +
                    (transaction.isIncome() ? "Income" : "Expense"));
            System.out.println("--------------------");
        }
    }

    public static void addIncome(
            Scanner scanner,
            ArrayList<Transaction> transactions) {

        scanner.nextLine();

        System.out.print("Enter income description: ");
        String description = scanner.nextLine();

        System.out.print("Enter income amount: ");

        try {
            double amount = scanner.nextDouble();

            if (amount > 0) {
                Transaction income =
                        new Transaction(description, amount, true);

                transactions.add(income);
                saveTransactions(transactions);
                System.out.println("Income added successfully!");
            } else {
                System.out.println("Amount must be greater than 0!");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Please enter a valid number!");
            scanner.nextLine();
        }
    }
    public static void addExpense(
            Scanner scanner,
            ArrayList<Transaction> transactions) {

        scanner.nextLine();

        System.out.print("Enter expense description: ");
        String description = scanner.nextLine();

        System.out.print("Enter expense amount: ");

        try {
            double amount = scanner.nextDouble();

            if (amount > 0) {
                Transaction expense =
                        new Transaction(description, amount, false);

                transactions.add(expense);
                saveTransactions(transactions);
                System.out.println("Expense added successfully!");
            } else {
                System.out.println("Amount must be greater than 0!");
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Please enter a valid number!");
            scanner.nextLine();
        }
    }
    public static void saveTransactions(ArrayList<Transaction> transactions) {
        try (FileWriter writer = new FileWriter("transactions.txt")) {
            for (Transaction transaction : transactions) {
                writer.write(
                        transaction.getDescription() + ","
                                + transaction.getAmount() + ","
                                + transaction.isIncome() + "\n"
                );
            }

            System.out.println("Transactions saved!");
        } catch (IOException e) {
            System.out.println("Error saving transactions!");
        }
    }
    public static ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        File file = new File("transactions.txt");

        try {
            if (file.createNewFile()) {
                System.out.println("File created successfully.");
            }
        } catch (IOException e) {
            System.out.println("Could not create file.");
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader("transactions.txt"))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    System.out.println("Invalid transaction data: " + line);
                    continue;
                }

                String description = parts[0];
                double amount;

                try {
                    amount = Double.parseDouble(parts[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount: " + parts[1]);
                    continue;
                }
                if (amount <= 0) {
                    System.out.println("Invalid amount: " + parts[1]);
                    continue;
                }
                if (!parts[2].equals("true") && !parts[2].equals("false")) {
                    System.out.println("Invalid transaction type: " + parts[2]);
                    continue;
                }

                boolean isIncome = Boolean.parseBoolean(parts[2]);

                Transaction transaction =
                        new Transaction(description, amount, isIncome);

                transactions.add(transaction);
            }

        } catch (IOException e) {
            System.out.println("Could not load transactions.");
        }
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        }

        return transactions;
    }
}