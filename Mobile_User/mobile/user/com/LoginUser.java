package mobile.user.com;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;

public class LoginUser {
	
	

    public static void main(String[] args) {

        try {
            // ðŸ”¹ IMPORTANT: Cookie manager to maintain session
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            HttpClient client = HttpClient.newBuilder()
                    .cookieHandler(cookieManager)
                    .build();

            // =========================
            // 1ï¸?âƒ£ LOGIN
            // =========================
            String loginJson = "{"
                    + "\"email\":\"alice@example.com\","
                    + "\"password\":\"Test@123!\""
                    + "}";

            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost/Mobile_User/api/login_api.php"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(loginJson))
                    .build();

            HttpResponse<String> loginResponse =
                    client.send(loginRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("ðŸ”? LOGIN RESPONSE:");
            System.out.println(
                    new GsonBuilder().setPrettyPrinting().create()
                            .toJson(JsonParser.parseString(loginResponse.body()))
            );

            // =========================
            // â?³ WAIT 6 SECONDS
            // =========================
            System.out.println("\nâ?³ Waiting 6 seconds before fetching products...");
            Thread.sleep(6000);

            // =========================
            // 2ï¸?âƒ£ FETCH PRODUCTS
            // =========================
            HttpRequest productRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost/Mobile_User/api/products_api.php"))
                    .GET()
                    .build();

            HttpResponse<String> productResponse =
                    client.send(productRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("\nðŸ›’ PRODUCTS RESPONSE:");
            System.out.println(
                    new GsonBuilder().setPrettyPrinting().create()
                            .toJson(JsonParser.parseString(productResponse.body()))
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }	
	
	
	
//
//    public static void main(String[] args) {
//
//        try {
//            HttpClient client = HttpClient.newHttpClient();
//
//            // JSON data for login
//            String json = "{"
//                    + "\"email\":\"alice@example.com\","
//                    + "\"password\":\"Test@123!\""
//                    + "}";
//
//            // Build POST request
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("http://localhost/Mobile_User/api/login_api.php"))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(json))
//                    .build();
//
//            // Send request
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            // Parse and pretty-print JSON response
//            JsonElement jsonElement = JsonParser.parseString(response.body());
//            System.out.println("Login API Response:");
//            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement));
//
//            // Optional: Get user ID
//            if (jsonElement.getAsJsonObject().get("success").getAsBoolean()) {
//                int userId = jsonElement.getAsJsonObject()
//                        .getAsJsonObject("data")
//                        .get("user_id")
//                        .getAsInt();
//                System.out.println("Logged in User ID: " + userId);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}
