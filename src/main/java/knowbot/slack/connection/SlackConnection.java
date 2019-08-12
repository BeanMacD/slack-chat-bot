package knowbot.slack.connection;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;
import java.io.IOException;

public class SlackConnection {

	public SlackConnection() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	/**
	 * This sample code is creating a Slack session and is connecting to slack. To get some more details on
	 * how to get a token, please have a look here : https://api.slack.com/bot-users
	 * @throws IOException 
	 */

	    public SlackSession connecttobot() throws IOException
	    {
	    	// RecommenderX bot code xoxb-160001715504-CIezxkj6tQeGFylOhaw2dJ1l
	    	//Aficionado Team bot code xoxb-133016753956-0Xji31gn8RGl6ibQRb7e1VBW
	    	//this takes in the bot user token as its arguement 
	        SlackSession session = SlackSessionFactory.createWebSocketSlackSession("xoxb-160001715504-CIezxkj6tQeGFylOhaw2dJ1l");
	        session.connect();
	      
	        System.out.println("You have connected");
	        
	        return session;
	        
	    }
	}


