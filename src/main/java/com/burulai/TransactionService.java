package com.burulai;

import java.time.YearMonth;
import java.util.List;

public class TransactionService {
    private final TransactionRepository repository;
    private List<Transaction> transactions;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
        this.transactions = repository.load();
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(String description, double amount, TransactionType type, Category category, java.time.LocalDate date) {
        Transaction transaction = new Transaction(description, amount, type, category, date);
        transactions.add(transaction);
        repository.save(transactions);
    }

    public void deleteTransaction(Transaction transaction) {
        transactions.remove(transaction);
        repository.save(transactions);
    }

    public double calculateTotal(TransactionType type) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getType() == type) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double calculateExpenseByCategory(Category category) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getCategory() == category && t.getType() == TransactionType.EXPENSE) {
                total += t.getAmount();
            }
        }
        return total;
    }

    public double calculateTotalByMonth(YearMonth yearMonth, TransactionType type) {
        double total = 0;
        for (Transaction t : transactions) {
            YearMonth tMonth = YearMonth.from(t.getDate());
            if (tMonth.equals(yearMonth) && t.getType() == type) {
                total += t.getAmount();
            }
        }
        return total;
    }
}