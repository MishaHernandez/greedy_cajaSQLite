package version2;

public class Cliente{
    int codigo;
    String nombre, apellido;
    //se pueden agrgar otros datos de cliente segun se pongan en la BD
    
    public Cliente(int codigo, String nombre, String apellido){
        this.codigo=codigo;
        this.nombre=nombre;
        this.apellido=apellido;
    }
    
    public int getnrCodigo(){
        return codigo;
    }
    public void setCodigo(int codigo){
        this.codigo=codigo;
    }
    
    public String getnombre(){
        return nombre;
    }
    public void setnombre(String nombre){
        this.nombre=nombre;
    }
    
    public String getApellido(){
        return apellido;
    }
    public void setApellid(String apellido){
        this.apellido=apellido;
    }
}
