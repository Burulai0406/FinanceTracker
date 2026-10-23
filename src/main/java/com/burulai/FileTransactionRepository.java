package com.burulai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileTransactionRepository implements TransactionRepository {

    private final String fileName;

    public FileTransactionRepository(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void save(List<Transaction> transactions) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (Transaction t : transactions) {
                writer.write(
                        t.getDescription().replace(";", ",") + ";"
                                + t.getAmount() + ";"
                                + t.getType().name() + ";"
                                + t.getCategory().name() + ";"
                                + t.getDate() + "\n"
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions!");
        }
    }

    @Override
    public List<Transaction> load() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(fileName);

        try {
            if (file.createNewFile()) {
                System.out.println("File created successfully.");
            }
        } catch (IOException e) {
            System.out.println("Could not create file.");
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                try {
                    transactions.add(parseLine(line));
                } catch (RuntimeException e) {
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load transactions.");
        }

        return transactions;
    }

    private Transaction parseLine(String line) {
        if (line.contains(";")) {
            String[] parts = line.split(";");
            if (parts.length != 5) {
                throw new IllegalArgumentException("Wrong number of fields");
            }

            return new Transaction(
                    parts[0],
                    Double.parseDouble(parts[1]),
                    TransactionType.valueOf(parts[2]),
                    Category.valueOf(parts[3]),
                    LocalDate.parse(parts[4])
            );
        }

        String[] parts = line.split(",");
        if (parts.length != 3 && parts.length != 4) {
            throw new IllegalArgumentException("Wrong number of fields");
        }
        if (!parts[2].equals("true") && !parts[2].equals("false")) {
            throw new IllegalArgumentException("Wrong type");
        }

        TransactionType type = Boolean.parseBoolean(parts[2])
                ? TransactionType.INCOME
                : TransactionType.EXPENSE;

        Category category = parts.length == 4
                ? Category.valueOf(parts[3])
                : Category.OTHER;

        return new Transaction(
                parts[0],
                Double.parseDouble(parts[1]),
                type,
                category,
                LocalDate.now()
        );
    }
}