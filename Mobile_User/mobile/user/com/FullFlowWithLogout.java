package mobile.user.com;

import java.net.URI;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

public class FullFlowWithLogout {

    static HttpClient client;

    public static void main(String[] args) {

        try {
            resetSession();

            // 1️⃣ LOGIN → ADD TO CART → LOGOUT
            login();
            Thread.sleep(4000);

            addToCart(3);
            Thread.sleep(4000);
            addToCart(1);
            Thread.sleep(4000);

            logout();
            Thread.sleep(4000);

            // 2️⃣ LOGIN → CHECKOUT → LOGOUT
            resetSession();
            login();
            Thread.sleep(4000);

            checkout("4111111111111111", 1);
            Thread.sleep(4000);

            logout();
            Thread.sleep(4000);

            // 3️⃣ LOGIN → VIEW ORDERS → LOGOUT
            resetSession();
            login();
            Thread.sleep(4000);

            viewOrders();
            Thread.sleep(4000);

            logout();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
    // 🔁 Reset PHP Session
    static void resetSession() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
    }

    // 🔐 LOGIN
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

        System.out.println("\n🔐 LOGIN");
        pretty(response.body());
    }

    // ➕ ADD TO CART
    static void addToCart(int productId) throws Exception {
        String json = "{ \"product_id\": " + productId + " }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/cart_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n➕ ADD TO CART");
        pretty(response.body());
    }

    // 💳 CHECKOUT
    static void checkout(String card, int addressId) throws Exception {
        String json = "{"
                + "\"card_number\":\"" + card + "\","
                + "\"address_id\":" + addressId
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/checkout_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n💳 CHECKOUT");
        pretty(response.body());

        JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();
        if (obj.get("success").getAsBoolean()) {
            System.out.println("📦 Tracking Number: " +
                    obj.get("tracking_number").getAsString());
        }
    }

    // 📦 VIEW ORDERS
    static void viewOrders() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/orders_api.php"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n📦 ORDERS");
        pretty(response.body());
    }

    // 🚪 LOGOUT
    static void logout() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/logout_api.php"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n🚪 LOGOUT");
        pretty(response.body());
    }

    // 🎨 Pretty JSON
    static void pretty(String json) {
        System.out.println(
                new GsonBuilder().setPrettyPrinting().create()
                        .toJson(JsonParser.parseString(json))
        );
    }
}
