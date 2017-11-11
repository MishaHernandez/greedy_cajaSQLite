package version2;

public class Transac{
    int idtransac, idcuenta, monto;
    String tipo, fecha;
    //se pueden agrgar otros datos de cliente segun se pongan en la BD
    
    public Transac(int idtransac, int idcuenta, String tipo, int monto, String fecha){
        this.idtransac=idtransac;
        this.idcuenta=idcuenta;
        this.monto=monto;
        this.tipo=tipo;
        this.fecha=fecha;
    }
    
    public int getidTransac(){
        return idtransac;
    }
    public void setidTransac(int idcliente){
        this.idtransac=idcliente;
    }
    
    public int getidCuenta(){
        return idcuenta;
    }
    public void setidCuenta(int idcuenta){
        this.idcuenta=idcuenta;
    }
    
    public int getmonto(){
        return monto;
    }
    public void setmonto(int monto){
        this.monto=monto;
    }
    
    public String gettipo(){
        return tipo;
    }
    public void settipo(String tipo){
        this.tipo=tipo;
    }
    
    public String getfecha(){
        return fecha;
    }
    public void setfecha(String fecha){
        this.fecha=fecha;
    }
}
