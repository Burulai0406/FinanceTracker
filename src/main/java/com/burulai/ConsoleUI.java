package com.burulai;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);
    private final TransactionService service;

    public ConsoleUI(TransactionService service) {
        this.service = service;
    }

    public void run() {
        int choice = 0;

        while (choice != 8) {
            printMenu();
            choice = readInt("Choose an option: ");

            switch (choice) {
                case 1 -> addTransaction(TransactionType.INCOME);
                case 2 -> addTransaction(TransactionType.EXPENSE);
                case 3 -> System.out.printf("Balance: %.2f%n", service.calculateBalance());
                case 4 -> showTransactions();
                case 5 -> showExpensesByCategory();
                case 6 -> showMonthlySummary();
                case 7 -> deleteTransaction();
                case 8 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid option! Please choose from 1 to 8.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===Finance Tracker===");
        System.out.println("1. Add income");
        System.out.println("2. Add expense");
        System.out.println("3. Show balance");
        System.out.println("4. Show transactions");
        System.out.println("5. Show expenses by category");
        System.out.println("6. Show monthly summary");
        System.out.println("7. Delete transaction");
        System.out.println("8. Exit");
    }

    private void addTransaction(TransactionType type) {
        String label = type == TransactionType.INCOME ? "income" : "expense";

        String description = readDescription("Enter " + label + " description: ");
        Category category = chooseCategory();
        double amount = readPositiveAmount("Enter " + label + " amount: ");
        LocalDate date = readDate();

        service.addTransaction(description, amount, type, category, date);
        System.out.println("Transaction added successfully!");
    }

    private void showTransactions() {
        List<Transaction> transactions = service.getTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }

        System.out.println("\n=== All Transactions ===");
        printTransactions(transactions);
    }

    private void printTransactions(List<Transaction> transactions) {
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            System.out.println((i + 1) + ". " + t.getDescription());
            System.out.println("   Amount:   " + t.getAmount());
            System.out.println("   Type:     " + (t.isIncome() ? "Income" : "Expense"));
            System.out.println("   Category: " + t.getCategory());
            System.out.println("   Date:     " + t.getDate());
            System.out.println("--------------------");
        }
    }

    private void showExpensesByCategory() {
        System.out.println("\n=== Expenses by category ===");

        for (Category category : Category.values()) {
            double total = service.calculateExpenseByCategory(category);
            System.out.printf("%-15s %.2f%n", category, total);
        }

        System.out.println("--------------------");
    }

    private void showMonthlySummary() {
        YearMonth month = readMonth();

        double income = service.calculateTotalByMonth(month, TransactionType.INCOME);
        double expense = service.calculateTotalByMonth(month, TransactionType.EXPENSE);

        System.out.println("\n=== Summary for " + month + " ===");
        System.out.printf("Income:  %.2f%n", income);
        System.out.printf("Expense: %.2f%n", expense);
        System.out.printf("Balance: %.2f%n", income - expense);
        System.out.println("--------------------");
    }

    private void deleteTransaction() {
        List<Transaction> transactions = service.getTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions to delete.");
            return;
        }

        System.out.println("\n=== All Transactions ===");
        printTransactions(transactions);

        int number = readInt("Enter number to delete (0 = cancel): ");

        if (number == 0) {
            System.out.println("Cancelled.");
        } else if (service.deleteTransaction(number - 1)) {
            System.out.println("Transaction deleted.");
        } else {
            System.out.println("No transaction with that number!");
        }
    }

    private Category chooseCategory() {
        Category[] categories = Category.values();

        System.out.println("Choose category:");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }

        while (true) {
            int number = readInt("Category number: ");

            if (number >= 1 && number <= categories.length) {
                return categories[number - 1];
            }

            System.out.println("Please enter a number from 1 to " + categories.length + "!");
        }
    }

    private String readDescription(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();

            if (!text.isEmpty()) {
                return text;
            }

            System.out.println("Description cannot be empty!");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number!");
            }
        }
    }

    private double readPositiveAmount(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                double amount = Double.parseDouble(scanner.nextLine().trim());

                if (amount > 0) {
                    return amount;
                }

                System.out.println("Amount must be greater than 0!");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private LocalDate readDate() {
        while (true) {
            System.out.print("Date (yyyy-MM-dd, Enter = today): ");
            String text = scanner.nextLine().trim();

            if (text.isEmpty()) {
                return LocalDate.now();
            }

            try {
                return LocalDate.parse(text);
            } catch (DateTimeParseException e) {
                System.out.println("Wrong format! Example: 2026-10-23");
            }
        }
    }

    private YearMonth readMonth() {
        while (true) {
            System.out.print("Month (yyyy-MM, Enter = current): ");
            String text = scanner.nextLine().trim();

            if (text.isEmpty()) {
                return YearMonth.now();
            }

            try {
                return YearMonth.parse(text);
            } catch (DateTimeParseException e) {
                System.out.println("Wrong format! Example: 2026-10");
            }
        }
    }
}