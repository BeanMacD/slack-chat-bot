package knowbot.adaptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class WikiQuery {

	public WikiQuery() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getWikiPageTitles(String searchQuery) {
		JSONObject resultObj = null;
		JSONArray resultArray = null;
		List<String> wikiPageTitles = new ArrayList<String>();
		String wikiQuery = null;
		try {
			wikiQuery = URLEncoder.encode(searchQuery, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String wikiApiQuery = "https://en.wikipedia.org/w/api.php?action=query&list=search&format=json&srwhat=text&srprop=snippet%7Csectionsnippet&srlimit=10"
				+ "&srsearch=" + wikiQuery;
		URL url = null;

		HttpURLConnection connection;

		try {
			url = new URL(wikiApiQuery);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			// reads in content of url (JSON from wiki)

			BufferedReader inStream = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb = new StringBuilder();

			String inputLine;

			while ((inputLine = inStream.readLine()) != null) {
				sb.append(inputLine);
			}

			resultObj = new JSONObject(sb.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// contains the array that holds the title of the wiki page for
		// recreating wiki url for response
		resultArray = resultObj.getJSONObject("query").getJSONArray("search");

		if (resultArray.isNull(0)) {
			System.out.println("Wiki couldnt find shit!!!");
			wikiPageTitles.add(0, "bfdd");
			return wikiPageTitles;
		}
		if (resultArray.length() == 0) {
			wikiPageTitles.add(0, "bfdd");
			return wikiPageTitles;
		}

		for (int i = 0; i < resultArray.length(); i++) {

			// this List<String> now contains the titles of the wiki pages for
			// the url construction
			wikiPageTitles.add(resultArray.getJSONObject(i).getString("title"));

		}
		
		return wikiPageTitles;

	}

	// public String createWikiUrl(String query) {
	// String responseUrl = "https://en.wikipedia.org/wiki/";
	// List<String> wikiTitles = this.getWikiPageTitles(query);
	// String title = null;
	//
	//
	//
	// // encode the title to be URL ready !!
	// try {
	// title = URLEncoder.encode(wikiTitles.get(0), "UTF-8");
	//
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// responseUrl = responseUrl + title;
	//
	// System.out.println("Url = " + responseUrl);
	//
	// return responseUrl;
	// }
	public List<String> createWikiUrl(List<String> titles,Integer numOfReturnedUrls) {
		String responseUrl = "https://en.wikipedia.org/wiki/";
		String errorMessage = "I could not find any answers on Wikipedia for the question you have asked. Please try again";
		String title = null;
		String returnWikiUrls = "";
		List<String> urls = new ArrayList<String>();

		// encode the title to be URL ready !!

		if (titles.get(0).equals("bfdd")) {
			
			return null;
		}

		if (titles.size() < numOfReturnedUrls) {
			numOfReturnedUrls = titles.size();
		}

		// numOfReturnedUrls = 3
		for (int i = 0; i < numOfReturnedUrls; i++) {
			try {
				//title = URLEncoder.encode(titles.get(i), "UTF-8");
				title = titles.get(i).replaceAll(" ", "_");
				urls.add(responseUrl + title);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		return urls;
	}

	public String returnPage(List<String> titles) {
		JSONObject resultObj = null;
		String title = null;
		String pageContent = null;
		String errorMessage = "I could not find any answers on WikiPedia for the question you have asked. Please try again";
		URL url = null;
		HttpURLConnection connection;
		if (titles.get(0) == "bfdd") {
			return errorMessage;
		}

		try {
			title = URLEncoder.encode(titles.get(0), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String wikiApiQuery = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&redirects=true&explaintext=true&exsentences=3"
				+ "&titles=" + title;

		try {
			url = new URL(wikiApiQuery);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			// reads in content of url (JSON from wiki)

			BufferedReader inStream = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuilder sb = new StringBuilder();

			String inputLine;

			while ((inputLine = inStream.readLine()) != null) {
				sb.append(inputLine);
			}

			resultObj = new JSONObject(sb.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// drill down the objects inside the resultObject to find the extract or
		// wiki page content
		JSONObject queryObject = resultObj.getJSONObject("query");

		JSONObject pagesObj = queryObject.getJSONObject("pages");

		Iterator<?> keys = pagesObj.keys();
		while (keys.hasNext()) {

			String key = (String) keys.next();

			JSONObject pageIdObj = pagesObj.getJSONObject(key);

			if (pageIdObj.has("extract")) {
				pageContent = pageIdObj.getString("extract");

			} else {
				System.out.println(" No extract tag in JSONObject was found");
			}

		}

		return pageContent;
	}

}
