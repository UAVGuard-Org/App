module com.uavguard.app {
    requires javafx.fxml;
    requires javafx.controls;
    requires com.uavguard.utilities;

    opens com.uavguard.app to javafx.fxml;
    exports com.uavguard.app;
}
