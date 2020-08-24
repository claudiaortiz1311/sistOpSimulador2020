
package simulador4;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;


public class AgProcControlador implements Initializable {
    @FXML StackPane panel;
    @FXML JFXButton botAgregar;
    @FXML JFXTextField txtNombre;
    @FXML JFXTextField txtArribo;
    @FXML JFXTextField txtMemoria;
    @FXML JFXTextField txtPrioridad;
    @FXML JFXButton botAgregarRafaga;
    @FXML JFXButton botLimpiar;
    @FXML JFXButton botBorrarRafaga;
    @FXML JFXListView listaRafagas;
    @FXML Label etqTituloDialogo;
    @FXML Label etqError;
    
    ConfigControlador padre;
    ObservableList<Rafaga> rafagas;
    JFXDialog dialogo;
    boolean editar = false;
    int indice;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rafagas = FXCollections.observableArrayList();
        etqTituloDialogo.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 24");
        etqError.setWrapText(true);
        etqError.setTextAlignment(TextAlignment.LEFT);
    }    
    
    void inicializar(ConfigControlador cc, JFXDialog d, int ind, Proceso p)
    {
        padre = cc;
        dialogo = d;
        
        if(p != null)
        {
            txtNombre.setText(p.getNombre());
            txtArribo.setText(String.valueOf(p.getArribo()));
            txtMemoria.setText(String.valueOf(p.getMemoria()));
            txtPrioridad.setText(String.valueOf(p.getPrioridad()));
            
            rafagas.clear();
            listaRafagas.getItems().clear();
            for(Rafaga r : p.rafagas)
            {
                rafagas.add(new Rafaga(r));
                String item = r.tipo + " " + r.duracion;
                listaRafagas.getItems().add(item);
            }
            editar = true;
            etqTituloDialogo.setText("Editando " + p.getNombre());
        }
        else
        {
            int i = 1;
            String nuevoNombre = "1";
            while(padre.existeProceso(nuevoNombre))
            {
                i++;
                nuevoNombre = String.valueOf(i);
            }
            txtNombre.setText(nuevoNombre);
        }
        txtNombre.requestFocus();
        indice = ind;
    }
    
    @FXML
    void botLimpiarActionEvent(ActionEvent evento)
    {
        rafagas.clear();
        listaRafagas.getItems().clear();
    }
    
    @FXML
    void botAgregarRafagaActionEvent(ActionEvent evento)
    {
        
        VBox vb = new VBox();
        JFXRadioButton rbCPU = new JFXRadioButton("CPU"), rbES = new JFXRadioButton("E/S");
        Label etqDuracion = new Label("Duracion:");
        JFXTextField txtDuracion = new JFXTextField();
        txtDuracion.setAlignment(Pos.CENTER);
        txtDuracion.setStyle("-fx-font-size: 14");
        
        EventHandler<KeyEvent> keyevent = (KeyEvent e) ->
        {
            if(e.getCharacter().length() > 0)
            {
                char c = e.getCharacter().charAt(0);
                if(c < '0' || c > '9') e.consume();
            }
        };
        txtDuracion.setOnKeyTyped(keyevent);
        
        ToggleGroup tg = new ToggleGroup();
        rbCPU.setToggleGroup(tg);
        rbES.setToggleGroup(tg);
        rbCPU.setSelected(true);
        rbES.setSelected(false);
        
        GridPane gp = new GridPane();
        GridPane.setRowIndex(rbCPU, 0);
        GridPane.setRowIndex(rbES, 0);
        GridPane.setRowIndex(etqDuracion, 1);
        GridPane.setRowIndex(txtDuracion, 1);
        GridPane.setColumnIndex(rbCPU, 0);
        GridPane.setColumnIndex(rbES, 1);
        GridPane.setColumnIndex(etqDuracion, 0);
        GridPane.setColumnIndex(txtDuracion, 1);
        GridPane.setMargin(rbCPU, new Insets(8, 8, 8, 8));
        GridPane.setMargin(rbES, new Insets(8, 8, 8, 8));
        GridPane.setMargin(etqDuracion, new Insets(16, 8, 16, 8));
        GridPane.setMargin(txtDuracion, new Insets(16, 8, 16, 8));
        gp.getChildren().addAll(rbCPU, rbES, etqDuracion, txtDuracion);
        vb.getChildren().add(gp);
        
        JFXButton boton = new JFXButton("Agregar");
        boton.setStyle("-fx-background-color:  #037bfc; -fx-text-fill: white;");
        
        
        JFXDialogLayout dl = new JFXDialogLayout();
        JFXDialog d = new JFXDialog(panel, dl, JFXDialog.DialogTransition.CENTER);
        
        boton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me)->{
            Rafaga r = new Rafaga();
            if(rbCPU.isSelected()) r.tipo = Rafaga.CPU;
            else r.tipo = Rafaga.ES;
            
            try
            {
                int duracion = Integer.parseInt(txtDuracion.getText());
                r.duracion = duracion;
            }
            catch(Exception ex)
            {
                r.duracion = 0;
            }
            
            if(rafagas.size() > 0)
            {
                Rafaga tmp = rafagas.get(rafagas.size() - 1);
                if(tmp.tipo.equals(r.tipo))
                {
                    rafagas.get(rafagas.size() - 1).duracion += r.duracion;
                }
                else
                {
                    rafagas.add(r);
                }
            }
            else 
            {
                rafagas.add(r);
            }
            actualizarListaRafagas();
            d.close();
        });
        
        dl.setBody(vb);
        dl.setActions(boton);
        txtDuracion.requestFocus();
        d.show();
    }
    
    void actualizarListaRafagas()
    {
        listaRafagas.getItems().clear();
        for(Rafaga r : rafagas)
        {
            String item = r.tipo + " " + r.duracion;
            listaRafagas.getItems().add(item);
        }
    }
    
    @FXML
    void botAgregarActionEvent(ActionEvent evento)
    {
        if(txtNombre.getText().length() == 0)
        {
            dialogo("Error", "Especificar un nombre para el proceso.");
            return;
        }
        
        //if(padre.existeProceso(txtNombre.getText()))
        //{
            //dialogo("Error", "Ya existe un proceso con el nombre " + txtNombre.getText().toUpperCase());
            //return;
        //}
        
        int mem;
        try {mem = Integer.parseInt(txtMemoria.getText());}
        catch(Exception e) {mem = 0;}
        
        if(mem == 0)
        {
            dialogo("Error", "Elegir un valor entero positivo de memoria.");
            return;
        }
        
        if(padre.memoria != null)
        {
            if(!padre.memoria.cabe(mem))
            {
                dialogo("Error", "No hay espacio suficiente en la memoria para ejecutar el proceso con " + mem + " KB.");
                return;
            }
        }
        
        if(rafagas.isEmpty())
        {
            dialogo("Error", "No se establecio ninguna irrupcion.");
            //etqError.setText("No se estableci\u00f3 ninguna irrupci\u00f3n.");
            return;
        }
        
        if((rafagas.get(0).tipo.equals(Rafaga.ES) || rafagas.get(rafagas.size() - 1).tipo.equals(Rafaga.ES))
                ||
            (rafagas.get(0).tipo.equals(Rafaga.CPU) && rafagas.get(0).duracion == 0)
                ||
            (rafagas.get(rafagas.size() - 1).tipo.equals(Rafaga.CPU) && rafagas.get(rafagas.size() - 1).duracion == 0))
        {
            dialogo("Error", "Las irrupciones deben comenzar y terminar con un ciclo de CPU de duracion mayor a 0.");
            //etqError.setText("Las irrupciones deben comenzar y terminar con un ciclo de CPU de duraci\u00f3n mayor a 0.");
            return;
        }
        
        
        Proceso p = new Proceso();
        p.setNombre(txtNombre.getText());
        if(txtArribo.getText().length() > 0) p.setArribo(Integer.parseInt(txtArribo.getText()));
        else p.setArribo(0);
        p.setMemoria(mem);
        if(txtPrioridad.getText().length() > 0) p.setPrioridad(Integer.parseInt(txtPrioridad.getText()));
        else p.setPrioridad(0);
        
        for(Rafaga r : rafagas)
        {
            p.rafagas.add(new Rafaga(r));
        }
        
        rafagas.clear();
        
        if(editar)
        {
            if(indice == -1) indice = padre.procesos.size() - 1;
            padre.cambiarProceso(indice, p);
            if(dialogo.isVisible()) dialogo.close();
        }
        else
        {
            // agregar
            p.color = padre.coloresProcesos[padre.indColor];
            p.colorSecundario = padre.coloresProcesos[padre.indColor + 1];
            padre.indColor += 2;
            if(padre.indColor >= padre.coloresProcesos.length) padre.indColor = 0;
            if(padre.agregarProceso(p))
            {
                etqError.setText("");
                //if(dialogo == null) return;
                //if(dialogo.isVisible()) dialogo.close();
            }
            else
            {
                dialogo("Error", "Ya existe un proceso con el nombre " + txtNombre.getText().toUpperCase());
            }
        }
    }
    
    
    private void dialogo(String titulo, String mensaje) {
        VBox vb = new VBox();
        Label etqTitulo = new Label(titulo);
        etqTitulo.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 24");
        VBox.setMargin(etqTitulo, new Insets(8,8,8,8));
        vb.getChildren().add(etqTitulo);
        
        Label etqMensaje = new Label(mensaje);
        etqMensaje.setStyle("-fx-font-family: Arial; -fx-font-size: 14");
        VBox.setMargin(etqMensaje, new Insets(24,8,8,16));
        vb.getChildren().add(etqMensaje);
        
        JFXButton boton = new JFXButton("Cerrar");
        boton.setStyle("-fx-background-color:-fx-secondary; -fx-text-fill: white;");
        vb.getChildren().add(boton);
        
        JFXDialogLayout dl = new JFXDialogLayout();
        JFXDialog d = new JFXDialog(panel, dl, JFXDialog.DialogTransition.CENTER);
        boton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me)->{
            d.close();
        });
        dl.setBody(vb);
        dl.setActions(boton);
        d.show();
    }
    
}
