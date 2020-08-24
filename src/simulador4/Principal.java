
package simulador4;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Principal extends Application {
    //static List<Proceso> listaProcesos;
    Pane panel;
    Scene escenaProcesos, vacio;
    
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Configuracion.fxml"));
        panel = (Pane) loader.load();
        
        escenaProcesos = new Scene(panel);
        stage.setScene(escenaProcesos);
        stage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
        });
        stage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
    
    
}
