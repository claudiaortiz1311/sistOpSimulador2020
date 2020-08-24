
package simulador4;

import java.util.ArrayList;
import java.util.List;


public class PlanificadorNP extends Planificador{
    int q1 = 1, q2 = 2, q3 = 4;
    int listaActual = 0;
    
    public PlanificadorNP(List<Proceso> _p)
    {
        procesos = new ArrayList<>(_p);  // clonar
        nuevos = new ArrayList<>();
        listos = new ArrayList<>();
        bloqueados = new ArrayList<>();
        
        lista1 = new ArrayList<>();
        lista2 = new ArrayList<>();
        lista3 = new ArrayList<>();
        lista4 = new ArrayList<>();
        
        log = new ArrayList<>();
        reloj = -1;
        pActual = null;
        fin = false;
        fragExt = 0;
        N = -1;
        RESET = 0;
        descripcion = "Colas multiniveles";
    }
    
    @Override
    void siguiente() {
        if(fin) return;
        reloj++;
        log.clear();
        int pos;
        
        
        if(pActual != null)
        {
            pActual.reloj++;
            if(pActual.listaActual != 4) pActual.uqt++;
            
            if(pActual.finCiclo())
            {
                if(pActual.indiceRActual == pActual.rafagas.size() - 1) // última ráfaga
                {
                    // terminar
                    pActual.TR += reloj - pActual.inicio;
                    TRP += pActual.TR;
                    TEP += pActual.TE;
                    log.add("Proceso <" + pActual.getNombre() + "> finaliza ejecuci\u00f3n. ");
                    memoria.desasignarProceso(pActual);
                }
                
                else
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
                switch(pActual.listaActual)
                {
                    case 1:
                        lista1.remove(indiceLista(pActual.getNombre(), pActual.listaActual));
                        break;
                        
                    case 2:
                        lista2.remove(indiceLista(pActual.getNombre(), pActual.listaActual));
                        break;
                        
                    case 3:
                        lista3.remove(indiceLista(pActual.getNombre(), pActual.listaActual));
                        break;
                        
                    case 4:
                        lista4.remove(indiceLista(pActual.getNombre(), pActual.listaActual));
                        break;
                }
                pActual = null;
            }
            
            if(pActual != null)
            {
                switch(pActual.listaActual)
                {
                    case 1:
                        if(pActual.uqt >= q1)
                        {
                            pActual.uqt = RESET;
                            pActual.inicioE = reloj;
                            pActual.inicio = reloj;
                            lista2.add(pActual);
                            lista1.remove(0);
                            pActual = null;
                        }
                        break;
                        
                    case 2:
                        if(pActual.uqt >= q2)
                        {
                            pActual.uqt = RESET;
                            pActual.inicioE = reloj;
                            pActual.inicio = reloj;
                            lista3.add(pActual);
                            lista2.remove(0);
                            pActual = null;
                        }
                        break;
                        
                    case 3:
                        if(pActual.uqt >= q3)
                        {
                            pActual.uqt = RESET;
                            pActual.inicioE = reloj;
                            pActual.inicio = reloj;
                            lista4.add(pActual);
                            lista3.remove(0);
                            pActual = null;
                        }
                        break;
                }
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
        
        // memoria
        pos = 0;
        while(pos < nuevos.size())
        {
            Proceso p = nuevos.get(pos);
            int mind = memoria.buscarEspacio(p.getMemoria(), algorMemoria);
            if(mind != -1)
            {
                p.indiceParticion = mind;
                p.inicioE = reloj;
                memoria.cargarProceso(p);
                p.inicio = reloj;
                lista1.add(p);
                nuevos.remove(pos);
                log.add("Proceso <" + p.getNombre() + "> agregado en primer cola de prioridad "
                        + "(" + p.getMemoria() + " KB).");
            
            }else  // no hay memoria
            {
                String linea = memoria.disponible();
                log.add("El proceso <" + p.getNombre() + "> arriba pero no hay "
                        + "memoria disponible. (Disponible: " + linea + ")");
                pos++;
            }
        }
        
        // bloqueados
        if(bloqueados.size() > 0)
        {
            bloqueados.get(0).reloj++;
            if(bloqueados.get(0).finCiclo())
            {
                log.add("Proceso <" + bloqueados.get(0).getNombre() + "> fin ciclo (e/s) ("
                        + bloqueados.get(0).reloj + " ns).");
                bloqueados.get(0).reloj = RESET;
                bloqueados.get(0).indiceRActual++;
                bloqueados.get(0).inicioE = reloj;
                bloqueados.get(0).inicio = reloj;
                lista1.add(bloqueados.get(0));
                bloqueados.remove(0);
            }
        }
        
        // verificar reemplazo
        if(pActual != null)
        {
            if(pActual.listaActual != 1 && lista1.size() > 0)
            {
                if(pActual.listaActual == 2)
                {
                    lista2.get(0).TE += reloj - lista2.get(0).inicioE;
                    //TEP += lista2.get(0).TE;
                    lista2.get(0).reloj = pActual.reloj;
                    lista2.get(0).indiceRActual = pActual.indiceRActual;
                }
                
                if(pActual.listaActual == 3)
                {
                    lista3.get(0).TE += reloj - lista3.get(0).inicioE;
                    //TEP += lista3.get(0).TE;
                    lista3.get(0).reloj = pActual.reloj;
                    lista3.get(0).indiceRActual = pActual.indiceRActual;
                }
                
                if(pActual.listaActual == 4)
                {
                    lista4.get(0).TE += reloj - lista4.get(0).inicioE;
                    //TEP += lista4.get(0).TE;
                    lista4.get(0).reloj = pActual.reloj;
                    lista4.get(0).indiceRActual = pActual.indiceRActual;
                }
                
                pActual = lista1.get(0);
                pActual.listaActual = 1;
            }
        }
        
        if(pActual == null)
        {
            if(lista1.size() > 0)
            {
                pActual = lista1.get(0);
                pActual.listaActual = 1;
                log.add(0, "Proceso <" + pActual.getNombre() + "> en ejecuci\u00f3n "
                        + "(" + pActual.getMemoria() + " KB).");
            }
            else if(lista2.size() > 0)
            {
                pActual = lista2.get(0);
                pActual.listaActual = 2;
                log.add(0, "Proceso <" + pActual.getNombre() + "> en ejecuci\u00f3n "
                        + "(" + pActual.getMemoria() + " KB).");
            }
            else if(lista3.size() > 0)
            {
                pActual = lista3.get(0);
                pActual.listaActual = 3;
                log.add(0, "Proceso <" + pActual.getNombre() + "> en ejecuci\u00f3n "
                        + "(" + pActual.getMemoria() + " KB).");
            }
            else if(lista4.size() > 0)
            {
                pActual = lista4.get(0);
                pActual.listaActual = 4;
                log.add(0, "Proceso <" + pActual.getNombre() + "> en ejecuci\u00f3n "
                        + "(" + pActual.getMemoria() + " KB).");
            }
        }
        
        if(lista1.isEmpty() && lista2.isEmpty() && lista3.isEmpty() && lista4.isEmpty() && bloqueados.isEmpty() && pActual == null)
        {
            fin = true;
            TRP = TRP / N;
            TEP = TEP / N;
        }
    }
    
    private int indiceLista(String nombre, int lista) {
        switch(lista)
        {
            case 1:
                for(int i = 0; i < lista1.size(); i++)
                {
                    if(lista1.get(i).getNombre().equalsIgnoreCase(nombre))
                    {
                        return i;
                    }
                }
                return -1;
                
            case 2:
                for(int i = 0; i < lista2.size(); i++)
                {
                    if(lista2.get(i).getNombre().equalsIgnoreCase(nombre))
                    {
                        return i;
                    }
                }
                return -1;
                
            case 3:
                for(int i = 0; i < lista3.size(); i++)
                {
                    if(lista3.get(i).getNombre().equalsIgnoreCase(nombre))
                    {
                        return i;
                    }
                }
                return -1;
                
            case 4:
                for(int i = 0; i < lista4.size(); i++)
                {
                    if(lista4.get(i).getNombre().equalsIgnoreCase(nombre))
                    {
                        return i;
                    }
                }
                return -1;
                
        }
        return -1;
    }
}
