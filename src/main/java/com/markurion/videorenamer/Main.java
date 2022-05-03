package com.markurion.videorenamer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

//        Group root = new Group();
//        Scene scene = new Scene(root, Color.BLACK);
//        Image icon = new Image("file:resources/paint.png");
//
//        stage.getIcons().add(icon);
//        stage.setTitle("Video Helper 2022 GUI Edition This time JAVA");
//        stage.setScene(scene);
//        stage.setResizable(true);
//
//        Text text = new Text();
//        text.setText("Video starting folder: ");
//        text.setX(10);
//        text.setY(100);
//        text.setFont(Font.font("Arial",50));
//        text.setFill(Color.LIMEGREEN);
//        root.getChildren().add(text);
//
//        Button button = new Button("Select Directory");
//        button.setOnAction(e -> {
//            System.out.println("but");
//        });
//
//        VBox vbox = new VBox(button);
//        root.getChildren().add(vbox);
//        stage.show();

//        Image icon = new Image("file:resources/paint.png");
        Image icon = new Image(String.valueOf(getClass().getResource("icon.png")));
        stage.getIcons().add(icon);

        stage.setTitle("Video Renamer 2022");
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("main.fxml")));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
