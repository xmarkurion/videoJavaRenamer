package com.markurion.videorenamer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;

public class CsvMaster {
    private String filename;
    private String pathToCSVFile;
    private boolean override;
    private String fullPathToCsvFile;

    public CsvMaster() {
        this.override = false;
        this.filename = "VideoList.csv";
        this.pathToCSVFile = "";
    }
    public String getFullPathToCsvFile() {
        return fullPathToCsvFile;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setPathToCSVFile(String path) {
        this.pathToCSVFile = path;
    }

    /**
     * void Generates CSV file in provided path for the file
     * @param fileList - arrayLst of type File
     * @throws IOException
     */
    public void generateCSV (ArrayList<File> fileList) throws IOException{
            this.fullPathToCsvFile = pathToCSVFile + "\\" + filename;

            if(!Files.exists(Paths.get(fullPathToCsvFile)) || override){
                System.out.println("Generating csv file from videos list....");
                try (
                        BufferedWriter writer = Files.newBufferedWriter(Paths.get(fullPathToCsvFile));
                        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "New_File_Name", " OLD_File_name"));
                ) {
                    for(int z=0; z <= fileList.size()-1; z++){
                        csvPrinter.printRecord(z,"",fileList.get(z).getName());
                    }
                    csvPrinter.flush();
                }
            }else {
                System.out.println("CSV is Already generated! ");
            }
        }

    /**
     *Reads csv file
     * @return ArrayList of String
     * @throws IOException
     */
    public ArrayList<String> readCSV() throws IOException {
            ArrayList<String> newFileNames = new ArrayList<>();

            this.fullPathToCsvFile = pathToCSVFile + "\\" + filename;

            Reader reader = Files.newBufferedReader(Paths.get(fullPathToCsvFile));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withHeader("ID", "New_File_Name", "OLD_File_name")
                    .withIgnoreHeaderCase()
                    .withTrim());

            for(CSVRecord csvRecord : csvParser){

                if( csvRecord.getRecordNumber() !=1 ){
                    String id = csvRecord.get("ID");
                    String oldFileName = csvRecord.get("OLD_File_name");
                    String newFileName = csvRecord.get("New_File_Name");
                    String fullFile = newFileName + "." + oldFileName.substring(oldFileName.lastIndexOf(".")+1);

                    newFileNames.add(fullFile);
                    System.out.println(csvRecord.getRecordNumber() + "  ID: " + id +  " New File name: " + fullFile + " || Was: " + oldFileName);
                }
            }

            System.out.println("Reading CSV done....");
            return newFileNames;
        }
    }

