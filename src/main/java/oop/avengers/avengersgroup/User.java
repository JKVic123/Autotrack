package oop.avengers.avengersgroup;

import org.bson.Document;

public class User extends BaseModel {

    private String username;
    private String role;

    private String password;

    public User() {}


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Document toDocument() {
        Document doc = new Document();

        doc.append("username", this.username);
        doc.append("password", this.password);
        doc.append("role", this.role);
        return doc;
    }
}
