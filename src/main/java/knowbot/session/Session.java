package knowbot.session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import knowbot.dao.ElasticSearch;
import knowbot.model.Answer;
import knowbot.model.AnswerAndRating;
import knowbot.model.Question;
import knowbot.model.User;

public class Session {

	private User user;
	// question contains a string for the question asked and a list of type
	// Answer
	// Answer contains answer shown preference and rating + list of answers and
	// their source
	private List<Question> questions = new ArrayList<Question>();

	// private List<SourceAndAnswers> answersAvailable = new
	// ArrayList<SourceAndAnswers>();
	private Date sessionEnd = new Date();

	private transient Timer timer = new Timer();
	private transient boolean canEndSession = false;

	public Session() {

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// public List<SourceAndAnswers> getAnswersAvailable() {
	// return answersAvailable;
	// }
	//
	// public void setAnswersAvailable(List<SourceAndAnswers> answersAvailable)
	// {
	// this.answersAvailable = answersAvailable;
	// }

	public Date getSessionEnd() {
		return sessionEnd;
	}

	public void setSessionEnd(Date sessionEnd) {
		this.sessionEnd = sessionEnd;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> question) {
		this.questions = question;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {

		this.timer = timer;

	}

	public boolean isCanEndSession() {
		return canEndSession;
	}

	public void setCanEndSession(boolean canEndSession) {
		this.canEndSession = canEndSession;
	}

	public void closeSession(Session session) {

		session.setQuestions(null);
		session.setUser(null);
	}

	public String toJayson(Session sesh) {

		Gson gson = new GsonBuilder().create();
		// convert your list to json
		String jsonSession = gson.toJson(sesh);
		// print your generated json
		System.out.println("--------------------------------------\n -----------------------\n-------------------");
		System.out.println("jsonSession: " + jsonSession);
		System.out.println("--------------------------------------\n -----------------------\n-------------------");

		return jsonSession;

	}

	@SuppressWarnings("unchecked")
	public void elasticSession(Session sesh) {

		// for each question in the list of questions
		for (Question q : sesh.getQuestions()) {

			JSONObject questionObject = new JSONObject();
			JSONArray answerArray = new JSONArray();
			//add question stuff object
			questionObject.put("question", q.getQuestion());
			questionObject.put("questionId", q.getQuestionId().toString());
			questionObject.put("questionTime", q.getQuestionTime());
			questionObject.put("query", q.getQuery());
			questionObject.put("equery", q.getQuery());
			

			List<AnswerAndRating> ansAndRating = q.getAnswers().getAnswerAndRating();
			for (int i = 0; i < ansAndRating.size(); i++) {

				//create an answerand rating object 
				JSONObject answerShownObject = new JSONObject();
				answerShownObject.put("answerSeen", ansAndRating.get(i).getAnswerShown());
				answerShownObject.put("timeShown", ansAndRating.get(i).getTimeAnswerShown());
				answerShownObject.put("rating", ansAndRating.get(i).getAnswerRating());
				answerShownObject.put("timeRating", ansAndRating.get(i).getTimeRatingGiven());
				answerShownObject.put("answerScore", ansAndRating.get(i).getAnswerScore());
				answerShownObject.put("answerSource",ansAndRating.get(i).getAnswerSource());
				//add object to answer array for each answer 
				answerArray.add(answerShownObject);
			}
			//add array of answer and rating objects to question object
			questionObject.put("answers", answerArray);
			//send question object to elastic search here 
			ElasticSearch dao = new ElasticSearch();
			dao.insertQuestion(questionObject);
		
		}
		
		

	}

	// {
	// "question" : "who is D trump?",
	// "questionTime" : "Mar 29, 2017 5:33:13 PM",
	// "query" : "D trump person",
	// "answer" : {
	// "answerAndRating" : [
	// {
	// "answerShown" : "https://en.wikipedia.org/wiki/Donald_Trump",
	// "answerRating" : "y",
	// "timeAnswerShown" : "Mar 29, 2017 5:33:21 PM",
	// "timeRatingGiven" : "Mar 29, 2017 5:33:24 PM",
	// "answerScore" : 1
	// },
	// {
	// "answerShown" :
	// "https://en.wikipedia.org/wiki/Inauguration_of_Donald_Trump",
	// "answerRating" : "n",
	// "timeAnswerShown" : "Mar 29, 2017 5:33:26 PM",
	// "timeRatingGiven" : "Mar 29, 2017 5:33:32 PM",
	// "answerScore" : -1
	// }
	// ],
	// "answerAvailable" : [
	// {
	// "source" : "Wikipedia",
	// "answers" : [
	// "https://en.wikipedia.org/wiki/Donald_Trump",
	// "https://en.wikipedia.org/wiki/Inauguration_of_Donald_Trump",
	// "https://en.wikipedia.org/wiki/Trump_University"
	// ]
	// },
	// {
	// "source" : "Reddit",
	// "answers" : [
	// "http://www.reddit.com/r/AskReddit/comments/5d8zop/serious_people_who_have_met_or_dealt_with_donald/?ref=search_posts",
	// "http://www.reddit.com/r/The_Donald/comments/55t7f4/i_cant_believe_my_eyescuckd_morning_joe_is/?ref=search_posts",
	// "http://www.reddit.com/r/changemyview/comments/4ccut6/cmv_anyone_who_supports_bernie_sanders_but_who/?ref=search_posts"
	// ]
	// }
	// ]
	// }
	// }

}
