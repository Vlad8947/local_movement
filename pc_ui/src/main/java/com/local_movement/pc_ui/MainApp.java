package com.local_movement.pc_ui;

import com.local_movement.core.AppProperties;
import com.local_movement.pc_ui.controllers.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    @Getter private static MainApp instance;
    private static Stage primaryStage;
    private MainViewController mainViewController;
    private ClassLoader classLoader = getClass().getClassLoader();

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
        Parent rootPane = loadMainView();
        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Parent loadMainView() throws IOException {
        @NonNull URL mainViewUrl = classLoader.getResource("view/MainView.fxml");
        FXMLLoader mainViewLoader = new FXMLLoader(mainViewUrl);
        Parent rootPane = mainViewLoader.load();
        mainViewController = mainViewLoader.getController();
        return rootPane;
    }

    public Node loadView(String path) throws IOException {
        @NonNull URL url = classLoader.getResource(path);
        return FXMLLoader.load(url);
    }

    public static File chooseFile() {
        return new FileChooser().showOpenDialog(primaryStage);
    }

}
