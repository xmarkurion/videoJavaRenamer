package com.markurion.videorenamer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Stream;

public class FileMaster {
    private boolean areThereFiles;
    private int videoFilesCounter;
    private ArrayList<File> listOfFiles;
    private Path folderPath;

    public FileMaster(Path folderWithFiles){
        listOfFiles = new ArrayList<>();
        this.folderPath = folderWithFiles;
        generate(folderWithFiles);
    }

    public ArrayList<File> getListOfFiles() {
        return listOfFiles;
    }

    public boolean endsWithTest(File file, String extension){
        return file.getName().endsWith(extension);
    }

    public boolean endsWithAutoCapitalTest(File f, String s){
       return (endsWithTest(f,s.toLowerCase(Locale.ROOT)) || endsWithTest(f,s.toUpperCase(Locale.ROOT)));
    }

    public void generate(Path folderPath){
        try{
            Stream<Path> paths = Files.walk(folderPath,1);
            paths
                .filter(Files::isRegularFile)
                .forEach((pathToFile)->{
                    File currentFile = new File("" + pathToFile);
                    System.out.println(""+ pathToFile + " | Is avi: " + endsWithTest(currentFile,".AVI") );
                    listOfFiles.add(currentFile);
                });
        }catch (IOException exception){
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Checks if they are anny files in a folder if Y then returns true.
     * @return
     */
    public boolean generatePathBoolean(){
        try{
            Stream<Path> paths = Files.walk(folderPath,1);
            paths
                    .filter(Files::isRegularFile)
                    .forEach((pathToFile)->{
                        System.out.println(pathToFile.getFileName());
                        areThereFiles = true;
                    });
        }catch (IOException exception){
            System.err.println(exception.getMessage());
            areThereFiles = false;
        }
        return  areThereFiles;
    }


    /**
     * @return amount of AVI, avi files in folder.
     */
    public int videoFilesInFolder(){
        listOfFiles.forEach((e)->{
            if(endsWithTest(e,".AVI") || endsWithTest(e,".avi")) {
                System.out.println("AVI: " + e);
                videoFilesCounter++;
            }
        });
        return this.videoFilesCounter;
    }

    public boolean isVideoFile(File file){
        return endsWithAutoCapitalTest(file, ".avi") ||
                endsWithAutoCapitalTest(file, ".mp4") ||
                endsWithAutoCapitalTest(file, ".mov");
    }


}
