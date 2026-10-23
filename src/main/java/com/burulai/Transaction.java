package com.burulai;

import java.time.LocalDate;
import java.util.Objects;

public class Transaction {
    private String description;
    private double amount;
    private TransactionType type;
    private Category category;
    private LocalDate date;

    public Transaction(String description, double amount,
                       TransactionType type, Category category, LocalDate date) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty!");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null!");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null!");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null!");
        }

        this.description = description;
        this.type = type;
        this.category = category;
        this.date = date;
        setAmount(amount);
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public double getSignedAmount() {
        return isIncome() ? amount : -amount;
    }

    public void setAmount(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0!");
        }

        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(description, that.description) &&
                type == that.type &&
                category == that.category &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, amount, type, category, date);
    }
}