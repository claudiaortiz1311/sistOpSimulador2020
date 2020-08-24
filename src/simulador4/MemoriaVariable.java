
package simulador4;

import java.util.ArrayList;


public class MemoriaVariable extends Memoria{
    int limInf;
    
    public MemoriaVariable()
    {
        so = 0;
        total = 0;
        limInf = 0;
        particiones = new ArrayList<>();
        
        tipo = Memoria.PART_VARIABLES;
    }
    
    @Override
    public Memoria crear(Memoria clon)
    {
        MemoriaVariable mv = new MemoriaVariable();
        mv.total = clon.total;
        mv.ponerSO(clon.getSO());
        mv.particiones = new ArrayList<>();
        mv.tipo = Memoria.PART_VARIABLES;
        return mv;
    }
    
    @Override
    public void ponerSO(int _so)
    {
        so = _so;
        limInf += so;
    }
    
    @Override
    public int buscarEspacio(int mem, int algoritmo) {
        int x = -1;
        int pos = 0;
        BloqueMemoria particion;
        while(pos < particiones.size())
        {
            particion = particiones.get(pos);
            if( particion.size >= mem && particion.nombre.equals("-1"))
            {
                x = pos;
                break; // first-fit
            }
            pos++;
        }
        if(x == -1)
        {
            if(libre() >= mem)
            {
                crearNuevoBloqueMemoriaAlFinal(mem);
                x = particiones.size() - 1;
            }
        }
        else
        {
            if(((BloqueMemoria) particiones.get(pos)).size > mem)
            {
                crearNuevoBloqueMemoriaIntermedio(x, mem);
            }
        }
        return x;
    }
    
    @Override
    public void cargarProceso(Proceso p)
    {
        particiones.get(p.indiceParticion).nombre = p.getNombre();
    }
    
    @Override
    public void desasignarProceso(Proceso p)
    {
        int pos = 0;
        while(pos < particiones.size())
        {
            BloqueMemoria bm = particiones.get(pos);
            if(bm.nombre.equalsIgnoreCase(p.getNombre()))
            {
                //particiones.set(pos, new BloqueMemoria("-1", bm.size));
                particiones.get(pos).nombre = "-1";
            }
            pos++;
        }
        redimensionar();
    }
    
    @Override
    public String disponible()
    {
        String linea = "";
        int pos = 0, cantidad = 0;
        BloqueMemoria bm;
        while(pos < particiones.size())
        {
            bm = particiones.get(pos);
            if(bm.nombre.equals("-1"))
            {
                linea += bm.size + " KB + ";
                cantidad += bm.size;
            }
            pos++;
        }
        
        if(linea.length() == 0) return libre() + " KB";
        
        cantidad += libre();
        linea += libre() + " KB = " + cantidad;
        
        if(linea.endsWith(",")) linea = linea.substring(0, linea.length() - 1);
        linea += " KB";
        //return String.valueOf(total - limInf);
        return linea;
    }
    
    @Override
    public boolean cabe(int mem)
    {
        boolean hay = false;
        if(  libre() >= mem)
        {
            hay = true;
        }
        return hay;
    }
    
    @Override
    public int registroValla(int indiceParticion)
    {
        int suma = 0;
        int i = 0;
        if(indiceParticion >= particiones.size()) return total;
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
        return total;
    }
    
    @Override
    public int getSO()
    {
        return so;
    }
    
    int libre()
    {
        return total-limInf;
    }
    
    void crearNuevoBloqueMemoriaAlFinal(int mem)
    {
        BloqueMemoria bm = new BloqueMemoria();
        bm.size = mem;
        particiones.add(bm);
        limInf += mem;
    }
    
    void crearNuevoBloqueMemoriaIntermedio(int pos, int mem)
    {
        BloqueMemoria bm1, bm2, bm;
        bm = (BloqueMemoria) particiones.get(pos);
        
        bm1 = new BloqueMemoria();
        bm1.size = mem;
        bm2 = new BloqueMemoria();
        bm2.size = bm.size - mem;
        
        particiones.set(pos, bm1);
        if(pos+1 <= particiones.size())
        {
            particiones.add(pos+1, bm2);
        }
    }
    
    @Override
    public String resumen()
    {
        String linea = "";
        linea += "Tipo: Particiones variables. " + nl();
        linea += "Total: " + total + nl();
        linea += "Partici\u00f3n del sistema operativo: " + so;
        return linea;
    }
    
    @Override
    public void vaciar()
    {
        particiones.clear();
        limInf = so;
    }
    
    @Override
    public String aString()
    {
        return String.valueOf(total);
    }
    
    void redimensionar()
    {
        int pos = 0;
        BloqueMemoria bm1, bm2;
        while(pos < particiones.size())
        {
            bm1 = (BloqueMemoria) particiones.get(pos);
            int pos2 = pos + 1;
            while(pos2 < particiones.size())
            {
                bm2 = particiones.get(pos2);
                if(bm1.nombre.equals("-1") && bm2.nombre.equals("-1"))
                {
                    BloqueMemoria nuevo = new BloqueMemoria();
                    nuevo.size = bm1.size + bm2.size;
                    particiones.set(pos, nuevo);
                    particiones.remove(pos2);
                }
                else pos2++;
            }
            pos++;
        }
        
        if(particiones.size() > 0)
        {
            BloqueMemoria bm = particiones.get(particiones.size() - 1);
            if(bm.nombre.equals("-1"))
            {
                limInf -= bm.size;
                particiones.remove(particiones.size() - 1);
            }
        }
    }

}
