package version2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;

public class Operaciones {
    Conector con;
    Voraz voraz;
    Transac tr; //necesita los parametros

    public Operaciones() {
        con = new Conector();
        voraz = new Voraz();
    }
    
    public void depositar(){
        int nroCta, monto,saldo;
        String tipo="deposito";
        con.conectar(); //abre conexion
        ResultSet result;
        nroCta=Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Nro de Cta: "));
        try{
            PreparedStatement st = con.conexion.prepareStatement("select * from cuenta where idcuenta="+nroCta+"");
            result = st.executeQuery();
            if(result.getInt("idcuenta")==nroCta){
                monto=Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Monto: "));
                agregarTransac(monto,nroCta,tipo); //guarda transaccion
                //saldo=result.getInt("saldo");
                //actualizarD(monto,nroCta,saldo); //actualizar saldo
                voraz.vueltoD(monto); //imprime el cambio
                con.cerrar(); //cierra conexion
            }
        }
        catch(SQLException e){
            System.err.println("error en deposito: "+e);
        }
    }
    
    public void retirar(){
        int nroCta, monto,saldo;
        String tipo="retiro";
        con.conectar(); //abre conexion
        ResultSet result;
        nroCta=Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Nro de Cta: "));
        try{
            PreparedStatement st = con.conexion.prepareStatement("select * from cuenta where idcuenta="+nroCta+"");
            result = st.executeQuery();
            if(result.getInt("idcuenta")==nroCta){
                monto=Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Monto: "));
                agregarTransac(monto,nroCta,tipo); //guarda transaccion
                saldo=result.getInt("saldo");
                actualizarR(monto,nroCta,saldo); //actualizar saldo
                voraz.vueltoR(monto); //imprime el cambio
                con.cerrar(); //cierra conexion
            }
        }
        catch(SQLException e){
            System.err.println("error en retiro: "+e);
        }
    }
    
    public void infoSaldo(){
        int nroCta;
        con.conectar(); //abre conexion
        ResultSet result;
        nroCta=Integer.parseInt(JOptionPane.showInputDialog(null, "ingrese Nro de Cta: "));
        try{
            PreparedStatement st = con.conexion.prepareStatement("select * from cuenta where idcuenta="+nroCta+"");
            result = st.executeQuery();
            if(result.getInt("idcuenta")==nroCta){
                System.out.println("Su saldo es de: "+result.getInt("saldo") +" "+ result.getString("divisa"));
                con.cerrar(); //cierra conexion
            }
        }
        catch(SQLException e){
            System.err.println("Error en informacion: "+e);
        }
    }
    
    //===OPERACIONES SECUNDARIAS===
    public void agregarTransac(int monto, int nroCta, String tipo){
        String fecha=getFecha();
        int nrotransac=getNroTransac();
        Transac trans;
        
        trans = new Transac(nrotransac,nroCta,tipo,monto,fecha);
        
        try {
            String sql="insert into transaccion (idtransac, idcuenta, tipo, monto, fecha) values (?,?,?,?,?)";
            PreparedStatement st = con.conexion.prepareStatement(sql);
            st.setInt(1, trans.getidTransac());
            st.setInt(2, trans.getidCuenta());
            st.setString(3, trans.gettipo());
            st.setInt(4, trans.getmonto());
            st.setString(5, trans.getfecha());
            st.execute();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void actualizarR(int monto, int nroCta, int saldo){
        int actual=saldo-monto;
        try {
            String sql="update cuenta set saldo= ? where idcuenta= ?";
            PreparedStatement st = con.conexion.prepareStatement(sql);
            st.setInt(1, actual);
            st.setInt(2, nroCta);
            st.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void actualizarD(int monto, int nroCta, int saldo){
        int actual=saldo+monto;
        try {
            String sql="update cuenta set saldo="+actual+" where idcuenta="+nroCta+"";
            PreparedStatement st = con.conexion.prepareStatement(sql);
            st.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    public String getFecha(){
        Calendar fecha = new GregorianCalendar();
        String fecha1;
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        fecha1= dia + "/" + (mes+1) + "/" + año;
        return fecha1;
    }
    
    public int getNroTransac(){
        int clave = (int) (Math.random()*2000+1000);
        return clave;
    }
}