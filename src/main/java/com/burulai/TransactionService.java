package com.burulai;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransactionService {

    private final TransactionRepository repository;
    private final ArrayList<Transaction> transactions;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
        this.transactions = new ArrayList<>(repository.load());
    }

    public void addTransaction(String description, double amount,
                               TransactionType type, Category category,
                               LocalDate date) {
        transactions.add(new Transaction(description, amount, type, category, date));
        repository.save(transactions);
    }

    public boolean deleteTransaction(Transaction transaction) {
        boolean removed = transactions.remove(transaction);

        if (removed) {
            repository.save(transactions);
        }

        return removed;
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public double calculateBalance() {
        double balance = 0;

        for (Transaction transaction : transactions) {
            balance += transaction.getSignedAmount();
        }

        return balance;
    }

    public double calculateTotal(TransactionType type) {
        double total = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == type) {
                total += transaction.getAmount();
            }
        }

        return total;
    }

    public double calculateExpenseByCategory(Category category) {
        double total = 0;

        for (Transaction transaction : transactions) {
            if (!transaction.isIncome() && transaction.getCategory() == category) {
                total += transaction.getAmount();
            }
        }

        return total;
    }

    public double calculateTotalByMonth(YearMonth month, TransactionType type) {
        double total = 0;

        for (Transaction transaction : transactions) {
            boolean sameMonth = YearMonth.from(transaction.getDate()).equals(month);
            if (sameMonth && transaction.getType() == type) {
                total += transaction.getAmount();
            }
        }

        return total;
    }
}