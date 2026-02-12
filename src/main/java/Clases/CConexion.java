package Clases;

import java.sql.Connection;
import java.sql.DriverManager;
import javafx.scene.control.Alert;

public class CConexion {

    private Connection conectar = null;

    public Connection estableceConexion(String host, String puerto, String db, String user, String password) {
        String cadena = "jdbc:postgresql://" + host + ":" + puerto + "/" + db;
        try {
            Class.forName("org.postgresql.Driver");
            conectar = DriverManager.getConnection(cadena, user, password);
            showAlert("Mensaje", "Conexion exitosa a la base: " + db);
        } catch (Exception e) {
            showAlert("Mensaje", "Error: " + e.toString());
        }
        return conectar;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void cerrarConexion() {
        try {
            if (conectar != null && !conectar.isClosed()) {
                conectar.close();
                showAlert("Mensaje", "Conexion cerrada");
            }
        } catch (Exception e) {
            showAlert("Mensaje", "Error al cerrar: " + e.toString());
        }
    }
}
