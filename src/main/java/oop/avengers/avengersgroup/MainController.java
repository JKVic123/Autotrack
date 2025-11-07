package oop.avengers.avengersgroup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label messageLabel;

    private UserRepository userRepository = new UserRepository();

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = userRepository.findByUsername(username);

        if (user == null) {
            messageLabel.setText("Invalid username or password.");
            return;
        }

        boolean passwordMatches = PasswordUtils.verifyPassword(password, user.getPassword());

        if (passwordMatches) {
            messageLabel.setText("Login successful!");

            if (user.getRole().equals("Admin")) {
                loadDashboard("admin-dashboard.fxml", "Admin Dashboard", user);
            } else if (user.getRole().equals("Cashier")) {
                loadDashboard("cashier-dashboard.fxml", "Cashier Dashboard", user);
            } else if (user.getRole().equals("Customer")) {
                // THIS IS THE UPDATED PART
                loadDashboard("customer-dashboard.fxml", "AutoTrack Storefront", user);
            } else {
                messageLabel.setText("Unknown user role.");
            }
        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    protected void onSignUpClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AutoTrackApplication.class.getResource("customer-signup.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage signUpStage = new Stage();
            signUpStage.setTitle("Create Customer Account");
            signUpStage.setScene(scene);
            signUpStage.initModality(Modality.APPLICATION_MODAL);

            signUpStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error: Could not load sign-up page.");
        }
    }

    private void loadDashboard(String fxmlFile, String title, User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AutoTrackApplication.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());

            Object controller = fxmlLoader.getController();

            if (controller instanceof CashierDashboardController) {
                ((CashierDashboardController) controller).initData(user);
            } else if (controller instanceof AdminDashboardController) {
                ((AdminDashboardController) controller).initData(user);
            } else if (controller instanceof CustomerDashboardController) {
                ((CustomerDashboardController) controller).initData(user);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);

            stage.sizeToScene();
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error: Could not load dashboard.");
        }
    }
}

