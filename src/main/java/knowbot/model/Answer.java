package knowbot.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Answer {

	private List<AnswerAndRating> answerAndRating = new ArrayList<AnswerAndRating>();

	// the available answers and their source e.g StackOverFlow x,y,z
	private List<SourceAndAnswers> answerAvailable = new ArrayList<SourceAndAnswers>();
	
	private transient Integer redditAnswerSeenCounter =0; 
	private transient Integer stackAnswerSeenCounter=0;
	private transient Integer wikiAnswerSeenCounter=0;
	private transient Integer recommendedAnswerSeenCounter=0;
	
	public Answer() {
		// TODO Auto-generated constructor stub
	}


	public List<AnswerAndRating> getAnswerAndRating() {
		return answerAndRating;
	}

	public void setAnswerAndRating(List<AnswerAndRating> answerAndRating) {
		this.answerAndRating = answerAndRating;
	}

	public void addtoAnswerAndRating(String shownAnswer){
		AnswerAndRating ansAndRating = new AnswerAndRating();
		ansAndRating.setAnswerShown(shownAnswer);
		ansAndRating.setTimeAnswerShown(new Date());
		this.answerAndRating.add(ansAndRating);
	}


	public List<SourceAndAnswers> getAnswerAvailable() {
		return answerAvailable;
	}

	public void setAnswerAvailable(List<SourceAndAnswers> answerAvailable) {
		this.answerAvailable = answerAvailable;
	}
	
	public void addToAnswerAvailable(SourceAndAnswers answerAvailable) {
		this.answerAvailable.add(answerAvailable);
	}


	public Integer getRedditAnswerSeenCounter() {
		return redditAnswerSeenCounter;
	}


	public void setRedditAnswerSeenCounter(Integer redditAnswerSeenCounter) {
		this.redditAnswerSeenCounter = redditAnswerSeenCounter;
	}


	public Integer getStackAnswerSeenCounter() {
		return stackAnswerSeenCounter;
	}


	public void setStackAnswerSeenCounter(Integer stackAnswerSeenCounter) {
		this.stackAnswerSeenCounter = stackAnswerSeenCounter;
	}


	public Integer getWikiAnswerSeenCounter() {
		return wikiAnswerSeenCounter;
	}


	public void setWikiAnswerSeenCounter(Integer wikiAnswerSeenCounter) {
		this.wikiAnswerSeenCounter = wikiAnswerSeenCounter;
	}


	public Integer getRecommendedAnswerSeenCounter() {
		return recommendedAnswerSeenCounter;
	}


	public void setRecommendedAnswerSeenCounter(Integer recommendedAnswerSeenCounter) {
		this.recommendedAnswerSeenCounter = recommendedAnswerSeenCounter;
	}

	
}
