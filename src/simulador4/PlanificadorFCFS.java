
package simulador4;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author alexis
 */
public class PlanificadorFCFS extends Planificador{
    
    public PlanificadorFCFS(List<Proceso> _p)
    {
        procesos = new ArrayList<>(_p);  // clonar
        nuevos = new ArrayList<>();
        listos = new ArrayList<>();
        bloqueados = new ArrayList<>();
        log = new ArrayList<>();
        reloj = -1;
        pActual = null;
        fin = false;
        fragExt = 0;
        N = -1;
        RESET = 0;
        descripcion = "First Come Firse Served (FCFS)";
    }
    
    @Override
    public void siguiente()
    {
        if(fin) return;
        reloj++;
        log.clear();
        int pos;
        
        // incrementar reloj del proceso en ejecución
        if(pActual != null)
        {
            pActual.reloj++;
            if(pActual.reloj == pActual.rafagas.get(pActual.indiceRActual).duracion)
            {
                if(pActual.indiceRActual == pActual.rafagas.size() - 1) // última ráfaga
                {
                    // terminar
                    pActual.TR += reloj - pActual.inicio;
                    TRP += pActual.TR;
                    TEP += pActual.TE;
                    log.add("Proceso <" + pActual.getNombre() + "> finaliza ejecuci\u00f3n. (TR = " + pActual.TR + ", TE = " + pActual.TE + ")");
                    memoria.desasignarProceso(pActual);
                }else
                {
                    // bloquear
                    pActual.TR += reloj - pActual.inicio;
                    pActual.reloj = RESET - 1; // en la siguiente etapa se suma 1 a los bloqueados, por 
                                                    // eso se inicializa en -1 en vez de 0.
                    pActual.indiceRActual++;
                    bloqueados.add(pActual);
                    log.add("Proceso <" + pActual.getNombre() + "> suspende ejecuci\u00f3n "
                            + "(i. E/S)");
                }
                listos.remove(indiceListos(pActual.getNombre()));
                pActual = null;
            }
        }
        
        if(N == -1) N = procesos.size();
        
        // buscar arribos a 'Nuevos'
        for(Proceso p : procesos)
        {
            if(p.getArribo() == reloj)
            {
                Proceso q = new Proceso(p, false);
                nuevos.add(q);
            }
        }
        
        // si hay procesos en 'Nuevo' esperando pasar a 'Listos'
        pos = 0;
        while(pos < nuevos.size())
        {
            Proceso p = nuevos.get(pos);
            // buscar memoria para enviarlo a 'Listos'
            int mind = memoria.buscarEspacio(p.getMemoria(), algorMemoria);
            if(mind != -1)
            {
                p.indiceParticion = mind;
                p.inicioE = reloj;
                memoria.cargarProceso(p);
                p.inicio = reloj;
                listos.add(p);
                nuevos.remove(pos);
                log.add("Proceso <" + p.getNombre() + "> agregado en cola de listos "
                        + "(" + p.getMemoria() + " KB).");
            
            }else  // no hay memoria
            {
                String linea = memoria.disponible();
                log.add("El proceso <" + p.getNombre() + "> arriba pero no hay "
                        + "memoria disponible. (Disponible: " + linea + ")");
                pos++;
            }
        }
        
        // control. procesos bloqueados
        if(!bloqueados.isEmpty())
        {
            bloqueados.get(0).reloj++;
            if(bloqueados.get(0).reloj == bloqueados.get(0).rafagas.get(bloqueados.get(0).indiceRActual).duracion)
            {
                log.add("Proceso <" + bloqueados.get(0).getNombre() + "> fin ciclo <E/S> ("
                        + bloqueados.get(0).reloj + " ns).");
                bloqueados.get(0).reloj = RESET;
                bloqueados.get(0).indiceRActual++;
                bloqueados.get(0).inicioE = reloj;
                bloqueados.get(0).inicio = reloj;
                listos.add(bloqueados.get(0));
                bloqueados.remove(0);
            }
        }
        
        // ejecutar listos[0]
        if(pActual == null)
        {
            if(!listos.isEmpty())
            {
                listos.get(0).TE += reloj - listos.get(0).inicioE;
                Proceso sig = listos.get(0);
                pActual = sig;
                //pActual.inicio = reloj;
                log.add(0, "Proceso <" + sig.getNombre() + "> en ejecuci\u00f3n "
                        + "(" + sig.getMemoria() + " KB).");  // prioridad
            }
        }
        
        if(verificarFin())
        {
            fin = true;
            TRP = TRP / N;
            TEP = TEP / N;
        }
    }
    
    void anterior()
    {
        if(reloj == -1) return;
        log.clear();
        int pos;
        reloj--;
        
        if(pActual != null)
        {
            pActual.reloj--;
            if(pActual.reloj == 0)
            {
                pActual.indiceRActual--;
                if(pActual.indiceRActual < 0)
                {
                    memoria.desasignarProceso(pActual);
                }
                else
                {
                    if(bloqueados.size() > 0)
                    {
                        if(bloqueados.get(0).reloj > 0) bloqueados.get(0).reloj--;
                    }
                    bloqueados.add(0, pActual);
                }
                listos.remove(indiceListos(pActual.getNombre()));
                pActual = null;
            }
        }
        
        for(Proceso p : listos)
        {
        }
    }
}
