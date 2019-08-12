package knowbot.app;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.elasticsearch.client.transport.TransportClient;
import org.hibernate.internal.util.compare.CalendarComparator;

import com.ullink.slack.simpleslackapi.SlackSession;

import knowbot.dao.ElasticSearch;
import knowbot.dao.MongoConnection;
import knowbot.languageprocess.QueryProcess;
import knowbot.model.Answer;
import knowbot.model.AnswerAndRating;
import knowbot.model.Question;
import knowbot.model.SessionLooker;
import knowbot.model.SourceAndAnswers;
import knowbot.model.User;
import knowbot.session.Session;
//import chatbot.group.scheduler.message.SendingMessages;
import knowbot.slack.connection.SlackConnection;
import knowbot.slack.listener.ListenToMessageEvent;
import knowbot.slack.message.SendingMessages;

public class App {

	public static void main(String[] args) throws IOException {
		// Link to tags and their definitions
		// https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
		// 1. CC Coordinating conjunction
		// 2. CD Cardinal number
		// 3. DT Determiner
		// 4. EX Existential there
		// 5. FW Foreign word
		// 6. IN Preposition or subordinating conjunction
		// 7. JJ Adjective
		// 8. JJR Adjective, comparative
		// 9. JJS Adjective, superlative
		// 10. LS List item marker
		// 11. MD Modal
		// 12. NN Noun, singular or mass
		// 13. NNS Noun, plural
		// 14. NNP Proper noun, singular
		// 15. NNPS Proper noun, plural
		// 16. PDT Predeterminer
		// 17. POS Possessive ending
		// 18. PRP Personal pronoun
		// 19. PRP$ Possessive pronoun
		// 20. RB Adverb
		// 21. RBR Adverb, comparative
		// 22. RBS Adverb, superlative
		// 23. RP Particle
		// 24. SYM Symbol
		// 25. TO to
		// 26. UH Interjection
		// 27. VB Verb, base form
		// 28. VBD Verb, past tense
		// 29. VBG Verb, gerund or present participle
		// 30. VBN Verb, past participle
		// 31. VBP Verb, non-3rd person singular present
		// 32. VBZ Verb, 3rd person singular present
		// 33. WDT Wh-determiner
		// 34. WP Wh-pronoun
		// 35. WP$ Possessive wh-pronoun
		// 36. WRB Wh-adverb
		// String tagged = QueryProcess.tagger.tagString("whose");
		// System.out.println(tagged);

		// WikiQuery wiki = new WikiQuery();
		// wiki.getWikiPageTitles("What is java?");
		// String Content = stackAdapt.returnStackAnswer(testing);
		// System.out.println("Boom Bitch the stack answer is " + Content);

		SlackConnection connect = new SlackConnection();
		SlackSession sesh = connect.connecttobot();
		ListenToMessageEvent eventn = new ListenToMessageEvent();
		eventn.registeringAListener(sesh);
		 
		
		
//		TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
//		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host1"), 9300))
//		        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host2"), 9300));
//
//		// on shutdown
//
//		client.close();

//		ElasticSearch eSearch = new ElasticSearch();
//		
//		List<String> answer = eSearch.getDocumentByQuestion("wages person");
//		System.out.println("Back from Search");
		
//		for(int i = 0; i < answer.size(); i++){
//			
//			System.out.println(answer.get(i));
//		}
		
//		eSearch.getDocumentByQuestion("Java");
//		System.out.println("Back from search part 2");
		
	
		
		// System.out.println("Whats up");
		// // on shutdown
		//
		//
		//
		// String json = "{" +
		// "\"user\":\"Podge\"," +
		// "\"message\":\"Test TEst Test Test out Elasticsearch\"" +
		// "}";

		// IndexResponse response = client.prepareIndex("testingshite", "yoyo")
		// .setSource(json)
		// .get();
		//

		// // Index name
		// String _index = response.getIndex();
		// // Type name
		// String _type = response.getType();
		// // Document ID (generated or not)
		// String _id = response.getId();
		// // Version (if it's the first time you index this document, you will
		// get: 1)
		// long _version = response.getVersion();
		// // status has stored current instance statement.
		// RestStatus status = response.status();
		//
		// System.out.println(_index + " " + _type);

		// GetResponse responses = client.prepareGet("twitter", "tweet",
		// "").get();
		// System.out.println(responses.getSourceAsString());

		// client.close();

		// Session sesh = new Session();
		// AnswerAndRating ansRat = new AnswerAndRating();
		// ansRat.setAnswerShown("qwertybitch");
		// ansRat.setAnswerRating("helpful");
		//
		// List<AnswerAndRating> lustofShite = new ArrayList<AnswerAndRating>();
		// lustofShite.add(ansRat);
		// SourceAndAnswers source = new SourceAndAnswers();
		// source.setSource("StackOverflow");
		//
		// List<String> ans = new ArrayList<String>();
		// ans.add("YOYOYOYOYOYOYOYO this is an answeer");
		// source.setAnswers(ans);
		//
		// List<SourceAndAnswers> answerAvailable = new
		// ArrayList<SourceAndAnswers>();
		// answerAvailable.add(source);
		//
		//
		// Answer answer = new Answer();
		// answer.setAnswerAndRating(lustofShite);
		// answer.setAnswerAvailable(answerAvailable);
		//
		// List<Answer> listOfAnswer = new ArrayList<Answer>();
		// listOfAnswer.add(answer);
		//
		// List<Question> question = new ArrayList<Question>();
		// Question q = new Question();
		// q.setQuestion("Yo this is a test");
		// q.setAnswers(listOfAnswer);
		// question.add(q);
		//
		//
		// sesh.setQuestions(question);
		//
		//
		//
		// User user = new User();
		// user.setUserId("1234");
		// user.setUserName("Podge");
		//
		// sesh.setUser(user);
		//
		// System.out.println("Here we go");
		// SessionLooker sessionLooker = new SessionLooker();
		// sessionLooker.toJayson(sesh);
		// System.exit(0);
		//

		// QueryProcess qproc = new QueryProcess();
		// String tagged =
		// qproc.decipherQuestion("What is the sky made up of?", "podge");
		// System.out.println(tagged);

		// U4K79AE1G
		// SendingMessages sending = new SendingMessages();
		// sending.sendDirectMessageToAUser(sesh, "Yo bitch","devuser");

	}

}
