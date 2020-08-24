
package simulador4;

import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Proceso {
    
    private String nombre;
    private int memoria;
    private int arribo;
    private int prioridad;
    public ArrayList<Rafaga> rafagas;
    Color color, colorSecundario;
    //public PanelProcesoItemLista procesoLista;
    
    // PCB
    protected int reloj; // se incrementa en 1 cuando está en ejecución
    //int periodoActual; // la duración de la ráfaga actual
    int indiceRActual;  // número de ráfaga actual
    int indiceParticion; // particion de memoria usado (part. fijas)
    //int limInferior; // límite inf. de memoria usado (reg. valla) (part. variables)
    
    int uqt;
    int listaActual = -1; // usado en colas multinivel
    
    // calculos
    double TR = 0;
    double TE = 0;
    int inicio, inicioE;
    
    // prioridad
    boolean interrumpido;
    
    public Proceso()
    {
        arribo = 0;
        memoria = 0;
        prioridad = 0;
        rafagas = new ArrayList<>();
        //procesoLista = new PanelProcesoItemLista(this);
        reloj = 0;
        indiceRActual = 0;
        interrumpido = false;
        TR = 0;
        TE = 0;
        inicio = 0;
        uqt = 0;
        
        color = Color.BLACK;
        colorSecundario = Color.WHITE;
    }
    
    // clonar
    public Proceso(Proceso p, boolean exacto)
    {
        nombre = p.nombre;
        arribo = p.arribo;
        prioridad = p.prioridad;
        memoria = p.memoria;
        
        if(exacto)
        {
            reloj = p.reloj;
            indiceRActual = p.indiceRActual;
            uqt = p.uqt;
            TR = p.TR;
            TE = p.TE;
            inicio = p.inicio;
            inicioE = p.inicioE;
            interrumpido = p.interrumpido;
        }
        else
        {
            reloj = 0;
            indiceRActual = 0;
            uqt = 0;
            TR = 0;
            TE = 0;
            inicio = 0;
            inicioE = 0;
            interrumpido = false;
        }
        
        
        
        rafagas = new ArrayList<>();
        for(Rafaga r : p.rafagas)
        {
            Rafaga r2 = new Rafaga(r);
            rafagas.add(r2);
        }
        
        color = p.color;
        colorSecundario = p.colorSecundario;
        
        
        
        
    }
    
    boolean finCiclo()
    {
        //if(indiceRActual >= rafagas.size()) return true;
        return reloj >= rafagas.get(indiceRActual).duracion;
    }
    
    boolean finEjecucion()
    {
        return indiceRActual == rafagas.size() - 1;
    }
    
    int duracionActual()
    {
        if(indiceRActual < rafagas.size()) return rafagas.get(indiceRActual).duracion;
        else return -1;
    }
    
    int tiempoRestante()
    {
        if(indiceRActual < rafagas.size()) return rafagas.get(indiceRActual).duracion - reloj;
        else return -1;
    }
    
    
    public String getNombre()
    {
        return nombre;
    }
    
    public Text getNombre2()
    {
        Text texto = new Text(nombre);
        texto.setStyle("-fx-weight: bold");
        texto.setFont(new Font("Arial", 15));
        texto.setFill(color);
        return texto;
    }
    
    public int getArribo()
    {
        return arribo;
    }
    
    public int getMemoria()
    {
        return memoria;
    }
    
    public int getPrioridad()
    {
        return prioridad;
    }
    
    public String getRafagas()
    {
        String linea = "";
        for(Rafaga r : rafagas)
        {
            linea += r.tipo + ":" + r.duracion + ", ";
        }
        if(linea.endsWith(", ")) linea = linea.substring(0, linea.length() - 2);
        return linea;
    }
    
    public void setNombre(String _id)
    {
        nombre = _id;
    }
    
    public void setArribo(int _a)
    {
        arribo = _a;
    }
    
    public void setMemoria(int _mem)
    {
        memoria = _mem;
    }
    
    public void setPrioridad(int _prioridad)
    {
        prioridad = _prioridad;
    }
    
    public int getReloj()
    {
        return reloj;
    }
    
}
