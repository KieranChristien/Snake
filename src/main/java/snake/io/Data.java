package main.java.snake.io;

import java.io.*;

public final class Data {
    private static final String FILE_NAME = "src/main/resources/data/scores.txt";

    private Data() {}

    public static void saveHighScore(int highScore) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(FILE_NAME))) {
            out.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.out.println("Could not save scores: " + e.getMessage());
        }
    }

    public static int loadHighScore() {
        int highScore = 0;
        File file = new File(FILE_NAME);

        if (!file.exists()) return highScore;

        try (BufferedReader in = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = in.readLine()) != null) {
                highScore = Integer.parseInt(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Could not load scores: " + e.getMessage());
        }

        return highScore;
    }
}
