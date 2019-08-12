package knowbot.model;

import java.util.ArrayList;
import java.util.List;

public class SourceAndAnswers {

	private String source;
	private List<String> answers = new ArrayList<String>();
	
	public SourceAndAnswers() {
		// TODO Auto-generated constructor stub
	}

	

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

}
