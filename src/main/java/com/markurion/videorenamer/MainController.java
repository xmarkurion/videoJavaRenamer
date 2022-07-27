package com.markurion.videorenamer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class MainController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private File inFolder;
    private int videosAmount;
    private FileMaster videoFiles;

    private File outFolder;
    private FileMaster fileMaster;

    private MarkurionHelper helper;

    @FXML private TextField textVideoPath;
    @FXML private TextField infoField;

    public MainController(){
        System.out.println("Main Controller Initialize Part \n");
    }

    @FXML public void initialize(){
        helper = new MarkurionHelper();
        infoField.setText("Please select Input folder with scope videos.");
        infoField.setStyle("-fx-text-fill: white; -fx-background-color: black; -fx-font-weight: bold; -fx-border-color: black; -fx-font-size: 13;");

        //For testing
        //inFolder = new File("C:\\Users\\Marcepan\\Downloads\\video");
        //testVideoPath();

        // TODO: Check if config file exist inside a folder and if go to scene 2  ( VideoList.csv )
    }

    public void btnGetPathClick() throws IOException {
        inFolder = helper.pickFolderDBB(textVideoPath,stage);
        System.out.println("news = " + inFolder);

        if(testIsConfigFileInTheFolder()){
            infoField.setText("Found configuration file.");
            videoFiles = new FileMaster(Paths.get(inFolder + "\\sourceVideos"));
            videosAmount = videoFiles.videoFilesInFolder();
        }else {
            testVideoPath();
        }
    }

    /*
        Checks is configuration file already in the folder and if is stage 2 is loaded.
     */
    public boolean testIsConfigFileInTheFolder(){
        File configFile = new File(inFolder + "\\VideoList.csv");
        return configFile.exists();
    }

    public void testVideoPath(){
        videoFiles = new FileMaster(Paths.get(inFolder.getPath()));
        videosAmount = videoFiles.videoFilesInFolder();

        System.out.println("Amount of video files in folder: " + videosAmount);

        if(videosAmount == 0){
            textVideoPath.setStyle("-fx-control-inner-background: red");
            infoField.setText("The input folder does not contains anny video's");
        }else{
            infoField.setText("Found " + videosAmount + " video files in source folder.");
        }
    }

    public void btnExistingClick(ActionEvent e) throws IOException {
        System.out.println("NextClick");
        switchToScene(e);
    }

    public void switchToScene(ActionEvent e) throws IOException{
        if(videosAmount > 0){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("second.fxml"));
            root = loader.load();

            SecondController secondController = loader.getController();
            secondController.setInFolder(this.inFolder);
            secondController.setVideosAmount(this.videosAmount);
            secondController.setFileInFolder(videoFiles);

            stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                //Kill entire app even if ffmpeg is in use.
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });
            stage.show();
        }else{
            infoField.setText("Please Pick folder with videos.");
        }
    }

    public File pickFolderDb(TextField field){
        File datt;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if(field.getText().length() == 0 ){
            datt = directoryChooser.showDialog(stage);
            field.setText(datt.getPath());
        }else {
            System.out.println("Is not nulll");
            directoryChooser.setInitialDirectory(new File(field.getText()));
            datt = directoryChooser.showDialog(stage);

            //In case user cancel the file select
            if(datt!=null) {field.setText(datt.getPath());
            }else{datt = new File(field.getText());}
        }
        field.setStyle("-fx-control-inner-background: green");
        return datt;
    }

}

