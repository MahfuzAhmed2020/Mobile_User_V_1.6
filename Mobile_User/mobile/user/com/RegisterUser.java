package mobile.user.com;


import java.net.URI;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.testng.Assert;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RegisterUser {

    static HttpClient client;

    public static void main(String[] args) {

        try {
            /* ======================
               SESSION SUPPORT
            ====================== */
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            client = HttpClient.newBuilder()
                    .cookieHandler(cookieManager)
                    .build();

            /* ======================
               1️⃣ REGISTER USER
            ====================== */
            TestLogger.step("Registering a new user");

            String json = "{"
                    + "\"first_name\":\"dd\","
                    + "\"last_name\":\"dd\","
                    + "\"email\":\"dd@gmail.com\","
                    + "\"password\":\"Test123!\","
                    + "\"confirm_password\":\"Test123!\""
                    + "}";

            TestLogger.request("POST /register_api.php\n" + json);

            HttpRequest registerRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost/Mobile_User/api/register_api.php"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> registerResponse =
                    client.send(registerRequest, HttpResponse.BodyHandlers.ofString());

            TestLogger.response(registerResponse.body());

            JsonObject registerObj =
                    JsonParser.parseString(registerResponse.body()).getAsJsonObject();

            TestLogger.assertCheck("Registration success must be true");
            Assert.assertTrue(registerObj.get("success").getAsBoolean());

            /* ======================
               2️⃣ LOGIN (Required)
            ====================== */
            TestLogger.step("Logging in user to access profile");

            String loginJson = "{"
                    + "\"email\":\"rock@gmail.com\","
                    + "\"password\":\"Test123!\""
                    + "}";

            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost/Mobile_User/api/login_api.php"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(loginJson))
                    .build();

            HttpResponse<String> loginResponse =
                    client.send(loginRequest, HttpResponse.BodyHandlers.ofString());

            TestLogger.response(loginResponse.body());

            JsonObject loginObj =
                    JsonParser.parseString(loginResponse.body()).getAsJsonObject();

            TestLogger.assertCheck("Login must succeed");
            Assert.assertTrue(loginObj.get("success").getAsBoolean());

            /* ======================
               3️⃣ FETCH PROFILE
            ====================== */
            TestLogger.step("Validating profile data");

            TestLogger.request("GET /profile_api.php");

            HttpRequest profileRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost/Mobile_User/api/profile_api.php"))
                    .GET()
                    .build();

            HttpResponse<String> profileResponse =
                    client.send(profileRequest, HttpResponse.BodyHandlers.ofString());

            TestLogger.response(profileResponse.body());

            JsonObject profileObj =
                    JsonParser.parseString(profileResponse.body())
                            .getAsJsonObject()
                            .getAsJsonObject("data");

            /* ======================
               ASSERTIONS
            ====================== */
            TestLogger.assertCheck("First name should be rock");
            Assert.assertEquals(profileObj.get("first_name").getAsString(), "dd");

            TestLogger.assertCheck("Last name should be stone");
            Assert.assertEquals(profileObj.get("last_name").getAsString(), "dd");

            TestLogger.assertCheck("Email should match registered email");
            Assert.assertEquals(profileObj.get("email").getAsString(), "dd@gmail.com");

            System.out.println("\n✅ ALL VALIDATIONS PASSED");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



















//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;


	
	
	
	
/*
 * code works
 */
  /*  public static void main(String[] args) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // JSON data
            String json = "{"
                    + "\"first_name\":\"rock\","
                    + "\"last_name\":\"stone\","
                    + "\"email\":\"rock@gmail.com\","
                    + "\"password\":\"Test123!\","
                    + "\"confirm_password\":\"Test123!\","
                    + "\"agree_terms\":\"on\""
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost/Mobile_User/api/register_api.php"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Register Response:");
            System.out.println(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

