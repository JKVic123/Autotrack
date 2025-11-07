package oop.avengers.avengersgroup;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderRepository extends BaseRepository<Order> {
    public OrderRepository() {
        super("orders");
    }

    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                orders.add(convert(cursor.next()));
            }
        }
        return orders;
    }

    public List<Document> findMostPopularProducts(int limit) {
        List<Document> popularProducts = new ArrayList<>();

        List<Document> pipeline = List.of(
                new Document("$unwind", "$items"),

                new Document("$group", new Document("_id", "$items.productId")
                        .append("name", new Document("$first", "$items.name"))
                        .append("totalQuantity", new Document("$sum", "$items.quantity"))
                ),

                new Document("$sort", new Document("totalQuantity", -1)),

                new Document("$limit", limit)
        );

        try (MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator()) {
            while (cursor.hasNext()) {
                popularProducts.add(cursor.next());
            }
        }
        return popularProducts;
    }

    @Override
    protected Order convert(Document doc) {
        Order order = new Order();
        order.setId(doc.getObjectId("_id"));
        order.setCustomerId(doc.getObjectId("customerId"));
        order.setCashierId(doc.getObjectId("cashierId"));
        order.setPaymentMethodId(doc.getObjectId("paymentMethodId"));
        order.setOrderDate(doc.getDate("orderDate"));

        order.setTotalAmount(BigDecimal.valueOf(doc.getDouble("totalAmount")));

        List<Document> itemDocuments = doc.getList("items", Document.class);
        if (itemDocuments != null) {
            List<OrderItem> orderItems = itemDocuments.stream().map(itemDoc -> {
                OrderItem item = new OrderItem();
                item.setProductId(itemDoc.getObjectId("productId"));
                item.setName(itemDoc.getString("name"));
                item.setQuantity(itemDoc.getInteger("quantity"));


                item.setPriceAtSale(BigDecimal.valueOf(itemDoc.getDouble("priceAtSale")));

                return item;
            }).collect(Collectors.toList());
            order.setItems(orderItems);
        }
        return order;
    }
}


