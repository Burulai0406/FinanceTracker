package com.burulai;

import java.util.List;

public interface TransactionRepository {
    void save(List<Transaction> transactions);

    List<Transaction> load();
}