package oop.avengers.avengersgroup;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class PaymentController {

    @FXML
    private Label totalLabel;
    @FXML
    private ListView<PaymentMethod> paymentListView;
    @FXML
    private Button confirmPaymentButton;
    @FXML
    private Label paymentMessageLabel;
    @FXML
    private ComboBox<String> paymentTypeComboBox;
    @FXML
    private TextField paymentDetailsField;
    @FXML
    private Button addMethodButton;
    @FXML
    private Label addMethodMessageLabel;

    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private PaymentMethodRepository paymentMethodRepository;

    private Cart currentCart;
    private User currentUser;
    private Stage cartStage;

    private final NumberFormat currencyFormatter =
            NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    @FXML
    public void initialize() {
        this.cartRepository = new CartRepository();
        this.productRepository = new ProductRepository();
        this.orderRepository = new OrderRepository();
        this.paymentMethodRepository = new PaymentMethodRepository();

        // Manually add items to ComboBox
        paymentTypeComboBox.setItems(FXCollections.observableArrayList(
                "Credit Card", "Gcash", "Cash on Delivery"
        ));
    }

    public void initData(Cart cart, User customer) {
        this.currentCart = cart;
        this.currentUser = customer;
        totalLabel.setText(currencyFormatter.format(cart.getCartTotal()));
        loadPaymentMethods();
    }

    public void setCartStage(Stage cartStage) {
        this.cartStage = cartStage;
    }

    private void loadPaymentMethods() {
        List<PaymentMethod> methods = paymentMethodRepository.findByUserId(currentUser.getId());
        paymentListView.setItems(FXCollections.observableArrayList(methods));
    }

    @FXML
    private void onAddMethodClick() {
        String type = paymentTypeComboBox.getValue();
        String details = paymentDetailsField.getText();

        if (type == null || details.isEmpty()) {
            setAddMethodMessage(Color.RED, "Please select a type and enter details.");
            return;
        }

        PaymentMethod newMethod = new PaymentMethod();
        newMethod.setUserId(currentUser.getId());
        newMethod.setMethodType(type);
        newMethod.setDetails(details);

        paymentMethodRepository.insert(newMethod);

        paymentListView.getItems().add(newMethod);
        paymentDetailsField.clear();
        setAddMethodMessage(Color.GREEN, "Method added successfully.");
    }

    @FXML
    private void onConfirmPaymentClick() {
        PaymentMethod selectedMethod = paymentListView.getSelectionModel().getSelectedItem();

        if (selectedMethod == null) {
            setPaymentMessage(Color.RED, "Please select a payment method from the list.");
            return;
        }

        if (currentCart.getItems().isEmpty()) {
            setPaymentMessage(Color.RED, "Your cart is empty.");
            return;
        }

        List<Product> productsToUpdate = new ArrayList<>();
        for (CartItem item : currentCart.getItems()) {
            Product product = productRepository.findById(item.getProductId());
            if (product.getStock() < item.getQuantity()) {
                new Alert(Alert.AlertType.ERROR, "Not enough stock for " + product.getName() + ".\nOnly " + product.getStock() + " remaining.").show();
                return;
            }
            product.setStock(product.getStock() - item.getQuantity());
            productsToUpdate.add(product);
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Checkout");
        confirmAlert.setHeaderText("Total: " + currencyFormatter.format(currentCart.getCartTotal()));
        confirmAlert.setContentText("Place order using: " + selectedMethod.toString() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Order newOrder = new Order();
            newOrder.setCustomerId(currentUser.getId());
            newOrder.setPaymentMethodId(selectedMethod.getId()); // Link the payment
            newOrder.setOrderDate(new Date());
            newOrder.setTotalAmount(currentCart.getCartTotal());

            List<OrderItem> orderItems = currentCart.getItems().stream()
                    .map(cartItem -> new OrderItem(
                            cartItem.getProductId(),
                            cartItem.getName(),
                            cartItem.getQuantity(),
                            cartItem.getPrice()
                    )).collect(Collectors.toList());

            newOrder.setItems(orderItems);
            orderRepository.insert(newOrder);

            for (Product product : productsToUpdate) {
                productRepository.update(product.getId(), product);
            }

            currentCart.getItems().clear();
            cartRepository.update(currentCart.getId(), currentCart);

            new Alert(Alert.AlertType.INFORMATION, "Order placed successfully!").show();

            closeWindow();
            if (cartStage != null) {
                cartStage.close();
            }
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) confirmPaymentButton.getScene().getWindow();
        stage.close();
    }

    private void setPaymentMessage(Color color, String message) {
        paymentMessageLabel.setTextFill(color);
        paymentMessageLabel.setText(message);
    }

    private void setAddMethodMessage(Color color, String message) {
        addMethodMessageLabel.setTextFill(color);
        addMethodMessageLabel.setText(message);
    }
}
