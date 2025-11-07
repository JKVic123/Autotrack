package oop.avengers.avengersgroup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CustomerSignupController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button createAccountButton;
    @FXML
    private Label messageLabel;

    private UserRepository userRepository;

    @FXML
    public void initialize() {
        userRepository = new UserRepository();
    }

    @FXML
    private void onCreateAccountClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            setMessage(Color.RED, "Please fill in all fields.");
            return;
        }

        if (password.length() < 8) {
            setMessage(Color.RED, "Password must be at least 8 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            setMessage(Color.RED, "Passwords do not match.");
            return;
        }

        if (userRepository.findByUsername(username) != null) {
            setMessage(Color.RED, "Username '" + username + "' is already taken.");
            return;
        }


        String hashedPassword = PasswordUtils.hashPassword(password);

        User newCustomer = new User();
        newCustomer.setUsername(username);
        newCustomer.setPassword(hashedPassword);
        newCustomer.setRole("Customer");


        userRepository.insert(newCustomer);


        setMessage(Color.GREEN, "Account created successfully! You can now log in.");


        createAccountButton.setDisable(true);
        usernameField.setDisable(true);


    }

    private void setMessage(Color color, String message) {
        messageLabel.setTextFill(color);
        messageLabel.setText(message);
    }
}

