
package simulador4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XML {
    List<String> log;
    ObservableList<Proceso> procesos;
    Memoria memoria;
    
    public XML()
    {
        log = new ArrayList<>();
        procesos = FXCollections.observableArrayList();
        memoria = null;
    }
    
    void abrir(String archivo) throws SAXException, IOException, ParserConfigurationException{
        log.clear();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(archivo);
        NodeList nodos = doc.getElementsByTagName("info").item(0).getChildNodes();
        
        Node nProcesos = null;
        Node nMemoria = null;
        
        for(int i = 0; i < nodos.getLength(); i++)
        {
            Node n = nodos.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE)
            {
                Element e = (Element) n;
                if(e.getTagName().equals("proceso"))
                {
                    nProcesos = e;
                }
                else if(e.getTagName().equals("memoria"))
                {
                    nMemoria = e;
                }
            }
        }
        
        //  procesos
        if(nProcesos == null) return;
        NodeList nodosP = nProcesos.getChildNodes();
        for(int i = 0; i < nodosP.getLength(); i++)
        {
            Node n = nodosP.item(i);
            if(n.getNodeType()==Node.ELEMENT_NODE)
            {
                Proceso p = new Proceso();
                Element e = (Element) n;
                String tmp;
                p.setNombre( e.getAttribute("id"));
                if((tmp = e.getAttribute("a")).length() > 0)
                    p.setArribo( Integer.parseInt(tmp));
                else
                    p.setArribo(0);
                
                if((tmp = e.getAttribute("mem")).length() > 0)
                    p.setMemoria( Integer.parseInt(tmp) );
                else
                    p.setMemoria(0);
                
                if((tmp = e.getAttribute("pr")).length() > 0)
                    p.setPrioridad( Integer.parseInt(tmp) );
                else
                    p.setPrioridad(0);
                
                NodeList rafagas = e.getChildNodes();
                for(int j = 0; j < rafagas.getLength(); j++)
                {
                    Rafaga r = new Rafaga();
                    Node d = rafagas.item(j);
                    if(d.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element e2 = (Element) d;
                        if(e2.getAttribute("tipo").length() > 0)
                            r.tipo = e2.getAttribute("tipo");
                        else
                            r.tipo = "CPU";
                        try
                        {
                            r.duracion = Integer.parseInt(e2.getTextContent());
                        }catch(Exception ex)
                        {
                            r.duracion = 0;
                        }
                        p.rafagas.add(r);
                    }
                }
                
                boolean agregar = true;
                switch(p.rafagas.size())
                {
                    case 0:
                        log.add("El proceso " + p.getNombre() + " no tiene irrupci\u00f3n. "
                                + "El proceso ser\u00e1 ignorado.");
                        agregar = false;
                        break;
                        
                    case 1:
                        if(!"CPU".equalsIgnoreCase(p.rafagas.get(0).tipo))
                        {
                            log.add("El proceso " + p.getNombre() + " no empieza y/o termina "
                                    + "con ciclo(s) CPU. El proceso ser\u00e1 ignorado.");
                            agregar = false;
                        }
                        break;
                        
                    default:
                        if(!"CPU".equalsIgnoreCase(p.rafagas.get(0).tipo) || 
                                !"CPU".equalsIgnoreCase(p.rafagas.get(p.rafagas.size() - 1).tipo))
                        {
                            log.add("El proceso " + p.getNombre() + " debe empezar y terminar "
                                    + "con ciclos CPU. El proceso ser\u00e1 ignorado.");
                            agregar = false;
                        }
                }
                if(agregar)
                {
                    int x = 0, y;
                    while(x < p.rafagas.size())
                    {
                        y = x + 1;
                        while(y < p.rafagas.size())
                        {
                            if(p.rafagas.get(x).tipo.equalsIgnoreCase(p.rafagas.get(y).tipo))
                            {
                                Rafaga rt = new Rafaga();
                                rt.tipo = p.rafagas.get(x).tipo;
                                rt.duracion = p.rafagas.get(x).duracion + p.rafagas.get(y).duracion;
                                p.rafagas.set(x, rt);
                                p.rafagas.remove(y);
                            }
                            else break;
                        }
                        x++;
                    }
                    if(existeProceso(procesos, p.getNombre()))
                    {
                        log.add("Ya existe un proceso con el nombre " + p.getNombre() + ". El proceso ser\u00e1 ignorado.");
                    }
                    else procesos.add(p);
                }
            }
        }
        
        // memoria
        if(nMemoria == null) return;
        NodeList partes = nMemoria.getChildNodes();
        Element eM = (Element) nMemoria;
        
        int _tipo = Integer.parseInt(eM.getAttribute("tipo"));
        if(_tipo == Memoria.PART_FIJAS)
        {
            memoria = new MemoriaFija();
            for(int i = 0; i < partes.getLength(); i++)
            {
                Node n = partes.item(i);
                if(n.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element e = (Element) n;
                    int q = Integer.parseInt( e.getTextContent() );
                    ((MemoriaFija) memoria).agregarParticionFija(q);
                }
            }
        }
        else
        {
            memoria = new MemoriaVariable();
            memoria.total = Integer.parseInt(eM.getAttribute("total"));
        }
        
        //memoria.so = Integer.parseInt(eM.getAttribute("so"))   ;
        try{memoria.ponerSO(Integer.parseInt(eM.getAttribute("so")));}
        catch(Exception e){memoria.ponerSO(0);}
    }
    
    
    boolean existeProceso(List<Proceso> p, String q)
    {
        for(Proceso r : p)
        {
            if(r.getNombre().equalsIgnoreCase(q))
            {
                return true;
            }
        }
        return false;
    }
    
}
