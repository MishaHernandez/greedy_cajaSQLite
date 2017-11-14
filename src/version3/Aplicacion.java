package version3;

import javax.swing.JOptionPane;

public class Aplicacion {

    public static void main(String[] args) {
        Aplicacion ap = new Aplicacion();
        
        ap.menu(); //inicio llamando al metodo menu
        
    }
    
    public void menu(){ //muestra un mensaje con las opciones
        int opc = Integer.parseInt(JOptionPane.showInputDialog(null, "MENU\n"
            + "1: Deposito\n"
            + "2: Retiro\n"
            + "3: Informacion de saldo\n"
            + "4: Salir\n"));
        
        opciones(opc); //llamo al metodo opciones y le paso un numero
    }
    
    public void opciones(int opc){ //llama a los metodos principales
        Operaciones op = new Operaciones(); //instancio la clase operaciones
        
        try {
            switch(opc){
                case 1:
                    op.depositar(); //ejecuta algoritmo voraz
                    break;

                case 2:
                    op.retirar(); //ejecuta algoritmo voraz
                    break;

                case 3:
                    op.infoSaldo(); //informacion del saldo
                    break;

                case 4:
                    System.exit(0); //salir el programa
            }
        }catch (Exception e) {
            System.out.println("Elegir una opción"+e);
        }
    }
    
}
