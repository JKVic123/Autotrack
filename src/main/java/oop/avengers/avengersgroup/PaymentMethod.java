package oop.avengers.avengersgroup;

import org.bson.Document;
import org.bson.types.ObjectId;

public class PaymentMethod extends BaseModel {

    private ObjectId userId;
    private String methodType;
    private String details;

    public PaymentMethod() {}
    public ObjectId getUserId() { return userId; }
    public void setUserId(ObjectId userId) { this.userId = userId; }
    public String getMethodType() { return methodType; }
    public void setMethodType(String methodType) { this.methodType = methodType; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    @Override
    public String toString() {
        return this.methodType + ": " + this.details;
    }

    @Override
    public Document toDocument() {
        return new Document("userId", userId)
                .append("methodType", methodType)
                .append("details", details);
    }
}
