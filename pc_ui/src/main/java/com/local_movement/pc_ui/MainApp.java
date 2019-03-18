package com.local_movement.pc_ui;

import com.local_movement.core.AppProperties;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;

public class MainApp extends Application {

    @Getter private static MainApp instance;
    @Getter private static Stage primaryStage;

    public MainApp() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        MainApp.primaryStage = primaryStage;
        MainApp.primaryStage.setTitle(AppProperties.getTitle());
        initScene();
    }

    private void initScene() throws IOException {
        Parent rootPane = ViewLoader.loadView("view/MainView.fxml");
        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
