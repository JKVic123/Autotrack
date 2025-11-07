package oop.avengers.avengersgroup;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MyCartController {
    @FXML
    private TableView<CartItem> cartTable;
    @FXML
    private TableColumn<CartItem, String> nameColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> priceColumn;
    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;
    @FXML
    private TableColumn<CartItem, BigDecimal> totalColumn;
    @FXML
    private Label totalLabel;
    @FXML
    private Button checkoutButton;

    private Cart currentCart;
    private User currentUser;
    private final NumberFormat currencyFormatter =
            NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    @FXML
    public void initialize() {
    }

    public void initData(Cart cart, User customer) {
        this.currentCart = cart;
        this.currentUser = customer;
        setupTable();
        loadCartItems();
        updateTotalLabel();
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        totalColumn.setCellValueFactory(new PropertyValueFactory<>("lineTotal"));

        priceColumn.setCellFactory(this::formatPriceCell);
        totalColumn.setCellFactory(this::formatPriceCell);
    }

    private void loadCartItems() {
        cartTable.setItems(FXCollections.observableArrayList(currentCart.getItems()));
    }

    private void updateTotalLabel() {
        totalLabel.setText("TOTAL: " + currencyFormatter.format(currentCart.getCartTotal()));
    }

    @FXML
    private void handleCheckoutClick() {
        if (currentCart.getItems().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Your cart is empty.").show();
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AutoTrackApplication.class.getResource("payment-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            PaymentController paymentController = fxmlLoader.getController();
            paymentController.initData(currentCart, currentUser);

            Stage paymentStage = new Stage();
            paymentStage.setTitle("Payment Simulation");
            paymentStage.setScene(scene);
            paymentStage.initModality(Modality.APPLICATION_MODAL);

            Stage cartStage = (Stage) checkoutButton.getScene().getWindow();
            paymentController.setCartStage(cartStage);

            paymentStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not load payment screen.").show();
        }
    }

    private <T> TableCell<T, BigDecimal> formatPriceCell(TableColumn<T, BigDecimal> column) {
        return new TableCell<T, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(price));
                }
            }
        };
    }
}
