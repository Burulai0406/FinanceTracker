package com.burulai;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final TransactionService service;

    public ConsoleUI(TransactionService service) {
        this.service = service;
    }

    public void run() {
        List<Transaction> transactions = service.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("Транзакциялар жок.");
            return;
        }

        System.out.println("Бардык транзакциялар:");
        for (int i = 0; i < transactions.size(); i++) {
            System.out.println((i + 1) + ". " + transactions.get(i).getDescription() + " - " + transactions.get(i).getAmount());
        }

        System.out.print("Өчүрө турган транзакциянын номерин киргизиңиз: ");
        int index = scanner.nextInt() - 1;

        if (index >= 0 && index < transactions.size()) {
            Transaction transactionToDelete = transactions.get(index);
            service.deleteTransaction(transactionToDelete);
            System.out.println("Транзакция ийгиликтүү өчүрүлдү!");
        } else {
            System.out.println("Туура эмес номер киргизилди!");
        }
    }
}