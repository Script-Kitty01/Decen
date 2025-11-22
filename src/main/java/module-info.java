module decen.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens decen.example to javafx.fxml;
    exports decen.example;
}
