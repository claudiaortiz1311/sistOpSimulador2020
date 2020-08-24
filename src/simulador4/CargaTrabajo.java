
package simulador4;

import java.util.ArrayList;
import java.util.List;


public class CargaTrabajo {
    List<Proceso> procesos;
    Memoria memoria;
    
    public CargaTrabajo(List<Proceso> lista, Memoria mem)
    {
        procesos = new ArrayList<>();
        for(Proceso p : lista)
        {
            procesos.add(new Proceso(p, false));
        }
        
        memoria = mem.crear(mem);
    }
}
