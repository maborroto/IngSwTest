package it.unical.ingsw2021;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * HttpClient example to do a simple HTTP POST request. This class will be mocked by
 * Mockito library when performing the unit tests for the class MyMath 
 */
public class HttpClient {

	private final String baseUrl;
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public HttpClient(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String sendPost(String endpoint, String bodyJson, Map<String, String> queryParameters) throws IOException {

		OkHttpClient client = new OkHttpClient();
		RequestBody formBody = new FormBody.Builder().build();
		if (bodyJson.trim().isEmpty()) {
			formBody = RequestBody.create(bodyJson, JSON);
		}

		HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + endpoint).newBuilder();
		if (!queryParameters.isEmpty())
			queryParameters.forEach((key, value) -> urlBuilder.addQueryParameter(key, value));
		String url = urlBuilder.build().toString();
		Request request = new Request.Builder().url(url).post(formBody).build();
		Response response = client.newCall(request).execute();

		int responseCode = response.code();
		System.out.println("GET Response Code :: " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			return response.body().string();
		}
		System.out.println("GET request not worked");
		return "";

	}
}
