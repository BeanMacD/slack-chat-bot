package knowbot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "TechWords")
public class TechWords {

	private Integer techId;
	private String techWord;
	private Integer count;

	public TechWords() {
		// TODO Auto-generated constructor stub
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Integer getTechId() {
		return techId;
	}

	public void setTechId(Integer techId) {
		this.techId = techId;
	}

	@Column(name = "word")
	public String getTechWord() {
		return techWord;
	}

	public void setTechWord(String techWord) {
		this.techWord = techWord;
	}
	
	@Column(name = "timesMentioned")
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
