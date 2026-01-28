package com.osa.jdbcs;

import java.net.URI;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

public class CheckoutAndVerifyOrder {

    static HttpClient client;

    public static void main(String[] args) {

        try {
            // 🔐 Maintain PHP Session
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            client = HttpClient.newBuilder()
                    .cookieHandler(cookieManager)
                    .build();

            // 1️⃣ LOGIN
            login();
            Thread.sleep(6000);

            // 2️⃣ ADD PRODUCT TO CART
            addToCart(1);
            Thread.sleep(6000);

            // 3️⃣ CHECKOUT (Card + Address)
            checkout(
                    "4111111111111111", // card_number
                    1                  // address_id
            );
            Thread.sleep(6000);

            // 4️⃣ VIEW ORDERS
            viewOrders();

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




    // 🔐 LOGIN
    static void login() throws Exception {
        String json = "{"
                + "\"email\":\"YES@GMAIL.com\","
                + "\"password\":\"Test123!\""
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/login_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n🔐 LOGIN:");
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

        System.out.println("\n➕ ADD TO CART:");
        pretty(response.body());
    }

    // 💳 CHECKOUT
    static void checkout(String cardNumber, int addressId) throws Exception {

        String json = "{"
                + "\"card_number\":\"" + cardNumber + "\","
                + "\"address_id\":" + addressId
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/checkout_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n💳 CHECKOUT:");
        pretty(response.body());

        // 🔎 Extract Tracking Number
        JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();
        if (obj.get("success").getAsBoolean()) {
            System.out.println("📦 Tracking Number: "
                    + obj.get("tracking_number").getAsString());
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

        System.out.println("\n📦 ORDERS:");
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
