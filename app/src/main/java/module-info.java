module com.uavguard.app {
    requires javafx.fxml;
    requires javafx.controls;
    requires com.uavguard.utilities;
    requires com.uavguard.plugin;

    opens com.uavguard.app to javafx.fxml;
    exports com.uavguard.app;
}
