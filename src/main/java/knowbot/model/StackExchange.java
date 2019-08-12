package knowbot.model;

public class StackExchange {

	private long questionId;
	private String questionContent;
	public StackExchange() {
		// TODO Auto-generated constructor stub
	}
	public StackExchange(long questionId,String qContent) {
		// TODO Auto-generated constructor stub
	}
	public long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}
	public String getQuestionContent() {
		return questionContent;
	}
	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

}
