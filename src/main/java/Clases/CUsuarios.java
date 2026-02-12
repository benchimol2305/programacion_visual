package Clases;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CUsuarios {

    private final Connection conexion;
    private String tabla;

    public CUsuarios(Connection conexion, String tabla) {
        this.conexion = conexion;
        this.tabla = tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public void cargarTablas(ComboBox<String> combo) {
        try {
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery("SELECT nombre_tabla FROM tablas_disponibles");

            combo.getItems().clear();
            while (rs.next()) {
                combo.getItems().add(rs.getString("nombre_tabla"));
            }

        } catch (Exception e) {
            showAlert("ERROR", "No se pudieron cargar las tablas: " + e.toString());
        }
    }

    public void MostrarSexoCombo(ComboBox<String> comboSexo) {
        comboSexo.getItems().clear();
        comboSexo.setValue("Seleccione Sexo");

        String sql = "SELECT * FROM sexo;";

        try {
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                int idSexo = rs.getInt("id");
                String nombreSexo = rs.getString("sexo");

                comboSexo.getItems().add(nombreSexo);
                comboSexo.getProperties().put(nombreSexo, idSexo);
            }

        } catch (Exception e) {
            showAlert("ERROR", "Error al mostrar sexos: " + e.toString());
        }
    }

    public void AgregarUsuario(TextField nombres, TextField apellidos, ComboBox<String> combosexo,
                               TextField edad, DatePicker fnacimiento, File foto) {

        String consulta = "INSERT INTO " + tabla + " (nombres, apellidos, sexo, edad, fnacimiento, foto) VALUES (?,?,?,?,?,?)";

        try (FileInputStream fis = new FileInputStream(foto);
             CallableStatement cs = conexion.prepareCall(consulta)) {

            cs.setString(1, nombres.getText());
            cs.setString(2, apellidos.getText());

            String nombreSexoSeleccionado = combosexo.getSelectionModel().getSelectedItem();
            int idSexo = (int) combosexo.getProperties().get(nombreSexoSeleccionado);
            cs.setInt(3, idSexo);

            cs.setInt(4, Integer.parseInt(edad.getText()));

            LocalDate fechaSeleccionada = fnacimiento.getValue();
            Date fechaSQL = Date.valueOf(fechaSeleccionada);
            cs.setDate(5, fechaSQL);

            cs.setBinaryStream(6, fis, (int) foto.length());
            cs.execute();

            showAlert("Información", "Se guardo correctamente en la tabla: " + tabla);

        } catch (Exception e) {
            showAlert("ERROR", "Error al guardar: " + e.toString());
        }
    }

    public void MostrarUsuarios(TableView<Object[]> TablaTotalUsuarios) {

        TablaTotalUsuarios.getColumns().clear();

        TableColumn<Object[], String> idColumn = new TableColumn<>("Id");
        TableColumn<Object[], String> nombresColumn = new TableColumn<>("Nombres");
        TableColumn<Object[], String> apellidosColumn = new TableColumn<>("Apellidos");
        TableColumn<Object[], String> sexoColumn = new TableColumn<>("Sexo");
        TableColumn<Object[], String> edadColumn = new TableColumn<>("Edad");
        TableColumn<Object[], String> fnacimientoColumn = new TableColumn<>("FNacimiento");
        TableColumn<Object[], String> fotoColumn = new TableColumn<>("Foto");

        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0].toString()));
        nombresColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1].toString()));
        apellidosColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2].toString()));
        sexoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3].toString()));
        edadColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[4].toString()));
        fnacimientoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[5].toString()));
        fotoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[6].toString()));

        TablaTotalUsuarios.getColumns().addAll(idColumn, nombresColumn, apellidosColumn,
                sexoColumn, edadColumn, fnacimientoColumn, fotoColumn);

        String sql = "SELECT " + tabla + ".id, " + tabla + ".nombres, " + tabla + ".apellidos, sexo.sexo, " +
                     tabla + ".edad, " + tabla + ".fnacimiento, " + tabla + ".foto " +
                     "FROM " + tabla + " INNER JOIN sexo ON " + tabla + ".sexo = sexo.id;";

        try {
            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {

                java.sql.Date fechaSQL = rs.getDate("fnacimiento");
                String nuevaFecha = (fechaSQL != null) ? sdf.format(fechaSQL) : null;

                byte[] imageBytes = rs.getBytes("foto");
                Image foto = null;

                if (imageBytes != null) {
                    try {
                        foto = new Image(new ByteArrayInputStream(imageBytes));
                    } catch (Exception e) {
                        showAlert("ERROR", "Error cargando imagen: " + e.toString());
                    }
                }

                Object[] rowData = {
                    rs.getString("id"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("sexo"),
                    rs.getString("edad"),
                    nuevaFecha,
                    foto
                };

                TablaTotalUsuarios.getItems().add(rowData);
            }

        } catch (Exception e) {
            showAlert("ERROR", "Error al mostrar usuarios: " + e.toString());
        }
    }

    public void SeleccionarUsuario(TableView<Object[]> TablaTotalUsuarios, TextField id, TextField nombres,
                                   TextField apellidos, ComboBox<String> combosexo, TextField edad,
                                   DatePicker fnacimiento, ImageView vistaImagen) {

        int fila = TablaTotalUsuarios.getSelectionModel().getSelectedIndex();

        if (fila >= 0) {

            Object[] filaSeleccionada = TablaTotalUsuarios.getItems().get(fila);

            id.setText(filaSeleccionada[0].toString());
            nombres.setText(filaSeleccionada[1].toString());
            apellidos.setText(filaSeleccionada[2].toString());

            combosexo.getSelectionModel().select(filaSeleccionada[3].toString());

            edad.setText(filaSeleccionada[4].toString());

            String fechaString = filaSeleccionada[5].toString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaLocalDate = LocalDate.parse(fechaString, formatter);
            fnacimiento.setValue(fechaLocalDate);

            Image imagen = (Image) filaSeleccionada[6];
            vistaImagen.setImage(imagen);
        }
    }

    public void ModificarUsuario(TextField id, TextField nombres, TextField apellidos,
                                 ComboBox<String> combosexo, TextField edad, DatePicker fnacimiento, File foto) {

        String consulta = "UPDATE " + tabla + " SET nombres=?, apellidos=?, sexo=?, edad=?, fnacimiento=?, foto=? WHERE id=?";

        try {
            CallableStatement cs = conexion.prepareCall(consulta);

            cs.setString(1, nombres.getText());
            cs.setString(2, apellidos.getText());

            String nombreSexoSeleccionado = combosexo.getSelectionModel().getSelectedItem();
            int idSexo = (int) combosexo.getProperties().get(nombreSexoSeleccionado);
            cs.setInt(3, idSexo);

            cs.setInt(4, Integer.parseInt(edad.getText()));

            LocalDate fechaSeleccionada = fnacimiento.getValue();
            Date fechaSQL = Date.valueOf(fechaSeleccionada);
            cs.setDate(5, fechaSQL);

            if (foto != null) {
                FileInputStream fis = new FileInputStream(foto);
                cs.setBinaryStream(6, fis, (int) foto.length());
            } else {
                String obtenerImagenActualSQL = "SELECT foto FROM " + tabla + " WHERE id=?";
                PreparedStatement obtenerImagen = conexion.prepareStatement(obtenerImagenActualSQL);
                obtenerImagen.setInt(1, Integer.parseInt(id.getText()));
                ResultSet rs = obtenerImagen.executeQuery();

                if (rs.next()) {
                    Blob blob = rs.getBlob("foto");
                    if (blob != null) {
                        cs.setBlob(6, blob);
                    } else {
                        cs.setNull(6, Types.BLOB);
                    }
                }
            }

            cs.setInt(7, Integer.parseInt(id.getText()));
            cs.execute();

            showAlert("Información", "Se modifico correctamente en la tabla: " + tabla);

        } catch (Exception e) {
            showAlert("ERROR", "No se modifico: " + e.toString());
        }
    }

    public void EliminarUsuario(TextField id) {

        String consulta = "DELETE FROM " + tabla + " WHERE id=?";

        try {
            CallableStatement cs = conexion.prepareCall(consulta);
            cs.setInt(1, Integer.parseInt(id.getText()));
            cs.execute();

            showAlert("Información", "Se elimino correctamente de la tabla: " + tabla);

        } catch (Exception e) {
            showAlert("ERROR", "No se elimino: " + e.toString());
        }
    }

    public void limpiarCampos(TextField id, TextField nombres, TextField apellidos,
                              ComboBox<String> combosexo, TextField edad, DatePicker fnacimiento) {

        id.setText("");
        nombres.setText("");
        apellidos.setText("");
        edad.setText("");
        fnacimiento.setValue(LocalDate.now());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
