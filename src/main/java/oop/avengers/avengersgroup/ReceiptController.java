package oop.avengers.avengersgroup;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReceiptController {
    private final NumberFormat currencyFormatter =
            NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @FXML
    private VBox receiptVBox;

    public void initData(Order order, User cashier, User customer) {
        Stage stage = new Stage();
        stage.setTitle("AutoTrack Sales Receipt");

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #FFFFFF;");

        Label title = new Label("AUTO TRACK");
        title.setFont(Font.font("Monospaced", Font.getDefault().getSize() + 4));
        Label type = new Label("SALES RECEIPT");
        type.setFont(Font.font("System Bold", 16));

        Label dateLabel = new Label("Date: " + dateFormatter.format(order.getOrderDate()));
        Label cashierLabel = new Label("Cashier: " + cashier.getUsername());
        String customerName = customer != null ? customer.getUsername() : "Walk-in Customer";
        Label customerLabel = new Label("Customer: " + customerName);

        VBox metaBox = new VBox(5, dateLabel, cashierLabel, customerLabel);
        metaBox.setAlignment(Pos.CENTER_LEFT);
        metaBox.setPadding(new Insets(10, 0, 10, 0));

        Label separator = new Label("--------------------------------------------------");
        Label itemsHeader = new Label(String.format("%-25s %7s %7s %7s", "ITEM", "QTY", "PRICE", "TOTAL"));
        itemsHeader.setFont(Font.font("Monospaced", Font.getDefault().getSize()));

        VBox itemsBox = new VBox(2);
        for (OrderItem item : order.getItems()) {
            BigDecimal lineTotal = item.getPriceAtSale().multiply(BigDecimal.valueOf(item.getQuantity()));
            String itemLine = String.format("%-25s %7d %7s %7s",
                    item.getName(),
                    item.getQuantity(),
                    currencyFormatter.format(item.getPriceAtSale()),
                    currencyFormatter.format(lineTotal));
            Label itemLabel = new Label(itemLine);
            itemLabel.setFont(Font.font("Monospaced", Font.getDefault().getSize()));
            itemsBox.getChildren().add(itemLabel);
        }

        Label totalSeparator = new Label("--------------------------------------------------");
        Label totalLabel = new Label("GRAND TOTAL:");
        totalLabel.setFont(Font.font("System Bold", 18));

        Label amountLabel = new Label(currencyFormatter.format(order.getTotalAmount()));
        amountLabel.setFont(Font.font("System Bold", 24));
        amountLabel.setTextFill(Color.web("#006400"));

        Button closeButton = new Button("Close Receipt");
        closeButton.setMaxWidth(Double.MAX_VALUE);
        closeButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(
                title, type, metaBox,
                separator, itemsHeader, itemsBox, totalSeparator,
                totalLabel, amountLabel, new Label(""), closeButton
        );

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
