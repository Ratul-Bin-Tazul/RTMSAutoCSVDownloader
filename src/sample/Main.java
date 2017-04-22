package sample;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;
import java.util.TimerTask;

public class Main extends Application {

    public static String lastSyncTime="";
    public static long lastSyncTimeLong=0;
    public static String timeLeftMinute="00";
    public static String timeLeftSeconds="00";
    public static final int[] sec = {Integer.parseInt(timeLeftSeconds)};
    public static final int[] min = {Integer.parseInt(timeLeftMinute)};
    static Label timeLeft;
    static Label syncStatusLable;
    public static Label lastSyncedTimeLabel;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        BorderPane borderPane = new BorderPane();

        //top
        BorderPane topBorderpane = new BorderPane();
        topBorderpane.setPadding(new Insets(10));
        Label lastSyncedLabel = new Label("Last Synced:");
        lastSyncedLabel.setAlignment(Pos.CENTER_LEFT);


        lastSyncedTimeLabel = new Label(lastSyncTime);
        lastSyncedTimeLabel.setAlignment(Pos.CENTER_RIGHT);

        topBorderpane.setLeft(lastSyncedLabel);
        topBorderpane.setRight(lastSyncedTimeLabel);
        //hBox.getChildren().addAll(lastSyncedLabel,lastSyncedTimeLabel);



