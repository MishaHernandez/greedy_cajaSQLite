package version6;
/*
*Aqui se guardan todas la funciones que utilizan sentencias sql
*se validan las transacciones y se actualiza el saldo de cada cliente y de caja
*/

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;

public class Operaciones {
    Conector con; //invoca la clase conector
    Voraz voraz; //invoca la clase voraz

    public Operaciones() { //constructor
        con = new Conector(); //instancia de la conexion
        voraz = new Voraz(); //instancia del algoritmo voraz
    }
    
    //===OPERACIONES PRINCIPALES===
    public void depositar(){
        int nroCta, monto, saldoCta; //se solicita cuenta -> se valida monto -> se actualiza saldo.
        String sql; //contiene la sentencia sql
        String tipo = "deposito"; //especifica el tipo de transaccion
        ResultSet result; //guarda el resultado de las consultas
        
        con.conectar(); //abre conexion con bd
        
        nroCta = Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Nro de Cta: "));
        
        try {
            //validar cuenta??
            sql = "select * from cuenta where idcuenta = " + nroCta + ""; //selecciona todos los reg de la tabla "cuenta"
            PreparedStatement pst = con.conexion.prepareStatement(sql); //envia sentencia a la bd
            result = pst.executeQuery(); //ejecuta sentencia y obtiene los datos
            saldoCta = result.getInt("saldo"); //extraemos el campo "saldo"
            
            if (result.getInt("idcuenta") == nroCta) { //si nro de cta existe
                
                validarOperacion(nroCta, tipo); //valida si la cuenta está habilitada para hacer depositos
                
                monto = Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese el Monto: "));
                
                validarMonto(monto, tipo); //verifica si el monto cumple con las restricciones
                validarCaja(monto, tipo, nroCta, saldoCta); //verifica si hay saldo en "caja" y posteriormente en "cuenta"
                guardarTransaccion(monto, nroCta, tipo); //regrista la transaccion en la tabla "transaccion"
                
                JOptionPane.showMessageDialog(null, "Operación realizada con éxito");
                System.out.println("Se ha depositado: " + monto);
                System.out.println(" ");
                
            } else {
                JOptionPane.showMessageDialog(null, "No existe Nro de Cta"); //no se muestra si la cta no existe!!!
            }
        } catch (SQLException e) {
            System.err.println("error en deposito: " + e);
        }
        con.cerrar(); //cierra conexion
    }
    
    public void retirar(){
        int nroCta, monto, saldoCta; //se solicita cuenta -> se valida monto -> se actualiza saldo.
        String tipo = "retiro"; //especifica el tipo de transaccion
        ResultSet result; //guarda el resultado de las consultas
        
        con.conectar(); //abre conexion con bd
        
        nroCta = Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Nro de Cta: "));
        try {
            //validar cuenta??
            PreparedStatement st = con.conexion.prepareStatement("select * from cuenta where idcuenta = " + nroCta + "");
            result = st.executeQuery(); //obtiene los datos de la cuenta
            saldoCta = result.getInt("saldo"); //obtiene el saldo de la tabla "cuenta"
            
            if (result.getInt("idcuenta") == nroCta) {
                
                validarOperacion(nroCta, tipo); //valida si la cuenta está habilitada para hacer retiros
                
                monto = Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Monto: "));
                
                validarMonto(monto, tipo); //verifica si el monto cumple con las restricciones
                validarCaja(monto, tipo, nroCta, saldoCta); //verifica si hay saldo en "caja" y posteriormente en "cuenta"
                guardarTransaccion(monto, nroCta, tipo); //regrista la transaccion en la tabla "transaccion"
                
                JOptionPane.showMessageDialog(null, "Operación realizada con éxito");
                System.out.println("Se ha retirado: " + monto);
                System.out.println(" ");
                
                voraz.vuelto(monto); //devuelve el cambio
                con.cerrar(); //cierra conexion
            }
        } catch (SQLException e) {
            System.err.println("error en retiro: " + e);
        }
        con.cerrar(); //cierra conexion
    }
    
