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

public class OrderFlowTest {
	
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
    	

    static HttpClient client;
    static String trackingNumber;

    @BeforeClass
    public void setup() {
        TestLogger.step("Initializing HTTP client with session support");

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
    }

    /* =====================
       1️⃣ LOGIN
    ===================== */
    @Test(priority = 1)
    public void testLogin() throws Exception {

        TestLogger.step("Testing Login API");

        String json = "{"
                + "\"email\":\"rr@GMAIL.com\","
                + "\"password\":\"Test123!\""
                + "}";

        TestLogger.request("POST /login_api.php\n" + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/login_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> res =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        TestLogger.response(res.body());

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();

        TestLogger.assertCheck("Login success must be true");
        Assert.assertTrue(obj.get("success").getAsBoolean());
    }

    /* =====================
       2️⃣ ADD TO CART
    ===================== */
    @Test(priority = 2)
    public void testAddToCart() throws Exception {

        TestLogger.step("Testing Add To Cart API");

        String json = "{ \"product_id\": 2 }";
        TestLogger.request("POST /cart_api.php\n" + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/cart_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> res =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        TestLogger.response(res.body());

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();

        TestLogger.assertCheck("Product added to cart");
        Assert.assertTrue(obj.get("success").getAsBoolean());
    }

    /* =====================
       3️⃣ CHECKOUT
    ===================== */
    @Test(priority = 3)
    public void testCheckout() throws Exception {

        TestLogger.step("Testing Checkout API");

        String json = "{"
                + "\"card_number\":\"4111111111111111\","
                + "\"address_id\":2"
                + "}";

        TestLogger.request("POST /checkout_api.php\n" + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/checkout_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> res =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        TestLogger.response(res.body());

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();

        TestLogger.assertCheck("Checkout successful");
        Assert.assertTrue(obj.get("success").getAsBoolean());

        TestLogger.assertCheck("Tracking number generated");
        trackingNumber = obj.get("tracking_number").getAsString();
        Assert.assertNotNull(trackingNumber);
    }

    /* =====================
       4️⃣ TRACK ORDER
    ===================== */
    @Test(priority = 4)
    public void testTrackOrder() throws Exception {

        TestLogger.step("Testing Order Tracking API");

        String json = "{ \"tracking_number\": \"" + trackingNumber + "\" }";
        TestLogger.request("POST /track_order_api.php\n" + json);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/track_order_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> res =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        TestLogger.response(res.body());

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();

        TestLogger.assertCheck("Tracking response success");
        Assert.assertTrue(obj.get("success").getAsBoolean());
    }

    /* =====================
       5️⃣ VERIFY EMAIL
    ===================== */
    @Test(priority = 5)
    public void testEmail() throws Exception {

        TestLogger.step("Verifying Order Confirmation Email in MailHog");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8025/api/v2/messages"))
                .GET()
                .build();

        HttpResponse<String> res =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        TestLogger.response(res.body());

        JsonObject obj = JsonParser.parseString(res.body()).getAsJsonObject();

        TestLogger.assertCheck("At least one email exists");
        Assert.assertTrue(obj.getAsJsonArray("items").size() > 0);
    }
}
