package knowbot.model;

import java.util.Date;
import java.util.UUID;

public class Question {

	private String question;
	private UUID questionId = UUID.randomUUID();
	private Date questionTime = new Date();
	private String query;
	private transient boolean lookUpOtherSources = false;
	

	// Source And answers contains a question And then a source and its list of
	private Answer answer = new Answer();
	// Answers
	
//	List<Answer>;
	//answer Source pref goes here
	
	
	public Question() {
		// TODO Auto-generated constructor stub
	}
	
	public Date getQuestionTime() {
		return questionTime;
	}

	public void setQuestionTime(Date questionTime) {
		this.questionTime = questionTime;
	}

	public UUID getQuestionId() {
		return questionId;
	}

	public void setQuestionId(UUID questionId) {
		this.questionId = questionId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean isLookUpOtherSources() {
		return lookUpOtherSources;
	}

	public void setLookUpOtherSources(boolean lookUpOtherSources) {
		this.lookUpOtherSources = lookUpOtherSources;
	}
	
	public Answer getAnswers() {
		return answer;
	}

	public void setAnswers(Answer answer) {
		this.answer = answer;
	}

	
	

}
