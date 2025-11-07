package oop.avengers.avengersgroup;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderItem {
    private ObjectId productId;
    private String name;
    private int quantity;
    private BigDecimal priceAtSale;

    public OrderItem(ObjectId productId, String name, int quantity, BigDecimal priceAtSale) { // Updated parameter type
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale.setScale(2, RoundingMode.HALF_UP);
    }

    public OrderItem() {}

    public ObjectId getProductId() { return productId; }
    public void setProductId(ObjectId productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPriceAtSale() { return priceAtSale; }
    public void setPriceAtSale(BigDecimal priceAtSale) {
        this.priceAtSale = priceAtSale.setScale(2, RoundingMode.HALF_UP);
    }

    public Document toDocument() {
        return new Document("productId", productId)
                .append("name", name)
                .append("quantity", quantity)
                .append("priceAtSale", priceAtSale.doubleValue()); // Storing as double in MongoDB
    }
}
