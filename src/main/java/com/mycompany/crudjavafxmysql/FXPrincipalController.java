/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.crudjavafxmysql;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Cesar
 */
public class FXPrincipalController implements Initializable {
@FXML
private ComboBox<String> cbsexo = new ComboBox<>();
private File selectedFile;
@FXML
private TextField txtnombreImagen;
@FXML
private ImageView vistaimagen;

@FXML 
private TextField txtnombres;

@FXML 
private TextField txtapellidos;
@FXML 
private TextField txtedad;

@FXML 
private DatePicker datenacimiento;

@FXML
private TableView<Object[]> tbUsuarios;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*Clases.CConexion objetoConexion = new Clases.CConexion();
        objetoConexion.estableceConexion();*/
        
        Clases.CUsuarios objetoUsuarios = new Clases.CUsuarios();
        objetoUsuarios.MostrarSexoCombo(cbsexo);
        objetoUsuarios.MostrarUsuarios(tbUsuarios);
    }  
    
@FXML 
private  void openFileChooser(ActionEvent event){
    
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Seleccionar Imagen");
    fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files","*.png","*.jpg","*.gif")
        );

    selectedFile= fileChooser.showOpenDialog(null);
    
    if(selectedFile!=null){
        txtnombreImagen.setText("Imagen seleccionada: "+ selectedFile.getName());
        
        try{
            Image image = new Image(selectedFile.toURI().toString());
            vistaimagen.setImage(image);
        
        }catch (Exception e){
            txtnombreImagen.setText("Error al cargar imagen");
        }
        
    }
    
    else{
        txtnombreImagen.setText("Error de seleccion imagen");
    }
        
}

@FXML

private void guardarUsuario(ActionEvent event){
    
    Clases.CUsuarios objetoUsuarios = new Clases.CUsuarios();
    objetoUsuarios.AgregarUsuario(txtnombres, txtapellidos, cbsexo, txtedad, datenacimiento, selectedFile);
    
}


    
}
