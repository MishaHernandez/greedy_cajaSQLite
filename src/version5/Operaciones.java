package version5;
/*
*Aqui se guardan todas la funciones que utilizan sentencias sql
*se guarda la transaccion y se actualiza el saldo de cada cliente
*/

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;

public class Operaciones {
    Conector con;
    Voraz voraz;

    public Operaciones() {
        con = new Conector();
        voraz = new Voraz();
    }
    
    //===OPERACIONES PRINCIPALES===
    public void depositar(){
        int nroCta, monto, saldo;
        String tipo = "deposito";
        String sql, fecha;
        ResultSet result, result1;
        
        con.conectar(); //abre conexion
        
        nroCta=Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Nro de Cta: "));
        try{
            PreparedStatement st = con.conexion.prepareStatement("select * from cuenta where idcuenta = "+nroCta+"");
            result = st.executeQuery(); //obtiene los datos de la cuenta
            saldo=result.getInt("saldo"); //obtiene el saldo de la tabla "cuenta"
            
            if(result.getInt("idcuenta") == nroCta){ //si la cuenta existe
                //validar operaciones del día -> obtener suma de depositos
                fecha = getFecha();
                sql="select sum(monto) from transaccion where idcuenta = "+nroCta+" and tipo = 'deposito' and fecha = '"+fecha+"'";
                PreparedStatement st1 = con.conexion.prepareStatement(sql);
                result1 = st1.executeQuery(); //obtiene el historial de depositos
                validarOperacion(result1); //valida si la cuenta está habilitada para depositos
                
                monto=Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese el Monto: "));
                
                actualizar_caja_Deposito(monto);
                validarMontoDeposito(monto, result1); //valida el monto a depositar
                guardar_Transaccion(monto,nroCta,tipo); //guarda la transaccion en la tabla "transaccion"
                actualizar_Deposito(monto,nroCta,saldo); //actualiza el saldo de la tabla "cuenta"
                
                JOptionPane.showMessageDialog(null, "Operación realizada con éxito");
                System.out.println("Se ha depositado: "+monto);
                System.out.println(" ");
                
                con.cerrar(); //cierra conexion
            }
        }
        catch(SQLException e){
            System.err.println("error en deposito: "+e);
        }
    }
    
