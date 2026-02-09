module com.mycompany.crudjavafxmysql {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.base;
    requires java.sql;

    opens com.mycompany.crudjavafxmysql to javafx.fxml;
    exports com.mycompany.crudjavafxmysql;
}
