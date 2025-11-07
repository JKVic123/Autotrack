package oop.avengers.avengersgroup;

import org.bson.Document;
import org.bson.types.ObjectId;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import static com.mongodb.client.model.Filters.eq;

public class CartRepository extends BaseRepository<Cart> {
    public CartRepository() {
        super("carts");
    }

    public Cart findByUserId(ObjectId userId) {
        Document doc = collection.find(eq("userId", userId)).first();
        if (doc == null) {
            return null;
        }
        return convert(doc);
    }

    @Override
    protected Cart convert(Document doc) {
        Cart cart = new Cart();
        cart.setId(doc.getObjectId("_id"));
        cart.setUserId(doc.getObjectId("userId"));
        List<Document> itemDocuments = doc.getList("items", Document.class);

        List<CartItem> cartItems = itemDocuments.stream().map(itemDoc -> {
            CartItem item = new CartItem();
            item.setProductId(itemDoc.getObjectId("productId"));
            item.setName(itemDoc.getString("name"));
            item.setQuantity(itemDoc.getInteger("quantity"));

            item.setPrice(BigDecimal.valueOf(itemDoc.getDouble("price")));

            return item;
        }).collect(Collectors.toList());

        cart.setItems(cartItems);
        return cart;
    }
}
