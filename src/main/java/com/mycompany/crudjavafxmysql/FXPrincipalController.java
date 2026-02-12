package com.mycompany.crudjavafxmysql;

import Clases.CConexion;
import Clases.CUsuarios;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class FXPrincipalController implements Initializable {

    private Connection conexion;
    private CUsuarios usuariosDAO;

    @FXML private TextField txthost;
    @FXML private TextField txtpuerto;
    @FXML private TextField txtdb;
    @FXML private TextField txtuser;
    @FXML private PasswordField txtpassword;
    @FXML private TextField txttabla;

    @FXML private ComboBox<String> cbsexo;

    @FXML private TextField txtnombreImagen;
    @FXML private ImageView vistaimagen;
    @FXML private TextField txtnombres;
    @FXML private TextField txtapellidos;
    @FXML private TextField txtedad;
    @FXML private DatePicker datenacimiento;
    @FXML private TableView<Object[]> tbUsuarios;
    @FXML private TextField txtid;

    private File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtid.setDisable(true);
        txtnombreImagen.setDisable(true);
    }

    @FXML
    private void conectarBD(ActionEvent event) {
        String host = txthost.getText();
        String puerto = txtpuerto.getText();
        String db = txtdb.getText();
        String user = txtuser.getText();
        String password = txtpassword.getText();
        String tabla = txttabla.getText();

        CConexion cc = new CConexion();
        conexion = cc.estableceConexion(host, puerto, db, user, password);

        if (conexion != null) {
            usuariosDAO = new CUsuarios(conexion, tabla);

            usuariosDAO.MostrarSexoCombo(cbsexo);

            tbUsuarios.getColumns().clear();
            tbUsuarios.getItems().clear();
            usuariosDAO.MostrarUsuarios(tbUsuarios);
        }
    }

    @FXML
    private void openFileChooser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            txtnombreImagen.setText("Imagen seleccionada: " + selectedFile.getName());
            try {
                Image image = new Image(selectedFile.toURI().toString());
                vistaimagen.setImage(image);
            } catch (Exception e) {
                txtnombreImagen.setText("Error al cargar imagen");
            }
        } else {
            txtnombreImagen.setText("Error de seleccion de imagen");
        }
    }

    @FXML
    private void guardarUsuario(ActionEvent event) {
        usuariosDAO.AgregarUsuario(txtnombres, txtapellidos, cbsexo, txtedad, datenacimiento, selectedFile);

        tbUsuarios.getColumns().clear();
        tbUsuarios.getItems().clear();

        usuariosDAO.MostrarUsuarios(tbUsuarios);
        usuariosDAO.limpiarCampos(txtid, txtnombres, txtapellidos, cbsexo, txtedad, datenacimiento);
    }

    @FXML
    private void seleccionarUsuario(MouseEvent event) {
        usuariosDAO.SeleccionarUsuario(tbUsuarios, txtid, txtnombres, txtapellidos, cbsexo, txtedad, datenacimiento, vistaimagen);
    }

    @FXML
    private void modificarUsuario(ActionEvent event) {
        usuariosDAO.ModificarUsuario(txtid, txtnombres, txtapellidos, cbsexo, txtedad, datenacimiento, selectedFile);

        tbUsuarios.getColumns().clear();
        tbUsuarios.getItems().clear();

        usuariosDAO.MostrarUsuarios(tbUsuarios);
        usuariosDAO.limpiarCampos(txtid, txtnombres, txtapellidos, cbsexo, txtedad, datenacimiento);
    }

    @FXML
    private void EliminarUsuario(ActionEvent event) {
        usuariosDAO.EliminarUsuario(txtid);

        tbUsuarios.getColumns().clear();
        tbUsuarios.getItems().clear();

        usuariosDAO.MostrarUsuarios(tbUsuarios);
        usuariosDAO.limpiarCampos(txtid, txtnombres, txtapellidos, cbsexo, txtedad, datenacimiento);
    }
}
