import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Map;

public class WordAnalyzerTest extends ApplicationTest {

    private WordAnalyzer analyzer;

    private Label outputLabel;

    @Override
    public void start(Stage stage) throws Exception {
        analyzer = new WordAnalyzer();
        outputLabel = new Label();
        analyzer.start(stage);
        analyzer.outputLabel = outputLabel;
    }

    @Test
    void testAnalyze() throws Exception {
        interact(() -> {
            try {
                analyzer.analyze();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        String output = outputLabel.getText();
        Assertions.assertTrue(output.contains("the:"));
        Assertions.assertTrue(output.contains("and:"));
        Assertions.assertTrue(output.contains("raven:"));

        Map<String, Integer> wordCount = analyzer.getWordCount();
        Assertions.assertNotNull(wordCount);
        Assertions.assertEquals(445, wordCount.size());
    }
}