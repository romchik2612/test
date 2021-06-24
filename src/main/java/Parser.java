import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.tools.FileObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Parser {
    private static int REQUEST_COUNT=0; // Count of HTTP sequests
    private static Document getPage() throws IOException { // get main page
        String url ="https://www.aboutyou.de/maenner/bekleidung"; // URL
        REQUEST_COUNT++;
        Document page = Jsoup.connect(url).get(); // get JSOUP document by url
        return page; // return 
    }
    private static void secondTask() throws IOException {
        String url = "https://www.aboutyou.de/p/indicode-jeans/shorts-conor-3835398"; // product url
        Document page = Jsoup.connect(url).get(); // get JSOUP dfocument by url
        JSONArray jsonArray = new JSONArray(); // create JSON Array
        Elements sizes_web = page.select("div.sc-1oa7xla-2"); // search sizes by class name and get them
        Elements colors_web = page.select("span.jlvxcb-1"); // search colors by class name and get them
        for(var size:sizes_web){
            if(size.hasAttr("disabled")) continue; // search not disabled sizes
            for(var color:colors_web){
                JSONObject jsonObject = new JSONObject(); // new JSON Object
                jsonObject.put("color",color.text()); // add color
                jsonObject.put("size",size.text()); // add size
                jsonArray.put(jsonObject); // put combination size and color to array
            }
        }
        try (FileWriter file = new FileWriter("color_size.json")) { // create file
            file.write(String.valueOf(jsonArray)); // put value to file
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void parseToJSON() throws IOException {
        Element div = getPage().select("div.sc-19tq43e-1.cYbXBI").first(); // get main page
        Elements products_web = div.select("a.sc-1qheze-0"); // search products by class name
        JSONArray jsonArray = new JSONArray(); // create new JSON Array
        for(var product_web:products_web){ // for every product
            JSONObject jsonObject = new JSONObject(); // create product array
            String url = "https://www.aboutyou.de" + product_web.attr("href"); // create product link
            REQUEST_COUNT++; // add request count
            Document page = Jsoup.connect(url).get(); // get new product page
            jsonObject.put("id",Integer.parseInt(product_web.attr("id"))); // search id attribute and put it to json object
            jsonObject.put("name",page.select("div.iay39c-1").first().text()); // search name on product page and put it to json object
            jsonObject.put("brand",product_web.select("p.sc-1gv4rhx-2").text()); // search brand and put in
            jsonObject.put("price",product_web.select("span.sc-1kqkfaq-0").text()); // search price and put in
            Elements colors_web = product_web.select("li.sc-1erb38y-0"); // find colors elements
            ArrayList<String> colors = new ArrayList<>(); // create new array
            for(var color:colors_web){ // for every color in colors_web
                colors.add(color.attr("color")); // get hex code and put it tp array
            }
            jsonObject.put("colors",colors); // put colors to json
            jsonArray.put(jsonObject); // put json object to array
        }
        try (FileWriter file = new FileWriter("products.json")) { // create new file
            file.write(String.valueOf(jsonArray));  // write json array to file
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Amount of extracted products: %s",products_web.size()); // sout number of products
        System.out.println();
        System.out.printf("Amount of triggered HTTP requests: %s", REQUEST_COUNT); // sout number of requests
    }

    public static void main(String[] args) throws IOException {
        parseToJSON();
        secondTask();
    }

}
