package com.example.billiard;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;

public class BilliardGame extends Application {
    private static final int WIDTH = 800; // Breite des Fensters
    private static final int HEIGHT = WIDTH / 2; // Höhe des Fensters

    private final PoolTable poolTable;
    private final Ball cueBall;
    private final Ball ball1, ball2, ball3, ball4, ball5, ball6, ball7, ball8, ball9, ball10, ball11, ball12, ball13, ball14, ball15;
    private final Cue cue;

    private boolean isCueSelected = false; // Gibt an, ob der Cue-Stick ausgewählt ist
    private double cueStartX; // Startposition des Cue-Sticks beim Klicken
    private double cueStartY;

    private final ArrayList<Ball> balls;

    public BilliardGame() {

        // Initialisierung der Objekte
        balls = new ArrayList<>();
        poolTable = new PoolTable(WIDTH, HEIGHT);
        cueBall = new Ball((double) WIDTH / 2, (double) HEIGHT / 2, 10, Color.WHITE);
        ball1 = new Ball((double) WIDTH / 3 - 23, (double) HEIGHT / 2 + 11, 10, Color.YELLOW);
        ball2 = new Ball((double) WIDTH / 3 - 23, (double) HEIGHT / 2 - 11, 10, Color.BLUE);
        ball3 = new Ball((double) WIDTH / 3 - 46, (double) HEIGHT / 2 + 22, 10, Color.RED);
        ball4 = new Ball((double) WIDTH / 3 - 46, (double) HEIGHT / 2, 10, Color.PURPLE);
        ball5 = new Ball((double) WIDTH / 3 - 46, (double) HEIGHT / 2 - 22, 10, Color.ORANGE);
        ball6 = new Ball((double) WIDTH / 3 - 69, (double) HEIGHT / 2 + 33, 10, Color.DARKGREEN);
        ball7 = new Ball((double) WIDTH / 3 - 69, (double) HEIGHT / 2 + 11, 10, Color.BROWN);
        ball8 = new Ball((double) WIDTH / 3 - 69, (double) HEIGHT / 2 - 11, 10, Color.BLACK);
        ball9 = new Ball((double) WIDTH / 3 - 69, (double) HEIGHT / 2 - 33, 10, Color.YELLOW);
        ball10 = new Ball((double) WIDTH / 3 - 92, (double) HEIGHT / 2 + 44, 10, Color.BLUE);
        ball11 = new Ball((double) WIDTH / 3 - 92, (double) HEIGHT / 2 + 22, 10, Color.RED);
        ball12 = new Ball((double) WIDTH / 3, (double) HEIGHT / 2, 10, Color.PURPLE);
        ball13 = new Ball((double) WIDTH / 3 - 92, (double) HEIGHT / 2, 10, Color.ORANGE);
        ball14 = new Ball((double) WIDTH / 3 - 92, (double) HEIGHT / 2 - 22, 10, Color.DARKGREEN);
        ball15 = new Ball((double) WIDTH / 3 - 92, (double) HEIGHT / 2 - 44, 10, Color.BROWN);
        balls.add(cueBall);
        balls.add(ball1);
        balls.add(ball2);
        balls.add(ball3);
        balls.add(ball4);
        balls.add(ball5);
        balls.add(ball6);
        balls.add(ball7);
        balls.add(ball8);
        balls.add(ball9);
        balls.add(ball10);
        balls.add(ball11);
        balls.add(ball12);
        balls.add(ball13);
        balls.add(ball14);
        balls.add(ball15);
        cue = new Cue(cueBall.getX(), cueBall.getY());
        // Weitere Initialisierungen können hier erfolgen
    }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Billiard Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // AnimationTimer, um das Spiel zu aktualisieren und zu zeichnen
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(); // Spiellogik aktualisieren
                draw(gc); // Spielobjekte zeichnen
            }
        };
        timer.start();

        // Event Handler für Mausklick

        canvas.setOnMouseClicked(event -> {
            // Wenn der Cue-Stick noch nicht ausgewählt ist und der Benutzer auf den Cue-Stick klickt
            isCueSelected = true;
            cueStartX = event.getX();
            cueStartY = event.getY();
        });

        // Event Handler für Mausbewegung
        canvas.setOnMouseMoved(event -> {
            if (isCueSelected) {
                // Wenn der Benutzer den Cue-Stick ausgewählt hat
                cue.setAngle(getCueAngle(event.getX(), event.getY()));
            }
        });

        // Event Handler für Mausfreigabe
        canvas.setOnMouseReleased(event -> {
            if (isCueSelected) {
                // Wenn der Benutzer den Cue-Stick freigibt
                double cueEndX = event.getX();
                double cueEndY = event.getY();
                double cuePower = calculateCuePower(cueStartX, cueStartY, cueEndX, cueEndY);

                // Setzen der Stoßkraft und Ausrichtung des Cue-Sticks
                cue.setPower(cuePower);
                cue.setAngle(getCueAngle(event.getX(), event.getY()));

                // Kugel stoßen
                cueBall.shoot(cue.getAngle(), cuePower);

                // Zurücksetzen der Cue-Stick-Selektion
                isCueSelected = false;
            }
        });
    }

    private void update() {
        // Kugeln bewegen
        for (Ball ball : balls){
            ball.move();
        }


        // Kollisionsprüfung mit den Wänden des Pool-Tischs
        for (Ball currentBall : balls) {
            if (currentBall.collidesWithWall(poolTable)) {
                currentBall.setVelocity(-currentBall.getDy(), currentBall.getDx());
            }
        }

        // Kollisionserkennung zwischen Kugeln
        for (int i = 0; i < balls.size(); i++) {
            Ball currentBall = balls.get(i);

            for (int j = i + 1; j < balls.size(); j++) {
                Ball otherBall = balls.get(j);
                if (currentBall.collidesWith(otherBall)) {
                    // Kraft und Richtungsübertragung auf den neuen Ball
                    otherBall.setVelocity(currentBall.getDx(), currentBall.getDy());
                    // Richtungsänderung des aktuellen Balls
                    currentBall.setVelocity(-currentBall.getDy(), currentBall.getDx());
                }
            }
        }

        // Kollisionserkennung mit den Taschen
        for (int i = 0; i < balls.size(); i++) {
            Ball currentBall = balls.get(i);
            if (currentBall.collidesWithWall(poolTable)) {
                currentBall.setVelocity(-currentBall.getDy(), currentBall.getDx());
            }
        }

        // Position des Cue aktualisieren
        cue.setX(cueBall.getX());
        cue.setY(cueBall.getY());
    }

    private double getCueAngle(double mouseX, double mouseY) {
        double dx = mouseX - cueBall.getX();
        double dy = mouseY - cueBall.getY();
        return Math.atan2(dy, dx);
    }

    private double calculateCuePower(double startX, double startY, double endX, double endY) {
        double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        // Skalierungsfaktor, um die Mausentfernung in eine geeignete Stoßkraft umzurechnen
        return distance * 0.1; // Skalierungsfaktor 0.1
    }

    private void draw(GraphicsContext gc) {
        // Hintergrund löschen
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        poolTable.draw(gc.getCanvas()); // Pool-Tisch zeichnen
        cue.draw(gc); // Cue zeichnen
        for (Ball ball : balls){ // Bälle zeichnen
            ball.draw(gc);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
