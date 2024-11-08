module t12311m0.shoes_store {
    requires javafx.controls;
    requires javafx.fxml;

    opens t12311m0.shoes_store to javafx.fxml;
    exports t12311m0.shoes_store;
    requires java.sql;
    requires com.oracle.database.jdbc;
    requires java.base;
}
