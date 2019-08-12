package knowbot.languageprocess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class QueryProcess {

	public static MaxentTagger tagger = init();

	public static MaxentTagger init() {
		try { // /home/podge/knowbot/bidirectional-distsim-wsj-0-18.tagger
			return new MaxentTagger("src/main/resources/bidirectional-distsim-wsj-0-18.tagger");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;
	}

	public QueryProcess() {
	}

	public String decipherQuestion(String messageContent, String user) {
		String query = "";
		System.out.println("decipherQuestion bro");
		// bfdd is only a placeholder string if set to "" i couldnt check if it
		// was contained in the message Content
		// see if(messageContent.contains("?"))
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution

		String questionWords = "bfdd";

		String tagPrep = messageContent.replaceAll("\\W", " ");
		System.out.println("Tagging shite bbroooo");
		String tagged = tagger.tagTokenizedString(tagPrep);
		System.out.println("She be tagged up!!!!");
		String[] words = tagged.split(" ");
		
		/*
		 * @Ben
		 * Is this where splitting words into entities and intents occurs?
		 */

		for (int i = 0; i < words.length; i++) {

			// adjective
			if (words[i].substring(words[i].lastIndexOf("_") + 1).startsWith("J")) {
				query += words[i].split("_")[0] + " ";
			}
			if (words[i].substring(words[i].lastIndexOf("_") + 1).startsWith("VBG")) {
				query += words[i].split("_")[0] + " ";
			}
			// gets all nouns from sentence if the tag starts with N its a
			// noun
			if (words[i].substring(words[i].lastIndexOf("_") + 1).startsWith("N")) {
				query += words[i].split("_")[0] + " ";
			}
			// pulls the question words out
			if (words[i].substring(words[i].lastIndexOf("_") + 1).startsWith("W")) {
				questionWords = words[i].split("_")[0];
			}
			// pulls the verb words out
			// if (words[i].substring(words[i].lastIndexOf("_") +
			// 1).startsWith("V")) {
			// query += words[i].split("_")[0] + " ";
			// }

		}

		// if no question word in content question word will = bfdd
		if (messageContent.contains("?") || messageContent.contains(questionWords)) {

			System.out.println("This is counted as a Question");
			// adds a descriptor to the query e.g Location etc
			query += QuestionLookUp.lookUp(questionWords);

			if (questionWords.toLowerCase().equals("how") || questionWords.toLowerCase().equals("why")) {

				return messageContent;
			}

			// if empty use the message content
			if (query.equals("")) {
				return messageContent;
			}
			System.out.println("Query is " + query);
			return query;

		} // end of if Question condition

		// if not a question it will return blank !!!
		return "";
	}

	public String isGreeting(String messageContent, String user) {

		String returnedAnswer = "";
		BufferedReader in;
		String lines;
		List<String> greetings = new ArrayList<String>();

		// messageContent has a question word

		// read in list of greetings from file
		try { // /home/podge/knowbot/greetings.txt
			in = new BufferedReader(new FileReader("greetings.txt"));

			while ((lines = in.readLine()) != null) {
				greetings.add(lines);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("The message Content is " + messageContent);
		for (int i = 0; i < greetings.size(); i++) {

			
			if (messageContent.toLowerCase().contains(greetings.get(i))
					&& messageContent.toLowerCase().contains("<@u4q01m1eu>")) {
				return "Hello " + user + " I am a QnA service. Ask me a question to get started!!";
			}
		}

		// if new member joins
		if (messageContent.toLowerCase().contains(" has joined the channel")) {
			returnedAnswer = "Welcome " + user + "! Im KnowBot. At the moment I"
					+ " can search Wikipedia, StackOverflow and Reddit for you ! "
					+ "go easy though I'm only in developement :)";
		} // if user leaves
		else if (messageContent.toLowerCase().contains("has left the channel")) {
			returnedAnswer = "See ya later " + user + "! You'll give me a call will ya. Niiiooocccceeeee waaaannn";
		}
		// read in greetings file and check if each one matches message content
		else {
			returnedAnswer = "bfdd";
		}

		System.out.println("The returned answer is " + returnedAnswer);
		return returnedAnswer;
	}

}
