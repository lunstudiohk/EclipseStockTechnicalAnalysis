package com.lunstudio.stocktechnicalanalysis.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.net.ssl.TrustManager;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
public class HttpUtils {

	private final static String NEW_LINE = "\n";
	private final static String USER_AGENT = "Mozilla/5.0";
	private static final String COOKIES_HEADER = "Set-Cookie";
	
	private static CookieManager msCookieManager = new java.net.CookieManager();
	
	private static HttpUtils instance = new HttpUtils();
	
	public HttpUtils() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				java.security.cert.X509Certificate[] chck = null;
				return chck;
			}
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
			}
		 } 
		};
		
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			
		}
	}
	
	public static HttpUtils getInstance() {
		return HttpUtils.instance;
	}
	
	public static String sendPost(String url, Map<String, String> params) throws Exception {
		URL obj = new URL(url);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        CookieHandler.setDefault(msCookieManager);

        HttpURLConnection conn = (HttpURLConnection)obj.openConnection();
/*        
        if (msCookieManager.getCookieStore().getCookies().size() > 0) {
        	String str = "";
        	for(HttpCookie cookie : msCookieManager.getCookieStore().getCookies() ) {
        		str = cookie.toString() + ";";
        	}
            // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
        	conn.setRequestProperty("Cookie", str);
            //TextUtils.join(";",  msCookieManager.getCookieStore().getCookies()));    
        }
*/        
        conn.setRequestMethod("POST");
		conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        
        /*
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                msCookieManager.getCookieStore().add(null,HttpCookie.parse(cookie).get(0));
            }               
        }
        */
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine).append(NEW_LINE);
		}
		in.close();
		return response.toString();
	}
	
	public static String sendGet(String url, Map<String, String> params) throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append(url).append("?");
		if( params != null ) {
		Iterator<String> paramsKeyIt = params.keySet().iterator();
		while(paramsKeyIt.hasNext()){
			String key = paramsKeyIt.next();
			String val = params.get(key);
			buf.append(key).append("=").append(val).append("&");
		}
		}
		URL obj = new URL(buf.toString());
		System.out.println("Call URL : " + buf.toString());
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine).append(NEW_LINE);
		}
		in.close();
		return response.toString();
	}

	public static String sendGet(String url) throws Exception {
		URL obj = new URL(url);
		System.out.println("Call URL : " + url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine).append(NEW_LINE);
		}
		in.close();
		return response.toString();
	}
	
	public static String sendGet(String url, String line) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			if( inputLine.indexOf(line) != -1) {
				break;
			}
		}
		in.close();
		return inputLine;
	}
	
	
	public String sendHttpsGet(String url) throws Exception {
		URL obj = new URL(url);
		System.out.println("Call URL : " + url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine).append(NEW_LINE);
		}
		in.close();
		return response.toString();
	}
	
	public static List<String> downloadCsv(String url, String encoding, String filePath) throws Exception {
		URL obj = new URL(url);
		System.out.println("Call URL : " + url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), encoding));
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
	    
		String inputLine;
		List<String> lineList = new ArrayList<String>();
		while ((inputLine = in.readLine()) != null) {
			lineList.add(inputLine);
			writer.write(inputLine);
			writer.write(NEW_LINE);
		}
		in.close();
		writer.close();
		return lineList;
	}
	
	public static List<String> downloadCsv(String url, String encoding) throws Exception {
		URL obj = new URL(url);
		System.out.println("Call URL : " + url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), encoding));
	    
		String inputLine;
		List<String> lineList = new ArrayList<String>();
		while ((inputLine = in.readLine()) != null) {
			if( inputLine.trim().length() > 0 ) {
				lineList.add(inputLine);
			}
		}
		in.close();
		return lineList;
	}
	
	public static String getUrl(String url, Map<String, String> params) {
		StringBuffer buf = new StringBuffer();
		buf.append(url).append("?");
		Iterator<String> paramsKeyIt = params.keySet().iterator();
		while(paramsKeyIt.hasNext()){
			String key = paramsKeyIt.next();
			String val = params.get(key);
			buf.append(key).append("=").append(val).append("&");
		}
		System.out.println("Call URL : " + buf.toString());
		return buf.toString();
	}
}
