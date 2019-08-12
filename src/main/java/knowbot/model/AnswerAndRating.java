package knowbot.model;

import java.util.Date;

public class AnswerAndRating {

	private String answerShown;
	private String answerRating;
	private String answerSource;
	private Date timeAnswerShown;
	private Date timeRatingGiven;
	private long answerScore = 0;
	
	public long getAnswerScore() {
		return answerScore;
	}
	public void incrementAnswerScore() {
		this.answerScore++;
	}
	public void decrementAnswerScore() {
		this.answerScore--;
	}
	public AnswerAndRating() {
		// TODO Auto-generated constructor stub
	}
	public String getAnswerShown() {
		return answerShown;
	}
	public void setAnswerShown(String answerShown) {
		this.answerShown = answerShown;
	}
	public String getAnswerRating() {
		return answerRating;
	}
	public void setAnswerRating(String answerRating) {
		this.answerRating = answerRating;
	}
	public String getAnswerSource() {
		return answerSource;
	}
	public void setAnswerSource(String answerSource) {
		this.answerSource = answerSource;
	}
	public Date getTimeAnswerShown() {
		return timeAnswerShown;
	}
	public void setTimeAnswerShown(Date timeAnswerShown) {
		this.timeAnswerShown = timeAnswerShown;
	}
	public Date getTimeRatingGiven() {
		return timeRatingGiven;
	}
	public void setTimeRatingGiven(Date timeRatingGiven) {
		this.timeRatingGiven = timeRatingGiven;
	}
	

}
