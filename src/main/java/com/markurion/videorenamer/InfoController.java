package com.markurion.videorenamer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class InfoController {
    @FXML private TextArea txtArea;
    private Stage stage;

    public InfoController() {
        System.out.println("Info controller.");
    }

    @FXML public void initialize(){
        txtArea.setEditable(false);
       txtArea.setText(infoTxt());
    }

    public void setStage(Stage stage){ this.stage = stage;}

    public void btnCloseController(ActionEvent e){
        System.out.print("click");
        stage.close();
    }

    private String infoTxt(){
       return "App was created to help with bath renaming. \n" +
               "And renaming & burning video title on top of video. \n" +
               " \n" +
               "Lib's used: \n" +
               "-> JavaFX \n"+
               "-> FFmpeg \n" +
               " \n" +
               "Done by Markurion Github: xmarkurion";
    }
}
