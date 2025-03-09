package com.markurion.videorenamer;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public class VideoMaster {
    private final String appDir;
    private File inputFile;
    private File outputFile;
    private String textToBurn;
    private String timetoBurn;
    private String titleToBurn;

    private String in;
    private String out;
    private int fontSize;
    private int x;
    private int y;
    private String fontcolor;
    private boolean overwrite;

    private boolean done;

    /**
     * Takes in file and burns the desired text on top.
     * @param in - String path to source file
     * @param out - String path to output file
     * @param textToBurn - Text to be burned on the file.
     * @throws IOException
     */
    public VideoMaster(String in, String out, String textToBurn, String timeToBurn) throws IOException, InterruptedException {
        this.timetoBurn = timeToBurn;
        this.done = false;
        this.appDir = System.getProperty("user.dir");
        checkFFmpeg();
       // this.in = in;
       // this.out = out;

        this.overwrite = true;
        this.fontSize = 18;
        this.x = 10;
        this.y = 55;
        this.fontcolor = "#00ff1e";
        this.textToBurn = textToBurn;
        this.in = in;

        //Transfer any extension to .mp4
        this.out = out.substring(0,out.lastIndexOf(".")) + ".mp4";
        setup();

//        this.in = "C:\\Users\\Marcepan\\Downloads\\video\\sourceVideosIV000001.AVI";
//        this.out = "C:\\Users\\Marcepan\\Downloads\\video\\test\\out.mp4";
    }

    /**
     * Overloaded with second topic
     * @param in
     * @param out
     * @param textToBurn
     * @param titleToBurn
     * @throws IOException
     * @throws InterruptedException
     */
    public VideoMaster(String in, String out, String textToBurn, String timeToBurn,String titleToBurn) throws IOException, InterruptedException {
        this.timetoBurn = timeToBurn;
        this.done = false;
        this.appDir = System.getProperty("user.dir");
        checkFFmpeg();

        this.overwrite = true;
        this.fontSize = 18;
        this.x = 10;
        this.y = 55;
        this.fontcolor = "#00ff1e";
        this.textToBurn = textToBurn;
        this.titleToBurn = titleToBurn;
        this.in = in;

        //Transfer any extension to .mp4
        this.out = out.substring(0, out.lastIndexOf(".")) + ".mp4";
        setup();
    }

    /**
     * Checks if thing is done.
     * @return if true mean is done.
     */
    public boolean getDone(){
        return this.done;
    }

    public void setup() throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        // Process builder accept only array so all commands need to be changed to array from arrayList.
//        String[] commands = {
//                "ffmpeg",
//                "-i",
//                this.in,
//                "-vf", String.format("drawtext=text='%s':fontsize=18:fontcolor=#00ff1e:borderw=1.5:bordercolor=black:x=10:y=55",texting)
//                ,"-c:a", "copy"
//                , this.out
//        };

        // Prepare Time to burn if exist. It will be burned on title string.
        if(!timetoBurn.isEmpty()){
            this.titleToBurn = this.titleToBurn + " Time: " + timetoBurn;
            this.titleToBurn = this.titleToBurn.replaceAll("\\\\", "\\\\\\\\\\\\\\\\").replaceAll("'", "'\\\\\\\\\\\\\''").replaceAll("%", "\\\\\\\\\\\\%").replaceAll(":", "\\\\\\\\\\\\:");
          }

        //Prepare font location for ffmpeg ......... in format without drive letter
        String fontLocation = String.valueOf(Paths.get(appDir + "\\" + "Armstrong.otf"));
        String replaced = fontLocation.replace("\\","/").substring(2);

        ArrayList<String> arrCommands = new ArrayList<>();
        arrCommands.add(String.valueOf(Paths.get(appDir + "\\" + "ffmpeg.exe")));

        //If arg overwrite add -y to a comand line
        if(overwrite){arrCommands.add("-y");}
        arrCommands.add("-i");
        arrCommands.add(this.in);
        arrCommands.add("-vf");

        if(titleToBurn != null){
            arrCommands.add(String.format(
                    "[in]drawtext='fontfile=%s':text='%s':fontsize=%d:fontcolor=%s:borderw=0.7:bordercolor=black:x=%d:y=%d, " +
                    "drawtext='fontfile=%s':text='%s':fontsize=%d:fontcolor=%s:borderw=0.7:bordercolor=black:x=%d:y=%d [out]"
                    ,replaced,titleToBurn,fontSize,fontcolor,x,y ,replaced,textToBurn,fontSize,fontcolor,x,y+20
            ));
        }else{
            arrCommands.add(String.format(
                    "drawtext='fontfile=%s':text='%s':fontsize=%d:fontcolor=%s:borderw=0.7:bordercolor=black:x=%d:y=%d"
                    ,replaced,textToBurn,fontSize,fontcolor,x,y
            ));
        }

        //Do not encode audio just copy it.
        arrCommands.add("-c:a");
        arrCommands.add("copy");
        arrCommands.add("-loglevel");
        arrCommands.add("quiet");
        arrCommands.add(this.out);

        Process proc = rt.exec(arrCommands.toArray(new String[0]));
        System.out.println("Vid convert proc created: " + this.textToBurn);
        proc.waitFor();
        this.done = true;

        // Uncomment to test Error output.
        //    outputErrorTest(proc);
    }

    /**
     * Checks if ffmpeg.exe exist if not, file is created.
     */
    private void checkFFmpeg() throws IOException {
        //If ffmpeg do not exist.
        if(!Files.exists(Paths.get(appDir + "\\" + "ffmpeg.exe"))){
            System.out.println("FFmpeg do not exist -> lets add one.");

            InputStream in = getClass().getResourceAsStream("ffmpeg/ffmpeg.exe");
            Files.copy(in, Paths.get(appDir + "\\" + "ffmpeg.exe"));
        }

        //If font not exist. (Ffmpeg does not have access to font from resources)
        if(!Files.exists(Paths.get(appDir + "\\" + "Armstrong.otf"))){
            System.out.println("Font do not exist -> lets add one.");

            InputStream in = getClass().getResourceAsStream("font/Armstrong.otf");
            Files.copy(in, Paths.get(appDir + "\\" + "Armstrong.otf"));
        }

    }

    public void outputErrorTest(Process proc) throws IOException {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

//      Read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s = null;

        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

//      Read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }

}

