package com.markurion.videorenamer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class VideoMaster {
    private Mode mode = Mode.MODE1;

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
    private boolean watermark;

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setWatermark(boolean watermark) {
        this.watermark = watermark;
    }

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

        this.overwrite = true;
        this.fontSize = 18;
        this.x = 10;
        this.y = 55;
        this.fontcolor = "#00ff1e";
        this.textToBurn = textToBurn;
        this.in = in;

        //Transfer any extension to .mp4
        this.out = out.substring(0,out.lastIndexOf(".")) + ".mp4";

        // setup() should be triggered after mode was set from the main controller.
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

        // Prepare Time to burn if existed. It will be burned on title string.
        if(!timetoBurn.isEmpty()){
            if (this.titleToBurn == null){
                this.titleToBurn = "";
            }

            if (mode == Mode.MODE2) {
                this.titleToBurn = timetoBurn + "   " + this.titleToBurn;
            }

            if (mode == Mode.MODE1) {
                this.titleToBurn = this.titleToBurn + " Time: " + timetoBurn;
            }

            this.titleToBurn = this.titleToBurn.replaceAll("\\\\", "\\\\\\\\\\\\\\\\").replaceAll("'", "'\\\\\\\\\\\\\''").replaceAll("%", "\\\\\\\\\\\\%").replaceAll(":", "\\\\\\\\\\\\:");
          }

        //Prepare font location for ffmpeg ......... in format without drive letter
        String fontLocation = String.valueOf(Paths.get(appDir + "\\" + "Armstrong.otf"));
        String replaced = fontLocation.replace("\\","/").substring(2);

        // Prepare image
        String imageLocation = String.valueOf(Paths.get(appDir + "\\" + "logo.png"));
        String replacedImage = imageLocation.replace("\\","/").substring(2);

        ArrayList<String> arrCommands = new ArrayList<>();
        arrCommands.add(String.valueOf(Paths.get(appDir + "\\" + "ffmpeg.exe")));

        //If arg overwrite add -y to a command line
        if(overwrite){arrCommands.add("-y");}
        arrCommands.add("-i");
        arrCommands.add(this.in);
//        arrCommands.add("-vf");
        arrCommands.add("-i");
        arrCommands.add(replacedImage);
        arrCommands.add("-filter_complex");


        //Overwrite to turn off the text to burn
        if(mode == Mode.MODE2)
        {
            textToBurn = "";
        }

        // Enable or disable watermark on demand.
        String logo = "";
        if(watermark){
            logo = logoPosition("right");
        }

        if(titleToBurn != null){
            arrCommands.add(String.format(
                    logo + "drawtext='fontfile=%s':text='%s':fontsize=%d:fontcolor=%s:borderw=0.7:bordercolor=black:x=%d:y=%d, " +
                    "drawtext='fontfile=%s':text='%s':fontsize=%d:fontcolor=%s:borderw=0.7:bordercolor=black:x=%d:y=%d"
                    ,replaced,titleToBurn,fontSize,fontcolor,x,y ,replaced,textToBurn,fontSize,fontcolor,x,y+20
            ));


        }else{
            arrCommands.add(String.format(
                    logo + "drawtext='fontfile=%s':text='%s':fontsize=%d:fontcolor=%s:borderw=0.7:bordercolor=black:x=%d:y=%d"
                    ,replaced,textToBurn,fontSize,fontcolor,x,y
            ));
        }

        // Encode audio to aac
        arrCommands.add("-c:a");
        arrCommands.add("aac");
        arrCommands.add("-loglevel");
        arrCommands.add("quiet");
        arrCommands.add(this.out);

        Process proc = rt.exec(arrCommands.toArray(new String[0]));
        System.out.println("Vid convert proc created: " + this.textToBurn);
        proc.waitFor();
        this.done = true;

        // Uncomment to test Error output.
//            outputErrorTest(proc);
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

        // If Watermark image do not exist
        if(!Files.exists(Paths.get(appDir + "\\" + "logo.png"))){
            System.out.println("Watermark image do not exist -> lets add one.");

            InputStream in = getClass().getResourceAsStream("images/logo.png");
            Files.copy(in, Paths.get(appDir + "\\" + "logo.png"));
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

    /**
     * Returns the position of the logo. With padding 5
     * @param position - String left or right
     * @return String
     */
    private String logoPosition(String position){
        if (position.equals("left")) {
            return "[1:v]scale=iw*0.6:ih*0.6[wm];[0:v][wm]overlay=5:5,";
        }
        if (position.equals("right")) {
            return "[1:v]scale=iw*0.6:ih*0.6[wm];[0:v][wm]overlay=W-w-5:5,";
        }
        return "";
    }

}

