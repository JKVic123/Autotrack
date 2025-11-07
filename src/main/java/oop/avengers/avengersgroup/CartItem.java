package oop.avengers.avengersgroup;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CartItem {
    private ObjectId productId;
    private String name;
    private int quantity;
    private BigDecimal price; // Changed to BigDecimal

    public CartItem(ObjectId productId, String name, int quantity, BigDecimal price) { // Updated parameter type
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
    }

    public CartItem() {
    }

    public ObjectId getProductId() {
        return productId;
    }

    public void setProductId(ObjectId productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() { // Updated return type
        return price;
    }

    public void setPrice(BigDecimal price) { // Updated parameter type
        this.price = price.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getLineTotal() { // Updated return type
        return this.price.multiply(BigDecimal.valueOf(this.quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    Document toDocument() {
        return new Document("productId", productId)
                .append("name", name)
                .append("quantity", quantity)
                .append("price", price.doubleValue()); // Storing as double in MongoDB for compatibility
    }
}

