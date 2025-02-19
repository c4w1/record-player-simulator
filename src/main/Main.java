/*
 *
 *
 */
package main;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.nio.file.Path;

public class Main extends Application {

    public static StackPane getVinylRecord() {
        var vinylRecord = new Circle(200, Color.BLACK);

        var vinylLabel = new Circle(50);
        var imagePath = Path.of("data/DemonDays/sideB/label.jpg").toAbsolutePath().toString();
        var labelImage = new Image("file:" + imagePath);
        vinylLabel.setFill(new ImagePattern(labelImage));

        var radialGradient = new RadialGradient(
                0.0, 0.0, 0.5, 0.5, 1.0, true,
                CycleMethod.REFLECT,
                // This adds a reflective highlight to the record.
                new Stop (0.0, Color.WHITE.deriveColor(0, 0, 0.3, 1)),
                // This adds shadow towards the edges.
                new Stop(0.8, Color.BLACK)
        );
        vinylRecord.setFill(radialGradient);

        var vinylGrooves = new StackPane();
        // It's supposed to look like the grooves on a record, but it doesn't really right now. :(
        for (int i = 180; i > 50; i -= 10) {
            var groove = new Circle(i, Color.TRANSPARENT);
            groove.setStroke(Color.DARKGRAY);
            groove.setStrokeWidth(0.5);
            vinylGrooves.getChildren().add(groove);
        }

        return new StackPane(vinylRecord, vinylGrooves, vinylLabel);
    }
    
    @Override
    public void start(Stage primaryStage) {
        StackPane vinylRecord = getVinylRecord();
        
        // Now, we set up music.
        String audioPath = Path.of("data/DemonDays/sideB/01-feel-good-inc.mp3").toUri().toString();

        var media = new Media(audioPath);
        var mediaPlayer = new MediaPlayer(media);

        // This next thing rotates the record while the music plays.
        // It doesn't look like anything right now because the record is
        // symmetrical on every axis, but I don't care.
        var vinylRotate = new RotateTransition(Duration.seconds(5.0), vinylRecord);
        vinylRotate.setByAngle(360.0);
        vinylRotate.setCycleCount(Animation.INDEFINITE);
        vinylRotate.setInterpolator(Interpolator.LINEAR);

        // I'm pretty sure real records wobble. Maybe my records just suck.
        var vinylWobble = new ScaleTransition(Duration.seconds(0.5), vinylRecord);
        vinylWobble.setFromX(1.0);
        vinylWobble.setToX(1.005);
        vinylWobble.setFromY(1.0);
        vinylWobble.setToY(0.995);
        vinylWobble.setCycleCount(Animation.INDEFINITE);
        vinylWobble.setAutoReverse(true);

        var play = new Button("Play");
        play.setOnAction(_ -> {
            mediaPlayer.play();
            vinylRotate.play();
            vinylWobble.play();
        });

        var pause = new Button("Pause");
        pause.setOnAction(_ -> {
            mediaPlayer.pause();
            vinylRotate.pause();
            vinylWobble.pause();
        });

        var stop = new Button("Stop");
        stop.setOnAction(_ -> {
            mediaPlayer.stop();
            vinylRotate.stop();
            vinylWobble.stop();
        });

        var volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setMaxWidth(300);
        volumeSlider.valueProperty().addListener((_, _, newVal) -> {
            mediaPlayer.setVolume(newVal.doubleValue());
        });

        var progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        mediaPlayer.currentTimeProperty().addListener((_, _, newTime) -> {
            progressBar.setProgress(newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds());
        });

        var buttonBox = new HBox(10, play, pause, stop);
        buttonBox.setAlignment(Pos.CENTER);

        var miscBox = new VBox(10, progressBar, volumeSlider);
        miscBox.setAlignment(Pos.CENTER);

        var layout = new VBox(20, vinylRecord, miscBox, buttonBox);

        layout.setAlignment(Pos.CENTER);



        var scene = new Scene(layout, 640, 480);
        primaryStage.setTitle("Vinyl Record");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
