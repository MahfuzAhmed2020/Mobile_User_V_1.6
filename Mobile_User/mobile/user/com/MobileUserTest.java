package mobile.user.com;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;

public class MobileUserTest {

    private static final String BASE_URL = "http://localhost/Mobile_User/api/";

    public static void main(String[] args) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // 1ï¸?âƒ£ Login
            System.out.println("Logging in...");
            String loginJson = "{\"email\":\"M@gmail.com\",\"password\":\"Test123!\"}";
            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "login_api.php"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(loginJson))
                    .build();

            HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());
            JsonObject loginObj = JsonParser.parseString(loginResponse.body()).getAsJsonObject();
            System.out.println("Login Response:");
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(loginObj));

            if (!loginObj.get("success").getAsBoolean()) {
                System.out.println("Login failed. Exiting.");
                return;
            }

            int userId = loginObj.getAsJsonObject("user").get("id").getAsInt();

            // Pause 4 seconds
            Thread.sleep(4000);

            // 2ï¸?âƒ£ Fetch profile (just for demonstration, normally profile API)
            System.out.println("\nFetching user profile...");
            HttpRequest profileReq = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "profile_api.php?user_id=" + userId))
                    .GET()
                    .build();

            HttpResponse<String> profileRes = client.send(profileReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("Profile Response:");
            System.out.println(new GsonBuilder().setPrettyPrinting().create()
                    .toJson(JsonParser.parseString(profileRes.body())));

            Thread.sleep(4000);

            // 3ï¸?âƒ£ Fetch products
            System.out.println("\nFetching products under $100...");
            HttpRequest productsReq = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "products_api.php"))
                    .GET()
                    .build();

            HttpResponse<String> productsRes = client.send(productsReq, HttpResponse.BodyHandlers.ofString());
            JsonObject productsObj = JsonParser.parseString(productsRes.body()).getAsJsonObject();
            System.out.println("Products Response:");
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(productsObj));

            Thread.sleep(4000);

            // 4ï¸?âƒ£ Add first product to cart
            JsonArray productsArr = productsObj.getAsJsonArray("data");
            if (productsArr.size() > 0) {
                int productId = productsArr.get(0).getAsJsonObject().get("id").getAsInt();
                System.out.println("\nAdding product " + productId + " to cart...");

                String addCartJson = "{\"product_id\":" + productId + "}";
                HttpRequest addCartReq = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "cart_api.php"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(addCartJson))
                        .build();

                HttpResponse<String> addCartRes = client.send(addCartReq, HttpResponse.BodyHandlers.ofString());
                System.out.println("Add to Cart Response:");
                System.out.println(new GsonBuilder().setPrettyPrinting().create()
                        .toJson(JsonParser.parseString(addCartRes.body())));
            }

            Thread.sleep(4000);

            // 5ï¸?âƒ£ Checkout
            System.out.println("\nChecking out...");
            String checkoutJson = "{\"card_number\":\"4111111111111111\",\"address_id\":1}";
            HttpRequest checkoutReq = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "checkout_api.php"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(checkoutJson))
                    .build();

            HttpResponse<String> checkoutRes = client.send(checkoutReq, HttpResponse.BodyHandlers.ofString());
            JsonObject checkoutObj = JsonParser.parseString(checkoutRes.body()).getAsJsonObject();
            System.out.println("Checkout Response:");
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(checkoutObj));

            Thread.sleep(4000);

            // 6ï¸?âƒ£ Verify Orders
            System.out.println("\nVerifying orders...");
            HttpRequest ordersReq = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "orders_api.php"))
                    .GET()
                    .build();

            HttpResponse<String> ordersRes = client.send(ordersReq, HttpResponse.BodyHandlers.ofString());
            System.out.println("Orders Response:");
            System.out.println(new GsonBuilder().setPrettyPrinting().create()
                    .toJson(JsonParser.parseString(ordersRes.body())));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
