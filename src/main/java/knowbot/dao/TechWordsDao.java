package knowbot.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import knowbot.model.TechWords;

public class TechWordsDao extends BaseDao {

	public TechWordsDao() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public List<TechWords> getAllTechWords() {

		String query = "select t from TechWords t";
		TypedQuery<TechWords> q;
		q = session.createQuery(query);

		return q.getResultList();

	}
	@SuppressWarnings("unchecked")
	public List<TechWords> getTechWord(String word) {

		String query = "select t from TechWords t where t.techWord like '" + word + "'";
		TypedQuery<TechWords> q;
		q = session.createQuery(query);

		return q.getResultList();

	}
	public void insertTechWords(TechWords techWords) {
		session.save(techWords);
	}
	
	public void updateCount(TechWords techWords){
//		techWords = (TechWords)session.get(TechWords.class, techWords.getTechId());
//		techWords.setCount(techWords.getCount()+1);
//		session.update(techWords); 
		
		long count = techWords.getCount() + 1;
		
		String update = "UPDATE TechWords t set t.count = " + count +  " WHERE t.techId = " + techWords.getTechId();
		session.createQuery(update).executeUpdate();	
		
	}
	
}
