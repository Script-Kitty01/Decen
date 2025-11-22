package decen.example;

import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void handleLogout() {
        try {
            App.setRoot("primary"); // go back to login
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
