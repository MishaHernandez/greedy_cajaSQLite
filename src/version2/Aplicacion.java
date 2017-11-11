package version2;

//import javax.swing.JOptionPane;

import java.util.InputMismatchException;
import javax.swing.JOptionPane;


public class Aplicacion {

    public static void main(String[] args) {
        Aplicacion ap = new Aplicacion();
        ap.menu();
    }
    
    public void menu(){
        int opc= Integer.parseInt(JOptionPane.showInputDialog(null, "MENU\n"
            + "1: Deposito\n"
            + "2: Retiro\n"
            + "3: Informacion de saldo\n"
            + "4: Salir\n"));
        opciones(opc);
    }
    
    public void opciones(int opc){
        Operaciones op=new Operaciones();
        try {
            switch(opc){
                case 1:
                    op.depositar();
                    break;

                case 2:
                    op.retirar();
                    break;

                case 3:
                    op.infoSaldo();
                    break;

                case 4:
                    System.exit(0);
            }
        }catch (InputMismatchException e) {
            System.out.println("Elegir una opción"+e);
        }
    }
    
}
