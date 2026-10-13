module org.example.proyectocrud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.proyectocrud to javafx.fxml;
    exports org.example.proyectocrud;

    opens org.example.proyectocrud.controller to javafx.fxml;
}