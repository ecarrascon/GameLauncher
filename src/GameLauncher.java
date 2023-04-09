import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class GameLauncher {
    private JFrame frame;
    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private ArrayList<Game> games;

    private static final String DATA_FILE = "game_data.txt";

    private Map<String, Long> gameData;

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
        gameData = loadGameData(DATA_FILE);
        displayGames(games);

        frame.add(scrollPane);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private ArrayList<Game> getGamesFromFolder(String folderPath) {
        //Scan the folder and create Game objects for each executable (Execpt some words like unins or unity)
        ArrayList<Game> games = new ArrayList<>();
        try {
            Files.walk(Paths.get(folderPath), 2)
                    .filter(path -> path.toString().endsWith(".exe"))
                    .filter(path -> {
                        String filename = path.getFileName().toString().toLowerCase();
                        return !filename.startsWith("unins") && !filename.startsWith("unity");
                    })
                    .forEach(path -> {
                        Game game = new Game(path.getFileName().toString(), path.toAbsolutePath().toString());
                        games.add(game);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return games;
    }


    private void displayGames(ArrayList<Game> games) {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();

        for (Game game : games) {
            //Get the game's icon
            Icon gameIcon = fileSystemView.getSystemIcon(new File(game.getPath()));



            //Create a button with the game's .exe icon and name
            JButton gameButton = new JButton(game.getName(), gameIcon);
            gameButton.addActionListener(e -> launchGame(game));
            gameButton.setHorizontalTextPosition(JButton.CENTER);
            gameButton.setVerticalTextPosition(JButton.BOTTOM);
            mainPanel.add(gameButton);

            //Display the number of hours played
            String hoursPlayed = String.format("%.2f", gameData.getOrDefault(game.getPath(), 0L) / 60.0);
            gameButton.setText(game.getName() + " - " + hoursPlayed + " hours");
        }
    }


    private Map<String, Long> loadGameData(String filename) {
        Map<String, Long> gameData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                String gamePath = parts[0].trim();
                long hoursPlayed = Long.parseLong(parts[1].trim());
                gameData.put(gamePath, hoursPlayed);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Data file not found, initializing an empty database.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gameData;
    }

    private void saveGameData(String filename, Map<String, Long> gameData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Long> entry : gameData.entrySet()) {
                writer.write(entry.getKey() + ";" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchGame(Game game) {
        try {
            Process process = new ProcessBuilder(game.getPath()).start();
            LocalDateTime startTime = LocalDateTime.now();
            process.waitFor();
            LocalDateTime endTime = LocalDateTime.now();

            Duration duration = Duration.between(startTime, endTime);
            long hoursPlayed = gameData.getOrDefault(game.getPath(), 0L);
            hoursPlayed += duration.toMillis() / (1000 * 60 * 60);
            gameData.put(game.getPath(), hoursPlayed);

            saveGameData(DATA_FILE, gameData);
        } catch (IOException | InterruptedException e) {
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
