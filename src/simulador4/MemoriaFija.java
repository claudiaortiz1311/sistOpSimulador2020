
package simulador4;

import java.util.ArrayList;


public class MemoriaFija extends Memoria{
    //List<String> bloquesMem;
    //List<Proceso> procesos;
    
    // p.fijas
    public MemoriaFija()
    {
        particiones = new ArrayList<>();
        so = 0;
        total = 0;
        
        tipo = Memoria.PART_FIJAS;
    }
    
    public MemoriaFija(Memoria clon)
    {
        particiones = new ArrayList<>();
        total = clon.total;
        tipo = clon.tipo;
        so = clon.so;
        
        for(Object t : clon.particiones)
        {
            agregarParticionFija((int)t);
        }
    }
    
    @Override
    public Memoria crear(Memoria clon)
    {
        MemoriaFija mv = new MemoriaFija();
        mv.total = clon.total;
        mv.ponerSO(clon.getSO());
        mv.particiones = new ArrayList<>();
        for(BloqueMemoria bm : clon.particiones)
        {
            mv.agregarParticionFija((int) bm.size);
        }
        mv.tipo = Memoria.PART_VARIABLES;
        return mv;
    }
    
    @Override
    public void ponerSO(int memSO)
    {
        so = memSO;
    }
    
    final void agregarParticionFija(int t)
    {
        particiones.add(new BloqueMemoria(t, "-1"));
        //bloquesMem.add("-1");
        //procesos.add(null);
        total += t;
    }
    
    @Override
    public int buscarEspacio(int m, int algoritmo) {
        int x = -1;
        int max = -1;
        OUTER:
        for (int i = 0; i < particiones.size(); i++) {
            switch (algoritmo) {
            // ff
                case Memoria.FIRST_FIT:
                    if (particiones.get(i).size >= m && 
                            particiones.get(i).nombre.equals("-1")) {
                        //x = i;
                        //break OUTER;
                        return i;
                    }
                    break;
            // bf
                case Memoria.BEST_FIT:
                    if(particiones.get(i).size >= m && 
                            particiones.get(i).nombre.equals("-1"))
                    {
                        if(max == -1)
                        {
                            x = i;
                            max = particiones.get(i).size;
                        }
                        else
                        {
                            if(max > particiones.get(i).size)
                            {
                                x = i;
                                max = particiones.get(i).size;
                            }
                        }
                    }   break;
            // wf
                default:
                    if(particiones.get(i).size >= m && 
                            particiones.get(i).nombre.equals("-1"))
                    {
                        if(max == -1)
                        {
                            x = i;
                            max = particiones.get(i).size;
                        }
                        else
                        {
                            if(max < particiones.get(i).size)
                            {
                                x = i;
                                max = particiones.get(i).size;
                            }
                        }
                    }   break;
            }
        }
        return x;
    }

    @Override
    public void cargarProceso(Proceso p) {
        if(p.indiceParticion < particiones.size())
        {
            //bloquesMem.set(p.indiceParticion, p.getNombre());
            //procesos.set(p.indiceParticion, p);
            int i = p.indiceParticion;
            particiones.set(i, new BloqueMemoria(particiones.get(i).size, p.getNombre()));
        }
    }

    @Override
    public void desasignarProceso(Proceso p) {
        if(p.indiceParticion < particiones.size())
        {
            //bloquesMem.set(p.indiceParticion, "-1");
            //procesos.set(p.indiceParticion, null);
            int i = p.indiceParticion;
            particiones.set(i, new BloqueMemoria(particiones.get(i).size, "-1"));
        }
    }

    @Override
    public String disponible() {
        String linea = "";
        int pos = 0, size = 0;
        while(pos < particiones.size())
        {
            if(particiones.get(pos).nombre.equals("-1"))
            {
                linea += "B" + (pos+1) + " (" + (particiones.get(pos).size) + " KB) + ";
                size += particiones.get(pos).size;
            }
            pos++;
        }
        if(linea.length() == 0) return "0 KB";
        
        
        linea = linea.trim();
        if(linea.endsWith("+")) linea = linea.substring(0, linea.length() - 1);
        linea += "= " + size + " KB";
        return linea.trim();
    }
    
    @Override
    public boolean cabe(int mem)
    {
        boolean hay = false;
        for(BloqueMemoria i : particiones)
        {
            if(i.size >= mem) hay = true;
        }
        return hay;
    }
    
    @Override
    public String resumen()
    {
        String linea = "";
        linea += "Tipo: Particiones fijas" + nl();
        for(BloqueMemoria bm : particiones)
        {
            linea += bm.size + " KB  ";
        }
        linea += nl();
        linea += "Partici\u00f3n del sistema operativo: " + so;
        return linea;
    }
    
    @Override
    public String aString()
    {
        if(particiones.isEmpty()) return "0";
        String l = "";
        for(BloqueMemoria bm : particiones)
        {
            l += bm.size + ",";
        }
        if(l.length() == 0) return "0";
        return l.substring(0, l.length() - 1);
    }
    
    @Override
    public void vaciar()
    {
        for(BloqueMemoria bm : particiones)
        {
            bm.nombre = "-1";
        }
    }
    
    @Override
    public int registroValla(int indiceParticion)
    {
        int suma = 0;
        int i = 0;
        while(i <= indiceParticion)
        {
            BloqueMemoria bm = particiones.get(i);
            suma += bm.size;
            i++;
        }
        return suma;
    }
    
    @Override
    public int getTotal()
    {
        //return total;
        int suma = 0;
        for(BloqueMemoria bm : particiones)
        {
            suma += bm.size;
        }
        return suma;
    }
    
    @Override
    public int getSO()
    {
        return so;
    }
    
}
