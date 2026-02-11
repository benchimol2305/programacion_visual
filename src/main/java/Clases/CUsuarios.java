/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import java.io.File;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 *
 * @author Cesar
 */
public class CUsuarios {
    
    public void MostrarSexoCombo(ComboBox<String> comboSexo){
        
        Clases.CConexion objetoConexion= new Clases.CConexion();
        
        comboSexo.getItems().clear();
        
        comboSexo.setValue("Seleccione Sexo");
        
        String sql = "Select * from sexo;";
        
        try {
            Statement st = objetoConexion.estableceConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while(rs.next()){
                int idSexo = rs.getInt("id");
                String nombreSexo = rs.getString("sexo");
                
                comboSexo.getItems().add(nombreSexo);
                
                comboSexo.getProperties().put(nombreSexo, idSexo);
            
            }
        }  catch (Exception e){
             
            showAlert("ERROR", "error al mostrar sexos: "+ e.toString());
        }
        finally{
            objetoConexion.cerrarConexion();
        }
    }
    public void AgregarUsuario(TextField nombres, TextField apellidos, ComboBox<String> combosexo, TextField edad, DatePicker fnacimiento, File foto){
     
        CConexion objetoCConexion = new CConexion();
        
        String consulta ="INSERT INTO usuarios(nombres,apellidos,sexo,edad,fnacimiento,foto) VALUES (?,?,?,?,?,?)";
        
        try(FileInputStream fis = new FileInputStream(foto);
                CallableStatement cs = objetoCConexion.estableceConexion().prepareCall(consulta)){
            
            
            
            cs.setString(1, nombres.getText());
            cs.setString(2, apellidos.getText());
            
            String nombreSexoSeleccionado = combosexo.getSelectionModel().getSelectedItem();
            
            int idSexo = (int) combosexo.getProperties().get(nombreSexoSeleccionado);
            
            cs.setInt(3, idSexo);
            
            cs.setInt(4, Integer.parseInt(edad.getText()));
            
            LocalDate fechaSeleccionada = fnacimiento.getValue();
            Date fechaSQL = Date.valueOf(fechaSeleccionada);
            cs.setDate(5, fechaSQL);
            
            cs.setBinaryStream(6, fis,(int) foto.length());
            cs.execute();
            
            showAlert("Informacion", "Se guardo correctamente");
                    
        }  catch (Exception e){
            
            showAlert("Informacion", "error al guardar"+ e.toString());
        }  finally {
            
            objetoCConexion.cerrarConexion();
        }
    
    }
    
    
    
    
    private void showAlert(String title, String content){
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }  
}
