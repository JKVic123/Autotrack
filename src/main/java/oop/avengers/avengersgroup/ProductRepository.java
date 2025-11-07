package oop.avengers.avengersgroup;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository extends BaseRepository<Product> {
    public ProductRepository() {
        super("products");
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                products.add(convert(cursor.next()));
            }
        }
        return products;
    }

    public List<Product> searchByName(String query) {
        List<Product> products = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(
                new Document("name", new Document("$regex", query).append("$options", "i"))
        ).iterator()) {
            while (cursor.hasNext()) {
                products.add(convert(cursor.next()));
            }
        }
        return products;
    }

    public List<Product> findLowStockProducts(int threshold) {
        List<Product> products = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(
                com.mongodb.client.model.Filters.lte("stock", threshold)
        ).iterator()) {
            while (cursor.hasNext()) {
                products.add(convert(cursor.next()));
            }
        }
        return products;
    }

    @Override
    protected Product convert(Document doc) {
        Product product = new Product();
        product.setId(doc.getObjectId("_id"));
        product.setName(doc.getString("name"));
        product.setDescription(doc.getString("description"));
        product.setPrice(BigDecimal.valueOf(doc.get("price", Number.class).doubleValue()));

        product.setStock(doc.get("stock", Number.class).intValue());
        return product;
    }
}