    public void infoSaldo(){
        int nroCta;
        ResultSet result;
        
        con.conectar(); //abre conexion
        
        nroCta=Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Nro de Cta: "));
        try{
            PreparedStatement st = con.conexion.prepareStatement("select * from cuenta where idcuenta = "+nroCta+"");
            result = st.executeQuery(); //ejecutamos la consulta y obtenemos el resultado
            
            if(result.getInt("idcuenta") == nroCta){
                System.out.println("Su saldo es de: "+result.getInt("saldo") +" "+ result.getString("divisa"));
                
                con.cerrar(); //cierra conexion
            }
        }
        catch(SQLException e){
            System.err.println("Error en informacion: "+e);
        }
    }
    
    //===OPERACIONES SECUNDARIAS===
    
    public void validarOperacion(int nroCta, String tipo) throws SQLException{
        int total; 
        String sql, fecha; //se registra sentencia sql y fecha de la transaccion
        ResultSet result; //obtiene el resultado de la consulta sql
        
        fecha = getFecha(); //obtiene la fecha actual del sistema
        sql = "select sum(monto) from transaccion where idcuenta = " + nroCta + " and tipo = '" + tipo + "' and fecha = '" + fecha + "'";
        PreparedStatement pst = con.conexion.prepareStatement(sql);
        result = pst.executeQuery();
        total = result.getInt(1); //obtiene una suma de la tabla "transacciones"
        
        if (total == 3000) { ////si historial de depositos/retiros es igual a 3mil
            JOptionPane.showMessageDialog(null, "Ha sobrepasado el límite de "+tipo+"s que puede hacer por día");
            System.exit(0);
        }
    }
    
    public void validarMonto(int monto, String tipo){
        if(monto < 10 || monto > 3000){ //si no cumple con el intervalo
            JOptionPane.showMessageDialog(null, "Debe hacer un "+tipo+" no menor a 10 ni mayor a 3mil");
            System.exit(0);
        }
    }
    
