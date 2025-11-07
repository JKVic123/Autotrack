package oop.avengers.avengersgroup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.Document;

public class AdminDashboardController {
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private User loggedInAdmin;

    @FXML
    private Button logoutButton;
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TableColumn<Product, BigDecimal> priceColumn; // Changed to BigDecimal
    @FXML
    private TableColumn<Product, Integer> stockColumn;
    @FXML
    private TableColumn<Product, Void> actionColumn;
    @FXML
    private Label formTitleLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label formMessageLabel;
    @FXML
    private Tab reportsTab; // Fixed @FXML
    @FXML
    private Label totalSalesLabel;
    @FXML
    private Label totalOrdersLabel;
    @FXML
    private ListView<String> lowStockListView;
    @FXML
    private ListView<String> popularProductsListView; // NEW
    @FXML
    private Tab userManagementTab; // Fixed @FXML
    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableColumn<User, String> userUsernameColumn;
    @FXML
    private TableColumn<User, String> userRoleColumn;
    @FXML
    private TableColumn<User, Void> userActionColumn;
    @FXML
    private TextField userUsernameField;
    @FXML
    private PasswordField userPasswordField;
    @FXML
    private ComboBox<String> userRoleComboBox;
    @FXML
    private Button userSaveButton;
    @FXML
    private Label userFormMessageLabel;

    private Product currentlyEditingProduct = null;
    private boolean reportsLoaded = false;
    private boolean usersLoaded = false;

    private final NumberFormat currencyFormatter =
            NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    public void initData(User user) {
        this.loggedInAdmin = user;
    }

    @FXML
    public void initialize() {
        productRepository = new ProductRepository();
        orderRepository = new OrderRepository();
        userRepository = new UserRepository();
        setupTableColumns();
        setupActionColumn();
        loadProductData();

        reportsTab.setOnSelectionChanged(event -> {
            if (reportsTab.isSelected() && !reportsLoaded) {
                loadReports();
                reportsLoaded = true;
            }
        });

        userManagementTab.setOnSelectionChanged(event -> {
            if (userManagementTab.isSelected() && !usersLoaded) {
                setupUserTable();
                setupUserActionColumn();
                loadUsers();
                usersLoaded = true;
            }
        });
        userRoleComboBox.setItems(FXCollections.observableArrayList("Admin", "Cashier"));
    }

