import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
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
        }
    }


    private void launchGame(Game game) {
        try {
            Process process = new ProcessBuilder(game.getPath()).start();
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
