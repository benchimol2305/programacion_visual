module com.mycompany.crudjavafxmysql {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.mycompany.crudjavafxmysql to javafx.fxml;
    exports com.mycompany.crudjavafxmysql;
}
