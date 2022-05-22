package ba.unsa.etf.zavrsnirad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MyResourceBundle.setLocale (Locale.getDefault());
        StartController mainController = new StartController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/startpage.fxml"), MyResourceBundle.getResourceBundle());
        loader.setController(mainController);
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(800);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
