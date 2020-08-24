
package simulador4;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTabPane;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class ConfigControlador implements Initializable {
    
    final Color[] coloresProcesos = new Color[]
    {
        Color.web("#db1b0d"),
        Color.web("#960c02"),
        
        Color.web("#0f22f7"),
        Color.web("#0310a1"),
        
        Color.web("#1fde29"),
        Color.web("#06750c"),
        
        Color.web("#63bf68"),
        Color.web("#326135"),
        
        Color.web("#711dcc"),
        Color.web("#47048f"),
        
        Color.web("#d9761a"),
        Color.web("#a35003"),
        
        Color.web("#e31b9d"),
        Color.web("#9c0366"),
        
        Color.web("#41e0a3"),
        Color.web("#144734"),
        
        Color.web("#98d930"),
        Color.web("#3e5914"),
        
        Color.web("#2993c4"),
        Color.web("#123f54"),
        
        Color.web("#2de35b"),
        Color.web("#145e27"),
        
        Color.web("#c3d12a"),
        Color.web("#565c12"),
        
        Color.web("#2521ed"),
        Color.web("#0f0e5c"),
        
        Color.web("#db2784"),
        Color.web("#570d32"),
        
        Color.web("#dbd388"),
        Color.web("#595637"),
        
        Color.web("#8fe082"),
        Color.web("#355430"),
        
        Color.web("#c3e864"),
        Color.web("#5b6e2c"),
        
        Color.web("#d97250"),
        Color.web("#96472d"),
        
        Color.web("#32a852"),
        Color.web("#144521"),
        
        Color.web("#e3a156"),
        Color.web("#6b4c28"),
        
        Color.web("#0f22f7"),
        Color.web("#0310a1"),
    };
    int indColor = 0;
    
    @FXML StackPane panel;
    @FXML JFXTabPane tabPanel;
    @FXML Tab tabProcesos;
    @FXML Tab tabAnalisis;
    
    @FXML MenuItem menuAbrir;
    @FXML MenuItem menuVerProcesos;
    @FXML MenuItem menuVerAnalisis;
    @FXML MenuItem menuVerConsola;
    @FXML MenuItem menuComparar;
    
    @FXML JFXButton botAgregarProceso;
    @FXML JFXButton botCambiarProceso;
    @FXML JFXButton botBorrarProceso;
    @FXML TableView tablaProcesos;
    
    @FXML JFXButton botConfigMemoria;
    
    @FXML TextFlow consola;
    @FXML JFXButton botConsolaAnterior;
    @FXML JFXButton botConsolaSiguiente;
    @FXML JFXButton botConsolaAhora;
    
    @FXML JFXButton botIniciar;
    @FXML JFXButton botDetener;
    @FXML JFXButton botTerminar;
    
    @FXML Canvas canvasMemoria;
    @FXML Canvas canvasDiagrama;
    GraphicsContext gcM, gcD;
    //ObservableList log;
    ObservableList<String> resumenes;
    int indiceResumenActual = 0;
    
    ObservableList<Proceso> procesos;
    int indColorProceso = 0;
    Memoria memoria;
    Planificador plan;
    List<double[]> bloques; // particiones fijas
    boolean enEjecucion;
    Tooltip tooltipMemoria, tooltipDiagrama;
    int seleccionActualMemoria = -1, seleccionActualDiagrama = -1;
    double PORCENTAJE_TOTAL = 0.9;
    double PORCENTAJE_PARCIAL = (PORCENTAJE_TOTAL) / 2;
    int UNIDAD_X;
    int ALTO_RN, ORIGEN_Y_VALORES;
    int X, Y;
    Punto ORIGEN_PR;
    List<PosicionDiagrama> posiciones, posicionesBloqueados;
    List<Object[]> tramosEjecucion, tramosBloqueados;
    List<int[]> tiempos;
    String pActual = "-1";
    int marcarTiempo = -1;
    String nombreFuente = "Times New Roman";
    int sizeFuente = 14;
    
    
    // dialogo inciar simulacion:
    VBox vb;
    Label etqAlgorProc;
    ListView lvAlgoritmos;
    Label etqAlgorMem;
    ListView lvAlgorMemoria;
    Label etqQt;
    JFXSlider sliderQt;
    JFXButton botDialogoSimulacion;
    
    // consola externa
    Stage ventanaConsola;
    ConsolaControlador controlConsola;
    
    private class PosicionDiagrama
    {
        double MIN,MAX;
        String nombre;
        int tiempoInf = 0, tiempoMax = 0;
        
        PosicionDiagrama()
        {
            MIN = -1;
            MAX = -1;
            nombre = "-1";
        }
        
        PosicionDiagrama(double d1, double d2, String s)
        {
            MIN = d1;
            MAX = d2;
            nombre = s;
        }
        
        @Override
        public String toString()
        {
            String linea = "[" + MIN + ", " + MAX + ", " + nombre + "]";
            return linea;
        }
    }
    
    private class Punto
    {
        int x, y;
        
        Punto()
        {
            x = 0;
            y = 0;
        }
        
        Punto(int _x, int _y)
        {
            x = _x;
            y = _y;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menuVerAnalisis.setDisable(true);
        
        tabPanel.getTabs().remove(tabAnalisis);
        
        procesos = FXCollections.observableArrayList();
        
        TableColumn<Proceso, Text> col1 = new TableColumn<>("ID Proceso");
        col1.setMinWidth(100);
        col1.setCellValueFactory(new PropertyValueFactory<>("nombre2"));
        
        TableColumn<Proceso, Integer> col2 = new TableColumn<>("Memoria");
        col2.setMinWidth(100);
        col2.setCellValueFactory(new PropertyValueFactory<>("memoria"));
        
        TableColumn<Proceso, Integer> col3 = new TableColumn<>("Arribo");
        col3.setMinWidth(100);
        col3.setCellValueFactory(new PropertyValueFactory<>("arribo"));
        
        TableColumn<Proceso, Integer> col4 = new TableColumn<>("Prioridad");
        col4.setMinWidth(100);
        col4.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        
        TableColumn<Proceso, String> col5 = new TableColumn<>("Rafagas");
        col5.setMinWidth(100);
        col5.setCellValueFactory(new PropertyValueFactory<>("rafagas"));
        
        tablaProcesos.getColumns().clear();
        tablaProcesos.getColumns().add(col1);
        tablaProcesos.getColumns().add(col2);
        tablaProcesos.getColumns().add(col3);
        tablaProcesos.getColumns().add(col4);
        tablaProcesos.getColumns().add(col5);
        
        tablaProcesos.setItems(procesos);
        
        resumenes = FXCollections.observableArrayList();
        //log = consola.getChildren();
        consola.setLineSpacing(4.0);
        
        gcM = canvasMemoria.getGraphicsContext2D();
        EventHandler<MouseEvent> handlerMouseMoved = (MouseEvent event) -> {
            movimientoCanvas(event);
        };
        EventHandler<MouseEvent> handlerMouseExited = (MouseEvent event) -> {
            mouseFueraCanvas(event);
        };
        canvasMemoria.setOnMouseMoved(handlerMouseMoved);
        canvasMemoria.setOnMouseExited(handlerMouseExited);
        tooltipMemoria = new Tooltip();
        
        double px = 0.5 * canvasMemoria.getWidth();
        double py = 300;
        tooltipMemoria.setX(px);
        tooltipMemoria.setY(py);
        gcD = canvasDiagrama.getGraphicsContext2D();
        enEjecucion = false;
        
        canvasDiagrama.setWidth(canvasMemoria.getWidth());
        EventHandler<MouseEvent> handlerMouseMoved2 = (MouseEvent event) -> {
            mouseMoveCanvasDiagrama(event);
        };
        EventHandler<MouseEvent> handlerMouseExited2 = (MouseEvent event) -> {
            mouseFueraCanvasDiagrama(event);
        };
        canvasDiagrama.setOnMouseMoved(handlerMouseMoved2);
        canvasDiagrama.setOnMouseExited(handlerMouseExited2);
        tooltipDiagrama = new Tooltip();
        px = 0.5 * canvasDiagrama.getWidth();
        py = 100;
        tooltipDiagrama.setX(px);
        tooltipDiagrama.setY(py);
        
        posiciones = new ArrayList<>();
        posicionesBloqueados = new ArrayList<>();
        tramosEjecucion = new ArrayList<>();
        tramosBloqueados = new ArrayList<>();
        tiempos = new ArrayList<>();
        
        inicializarCosas();
    }
    
    void inicializarCosas()
    {
        // dialogo de iniciar simulación (elegir algoritmo, etc.)
        vb = new VBox();
        vb.setAlignment(Pos.CENTER);
        Label etqTitulo = new Label("Simulaci\u00f3n");
        etqTitulo.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 24");
        VBox.setMargin(etqTitulo, new Insets(8,8,8,8));
        vb.getChildren().add(etqTitulo);
        
        etqAlgorProc = new Label("Algoritmo de planificaci\u00f3n:");
        etqAlgorProc.setStyle("-fx-font-family: Arial; -fx-font-size: 14");
        VBox.setMargin(etqAlgorProc, new Insets(24,8,8,16));
        vb.getChildren().add(etqAlgorProc);
        
        lvAlgoritmos = new ListView();
        lvAlgoritmos.getItems().add("FCFS");
        lvAlgoritmos.getItems().add("SJF");
        lvAlgoritmos.getItems().add("Prioridad");
        lvAlgoritmos.getItems().add("SRTF");
        lvAlgoritmos.getItems().add("Round Robin");
        lvAlgoritmos.getItems().add("Colas multinivel");
        VBox.setMargin(lvAlgoritmos, new Insets(24,8,8,16));
        vb.getChildren().add(lvAlgoritmos);
        
        etqAlgorMem = new Label("Asignaci\u00f3n de memoria:");
        etqAlgorMem.setStyle("-fx-font-family: Arial; -fx-font-size: 14");
        VBox.setMargin(etqAlgorMem, new Insets(24,8,8,16));
        vb.getChildren().add(etqAlgorMem);
        
        lvAlgorMemoria = new ListView();
        lvAlgorMemoria.getItems().add("FIRST-FIT");
        lvAlgorMemoria.getItems().add("BEST-FIT");
        lvAlgorMemoria.getItems().add("WORST-FIT");
        VBox.setMargin(lvAlgorMemoria, new Insets(24,8,8,16));
        vb.getChildren().add(lvAlgorMemoria);
        
        etqQt = new Label("Quantum de Round Robin:");
        etqQt.setStyle("-fx-font-family: Arial; -fx-font-size: 14");
        VBox.setMargin(etqQt, new Insets(24,8,8,16));
        vb.getChildren().add(etqQt);
        etqQt.setVisible(true);
        
        sliderQt = new JFXSlider();
        sliderQt.setMaxWidth(50);
        sliderQt.setMin(1);
        sliderQt.setMax(10);
        sliderQt.setShowTickMarks(false);
        sliderQt.setSnapToTicks(false);
        sliderQt.setValue(2);
        vb.getChildren().add(sliderQt);
        sliderQt.setVisible(true);
        
        lvAlgoritmos.setOnMouseClicked((MouseEvent event) -> {
            if(lvAlgoritmos.getSelectionModel().getSelectedIndex() == 4)
            {
                if(!vb.getChildren().contains(etqQt)) vb.getChildren().add(etqQt);
                if(!vb.getChildren().contains(sliderQt)) vb.getChildren().add(sliderQt);
                vb.getChildren().remove(botDialogoSimulacion);
                vb.getChildren().add(botDialogoSimulacion);
                //sliderQt.setVisible(true);
            }
            else            
            {
                vb.getChildren().remove(etqQt);
                vb.getChildren().remove(sliderQt);
                vb.getChildren().remove(botDialogoSimulacion);
                vb.getChildren().add(botDialogoSimulacion);
                //etqQt.setVisible(false);
                //sliderQt.setVisible(false);
            }
        });
        
        botDialogoSimulacion = new JFXButton("Iniciar");
        VBox.setMargin(botDialogoSimulacion, new Insets(24,8,8,16));
        botDialogoSimulacion.setStyle("-fx-background-color:-fx-secondary; -fx-text-fill: white; -fx-font-size: 20px");
        vb.getChildren().add(botDialogoSimulacion);
        
        // ventana de consola
        ventanaConsola = new Stage();
        ventanaConsola.setAlwaysOnTop(true);
        ventanaConsola.setTitle("Consola");
        
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Consola.fxml"));
            Pane __panel = loader.load();
            controlConsola = (ConsolaControlador) loader.getController();
            controlConsola.iniciar(this);
            
            Scene escena = new Scene(__panel);
            ventanaConsola.setScene(escena);
        }catch(Exception ex){}
        
        ventanaConsola.hide();
    }
    
    void abrirXML()
    {
        FileChooser fc = new FileChooser();
        File archivo = fc.showOpenDialog( tabPanel.getScene().getWindow()  );
        List<String> errores = new ArrayList<>();
        if(archivo != null)
        {
            XML xml = new XML();
            try
            {
                xml.abrir(archivo.getAbsolutePath());
                
                for(String error : xml.log) errores.add(error);
                
                if(xml.memoria != null)
                {
                    if(xml.memoria instanceof MemoriaFija)
                    {
                        memoria = new MemoriaFija();
                        for(Memoria.BloqueMemoria i : xml.memoria.particiones)
                        {
                            ((MemoriaFija)memoria).agregarParticionFija(i.size);
                        }
                    }
                    else
                    {
                        memoria = new MemoriaVariable();
                        memoria.total = xml.memoria.total;
                    }
                    memoria.ponerSO(xml.memoria.so);
                }
                
                if(!xml.procesos.isEmpty())
                {
                    for(int i = 0; i < xml.procesos.size(); i++)
                    {
                        Proceso p = xml.procesos.get(i);
                        
                        if(memoria != null)
                        {
                            if(!memoria.cabe(p.getMemoria()))
                            {
                                dialogo("Error", "No hay espacio suficiente en la memoria para ejecutar el proceso " + p.getNombre() + " con " + p.getMemoria() + " KB.");
                                continue;
                            }
                        }
                        
                        p.color = coloresProcesos[indColor];
                        p.colorSecundario = coloresProcesos[indColor + 1];
                        if(indColor >= coloresProcesos.length) indColor = 0;
                        if(!agregarProceso(p))
                        {
                            errores.add("Ya existe un proceso con el nombre " + p.getNombre() + ". El proceso ser\u00e1 ignorado.");
                        }
                        else
                        {
                            //procesos.add(new Proceso(p));
                            indColor += 2;
                            if(indColor >= coloresProcesos.length) indColor = 0;
                        }
                    }

                }
                
                if(errores.size() > 0)
                {
                    String cuerpo = "";
                    for(String linea : errores)
                    {
                        cuerpo += nl();
                        cuerpo += linea + nl();
                        cuerpo += nl();
                    }
                    dialogo("Errores en " + archivo.getName(), cuerpo);
                }
            }
            catch(Exception e){}
        }
    }
    
    @FXML
    void botConfigMemoriaAccion(ActionEvent evt)
    {
        VBox _vb = new VBox();
        _vb.setAlignment(Pos.CENTER);
        Label etqTitulo = new Label("Configuraci\u00f3n de memoria");
        etqTitulo.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 24");
        VBox.setMargin(etqTitulo, new Insets(8,8,8,8));
        _vb.getChildren().add(etqTitulo);
        
        Label etTipoParticion = new Label("Tipo de particiones:");
        etTipoParticion.setStyle("-fx-font-family: Arial; -fx-font-size: 14");
        VBox.setMargin(etTipoParticion, new Insets(24,8,8,16));
        _vb.getChildren().add(etTipoParticion);
        
        Label _etqParticiones = new Label("Particiones: (separar con comas)");
        
        ListView lv = new ListView();
        lv.getItems().add("PARTICIONES FIJAS");
        lv.getItems().add("PARTICIONES VARIABLES");
        if(memoria != null)
        {
            lv.getSelectionModel().select(memoria instanceof MemoriaFija ? 0 : 1);
            if(lv.getSelectionModel().getSelectedIndex() == 0) _etqParticiones.setText("Particiones: (separar con comas)");
            else if(lv.getSelectionModel().getSelectedIndex() == 1) _etqParticiones.setText("Tama\u00f1o total:");
        }
        lv.setMaxHeight(150);
        VBox.setMargin(lv, new Insets(24,8,8,16));
        _vb.getChildren().add(lv);
        
        _etqParticiones.setStyle("-fx-font-family: Arial; -fx-font-size: 14");
        VBox.setMargin(_etqParticiones, new Insets(24,8,8,16));
        _vb.getChildren().add(_etqParticiones);
        
        TextField tf = new TextField();
        tf.setStyle("-fx-font-size: 18px");
        tf.setAlignment(Pos.CENTER);
        tf.setOnKeyTyped((KeyEvent event) -> {
            if(event.getCharacter().length() > 0)
            {
                char c = event.getCharacter().charAt(0);
                
                if(lv.getSelectionModel().getSelectedIndex() == 0)
                {
                    if( !((c >= '0' && c <= '9') || c == ',')  ) event.consume();
                }
                else
                {
                    if(c < '0' || c > '9') event.consume();
                }
            }
        });
        if(memoria != null) tf.setText(memoria.aString());
        VBox.setMargin(tf, new Insets(12,8,8,16));
        _vb.getChildren().add(tf);
        
        Label _etqPartSO = new Label("Partici\u00f3n del sistema operativo:");
        _etqPartSO.setStyle("-fx-font-family: Arial; -fx-font-size: 14");
        VBox.setMargin(_etqPartSO, new Insets(24,8,8,16));
        _vb.getChildren().add(_etqPartSO);
        
        TextField tf2 = new TextField();
        tf2.setStyle("-fx-font-size: 18px");
        tf2.setAlignment(Pos.CENTER);
        tf2.setOnKeyTyped((KeyEvent event) -> {
            if(event.getCharacter().length() > 0)
            {
                char c = event.getCharacter().charAt(0);
                if(c < '0' || c > '9') event.consume();
            }
        });
        if(memoria != null) tf2.setText(String.valueOf(memoria.getSO()));
        VBox.setMargin(tf2, new Insets(12,8,8,16));
        _vb.getChildren().add(tf2);
        
        lv.setOnMouseClicked((MouseEvent event) -> {
            if(lv.getSelectionModel().getSelectedIndex() == 0)
            {
                _etqParticiones.setText("Particiones: (separar con comas)");
            }
            else if(lv.getSelectionModel().getSelectedIndex() == 1)
            {
                _etqParticiones.setText("Tama\u00f1o total:");
                if(tf.getText().length() > 0 && tf.getText().contains(","))
                {
                    tf.setText(tf.getText().substring(0, tf.getText().indexOf(",")));
                }
            }
        });
        
        JFXButton boton = new JFXButton("Guardar");
        VBox.setMargin(boton, new Insets(24,8,8,16));
        boton.setStyle("-fx-background-color:-fx-secondary; -fx-text-fill: white; -fx-font-size: 20px");
        _vb.getChildren().add(boton);
        
        JFXDialogLayout dl = new JFXDialogLayout();
        JFXDialog dialogo = new JFXDialog(panel, dl, JFXDialog.DialogTransition.CENTER);
        boton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me)->{
            String linea = tf.getText();
            if(linea.length() == 0) return;
            while(linea.contains(",,")) linea = linea.replaceAll(",,", ",");
            if(linea.equals(",")) return;
            if(linea.startsWith(",")) linea = linea.substring(1);
            if(linea.endsWith(",")) linea = linea.substring(0, linea.length() - 1);
            switch (lv.getSelectionModel().getSelectedIndex()) {
                case -1:
                    return;
                case 0:
                    memoria = new MemoriaFija();
                    memoria.tipo = Memoria.PART_FIJAS;
                    String[] items = linea.split(",");
                    int part;
                    for(String i : items)
                    {
                        try
                        {
                            part = Integer.parseInt(i);
                            if(part == 0)
                            {
                                dialogo("Error", "No poner particiones de tama\u00f9o cero.");
                                return;
                            }
                            ((MemoriaFija)memoria).agregarParticionFija(part);
                        }
                        catch(Exception e){}
                    }
                    break;
                default:
                    memoria = new MemoriaVariable();
                    memoria.tipo = Memoria.PART_VARIABLES;
                    try{memoria.total = Integer.parseInt(linea);}
                    catch(Exception e){memoria.total = 0;}
                    break;
            }
            try{memoria.ponerSO(Integer.parseInt(tf2.getText()));}
            catch(Exception e){memoria.ponerSO(0);}
            
            //System.out.println(memoria.resumen());
            dialogo.close();
        });
        dl.setBody(_vb);
        dialogo.show();
    }
    
    @FXML
    void menuAbrirAccion(ActionEvent evt)
    {
        abrirXML();
    }
    
    void simulacion(int tipoPlan, int algorMem, int q, boolean terminar)
    {
        if(!enEjecucion)
        {
            switch(tipoPlan)
            {
                case Planificador.FCFS:
                    plan = new PlanificadorFCFS(procesos);
                    break;

                case Planificador.SJF:
                    plan = new PlanificadorSJF(procesos);
                    break;

                case Planificador.PRIORIDAD:
                    plan = new PlanificadorPrioridad(procesos);
                    break;

                case Planificador.SRTF:
                    plan = new PlanificadorSRTF(procesos);
                    break;

                case Planificador.RR:
                    plan = new PlanificadorRR(procesos, q);
                    break;
                    
                case Planificador.MULTINIVEL:
                    plan = new PlanificadorNP(procesos);
                    break;

                default:
                    dialogo("Error", "Seleccionar un algoritmo de planificaci\u00f3n de procesos.");
                    return;
            }
            
            if(algorMem == -1)
            {
                dialogo("Error", "Seleccionar un m\u00e9todo de asignaci\u00f3n de memoria.");
                return;
            }
            
            bloques = new ArrayList<>();
            plan.memoria = memoria.crear(memoria);
            plan.algorMemoria = algorMem;
            
            //if(plan.memoria instanceof MemoriaFija)
            if(true)
            {
                final int cantidad = plan.memoria.particiones.size();
                double posX = 0;
                double[] d;
                for(int i = 0; i < cantidad; i++)
                {
                    d = new double[2];
                    int particion = plan.memoria.particiones.get(i).size;
                    double ancho = ((double) particion) / plan.memoria.getTotal() * canvasMemoria.getWidth();
                    d[0] = posX;
                    d[1] = ancho;
                    bloques.add(d);
                    posX += ancho;
                }
            }
            
            if(!tabPanel.getTabs().contains(tabAnalisis))
                tabPanel.getTabs().add(tabAnalisis);
            tabPanel.getSelectionModel().select(tabAnalisis);
            
            menuVerAnalisis.setDisable(false);
            
            actualizarBloques();
            dibujarMemoria(-1);
            enEjecucion = true;
            escribir(plan.resumen(), true);
            
            UNIDAD_X = (int) (canvasDiagrama.getWidth() / plan.tiempoTrabajo());
            plan.reset();
            ORIGEN_PR = new Punto(0 * UNIDAD_X, 0);
            X = ORIGEN_PR.x;
            ALTO_RN = (int) (canvasDiagrama.getHeight() * PORCENTAJE_TOTAL);
            Y = ORIGEN_PR.y;
            ORIGEN_Y_VALORES = ALTO_RN + 12;
            
            marcarTiempo = -1;
            dibujarDiagrama();
            botIniciar.setText("Siguiente");
        }
        else
        {
            borrar();
            if(!terminar)
            {
                plan.siguiente();
                actualizarDatosDeDiagramacion();
            }
            else
            {
                int aux = 0;
                while(!plan.fin() && aux < 5000)
                {
                    plan.siguiente();
                    escribir(plan.resumen(), false);
                    actualizarDatosDeDiagramacion();
                    aux ++;
                }
            }
            //if(plan.fin()) enEjecucion = false;
            escribir(plan.resumen(), true);
        }
    }
    
    void actualizarDatosDeDiagramacion()
    {
        actualizarBloques();

        //if(plan.actual() == null)
        //{
            //pActual = "-1";
            //if(plan.reloj() - marcarTiempo <= 1) marcarTiempo = -1;
            //else marcarTiempo = plan.reloj();
        //}
        //else
        //{
        //pActual = plan.actual().getNombre();
            if(!pActual.equalsIgnoreCase(plan.actualNombre()))
            {
                pActual = plan.actualNombre();
                marcarTiempo = plan.reloj();
            }
            else 
            {
                marcarTiempo = -1;
                //pActual =
            }
        //}
        if(plan.fin()) marcarTiempo = plan.reloj();
        
        dibujarMemoria(-1);
        dibujarDiagrama();
    }
    
    void mostrarDialogoSimulacion()
    {
        lvAlgoritmos.getSelectionModel().clearSelection();
        lvAlgorMemoria.getSelectionModel().clearSelection();
        if(vb.getChildren().contains(etqQt)) vb.getChildren().remove(etqQt);
        if(vb.getChildren().contains(sliderQt)) vb.getChildren().remove(sliderQt);
        sliderQt.setValue(2);
        
        JFXDialogLayout dl = new JFXDialogLayout();
        JFXDialog dialogo = new JFXDialog(panel, dl, JFXDialog.DialogTransition.CENTER);
        
        botDialogoSimulacion.setOnAction((ActionEvent event) -> {
            if(lvAlgoritmos.getSelectionModel().getSelectedIndex() != -1 && lvAlgorMemoria.getSelectionModel().getSelectedIndex() != -1)
            {
                int i = lvAlgoritmos.getSelectionModel().getSelectedIndex();
                int j = lvAlgorMemoria.getSelectionModel().getSelectedIndex();
                int k = (int) sliderQt.getValue();
                simulacion(i,j,k, false);
                /*
                switch(lvAlgoritmos.getSelectionModel().getSelectedIndex())
                {
                case Planificador.FCFS:
                plan = new PlanificadorFCFS(procesos);
                break;
                
                case Planificador.SJF:
                plan = new PlanificadorSJF(procesos);
                break;
                
                case Planificador.PRIORIDAD:
                plan = new PlanificadorPrioridad(procesos);
                break;
                
                case Planificador.SRTF:
                plan = new PlanificadorSRTF(procesos);
                break;
                
                case Planificador.RR:
                plan = new PlanificadorRR(procesos, (int) sliderQt.getValue());
                break;
                
                case Planificador.MULTINIVEL:
                plan = new PlanificadorNP(procesos);
                break;
                
                default:
                dialogo("Error", "Seleccionar un algoritmo de planificaci\u00f3n de procesos.");
                return;
                }
                
                if(plan != null)
                {
                plan.memoria = memoria.crear(memoria);
                if((lvAlgorMemoria.getSelectionModel().getSelectedIndex() == 0)
                ||
                (lvAlgorMemoria.getSelectionModel().getSelectedIndex() == 1)
                ||
                (lvAlgorMemoria.getSelectionModel().getSelectedIndex() == 2))
                {
                plan.algorMemoria = lvAlgorMemoria.getSelectionModel().getSelectedIndex();
                }
                else
                {
                dialogo("Error", "Seleccionar un algoritmo de asignaci\u00f3n de memoria.");
                return;
                }
                }
                */
                //simulacion();
                dialogo.close();
            }
        });
        dl.setBody(vb);
        dialogo.show();
    }
    
    @FXML
    void botIniciarAccion(ActionEvent evt)
    {
        if(enEjecucion)
        {
            simulacion(-1, -1, -1, false);
            return;
        }
        
        
        if(procesos.isEmpty())
        {
            dialogo("Error", "No hay procesos cargados.");
            return;
        }

        if(memoria == null)
        {
            dialogo("Error", "La memoria no fue asignada.");
            return;
        }
        
        mostrarDialogoSimulacion();
    }
    
    @FXML
    void botTerminarActionEvent(ActionEvent evt)
    {
        if(enEjecucion)
        {
            if(plan != null)
            {
                simulacion(-1, -1, -1, true);
            }
        }
    }
    
    @FXML
    void botDetenerActionEvent(ActionEvent evt)
    {
        if(enEjecucion)
        {
            enEjecucion = false;
            botIniciar.setText("Iniciar");
            tiempos.clear();
            tramosEjecucion.clear();
            tramosBloqueados.clear();
            seleccionActualMemoria = -1;
            seleccionActualDiagrama = -1;
            posiciones.clear();
            posicionesBloqueados.clear();

            plan = null;
            memoria.vaciar();
            dibujarMemoria(-1);
            dibujarDiagrama();
            
            if(!tabPanel.getTabs().contains(tabProcesos)) tabPanel.getTabs().add(tabProcesos);
            tabPanel.getSelectionModel().select(tabProcesos);
            tabPanel.getTabs().remove(tabAnalisis);
            menuVerAnalisis.setDisable(false);
        }
    }
    
    void comparacion()
    {
        String resumen = "Las comparaciones se hacen con First-Fit." + nl();
        Planificador plantmp = new PlanificadorFCFS(procesos);
        plantmp.memoria = memoria;
        plantmp.algorMemoria = 0;
        int tiempo = plantmp.tiempoTrabajo();
        resumen += "FCFS: Tiempo: " + tiempo + " TRP = " + plantmp.TRP + " TEP = " + plantmp.TEP + nl();
        
        resumen += nl();
        
        plantmp = new PlanificadorSJF(procesos);
        plantmp.memoria = memoria;
        plantmp.algorMemoria = 0;
        tiempo = plantmp.tiempoTrabajo();
        resumen += "SJF: Tiempo: " + tiempo + " TRP = " + plantmp.TRP + " TEP = " + plantmp.TEP + nl();
        
        resumen += nl();
        
        plantmp = new PlanificadorPrioridad(procesos);
        plantmp.memoria = memoria;
        plantmp.algorMemoria = 0;
        tiempo = plantmp.tiempoTrabajo();
        resumen += "Prioridad: Tiempo: " + tiempo + " TRP = " + plantmp.TRP + " TEP = " + plantmp.TEP + nl();
        
        resumen += nl();
        
        plantmp = new PlanificadorRR(procesos, 2);
        plantmp.memoria = memoria;
        plantmp.algorMemoria = 0;
        tiempo = plantmp.tiempoTrabajo();
        resumen += "Round Robin: Tiempo: " + tiempo + " TRP = " + plantmp.TRP + " TEP = " + plantmp.TEP + nl();
        
        resumen += nl();
        
        plantmp = new PlanificadorSRTF(procesos);
        plantmp.memoria = memoria;
        plantmp.algorMemoria = 0;
        tiempo = plantmp.tiempoTrabajo();
        resumen += "SRTF: Tiempo: " + tiempo + " TRP = " + plantmp.TRP + " TEP = " + plantmp.TEP + nl();
        
        resumen += nl();
        
        plantmp = new PlanificadorNP(procesos);
        plantmp.memoria = memoria;
        plantmp.algorMemoria = 0;
        tiempo = plantmp.tiempoTrabajo();
        resumen += "Colas multinivel: Tiempo: " + tiempo + " TRP = " + plantmp.TRP + " TEP = " + plantmp.TEP;
        
        dialogo("Comparaci\u00f3n de algoritmos", resumen);
        
    }
    
    void dialogo1(String titulo, String mensaje, String cerrar)
    {
        VBox _vb = new VBox();
        Label etqTitulo = new Label(titulo);
        etqTitulo.setStyle("-fx-font-weight: bold; -fx-font-family: Arial; -fx-font-size: 24");
        VBox.setMargin(etqTitulo, new Insets(8,8,8,8));
        _vb.getChildren().add(etqTitulo);
        
        Label etqMensaje = new Label(mensaje);
        etqMensaje.setStyle("-fx-font-family: Arial; -fx-font-size: 14");
        VBox.setMargin(etqMensaje, new Insets(24,8,8,16));
        _vb.getChildren().add(etqMensaje);
        
        JFXButton boton = new JFXButton(cerrar);
        boton.setStyle("-fx-background-color:-fx-secondary; -fx-text-fill: white;");
        _vb.getChildren().add(boton);
        
        JFXDialogLayout dl = new JFXDialogLayout();
        JFXDialog dialogo = new JFXDialog(panel, dl, JFXDialog.DialogTransition.CENTER);
        boton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent me)->{
            dialogo.close();
        });
        dl.setBody(_vb);
        dl.setActions(boton);
        dialogo.show();
    }
    
    void dialogoAgregarProceso(int indice, Proceso p)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AgregarProceso.fxml"));
            Pane _panel = (Pane) loader.load();

            JFXDialogLayout dl = new JFXDialogLayout();
            dl.setBody(_panel);
            
            JFXDialog dialogo = new JFXDialog(panel, dl, JFXDialog.DialogTransition.CENTER);
            ((AgProcControlador) loader.getController()).inicializar(this, dialogo, indice, p);
            dialogo.show();
        }catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    void dialogo(String titulo, String texto)
    {
        dialogo1(titulo, texto, "Cerrar");
    }
    
    void borrar()
    {
        //log.clear();
        consola.getChildren().clear();
        
    }
    
    void escribir(String linea, boolean mostrar)
    {
        //if(linea.equals(resumenes.get(0))) return;
        resumenes.add(linea);
        indiceResumenActual = resumenes.size() - 1;
        if(controlConsola != null) controlConsola.agregarEntrada(linea);
        if(mostrar)
        {
            Text li = new Text(linea + nl());
            li.setFont(new Font(15));
            consola.getChildren().add(li);
        }
    }
    
    String nl()
    {
        return System.getProperty("line.separator");
    }
    
    @FXML
    public void menuVerAccion(ActionEvent evt)
    {
        if(evt.getSource() == menuVerProcesos)
        {
            if(!tabPanel.getTabs().contains(tabProcesos))
            {
                tabPanel.getTabs().add(tabProcesos);
            }
        }
        if(evt.getSource() == menuVerAnalisis)
        {
            if(enEjecucion)
            {
                if(!tabPanel.getTabs().contains(tabAnalisis))
                {
                    tabPanel.getTabs().add(tabAnalisis);
                }
            }
        }
        if(evt.getSource() == menuComparar)
        {
            if(procesos.isEmpty())
            {
                dialogo("Error", "No hay procesos.");
                return;
            }
            if(memoria == null)
            {
                dialogo("Error", "No se configur\u00f3 la memoria.");
                return;
            }
            
            comparacion();
        }
        if(evt.getSource() == menuVerConsola)
        {
            if(!ventanaConsola.isShowing()) ventanaConsola.show();
        }
    }
    
    void dibujarMemoria(int pos)
    {
        final int borde = 4;
        final int radio = 24;
        final double porcentaje = 0.75;
        final double altoBloque = canvasMemoria.getHeight() * porcentaje;
        final Color vacio = Color.DARKSEAGREEN;
        final Color fondo = Color.web("#ebedf0");
        final Color marca = Color.DARKCYAN;
        
        gcM.setFill(fondo);
        gcM.fillRoundRect(0, 0, canvasMemoria.getWidth(), canvasMemoria.getHeight(), radio, radio);
        
        if(plan == null) return;
        
        if(plan.memoria instanceof MemoriaFija )
        {
            if(bloques == null) return;
            
            if(seleccionActualMemoria != -1)
            {
                gcM.setFill(marca);
                gcM.fillRoundRect(bloques.get(seleccionActualMemoria)[0], 0, bloques.get(seleccionActualMemoria)[1], altoBloque, radio, radio);
            }
            
            int direcciones = 0, XDIR = 0;
            for(int i = 0; i < plan.memoria.particiones.size(); i++)
            {
                gcM.setFill(vacio);
                gcM.fillRoundRect(bloques.get(i)[0]+borde, 0+borde, bloques.get(i)[1]-(2*borde), altoBloque-(2*borde), radio, radio);
                
                String s = plan.memoria.particiones.get(i).nombre;
                if(!s.equals("-1"))
                {
                    Proceso p = getProcesoByNombre(s);
                    Color color = p.color;
                    int mem = p.getMemoria();
                    double ancho = (double)mem / plan.memoria.getTotal() * canvasMemoria.getWidth();
                    
                    gcM.setFill(color);
                    gcM.fillRoundRect(bloques.get(i)[0]+borde, 0+borde, ancho-(2*borde), altoBloque-(2*borde), radio, radio);
                }
                
                // direcciones de memoria
                gcM.setFill(Color.BLACK);
                gcM.setFont(new Font(nombreFuente, sizeFuente));
                gcM.fillText(String.valueOf(direcciones) + " KB", XDIR + borde, canvasMemoria.getHeight() * 0.9);
                int particion = plan.memoria.particiones.get(i).size;
                double ancho = ((double) particion) / plan.memoria.getTotal() * canvasMemoria.getWidth();
                XDIR += ancho;
                direcciones += particion;
            }
        }
        else
        {
            if(bloques == null) return;
            
            // seleccion
            if(seleccionActualMemoria != -1)
            {
                gcM.setFill(marca);
                gcM.fillRoundRect(bloques.get(seleccionActualMemoria)[0], 0, bloques.get(seleccionActualMemoria)[1], altoBloque, radio, radio);
            }
            
            int posX = 0;
            int direcciones = 0, XDIR = 0;
            for(int i = 0; i < plan.memoria.particiones.size(); i++)
            {
                Memoria.BloqueMemoria bm = plan.memoria.particiones.get(i);
                double ancho = ((double)bm.size) / plan.memoria.getTotal();
                ancho *= canvasMemoria.getWidth();
                Color color;
                if(!bm.nombre.equals("-1"))
                {
                    color = getProcesoByNombre(bm.nombre).color;
                }
                else
                {
                    color = Color.GRAY;
                }
                
                // pintar bloque
                gcM.setFill(color);
                gcM.fillRoundRect(posX+borde, 0+borde, ancho-(2*borde), altoBloque-(2*borde), radio, radio);
                posX += ancho;
                
                // direcciones de memoria
                gcM.setFill(Color.BLACK);
                gcM.setFont(new Font(nombreFuente, sizeFuente));
                gcM.fillText(String.valueOf(direcciones) + " KB", XDIR + borde, canvasMemoria.getHeight() * 0.9);
                int particion = plan.memoria.particiones.get(i).size;
                ancho = ((double) particion) / plan.memoria.getTotal() * canvasMemoria.getWidth();
                XDIR += ancho;
                direcciones += particion;
            }
            
            double ancho = canvasMemoria.getWidth() - posX;
            gcM.setFill(Color.GRAY);
            gcM.fillRoundRect(posX+borde, 0+borde, ancho-(2*borde), altoBloque-(2*borde), radio, radio);
        }
    }
    
    
    void dibujarDiagrama()
    {
        final Color vacio = Color.DARKSEAGREEN;
        final Color fondo = Color.web("#ebedf0");
        
        // pintar fondo
        gcD.setFill(fondo);
        gcD.fillRect(0, 0, canvasDiagrama.getWidth(), ALTO_RN);
        
        if(plan == null)
        {
            gcD.fillRect(0, 0, canvasDiagrama.getWidth(), canvasDiagrama.getHeight());
            return;
        }
        //
        
        if(marcarTiempo != -1 && !plan.fin())
        {
            int[] t = new int[2];
            t[0] = X;
            t[1] = marcarTiempo;
            tiempos.add(t);
        }
        
        // segmento numérico 
        gcD.clearRect(0, ALTO_RN, canvasDiagrama.getWidth(), canvasDiagrama.getHeight()*(1-PORCENTAJE_TOTAL));
        gcD.setFill(Color.BLACK);
        gcD.setLineWidth(1.0);
        gcD.setFont(new Font(nombreFuente, sizeFuente));
        int corrimiento = 4;
        for(int[] tiempo : tiempos)
        {
            gcD.fillText(  String.valueOf(tiempo[1])    , tiempo[0]-2 + corrimiento, ORIGEN_Y_VALORES);
        }
        if(plan.fin())
        {
            gcD.fillText(  String.valueOf(plan.reloj())    , X-2 + corrimiento, ORIGEN_Y_VALORES);
        }
        
        // para mostrar datos del proceso en ejecución
        if(!plan.fin() && plan.reloj() != -1)
        {
            // agregar proceso actual
            Object[] item = new Object[5];
            item[0] = X;
            String nombreEjActual;
            if(plan.actual() != null)
            {
                nombreEjActual = plan.actual().getNombre();
                item[1] = plan.actual().color;
            }
            else
            {
                nombreEjActual = "CPU sin ejecuci\u00f3n de tareas.";
                item[1] = vacio;
            }
            
            item[2] = nombreEjActual;
            item[3] = UNIDAD_X;
            if(plan.actual() != null) item[4] = plan.actual().colorSecundario;
            else item[4] = vacio;
            if(tramosEjecucion.size() > 0)
            {
                if(nombreEjActual.equalsIgnoreCase((String)tramosEjecucion.get(tramosEjecucion.size() - 1)[2]))
                {
                    tramosEjecucion.get(tramosEjecucion.size() - 1)[3] = (int)tramosEjecucion.get(tramosEjecucion.size() - 1)[3] + UNIDAD_X;
                }
                else
                {
                    tramosEjecucion.add(item);
                }
            }
            else
            {
                tramosEjecucion.add(item);
            }
            
            // agregar bloqueado actual
            //if(plan.bloqueados.size() > 0)
            if(true)
            {
                Object[] bloqueo = new Object[6];
                bloqueo[0] = X;
                bloqueo[3] = UNIDAD_X;
                bloqueo[5] = plan.reloj(); // lim. inf.
                
                if(plan.bloqueados.size() > 0)
                {
                    bloqueo[1] = plan.bloqueados.get(0).color;
                    bloqueo[2] = plan.bloqueados.get(0).getNombre();
                    bloqueo[4] = plan.bloqueados.get(0).colorSecundario;
                }
                else
                {
                    bloqueo[1] = Color.TRANSPARENT;
                    bloqueo[2] = "-1";
                    bloqueo[4] = Color.TRANSPARENT;
                }
                
                String nombreBloqActual = (String) bloqueo[2];
                
                boolean agregar = true;
                if(tramosBloqueados.size() > 0)
                {
                    if(nombreBloqActual.equalsIgnoreCase((String)tramosBloqueados.get(tramosBloqueados.size() - 1)[2]) )
                    {
                        int t = (int) tramosBloqueados.get(tramosBloqueados.size() - 1)[3];
                        //int t2 = t / UNIDAD_X;
                        //int t3 = (int) tramosBloqueados.get(tramosBloqueados.size() - 1)[5];
                        //t2 = plan.reloj() - t2;
                        
                        //if(t2 == t3)
                        //{
                            t += UNIDAD_X;
                            tramosBloqueados.get(tramosBloqueados.size() - 1)[3] = t;
                            agregar = false;
                        //}
                    }
                }
                if(agregar)
                {
                    tramosBloqueados.add(bloqueo);
                }
                
                agregar = true;
                if(posicionesBloqueados.size() > 0 && posicionesBloqueados.get(posicionesBloqueados.size() - 1).nombre.equalsIgnoreCase(nombreBloqActual))
                {
                    posicionesBloqueados.get(posicionesBloqueados.size() - 1).MAX += UNIDAD_X;
                    posicionesBloqueados.get(posicionesBloqueados.size() - 1).tiempoMax += 1;
                    agregar = false;
                }
                if(agregar)
                {
                    PosicionDiagrama pd = new PosicionDiagrama(X, X + UNIDAD_X, nombreBloqActual);
                    pd.tiempoInf = plan.reloj();
                    pd.tiempoMax = plan.reloj();
                    posicionesBloqueados.add(pd);
                }
            }
            
            if(posiciones.size() > 0 && posiciones.get(posiciones.size() - 1).nombre.equalsIgnoreCase(nombreEjActual))
            {
                posiciones.get(posiciones.size() - 1).MAX += UNIDAD_X;
                posiciones.get(posiciones.size() - 1).tiempoMax += 1;
            }
            else
            {
                PosicionDiagrama pd = new PosicionDiagrama(X, X + UNIDAD_X, nombreEjActual);
                pd.tiempoInf = plan.reloj();
                pd.tiempoMax = plan.reloj();
                posiciones.add(pd);
            }
            
            X += UNIDAD_X;
        }
        
        // pintar proceso en ejecución actual
        Y = ORIGEN_PR.y;
        int radio;
        int bordecito;
        for(Object[] tr : tramosEjecucion)
        {
            // pintar
            bordecito = 4;
            radio = Math.min((int) tr[3] / 4 + 4 , 16);
            gcD.setFill((Color)tr[1]);
            gcD.fillRoundRect((int) tr[0] + bordecito, Y + bordecito, (int) tr[3] - 2 * bordecito, canvasDiagrama.getHeight() * PORCENTAJE_PARCIAL - 2 * bordecito, radio, radio);
            bordecito = 6;
            gcD.setFill((Color)tr[4]);
            gcD.fillRoundRect((int) tr[0] + bordecito, Y + bordecito, (int) tr[3] - 2 * bordecito, canvasDiagrama.getHeight() * PORCENTAJE_PARCIAL - 2 * bordecito, radio, radio);
        }
        
        // pintar proceso bloqueado actual
        Y = (int) (ORIGEN_PR.y + canvasDiagrama.getHeight() * PORCENTAJE_PARCIAL);
        for(Object[] tr : tramosBloqueados)
        {
            // pintar
            bordecito = 4;
            radio = Math.min((int) tr[3] / 4 + 4 , 16);
            gcD.setFill((Color)tr[1]);
            gcD.fillRoundRect((int) tr[0] + bordecito, Y + bordecito, (int) tr[3] - 2 * bordecito, canvasDiagrama.getHeight() * PORCENTAJE_PARCIAL - 2 * bordecito, radio, radio);
            bordecito = 6;
            gcD.setFill((Color)tr[4]);
            gcD.fillRoundRect((int) tr[0] + bordecito, Y + bordecito, (int) tr[3] - 2 * bordecito, canvasDiagrama.getHeight() * PORCENTAJE_PARCIAL - 2 * bordecito, radio, radio);
        }
        
    }

    private void movimientoCanvas(MouseEvent event) {
        if(bloques == null) return;
        
        double x = event.getX();
        int target = -1;
        for(int i = 0; i < bloques.size(); i++)
        {
            if(x < bloques.get(i)[0])
            {
                target = i-1;
                break;
            }
        }
        
        if(target == -1)
        {
            if(memoria instanceof MemoriaVariable)
            {
                if(bloques.isEmpty())
                {
                    int libre = ((MemoriaVariable)memoria).libre();
                    tooltipMemoria.setText("vacio " + nl() + libre + " KB." + nl() + "Reg.Valla: " + (memoria.getTotal() - memoria.getSO()));
                    tooltipMemoria.setX(canvasMemoria.getLayoutX() + canvasMemoria.getWidth() / 2);
                    tooltipMemoria.setY(canvasMemoria.getLayoutY() + canvasMemoria.getHeight() / 2);
                    if(!tooltipMemoria.isShowing()) tooltipMemoria.show(panel.getScene().getWindow());
                    dibujarMemoria(-1);
                    return;
                }
                else if(x > bloques.get(bloques.size() - 1)[0] + bloques.get(bloques.size() - 1)[1])
                {
                    int libre = ((MemoriaVariable)memoria).libre();
                    tooltipMemoria.setText("vacio " + nl() + libre + " KB." + nl() + "Reg.Valla: " + (memoria.getTotal() - memoria.getSO()));
                    tooltipMemoria.setX(canvasMemoria.getLayoutX() + canvasMemoria.getWidth() / 2);
                    tooltipMemoria.setY(canvasMemoria.getLayoutY() + canvasMemoria.getHeight() / 2);
                    if(!tooltipMemoria.isShowing()) tooltipMemoria.show(panel.getScene().getWindow());
                    dibujarMemoria(-1);
                    return;
                }
                else
                {
                    target = bloques.size() - 1;
                }
            }
            else target = bloques.size() - 1;
        }
        
        if(target == seleccionActualMemoria) return;       ///////////////
        else seleccionActualMemoria = target;
        
        if(target == -1) return;
        if(plan == null) return;
        if(memoria == null) return;
        
        String tt;
        Memoria.BloqueMemoria bm = plan.memoria.particiones.get(target);
        Proceso p = getProcesoByNombre(bm.nombre);
        if(bm.nombre.equals("-1"))
        {
            tt = "vac\u00edo" + nl() + "(" + bm.size + " KB)" + nl();
            int regvalla = plan.memoria.registroValla(target - 0);
            tt += "Reg.Valla: " + (regvalla - 1) + " KB.";
        }
        else
        {
            int i = plan.memoria.buscarPorId(p.getNombre());
            int frag = -1;
            if(i >= 0) frag = plan.memoria.particiones.get(i).size - p.getMemoria();
            
            tt = p.getNombre() + " (" + p.getMemoria() + " KB)";
            
            if(frag > 0)
                tt += nl() + "fragmentaci\u00f3n interna: " + frag + " KB.";
        }
        
        tooltipMemoria.setText(tt);
        tooltipMemoria.setX(canvasMemoria.getLayoutX() + canvasMemoria.getWidth() / 2);
        tooltipMemoria.setY(canvasMemoria.getLayoutY() + canvasMemoria.getHeight() / 2);
        if(!tooltipMemoria.isShowing()) tooltipMemoria.show(panel.getScene().getWindow());
        dibujarMemoria(-1);
    }
    
    private void mouseFueraCanvas(MouseEvent event)
    {
        if(plan == null) return;
        seleccionActualMemoria = -1;
        dibujarMemoria(-1);
        if(tooltipMemoria.isShowing()) tooltipMemoria.hide();
        
    }
    
    private void mouseMoveCanvasDiagrama(MouseEvent event)
    {
        if(posiciones == null || posicionesBloqueados == null) return;
        
        String linea = "";
        int target = -1;
        if(event.getY() < canvasDiagrama.getHeight() * PORCENTAJE_PARCIAL)
        {
            for(int i = 0; i < posiciones.size(); i++)
            {
                PosicionDiagrama pd = posiciones.get(i);
                double x = event.getX();
                if(pd.MIN <= x && x <= pd.MAX)
                {
                    linea = pd.nombre + nl() + " desde " + pd.tiempoInf + " hasta " + (pd.tiempoMax + 1) +
                            nl() + "(" + (pd.tiempoMax - pd.tiempoInf + 1) + " ciclo(s))" + ".";
                    target = i;
                    break;
                }
            }
        }
        else
        {
            for(int i = 0; i < posicionesBloqueados.size(); i++)
            {
                PosicionDiagrama pd = posicionesBloqueados.get(i);
                double x = event.getX();
                if(pd.MIN <= x && x <= pd.MAX)
                {
                    if(pd.nombre.equals("-1")) return;
                    linea = pd.nombre + nl() + " desde " + pd.tiempoInf + " hasta " + (pd.tiempoMax + 0) + ".";
                    target = i;
                    break;
                }
            }
            
        }
        
        seleccionActualDiagrama = target;
        
        if(linea.length() == 0) 
        {
            return;
        }
        if(linea.equals("-1")) linea = "CPU sin trabajo";
        
        tooltipDiagrama.setText(linea);
        tooltipDiagrama.setX(canvasDiagrama.getLayoutX() + canvasDiagrama.getWidth() / 2);
        tooltipDiagrama.setY(canvasDiagrama.getLayoutY() + canvasDiagrama.getHeight() / 1);
        if(!tooltipDiagrama.isShowing()) tooltipDiagrama.show(panel.getScene().getWindow());
    }
    
    private void mouseFueraCanvasDiagrama(MouseEvent event)
    {
        if(tooltipDiagrama.isShowing()) tooltipDiagrama.hide();
    }
    
    void actualizarBloques()
    {
        final int cantidad = plan.memoria.particiones.size();
        bloques.clear();
        double posX = 0;
        double[] d;
        for(int i = 0; i < cantidad; i++)
        {
            d = new double[2];
            int particion = plan.memoria.particiones.get(i).size;
            double ancho = ((double) particion) / plan.memoria.getTotal() * canvasMemoria.getWidth();
            d[0] = posX;
            d[1] = ancho;
            bloques.add(d);
            posX += ancho;
        }
    }
    
    @FXML
    void botConsolaActionEvent(ActionEvent evento)
    {
        if(resumenes.isEmpty()) return;
        if(evento.getSource() == botConsolaAnterior)
        {
            indiceResumenActual--;
            if(indiceResumenActual < 0) indiceResumenActual = 0;
            Text texto = new Text(resumenes.get(indiceResumenActual));
            texto.setFont(new Font(15));
            consola.getChildren().clear();
            consola.getChildren().add(texto);
        }
        
        if(evento.getSource() == botConsolaSiguiente)
        {
            indiceResumenActual++;
            if(indiceResumenActual >= resumenes.size()) indiceResumenActual--;
            Text texto = new Text(resumenes.get(indiceResumenActual));
            texto.setFont(new Font(15));
            consola.getChildren().clear();
            consola.getChildren().add(texto);
        }
        
        if(evento.getSource() == botConsolaAhora)
        {
            if(plan == null) return;
            if(plan.reloj() < 0) return;
            //indiceResumenActual = plan.reloj();
            indiceResumenActual = resumenes.size() - 1;
            //if(indiceResumenActual >= resumenes.size()) indiceResumenActual--;
            Text texto = new Text(resumenes.get(indiceResumenActual));
            texto.setFont(new Font(15));
            consola.getChildren().clear();
            consola.getChildren().add(texto);
        }
    }
    
    @FXML
    void botCambiarProcesoActionEvent(ActionEvent evento)
    {
        int indice = tablaProcesos.getSelectionModel().getSelectedIndex();
        if(indice == -1) return;
        Proceso p = procesos.get(indice);
        dialogoAgregarProceso(indice, p);
    }
    
    @FXML
    void botAgregarProcesoActionEvent(ActionEvent evento)
    {
        if(memoria == null)
        {
            dialogo("Error", "Se debe configurar la memoria antes de cargar procesos.");
            return;
        }
        dialogoAgregarProceso(-1, null);
    }
    
    @FXML
    void botBorrarProcesoActionEvent(ActionEvent evento)
    {
        int indice = tablaProcesos.getSelectionModel().getSelectedIndex();
        if(indice != -1)  procesos.remove(indice);
    }
    
    Color randomColor()
    {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b, 1);
    }
    
    boolean existeProceso(List<Proceso> p, Proceso q)
    {
        for(Proceso o : p)
        {
            if(o.getNombre().equalsIgnoreCase(q.getNombre()))
            {
                return true;
            }
        }
        return false;
    }
    
    boolean existeProceso(String nombre)
    {
        for(Proceso o : procesos)
        {
            if(o.getNombre().equalsIgnoreCase(nombre))
            {
                return true;
            }
        }
        return false;
    }
    
    boolean agregarProceso(Proceso q)
    {
        if(existeProceso(q.getNombre())) return false;
        else
        {
            procesos.add(q);
            return true;
        }
    }
    
    boolean cambiarProceso(int indice, Proceso q)
    {
        procesos.set(indice, q);
        return true;
    }
    
    Proceso getProcesoByNombre(String id)
    {
        for(Proceso p : plan.procesos)
        {
            if(p.getNombre().equalsIgnoreCase(id)) return p;
        }
        return null;
    }
}
