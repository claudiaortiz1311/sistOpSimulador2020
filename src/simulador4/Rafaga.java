
package simulador4;

public class Rafaga {
    public static final String CPU = "CPU";
    public static final String ES = "ES";
    public String tipo;
    public int duracion;
    
    public Rafaga()
    {
        tipo = CPU;
        duracion = 1;
    }
    
    public Rafaga(Rafaga r)
    {
        tipo = r.tipo;
        duracion = r.duracion;
    }
}
