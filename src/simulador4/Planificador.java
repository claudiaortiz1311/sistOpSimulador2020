
package simulador4;

import java.util.List;

/**
 *
 * @author alexis
 */
public abstract class Planificador {
    final static int FCFS = 0;
    final static int SJF = 1;
    final static int PRIORIDAD = 2;
    final static int SRTF = 3;
    final static int RR = 4;
    final static int MULTINIVEL = 5;
    
    protected List<Proceso> procesos, nuevos, listos, bloqueados;
    protected List<Proceso> lista1, lista2, lista3, lista4;
    protected int reloj;
    protected Proceso pActual;
    protected boolean fin;
    Memoria memoria;
    double TRP = 0,TEP = 0,N = -1;
    int RESET;
    
    int algorMemoria;
    int bloquearTodos;
    int fragExt;
    String descripcion;
    int inicioTexto = 0;
    protected List<String> log;
    
    abstract void siguiente();
    
    
    final int reloj()
    {
        return reloj;
    }
    
    final void memoria(Memoria m)
    {
        memoria = m;
    }
    
    final String resumen()
    {
        if(reloj == -1)
        {
            String particiones = "";
            String texto = "Planificador " + descripcion + "." + nl();
            texto += "Cantidad de procesos: " + procesos.size() + "." + nl();
            texto += "Tipo de memoria: memoria de ";
            if(memoria instanceof MemoriaFija)
            {
                texto += "particiones fijas.";
                for(int i = 0; i < memoria.particiones.size(); i++)
                {
                    Memoria.BloqueMemoria bm = memoria.particiones.get(i);
                    particiones += "B" + (i+1) + " = " + bm.size + " KB, ";
                }
                if(particiones.endsWith(", ")) particiones = particiones.substring(0, particiones.length() - 2);
            }
            else
            {
                texto += "particiones variables.";
            }
            texto += nl() + "Tama\u00f1o total de memoria: " + memoria.total + " KB." + nl();
            if(particiones.length() > 0)
                texto += "Particiones de memoria: " + particiones + nl();
            texto += "Partici\u00f3n del sistema operativo: " + memoria.so;
            return texto;
        }
        
        String texto = "";
        if(fin)
        {
            //finalizado = true;
            texto += "=============   TIEMPOS   =============" + nl();
            texto += "Tiempo de trabajo: " + reloj + nl() + nl();
            texto += calculos() + nl();
            return texto;
        }
        
        texto += "===== " + reloj + " =====" + nl();
        
        if(pActual != null)
        {
            String lista = "";
            if(pActual.listaActual != -1) lista += "(" + pActual.listaActual + " nivel)";
            if(pActual.indiceRActual < pActual.rafagas.size())
            {
                texto += "Proceso actual: " + pActual.getNombre() + "(p. " + 
                    (pActual.getReloj()+1) + "/" + pActual.rafagas.get(pActual.indiceRActual).duracion + " )." + lista + nl();
            }
            else
            {
                texto += "Proceso actual: " + pActual.getNombre() + "(" + (pActual.getReloj()+inicioTexto) + "-ultimo ciclo)." + lista + nl();
            }
        }
        else
            texto += "Proceso actual: ninguno." + nl();
        
        texto += "Nuevos:\n" + listarNuevos() + nl();
        
        if(this instanceof PlanificadorNP)
        {
            texto += "Lista 1: ";
            if(lista1.size() > 0)
            {
                for(Proceso p : lista1)
                {
                    texto += p.getNombre() + "(B" + (inicioTexto+p.indiceParticion) + ")" + ", ";
                }
                texto = texto.substring(0, texto.length()-2);
            }
            else
            {
                texto += "<vac\u00edo>";
            }
            texto += nl();
            
            texto += "Lista 2: ";
            if(lista2.size() > 0)
            {
                for(Proceso p : lista2)
                {
                    texto += p.getNombre() + "(B" + (inicioTexto+p.indiceParticion) + ")" + ", ";
                }
                texto = texto.substring(0, texto.length()-2);
            }
            else
            {
                texto += "<vac\u00edo>";
            }
            texto += nl();
            
            texto += "Lista 3: ";
            if(lista3.size() > 0)
            {
                for(Proceso p : lista3)
                {
                    texto += p.getNombre() + "(B" + (inicioTexto+p.indiceParticion) + ")" + ", ";
                }
                texto = texto.substring(0, texto.length()-2);
            }
            else
            {
                texto += "<vac\u00edo>";
            }
            texto += nl();
            
            texto += "Lista 4: ";
            if(lista4.size() > 0)
            {
                for(Proceso p : lista4)
                {
                    texto += p.getNombre() + "(B" + (inicioTexto+p.indiceParticion) + ")" + ", ";
                }
                texto = texto.substring(0, texto.length()-2);
            }
            else
            {
                texto += "<vac\u00edo>";
            }
            texto += nl();
            
        }
        else
        {
            texto += "Listos:\n" + listarListos() + nl();
        }
        
        texto += "Bloqueados:\n" + listarBloqueados() + nl();
        for(String t : log)
        {
            texto += t + nl();
        }
        
        texto += "Memoria disponible: " + memoria.disponible() + nl();
        return texto;
    }
    
