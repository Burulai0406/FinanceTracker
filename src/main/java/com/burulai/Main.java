package com.burulai;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        TransactionRepository repository = new FileTransactionRepository("transactions.txt");
        TransactionService service = new TransactionService(repository);
        MainView mainView = new MainView(service);

        Scene scene = new Scene(mainView.getRoot(), 1000, 650);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Finance Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}