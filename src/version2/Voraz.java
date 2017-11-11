package version2;

import java.util.ArrayList;
import javax.swing.JOptionPane;

public class Voraz {
    
    public void vueltoD(int deposito){
        int[] Billetes={200, 100, 50, 20, 10}; //billetes disponibles en el cajero
        ArrayList Solucion = new ArrayList(); //conjunto solucion/ billetes devueltos
        
        int dinero=Integer.parseInt(JOptionPane.showInputDialog(null,"Dinero disponible")); //dinero entregado
        int x=0; //billete seleccionado para devolver
        int i=0; //contador
        int acum=0; //acumula los billetes devueltos
        int dif=dinero-deposito; //diferencia entre el deposito y el dinero entregado
        int vuelto=dif;
        
        if(deposito<=3000 && deposito>=10){ //verificamos el maximo retiro que se puede solicitar
            while(acum != dif){ //mientras los billetes hasta ahora devueltos no cumplan el valor solicitado...
                vuelto=vuelto-x; //se acumula la diferencia
                x=seleccionar(Billetes, vuelto); //eleccion del billete adecuado y guardar -> revisar en linea 48
                acum=acum+x; //guarda el billete elegido
                Solucion.add(x); //agregar al conjunto solucion
                i++;
            }
            System.out.println("Billetes devueltos: ");
            for (int j = 0; j<Solucion.size(); j++) { //recorrido e impresion del conjunto solución
                System.out.println(Solucion.get(j));
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "El retiro debe ser mayor de 10 y no exceder los 3 mil");
        }
    }
    
    public void vueltoR(int retiro){
        int[] Billetes={200, 100, 50, 20, 10}; //billetes disponibles en el cajero
        ArrayList Solucion = new ArrayList(); //conjunto solucion/ billetes devueltos
        
        int x=0; //billete seleccionado para devolver
        int i=0; //contador
        int acum=0; //acumula los billetes devueltos
        int vuelto=retiro; //diferencia en cada retiro respecto al monto solicitado
        
        if(retiro<=3000 && retiro>=10){ //verificamos el maximo retiro que se puede solicitar
            while(acum != retiro){ //mientras los billetes hasta ahora devueltos no cumplan el valor solicitado...
                vuelto=vuelto-x; //se acumula la diferencia
                x=seleccionar(Billetes, vuelto); //eleccion del billete adecuado y guardar -> revisar en linea 48
                acum=acum+x; //guarda el billete elegido
                Solucion.add(x); //agregar al conjunto solucion
                i++;
            }
            System.out.println("Billetes devueltos: ");
            for (int j = 0; j<Solucion.size(); j++) { //recorrido e impresion del conjunto solución
                System.out.println(Solucion.get(j));
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "El retiro debe ser mayor de 10 y no exceder los 3 mil");
        }
    }
    
    int seleccionar(int[] Bill, int dif){ //metodo de eleccion del billete ideal
        int aux=0;
        
        for (int j = 0; j < Bill.length; j++) { //recorrido de los billetes disponibles
            if(Bill[j]<=dif){ //si es menor que la cantidad por devolver
                aux=Bill[j]; //guardar billete ideal
                break;
            }
        }
        return aux; //-> revisar linea 36
    }
}
