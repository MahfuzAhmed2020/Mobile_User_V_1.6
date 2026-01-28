package mobile.user.com;

import java.net.URI;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class CartFlowTest {

    static HttpClient client;

    public static void main(String[] args) {

        try {
            // 🔹 Maintain PHP session
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            client = HttpClient.newBuilder()
                    .cookieHandler(cookieManager)
                    .build();

            // =========================
            // 1️⃣ LOGIN
            // =========================
            login();

            Thread.sleep(7000);

            // =========================
            // 2️⃣ ADD PRODUCT 1
            // =========================
            addToCart(1);
            Thread.sleep(7000);

            // =========================
            // 3️⃣ ADD PRODUCT 2
            // =========================
            addToCart(2);
            Thread.sleep(7000);

            // =========================
            // 4️⃣ VIEW CART
            // =========================
            viewCart();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔐 LOGIN METHOD
    static void login() throws Exception {
        String json = "{"
                + "\"email\":\"alice@example.com\","
                + "\"password\":\"Test@123!\""
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/login_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n🔐 LOGIN RESPONSE:");
        prettyPrint(response.body());
    }

    // ➕ ADD PRODUCT TO CART
    static void addToCart(int productId) throws Exception {

        String json = "{ \"product_id\": " + productId + " }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/cart_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n➕ ADD TO CART (Product ID: " + productId + "):");
        prettyPrint(response.body());
    }

    // 🛒 VIEW CART
    static void viewCart() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/cart_api.php"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n🛒 CART CONTENT:");
        prettyPrint(response.body());
    }

    // 🎨 PRETTY PRINT JSON
    static void prettyPrint(String json) {
        System.out.println(
                new GsonBuilder().setPrettyPrinting().create()
                        .toJson(JsonParser.parseString(json))
        );
    }
}
