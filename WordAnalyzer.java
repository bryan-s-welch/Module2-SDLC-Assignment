import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.*;
import java.util.Map;

public class WordAnalyzer extends Application {

    Label outputLabel;
    Map<String, Integer> wordCount;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();

        Text t = new Text();
        t.setText("\nPress the Analyze button to see the top 20 words used in the poem 'The Raven' by Edgar Allen Poe");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        root.setTop(t);

        Button analyzeButton = new Button("Analyze");
        analyzeButton.setOnAction(e -> {
            try {
                analyze();
            } catch (Exception ex) {
                System.out.println("error: exception");
            }
        });
        HBox buttonBox = new HBox(analyzeButton);
        buttonBox.setAlignment(Pos.CENTER);
        root.setBottom(buttonBox);
        BorderPane.setMargin(buttonBox, new Insets(10));

        outputLabel = new Label();
        root.setCenter(outputLabel);
        BorderPane.setAlignment(outputLabel, Pos.CENTER);
        outputLabel.setFont(Font.font("Arial",14));

        Scene scene = new Scene(root, 665, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Text Analyzer");
        primaryStage.show();
    }

    public Map<String, Integer> getWordCount() {
        return wordCount;
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/word_occurrences?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "pass";

        return DriverManager.getConnection(url, user, password);
    }

    void resetWordCount() throws SQLException {
        try (Connection connection = getConnection()) {
            PreparedStatement resetStmt = connection.prepareStatement("UPDATE word SET count = 0");
            resetStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    void analyze() throws Exception {
        resetWordCount();
        String url = "https://www.gutenberg.org/files/1065/1065-h/1065-h.htm";

        Document document = Jsoup.connect(url).get();
        String h1 = document.select("h1").text();
        String h2 = document.select("h2").text();
        String chapter = document.select("div.chapter").text();

        String allText = h1 + h2 + chapter;
        String[] words = allText.split("\\s+");

        try (Connection connection = getConnection()) {
            for (String word : words) {
                word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase();
                if (!word.isEmpty()) {

                    PreparedStatement checkStmt = connection.prepareStatement("SELECT count, id FROM word WHERE word = ?");
                    checkStmt.setString(1, word);
                    ResultSet resultSet = checkStmt.executeQuery();

                    if (resultSet.next()) {

                        int count = resultSet.getInt("count");
                        int id = resultSet.getInt("id");
                        PreparedStatement updateStmt = connection.prepareStatement("UPDATE word SET count = ? WHERE id = ?");
                        updateStmt.setInt(1, count + 1);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                    } else {

                        PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO word (word, count) VALUES (?, 1)");
                        insertStmt.setString(1, word);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Connection connection = getConnection()) {
            PreparedStatement topWordsStmt = connection.prepareStatement("SELECT word, count FROM word ORDER BY count DESC LIMIT 20");
            ResultSet resultSet = topWordsStmt.executeQuery();
            StringBuilder sb = new StringBuilder("Top 20 words:\n\n");

            while (resultSet.next()) {
                String word = resultSet.getString("word");
                int count = resultSet.getInt("count");
                sb.append(word).append(": ").append(count).append("\n");
            }

            outputLabel.setText(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}