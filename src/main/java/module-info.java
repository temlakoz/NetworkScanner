module com.example.networkscanner {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.networkscanner to javafx.fxml;
    exports com.example.networkscanner;
}