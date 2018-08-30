/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bodastage.cm.ericssonbsmparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Emmanuel
 */
public class EricssonBSMParser {
    Logger logger = LoggerFactory.getLogger(EricssonBSMParser.class);
    
    /**
     * Current release version
     *
     * @since 0.1.0
     */
    final static String VERSION = "0.1.0";

    /**
     * The name of the node being connected to.
     *
     * @since 0.1.0 This is picked from the external system value.
     */
    String nodeName = "";

    /**
     * The AXE printout generation timestamp.
     *
     * @since 0.1.0
     */
    String dateTime = "";
    
    /**
     * File or directory to parse
     * 
     * @since 0.1.0
     */
    String dataSource;
    
    /**
     * File line counter
     */
    int lineCount = 0;
    
    
    /**
     * Current state of the parser state machine
     * 
     * @since 0.1.0
     */
    int parserState = ParserStates.EXTRACTING_VALUES;
    
    /**
     * Output file printout writer.
     * 
     */
    PrintWriter printWriter = null;
    
    /**
     * File to parse
     */
    String dataFile;
    
    String baseFileName;
    
    /**
     * Output directory.
     *
     * @since 0.1.0
     */
    String outputFile = "";
    
    /**
     * @param args the command line arguments
     *
     * @since 0.1.0
     * @version 0.1.0
     */
    public static void main(String[] args) {
        //Define
        Options options = new Options();
        CommandLine cmd = null;
        String outputFile = null;
        String inputFile = null;
        Boolean onlyExtractParameters = false;
        Boolean showHelpMessage = false;
        Boolean showVersion = false;
        Boolean attachMetaFields = false; //Attach mattachMetaFields FILENAME,DATETIME,TECHNOLOGY,VENDOR,VERSION,NETYPE

        try {
            options.addOption("v", "version", false, "display version");
            options.addOption(Option.builder("i")
                    .longOpt("input-file")
                    .desc("input file or directory name")
                    .hasArg()
                    .argName("INPUT_FILE").build());
            options.addOption(Option.builder("o")
                    .longOpt("output-file")
                    .desc("output file name")
                    .hasArg()
                    .argName("OUTPUT_FILE").build());
            options.addOption("h", "help", false, "show help");

            //Parse command line arguments
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                showHelpMessage = true;
            }

            if (cmd.hasOption("v")) {
                showVersion = true;
            }

            if (cmd.hasOption('o')) {
                outputFile = cmd.getOptionValue("o");
            }

            if (cmd.hasOption('i')) {
                inputFile = cmd.getOptionValue("i");
            }

        } catch (IllegalArgumentException e) {

        } catch (ParseException ex) {
//            java.util.logging.Logger.getLogger(HuaweiCMObjectParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            
            if(showVersion == true ){
                System.out.println(VERSION);
                System.out.println("Copyright (c) 2018 Bodastage Solutions(http://www.bodastage.com)");
                System.exit(0);
            }
            
            //show help
            if( showHelpMessage == true || 
                inputFile == null || 
                ( outputFile == null && onlyExtractParameters == false) ){
                     HelpFormatter formatter = new HelpFormatter();
                     String header = "Parses Ericsson BSM dumps to csv\n\n";
                     String footer = "\n";
                     footer += "Examples: \n";
                     footer += "java -jar boda-ericssonbsmparser.jar -i input_file -o out_file.csv\n";
                     footer += "java -jar boda-ericssonbsmparser.jar -i input_folder -o out_file.csv\n";
                     footer += "\nCopyright (c) 2018 Bodastage Solutions(http://www.bodastage.com)";
                     formatter.printHelp( "java -jar boda-ericssonbsmparser.jar", header, options, footer );
                     System.exit(0);
            }
            
            //Confirm that the output directory is a directory and has write 
            //privileges
            if(outputFile == null ){
                System.err.println("ERROR: Output file is required.");
            }
            
            //Get parser instance
            EricssonBSMParser cmParser = new EricssonBSMParser();
            
            
            cmParser.setDataSource(inputFile);
            if(outputFile != null ) cmParser.setOutputFile(outputFile);
            
            cmParser.parse();

        }catch(Exception e){
            System.out.println(e.getMessage());
            System.exit(1);
        }
        

        
    }
    

    /**
     * Set the output directory.
     *
     * @since 1.0.0
     * @version 1.0.0
     * @param String directoryName
     */
    public void setOutputFile(String fileName) {
        this.outputFile = fileName;
    }
    
    /**
     * Set name of file to parser.
     *
     * @since 0.1.0
     * @version 0.1.0
     * @param dataSource
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * Get file base name.
     * 
     * @since 0.1.0
     */
    public String getFileBasename(String filename){
        try{
            return new File(filename).getName();
        }catch(Exception e ){
            return filename;
        }
    }    
    
    public void parseFile(String inputFilename) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(inputFilename));
        lineCount = 0;
        for(String line; (line = br.readLine()) != null; ) {
            lineCount++;
            processLine(line);
        }
    }
        
    /**
     * Holds the parser logic.
     * 
     * @param line  String
     * @since 1.0.0
     * @version 1.0.0
     */
    public void processLine(String line){

        if(lineCount == 2 && line.startsWith("Created")){
            String pattern = "(.*)on (.*)$";        
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(line);
            if(m.find()) dateTime = m.group(2);
            return;
        }
        
        if (lineCount < 6 ) return;
        
//        System.out.println(line);
        String[] fields = line.split("\t");

        String data = baseFileName + "," + dateTime;
        for(int i =0; i < fields.length; i++){
            data += "," + toCSVFormat(fields[i]);
        }
        printWriter.println(data);
        
    }
    
    public void parse() throws IOException {

        printWriter = new PrintWriter(this.outputFile);
        printWriter.println("FILENAME,DATETIME,DN,RUTAG,RULOGICALID,RUSERIALNO,RUREVISION,RUPOSITION,MODEL,MO,VENDOR,RU");
        
        if (parserState == ParserStates.EXTRACTING_PARAMETERS) {
            processFileOrDirectory();

            parserState = ParserStates.EXTRACTING_VALUES;
        }


        //Extracting values
        if (parserState == ParserStates.EXTRACTING_VALUES) {
            processFileOrDirectory();
            parserState = ParserStates.EXTRACTING_DONE;
        }

        printWriter.close();
    }
    
    public void processFileOrDirectory()
            throws FileNotFoundException, IOException {
        //this.dataFILe;
        Path file = Paths.get(dataSource);
        boolean isRegularExecutableFile = Files.isRegularFile(file)
                & Files.isReadable(file);

        boolean isReadableDirectory = Files.isDirectory(file)
                & Files.isReadable(file);

        if (isRegularExecutableFile) {
            dataFile = dataSource;
            baseFileName =  getFileBasename(dataFile);
            if( parserState == ParserStates.EXTRACTING_PARAMETERS){
                System.out.print("Extracting parameters from " + baseFileName + "...");
            }else{
                System.out.print("Parsing " + baseFileName + "...");
            }
            parseFile(dataSource);
            if( parserState == ParserStates.EXTRACTING_PARAMETERS){
                 System.out.println("Done.");
            }else{
                System.out.println("Done.");
               //System.out.println(this.baseFileName + " successfully parsed.\n");
            }
        }

        if (isReadableDirectory) {

            File directory = new File(dataSource);

            //get all the files from a directory
            File[] fList = directory.listFiles();

            for (File f : fList) {
                setFileName(f.getAbsolutePath());
                dataFile = f.getAbsolutePath();
                try {
                    baseFileName =  getFileBasename(f.getAbsolutePath());
                    if( parserState == ParserStates.EXTRACTING_PARAMETERS){
                        System.out.print("Extracting parameters from " + baseFileName + "...");
                    }else{
                        System.out.print("Parsing " + baseFileName + "...");
                    }
                    
                    //Parse
                    parseFile(f.getAbsolutePath());
                    if( parserState == ParserStates.EXTRACTING_PARAMETERS){
                         System.out.println("Done.");
                    }else{
                        System.out.println("Done.");
                        //System.out.println(this.baseFileName + " successfully parsed.\n");
                    }
                   
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println("Skipping file: " + baseFileName + "\n");
                }
            }
        }

    }

    /**
     * Process given string into a format acceptable for CSV format.
     *
     * @since 1.0.0
     * @param s String
     * @return String Formated version of input string
     */
    public String toCSVFormat(String s) {
        String csvValue = s;

        //Check if value contains comma
        if (s.contains(",")) {
            csvValue = "\"" + s + "\"";
        }

        if (s.contains("\"")) {
            csvValue = "\"" + s.replace("\"", "\"\"") + "\"";
        }

        return csvValue;
    }
    
    /**
     * Set name of file to parser.
     *
     * @since 1.0.0
     * @version 1.0.0
     * @param String filename
     */
    public void setFileName(String filename) {
        this.dataFile = filename;
    }
}
