package com.burulai;

public enum Category {
    SALARY("Salary", TransactionType.INCOME),
    FREELANCE("Freelance", TransactionType.INCOME),
    GIFT("Gift", TransactionType.INCOME),
    FOOD("Food", TransactionType.EXPENSE),
    TRANSPORT("Transport", TransactionType.EXPENSE),
    ENTERTAINMENT("Entertainment", TransactionType.EXPENSE),
    HEALTH("Health", TransactionType.EXPENSE),
    SHOPPING("Shopping", TransactionType.EXPENSE),
    BILLS("Bills", TransactionType.EXPENSE),
    OTHER("Other", null);

    private final String label;
    private final TransactionType type;

    Category(String label, TransactionType type) {
        this.label = label;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public boolean isAvailableFor(TransactionType transactionType) {
        return type == null || type == transactionType;
    }

    @Override
    public String toString() {
        return label;
    }
}