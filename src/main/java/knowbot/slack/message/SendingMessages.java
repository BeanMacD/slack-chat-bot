package knowbot.slack.message;

import java.util.List;

import org.json.JSONObject;

import com.ullink.slack.simpleslackapi.*;
import com.ullink.slack.simpleslackapi.replies.SlackChannelReply;

import knowbot.adaptor.WikiQuery;

/**
 * This sample code is showing how to send some messages assuming you already have a SlackSession
 */
public class SendingMessages
{

    /**
     * This method shows how to send a message to a given channel (public channel, private group or direct message channel)
     */
    public void sendMessageToAChannel(SlackSession session,String message,String ChannelName)
    {
    	//System.out.println(obj);
		//get a channel
        SlackChannel channel = session.findChannelByName(ChannelName);
        session.sendMessage(channel, message);
    }

    /**
     * This method shows how to send a direct message to a user
     */
    public void sendDirectMessageToAUser(SlackSession session,String message, String userName)
    {
//    	WikiQuery wikiQ = new WikiQuery();
//		List<String> titles;
//		titles = wikiQ.getWikiPageTitles("What is Java");

		//String message = wikiQ.returnPage(titles);
        //get a user
        SlackUser user = session.findUserByUserName(userName);

        session.sendMessageToUser(user,message, null);

    }

    /**
     * This method shows how to send a direct message to a user, but this time it shows how it can be done using the
     * direct message channels
     */
    public void sendDirectMessageToAUserTheHardWay(SlackSession session)
    {

        //get a user
        SlackUser user = session.findUserByUserName("killroy");

        //get its direct message channel
        SlackMessageHandle<SlackChannelReply> reply = session.openDirectMessageChannel(user);

        //get the channel
        SlackChannel channel = reply.getReply().getSlackChannel();

        //send the message to this channel
        session.sendMessage(channel, "Hi, how are you", null);
    }

    /**
     * This method shows how to send a direct message to multiple users.
     */
    public void sendDirectMessageToMultipleUsers(SlackSession session)
    {

        //get some users
        SlackUser killroy = session.findUserByUserName("killroy");
        SlackUser janedoe = session.findUserByUserName("janedoe");
        SlackUser agentsmith = session.findUserByUserName("agentsmith");

        //open a multiparty direct message channel between the bot and these users
        SlackMessageHandle<SlackChannelReply> reply = session.openMultipartyDirectMessageChannel(killroy, janedoe, agentsmith);

        //get the channel
        SlackChannel channel = reply.getReply().getSlackChannel();

        //send the message to this channel
        session.sendMessage(channel, "Hi, how are you guys", null);
    }

    /**
     * This method shows how to send a message using the PreparedMessage builder (allows for multiple attachments)
     */
    public void sendUsingPreparedMessage(SlackSession session)
    {
        //get a channel
        SlackChannel channel = session.findChannelByName("achannel");

        //build a message object
        SlackPreparedMessage preparedMessage = new SlackPreparedMessage.Builder()
                .withMessage("Hey, this is a message")
                .withUnfurl(false)
                .addAttachment(new SlackAttachment())
                .addAttachment(new SlackAttachment())
                .build();

        session.sendMessage(channel, preparedMessage);
    }
}
