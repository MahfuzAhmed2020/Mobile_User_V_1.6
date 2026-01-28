package mobile.user.com;

import java.net.URI;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OrderFlowTest_Without_Log {

    static HttpClient client;
    static String trackingNumber;

    @BeforeClass
    public void setup() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
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
    
    
    
    /* =====================
       1️⃣ LOGIN TEST
    ===================== */
    @Test(priority = 1)
    public void testLogin() throws Exception {

        String json = "{"
                + "\"email\":\"rr@GMAIL.com\","
                + "\"password\":\"Test123!\""
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/login_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> res =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();

        Assert.assertNotNull(res.body(), "Login response is null");
        Assert.assertTrue(obj.get("success").getAsBoolean(), "Login failed");
    }

    /* =====================
       2️⃣ ADD TO CART
    ===================== */
    @Test(priority = 2)
    public void testAddToCart() throws Exception {

        String json = "{ \"product_id\": 1 }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/cart_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> res =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();

        Assert.assertTrue(obj.get("success").getAsBoolean(), "Add to cart failed");
    }

    /* =====================
       3️⃣ CHECKOUT
    ===================== */
    @Test(priority = 3)
    public void testCheckout() throws Exception {

        String json = "{"
                + "\"card_number\":\"4111111111111111\","
                + "\"address_id\":1"
                + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/checkout_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> res =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();

        Assert.assertTrue(obj.get("success").getAsBoolean(), "Checkout failed");
        Assert.assertTrue(obj.has("tracking_number"), "Tracking number missing");

        trackingNumber = obj.get("tracking_number").getAsString();
        Assert.assertFalse(trackingNumber.isEmpty(), "Tracking number empty");
    }

    /* =====================
       4️⃣ TRACK ORDER
    ===================== */
    @Test(priority = 4)
    public void testTrackOrder() throws Exception {

        String json = "{ \"tracking_number\": \"" + trackingNumber + "\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/track_order_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> res =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();

        Assert.assertTrue(obj.get("success").getAsBoolean(), "Tracking failed");

        JsonObject data = obj.getAsJsonObject("data");

        Assert.assertEquals(
                data.get("tracking_number").getAsString(),
                trackingNumber,
                "Tracking number mismatch"
        );

        Assert.assertTrue(data.getAsJsonArray("items").size() > 0,
                "Order items missing");
    }

    /* =====================
       5️⃣ VERIFY EMAIL (MAILHOG)
    ===================== */
    @Test(priority = 5)
    public void testEmailSent() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8025/api/v2/messages"))
                .GET()
                .build();

        HttpResponse<String> res =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();

        Assert.assertTrue(
                obj.getAsJsonArray("items").size() > 0,
                "No emails found in MailHog"
        );
    }
}
