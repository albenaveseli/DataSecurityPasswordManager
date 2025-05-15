module com.example.datasecuritypasswordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens app to javafx.fxml;
    exports app;
    exports controller to javafx.fxml;
    opens controller to javafx.fxml;


}