package oop.avengers.avengersgroup;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;

public abstract class BaseRepository<T extends BaseModel> {
    protected final MongoCollection<Document> collection;

    public BaseRepository(String collectionName) {
        if (collectionName == null || collectionName.isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty");
        }
        MongoDatabase database = DatabaseHelper.getInstance().getDatabase();
        this.collection = database.getCollection(collectionName);
    }

    public void insert(T entity) {
        Document document = entity.toDocument();
        this.collection.insertOne(document);
        entity.setId(document.getObjectId("_id"));
    }

    public T findById(ObjectId id) {
        Document doc = collection.find(eq("_id", id)).first();
        if (doc == null) {
            return null;
        }
        return this.convert(doc);
    }

    public void update(ObjectId id, T newEntity) {
        Document newDoc = newEntity.toDocument();
        this.collection.replaceOne(eq("_id", id), newDoc);
    }

    public boolean delete(ObjectId id) {
        return this.collection.deleteOne(eq("_id", id)).getDeletedCount() > 0;
    }

    protected abstract T convert(Document document);
}
