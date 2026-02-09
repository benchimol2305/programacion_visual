/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import java.sql.Connection;
import java.sql.DriverManager;
import javafx.scene.control.Alert;
/**
 *
 * @author Cesar
 */
public class CConexion {
    
    Connection conectar = null;
    
    private final String host = "localhost";
    private final String puerto= "5432";
    private final String db = "busuarios";
    private final String user= "postgres";
    private final String password = "123456789";
    
    String cadena = "jdbc:postgresql://"+host+":"+puerto+"/"+db;
    
    
    public Connection estableceConexion(){
        
        try{
            Class.forName("org.postgresql.Driver");
            
            conectar = DriverManager.getConnection(cadena, user, password);
            showAlert("Mensaje", "se conecto a la bd");
        }  catch (Exception e){
            showAlert("Mensaje", "no se conecto a la bd, error:"+e.toString());
        }
        
        return conectar;
    }
private void showAlert(String title, String content){
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}  


    public void cerrarConexion(){
        try{
            if(conectar!= null && !conectar.isClosed()){
                conectar.close();
                showAlert("Mensaje", "Conexion cerrada");
          }
      } catch (Exception e){
          showAlert("Mensaje", "Error al cerrar conexion"+ e.toString());
      }
    }    
}
