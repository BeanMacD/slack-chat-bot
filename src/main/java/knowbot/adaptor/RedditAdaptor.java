package knowbot.adaptor;

import java.util.ArrayList;
import java.util.List;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubmissionSearchPaginator;
import sun.security.krb5.Credentials;

public class RedditAdaptor {

	// configure stuff
	public static RedditClient redditConfig() throws NetworkException, OAuthException {
		UserAgent myUserAgent = UserAgent.of("desktop", "com.recommenderx.feedmix", "v0.1", "recommenderx");
		RedditClient redditClient = new RedditClient(myUserAgent);
		Credentials credentials = Credentials.script("recommenderx", "recommenderx", "_VJqECXJ4-npGg",
				"t-fS4CraC5nyB72qIMBfimYgHOM");
		OAuthData authData = redditClient.getOAuthHelper().easyAuth(credentials);
		System.out.println("Reddit config 1");

		redditClient.authenticate(authData);
		System.out.println("Reddit config 2");

		return redditClient;
	}

	// search through reddit
	public List<String> getRedditUrl(String query, Integer numOfReturnedUrls) {
		// reddit configuration
		RedditClient redditclient = null;
		
		String redditUrl = "http://www.reddit.com";
		String errorMessage = "I could not find any answers on Reddit for the question you have asked. Please try again";
		List<String> urls = new ArrayList<String>();
		try {
			redditclient = RedditAdaptor.redditConfig();
		} catch (NetworkException e) {
			
			e.printStackTrace();
		} catch (OAuthException e) {
			
			e.printStackTrace();
		}
		
		SubmissionSearchPaginator ssp = new SubmissionSearchPaginator(redditclient, query);
		System.out.println("Searched for stuff there on Reddit");
		
		
		Listing<Submission> posts = ssp.next();
		
		if (posts.size() < numOfReturnedUrls) {
			numOfReturnedUrls = posts.size();
		}

		if (posts.isEmpty()) {
			urls.add(errorMessage);
			System.out.println("Reddit is empty posts");
			return null;
		}
		else {
			for (int i = 0; i < numOfReturnedUrls; i++) {
				String urlEnding = posts.get(i).getPermalink();
				urls.add(redditUrl + urlEnding);

			} // end for

			if (urls.isEmpty()) {
				System.out.println("Url stuff inside of reddit be null");
				return null;
			} // end if return message is empty

			return urls;
		} // end else
	}

}
