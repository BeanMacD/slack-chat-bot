package knowbot.languageprocess;

public class QuestionLookUp {

	public QuestionLookUp() {
		// TODO Auto-generated constructor stub
	}
	
	public static String lookUp(String questionWord){
		String addToQuery = "";
		
		 if(questionWord.toLowerCase().equals("what")){
			 //what noun of question
			 //return nothing use just nouns for now 
			 
		 }if(questionWord.toLowerCase().equals("when")){
			 addToQuery = "date";
			 
		 }if(questionWord.toLowerCase().equals("where")){
			 addToQuery = "location";
			 //send to google maps (couple of versions on)
			 
		 }if(questionWord.toLowerCase().equals("how")){
			 addToQuery = "";
			 //ask whole question 
		 }if(questionWord.toLowerCase().equals("who")){
			 addToQuery = "person";
		 }if(questionWord.toLowerCase().equals("which")){
			 addToQuery = "";
			 //not sure what would work here 
		 }if(questionWord.toLowerCase().equals("why")){
			 addToQuery = "";
			 //maybe addToQuery = reason or whole question
		 }
		 
		 //if for some reason it doesnt match any of the above it will pass back ""
		return addToQuery;
		
	}

}
