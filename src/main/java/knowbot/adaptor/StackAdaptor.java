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
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import knowbot.model.StackExchange;

public class StackAdaptor {

	public StackAdaptor() {
		// 
	}

	public List<StackExchange> stackOverFlowQuestion(String searchQuery) {
		JSONObject resultObj = null;
		JSONArray resultArray = null;
		List<StackExchange> stackQAndId = new ArrayList<StackExchange>();
		String stackQuery = null;

		try {
			stackQuery = URLEncoder.encode(searchQuery, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// 
			e1.printStackTrace();
		}

		String stackApiQuery = "https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=relevance&q="
				+ stackQuery + "&site=stackoverflow";

		URL url = null;

		HttpURLConnection connection;

		try {
			url = new URL(stackApiQuery);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			// reads in content of url (JSON from wiki)

			BufferedReader inStream = new BufferedReader(new InputStreamReader(new GZIPInputStream(url.openStream())));
			StringBuilder sb = new StringBuilder();

			String inputLine;

			while ((inputLine = inStream.readLine()) != null) {
				sb.append(inputLine);
			}

			resultObj = new JSONObject(sb.toString());

		} catch (IOException e) {
			// 
			e.printStackTrace();
		}
		System.out.println("Out from http api working with JSON now for Stack");
		resultArray = resultObj.getJSONArray("items");
//		if (resultArray.isNull(0)) {
//			System.out.println("Stack Q array is null");
//			StackExchange response = new StackExchange();
//			response.setQuestionId(0);
//			response.setQuestionContent("bfdd");
//			stackQAndId.add(response);
//			return stackQAndId;
//
//		}

//		if (resultArray.length() == 0) {
//			System.out.println("Stack Q array is of length 0");
//			StackExchange response = new StackExchange();
//			response.setQuestionId(0);
//			response.setQuestionContent("bfdd");
//			stackQAndId.add(response);
//			return stackQAndId;
//		}

		for (int i = 0; i < resultArray.length(); i++) {

			// create an object with the id and question
			StackExchange response = new StackExchange();

			if (resultArray.getJSONObject(i).getBoolean("is_answered")) {
				response.setQuestionId(resultArray.getJSONObject(i).getLong("question_id"));
				response.setQuestionContent(resultArray.getJSONObject(i).getString("title"));
				stackQAndId.add(response);
			}

		}
		

		return stackQAndId;

	}

	// returns the top answer(most votes) for the top result from the
	// stackOverflowQuestion method
	// returns a html encoded response need to add decoder if going to use this

	public String stackOverFlowAnswer(List<StackExchange> question) {
		JSONObject resultObj = null;
		JSONArray resultArray = null;
		String errorMessage = "I could not find any answers on StackOverFlow for the question you have asked. Please try again";

		String pageContent = null;
		URL url = null;
		HttpURLConnection connection;

		if (question.get(0).getQuestionId() == 0 && question.get(0).getQuestionContent().equals("bfdd")) {
			return errorMessage;
		}

		String stackApiContent = "https://api.stackexchange.com/2.2/questions/" + question.get(0).getQuestionId()
				+ "/answers?order=desc&sort=votes&filter=withbody&site=stackoverflow";

		try {
			url = new URL(stackApiContent);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			// reads in content of url (JSON from wiki)

			BufferedReader inStream = new BufferedReader(new InputStreamReader(new GZIPInputStream(url.openStream())));
			StringBuilder sb = new StringBuilder();

			String inputLine;

			while ((inputLine = inStream.readLine()) != null) {
				sb.append(inputLine);
			}

			resultObj = new JSONObject(sb.toString());

		} catch (IOException e) {
			
			e.printStackTrace();
		}

		// drill down the objects inside the resultObject to find the extract or

		resultArray = resultObj.getJSONArray("items");

		pageContent = resultArray.getJSONObject(0).getString("body");

		return pageContent;
	}

	// return three urls in one string add \n to each url
	public List<String> stackOverFlowAnswerUrl(List<StackExchange> question, Integer numOfReturnedUrls) {
		List<String> urlToQuestion = new ArrayList<String>();
		// add question id to this
		String urlConstructor = "http://stackoverflow.com/questions/";

		System.out.println(question.size());
		// if (question.get(0).getQuestionId() == 0 &&
		// question.get(0).getQuestionContent().equals("bfdd")) {
		// System.out.println("na na na na an ana nan nana an");
		//
		// return null;
		// }
		
		if (question.size() == 0) {
			System.out.println("Returning Null mannn");
			return null;
		}

		if (question.size() < numOfReturnedUrls) {
			numOfReturnedUrls = question.size();
		}

		// numOfReturnedUrls = 3
		for (int i = 0; i < numOfReturnedUrls; i++) {
			urlToQuestion.add(urlConstructor + question.get(i).getQuestionId());
		}

		// for (int i = 0; i < urlToQuestion.size(); i++) {
		// //returnUrls += urlToQuestion.get(i) + "\n";
		//
		// }

		return urlToQuestion;
	}

}