    @FXML
    private void handleLogout() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        NavigationService.showLoginScreen(stage);
    }

    private void loadReports() {
        List<Order> allOrders = orderRepository.findAll();
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Order order : allOrders) {
            totalSales = totalSales.add(order.getTotalAmount());
        }
        totalSalesLabel.setText(currencyFormatter.format(totalSales));
        totalOrdersLabel.setText(String.valueOf(allOrders.size()));

        final int LOW_STOCK_THRESHOLD = 10;
        List<Product> lowStockProducts = productRepository.findLowStockProducts(LOW_STOCK_THRESHOLD);
        ObservableList<String> lowStockItems = FXCollections.observableArrayList();
        for (Product product : lowStockProducts) {
            lowStockItems.add(product.getName() + " (Stock: " + product.getStock() + ")");
        }
        if (lowStockItems.isEmpty()) {
            lowStockItems.add("No low stock items found. Great job!");
        }
        lowStockListView.setItems(lowStockItems);

        List<Document> popularProducts = orderRepository.findMostPopularProducts(5); // Top 5
        ObservableList<String> popularItems = FXCollections.observableArrayList();

        if (popularProducts.isEmpty()) {
            popularItems.add("No orders processed yet.");
        } else {
            for (Document doc : popularProducts) {
                String name = doc.getString("name");
                int quantity = doc.getInteger("totalQuantity");
                popularItems.add(String.format("%s - %d units sold", name, quantity));
            }
        }
        popularProductsListView.setItems(popularItems);
    }

    private void setupUserTable() {
        userUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void loadUsers() {
        List<User> userList = userRepository.findAll();
        userTableView.setItems(FXCollections.observableArrayList(userList));
    }

    private void setupUserActionColumn() {
        userActionColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setOnAction(event -> {
                    User user =
                            getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super
                        .updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (loggedInAdmin.getId().equals(user.getId())) {
                        deleteButton.setDisable(true);
                    } else {
                        deleteButton.setDisable(false);
                    }
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void deleteUser(User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure you want to delete this user?");
        alert.setContentText(user.getUsername() + " (Role: " + user.getRole() + ")");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            userRepository.delete(user.getId());
            userTableView.getItems().remove(user);
            setUserFormMessage(Color.GREEN, "User deleted successfully.");
        }
    }

    @FXML
    private void onSaveUserClick() {
        String username = userUsernameField.getText();
        String password = userPasswordField.getText();
        String role = userRoleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            setUserFormMessage(Color.RED, "Please fill in all fields.");
            return;
        }
        if (password.length() < 8) {
            setUserFormMessage(Color.RED, "Password must be at least 8 characters.");
            return;
        }
        if (userRepository.findByUsername(username) != null) {
            setUserFormMessage(Color.RED, "Username '" + username + "' already exists.");
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword);
        newUser.setRole(role);
        userRepository.insert(newUser);
        userTableView.getItems().add(newUser);
        setUserFormMessage(Color.GREEN, "User created successfully!");
        clearUserForm();
    }

    private void clearUserForm() {
        userUsernameField.clear();
        userPasswordField.clear();
        userRoleComboBox.setValue(null);
    }

    private void setUserFormMessage(Color color, String message) {
        userFormMessageLabel.setTextFill(color);
        userFormMessageLabel.setText(message);
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // FIX: Price cell factory now handles BigDecimal
        priceColumn.setCellFactory(tc -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(price));
                }
            }
        });
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<Product, Void>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(editButton, deleteButton);
            {
                pane.setSpacing(10);
                pane.setAlignment(Pos.CENTER);
                editButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    populateFormForEdit(product);
                });
                deleteButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    deleteProduct(product);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadProductData() {
        List<Product> productList = productRepository.findAll();
        ObservableList<Product> observableProductList =
                FXCollections.observableArrayList(productList);
        productTableView.setItems(observableProductList);
    }

    private void populateFormForEdit(Product product) {
        currentlyEditingProduct = product;
        nameField.setText(product.getName());
        descriptionArea.setText(product.getDescription());
        priceField.setText(product.getPrice().toPlainString());
        stockField.setText(Integer.toString(product.getStock()));
        formTitleLabel.setText("Edit Product");
        saveButton.setText("Update Product");
        cancelButton.setVisible(true);
        setFormMessage(Color.BLUE, "Editing: " + product.getName());
    }

    private void deleteProduct(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Are you sure you want to delete this product?");
        alert.setContentText(product.getName() + "\nThis action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            productRepository.delete(product.getId());
            productTableView.getItems().remove(product);
            setFormMessage(Color.GREEN, "Product deleted successfully.");
        }
    }

    @FXML
    private void onSaveProductClick() {
        String name = nameField.getText();
        String description = descriptionArea.getText();
        String priceText = priceField.getText();
        String stockText = stockField.getText();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            setFormMessage(Color.RED, "Please fill in all fields.");
            return;
        }

        BigDecimal price;
        int stock;
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException e) {
            setFormMessage(Color.RED, "Price must be a valid number (e.g., 1800.00).");
            return;
        }

        try {
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            setFormMessage(Color.RED, "Stock must be a valid whole number (e.g., 50).");
            return;
        }

        Product product = new Product(name, description, price, stock);
        if (currentlyEditingProduct == null) {
            productRepository.insert(product);
            productTableView.getItems().add(product);
            setFormMessage(Color.GREEN, "Product added successfully!");
        } else {
            productRepository.update(currentlyEditingProduct.getId(), product);
            loadProductData();
            setFormMessage(Color.GREEN, "Product updated successfully!");
        }
        clearFormFields();
    }

    @FXML
    private void onCancelEditClick() {
        clearFormFields();
    }

    private void clearFormFields() {
        nameField.clear();
        descriptionArea.clear();
        priceField.clear();
        stockField.clear();
        currentlyEditingProduct = null;
        saveButton.setText("Save New Product");
        formTitleLabel.setText("Add New Product");
        cancelButton.setVisible(false);
        setFormMessage(Color.BLACK, "");
    }

    private void setFormMessage(Color color, String message) {
        formMessageLabel.setTextFill(color);
        formMessageLabel.setText(message);
    }
}
