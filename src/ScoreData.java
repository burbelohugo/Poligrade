// DESCRIPTION: Code to run through all members of Congress/government branches to collect data to get averages, etc... for score collection. This is to normalize data in the scoring aspect of our program

// BUGS: None, works as intended

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder; //not sure why we have to add this specifically, but it only worked after we added this line

public class ScoreData //used to get data from a user. Can be expanded for more features.
{
	public static void main(String []args) throws TwitterException, IOException, InterruptedException
	{  
		ConfigurationBuilder cb = new ConfigurationBuilder(); //code to authorize @P2A_LiRa 
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("Akrbil1kDfds7smxklHJqBDFI")
		.setOAuthConsumerSecret("WfiIdQZiAH9D1GDT1oODUejIq6WGH2HHvb7aTx5R8UarrDL6wD")
		.setOAuthAccessToken("879695567697448960-6HaZs6ghyPJLseiHUeCxH2NhgQf3Ady")
		.setOAuthAccessTokenSecret("vRU898HftZytViaQ3pAVAa6FnLKesC1kUZtdhFMBdJFRu");
		Twitter myTwitter = new TwitterFactory(cb.build()).getInstance(); //required in Twitter4J
	    
		System.out.println("START");
		System.out.println("");
		long startTime = (new Date()).getTime();
		long cursor = -1; //for some reason this is required
		IDs ids = myTwitter.getFriendsIDs(cursor); //gets the friend IDs of @P2A_LiRa
	    PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("ScoreData.txt"), "UTF-8")); //outputs to Data.txt for Excel conversion
		long[] idData = ids.getIDs();
		System.out.println("ID Size: " + idData.length);
		System.out.println("");
		System.out.println("Username" + "|" + "Age" + "|" + "Twt. Count" + "|" + "Flw. Count" + "|" + "Verified" + "|" + "ImageDef" + "|" + "Prof. Def" + "|" + "Likes" + "|" + "Retweets");
		for(long id : idData)
		{
			User u = myTwitter.showUser(id); //makes new user based on long 
		    int days = Integer.parseInt("" + ((startTime - u.getCreatedAt().getTime())/86400000)); //the number of days the account has existed
			int tweetsCount = u.getStatusesCount(); //the number of tweets of the user
			long followersCount = u.getFollowersCount(); //the number of followers of the user
			boolean verified = u.isVerified(); //whether or not the user is verified
			boolean defaultProfileImage = u.isDefaultProfileImage(); //whether or not the user has the default profile image
			boolean defaultProfile = u.isDefaultProfile(); //whether or not the user has the default profile (general)
			long likes = 0; //counts the number of likes of the top 20 tweets on the user's timeline (most recent)
			long retweets = 0; //counts the number of retweets of the top 20 tweets on the user's timeline
			List<Status> tweets = myTwitter.getUserTimeline(id); //collects the top 20 tweets on the user timeline
				for(Status s: tweets)
				{
					if(!s.isRetweet())
					{
						likes += s.getFavoriteCount(); //increments likes
						retweets += s.getRetweetCount(); //increments retweets
					}
				}
			System.out.println(u.getScreenName() + "|" + days + "|" + tweetsCount + "|" + followersCount + "|" + verified + "|" + defaultProfileImage + "|" + defaultProfile + "|" + likes + "|" + retweets);
			writer.println(u.getScreenName() + "|" + days + "|" + tweetsCount + "|" + followersCount + "|" + verified + "|" + defaultProfileImage + "|" + defaultProfile + "|" + likes + "|" + retweets);
		}
		writer.close();
		System.out.println("");
		long endTime = (new Date()).getTime();
		System.out.println("END. This test took " + (endTime - startTime) + "ms to complete.");
	}
}

