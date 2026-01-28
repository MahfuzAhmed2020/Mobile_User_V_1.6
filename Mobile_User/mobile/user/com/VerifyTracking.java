package mobile.user.com;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;

public class VerifyTracking {

    private static final String ORDERS_URL = "http://localhost/Mobile_User/api/orders_api.php";

    public static void main(String[] args) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ORDERS_URL))
                    .GET()
                    .build();
            /*{
            "email": "tomtom@gmail.com",
            "password": "Test123!"
        }
        //shah@gmail.com //new@gmail.com tomtom@gmail.com  rr@gmail.com Test123!
        // aaaa@gmail.com bills@gmail.com  yes@gmail.com
        // M@gmail.com

        // // win@gmail.com TestUser@example.com  john@example.com  
         *  // pp@gmail.com //jj@gmail.com alice@example.com a@gmail.com  "Test@123
         */
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Pretty-print JSON
            JsonElement je = JsonParser.parseString(response.body());
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(je));

            // Optionally, extract first order tracking number
            JsonElement firstTracking = JsonParser.parseString(response.body())
                    .getAsJsonObject()
                    .getAsJsonArray("data")
                    .get(0)
                    .getAsJsonObject()
                    .get("tracking_number");
           
            System.out.println("First order tracking number: " + firstTracking.getAsString());
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
