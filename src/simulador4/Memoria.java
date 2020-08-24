
package simulador4;

import java.util.List;


public abstract class Memoria {
    final static int PART_FIJAS = 0;
    final static int PART_VARIABLES = 1;
    final static int FIRST_FIT = 0;
    final static int BEST_FIT = 1;
    final static int WORST_FIT = 2;
    final static String[] NOMBRES = {"FIRST-FIT", "BEST-FIT", "WORST-FIT"};
    
    int tipo;
    
    protected int total, so;
    protected List<BloqueMemoria> particiones;
    
    protected class BloqueMemoria
    {
        int size;
        String nombre;
        
        BloqueMemoria()
        {
            size = 0;
            nombre = "-1";
        }
        
        BloqueMemoria(int p, String s)
        {
            size = p;
            nombre = s;
        }
    }
    
    public int buscarPorId(String id) {
        for(int i = 0; i < particiones.size(); i++)
        {
            if(particiones.get(i).nombre.equalsIgnoreCase(id))
            {
                return i;
            }
        }
        return -1;
    }
    
    String nl()
    {
        return System.getProperty("line.separator");
    }
    
    public abstract Memoria crear(Memoria clon);
    public abstract void ponerSO(int memSO);
    public abstract int buscarEspacio(int m, int algoritmo);
    public abstract void cargarProceso(Proceso p);
    public abstract void desasignarProceso(Proceso p);
    public abstract String disponible();
    public abstract int getTotal();
    public abstract int getSO();
    public abstract int registroValla(int indiceParticion);
    public abstract void vaciar();
    public abstract String resumen();
    public abstract String aString();
    public abstract boolean cabe(int mem);
    
    //public abstract void agregarParticionFija(int m);
}
