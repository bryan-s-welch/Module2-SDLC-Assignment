package CEN3024C;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class Analyzer {
	public static void main(String[] args) throws IOException {
		URL url = new URL("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm");
		Scanner sc = new Scanner (url.openStream());
		StringBuffer sb = new StringBuffer();
		while(sc.hasNext()) {
			sb.append(sc.next());
			System.out.println(sc.next());
		}
	}
}
