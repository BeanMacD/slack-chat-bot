package knowbot.adaptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DuckDuckGo {

	public DuckDuckGo() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getDuckUrl(String query, Integer numOfUrls){
		String duckQuery = null;
		URL url = null;
		HttpURLConnection connection;
		JSONObject resultObj = null;
		JSONArray resultArray = null;
		String errorMessage = "I could not find any answers on DuckDuckGo for the question you have asked. Please try again";
		String returnedDuckUrls = "";
		List<String> urls = new ArrayList<String>();


		try {
			duckQuery = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String duckGoApi = "http://api.duckduckgo.com/?q="+ duckQuery+ "&format=json";
		
		
		try {
			url = new URL(duckGoApi);
			connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31");

			connection.connect();
			// reads in content of url (JSON from wiki)
			
			BufferedReader inStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder sb = new StringBuilder();

			String inputLine;

			while ((inputLine = inStream.readLine()) != null) {
				sb.append(inputLine);
			}

			//contains json from api call
			resultObj = new JSONObject(sb.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//get the array of results
		try {
			resultArray = resultObj.getJSONArray("RelatedTopics");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(resultArray.length() <numOfUrls){
			numOfUrls = resultArray.length();
		}

		if (resultArray.isNull(0)) {
			urls.add(errorMessage);
			return urls;
		}else{
			for (int i = 0; i < numOfUrls; i++) {

				// this List<String> now contains the titles of the wiki pages for
				// the url construction
				//wikiPageTitles.add(resultArray.getJSONObject(i).getString("title"));
				try {
					urls.add(resultArray.getJSONObject(i).getString("FirstURL"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				

			}
		}
		
		return urls;
	}
}
