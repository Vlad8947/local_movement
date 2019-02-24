package com.local_movement.pc_ui;

import com.local_movement.core.AppProperties;
import com.local_movement.pc_ui.view.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.NonNull;
import sun.applet.Main;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private Stage primaryStage;
    private MainViewController mainViewController;
    private ClassLoader classLoader = getClass().getClassLoader();

    public MainApp() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(AppProperties.getTitle());

        initScene();
    }

    private void initScene() throws IOException {
        Parent rootPane = loadMainView();
        Scene scene = new Scene(rootPane);

        loadSendView();
        loadReceiveView();

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

    private void loadSendView() throws IOException {
        String path = "view/SendFileView.fxml";
        Node sendView = loadView(path);
        mainViewController.getSendFileTab().setContent(sendView);
    }

    private void loadReceiveView() throws IOException {
        String path = "view/ReceiveFileView.fxml";
        Node view = loadView(path);
        mainViewController.getReceiveFileTab().setContent(view);
    }

    private Node loadView(String path) throws IOException {
        @NonNull URL url = classLoader.getResource(path);
        return FXMLLoader.load(url);
    }

}
