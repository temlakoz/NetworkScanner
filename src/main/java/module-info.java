module com.networkscanner.networkscanner2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.networkscanner.networkscanner2 to javafx.fxml;
    exports com.networkscanner.networkscanner2;
}