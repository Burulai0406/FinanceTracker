package com.burulai;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class MainView {

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final TransactionService service;

    private final ObservableList<Transaction> items = FXCollections.observableArrayList();
    private final FilteredList<Transaction> filtered = new FilteredList<>(items, t -> true);

    private final BorderPane root = new BorderPane();
    private final TableView<Transaction> table = new TableView<>();

    private final Label balanceValue = new Label();
    private final Label incomeValue = new Label();
    private final Label expenseValue = new Label();

    private final ComboBox<String> typeFilter = new ComboBox<>();
    private final TextField searchField = new TextField();
    private final Button deleteButton = new Button("Delete selected");

    private final ToggleButton incomeToggle = new ToggleButton("Income");
    private final ToggleButton expenseToggle = new ToggleButton("Expense");
    private final TextField descriptionField = new TextField();
    private final TextField amountField = new TextField();
    private final ComboBox<Category> categoryBox = new ComboBox<>();
    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final Label errorLabel = new Label();

    private final PieChart pieChart = new PieChart();
    private final BarChart<String, Number> barChart =
            new BarChart<>(new CategoryAxis(), new NumberAxis());

    public MainView(TransactionService service) {
        this.service = service;

        root.setPadding(new Insets(20));
        root.setTop(buildHeader());
        root.setCenter(buildCenter());
        root.setRight(buildForm());

        refresh();
    }

    public BorderPane getRoot() {
        return root;
    }

    // ---------- Header ----------

    private Node buildHeader() {
        Label title = new Label("Finance Tracker");
        title.getStyleClass().add("app-title");

        HBox cards = new HBox(16,
                buildCard("Balance", balanceValue, "balance"),
                buildCard("Total income", incomeValue, "income"),
                buildCard("Total expenses", expenseValue, "expense"));

        for (Node card : cards.getChildren()) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }

        VBox box = new VBox(14, title, cards);
        box.setPadding(new Insets(0, 0, 16, 0));
        return box;
    }

    private VBox buildCard(String title, Label valueLabel, String styleClass) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");

        valueLabel.getStyleClass().addAll("card-value", styleClass);

        VBox card = new VBox(4, titleLabel, valueLabel);
        card.getStyleClass().add("card");
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    // ---------- Center (tabs) ----------

    private Node buildCenter() {
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
                new Tab("Transactions", buildTransactionsPane()),
                new Tab("Charts", buildChartsPane())
        );

        BorderPane.setMargin(tabs, new Insets(0, 16, 0, 0));
        return tabs;
    }

    private Node buildTransactionsPane() {
        typeFilter.getItems().addAll("All", "Income", "Expense");
        typeFilter.setValue("All");
        typeFilter.valueProperty().addListener((obs, oldValue, newValue) -> applyFilter());

        searchField.setPromptText("Search by description or category");
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilter());
        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox filterBar = new HBox(10, typeFilter, searchField);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        configureTable();

        deleteButton.getStyleClass().add("danger-button");
        deleteButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.setOnAction(e -> onDelete());

        HBox bottomBar = new HBox(deleteButton);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);

        VBox.setVgrow(table, Priority.ALWAYS);

        VBox pane = new VBox(12, filterBar, table, bottomBar);
        pane.setPadding(new Insets(14));
        pane.getStyleClass().add("panel");
        return pane;
    }

    @SuppressWarnings("unchecked")
    private void configureTable() {
        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDate()));
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(DISPLAY_DATE));
            }
        });
        dateCol.setSortType(TableColumn.SortType.DESCENDING);

        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory().getLabel()));

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isIncome() ? "Income" : "Expense"));

        TableColumn<Transaction, Transaction> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue()));
        amountCol.setComparator(Comparator.comparingDouble(Transaction::getSignedAmount));
        amountCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Transaction item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("amount-income", "amount-expense");

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(String.format("%+,.2f", item.getSignedAmount()));
                getStyleClass().add(item.isIncome() ? "amount-income" : "amount-expense");
            }
        });

        table.getColumns().addAll(dateCol, descCol, categoryCol, typeCol, amountCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No transactions yet. Add your first one on the right."));

        SortedList<Transaction> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
        table.getSortOrder().add(dateCol);

        table.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                onDelete();
            }
        });
    }

    private Node buildChartsPane() {
        pieChart.setTitle("Expenses by category");
        pieChart.setAnimated(false);
        pieChart.setPrefWidth(380);

        barChart.setTitle("Income vs expenses, last 6 months");
        barChart.setAnimated(false);
        barChart.setCategoryGap(20);
        barChart.setPrefWidth(520);

        HBox.setHgrow(pieChart, Priority.ALWAYS);
        HBox.setHgrow(barChart, Priority.ALWAYS);

        HBox pane = new HBox(16, pieChart, barChart);
        pane.setPadding(new Insets(14));
        pane.getStyleClass().add("panel");
        return pane;
    }

    // ---------- Form ----------

    private Node buildForm() {
        Label title = new Label("New transaction");
        title.getStyleClass().add("panel-title");

        ToggleGroup group = new ToggleGroup();
        expenseToggle.setToggleGroup(group);
        incomeToggle.setToggleGroup(group);
        expenseToggle.getStyleClass().add("toggle-expense");
        incomeToggle.getStyleClass().add("toggle-income");
        expenseToggle.setMaxWidth(Double.MAX_VALUE);
        incomeToggle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(expenseToggle, Priority.ALWAYS);
        HBox.setHgrow(incomeToggle, Priority.ALWAYS);
        expenseToggle.setSelected(true);

        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null) {
                oldToggle.setSelected(true);
                return;
            }
            updateCategories();
        });

        HBox toggles = new HBox(expenseToggle, incomeToggle);

        descriptionField.setPromptText("e.g. Groceries");
        amountField.setPromptText("0.00");

        categoryBox.setMaxWidth(Double.MAX_VALUE);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        updateCategories();

        Button addButton = new Button("Add transaction");
        addButton.getStyleClass().add("primary-button");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setDefaultButton(true);
        addButton.setOnAction(e -> onAdd());

        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);

        VBox form = new VBox(8,
                title,
                toggles,
                fieldLabel("Description"), descriptionField,
                fieldLabel("Amount"), amountField,
                fieldLabel("Category"), categoryBox,
                fieldLabel("Date"), datePicker,
                addButton,
                errorLabel);
        form.getStyleClass().add("panel");
        form.setPadding(new Insets(18));
        form.setPrefWidth(320);
        form.setMinWidth(320);
        return form;
    }

    private Label fieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");
        return label;
    }

    private TransactionType currentType() {
        return incomeToggle.isSelected() ? TransactionType.INCOME : TransactionType.EXPENSE;
    }

    private void updateCategories() {
        TransactionType type = currentType();

        categoryBox.getItems().clear();
        for (Category category : Category.values()) {
            if (category.isAvailableFor(type)) {
                categoryBox.getItems().add(category);
            }
        }
        categoryBox.getSelectionModel().selectFirst();
    }

    // ---------- Actions ----------

    private void onAdd() {
        errorLabel.setText("");

        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            errorLabel.setText("Enter a description.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            errorLabel.setText("Enter a valid amount, for example 250.50");
            return;
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            errorLabel.setText("Amount must be greater than 0.");
            return;
        }

        Category category = categoryBox.getValue();
        if (category == null) {
            errorLabel.setText("Choose a category.");
            return;
        }

        LocalDate date = datePicker.getValue();
        if (date == null) {
            errorLabel.setText("Choose a date.");
            return;
        }

        try {
            service.addTransaction(description, amount, currentType(), category, date);
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
            return;
        }

        descriptionField.clear();
        amountField.clear();
        descriptionField.requestFocus();
        refresh();
    }

    private void onDelete() {
        Transaction selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete \"" + selected.getDescription() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Delete transaction");
        confirm.setHeaderText(null);

        confirm.showAndWait()
                .filter(button -> button == ButtonType.OK)
                .ifPresent(button -> {
                    service.deleteTransaction(selected);
                    refresh();
                });
    }

    // ---------- Refresh ----------

    private void applyFilter() {
        String type = typeFilter.getValue();
        String query = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        filtered.setPredicate(t -> {
            boolean typeOk = type == null
                    || type.equals("All")
                    || (type.equals("Income") && t.isIncome())
                    || (type.equals("Expense") && !t.isIncome());

            boolean textOk = query.isEmpty()
                    || t.getDescription().toLowerCase().contains(query)
                    || t.getCategory().getLabel().toLowerCase().contains(query);

            return typeOk && textOk;
        });
    }

    private void refresh() {
        items.setAll(service.getTransactions());
        updateSummary();
        updateCharts();
    }

    private void updateSummary() {
        double income = service.calculateTotal(TransactionType.INCOME);
        double expense = service.calculateTotal(TransactionType.EXPENSE);
        double balance = income - expense;

        balanceValue.setText(formatMoney(balance));
        incomeValue.setText(formatMoney(income));
        expenseValue.setText(formatMoney(expense));

        balanceValue.getStyleClass().remove("negative");
        if (balance < 0) {
            balanceValue.getStyleClass().add("negative");
        }
    }

    private void updateCharts() {
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Category category : Category.values()) {
            double total = service.calculateExpenseByCategory(category);
            if (total > 0) {
                pieData.add(new PieChart.Data(
                        category.getLabel() + " (" + formatMoney(total) + ")", total));
            }
        }
        pieChart.setData(pieData);

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        YearMonth now = YearMonth.now();
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMM yy");

        for (int i = 5; i >= 0; i--) {
            YearMonth month = now.minusMonths(i);
            String label = month.format(monthFormat);

            incomeSeries.getData().add(new XYChart.Data<>(
                    label, service.calculateTotalByMonth(month, TransactionType.INCOME)));
            expenseSeries.getData().add(new XYChart.Data<>(
                    label, service.calculateTotalByMonth(month, TransactionType.EXPENSE)));
        }

        barChart.getData().clear();
        barChart.getData().addAll(incomeSeries, expenseSeries);
    }

    private String formatMoney(double value) {
        return String.format("%,.2f", value);
    }
}