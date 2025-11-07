package oop.avengers.avengersgroup;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class PaymentMethodRepository extends BaseRepository<PaymentMethod> {

    public PaymentMethodRepository() {
        super("paymentMethods");
    }

    public List<PaymentMethod> findByUserId(ObjectId userId) {
        List<PaymentMethod> methods = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(eq("userId", userId)).iterator()) {
            while (cursor.hasNext()) {
                methods.add(convert(cursor.next()));
            }
        }
        return methods;
    }

    @Override
    protected PaymentMethod convert(Document doc) {
        PaymentMethod method = new PaymentMethod();
        method.setId(doc.getObjectId("_id"));
        method.setUserId(doc.getObjectId("userId"));
        method.setMethodType(doc.getString("methodType"));
        method.setDetails(doc.getString("details"));
        return method;
    }
}
