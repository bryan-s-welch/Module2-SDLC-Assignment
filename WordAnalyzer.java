import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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

    void analyze() throws Exception {
        String url = "https://www.gutenberg.org/files/1065/1065-h/1065-h.htm";

        Document document = Jsoup.connect(url).get();
        String h1 = document.select("h1").text();
        String h2 = document.select("h2").text();
        String chapter = document.select("div.chapter").text();

        String allText = h1 + h2 + chapter;
        String[] words = allText.split("\\s+");

        wordCount = new HashMap<>();
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase();
            if (!word.isEmpty()) {
                wordCount.put(word,  wordCount.getOrDefault(word, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordCount.entrySet());
        sortedWords.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        StringBuilder sb = new StringBuilder("Top 20 words:\n\n");

        for (int i = 0; i < 20 && i < sortedWords.size(); i++) {
            Map.Entry<String, Integer> entry = sortedWords.get(i);
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
//        System.out.println(sb);

        outputLabel.setText(sb.toString());
    }


}