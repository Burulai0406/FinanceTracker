package com.burulai;

import java.io.*;
import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

public class TransactionRepository {
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
