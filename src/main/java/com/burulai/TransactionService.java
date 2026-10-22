package com.burulai;

import java.util.ArrayList;

public class TransactionService {

    public static double calculateBalance(
            ArrayList<Transaction> transactions) {

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


    public static void addTransaction(
            String description,
            double amount,
            boolean isIncome,
            ArrayList<Transaction> transactions) {

        if (amount > 0) {
            Transaction transaction =
                    new Transaction(description, amount, isIncome);

            transactions.add(transaction);
            TransactionRepository.saveTransactions(transactions);

            System.out.println("Transaction added successfully!");
        } else {
            System.out.println("Amount must be greater than 0!");
        }
    }


}
