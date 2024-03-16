package com.example.snakegameb;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Omar_Bazarbek_230107140 extends Application {
    static final int width = 800;
    static final int height = 625;
    static final int unit_size = 25;
    static final int game_units = (height * width) / unit_size;
    private int[] x = new int[game_units];
    private int[] y = new int[game_units];
    int bodyInitially = 3, applesEaten, bestScore, pointX, pointY;
    char direction = 'I';
    boolean running = true;
    boolean appleEatenFrame = false;
    Random randomizer = new Random();
    GraphicsContext gContext;
    long speed = 1000000000 / 9;
    long speedToAdd = 10000000 / 2;
    boolean isPressed = false;
    boolean paused = false;
    boolean speedIncreased = false;
    private Scene scene;
    private boolean lose = false;
    private boolean wPressed = false;
    private boolean aPressed = false;
    private boolean sPressed = false;
    private boolean dPressed = false;
    private final List<Wall> walls = new ArrayList<>();

    private void check() throws FileNotFoundException{
        Pane root = new Pane();
        Canvas canvas = new Canvas(width, height);
        gContext = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        newApple();

        root.setBackground(Background.fill(Color.BLACK));


        scene = new Scene(root, width, height);
        scene.setOnKeyPressed(e -> KeyStrokes(e.getCode()));
        scene.setOnKeyReleased(e -> handleKeyReleased(e.getCode()));
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException{
        check();
        primaryStage.setTitle("Snake_Game byOmar");
        primaryStage.setScene(scene);
        restartGame();
        primaryStage.show();
    }

    private void KeyStrokes(KeyCode code) {
        if (!isPressed) {
            switch (code) {
                case W, UP -> {
                    if (direction != 'D' && !sPressed) {
                        direction = 'U';
                        wPressed = true;
                    }
                }
                case S, DOWN -> {
                    if (direction != 'U' && !wPressed) {
                        direction = 'D';
                        sPressed = true;
                    }
                }
                case A, LEFT -> {
                    if (direction != 'R' && !dPressed) {
                        direction = 'L';
                        aPressed = true;
                    }
                }
                case D, RIGHT -> {
                    if (direction != 'L' && !aPressed) {
                        direction = 'R';
                        dPressed = true;
                    }
                }
                case R -> {
                    if (lose) {
                        restartGame();
                    }
                }
                case P -> {
                    paused = !paused;
                    if (!paused && !speedIncreased) {
                        speed -= speedToAdd;
                        speedIncreased = true;
                    }
                }
            }
            isPressed = true;
        }
    }

    private void handleKeyReleased(KeyCode code) {
        switch (code) {
            case W, UP -> wPressed = false;
            case S, DOWN -> sPressed = false;
            case A, LEFT -> aPressed = false;
            case D, RIGHT -> dPressed = false;
        }
        isPressed = false;
    }

    private void restartGame() {
        running = true;
        direction = 'I';
        appleEatenFrame = false;
        speed = 1000000000 / 8;
        bodyInitially = 3;
        applesEaten = 0;
        x = new int[game_units];
        y = new int[game_units];
        lose = false;
        newApple();
        Animation();
    }

    private void Animation() {
        new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= speed && !paused) {
                    if (running) {
                        move();
                        checkApple();
                        checkCollisions();
                        try {
                            draw();
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        isPressed = false;
                        if (appleEatenFrame) {
                            speed -= speedToAdd;
                            appleEatenFrame = false;
                        }
                    } else {
                        stop();
                    }
                    lastUpdate = now;
                }
            }
        }.start();
    }

    private void draw() throws FileNotFoundException {
        gContext.clearRect(0, 0, width, height);
        double red = Math.random();
        double green = Math.random();
        double blue = Math.random();
        gContext.fillOval(pointX, pointY, unit_size, unit_size);
        if (running) {
            for (int i = 0; i < bodyInitially; i++) {
                if (i == 0) {
                    gContext.setFill(Color.DARKGREEN);
                    gContext.fillRect(x[i], y[i], unit_size, unit_size);
                } else {
                    gContext.setFill(new Color(red, green, blue, 1.0));
                    gContext.fillRect(x[i], y[i], unit_size, unit_size);
                }
            }
            gContext.setFill(Color.RED);
            for (Wall wall : walls) {
                gContext.fillRect(wall.getX(), wall.getY(), unit_size, unit_size);
            }
        } else {
            gameOver(gContext);
        }
        String button_pause = "P";
        gContext.setFill(Color.YELLOW);
        gContext.setFont(Font.font("Times New Roman Bold", FontWeight.BOLD, 15));
        gContext.fillText("Eaten Foods: " + applesEaten, 10, 20);
        gContext.fillText("Best Score: " + bestScore, 695, 20);
        gContext.fillText("Pause-" + button_pause, 710, 40);
        gContext.setFill(Color.YELLOW);
        PowerUp[] powerUps = new PowerUp[0];
        for (PowerUp powerUp : powerUps) {
            gContext.setFill(powerUp.getColor());
            gContext.fillRect(powerUp.getX(), powerUp.getY(), unit_size, unit_size);
        }
    }

    private void move() {
        for (int i = bodyInitially; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        if (wPressed) {
            direction = 'U';
        } else if (sPressed) {
            direction = 'D';
        } else if (aPressed) {
            direction = 'L';
        } else if (dPressed) {
            direction = 'R';
        }
        switch (direction) {
            case 'U' -> y[0] -= unit_size;
            case 'D' -> y[0] += unit_size;
            case 'L' -> x[0] -= unit_size;
            case 'R' -> x[0] += unit_size;
        }
        isPressed = true;
        if (x[0] < 0) {
            x[0] = width - unit_size;
        } else if (x[0] >= width) {
            x[0] = 0;
        } else if (y[0] < 0) {
            y[0] = height - unit_size;
        } else if (y[0] >= height) {
            y[0] = 0;
        }
    }

    private void checkCollisions() {
        if (direction == 'I') return;
        for (int i = bodyInitially; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                if (applesEaten > bestScore) {
                    bestScore = applesEaten;
                }
                break;
            }
        }
        for (Wall wall : walls) {
            if (x[0] == wall.getX() && y[0] == wall.getY()) {
                running = false;
                if (applesEaten > bestScore) {
                    bestScore = applesEaten;
                }
                break;
            }
        }
    }

    private void checkApple() {
        if (x[0] == pointX && y[0] == pointY) {
            applesEaten++;
            bodyInitially++;
            newApple();
            appleEatenFrame = true;
        }
    }

    private void newApple() {
        pointX = randomizer.nextInt(width / unit_size) * unit_size;
        pointY = randomizer.nextInt(height / unit_size) * unit_size;
        walls.clear();
        for (int i = 0; i < 8; i++) {
            int wallX = randomizer.nextInt(width / unit_size) * unit_size;
            int wallY = randomizer.nextInt(height / unit_size) * unit_size;
            while ((wallX == pointX && wallY == pointY) || isWallOverlap(wallX, wallY)) {
                wallX = randomizer.nextInt(width / unit_size) * unit_size;
                wallY = randomizer.nextInt(height / unit_size) * unit_size;
            }
            walls.add(new Wall(wallX, wallY));
        }
    }

    private boolean isWallOverlap(int wallX, int wallY) {
        for (Wall wall : walls) {
            if (wall.getX() == wallX && wall.getY() == wallY) {
                return true;
            }
        }
        return false;
    }

    private void gameOver(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 50));
        gc.fillText("Game Over!", (double) width / 2 - 140, (double) height / 2 - 40);

        gc.setFont(new Font("Arial", 30));
        gc.fillText("Score: " + applesEaten, (double) width / 2 - 68, (double) height / 2 + 20);

        gc.setFont(new Font("Arial", 20));
        gc.fillText("Don't give up!", (double) width / 2 - 69, (double) height / 2 + 70);

        gc.setFont(new Font("Arial", 15));
        gc.fillText("Press R to restart", (double) width / 2 - 66, (double) height / 2 + 110);
        lose = true;
    }
}

class Wall {
    private final int x;
    private final int y;

    public Wall(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class PowerUp {
    private int x;
    private int y;
    private Color color;

    public PowerUp(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }
}
