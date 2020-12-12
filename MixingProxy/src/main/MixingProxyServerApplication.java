package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Scanner;

public class MixingProxyServerApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        MixingProxyServer server = new MixingProxyServer();
        server.start();


        primaryStage.setTitle("MixingProxyServer");
        HBox hb = new HBox();
        Button flush = new Button();
        flush.setText("Flush");
        Button print = new Button();
        print.setText("print database");
        flush.setOnMouseClicked(event -> server.sendCapsules());
        print.setOnMouseClicked(event -> server.printDatabase());
        hb.getChildren().add(flush);
        hb.getChildren().add(print);

        primaryStage.setScene(new Scene(hb));
        primaryStage.show();
    }
}