    protected final String nl()
    {
        return System.getProperty("line.separator");
    }
    
    //    devuelve el proceso actual en ejecución, o null
    final Proceso actual()
    {
        return pActual;
    }
    
    final String actualNombre()
    {
        if (pActual == null) return "-1";
        else return pActual.getNombre();
    }
    
    //    Convierte a una línea de string todos los nombres de los procesos en 'listos'
    final String listarListos()
    {
        if(listos.isEmpty()) return "<vac\u00edo>";
        if(listos.size() == 1) return "  " + listos.get(0).getNombre() + "(B" + (inicioTexto+listos.get(0).indiceParticion) + ")";
        String texto = "  ";
        for(int i = 0; i < listos.size(); i++)
        {
            texto += listos.get(i).getNombre() + "(B" + (inicioTexto+listos.get(i).indiceParticion) + ")" + ", ";
        }
        texto = texto.substring(0, texto.length()-2);
        
        return texto;
    }
    
    //  Convierte a una línea de string todos los nombres de los procesos en 'bloqueados'
    final String listarBloqueados()
    {
        if(bloqueados.isEmpty()) return "<vac\u00edo>";
        String linea = "";
        for(int i = 0; i < bloqueados.size(); i++)
        {
            linea += bloqueados.get(i).getNombre() + "(" + (bloqueados.get(i).getReloj()+1) 
                    + "/" + bloqueados.get(i).rafagas.get(bloqueados.get(i).indiceRActual).duracion + ")" + "  ";
        }
        return linea.trim();
    }
    
    //    Convierte a una línea de string todos los nombres de los procesos en 'nuevos'
    final String listarNuevos()
    {
        if(nuevos.isEmpty()) return "<vac\u00edo>";
        String texto = "  ";
        for(int i = 0; i < nuevos.size(); i++)
        {
            texto += nuevos.get(i).getNombre() + "  ";
        }
        return texto.trim();
    }
    
    final boolean fin()
    {
        return fin;
    }
    
    protected final int indiceListos(String idProceso) {
        for(int i = 0; i < listos.size(); i++)
        {
            if(listos.get(i).getNombre().equalsIgnoreCase(idProceso))
            {
                return i;
            }
        }
        return -1;
    }
    
    
    /*
        
    */
    protected String calculos()
    {
        String linea = "";
        if(N != 0)
        {
            linea += "Tiempo de retorno promedio: " + TRP + "\n";
            linea += "Tiempo de espera promedio: " + TEP + "\n";
        }
        return linea;
    }
    
    protected boolean verificarFin()
    {
        if(listos.isEmpty() && pActual == null && bloqueados.isEmpty())
        {
            if(nuevos.isEmpty())
            {
                return true;
            }
            else
            {
                for(Proceso p : nuevos)
                {
                    int mind = memoria.buscarEspacio(p.getMemoria(), algorMemoria);
                    if(mind != -1) return false;
                }
                return true;
            }
        }
        else return false;
    }
    
    protected int tiempoTrabajo()
    {
        int duracion = 0;
        int aux = 0;
        while(!fin && aux < 5000)
        {
            siguiente();
            duracion++;
            aux++;
        }
        //reset();
        
        if(duracion == 0 && aux >= 5000) return -1;
        return duracion;
    }
    
    void reset()
    {
        for(Proceso p : procesos)
        {
            p.indiceRActual = 0;
            p.reloj = 0;
            p.interrumpido = false;
            p.TE = 0;
            p.TR = 0;
            p.inicio = 0;
            p.inicioE = 0;
        }
        
        if(lista1 != null) lista1.clear();
        if(lista2 != null) lista2.clear();
        if(lista3 != null) lista3.clear();
        if(lista4 != null) lista4.clear();
        
        
        reloj = -1;
        listos.clear();
        nuevos.clear();
        bloqueados.clear();
        pActual = null;
        fin = false;
        TEP = 0;
        TRP = 0;
        N = -1;
    }
}
