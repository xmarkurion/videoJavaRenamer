package com.markurion.videorenamer;

import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MarkurionHelper {
    public MarkurionHelper(){
    }

    /**
     * Opens file picker in java FX.
     * @param field
     * @param stage
     * @return  return object File class.
     * @throws IOException
     */
    public File pickFolderDBB(TextField field, Stage stage) throws IOException {
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
