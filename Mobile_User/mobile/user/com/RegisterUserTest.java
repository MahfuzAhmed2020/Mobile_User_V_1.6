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

public class RegisterUserTest {

    HttpClient client;

    @BeforeClass
    void setup() {
        CookieManager cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        TestLogger.step("Initializing HTTP client with session support");
        client = HttpClient.newBuilder()
                .cookieHandler(cm)
                .build();
    }

    @Test(priority = 1)
    void registerUser() throws Exception {

        String json = "{"
                + "\"first_name\":\"selim\","
                + "\"last_name\":\"abu\","
                + "\"email\":\"selim@gmail.com\","
                + "\"password\":\"Test123!\","
                + "\"confirm_password\":\"Test123!\""
                + "}";
        TestLogger.request("Initializing HTTP client with session support");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/register_api.php"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
        TestLogger.response("Initializing HTTP client with session support");
        JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();

        Assert.assertTrue(obj.get("success").getAsBoolean());
        Assert.assertTrue(obj.get("user_id").getAsInt() > 0);
    }

    @Test(priority = 2)
    void validateProfile() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost/Mobile_User/api/profile_api.php"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject data = obj.getAsJsonObject("data");

        Assert.assertTrue(obj.get("success").getAsBoolean());
        Assert.assertEquals(data.get("email").getAsString(), "rock@gmail.com");
        Assert.assertEquals(data.get("first_name").getAsString(), "rock");
        Assert.assertEquals(data.get("last_name").getAsString(), "stone");
    }
}
