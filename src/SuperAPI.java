// BUGS: none currently (all in UserTweets and TweetScore classes)
// PROGRESS: successfully makes new UserTweets variable, prints out to file, gets some of the tweets
// TODO: basic GUI using JFrame

import java.util.*;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;

import java.io.*;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class SuperAPI
{
	private Twitter myTwitter;
	private String un;
	private UserTweets ut;
	private TweetScore ts;
	
	public SuperAPI(String userName) throws TwitterException, IOException //constructor
	{
		ConfigurationBuilder cb = new ConfigurationBuilder(); //code to authorize @P2A_LiRa 
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("Akrbil1kDfds7smxklHJqBDFI")
		.setOAuthConsumerSecret("WfiIdQZiAH9D1GDT1oODUejIq6WGH2HHvb7aTx5R8UarrDL6wD")
		.setOAuthAccessToken("879695567697448960-6HaZs6ghyPJLseiHUeCxH2NhgQf3Ady")
		.setOAuthAccessTokenSecret("vRU898HftZytViaQ3pAVAa6FnLKesC1kUZtdhFMBdJFRu");
		myTwitter = new TwitterFactory(cb.build()).getInstance(); //required in Twitter4J
		un = userName;
		ut = new UserTweets(userName);
		ts = new TweetScore();
	}
	
	public int getFollowCount() throws TwitterException //returns the number of followers of the specified user
	{
		return myTwitter.showUser(un).getFollowersCount();
	}
	public String[][] getTwentyTweets() throws TwitterException //returns the necessary data for the last 20 Tweets. Format: [TweetText][Likes][Retweets] x 20
	{
		String[][] toReturn = new String[20][3];
		int count = 0;
		for(Status s : myTwitter.getUserTimeline())
		{
			toReturn[count][0] = s.getText().replaceAll("[\n\r]", "");
			toReturn[count][1] = s.getFavoriteCount() + "";
			toReturn[count][2] = s.getRetweetCount() + "";
			count++;
		}
		return toReturn;
	}
	public String[] analyze(int n) throws UnsupportedEncodingException, FileNotFoundException, IndexOutOfBoundsException, TwitterException, IOException //code to analyze the nth Tweet on the user's Timeline
	{
		String[] toReturn = new String[13];
	    PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("TwitterResponses.txt"), "UTF-8"));
	    for(String s : ut.returnReplies(n))
	    {
	    	writer.println(s);
	    }
	    writer.close();
	    String command = "python /Users/fellowliamrathke/Documents/workspace/Project/DataPasserWatson.py";
	    Process p = Runtime.getRuntime().exec(command);
		Scanner scan = new Scanner(new File("combined_data_report.txt"));
		for(int i = 0; i < 13; i++)
		{
			toReturn[i] = scan.nextLine();
		}
		scan.close();
	    return toReturn;
	}
	public String[] scoreUser() throws OutOfRangeException, MathIllegalArgumentException, TwitterException //code to score user, based on format
	{
		return ts.score(un);
	}
}