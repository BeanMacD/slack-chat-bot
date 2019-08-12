package knowbot.dao;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.TermQuery;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class ElasticSearch {
/*
 * Why choose to use this instead of webhook?
 */
	public static String indexName = "knowbot";
	public static String typeName = "question";
	
	public ElasticSearch() {
		// TODO Auto-generated constructor stub
	}

	public static TransportClient client = init();

	public static TransportClient init() {
		try {
			client = new PreBuiltTransportClient(Settings.EMPTY)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("THE TRANSPORT CLIENT COULDNT CONNECT");
		}
		return client;
	}

	public void insertQuestion(org.json.simple.JSONObject questionObject) {

		@SuppressWarnings("unchecked")
		IndexResponse response = client.prepareIndex(indexName, typeName, (String) questionObject.get("questionId"))
				.setSource(questionObject).get();

		System.out.println("Shes inserted into elasticSearch");

	}

	public List<String> getDocumentByQuestion(String question) {
		String include[] = { "answers.answerSeen", "answers.answerScore" };
		String exclude[] = { "" };
		List<String> answers = new ArrayList<String>();

		
		float minScore = 0.85f;
			SearchResponse response = client.prepareSearch(indexName).setTypes(typeName)
					.setSearchType(SearchType.DEFAULT)
					.setMinScore(minScore)
					.setPostFilter(QueryBuilders.rangeQuery("answers.answerScore").gte(0))
					.setQuery(QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("query", question))
					.should(QueryBuilders.termQuery("equery", question))
					.should(QueryBuilders.matchQuery("answer.answerShown", question)))
					.setFetchSource(include, exclude)
					.setFrom(0).setSize(60).setExplain(true)
					.execute()
					.actionGet();
			
			SearchHit[] results = response.getHits().getHits();

			

			for (SearchHit hit : results) {
				System.out.println("------------------------------");
				String sourceDoc = hit.getSourceAsString();
				System.out.println("----------------------------------");
				

				JSONObject json = new JSONObject(sourceDoc);
				JSONArray arr = json.getJSONArray("answers");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject temp = new JSONObject();
					temp = arr.getJSONObject(i);
					if (temp.getInt("answerScore") >= 0) {
						answers.add(temp.getString("answerSeen"));
					}
				}
			}

		

		if (answers.size() == 0 || answers == null) {
			return null;
		}

		return answers;
	}


}
