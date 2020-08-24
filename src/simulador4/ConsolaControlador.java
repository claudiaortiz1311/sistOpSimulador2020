
package simulador4;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class ConsolaControlador implements Initializable {
    @FXML TextFlow consola;
    @FXML JFXButton botAnterior;
    @FXML JFXButton botSiguiente;
    @FXML JFXButton botAhora;
    
    ConfigControlador padre;
    ArrayList<String> items;
    int indiceResumenActual = -1;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        items = new ArrayList<>();
    }    
    
    void iniciar(ConfigControlador config)
    {
        padre = config;
        consola.setLineSpacing(4.0);
    }
    
    void agregarEntrada(String entrada)
    {
        items.add(entrada);
        indiceResumenActual++;
        Text texto = new Text(entrada);
        texto.setFont(new Font(15));
        consola.getChildren().clear();
        consola.getChildren().add(texto);
    }
    
    @FXML
    void botConsolaActionEvent(ActionEvent evento)
    {
        if(items.isEmpty()) return;
        if(evento.getSource() == botAnterior)
        {
            if(indiceResumenActual > 0) indiceResumenActual--;
            Text texto = new Text(items.get(indiceResumenActual));
            texto.setFont(new Font(15));
            consola.getChildren().clear();
            consola.getChildren().add(texto);
        }
        
        if(evento.getSource() == botSiguiente)
        {
            indiceResumenActual++;
            if(indiceResumenActual >= items.size()) indiceResumenActual--;
            Text texto = new Text(items.get(indiceResumenActual));
            texto.setFont(new Font(15));
            consola.getChildren().clear();
            consola.getChildren().add(texto);
        }
        
        if(evento.getSource() == botAhora)
        {
            indiceResumenActual = items.size() - 1;
            Text texto = new Text(items.get(indiceResumenActual));
            texto.setFont(new Font(15));
            consola.getChildren().clear();
            consola.getChildren().add(texto);
        }
    }
    
}
