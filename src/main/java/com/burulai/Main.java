package com.burulai;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        TransactionRepository repository = new FileTransactionRepository("transactions.txt");
        TransactionService service = new TransactionService(repository);
        MainView view = new MainView(service);

        Scene scene = new Scene(view.getRoot(), 1200, 760);

        URL css = Main.class.getResource("/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("Finance Tracker");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}