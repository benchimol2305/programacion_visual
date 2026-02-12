/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Clases;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

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
    
    public void MostrarUsuarios (TableView<Object[]> TablaTotalUsuarios){
        
        Clases.CConexion objetoConexion = new Clases.CConexion();
        
        TableColumn<Object[],String> idColumn = new TableColumn<>("Id");
        TableColumn<Object[],String> nombresColumn = new TableColumn<>("Nombres");
        TableColumn<Object[],String> apellidosColumn = new TableColumn<>("Apellidos");
        TableColumn<Object[],String> sexoColumn = new TableColumn<>("Sexo");
        TableColumn<Object[],String> edadColumn = new TableColumn<>("Edad");
        TableColumn<Object[],String> fnacimientoColumn = new TableColumn<>("FNacimiento");
        TableColumn<Object[],String> fotoColumn = new TableColumn<>("Foto");
        
        
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0].toString()));
        nombresColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1].toString()));
        apellidosColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2].toString()));
        sexoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3].toString()));
        edadColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[4].toString()));
        fnacimientoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[5].toString()));
        fotoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[6].toString()));
        
        TablaTotalUsuarios.getColumns().addAll(idColumn,nombresColumn,apellidosColumn,sexoColumn,edadColumn,fnacimientoColumn,fotoColumn);
        
        String sql = "select usuarios.id,usuarios.nombres,usuarios.apellidos,sexo.sexo,usuarios.edad,usuarios.fnacimiento,usuarios.foto from usuarios inner join sexo ON usuarios.sexo = sexo.id;";
        
        try{
            
            Statement st = objetoConexion.estableceConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy");
            
            while (rs.next()){
                
                java.sql.Date fechaSQL = rs.getDate("fnacimiento");
                
                String nuevaFecha = (fechaSQL != null) ? sdf.format(fechaSQL): null;
                
                byte[] imageBytes = rs.getBytes("foto");
                
                Image foto = null;
                
                if(imageBytes != null){
                    try{
                        foto = new Image(new ByteArrayInputStream(imageBytes));
                    }catch(Exception e){
                        showAlert("ERROR", "Error: " + e.toString());
                    }
                }
                
                Object[] rowData = {
                    rs.getString("Id"),
                    rs.getString("nombres"),
                    rs.getString("apellidos"),
                    rs.getString("sexo"),
                    rs.getString("edad"),
                    nuevaFecha,
                    foto
                };
                
                TablaTotalUsuarios.getItems().add(rowData);
            }
            
        }catch(Exception e){
            showAlert("ERROR", "Error al guardar: " + e.toString());
        } finally{
            objetoConexion.cerrarConexion();
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
