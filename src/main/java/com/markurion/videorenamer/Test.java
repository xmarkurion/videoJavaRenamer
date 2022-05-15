package com.markurion.videorenamer;

import com.itextpdf.text.DocumentException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Test {

    private static final String FILE_NAME = "/MyFirstExcel.xlsx";

    public static void main(String[] args) throws IOException, DocumentException {
        System.out.println("Before test");
        pdfTest();
        System.out.println("After test....");
    }

    public static void pdfTest() throws DocumentException, FileNotFoundException {
//        PdfMaster pdf = new PdfMaster("grator");
//        pdf.generatePDF();

    }

    public static void videotest() throws IOException, InterruptedException {
        String in = "C:\\Users\\Marcepan\\Downloads\\video\\sourceVideosIV000001.AVI";
        String out = "C:\\Users\\Marcepan\\Downloads\\video\\test\\outsss.MKV";

        VideoMaster vid = new VideoMaster(in, out,"ISO-123-324234-4324 Amesor");
//        vid.setup();
//       vid.checkFFmpeg();
//        vid.check();
    }

    public void csvTest() throws IOException {

        String pathToCSVFile = "C:\\Users\\Marcepan\\Downloads\\video";
        ArrayList<File> fileList = new ArrayList<>();
        fileList.add(new File(pathToCSVFile + "sourceVideosIV000001.AVI"));
        fileList.add(new File(pathToCSVFile + "sourceVideosIV000002.AVI"));
        fileList.add(new File(pathToCSVFile + "sourceVideosIV000003.AVI"));
        fileList.add(new File(pathToCSVFile + "sourceVideosIV000005.AVI"));

        CsvMaster csvMaster = new CsvMaster();
        csvMaster.setPathToCSVFile(pathToCSVFile);
        csvMaster.generateCSV(fileList);
        //dfsdfsdfd1

        ArrayList<String> fileNames = csvMaster.readCSV();
        fileNames.forEach(e->{
            System.out.println(e);
        });
    }

    }

