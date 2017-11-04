// BUGS: Sometimes the replies to the tweet are not among the 100 replies. Thus, tweets are sometimes missing from the program when they exist in real life. We need to either a) come up with a better
// tweet-finding algorithm or look at more results. This is the biggest issue, that needs to be fixed ASAP
// PROGRESS: identify the first few tweets of a user, get some (but not all) of the replies, send them to the driver where they can be analyzed
// NOTSURE: filters: set up and added to, not sure if they work fully or not
// TODO: fix issue regarding shortened tweets
// TODO: add mixed/recent/popular filters

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder; //not sure why we have to add this specifically, but it only worked after we added this line

public class UserTweets //used to get data from a user. Can be expanded for more features.
{
	private Twitter myTwitter;
	private String myUser;
	
	//filter variables:
	private boolean noRetweets = false; //noRetweets filter set off by default
	private boolean noMedia = false; //noMedia filter set off by default
	private boolean noLinks = false; //noLinks filter set off by default
	private String dateUntil = null; //set to null for no data restrictions by dateSince, in the format year-month-day
	//more to be added

	public UserTweets(String userName) throws TwitterException, IOException //constructor
	{ 
	    ConfigurationBuilder cb = new ConfigurationBuilder(); //code to authorize @P2A_LiRa 
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("Akrbil1kDfds7smxklHJqBDFI")
		.setOAuthConsumerSecret("WfiIdQZiAH9D1GDT1oODUejIq6WGH2HHvb7aTx5R8UarrDL6wD")
		.setOAuthAccessToken("879695567697448960-6HaZs6ghyPJLseiHUeCxH2NhgQf3Ady")
		.setOAuthAccessTokenSecret("vRU898HftZytViaQ3pAVAa6FnLKesC1kUZtdhFMBdJFRu");
		myTwitter = new TwitterFactory(cb.build()).getInstance(); //required in Twitter4J
	    myUser = userName; //it works!
	} 	
	public String getUsername() //returns userName of UserTweets class
    {
    	return myUser;
    }
	public ArrayList<String> returnReplies(int n) throws TwitterException, IndexOutOfBoundsException //returns the top replies to the nth tweet from user userName, where n starts at 0
    {
		try
		{
	    	if(-1 >= n || n >= 20) //limit of 20 for getUserTimeline()
	    		throw new IndexOutOfBoundsException(); //prevent errors from occurring
		}
		catch (IndexOutOfBoundsException e)
		{
			System.err.println("Error: n is outside the scope of the current timeline!");
		}
    	ArrayList<String> toReturn = new ArrayList<String>(); //an ArrayList<String> of raw tweets for Watson to analyze
    	Paging paging = new Paging(1, 20); //pagination could be useful later on for showing pages
    	List<Status> topTweets = myTwitter.getUserTimeline(myUser, paging); //this returns the top 20 tweets of a user
    	Status thisTweet = topTweets.get(n); //the tweet that we will be looking at replies to
    	long statusID = thisTweet.getId(); //this is the ID of the tweet, given by Twitter. 
    	System.out.printf("%-5s %-20s %-250s\n", "#:", "User:"," ||| " + thisTweet.getText().replaceAll("[\n\r]", "")); //formatting for intro text
    	System.out.printf("%-5s %-20s %-250s\n", "--", "----------", " ||| ----------"); //prints out a line for spacing
    	Query q = new Query("to:" + myUser + " since_id:" + statusID); //not sure if this works all of the time. Consult Luke. This is supposed to check the tweets with replies for a certain user
    	q.setCount(100); //sets count to 100, need to increase. Maximum of 450 calls every 15 minutes
    	queryRestrict(q); //filters out data based on information provided
    	QueryResult r = myTwitter.search(q); //T4J formatting
    	int count = 0; //int to count number of replies (not sure how to find total number of replies to any given tweet)
    	int statusCount = 0; //int to count total number of statuses viewed
    	for(Status s : r.getTweets()) //runs through statuses to filter out correct tweets
    	{
    		statusCount++; //int for debugging
    		if(s.getInReplyToStatusId() == statusID) //verifies correct response
    		{
    			count++; //useful for printing out data
    			String text = s.getText().replaceAll("[\n\r]", ""); //filters out potential empty space
    			StringBuilder sb = new StringBuilder(""); //set up new StringBuilder to filter out words with '@'
    			String[] sbSplit = text.split(" "); //separate text into Strings
    			for(int i = 0; i < sbSplit.length; i++) //loop through String[] of words
    			{
    				if((sbSplit[i].length() > 0) && (sbSplit[i].charAt(0) != '@')) //find out whether word starts with '@'
    					sb.append(sbSplit[i] + " "); //if it doesn't, append to StringBuilder
    			}
    			text = sb.toString();
    			if(text.contains("… https://t.co/")) //this tweet is not complete; JSON call with tweet_mode=extended required
    			{
    				//System.out.print("*");
    				//long sID = s.getId();
    				//ObjectMapper map = new ObjectMapper();
    				//ETF etf = map.readValue(new URL("https://api.twitter.com/1.1/statuses/show.json?id=" + sID), ETF.class);
    			}
    			toReturn.add(text); //adds tweets to be sent through Watson
    	    	System.out.printf("%-5s %-20s %-250s\n",count + "", s.getUser().getScreenName(), " ||| " + text); //for debugging purposes 	

    		}
    	}
    	System.out.println("Number of statuses viewed: " + statusCount); //never reached
    	return toReturn; //returns the ArrayList<String> of replies to the specific tweet we're looking at
    }
		
	//filters to limit data collection that make code more efficient and accurate, as the client requests	
	
    public void queryRestrict(Query q) //runs through all of the filters and restricts query appropriately
    {
    	if(noRetweets == true)
    	{
    		q.setQuery(q.getQuery() + " -filter:retweets");
    	}
    	if(noMedia == true)
    	{
    		q.setQuery(q.getQuery() + " -filter:media");
    	}
    	if(noLinks == true)
    	{
    		q.setQuery(q.getQuery() + " -filter:links");
    	}
    	if(dateUntil != null)
    	{
    		q.setQuery(q.getQuery() + " until:" + dateUntil);  		    		
    	}
    }
    public void noRetweetsSetter(boolean tf) //set noRetweets filter on. One of the filter options we will expand upon in the future
    {
    	noRetweets = tf;
    }
    public void noMediaSetter(boolean tf) //set noMedia filter on
    {
    	noMedia = tf;
    }
    public void noLinksSetter(boolean tf) //set noRetweets filter on
    {
    	noLinks = tf;
    }
    public void dateUntilSetter(int year, int month, int day) //set the dateSince filter
    {
    	dateUntil = year + "-" + month + "-" + day;  	
    }
}

class ETF
{
	private String jsonData;
	
	public String getExtendedTweet()
	{
		jsonData = jsonData.replace("\n", " ").replace("\r", " ");
		int start = jsonData.indexOf("\"full_text\": \"") + 13;
		int end = jsonData.indexOf("\", \"truncated\": ");
		return jsonData.substring(start, end);		
	}
}


