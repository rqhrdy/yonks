import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CreateMatcherExample {

    public static void main(String[] args) throws IOException {

        URL url = new URL("https://www.cbsnews.com/news/andrew-gillum-instagram-video-addiction-recovery-miami-hotel/");
        readAny(url, "kevnbois.txt");
        // Get the input stream through URL Connection

    }

    public static void readCNN(URL url, String filename) throws IOException {
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        Document html = Jsoup.parse(stringBuilder.toString());
        String articleBody="";
        List<String> paragraphs = html.getElementsByClass("zn-body__paragraph").eachText();
        for(String paragraph:paragraphs)
            articleBody = articleBody.concat("\t"+paragraph+"\n\n");
        File myObj = new File(filename);
        myObj.createNewFile();
        FileWriter myWriter = new FileWriter(filename);
        myWriter.write(articleBody);
        myWriter.close();
    }

    public static void readAny(URL url, String filename) throws IOException {
        URLConnection con = url.openConnection();
        InputStream is =con.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }
        Document html = Jsoup.parse(stringBuilder.toString());
        String articleBody="";
        List<String> paragraphs = html.getElementsByClass("zn-body__paragraph").eachText();
        //articleBody = articleBody.concat(html.body().text());
        Elements elements = html.getElementsByTag("p");
        paragraphs = elements.eachText();
        for(String paragraph:paragraphs)
            articleBody = articleBody.concat("\t"+paragraph+"\n\n");
        File myObj = new File(filename);
        myObj.createNewFile();
        FileWriter myWriter = new FileWriter(filename);
        myWriter.write(articleBody);
        myWriter.close();
    }
}