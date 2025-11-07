package oop.avengers.avengersgroup;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Order extends BaseModel {
    private ObjectId customerId;
    private ObjectId cashierId;
    private ObjectId paymentMethodId;
    private Date orderDate;
    private BigDecimal totalAmount;
    private List<OrderItem> items;

    public ObjectId getCustomerId() { return customerId; }
    public void setCustomerId(ObjectId customerId) { this.customerId = customerId; }
    public ObjectId getCashierId() { return cashierId; }
    public void setCashierId(ObjectId cashierId) { this.cashierId = cashierId; }
    public ObjectId getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(ObjectId paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    @Override
    public Document toDocument() {
        List<Document> itemDocuments = this.items.stream()
                .map(OrderItem::toDocument)
                .collect(Collectors.toList());

        Document doc = new Document("orderDate", orderDate)
                .append("totalAmount", totalAmount.doubleValue())
                .append("items", itemDocuments);

        if (customerId != null) {
            doc.append("customerId", customerId);
        }
        if (cashierId != null) {
            doc.append("cashierId", cashierId);
        }
        if (paymentMethodId != null) {
            doc.append("paymentMethodId", paymentMethodId);
        }
        return doc;
    }
}