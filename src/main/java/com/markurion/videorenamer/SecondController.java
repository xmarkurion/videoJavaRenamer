package com.markurion.videorenamer;

import com.itextpdf.text.DocumentException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.Desktop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SecondController {
    @FXML
    private Button btnOpenOutputFolder;
    @FXML
    private Button btnOutFolder;
    @FXML
    private CheckBox checkBoxAddTitle;
    @FXML
    private TextField fieldVideoTitle;
    @FXML
    private AnchorPane anchor;
    @FXML
    private ProgressIndicator progressCircle;
    @FXML
    private Label labelProgress;
    @FXML
    private Button btnRename;
    @FXML
    private Button btnBurn;
    @FXML
    private ListView<String> listView;
    @FXML
    private Button btnTryReading;
    @FXML
    private Button btnGenExcel;
    @FXML
    private Label statusLabel;
    @FXML
    private Button btnCopy;
    @FXML
    private Button btnMove;
    @FXML
    private Label sourceLabel;
    @FXML
    private Label videosAmountLabel;
    @FXML
    private TextField textFieldOutFolder;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private boolean namesStatus;
    private ArrayList<String> modNames;
    private ArrayList<String> pdfBurnedNames;

    private File outFolder;
    private ArrayList<File> mainFileList;

    private FileMaster fileMasterInFolder;
    private FolderMaster folderMaster;

    private MarkurionHelper helper;
    private CsvMaster csvMaster;

    private int counter;
    private String tempString;
    private boolean configPresent;


    public SecondController() {
        System.out.println("Second Controller - Initialized");
        helper = new MarkurionHelper();
        folderMaster = new FolderMaster();
        csvMaster = new CsvMaster();
        mainFileList = new ArrayList<>();
        namesStatus = false;
        counter = 0;
        tempString = "";
        configPresent = false;
        pdfBurnedNames = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        statusLabel.setText("Please select Output folder.");
        progressCircle.setVisible(false);
        labelProgress.setVisible(false);
    }

    public void loadConfigFilePresentSettings(){
        System.out.println("Loading settings for config found scenario.");
        configPresent = true;
        this.outFolder = new File(sourceLabel.getText());
        btnOutFolder.setDisable(true);

        textFieldOutFolder.setText(sourceLabel.getText());
        textFieldOutFolder.setDisable(true);

        btnCopy.setDisable(false);
        btnCopy.fire();
    }

    public void setFileInFolder(FileMaster e) {
        this.fileMasterInFolder = e;
    }

    public void setInFolder(File inFolder) {
        sourceLabel.setText(inFolder.getPath());
    }

    public void setVideosAmount(int amount) {
        videosAmountLabel.setText("" + amount);
    }

    public void back(ActionEvent e) throws IOException {
        System.out.println("Back-Clicked!");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void btnOutFolderClick(ActionEvent e) throws IOException {
        this.outFolder = helper.pickFolderDBB(textFieldOutFolder, stage);
        if (outFolder != null) {
            statusLabel.setText("Please select Copy or Move.");
            folderActionBtn(false);
            btnOutFolder.setDisable(true);
            btnOpenOutputFolder.setDisable(false);
        }
    }

    public void btnCopyClick(ActionEvent e) throws IOException {
        System.out.println("Btn Copy Click.");
        mainFileList.clear();
        if (outFolder != null) {
            folderMaster.mkdirName(outFolder.getPath() + "\\sourceVideos");
            fileMasterInFolder.getListOfFiles().forEach((file) ->
            {
                Path sourcePath = Paths.get(String.valueOf(file));
                String destPath = outFolder.getPath() + "\\sourceVideos\\" + file.getName();

                if (fileMasterInFolder.isVideoFile(file)) {
                    if (new File(destPath).exists()) {
                        System.out.println("File exist: " + destPath);
                    } else {
                        Path dest = Paths.get(destPath);
                        try {
                            Files.copy(sourcePath, dest);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    mainFileList.add(new File(destPath));
                }
            });
            folderActionBtn(true);
            btnGenExcel.setDisable(false);


            if (configPresent){
                System.out.println("Config present - lets try to read configuration");
                setExistingCSVFileAndOpenIt();
                btnOpenOutputFolder.setDisable(false);
            } else {
                generateCSVFileAndOpenIt();
            }

        }
    }

    public void btnMoveClick(ActionEvent e) throws IOException {
        System.out.println("Btn Move Click.");
        mainFileList.clear();
        if (outFolder != null) {
            folderMaster.mkdirName(outFolder.getPath() + "\\sourceVideos");
            fileMasterInFolder.getListOfFiles().forEach((file) ->
            {
                String destPath = outFolder.getPath() + "\\sourceVideos\\" + file.getName();
                if (fileMasterInFolder.isVideoFile(file)) {
                    file.renameTo(new File(destPath));
                    mainFileList.add(new File(destPath));
                }
            });
            folderActionBtn(true);
            btnGenExcel.setDisable(false);
            generateCSVFileAndOpenIt();
        }
    }

    /**
     * Disables or enables Folder Action buttons
     *
     * @param q
     */
    public void folderActionBtn(boolean q) {
        btnMove.setDisable(q);
        btnCopy.setDisable(q);
    }

    public void btnOpenExcelClick(ActionEvent e) throws IOException {
        System.out.println("Gen Excel Clicked!");
        Desktop.getDesktop().open(new File(csvMaster.getFullPathToCsvFile()));
    }

    public void btnTryReadingCSVData(ActionEvent e) throws IOException {
        System.out.println("Btn Try Reading CSV data clicked!....");
        populateListView();
    }

    private void prepareCSV() throws IOException{
        csvMaster.setPathToCSVFile(outFolder.getPath());
        csvMaster.setOverride(false);
        csvMaster.generateCSV(mainFileList);
    }

    public void generateCSVFileAndOpenIt() throws IOException {
        prepareCSV();
        Desktop.getDesktop().open(new File(csvMaster.getFullPathToCsvFile()));
        statusLabel.setText("Please fill CSV document & click -> Try Read CSV ");
        btnTryReading.setDisable(false);
    }

    public void setExistingCSVFileAndOpenIt() throws IOException {
        prepareCSV();
        statusLabel.setText("CSV read from existing data file. ");
        btnTryReading.setDisable(false);
        btnTryReading.fire();
    }

    public void populateListView() throws IOException {
        listView.getItems().clear();
        modNames = csvMaster.readCSV();

        pdfBurnedNames.clear();
        modNames.forEach(name->{
            name = removeTimeToBurn(name);
            pdfBurnedNames.add( name.substring(0,name.lastIndexOf("."))+ ".mp4");
        });
        this.namesStatus = false;

        // You can't add same names to set that my way of finding duplicates
        Set<String> setNames = new HashSet<>();

        //Check if amount is the same
        if (modNames.size() == mainFileList.size()) {
            for (int x = 0; x <= modNames.size() - 1; x++) {

                int nameLength = modNames.get(x).length();
                int extLength = (modNames.get(x).substring(modNames.get(x).lastIndexOf(".") + 1).length());

                // IF there is no name entered the Excel.
                if (extLength + 1 == nameLength) {
                    String fullString = "IN: " + mainFileList.get(x).getName() + "  ||-->||  ENTER NEW NAME PLEASE!";
                    listView.getItems().add(fullString);
                    this.namesStatus = true;
                }
                // Here trying to add name that exist will result in false.
                if (!setNames.add(modNames.get(x))) {
                    String fullString = "IN: " + mainFileList.get(x).getName() + "  ||-->||  " + modNames.get(x) + " || Duplicate Name!";
                    listView.getItems().add(fullString);
                    this.namesStatus = true;
                }
                else {
                    String fullString = "IN: " + mainFileList.get(x).getName() + "  ||-->||  " + modNames.get(x);
                    listView.getItems().add(fullString);
                }

            }
        }

        if (namesStatus) {
            statusLabel.setText("One of the names is not provided. Or the file name is duplicated.");
            btnRename.setDisable(true);
            btnBurn.setDisable(true);
        } else {
            statusLabel.setText("All Good If you are happy press |Change Names| OR |Burn Video Title| \"");
            btnRename.setDisable(false);
            btnBurn.setDisable(false);
        }
    }


    public void btnRename(ActionEvent e) throws IOException, DocumentException {
        System.out.println("Btn Rename Clicked! .... ");

        progressCircle.setVisible(true);
        labelProgress.setVisible(true);
        resetCounter();
        double increment = (100.0 / modNames.size()) * 0.01;

        String renamedFolderPath = outFolder.getPath() + "\\RenamedVideos";
        folderMaster.mkdirName(renamedFolderPath);

        Runnable counter = () -> {
            progressCircle.setProgress(progressCircle.getProgress() + increment);
            if (progressCircle.getProgress() >= 0.99) {
                progressCircle.setProgress(100.0);
            }

            //Solve the issue with unexpected javaFX exception
            Platform.runLater(() -> {
                if (progressCircle.getProgress() == 100) {
                    labelProgress.setText("All " + modNames.size() + " files are processed. Have a great Day!");
                }
            });
        };

        AtomicInteger q = new AtomicInteger();
        mainFileList.forEach(file -> {
            Path source = Paths.get(file.getPath());
            String fileNameCleanedFromTime = removeTimeToBurn(modNames.get(q.get()));
            Path dest = Paths.get(renamedFolderPath + "\\" + fileNameCleanedFromTime);

            System.out.println("Source: " + source);
            System.out.println("  Dest: " + dest);

            this.tempString = modNames.get(q.get());

            Thread count = new Thread(counter);
            count.start();

            try {
                count.join(0);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            //If file is not in the folder already.
            if (!Files.exists(dest)) {
                try {
                    Files.copy(source, dest);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            q.getAndIncrement();
        });
        q.set(0);

        Runnable gen = () -> {
            try {
                generatePDF(null,renamedFolderPath,false);
            } catch (DocumentException | FileNotFoundException ex) {
                ex.printStackTrace();
            }
        };

        Thread genn = new Thread(gen);
        genn.start();

        Desktop.getDesktop().open(new File(renamedFolderPath));

    }

    private ArrayList<String> getMainFileListInStringArray(){
        ArrayList<String> oldNames = new ArrayList<>();
        mainFileList.forEach(file -> oldNames.add(file.getName()));
        return oldNames;
    }

    public void generatePDF(String title,String dir, boolean burned) throws DocumentException, FileNotFoundException {
        String now = "" + System.currentTimeMillis();
        //Renaming to mp4 if burned is true
        PdfMaster pdf;
        if(burned){
            pdf = new PdfMaster("Rename_report_" + now, getMainFileListInStringArray(), pdfBurnedNames, dir);
            pdf.setVideoNameBurnedOnVideo(true);
        }else {
            // Foreach modnames remove time to burn
            ArrayList<String> noTimeToBurnFileNames = new ArrayList<>();
            this.modNames.forEach(name -> noTimeToBurnFileNames.add(removeTimeToBurn(name)));


            pdf = new PdfMaster("Rename_report_" + now, getMainFileListInStringArray(), noTimeToBurnFileNames, dir);
        }
        preGeneratePDF(title,pdf);
    }

    private void preGeneratePDF(String title, PdfMaster pdf) throws DocumentException, FileNotFoundException {
        if(title != null){
            pdf.setOptionalVideoTitle(title);
        }
        pdf.generatePDF();
    }

    public void btnBurn(ActionEvent e) {
        System.out.println("Btn Burn Clicked! .... ");
        checkBoxAddTitle.setDisable(false);
        fieldVideoTitle.setDisable(false);

        progressCircle.setVisible(true);
        labelProgress.setVisible(true);
        resetCounter();
        double increment = (100.0 / modNames.size()) * 0.01;

        String burnedFolderPath = outFolder.getPath() + "\\BurnedVideos";
        folderMaster.mkdirName(burnedFolderPath);

        Runnable counter = () -> {
            progressCircle.setProgress(progressCircle.getProgress() + increment);
            if (progressCircle.getProgress() >= 0.99) {
                progressCircle.setProgress(100.0);
            }


            //Solve the issue with unexpected javaFX exception
            Platform.runLater(() -> {
                System.out.println("Processing file: " + this.tempString);
                labelProgress.setText(tempString);
                if (progressCircle.getProgress() == 100) {
                    labelProgress.setText("All " + modNames.size() + " files are processed. Have a nice Day!");
                    try {
                        Desktop.getDesktop().open(new File(burnedFolderPath));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        };

        Runnable k = () -> {
            AtomicInteger q = new AtomicInteger();
            mainFileList.forEach(file -> {

                String source = file.getPath();
                String originalModname = modNames.get(q.get());

                // extract from textToBurn and remove time from textToBurn
                String timetoBurn= "";
                if (originalModname.contains("(t=")){
                    timetoBurn = extractTimeToBurn(originalModname);
                    originalModname = removeTimeToBurn(originalModname);
                }

                String textToBurn = originalModname.substring(0, originalModname.lastIndexOf("."));
                String destination = burnedFolderPath + "\\" + originalModname;

                System.out.println("--------");
                System.out.println(textToBurn);
                if(!timetoBurn.equals("")){
                    System.out.println("Time that will be burned: " + timetoBurn);
                }
                System.out.println(destination);

                String existedFile = destination.substring(0, destination.lastIndexOf(".")) + ".mp4";

                //Way of handling interface out of the lambda function;
                this.tempString = originalModname;
                new Thread(counter).start();

                if (Files.exists(Paths.get(existedFile))) {
                    System.out.println("File exist: " + existedFile);
                } else {
                    // If title is not set
                    if (!checkBoxAddTitle.isSelected()) {
                        try {
                            VideoMaster tea = new VideoMaster(source, destination, textToBurn, timetoBurn);
                        } catch (IOException | InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        try {
                            VideoMaster tea = new VideoMaster(source, destination, textToBurn, timetoBurn, fieldVideoTitle.getText());
                        } catch (IOException | InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                }
                q.getAndIncrement();
                this.counter++;
            });
        };
        new Thread(k).start();

        Runnable pdftask = () -> {
            if (!checkBoxAddTitle.isSelected()){
                try {
                    generatePDF(null, burnedFolderPath,true);
                } catch (DocumentException | FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }else{
                try {
                    generatePDF(fieldVideoTitle.getText(), burnedFolderPath,true);
                } catch (DocumentException | FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }

        };

        new Thread(pdftask).start();

    }

    public void resetCounter() {
        this.counter = 0;
        progressCircle.setProgress(0);
    }

    public void checkBoxActionHandler() {
        if (checkBoxAddTitle.isSelected()) {
            fieldVideoTitle.setDisable(false);
        } else {
            fieldVideoTitle.setDisable(true);
            fieldVideoTitle.clear();
        }
    }

    public void setBtnOpenOutputFolderHandler(ActionEvent e) throws IOException {
        Desktop.getDesktop().open(new File(outFolder.getPath()));
    }

    private String extractTimeToBurn(String originalModname){
        String timetoBurn= "";
        if (originalModname.contains("(t=")){
            timetoBurn = originalModname.substring(originalModname.indexOf("(t=")+3,originalModname.indexOf(")"));
        }
        return timetoBurn;
    }

    private String removeTimeToBurn(String textToBurn){
        if (textToBurn.contains("(t=")){
            textToBurn = textToBurn.replace("(t="+extractTimeToBurn(textToBurn)+")","");
        }
        return textToBurn;
    }
}
