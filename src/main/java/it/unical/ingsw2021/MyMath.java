package it.unical.ingsw2021;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class MyMath {

	private ConnectionManager cm;
	private HttpClient hClient;

	/**
	 * Creating dependency within the class makes testing difficult, it might be
	 * better to receive the dependency from outside
	 */

	/*public MyMath() {
		this.cm = new ConnectionManager();
		this.hClient = new HttpClient("https://randommer.io/");
	}*/

	/**
	 * This constructor is used to receive the ConnectionManager, and HttpClient
	 * dependencies (Set methods could also be used)
	 */
	public MyMath(ConnectionManager cm, HttpClient hClient) {
		this.cm = cm;
		this.hClient = hClient;
	}

	/**
	 * Calculates fibonacci series until it finds the nth number in the series
	 * 0 1 1 2 3 5 8
	 * @param n the nth position
	 * @return the nth number in the fibonacci series
	 */
	public int fibonacci(int n) {
		if (n < 0) {
			throw new IllegalArgumentException("Illegal value " + n);
		}
		if (n == 0) {
			return 0;
		}
		if (n == 1) {
			return 1;
		}
		return fibonacci(n - 1) + fibonacci(n - 2);
	}

	/**
	 * Calculates the factorial of a given number
	 * 
	 * @param n the number to calculate the factorial
	 * @return the factorial for a given integer
	 */
	public int factorial(int n) {

		if (n == 0) {
			return 1;
		}
		return n * factorial(n - 1);
	}

	/**
	 * This is a method that calculates the nth number in the Fibonacci series by
	 * using an online service. Note that this is just an example method to show the
	 * different features provided by JUNIT and Mockito to write unit tests
	 * 
	 * Online service can accessed via a POST request thanks to Randommer.io:
	 * https://randommer.io/fibonacci-numbers?limit=a number greater than 1
	 * 
	 * @param n Integer greater than 0
	 * @return A number in the fibonacci series
	 * @throws IOException
	 */
	public BigInteger remoteFibbonacci(int n) throws IOException {

		if (n <= 0) {
			throw new IllegalArgumentException("n index must be greater than 0");
		}

		if (!cm.isNetworkConnected()) {
			throw new RuntimeException("There is no internet connection");
		}

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("limit", String.valueOf(n));

		String response = hClient.sendPost("fibonacci-numbers", "", queryParameters);

		if (response.trim().isEmpty()) {
			return BigInteger.valueOf(-1);
		}

		JSONObject json = new JSONObject(response);
		List<Object> fibSeries = json.getJSONArray("result").toList();

		String result = fibSeries.size() > 0 ? (String) fibSeries.get(fibSeries.size() - 1) : "-1";
		return new BigInteger(result);
	}

}
