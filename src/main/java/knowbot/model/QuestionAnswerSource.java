package knowbot.model;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnswerSource {

	private String question;
	private List<SourceAndAnswers> answersAndSource = new ArrayList<SourceAndAnswers>();
	public QuestionAnswerSource() {
		// TODO Auto-generated constructor stub
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public List<SourceAndAnswers> getAnswersAndSource() {
		return answersAndSource;
	}
	public void setAnswersAndSource(List<SourceAndAnswers> answersAndSource) {
		this.answersAndSource = answersAndSource;
	}

}