        //center
        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(10));
        vBox.setAlignment(Pos.CENTER);
        vBox.setFillWidth(true);

        timeLeft = new Label(timeLeftMinute+" : "+timeLeftSeconds);
        timeLeft.getStyleClass().add("syncStatusLable");
        syncStatusLable = new Label("Time till next Sync:");
        timeLeft.getStyleClass().add("timeLeftText");

        Label minLabel = new Label("Minutes");
        timeLeft.getStyleClass().add("syncStatusLable");

        //vBox.setFillWidth(true);
        //timeLeft.setTextAlignment(TextAlignment.CENTER);
        vBox.getChildren().addAll(syncStatusLable,timeLeft,minLabel);

        //bottom
        //HBox hBox = new HBox(10);
        BorderPane bottomBorderpane = new BorderPane();
        bottomBorderpane.setPadding(new Insets(10));
        JFXButton cancelBtn = new JFXButton("Exit");
        //cancelBtn.setAlignment(Pos.CENTER_LEFT);
        cancelBtn.setPrefWidth(135);
        cancelBtn.setButtonType(JFXButton.ButtonType.RAISED);
        cancelBtn.getStyleClass().addAll("btn");
        cancelBtn.setOnAction(event -> {
            primaryStage.close();
        });

        JFXButton updateNowBtn = new JFXButton("Update Now");
        //updateNowBtn.setAlignment(Pos.CENTER_RIGHT);
        updateNowBtn.setPrefWidth(135);
        updateNowBtn.setButtonType(JFXButton.ButtonType.RAISED);
        updateNowBtn.getStyleClass().addAll("btn");
        updateNowBtn.setOnAction(event -> {
            syncCSV();
            lastSyncTime = getCurrentTime();
            min[0] = 05;
            sec[0] = 00;
        });
        //hBox.getChildren().addAll(cancelBtn,updateNowBtn);
        bottomBorderpane.setLeft(cancelBtn);
        bottomBorderpane.setRight(updateNowBtn);

        borderPane.setCenter(vBox);
        borderPane.setTop(topBorderpane);
        borderPane.setBottom(bottomBorderpane);
        Scene scene = new Scene(borderPane,300,300);
        scene.getStylesheets().add(
                getClass().getResource("mainStyle.css").toExternalForm());
        primaryStage.setTitle("RTMS Auto sync");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("Img/logo.png"));

        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false);



        timer();
    }


    public static void main(String[] args) {
        //syncCSV();
        lastSyncTime = getCurrentTime();
        lastSyncTimeLong = System.currentTimeMillis();
        launch(args);

    }

    public static String getCurrentTime() { //creating the csv
        String timeStamp = new SimpleDateFormat("hh:mm").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    public static void syncCSV() { //creating the csv


        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        min[0] = 5;
                        sec[0] =00;

                        syncStatusLable.setText("Sync Started");
                        timeLeft.setText("0"+min[0] + " : 0" + sec[0]);

                    }
                });
                //System.out.print(System.getProperty(" user.dir "));
                String filename ="test.csv";
                String workingFileName = "working.csv";
                try {
                    FileWriter fw = new FileWriter(filename);
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    Connection conn = DriverManager.getConnection("jdbc:mysql://www.rafathossain.com/rafathos_tazul", "rafathos_tazul", "tazul_2016");
                    String query = "select * from test_table";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    fw.append("id");
                    fw.append(',');
                    fw.append("Dnso Name");
                    fw.append(',');
                    fw.append("District");
                    fw.append(',');
                    fw.append("Year");
                    fw.append(',');
                    fw.append("Month");
                    fw.append(',');
                    fw.append("First Line");
                    fw.append(',');
                    fw.append("Front Line");
                    fw.append(',');
                    fw.append("Male");
                    fw.append(',');
                    fw.append("Female");
                    fw.append(',');
                    fw.append("Anthropocentric Measurment");
                    fw.append('\n');

                    while (rs.next()) {
                        fw.append(rs.getString(1));
                        fw.append(',');
                        fw.append(rs.getString(2));
                        fw.append(',');
                        fw.append(rs.getString(3));
                        fw.append(',');
                        fw.append(rs.getString(4));
                        fw.append(',');
                        fw.append(rs.getString(5));
                        fw.append(',');
                        fw.append(rs.getString(6));
                        fw.append(',');
                        fw.append(rs.getString(7));
                        fw.append(',');
                        fw.append(rs.getString(8));
                        fw.append(',');
                        fw.append(rs.getString(9));
                        fw.append(',');
                        fw.append(rs.getString(10));
                        fw.append('\n');
                    }
                    fw.flush();
                    fw.close();
                    conn.close();
                    System.out.println("CSV File is created successfully.");
                    //renameFile(workingFileName,filename);
                } catch (Exception e) {
                    e.printStackTrace();
                }



                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        lastSyncedTimeLabel.setText(getCurrentTime());
                        syncStatusLable.setText("Time till next Sync:");
                    }
                });


                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();



    }

    public static void renameFile(String oldName,String newName) { //renaming the csv to match the original one
        File oldfile =new File(oldName);
        File newfile =new File(newName);
        try{
            //oldfile.renameTo(newfile);
            Path source = Paths.get(oldName);
            Files.move(source, source.resolveSibling(newName));
        }catch (Exception e) {
            System.out.print(e);
        }



//        if(oldfile.renameTo(newfile)){
//            System.out.println("Rename succesful");
//        }else{
//            System.out.println("Rename failed");
//        }

    }

    public void timer() {

        long tStart = lastSyncTimeLong;
//        while(true) {
//            long tEnd = System.currentTimeMillis();
//            long tDelta = tEnd - tStart;
//            double elapsedSeconds = tDelta / 1000.0;
//
//            System.out.print("bbbb");
//            if(sec!=0 && (int) elapsedSeconds!=0) {
//                sec  = sec-(int) elapsedSeconds;
//                timeLeft.setText(min+" : "+sec);
//            }else{
//                if(min==0) {
//                    min=5;
//                }else {
//                    min--;
//                    sec=59;
//                }
//                timeLeft.setText(min+" : "+sec);
//            }
//        Task task = new Task<Void>() {
//            @Override
//            public Void call() throws Exception {
//                int i = 0;
//                while (true) {
//
//                    Thread.sleep(1000);
//                }
//            }
//        };
//        Thread th = new Thread(task);
//        th.setDaemon(true);
//        th.start();
//
//        }

        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                int i = 0;
                while (true) {
                    //final int finalI = i;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //long tEnd = System.currentTimeMillis();
                            //long tDelta = tEnd - tStart;
                            //double elapsedSeconds = tDelta / 1000.0;

                            if (sec[0] > 0) {
                                sec[0] = sec[0] - 1;
                                //timeLeft.setText(min[0] + " : " + sec[0]);
                            } else {
                                if (min[0] == 0) { //5 mnt cycle complete
                                    min[0] = 5;
                                    syncCSV();
                                    lastSyncTime = getCurrentTime();
                                } else {
                                    min[0]--;
                                    sec[0] = 59;
                                }

                            }
                            if(sec[0]<=9)
                                timeLeft.setText("0"+min[0] + " : 0" + sec[0]);
                            else
                                timeLeft.setText("0"+min[0] + " : " + sec[0]);
                        }
                    });
                    i++;
                    Thread.sleep(1000);
                }
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

    }
}


