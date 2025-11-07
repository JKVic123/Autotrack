package oop.avengers.avengersgroup;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CustomerDashboardController {
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private User currentUser;
    private Cart currentCart;

    @FXML
    private Button logoutButton;
    @FXML
    private Button myCartButton;
    @FXML
    private FlowPane productFlowPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField searchField;

    private final NumberFormat currencyFormatter =
            NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    public void initData(User user) {
        this.currentUser = user;
        loadProductsAndCart();
    }

    @FXML
    public void initialize() {
        this.productRepository = new ProductRepository();
        this.cartRepository = new CartRepository();

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldText, newText) -> searchProducts(newText));
        }
    }

    private void loadProductsAndCart() {
        this.currentCart = cartRepository.findByUserId(currentUser.getId());
        if (this.currentCart == null) {
            this.currentCart = new Cart(currentUser.getId());
            cartRepository.insert(this.currentCart);
        }
        updateCartButton();

        searchProducts("");
    }

    private void searchProducts(String query) {
        List<Product> products;

        if (query == null || query.trim().isEmpty()) {
            products = productRepository.findAll();
        } else {
            products = productRepository.searchByName(query);
        }

        productFlowPane.getChildren().clear();
        for (Product product : products) {
            productFlowPane.getChildren().add(createProductCard(product));
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 1);");

        Label name = new Label(product.getName());
        name.setFont(Font.font("System Bold", 16));
        name.setWrapText(true);
        Label description = new Label(product.getDescription());
        description.setWrapText(true);
        description.setPrefHeight(60);

        Label price = new Label(currencyFormatter.format(product.getPrice()));
        price.setFont(Font.font("System Bold", 14));

        int cartQuantity = currentCart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .mapToInt(CartItem::getQuantity)
                .sum();

        Label stock = new Label(String.format("Stock: %d (In Cart: %d)", product.getStock(), cartQuantity));

        Button addToCartButton = new Button("Add to Cart");
        addToCartButton.setMaxWidth(Double.MAX_VALUE);

        boolean maxStockReached = (product.getStock() == cartQuantity);

        if (product.getStock() == 0) {
            stock.setText("OUT OF STOCK");
            stock.setTextFill(Color.RED);
            addToCartButton.setDisable(true);
        } else if (maxStockReached) {
            stock.setText("MAX STOCK REACHED IN CART");
            stock.setTextFill(Color.ORANGE);
            addToCartButton.setDisable(true);
        }

        addToCartButton.setOnAction(event -> handleAddToCart(product));
        card.getChildren().addAll(name, description, price, stock, addToCartButton);
        return card;
    }

    private void handleAddToCart(Product product) {
        Product latestProduct = productRepository.findById(product.getId());
        int currentStock = latestProduct.getStock();

        int existingQuantity = currentCart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .mapToInt(CartItem::getQuantity)
                .sum();

        if (existingQuantity >= currentStock) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Stock Warning");
            alert.setContentText("Cannot add more " + product.getName() + ". Maximum stock quantity reached in cart.");
            alert.showAndWait();
            return;
        }

        boolean found = false;
        for (CartItem item : currentCart.getItems()) {
            if (item.getProductId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            currentCart.getItems().add(new CartItem(
                    product.getId(),
                    product.getName(),
                    1,
                    product.getPrice()
            ));
        }

        cartRepository.update(currentCart.getId(), currentCart);
        updateCartButton();

        searchProducts(searchField.getText());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cart Updated");
        alert.setHeaderText(null);
        alert.setContentText(product.getName() + " has been added to your cart.");
        alert.showAndWait();
    }

    @FXML
    private void handleMyCartClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AutoTrackApplication.class.getResource("my-cart-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            MyCartController cartController = fxmlLoader.getController();
            cartController.initData(this.currentCart, this.currentUser);

            Stage cartStage = new Stage();
            cartStage.setTitle("My Cart");
            cartStage.setScene(scene);
            cartStage.initModality(Modality.APPLICATION_MODAL);
            cartStage.showAndWait();

            searchProducts(searchField.getText());
            updateCartButton();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not load cart.").show();
        }
    }

    private void updateCartButton() {
        int totalItems = 0;
        for (CartItem item : currentCart.getItems()) {
            totalItems += item.getQuantity();
        }
        myCartButton.setText("My Cart (" + totalItems + ")");
    }

    @FXML
    private void handleLogout() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        NavigationService.showLoginScreen(stage);
    }
}