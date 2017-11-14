package version3;

public class Voraz {
    
    public void vuelto(int monto){
        int[] moneda = {200,100,50,20,10};
        int[] cambio = calcula(monto, moneda);
        
        System.out.println("Se ha entregado billetes de: ");
        for(int i = 0; i < cambio.length; i++) //imprime los billetes cambiados
            System.out.println(moneda[i]+" = "+cambio[i]+" unidad(es)");
    }
    
    //algoritmo voraz
    public int[] calcula(int monto, int[] moneda){
	int[] solucion = new int[moneda.length];
        
	for(int i=0; i<moneda.length; i++){
		while (moneda[i] <= monto){
			solucion[i]++;
			monto = monto - moneda[i];
		}
	}
	return solucion;
    }
}
