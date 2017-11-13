/*
 * conexion con base de datos SQLite
 */
package version2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conector{
    Connection conexion;
    String ruta;

    public void conectar(){ //abrir la conexion
        try{
            ruta = "caja_piura.db"; //dentro del mismo proyecto
            conexion = DriverManager.getConnection("jdbc:sqlite:" +ruta);
            
            if(conexion!=null){ //mensaje de conexion exitosa
                JOptionPane.showMessageDialog(null, "Conectado");
            }
        }
        catch(SQLException e){
            System.err.println("No hay conexion: "+e);
        }
    }
    
    public void cerrar(){ //cerrar conexion con la bd
        try {
            conexion.close();
        } catch (SQLException e) {
            System.err.println("Error de cierre"+e);
        }
    }
    
}
