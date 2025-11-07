package oop.avengers.avengersgroup;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Cart extends BaseModel {
    private ObjectId userId;
    private List<CartItem> items;

    public Cart(ObjectId userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    public Cart() {
        this.items = new ArrayList<>();
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getCartTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getLineTotal());
        }
        return total;
    }

    @Override
    public Document toDocument() {
        List<Document> itemDocuments = this.items.stream()
                .map(CartItem::toDocument)
                .collect(Collectors.toList());

        return new Document("userId", userId)
                .append("items", itemDocuments);
    }
}
