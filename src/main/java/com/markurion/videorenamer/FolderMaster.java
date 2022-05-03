package com.markurion.videorenamer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FolderMaster {
    private String fullStringFolderPath;
    private String fullStringFolderPathWithName;

    public FolderMaster(){
        fullStringFolderPathWithName = null;
    }

    public String getFullStringFolderPathWithName() {
        return fullStringFolderPathWithName;
    }

    public String appDir(){
        return System.getProperty("user.dir");
    }

    public void mkdirName(String folder_name){
        String folderName = appDir() + "\\" + folder_name;
        Path myWay = Paths.get(folder_name);
        try{
            Files.createDirectories(myWay);
            this.fullStringFolderPathWithName = folderName;
        }catch (IOException exception){
            System.err.println("Cant create a dir! " + exception.getMessage());
        }
    }

//    public void moveToFolder(String folderName){
//        if(fullStringFolderPathWithName != null){
//            try{
//                String newFolderName = fullStringFolderPathWithName + "\\" + folderName;
//                Files.createDirectories(Paths.get(newFolderName));
//            }catch (IOException exception){
//            System.err.println("Cant create a dir! " + exception.getMessage());
//            }
//        }
//    }

}
