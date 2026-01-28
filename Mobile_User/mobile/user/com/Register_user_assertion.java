package mobile.user.com;

import java.net.URI;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Register_user_assertion {

	static HttpClient client;

	public static void main(String[] args) {

		try {
			// 🔐 Enable session (IMPORTANT)
			CookieManager cookieManager = new CookieManager();
			cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

			client = HttpClient.newBuilder().cookieHandler(cookieManager).build();

			registerUser();
			verifyProfile();

			System.out.println("\n✅ ALL ASSERTIONS PASSED");

		} catch (AssertionError ae) {
			System.err.println("❌ ASSERTION FAILED: " + ae.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ====================== REGISTER USER ======================
	 */
	static void registerUser() throws Exception {

		String json = "{" + "\"first_name\":\"pop\"," + "\"last_name\":\"pop\"," + "\"email\":\"pop@gmail.com\","
				+ "\"password\":\"Test123!\"," + "\"confirm_password\":\"Test123!\"" + "}";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost/Mobile_User/api/register_api.php"))
				.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println("\n🧾 REGISTER RESPONSE:");
		System.out.println(response.body());

		JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();

		// 🔎 ASSERTIONS
		assert obj.get("success").getAsBoolean() : "Registration failed";
		assert obj.get("user_id").getAsInt() > 0 : "User ID not returned";
	}

	/*
	 * ====================== VERIFY PROFILE ======================
	 */
	
	static void verifyProfile() throws Exception {
		Thread.sleep(6000);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost/Mobile_User/api/profile_api.php")).GET().build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println("\n👤 PROFILE RESPONSE:");
		System.out.println(response.body());

		JsonObject obj = JsonParser.parseString(response.body()).getAsJsonObject();
		JsonObject data = obj.getAsJsonObject("data");

		// 🔎 ASSERTIONS
		assert obj.get("success").getAsBoolean() : "Profile API failed";
		assert data.get("email").getAsString().equals("pop@gmail.com") : "Email mismatch";
		assert data.get("first_name").getAsString().equals("pop") : "First name mismatch";
		assert data.get("last_name").getAsString().equals("pop") : "Last name mismatch";

		System.out.println("✔ Profile data validated successfully");
	}
}
