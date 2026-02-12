package com.mycompany.crudjavafxmysql;

import Clases.CConexion;
import Clases.CUsuarios;
import java.net.URL;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class FXPrincipalController implements Initializable {

    private Connection conexion;
    private CUsuarios dao;
    private Map<String, TextField> campos = new HashMap<>();

    @FXML private TextField txthost;
    @FXML private TextField txtpuerto;
    @FXML private TextField txtdb;
    @FXML private TextField txtuser;
    @FXML private PasswordField txtpassword;
    @FXML private TextField txttabla;

    @FXML private VBox formContainer;
    @FXML private TableView<Object[]> tbDatos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
            dao = new CUsuarios(conexion, tabla);

            dao.construirFormulario(formContainer, campos);
            dao.MostrarTablaGenerica(tbDatos);
        }
    }

    @FXML
    private void guardar(ActionEvent event) {
        if (dao == null) return;
        dao.insertarRegistro(campos);
        dao.MostrarTablaGenerica(tbDatos);
    }

    @FXML
    private void modificar(ActionEvent event) {
        if (dao == null) return;
        dao.modificarRegistro(campos);
        dao.MostrarTablaGenerica(tbDatos);
    }

    @FXML
    private void eliminar(ActionEvent event) {
        if (dao == null) return;
        dao.eliminarRegistro(campos);
        dao.MostrarTablaGenerica(tbDatos);
    }

    @FXML
    private void seleccionarFila(MouseEvent event) {
        if (dao == null) return;
        dao.seleccionarFila(tbDatos, campos);
    }
}
