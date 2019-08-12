package knowbot.slack.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

//import knowbot.adaptor.DuckDuckGo;
import knowbot.adaptor.RedditAdaptor;
import knowbot.adaptor.StackAdaptor;
import knowbot.adaptor.WikiQuery;
import knowbot.dao.ElasticSearch;
import knowbot.dao.MongoConnection;
import knowbot.languageprocess.QueryProcess;
import knowbot.model.Answer;
import knowbot.model.AnswerAndRating;
import knowbot.model.Question;
import knowbot.model.SourceAndAnswers;
import knowbot.model.StackExchange;
import knowbot.model.User;
import knowbot.session.Session;
import knowbot.slack.message.SendingMessages;

public class ListenToMessageEvent {

	public ListenToMessageEvent() {

	}

	public static Integer numOfReturnedUrls = 3;
	public static String messageQuestion;
	public static List<String> wikiUrls = new ArrayList<String>();
	public static List<String> stackUrls = new ArrayList<String>();
	public static List<String> redditUrls = new ArrayList<String>();
	public static List<String> duckGoUrls = new ArrayList<String>();
	public static List<String> recommended = new ArrayList<String>();
	final static String wikipedia = "Wikipedia";
	final static String stackOverFlow = "StackOverFlow";
	final static String reddit = "Reddit";
	final static String wikiPreference = "w";
	final static String stackPreference = "s";
	final static String redditPreference = "r";
	// variable for timer This represents minutes in miliseconds
	final static long timerTimeOut = 100000;
	final static long closeSesionTimer = 20000;
	final static String dateFormat = "yyyy.MM.dd G HH:mm:ss ";

	// Session sesh = null;
	HashMap<String, Session> seshOn = new HashMap<String, Session>();

