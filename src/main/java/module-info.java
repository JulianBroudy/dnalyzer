module com.broudy {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires poi.ooxml;

    opens com.broudy to javafx.fxml;
    exports com.broudy;
    opens com.broudy.boundary.view_controllers to javafx.fxml;
    exports com.broudy.boundary.view_controllers;

}