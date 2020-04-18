package jo.BankruptcyPredictionProject.Utility;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class BPPLogger {

    private static final String logFilePath = "./src/main/resources/log.txt";

    public static void log(String message) {
        try {
            System.out.println(message);
            BufferedWriter bw = new BufferedWriter(new FileWriter(logFilePath, true));
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            bw.append(timestamp.toString()).append(" ").append(message).append("\n");
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            System.out.println("File: " + logFilePath + " not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Writing to file: " + logFilePath + " failed!");
            e.printStackTrace();
        }
    }

    public static void clear() {
        try {
            FileWriter fw = new FileWriter(logFilePath, false);
            fw.close();
        } catch (FileNotFoundException e) {
            System.out.println("File: " + logFilePath + " not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Writing to file: " + logFilePath + " failed!");
            e.printStackTrace();
        }
    }

}