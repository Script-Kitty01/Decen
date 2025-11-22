package decen.example;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private void handleLogin() {
        System.out.println("Button pressed!");
        System.out.println("Login clicked!");

        try {
            App.setRoot("secondary");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
