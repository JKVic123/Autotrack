package oop.avengers.avengersgroup;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.types.ObjectId;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CashierDashboardController {
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private CartRepository cartRepository;
    private User currentCashier;
    private User currentCustomer;

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Product> productSearchTable;

    // FIX: Ensure all columns are explicitly annotated @FXML
    @FXML
    private TableColumn<Product, String> searchNameColumn;
    @FXML
    private TableColumn<Product, BigDecimal> searchPriceColumn; // Type: BigDecimal
    @FXML
    private TableColumn<Product, Integer> searchStockColumn; // <-- Fix for current error
    @FXML
    private TableColumn<Product, Void> searchActionColumn;

    @FXML
    private TextField customerSearchField;
    @FXML
    private Button loadCartButton;
    @FXML
    private Label customerNameLabel;
    @FXML
    private TableView<CartItem> cartTable;

    // FIX: Ensure all columns are explicitly annotated @FXML
    @FXML
    private TableColumn<CartItem, String> cartNameColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> cartPriceColumn; // Type: BigDecimal
    @FXML
    private TableColumn<CartItem, Integer> cartQtyColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> cartTotalColumn; // Type: BigDecimal

    @FXML
    private Label totalLabel;
    @FXML
    private Button clearSaleButton;
    @FXML
    private Button completeSaleButton;
    @FXML
    private Button logoutButton;

    private ObservableList<Product> allProductsList;
    private ObservableList<CartItem> cartItemsList;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    @FXML
    public void initialize() {
        productRepository = new ProductRepository();
        orderRepository = new OrderRepository();
        userRepository = new UserRepository();
        cartRepository = new CartRepository();
        allProductsList = FXCollections.observableArrayList();
        cartItemsList = FXCollections.observableArrayList();
        setupSearchTable();
        setupCartTable();
        searchField.textProperty().addListener((obs, oldText, newText) -> searchProducts(newText));
        cartItemsList.addListener((ListChangeListener<CartItem>) c -> updateTotals());
        loadAllProducts();
        updateTotals();
    }

    public void initData(User cashier) {
        this.currentCashier = cashier;
        this.currentCustomer = null;
    }

    private void loadAllProducts() {
        allProductsList.setAll(productRepository.findAll());
    }

    private void setupSearchTable() {
        searchNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        // FIX: Ensure column type matches model type
        searchPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        searchStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // FIX: Update cell factory to handle BigDecimal
        searchPriceColumn.setCellFactory(tc -> new TableCell<Product, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : currencyFormatter.format(price));
            }
        });

        searchActionColumn.setCellFactory(param -> new TableCell<Product, Void>() {
            private final Button addButton = new Button("Add to Cart");
            {
                addButton.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    addToCart(product, 1);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    addButton.setDisable(product.getStock() <= 0);
                    setGraphic(addButton);
                }
            }
        });
        productSearchTable.setItems(allProductsList);
    }

    private void setupCartTable() {
        cartNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cartPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartQtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantityControls"));
        cartTotalColumn.setCellValueFactory(new PropertyValueFactory<>("lineTotal"));

        // FIX: Update cell factory to handle BigDecimal
        cartPriceColumn.setCellFactory(tc -> formatPriceCell());
        cartTotalColumn.setCellFactory(tc -> formatPriceCell());

        cartTable.setItems(cartItemsList);
    }

    private void searchProducts(String query) {
        if (query == null || query.isEmpty()) {
            loadAllProducts();
        } else {
            allProductsList.setAll(productRepository.searchByName(query));
        }
    }

    private void addToCart(Product product, int quantity) {
        for (CartItem item : cartItemsList) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.incrementQuantity(quantity);
                cartTable.refresh();
                updateTotals();
                return;
            }
        }
        cartItemsList.add(new CartItem(product, quantity));
    }

    private void updateTotals() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItemsList) {
            total = total.add(item.getLineTotal());
        }
        totalLabel.setText("TOTAL: " + currencyFormatter.format(total));
    }

    @FXML
    private void onClearSaleClick() {
        if (cartItemsList.isEmpty()) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to clear this transaction?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cartItemsList.clear();
                currentCustomer = null;
                customerNameLabel.setText("Current Customer: Walk-in");
            }
        });
    }

    @FXML
    private void onCompleteSaleClick() {
        if (cartItemsList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The cart is empty.");
            alert.show();
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // --- 1. Pre-check Stock and build Order Items ---
        for (CartItem cartItem : cartItemsList) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            Product latestProduct = productRepository.findById(product.getId());

            if (latestProduct.getStock() < quantity) {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Not enough stock for " + latestProduct.getName() + ".\nOnly " +
                                latestProduct.getStock() + " remaining.");
                alert.show();
                searchProducts(searchField.getText());
                return;
            }

            orderItems.add(new OrderItem(latestProduct.getId(), latestProduct.getName(),
                    quantity, latestProduct.getPrice()));

            BigDecimal lineTotal = latestProduct.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(lineTotal);
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Complete Sale");
        confirmAlert.setHeaderText("Total: " + currencyFormatter.format(totalAmount));
        confirmAlert.setContentText("Are you sure you want to complete this sale?");
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // --- 2. Create the Order ---
            Order newOrder = new Order();
            newOrder.setCashierId(currentCashier.getId());
            if (currentCustomer != null) {
                newOrder.setCustomerId(currentCustomer.getId());
            }
            newOrder.setOrderDate(new Date());
            newOrder.setTotalAmount(totalAmount);
            newOrder.setItems(orderItems);
            orderRepository.insert(newOrder);

            // --- 3. Decrement Stock ---
            for (CartItem cartItem : cartItemsList) {
                Product product = productRepository.findById(cartItem.getProduct().getId());
                int newStock = product.getStock() - cartItem.getQuantity();
                product.setStock(newStock);
                productRepository.update(product.getId(), product);
            }

            // --- 4. Clear Customer's Online Cart (if applicable) ---
            if (currentCustomer != null) {
                Cart cart = cartRepository.findByUserId(currentCustomer.getId());
                if (cart != null) {
                    cart.getItems().clear();
                    cartRepository.update(cart.getId(), cart);
                }
            }

            // --- 5. Finalize UI and Show Receipt ---
            cartItemsList.clear();
            loadAllProducts();
            customerNameLabel.setText("Current Customer: Walk-in");

            showReceipt(newOrder);

            Alert info = new Alert(Alert.AlertType.INFORMATION, "Sale Completed Successfully!");
            info.show();
        }
    }

    private void showReceipt(Order order) {
        try {
            ReceiptController controller = new ReceiptController();
            controller.initData(order, currentCashier, currentCustomer);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not load receipt dialog.").show();
        }
    }

    @FXML
    private void onLoadCustomerCart() {
        String username = customerSearchField.getText();
        if (username.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a customer username.");
            alert.show();
            return;
        }
        User customer = userRepository.findByUsername(username);
        if (customer == null || !customer.getRole().equals("Customer")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No customer found with that username.");
            alert.show();
            return;
        }
        this.currentCustomer = customer;
        customerNameLabel.setText("Current Customer: " + customer.getUsername());
        Cart cart = cartRepository.findByUserId(customer.getId());
        if (cart == null || cart.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Customer found, but their online cart is empty.");
            alert.show();
            cartItemsList.clear();
            return;
        }
        cartItemsList.clear();
        for (oop.avengers.avengersgroup.CartItem item : cart.getItems()) {
            Product product = productRepository.findById(item.getProductId());
            if (product != null) {
                if (product.getStock() < item.getQuantity()) {
                    new Alert(Alert.AlertType.WARNING,
                            "Not enough stock for " + product.getName() + ". Cart quantity adjusted to " + product.getStock())
                            .show();
                    addToCart(product, product.getStock());
                } else {
                    addToCart(product, item.getQuantity());
                }
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Customer's cart has been loaded.");
        alert.show();
    }

    @FXML
    private void handleLogout() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        NavigationService.showLoginScreen(stage);
    }

    private TableCell<CartItem, BigDecimal> formatPriceCell() {
        return new TableCell<CartItem, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : currencyFormatter.format(price));
            }
        };
    }

    public class CartItem {
        private final Product product;
        private final SimpleIntegerProperty quantity;
        private final SimpleObjectProperty<BigDecimal> lineTotal;
        private final SimpleStringProperty name;
        private final SimpleObjectProperty<BigDecimal> price;

        public CartItem(Product product, int initialQuantity) {
            this.product = product;
            this.quantity = new SimpleIntegerProperty(initialQuantity);

            BigDecimal initialTotal = product.getPrice().multiply(BigDecimal.valueOf(initialQuantity)).setScale(2, RoundingMode.HALF_UP);
            this.lineTotal = new SimpleObjectProperty<>(initialTotal);

            this.name = new SimpleStringProperty(product.getName());
            this.price = new SimpleObjectProperty<>(product.getPrice());

            this.quantity.addListener((obs, oldVal, newVal) -> {
                BigDecimal newTotal = product.getPrice().multiply(BigDecimal.valueOf(newVal.intValue())).setScale(2, RoundingMode.HALF_UP);
                lineTotal.set(newTotal);
                updateTotals();
            });
        }

        public Product getProduct() { return product; }
        public String getName() { return name.get(); }
        public SimpleStringProperty nameProperty() { return name; }
        public BigDecimal getPrice() { return price.get(); }
        public SimpleObjectProperty<BigDecimal> priceProperty() { return price; }
        public int getQuantity() { return quantity.get(); }
        public SimpleIntegerProperty quantityProperty() { return quantity; }
        public BigDecimal getLineTotal() { return lineTotal.get(); }
        public SimpleObjectProperty<BigDecimal> lineTotalProperty() { return lineTotal; }

        public void incrementQuantity(int amount) {
            Product latestProduct = productRepository.findById(product.getId());
            int stock = (latestProduct != null) ? latestProduct.getStock() : product.getStock();
            int newQuantity = this.quantity.get() + amount;
            if (newQuantity > stock) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "No more stock available for " + product.getName() + ".\nOnly " +
                                stock + " available.");
                alert.show();
                this.quantity.set(stock);
            } else {
                this.quantity.set(newQuantity);
            }
        }

        public void decrementQuantity() {
            if (this.quantity.get() > 1) {
                this.quantity.set(this.quantity.get() - 1);
            }
        }

        public HBox getQuantityControls() {
            Button plusButton = new Button("+");
            plusButton.setOnAction(e -> incrementQuantity(1));
            Button minusButton = new Button("-");
            minusButton.setOnAction(e -> decrementQuantity());
            Button removeButton = new Button("X");
            removeButton.setStyle("-fx-text-fill: red;");
            removeButton.setOnAction(e -> cartItemsList.remove(this));
            Label qtyLabel = new Label();
            qtyLabel.textProperty().bind(quantity.asString());
            qtyLabel.setPadding(new Insets(0, 5, 0, 5));
            HBox pane = new HBox(minusButton, qtyLabel, plusButton, removeButton);
            pane.setSpacing(5);
            pane.setAlignment(Pos.CENTER);
            return pane;
        }
    }
}