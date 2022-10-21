package it.unical.ingsw2021;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is to simulate an Internet Connection Manager, to show how Mockito
 * library is able to mock instances and enforce class methods behavior
 */
public class ConnectionManager {

	private String trustUrl = "http://www.google.com";

	public boolean isNetworkConnected() {
		try {
			URL url = new URL(trustUrl);
			URLConnection connection = url.openConnection();
			connection.connect();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
