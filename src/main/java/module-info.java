module com.markurion.videorenamer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires commons.csv;
    requires java.desktop;

    opens com.markurion.videorenamer to javafx.fxml;
    exports com.markurion.videorenamer;
}