	public void registeringAListener(SlackSession session) {
		// first define the listener
		SlackMessagePostedListener messagePostedListener = new SlackMessagePostedListener() {

			public void onEvent(SlackMessagePosted event, final SlackSession session) {
				
				final SendingMessages sending = new SendingMessages();
				final SlackUser messageSender;
				final String channelOnWhichMessageWasPosted = event.getChannel().getName();
				String messageContent = event.getMessageContent();
				String answerReturn = "";
				long hasSentMessage = 0;

				messageSender = event.getSender();
				// this session = session of user who just typed. If no user
				// found then userSesion = null
				Session userSession = seshOn.get(messageSender.getId());

				final TimerTask closeSession = new TimerTask() {

					@Override
					public void run() {
						if (channelOnWhichMessageWasPosted.startsWith("D")) {

							sending.sendDirectMessageToAUser(session,
									"Hope I helped you out " + messageSender.getUserName()
											+ ". Please ask a new question to get started again.",
									messageSender.getUserName());

						}
						// send it back to channel
						else {
							// send Wiki message
							sending.sendMessageToAChannel(session,
									"Hope I helped you out " + messageSender.getUserName()
											+ ". Please ask a new question to get started again.",
									channelOnWhichMessageWasPosted);

						}
						Session session = seshOn.get(messageSender.getId());
						session.setCanEndSession(false);
						session.getTimer().cancel();

						Date sessionEnd = new Date();

						session.setSessionEnd(sessionEnd);

						String jsonSession = session.toJayson(session);
						MongoConnection mongo = new MongoConnection();
						mongo.connectToDB(jsonSession);

						// session.elasticSession(session);
						// killed the session
						System.out.println(messageSender.getUserName());
						seshOn.put(messageSender.getId(), null);

					}
				};
				TimerTask timeTask = new TimerTask() {

					@Override
					public void run() {

						if (channelOnWhichMessageWasPosted.startsWith("D")) {

							sending.sendDirectMessageToAUser(session,
									"Hey " + messageSender.getUserName()
											+ "! Can I help you out with anything else?.",
									messageSender.getUserName());

						}
						// send it back to channel
						else {
							// send Wiki message
							sending.sendMessageToAChannel(session,
									"Hey " + messageSender.getUserName()
											+ "! Can I help you out with anything else?.",
									channelOnWhichMessageWasPosted);

						}

						Session userSessionBotNotification = seshOn.get(messageSender.getId());

						userSessionBotNotification.getTimer().cancel();
						Timer lastComms = new Timer();
						userSessionBotNotification.setTimer(lastComms);
						Timer time = userSessionBotNotification.getTimer();
						userSessionBotNotification.setCanEndSession(true);
						// two minutes after this another timer will be called
						time.schedule(closeSession, closeSesionTimer);
					}
				};

				if (!event.getSender().isBot()) {
					List<String> titles = new ArrayList<String>();
					List<StackExchange> stackTitles = new ArrayList<StackExchange>();
					WikiQuery wikiQ = new WikiQuery();
					StackAdaptor stackOverflow = new StackAdaptor();
					QueryProcess queryProcess = new QueryProcess();
					String query = queryProcess.decipherQuestion(messageContent, messageSender.getUserName());
					String greetingMessage = queryProcess.isGreeting(messageContent, messageSender.getUserName());

					System.out.println("Query is " + query);
					// check if greeting will be sent if not go on and get
					// answers
					// if it isnt already sent it will go into if and send an
					// apropriate welcome message
					// bfdd is a string put into list if message content doesnt
					// match any greetings from file
					if (!greetingMessage.equals("bfdd")) {
						System.out.println("greeting stuff");

						hasSentMessage = 1;
						// send greeting to user or channel
						if (channelOnWhichMessageWasPosted.startsWith("D")) {
							sending.sendDirectMessageToAUser(session, greetingMessage, messageSender.getUserName());
						}
						// send it back to channel
						else {
							sending.sendMessageToAChannel(session, greetingMessage, channelOnWhichMessageWasPosted);
						}
					} // end greeting check if

					// if its a question
					if (!query.equals("")) {

						if (userSession == null) {
							System.out.println("seshOn didnt have that user");
							userSession = new Session();

							User user = new User();
							user.setUserId(messageSender.getId());
							user.setUserName(messageSender.getUserName());
							userSession.setUser(user);
							System.out.println("Added stuff to user");
							seshOn.put(messageSender.getId(), userSession);
							System.out.println("the sesh is awn");

						} else {
							System.out.println(
									"the session is already created for user " + userSession.getUser().getUserName());
						}

						// start the session timer by canceling the original one
						// and starting a new
						userSession.getTimer().cancel();
						Timer lastComms = new Timer();
						userSession.setTimer(lastComms);

						Timer time = userSession.getTimer();
						time.schedule(timeTask, timerTimeOut);
						// send a Im working on your query question
//						String initialResponse = "";
//						if (channelOnWhichMessageWasPosted.startsWith("D") && hasSentMessage == 0) {
//							initialResponse = "Hey " + messageSender.getUserName() + ". " + " Nice question. "
//									+ "Hopefully i can help you out.";
//							sending.sendDirectMessageToAUser(session, initialResponse, messageSender.getUserName());
//
//						}
//						// send it back to channel
//						else if (hasSentMessage == 0) {
//							initialResponse = "Hey " + messageSender.getUserName() + ". " + "Nice question. "
//									+ "Hopefully i can help you out.";
//							// send Wiki message
//							sending.sendMessageToAChannel(session, initialResponse, channelOnWhichMessageWasPosted);
//						}
						Question question = new Question();
						Answer answer = new Answer();
						List<SourceAndAnswers> sourceAnswerList = new ArrayList<SourceAndAnswers>();
						// start timer here !!!

						// question instance
						System.out.println("Adding Question to session");

						question.setQuestion(messageContent);
						question.setQuery(query);
						Date questionTime = new Date();
						question.setQuestionTime(questionTime);
						userSession.getQuestions().add(question);
						seshOn.put(messageSender.getId(), userSession);

						ElasticSearch eSearch = new ElasticSearch();
						recommended = eSearch.getDocumentByQuestion(query);
						// set the look up sources boolean to true
						userSession.getQuestions().get(userSession.getQuestions().size() - 1)
								.setLookUpOtherSources(true);

						if (recommended != null && !recommended.isEmpty()) {
							SourceAndAnswers sourceAndAnswersRecommended = new SourceAndAnswers();
							sourceAndAnswersRecommended.setSource("recommended");
							sourceAndAnswersRecommended.setAnswers(recommended);
							// add to list
							sourceAnswerList.add(sourceAndAnswersRecommended);

							// set the answers in the session
							answer.setAnswerAvailable(sourceAnswerList);
							userSession.getQuestions().get(userSession.getQuestions().size() - 1).setAnswers(answer);

							// hardcoded to show first answer from knowledge
							// base
							answerReturn += recommended.get(0);
							// add one to the recommended answers seen counter
							userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
									.setRecommendedAnswerSeenCounter(
											userSession.getQuestions().get(userSession.getQuestions().size() - 1)
													.getAnswers().getRecommendedAnswerSeenCounter() + 1);

							System.out.println("BLAH BLAH ");
						}

					}

					if (messageContent.toLowerCase().equals("no") && userSession.getQuestions()
							.get(userSession.getQuestions().size() - 1).isLookUpOtherSources()) {
						answerReturn = "Ok " + userSession.getUser().getUserName() + ". Hopefully i helped you out!!";
						userSession.getQuestions().get(userSession.getQuestions().size() - 1)
								.setLookUpOtherSources(false);

					}

					System.out.println(messageContent + " message content" + " boolean= " + userSession.getQuestions()
							.get(userSession.getQuestions().size() - 1).isLookUpOtherSources());
					if ((messageContent.toLowerCase().equals("yes") && userSession.getQuestions()
							.get(userSession.getQuestions().size() - 1).isLookUpOtherSources())
							|| (recommended == null && userSession.getQuestions()
									.get(userSession.getQuestions().size() - 1).isLookUpOtherSources())) {
						// if its true and they want to see other messages

						System.out.println("BLAH BLAH BALJA");
						titles = wikiQ.getWikiPageTitles(
								userSession.getQuestions().get(userSession.getQuestions().size() - 1).getQuery());
						wikiUrls = wikiQ.createWikiUrl(titles, numOfReturnedUrls);

						if (wikiUrls != null) {
							answerReturn += "\nPlease type 'w' to see answers from Wikipedia";

							SourceAndAnswers sourceAndAnswers = new SourceAndAnswers();
							sourceAndAnswers.setSource(wikipedia);
							sourceAndAnswers.setAnswers(wikiUrls);
							// add this source and its answers to a list
							List<Question> questions = userSession.getQuestions();
							System.out.println("TestTest");
							userSession.getQuestions().get(questions.size() - 1).getAnswers()
									.addToAnswerAvailable(sourceAndAnswers);
							System.out.println("123");
						}
						// stack answers

						System.out.println("Bout to Check stack");
						stackTitles = stackOverflow.stackOverFlowQuestion(
								userSession.getQuestions().get(userSession.getQuestions().size() - 1).getQuery());
						System.out.println("Got questions going to check for the answers " + stackTitles.size());
						stackUrls = stackOverflow.stackOverFlowAnswerUrl(stackTitles, numOfReturnedUrls);
						System.out.println("Back from getting stack question urls");
						System.out.println("BOOBOBOredtyfuBBOBOB");

						if (stackUrls != null) {
							answerReturn += "\nPlease type 's' to see answers from StackOverflow";
							SourceAndAnswers sourceAndAnswersStack = new SourceAndAnswers();
							sourceAndAnswersStack.setSource(stackOverFlow);
							sourceAndAnswersStack.setAnswers(stackUrls);
							// add this source and its answers to a list
							List<Question> questions = userSession.getQuestions();
							userSession.getQuestions().get(questions.size() - 1).getAnswers()
									.addToAnswerAvailable(sourceAndAnswersStack);

						}
						// reddit answers

						redditUrls = new RedditAdaptor().getRedditUrl(
								userSession.getQuestions().get(userSession.getQuestions().size() - 1).getQuery(),
								numOfReturnedUrls);

						if (redditUrls != null) {
							answerReturn += "\nPlease type 'r' to see answers from Reddit.";
							SourceAndAnswers sourceAndAnswersReddit = new SourceAndAnswers();
							sourceAndAnswersReddit.setSource(reddit);
							sourceAndAnswersReddit.setAnswers(redditUrls);
							// add this source and its answers to a list
							List<Question> questions = userSession.getQuestions();
							userSession.getQuestions().get(questions.size() - 1).getAnswers()
									.addToAnswerAvailable(sourceAndAnswersReddit);
							System.out.println("Added Answer for Reddit to session");
						} else {
							System.out.println("Reddit is null");
							System.out.println("Maybe Reddit took too Long");
						}

						if (wikiUrls == null && stackUrls == null && redditUrls == null) {
							answerReturn = "Awh no !!. Sorry " + messageSender.getUserName()
									+ ". I couldnt find any answers at all. Lucky im still in development";
						}
						userSession.getQuestions().get(userSession.getQuestions().size() - 1)
								.setLookUpOtherSources(false);
					}

					if (messageContent.toLowerCase().equals("rec")) {
						userSession.getTimer().cancel();
						Timer lastComms = new Timer();
						userSession.setTimer(lastComms);
						Timer timr = userSession.getTimer();
						timr.schedule(timeTask, timerTimeOut);
						List<Question> questions = userSession.getQuestions();
						Answer answers = questions.get(questions.size() - 1).getAnswers();
						List<SourceAndAnswers> sourceAndAns = answers.getAnswerAvailable();
						List<AnswerAndRating> answerShownAndRating = new ArrayList<AnswerAndRating>();
						int recommendedSeenCount = answers.getRecommendedAnswerSeenCounter();

						for (int k = 0; k < sourceAndAns.size(); k++) {
							if (sourceAndAns.get(k).getSource().equals("recommended")) {
								// answersAvailable contain the answers that
								// are available for the source of recommended
								List<String> answersAvailable = new ArrayList<String>();
								answersAvailable = sourceAndAns.get(k).getAnswers();
								if (recommendedSeenCount >= answersAvailable.size()) {
									answerReturn = "Hey " + userSession.getUser().getUserName()
											+ ". You have seen all the recommended answers"
											+ ". If you like you can see the answers from the other sources.";
								} else {
									answerReturn = "Hey " + userSession.getUser().getUserName()
											+ ". Here's my recommended answer "
											+ answersAvailable.get(recommendedSeenCount) + "\n type 'rec'"
											+ " if you wish to see the next answer from our Recommended list or "
											+ "type other source type from my previous message."
											+ "\n\n Please let me know if this was helpful (y/n).";
									AnswerAndRating answerAndRating = new AnswerAndRating();
									answerAndRating.setAnswerShown(answersAvailable.get(recommendedSeenCount));
									answerAndRating.setAnswerSource("recommended");
									Date answerShownDate = new Date();
									answerAndRating.setTimeAnswerShown(answerShownDate);
									answerShownAndRating.add(answerAndRating);
									// add one to counter of seen answers
									userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
											.setRecommendedAnswerSeenCounter(userSession.getQuestions()
													.get(userSession.getQuestions().size() - 1).getAnswers()
													.getRecommendedAnswerSeenCounter() + 1);
									// if there is no Shown Answers yet set
									// them
									// else add an item to it
									if (userSession.getQuestions().get(userSession.getQuestions().size() - 1)
											.getAnswers().getAnswerAndRating().size() == 0) {
										userSession.getQuestions().get(userSession.getQuestions().size() - 1)
												.getAnswers().setAnswerAndRating(answerShownAndRating);
										System.out.println("Set Answer and rating");
									} else {
										userSession.getQuestions().get(userSession.getQuestions().size() - 1)
												.getAnswers()
												.addtoAnswerAndRating(answersAvailable.get(recommendedSeenCount));
										System.out.println("Added Answer and rating");
									}
									System.out.println("Added stuff to session bro");
								} // end else
							} // end if
						} // end inner for
					} // end first if
					if (messageContent.toLowerCase().equals(wikiPreference)) {
						// user has asked for an anwer last communication = now
						userSession.getTimer().cancel();
						Timer lastComms = new Timer();
						userSession.setTimer(lastComms);
						Timer timr = userSession.getTimer();
						timr.schedule(timeTask, timerTimeOut);
						List<Question> questions = userSession.getQuestions();
						Answer answers = questions.get(questions.size() - 1).getAnswers();
						int wikiSeenCount = answers.getWikiAnswerSeenCounter();
						System.out.println("The wiki count variable is " + wikiSeenCount);
						List<SourceAndAnswers> sourceAndAns = answers.getAnswerAvailable();
						List<AnswerAndRating> answerShownAndRating = new ArrayList<AnswerAndRating>();
						for (int k = 0; k < sourceAndAns.size(); k++) {
							if (sourceAndAns.get(k).getSource().equals(wikipedia)) {
								// answersAvailable contain the answers that
								// are available for the source of WikiPedia
								List<String> answersAvailable = new ArrayList<String>();
								answersAvailable = sourceAndAns.get(k).getAnswers();
								if (wikiSeenCount >= answersAvailable.size()) {
									answerReturn = "Hey " + userSession.getUser().getUserName()
											+ ". You have seen all the answers from " + wikipedia
											+ ". If you like you can see the answers from StackOverflow (type 's') or "
											+ "from Reddit (types 'r') or from our recommended answers (type 'rec').";
								} else {
									// need it to show answer at pos 0 then
									// at
									// pos 1 then at pos 2
									answerReturn = "Hey " + userSession.getUser().getUserName()
											+ ". Here's the answer I found from " + wikipedia + " "
											+ answersAvailable.get(wikiSeenCount) + "\n type " + wikiPreference
											+ " if you wish to see the next answer from " + wikipedia + " or "
											+ "type other source type from my previous message."
											+ "\n\n Please let me know if this was helpful (y/n).";
									AnswerAndRating answerAndRating = new AnswerAndRating();
									answerAndRating.setAnswerShown(answersAvailable.get(wikiSeenCount));
									answerAndRating.setAnswerSource(wikipedia);
									Date answerShownDate = new Date();
									answerAndRating.setTimeAnswerShown(answerShownDate);
									answerShownAndRating.add(answerAndRating);
									// add one to counter of seen answers
									userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
											.setWikiAnswerSeenCounter(userSession.getQuestions()
													.get(userSession.getQuestions().size() - 1).getAnswers()
													.getWikiAnswerSeenCounter() + 1);
									// if there is no Shown Answers yet set
									// them
									// else add an item to it
									if (userSession.getQuestions().get(userSession.getQuestions().size() - 1)
											.getAnswers().getAnswerAndRating().size() == 0) {

										// userSession.getQuestions().get(userSession.getQuestions().size()
										// - 1)
										// .getAnswers().get(j).setAnswerShown(answerShown);

										userSession.getQuestions().get(userSession.getQuestions().size() - 1)
												.getAnswers().setAnswerAndRating(answerShownAndRating);
										System.out.println("Set Answer and rating");
									} else {

										userSession.getQuestions().get(userSession.getQuestions().size() - 1)
												.getAnswers().addtoAnswerAndRating(answersAvailable.get(wikiSeenCount));
										System.out.println("Added Answer and rating");

									}
								} // end else
							} // end if
						} // end inner for

					} // end first if

					if (messageContent.toLowerCase().equals(stackPreference)) {

						userSession.getTimer().cancel();
						Timer lastComms = new Timer();
						userSession.setTimer(lastComms);
						Timer timr = userSession.getTimer();
						timr.schedule(timeTask, timerTimeOut);

						List<Question> questions = userSession.getQuestions();
						Answer answers = questions.get(questions.size() - 1).getAnswers();
						List<SourceAndAnswers> sourceAndAns = answers.getAnswerAvailable();
						List<AnswerAndRating> answerShownAndRating = new ArrayList<AnswerAndRating>();

						int stackSeenCount = answers.getStackAnswerSeenCounter();
						for (int k = 0; k < sourceAndAns.size(); k++) {

							if (sourceAndAns.get(k).getSource().equals(stackOverFlow)) {

								// answersAvailable contain the answers that
								// are available for the source of WikiPedia
								List<String> answersAvailable = new ArrayList<String>();
								answersAvailable = sourceAndAns.get(k).getAnswers();

								if (stackSeenCount >= answersAvailable.size()) {
									answerReturn = "Hey " + userSession.getUser().getUserName()
											+ ". You have seen all the answers from " + stackOverFlow
											+ ". If you like you can see the answers from Wikipedia (type 'w') or "
											+ "from Reddit (types 'r') or choose from our recommended answers (type 'rec')";

								} else {
									answerReturn = "Hey " + userSession.getUser().getUserName()
											+ ". Here's the answer I found from " + stackOverFlow + " "
											+ answersAvailable.get(stackSeenCount) + "\n type " + stackPreference
											+ " if you wish to see the next answer from StackOverflow or "
											+ "type other source type from my previous message."
											+ "\n\n Please let me know if this was helpful (y/n).";

									AnswerAndRating answerAndRating = new AnswerAndRating();
									answerAndRating.setAnswerShown(answersAvailable.get(stackSeenCount));
									answerAndRating.setAnswerSource(stackOverFlow);
									Date answerShownDate = new Date();
									answerAndRating.setTimeAnswerShown(answerShownDate);
									answerShownAndRating.add(answerAndRating);
									// add one to counter of seen answers
									userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
											.setStackAnswerSeenCounter(userSession.getQuestions()
													.get(userSession.getQuestions().size() - 1).getAnswers()
													.getStackAnswerSeenCounter() + 1);
									// answerShown.add(answersAvailable.get(stackCount));
									// if there is no Shown Answers yet set
									// them
									// else add an item to it
									if (userSession.getQuestions().get(userSession.getQuestions().size() - 1)
											.getAnswers().getAnswerAndRating().size() == 0) {
										userSession.getQuestions().get(userSession.getQuestions().size() - 1)
												.getAnswers().setAnswerAndRating(answerShownAndRating);
									} else {
										userSession.getQuestions().get(userSession.getQuestions().size() - 1)
												.getAnswers()
												.addtoAnswerAndRating(answersAvailable.get(stackSeenCount));

									}
								} // end else
							} // end if
						} // end inner for

					} // end first if
					if (messageContent.toLowerCase().equals(redditPreference)) {

						userSession.getTimer().cancel();
						Timer lastComms = new Timer();
						userSession.setTimer(lastComms);
						Timer timr = userSession.getTimer();
						timr.schedule(timeTask, timerTimeOut);

						List<Question> questions = userSession.getQuestions();
						Answer answers = questions.get(questions.size() - 1).getAnswers();

						List<SourceAndAnswers> sourceAndAns = answers.getAnswerAvailable();
						List<AnswerAndRating> answerShownAndRating = new ArrayList<AnswerAndRating>();

						int redditSeenCounter = answers.getRedditAnswerSeenCounter();

						for (int k = 0; k < sourceAndAns.size(); k++) {

							if (sourceAndAns.get(k).getSource().equals(reddit)) {

								// answersAvailable contain the answers that
								// are available for the source of WikiPedia
								List<String> answersAvailable = new ArrayList<String>();
								answersAvailable = sourceAndAns.get(k).getAnswers();

								if (redditSeenCounter >= answersAvailable.size()) {
									answerReturn = "Hey " + userSession.getUser().getUserName()
											+ ". You have seen all the answers from " + reddit
											+ ". If you like you can see the answers from Wikipedia (type w) or "
											+ "from StackOverflow (types s)";

								} else {

									// need it to show answer at pos 0 then
									// at
									// pos 1 then at pos 2
									answerReturn = "Hey " + userSession.getUser().getUserName()
											+ ". Here's the first answer I found from " + reddit + " "
											+ answersAvailable.get(redditSeenCounter) + "\n type " + redditPreference
											+ " if you wish to see the next answer from Reddit or "
											+ "type other source type from my previous message."
											+ "\n\n Please let me know if this was helpful (y/n).";

									AnswerAndRating answerAndRating = new AnswerAndRating();
									answerAndRating.setAnswerShown(answersAvailable.get(redditSeenCounter));
									answerAndRating.setAnswerSource(reddit);
									Date answerShownDate = new Date();
									answerAndRating.setTimeAnswerShown(answerShownDate);
									answerShownAndRating.add(answerAndRating);
									userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
											.setRedditAnswerSeenCounter(userSession.getQuestions()
													.get(userSession.getQuestions().size() - 1).getAnswers()
													.getRedditAnswerSeenCounter() + 1);

									// answerShown.add(answersAvailable.get(redditCount));
									// if there is no Shown Answers yet set
									// them
									// else add an item to it
									if (userSession.getQuestions().get(userSession.getQuestions().size() - 1)
											.getAnswers().getAnswerAndRating().size() == 0) {
										userSession.getQuestions().get(userSession.getQuestions().size() - 1)
												.getAnswers().setAnswerAndRating(answerShownAndRating);
									} else {
										userSession.getQuestions().get(userSession.getQuestions().size() - 1)
												.getAnswers()
												.addtoAnswerAndRating(answersAvailable.get(redditSeenCounter));

									}
								} // end else
							} // end if
						} // end inner for

					} // end first if
					if (messageContent.toLowerCase().equals("no") && userSession.isCanEndSession()) {
						userSession.getTimer().cancel();
						Date endSession = new Date();
						userSession.setSessionEnd(endSession);
						// turn to json and send to mongoDB
						String jsonSession = userSession.toJayson(userSession);
						MongoConnection mongo = new MongoConnection();
						mongo.connectToDB(jsonSession);
						// send to session create json object for knowledge base
						// and insert it into knowledgeBase

						// userSession.elasticSession(userSession);

						seshOn.put(messageSender.getId(), null);
						answerReturn = "Ok " + messageSender.getUserName()
								+ ". Ask another question to start a new session";

					}

					if (messageContent.toLowerCase().equals("yes") && userSession.isCanEndSession()) {
						answerReturn = "Ok " + messageSender.getUserName() + ". So how can I help you :) ?";
						userSession.setCanEndSession(false);
						userSession.getTimer().cancel();
						Timer lastComms = new Timer();
						userSession.setTimer(lastComms);
						Timer timr = userSession.getTimer();
						timr.schedule(timeTask, timerTimeOut);

					}

					// if message content = yes and they have seen an answer
					// then set answer rating for
					if ((messageContent.toLowerCase().equals("y") || messageContent.toLowerCase().equals("n"))
							&& userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
									.getAnswerAndRating().size() != 0) {

						System.out.println("Added Rating maybe");
						userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
								.getAnswerAndRating()
								.get(userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
										.getAnswerAndRating().size() - 1)
								.setAnswerRating(messageContent);
						// store the time the user gave the rating
						Date ratingTime = new Date();
						userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
								.getAnswerAndRating()
								.get(userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
										.getAnswerAndRating().size() - 1)
								.setTimeRatingGiven(ratingTime);
						if (messageContent.toLowerCase().equals("n")) {
							userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
									.getAnswerAndRating()
									.get(userSession.getQuestions().get(userSession.getQuestions().size() - 1)
											.getAnswers().getAnswerAndRating().size() - 1)
									.decrementAnswerScore();
						}
						if (messageContent.toLowerCase().equals("y")) {
							userSession.getQuestions().get(userSession.getQuestions().size() - 1).getAnswers()
									.getAnswerAndRating()
									.get(userSession.getQuestions().get(userSession.getQuestions().size() - 1)
											.getAnswers().getAnswerAndRating().size() - 1)
									.incrementAnswerScore();
						}

						answerReturn = " Thank you for you're feed back. This will allow me to become more intelligent and eventually give you better answers.";

					}

					if (channelOnWhichMessageWasPosted.startsWith("D") && hasSentMessage == 0) {
						sending.sendDirectMessageToAUser(session, answerReturn, messageSender.getUserName());

					}
					// send it back to channel
					else if (hasSentMessage == 0) {
						// send Wiki message
						sending.sendMessageToAChannel(session, answerReturn, channelOnWhichMessageWasPosted);

					}
				} // end if not bot
			}

		};

		// add it to the session
		session.addMessagePostedListener(messagePostedListener);

	}

}