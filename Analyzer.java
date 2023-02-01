package CEN3024C;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Analyzer {
    public static void main(String[] args) throws Exception {
    	Document document = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();
    	String h1 = document.select("h1").text();
    	String h2 = document.select("h2").text();
    	String chapter = document.select("div.chapter").text();
    	
    	String allText = h1 + h2 + chapter;
    	String[] words = allText.split("\\s+");
    	
    	Map<String, Integer> wordCount = new HashMap<>();
    	for (String word : words) {
    		word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    		if (!word.isEmpty()) {
    			wordCount.put(word,  wordCount.getOrDefault(word, 0) + 1);
    		}
    	}
    	
    	List<Map.Entry<String, Integer>> sortedWords = new ArrayList<>(wordCount.entrySet());
    	sortedWords.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
    	
    	System.out.println("Top 20 words: ");
    	for (int i = 0; i < 20 && i < sortedWords.size(); i++) {
    		Map.Entry<String, Integer> entry = sortedWords.get(i);
    		System.out.println(entry.getKey() + ": " + entry.getValue());
    	}
    }
}

