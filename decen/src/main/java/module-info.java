module com.decen {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.decen to javafx.fxml;
    exports com.decen;
}
