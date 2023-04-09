import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GameLauncher {
    private JFrame frame;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private ArrayList<Game> games;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameLauncher().createAndShowGUI());
    }

    public void createAndShowGUI() {
        frame = new JFrame("Game Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(mainPanel);

        games = getGamesFromFolder("C:\\Games");
        displayGames(games);

        frame.add(scrollPane);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private ArrayList<Game> getGamesFromFolder(String folderPath) {
        ArrayList<Game> games = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath), "*.exe")) {
            for (Path entry : stream) {
                Game game = new Game(entry.getFileName().toString(), entry.toAbsolutePath().toString());
                games.add(game);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return games;
    }

    private void displayGames(ArrayList<Game> games) {
        for (Game game : games) {
            JButton gameButton = new JButton(game.getName());
            gameButton.addActionListener(e -> launchGame(game));
            mainPanel.add(gameButton);
        }
    }

    private void launchGame(Game game) {
        try {
            Process process = new ProcessBuilder(game.getPath()).start();
            // Update hours played, save to storage (e.g. file, database) if needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Game {
    private String name;
    private String path;
    private float hoursPlayed;

    public Game(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public float getHoursPlayed() {
        return hoursPlayed;
    }

    public void setHoursPlayed(float hoursPlayed) {
        this.hoursPlayed = hoursPlayed;
    }
}
