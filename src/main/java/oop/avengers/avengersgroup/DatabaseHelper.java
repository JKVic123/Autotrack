package oop.avengers.avengersgroup;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseHelper {

    //PRODUCTION (MongoDB Atlas Cloud)
    protected final String DB_URI = "mongodb+srv://autotrackUser:autotrackPassword123@cluster0.mp0cxla.mongodb.net/?appName=Cluster0";

    protected final String DB_NAME = "autotrackdb";

    private static final DatabaseHelper instance = new DatabaseHelper();

    private MongoDatabase database;
    private MongoClient mongoClient;

    private DatabaseHelper() {
    }

    public static DatabaseHelper getInstance() {
        return instance;
    }

    public MongoClient getDatabaseClient() {
        if (this.mongoClient == null) {
            ConnectionString connectionString = new ConnectionString(DB_URI);
            this.mongoClient = MongoClients.create(connectionString);
        }
        return this.mongoClient;
    }

    public MongoDatabase getDatabase() {
        if (this.database == null) {
            this.database = this.getDatabaseClient().getDatabase(DB_NAME);
        }
        return this.mongoClient.getDatabase(DB_NAME);
    }
}
