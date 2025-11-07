package oop.avengers.avengersgroup;

import org.bson.Document;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Product extends BaseModel {
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;

    public Product() {}

    public Product(String name, String description, BigDecimal price, int stock) {
        this.name = name;
        this.description = description;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price.setScale(2, RoundingMode.HALF_UP);
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public Document toDocument() {
        Document doc = new Document();
        doc.append("name", this.name);
        doc.append("description", this.description);
        doc.append("price", this.price.doubleValue());
        doc.append("stock", this.stock);
        return doc;
    }
}
