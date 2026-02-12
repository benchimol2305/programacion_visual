package Clases;

import java.sql.*;
import java.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CUsuarios {

    private final Connection conexion;
    private String tabla;

    private List<String> columnas = new ArrayList<>();
    private List<Integer> tipos = new ArrayList<>();
    private List<String> columnasSinPK = new ArrayList<>();

    public CUsuarios(Connection conexion, String tabla) {
        this.conexion = conexion;
        this.tabla = tabla;
    }

    private void cargarMetadata() throws Exception {
        columnas.clear();
        tipos.clear();
        columnasSinPK.clear();

        String sql = "SELECT * FROM " + tabla + " LIMIT 1";
        Statement st = conexion.createStatement();
        ResultSet rs = st.executeQuery(sql);
        ResultSetMetaData meta = rs.getMetaData();

        int count = meta.getColumnCount();

        for (int i = 1; i <= count; i++) {
            columnas.add(meta.getColumnName(i));
            tipos.add(meta.getColumnType(i));
        }

        // columnas sin la PK (primera columna)
        for (int i = 1; i < columnas.size(); i++) {
            columnasSinPK.add(columnas.get(i));
        }

        rs.close();
        st.close();
    }

    public void construirFormulario(VBox contenedor, Map<String, TextField> campos) {
        try {
            cargarMetadata();
            contenedor.getChildren().clear();
            campos.clear();

            for (String col : columnas) {
                Label lbl = new Label(col + ":");
                TextField txt = new TextField();
                txt.setPromptText(col);
                contenedor.getChildren().addAll(lbl, txt);
                campos.put(col, txt);
            }

        } catch (Exception e) {
            showAlert("ERROR", "Error al construir formulario: " + e.toString());
        }
    }

    public void MostrarTablaGenerica(TableView<Object[]> tablaView) {
        try {
            cargarMetadata();

            String sql = "SELECT * FROM " + tabla;
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);

            tablaView.getColumns().clear();
            tablaView.getItems().clear();

            int count = columnas.size();

            for (int i = 0; i < count; i++) {
                final int index = i;
                TableColumn<Object[], String> col = new TableColumn<>(columnas.get(i));
                col.setCellValueFactory(data ->
                        new SimpleStringProperty(
                                data.getValue()[index] != null ? data.getValue()[index].toString() : ""
                        )
                );
                tablaView.getColumns().add(col);
            }

            while (rs.next()) {
                Object[] row = new Object[count];
                for (int i = 0; i < count; i++) {
                    row[i] = rs.getObject(columnas.get(i));
                }
                tablaView.getItems().add(row);
            }

            rs.close();
            st.close();

        } catch (Exception e) {
            showAlert("ERROR", "Error al mostrar tabla: " + e.toString());
        }
    }

    public void seleccionarFila(TableView<Object[]> tablaView, Map<String, TextField> campos) {
        int fila = tablaView.getSelectionModel().getSelectedIndex();
        if (fila < 0) return;

        Object[] datos = tablaView.getItems().get(fila);

        for (int i = 0; i < columnas.size(); i++) {
            campos.get(columnas.get(i)).setText(
                    datos[i] != null ? datos[i].toString() : ""
            );
        }
    }

    public void insertarRegistro(Map<String, TextField> campos) {
        try {
            cargarMetadata();

            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(tabla).append(" (");

            for (int i = 1; i < columnas.size(); i++) {
                sb.append(columnas.get(i));
                if (i < columnas.size() - 1) sb.append(", ");
            }

            sb.append(") VALUES (");

            for (int i = 1; i < columnas.size(); i++) {
                sb.append("?");
                if (i < columnas.size() - 1) sb.append(", ");
            }

            sb.append(")");

            PreparedStatement ps = conexion.prepareStatement(sb.toString());

            int index = 1;
            for (int i = 1; i < columnas.size(); i++) {
                setValor(ps, index++, tipos.get(i), campos.get(columnas.get(i)).getText());
            }

            ps.executeUpdate();
            ps.close();

            showAlert("Información", "Registro insertado correctamente");

        } catch (Exception e) {
            showAlert("ERROR", "Error al insertar: " + e.toString());
        }
    }

    public void modificarRegistro(Map<String, TextField> campos) {
        try {
            cargarMetadata();

            String pkCol = columnas.get(0);
            String pkVal = campos.get(pkCol).getText();

            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE ").append(tabla).append(" SET ");

            for (int i = 1; i < columnas.size(); i++) {
                sb.append(columnas.get(i)).append("=?");
                if (i < columnas.size() - 1) sb.append(", ");
            }

            sb.append(" WHERE ").append(pkCol).append("=?");

            PreparedStatement ps = conexion.prepareStatement(sb.toString());

            int index = 1;
            for (int i = 1; i < columnas.size(); i++) {
                setValor(ps, index++, tipos.get(i), campos.get(columnas.get(i)).getText());
            }

            setValor(ps, index, tipos.get(0), pkVal);

            ps.executeUpdate();
            ps.close();

            showAlert("Información", "Registro modificado correctamente");

        } catch (Exception e) {
            showAlert("ERROR", "Error al modificar: " + e.toString());
        }
    }

    public void eliminarRegistro(Map<String, TextField> campos) {
        try {
            cargarMetadata();

            String pkCol = columnas.get(0);
            String pkVal = campos.get(pkCol).getText();

            String sql = "DELETE FROM " + tabla + " WHERE " + pkCol + "=?";

            PreparedStatement ps = conexion.prepareStatement(sql);
            setValor(ps, 1, tipos.get(0), pkVal);
            ps.executeUpdate();
            ps.close();

            showAlert("Información", "Registro eliminado correctamente");

        } catch (Exception e) {
            showAlert("ERROR", "Error al eliminar: " + e.toString());
        }
    }

    private void setValor(PreparedStatement ps, int index, int tipo, String valor) throws Exception {
        if (valor == null || valor.isEmpty()) {
            ps.setNull(index, tipo);
            return;
        }

        switch (tipo) {
            case Types.INTEGER:
                ps.setInt(index, Integer.parseInt(valor));
                break;

            case Types.NUMERIC:
            case Types.DECIMAL:
                ps.setBigDecimal(index, new java.math.BigDecimal(valor));
                break;

            default:
                ps.setString(index, valor);
                break;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
