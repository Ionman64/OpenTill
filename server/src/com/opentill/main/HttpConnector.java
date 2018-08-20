package com.opentill.main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.opentill.logging.Log;

public class HttpConnector {

	private final String USER_AGENT = "Mozilla/5.0";
	public final static String CONTENT_TYPE_JSON = "application/json";

	// HTTP GET request
	/*
	 * private String sendGet(String url) throws Exception {
	 * 
	 * URL obj = new URL(url); HttpURLConnection con = (HttpURLConnection)
	 * obj.openConnection();
	 * 
	 * // optional default is GET con.setRequestMethod("GET");
	 * 
	 * //add request header con.setRequestProperty("User-Agent", USER_AGENT);
	 * 
	 * int responseCode = con.getResponseCode();
	 * System.out.println("\nSending 'GET' request to URL : " + url);
	 * System.out.println("Response Code : " + responseCode);
	 * 
	 * BufferedReader in = new BufferedReader( new
	 * InputStreamReader(con.getInputStream())); String inputLine; StringBuffer
	 * response = new StringBuffer();
	 * 
	 * while ((inputLine = in.readLine()) != null) { response.append(inputLine); }
	 * in.close(); return response.toString(); }
	 */

	// HTTP POST request
	public String sendPost(String url, String urlParameters, String contentType) throws Exception {
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-type", contentType);

		// Send post request
		if (urlParameters != null) {
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		}

		if (con.getResponseCode() != 200) {
			Log.warn("Update Server returned non-200 status code");
			return null;
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return (response.toString());
	}

}