    public void validarCaja(int monto, String tipo, int nroCta, int saldoCta){
        ResultSet result;
        int saldoCaja, ingreso, egreso, saldoActual, ingresoActual, egresoActual;
        String fecha = getFecha(); //obtiene la fecha actual del sistema
        
        try {
            PreparedStatement st = con.conexion.prepareStatement("select * from caja where fecha = '" + fecha + "'");
            result = st.executeQuery(); //obtiene los datos de "caja" en el día de hoy
            saldoCaja = result.getInt("saldo"); //obtiene el saldo de "caja"

            if (tipo.equals("deposito")) {

                ingreso = result.getInt("ingreso"); //obtiene el saldo de ingresos

                validarCuenta(monto, tipo, nroCta, saldoCta); //verifica el saldo de la tabla "cuenta"
                //si se actualiza el saldo de "cuenta" entonces...
                saldoActual = saldoCaja + monto; //aumenta el saldo de "caja"
                ingresoActual = ingreso + monto; //aumenta el saldo en ingresos

                String sql = "update caja set saldo = " + saldoActual + ", ingreso = " + ingresoActual + " where fecha = '" + fecha + "'";
                PreparedStatement pst = con.conexion.prepareStatement(sql);
                pst.executeUpdate(); //ejecutar consulta de actualizacion en "caja"
                
            }else if(tipo.equals("retiro")){
                
                egreso = result.getInt("egreso"); //obtiene el saldo de egresos

                if ((saldoCaja - monto) >= 0) { //si monto a retirar es menor o igual al saldo de "caja"

                    validarCuenta(monto, tipo, nroCta, saldoCta); //verifica el saldo de la tabla "cuenta"
                    //si se actualiza el saldo de "cuenta" entonces...
                    saldoActual = saldoCaja - monto; //disminuye el saldo de caja
                    egresoActual = egreso + monto; ////aumenta el saldo en egresos

                    String sql = "update caja set saldo = " + saldoActual + ", egreso = " + egresoActual + " where fecha = '" + fecha + "'";
                    PreparedStatement pst = con.conexion.prepareStatement(sql);
                    pst.executeUpdate(); //ejecutar consulta de actualizacion en "caja"

                } else {
                    JOptionPane.showMessageDialog(null, "No hay fondos disponibles en Caja");
                    System.exit(0);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void validarCuenta(int monto, String tipo, int nroCta, int saldoCta){
        String sql, fecha;
        int total = 0;
        ResultSet result;
        
        fecha = getFecha(); //conseguir fecha actual del sistema
        sql = "select sum(monto) from transaccion where idcuenta = " + nroCta + " and tipo = '" + tipo + "' and fecha = '" + fecha + "'";
        
        try {
            PreparedStatement pst = con.conexion.prepareStatement(sql);
            result = pst.executeQuery();
            total = result.getInt(1); //obtiene una suma de la tabla "transacciones"
        }catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        if(tipo.equals("deposito")){
            if(total + monto <= 3000){ //si historial de depositos es menor a 3mil
                int actual = saldoCta + monto; //sumamos el valor depositado
                sql = "update cuenta set saldo = "+actual+" where idcuenta = "+nroCta+"";
                
                try {
                    PreparedStatement st = con.conexion.prepareStatement(sql);
                    st.executeUpdate(); //ejecutar consulta
                    
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }else{
                JOptionPane.showMessageDialog(null, "Sólo puede hacer un "+tipo+" no  mayor a "+ (3000-saldoCta));
                //desea intentarlo denuevo? si no
                System.exit(0);
            }
            
        }else if(tipo.equals("retiro")){
            if(total <= 3000 && monto <= saldoCta){ //si historial de depositos es menor a 3mil y monto no supera el saldo
                int actual = saldoCta - monto; //restamos el valor retirado
                sql = "update cuenta set saldo = "+actual+" where idcuenta = "+nroCta+"";
                
                try {
                    PreparedStatement st = con.conexion.prepareStatement(sql);
                    st.executeUpdate(); //ejecutar consulta de actualizacion
                    
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }else{
                JOptionPane.showMessageDialog(null, "-El saldo es insuficiente\n" + "-El saldo actual es de " + saldoCta);
                //desea intentarlo denuevo? si no
                System.exit(0);
            }
        }
    }
    
    public void guardarTransaccion(int monto, int nroCta, String tipo){
        String fecha = getFecha();
        int nrotransac = getNroTransac();
        String sql="insert into transaccion (idtransac, idcuenta, tipo, monto, fecha) values (?,?,?,?,?)";
        
        try {
            PreparedStatement st = con.conexion.prepareStatement(sql);
            
            st.setInt(1, nrotransac);
            st.setInt(2, nroCta);
            st.setString(3, tipo);
            st.setInt(4, monto);
            st.setString(5, fecha);
            st.execute(); //ejecutar consulta
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public String getFecha(){
        Calendar fecha = new GregorianCalendar();
        
        String formato;
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        
        formato = dia + "/" + (mes+1) + "/" + año; //formato DD/MM/AAAA
        
        return formato;
    } //conseguir fecha actual del sistema
    
    public int getNroTransac(){
        int clave = (int) (Math.random() * 2000 + 1000); //numero aleatorio de 4 cifras
        
        return clave;
    } //generar numero de transaccion (aleatorio)
    
    void caja() {
        String sql, fecha;
        ResultSet result;
        
        fecha = getFecha();
        con.conectar();
        
        try {
            PreparedStatement st = con.conexion.prepareStatement("select * from caja where fecha = '"+fecha+"'");
            result = st.executeQuery();
            boolean dato=result.next(); //obtiene los datos de "caja"
            
            if(dato == false){ //si no existen datos del día
                int saldoCaja = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese Monto de Apertura de Caja:"));
                
                sql="insert into caja (fecha, saldo, ingreso, egreso) values (?,?,?,?)";
                PreparedStatement st1 = con.conexion.prepareStatement(sql);
                
                st1.setString(1, fecha);
                st1.setInt(2, saldoCaja);
                st1.setInt(3, 0);
                st1.setInt(4, 0);
                st1.execute(); //ejecutar consulta de actualizacion
            }
//            else if(result.getInt("saldo") == 0){ //Evita que la caja se quede sin fondos
//                int saldo_caja = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese Monto de Apertura de Caja:"));
//                sql = "update caja set saldo = "+saldo_caja+", ingreso = "+0+", egreso = "+0+" where fecha = '"+fecha+"'";
//                PreparedStatement st1 = con.conexion.prepareStatement(sql);
//                st1.executeUpdate(); //ejecutar consulta
//            }
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        con.cerrar();
    }
}