    public void retirar(){
        int nroCta, monto,saldo;
        String tipo = "retiro";
        String sql, fecha;
        ResultSet result, result1;
        
        con.conectar(); //abre conexion
        
        nroCta=Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Nro de Cta: "));
        try{
            PreparedStatement st = con.conexion.prepareStatement("select * from cuenta where idcuenta = "+nroCta+"");
            result = st.executeQuery(); //obtiene los datos de la cuenta
            saldo=result.getInt("saldo"); //obtiene el saldo de la tabla "cuenta"
            
            if(result.getInt("idcuenta") == nroCta){
                //validar operaciones del día -> obtener suma de retiros
                fecha = getFecha();
                sql="select sum(monto) from transaccion where idcuenta = "+nroCta+" and tipo = 'retiro' and fecha = '"+fecha+"'";
                PreparedStatement st1 = con.conexion.prepareStatement(sql);
                result1 = st1.executeQuery(); //obtiene el historial de retiros del día
                validarOperacion(result1); //valida si la cuenta está habilitada para retiros
                
                monto=Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Monto: "));
                
                actualizar_caja_Retiro(monto);
                validarMontoRetiro(monto, result1, saldo); //valida el monto a retirar
                guardar_Transaccion(monto,nroCta,tipo); //guarda la transaccion en la tabla "transaccion"
                actualizar_Retiro(monto,nroCta,saldo); //actualiza el saldo de la tabla "cuenta"
                
                JOptionPane.showMessageDialog(null, "Operación realizada con éxito");
                System.out.println("Se ha retirado: "+monto);
                System.out.println(" ");
                
                voraz.vuelto(monto); //devuelve el cambio
                
                con.cerrar(); //cierra conexion
            }
        }
        catch(SQLException e){
            System.err.println("error en retiro: "+e);
        }
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
    public void guardar_Transaccion(int monto, int nroCta, String tipo){
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
    
    public void actualizar_Retiro(int monto, int nroCta, int saldo){
        int actual = saldo - monto; //restamos el valor retirado
        String sql = "update cuenta set saldo = ? where idcuenta = ?";
        
        try {
            PreparedStatement st = con.conexion.prepareStatement(sql);
            
            st.setInt(1, actual);
            st.setInt(2, nroCta);
            st.executeUpdate(); //ejecutar consulta
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void actualizar_Deposito(int monto, int nroCta, int saldo){
        int actual = saldo + monto; //sumamos el valor depositado
        String sql = "update cuenta set saldo = "+actual+" where idcuenta = "+nroCta+"";
        
        try {
            PreparedStatement st = con.conexion.prepareStatement(sql);
            
            st.executeUpdate(); //ejecutar consulta
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void actualizar_caja_Deposito(int monto){
        ResultSet result;
        int saldo_caja, ingreso, actual_s, actual_i;
        String fecha = getFecha();
        
        try {
            PreparedStatement st = con.conexion.prepareStatement("select * from caja where fecha = '"+fecha+"'");
            result = st.executeQuery(); //obtiene los datos de la cuenta
            saldo_caja = result.getInt("saldo");
            ingreso= result.getInt("ingreso");
            
            actual_s = saldo_caja + monto; //restamos el saldo de caja
            actual_i = ingreso + monto;
            String sql = "update caja set saldo = "+actual_s+", ingreso = "+actual_i+" where fecha = '"+fecha+"'";
            PreparedStatement st1 = con.conexion.prepareStatement(sql);
            st1.executeUpdate(); //ejecutar consulta
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void actualizar_caja_Retiro(int monto){
        ResultSet result;
        int saldo_caja, egreso, actual_s, actual_e;
        String fecha = getFecha();
        
        try {
            PreparedStatement st = con.conexion.prepareStatement("select * from caja where fecha = '"+fecha+"'");
            result = st.executeQuery(); //obtiene los datos de la cuenta
            saldo_caja = result.getInt("saldo");
            egreso= result.getInt("egreso");
            
            if((saldo_caja-monto)>=0){
                actual_s = saldo_caja - monto; //restamos el saldo de caja
                actual_e = egreso + monto;
                String sql = "update caja set saldo = "+actual_s+", egreso = "+actual_e+" where fecha = '"+fecha+"'";
                PreparedStatement st1 = con.conexion.prepareStatement(sql);
                st1.executeUpdate(); //ejecutar consulta
            }else {
                JOptionPane.showMessageDialog(null, "No hay fondos disponibles en Caja");
                System.exit(0);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void validarOperacion(ResultSet result1) throws SQLException{ //valida el efectivo del día
        int total;
        
        total=result1.getInt(1);
        if(total==3000){
            JOptionPane.showMessageDialog(null, "Ha sobrepasado el límite de efectivo que puede mover por día");
            System.exit(0);
        }
    }
    
    public void validarMontoDeposito(int monto, ResultSet result1) throws SQLException{
        int historial;
        
        historial=result1.getInt(1);
        
        if (monto < 10 || monto > 3000) {
            JOptionPane.showMessageDialog(null, "Debe hacer un deposito no mayor a 3mil ni menor a 10");
            System.exit(0);
        } else if ((monto + historial) > 3000) {
            JOptionPane.showMessageDialog(null, "Solo puede hacer un deposito no mayor a " + (3000 - historial));
            System.exit(0);
        }
    }
    
    public void validarMontoRetiro(int monto, ResultSet result1, int saldo) throws SQLException{
        int historial;
        
        historial=result1.getInt(1);
        
        if (saldo >= monto) { //Si retiro no excede el saldo
            if (monto < 10 || monto > 3000) { //si retiro no está entre 10 y 3mil
                JOptionPane.showMessageDialog(null, "Debe hacer un retiro no mayor a 3mil ni menor a 10");
                System.exit(0);
            } else if ((monto + historial) > 3000) { //si retiros del día suman más de 3mil
                JOptionPane.showMessageDialog(null, "Solo puede hacer un retiro no mayor a " + (3000 - historial));
                System.exit(0);
            }
        } else {
            JOptionPane.showMessageDialog(null, "-El saldo es insuficiente\n" + "-El saldo actual es de " + saldo);
            System.exit(0);
        }
    }
    
    public String getFecha(){ //obtiene la fecha del sistema
        Calendar fecha = new GregorianCalendar();
        
        String formato;
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        
        formato = dia + "/" + (mes+1) + "/" + año; //formato DD/MM/AAAA
        
        return formato;
    }
    
    public int getNroTransac(){ //genera un codigo de la transaccion
        int clave = (int) (Math.random() * 2000 + 1000); //numero aleatorio de 4 cifras
        
        return clave;
    }

    void caja() {
        String sql, fecha;
        ResultSet result;
        //apertura de caja con un monto inicial
        con.conectar();
        fecha = getFecha();
        
        try {
            PreparedStatement st = con.conexion.prepareStatement("select * from caja where fecha = '"+fecha+"'");
            result = st.executeQuery(); //obtiene los datos de la cuent
            boolean dato=result.next();
            
            if(dato == false){
                int saldo_caja = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese Monto de Apertura de Caja:"));
                
                sql="insert into caja (fecha, saldo, ingreso, egreso) values (?,?,?,?)";
                PreparedStatement st1 = con.conexion.prepareStatement(sql);
                
                st1.setString(1, fecha);
                st1.setInt(2, saldo_caja);
                st1.setInt(3, 0);
                st1.setInt(4, 0);
                st1.execute(); //ejecutar consulta
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