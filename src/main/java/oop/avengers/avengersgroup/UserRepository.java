package oop.avengers.avengersgroup;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class UserRepository extends BaseRepository<User> {

    public UserRepository() {
        super("users");
    }

    public User findByUsername(String username) {
        Document doc = collection.find(eq("username", username)).first();
        if (doc == null) {
            return null;
        }
        return this.convert(doc);
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(
                eq("role", new Document("$ne", "Customer"))
        ).iterator()) {
            while (cursor.hasNext()) {
                users.add(convert(cursor.next()));
            }
        }
        return users;
    }

    @Override
    protected User convert(Document document) {
        User user = new User();
        user.setId(document.getObjectId("_id"));
        user.setUsername(document.getString("username"));
        user.setPassword(document.getString("password"));
        user.setRole(document.getString("role"));
        return user;
    }
}

