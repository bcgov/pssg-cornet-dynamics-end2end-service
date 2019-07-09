package CTD_E2E;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class chart extends Application {
    final int WINDOW_SIZE = 10;
    private ScheduledExecutorService scheduledExecutorService;
    chartinp ch = new chartinp();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("JavaFX Realtime Chart Demo");

        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis(); // we are gonna plot against time
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time/s");
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel("Value");
        yAxis.setAnimated(false); // axis animations are removed

        //creating the line chart with two axis created above
        final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Realtime JavaFX Charts");
        lineChart.setAnimated(false); // disable animations

        //defining a series to display data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Data Series");
        
        LinkedHashSet<String> user_number = ch.getfirstaxis();
		Iterator<String> it = user_number.iterator();

		LinkedHashSet<Integer> timediff = ch.gety();
		Iterator<Integer> diff = timediff.iterator();

        // add series to chart
        lineChart.getData().add(series);
        lineChart.applyCss();
        lineChart.layout();
        
        // setup scene
        Scene scene = new Scene(lineChart, 800, 600);
        primaryStage.setScene(scene);

        // show the stage
        primaryStage.show();

        // this is used to display time in HH:mm:ss format
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        

        // setup a scheduled executor to periodically put data into the chart
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        
        WritableImage img = new WritableImage(1000, 700);
		File file = new File(System.getProperty("user.dir") + "/chart/saved.png");

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10
        	
           
    	    
        	String random="start";
        	if(!it.hasNext())
        	{
            random = "stop";
        	}
            // Update the chart
        	if(random.equals("stop"))
        	{
        		scheduledExecutorService.shutdownNow();
        	}
        	else
        	{
            Platform.runLater(() -> {
                // get current time
            	
               // put random number with current time
                series.getData().add(new XYChart.Data<>(it.next(), diff.next()));

                if (series.getData().size() > WINDOW_SIZE)
                    series.getData().remove(0);
                
                
        		scene.snapshot(img);
        		try {
					ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

            });}
        }, 0, 1, TimeUnit.SECONDS);
        
        PauseTransition delay = new PauseTransition(Duration.seconds(20));
		delay.setOnFinished(event -> primaryStage.close());
		delay.play();


    }

    @Override
    public void stop() throws Exception {
        super.stop();
        scheduledExecutorService.shutdownNow();
    }
}

