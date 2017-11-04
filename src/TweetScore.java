// BUGS: None! It works!

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.random.EmpiricalDistribution;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class TweetScore //gets the weighted Twitter score of a user
{
	private Twitter myTwitter; //required in Twitter4J
	private ArrayList<TwitterData> congressmen; //ArrayList<TwitterData> that holds recently updated info to avoid hitting rate limits
	private double[] tweetsDayTotal; //will collect the total data set for tweets/day
	private double[] followersDayTotal; //will collect the total data set for followers/day
	private double[] likesTotal; //will collect the total data set for likes
	private double[] retweetsTotal; //will collect the total data set for retweets
	
	public TweetScore() throws TwitterException, IOException //constructor
	{ 
	    ConfigurationBuilder cb = new ConfigurationBuilder(); //code to authorize @P2A_LiRa 
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("Akrbil1kDfds7smxklHJqBDFI")
		.setOAuthConsumerSecret("WfiIdQZiAH9D1GDT1oODUejIq6WGH2HHvb7aTx5R8UarrDL6wD")
		.setOAuthAccessToken("879695567697448960-6HaZs6ghyPJLseiHUeCxH2NhgQf3Ady")
		.setOAuthAccessTokenSecret("vRU898HftZytViaQ3pAVAa6FnLKesC1kUZtdhFMBdJFRu");
		myTwitter = new TwitterFactory(cb.build()).getInstance(); //required in Twitter4J
		congressmen = new ArrayList<TwitterData>(); //instantiates congressmen
		Scanner scanning = new Scanner(new File("ScoreData.txt")); //scans through the pre-collected data to save API requests
		while(scanning.hasNext()) //runs through entire text file
		{
			String scanned = scanning.next(); //data from database
			String[] s = scanned.split("\\|"); //groups data into an array
			congressmen.add(new TwitterData(s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7], s[8])); //constructor set up to easily accept input
		}
		scanning.close(); //no further scanning input required
		int s = congressmen.size(); //the size of the arrays can now be determined
		tweetsDayTotal = new double[s]; //tDT is now the right size
		followersDayTotal = new double[s]; //fDT is now the right size
		likesTotal = new double[s]; //lT is now the correct size
		retweetsTotal = new double[s]; //rT is now the correct size
		for(int index = 0; index < congressmen.size(); index++) //runs through this new size to add all the data to respective arrays
		{
			TwitterData temp = congressmen.get(index); //temporary TwitterData to get results from
			tweetsDayTotal[index] = temp.getTC()/temp.getA(); //fixes tDT
			followersDayTotal[index] = temp.getFC()/temp.getA(); //fixes fDT
			likesTotal[index] = temp.getL(); //fixes lT
			retweetsTotal[index] = temp.getR(); //fixes rT
		}
	}
	
	public TwitterData getUser(String userName) throws TwitterException//returns the user's TwitterData
	{
		TwitterData toReturn;
		int found = -1; //dummy int to get index of potential userName object
		int counting = 0; //counting int to get indexOf (inefficient)
		for(TwitterData t : congressmen) //runs through prefilled congressmen ArrayList<TwitterData>
		{
			if(userName.equalsIgnoreCase(t.toString())) //checks whether the userName matches, broken
			{
				found = counting; //sets found equal to indexOf
				break; //no scanning left to do, saves time
			}
			counting++;
		}
		if(found != -1) //the user inputs a congressman whose data we have on file
		{
			System.out.println("This Congressman has been located in our database."); //for debugging purposes
			toReturn = congressmen.get(found); //the full TwitterData for the congressman we're looking at
		}
		else //the user inputs another userName whose data we don't have on file
		{
			System.out.println("This user has not been located in our database."); //for debugging purposes
			User u = myTwitter.showUser(userName); //gets the user's profile
			int tempLikes = 0; //temp int to count past 20 tweets' likes total
			int tempRetweets = 0; //temp int to count past 20 tweets' likes total
			List<Status> tweeted = myTwitter.getUserTimeline(userName); //collects the top 20 tweets on the user timeline
			for(Status s: tweeted) //runs through statuses
			{
				tempLikes += s.getFavoriteCount(); //increments likes
				tempRetweets += s.getRetweetCount(); //increments retweets
			}
			toReturn = new TwitterData(userName, Integer.parseInt("" + (((new Date()).getTime() - u.getCreatedAt().getTime())/86400000)), u.getStatusesCount(), u.getFollowersCount(), u.isVerified(), u.isDefaultProfileImage(), u.isDefaultProfile(), tempLikes, tempRetweets);
		}
		return toReturn;
	}
	public String[] score(String userName) throws TwitterException, MathIllegalArgumentException, OutOfRangeException //returns the score, and a bunch of other data about what the score means.
	{
		int tweetsDay; //the user's tweets per day
		int followersDay; //the user's followers per day
		int likes; //the number of likes for the last 20 tweets of the user
		int retweets; //the number of retweets for the last 20 tweets of the user
		int tDS; //tweets/day score
		Double tDP; //tweets/day percentile
		int fDS; //followers/day score
		Double fDP; //followers/day percentile
		int lS; //likes score
		Double lP; //likes percentile
		int rS; //retweets score
		Double rP; //retweets percentile
		boolean ver; //verified
		boolean imD; //image default
		boolean prD; //profile default
		int score; //the score
		String[] toReturn = new String[12]; //the size of what is to be returned, hard coded
		TwitterData congressman = getUser(userName);
		System.out.println("Full Text: " + congressman.fullString()); //for debugging purposes
		tweetsDay = congressman.getTC()/congressman.getA(); //tweets per day
		followersDay = congressman.getFC()/congressman.getA(); //followers per day
		likes = congressman.getL(); //number of likes
		retweets = congressman.getR(); //number of retweets
		ver = congressman.getV(); //verified
		imD = congressman.getID(); //image default
		prD = congressman.getPD(); //profile default
		//inefficient code to check percentiles. Will be fixed later if necessary. Works sometimes, need to fix Double.NaN results
		EmpiricalDistribution tDPED = new EmpiricalDistribution(tweetsDayTotal.length);
		tDPED.load(tweetsDayTotal);
		tDP = tDPED.cumulativeProbability((double) tweetsDay);
		if(Double.isNaN(tDP))
		{
			tDP = 1.0;
		}
		toReturn[8] = "Percentile: " + tDP;
		tDS = (int) (tDP * 25);
		toReturn[4] = tDS + "/25";
		EmpiricalDistribution fDPED = new EmpiricalDistribution(followersDayTotal.length);
		fDPED.load(followersDayTotal);
		fDP = fDPED.cumulativeProbability((double) followersDay);
		if(Double.isNaN(fDP))
		{
			fDP = 1.0;
		}
		toReturn[9] = "Percentile: " + fDP;
		fDS = (int) (fDP * 25);
		toReturn[5] = fDS + "/25";
		EmpiricalDistribution lPED = new EmpiricalDistribution(likesTotal.length);
		lPED.load(likesTotal);
		lP = lPED.cumulativeProbability((double) likes);
		if(Double.isNaN(lP))
		{
			lP = 1.0;
		}
		toReturn[10] = "Percentile: " + lP;
		lS = (int) (lP * 25);
		toReturn[6] = lS + "/25";
		EmpiricalDistribution rPED = new EmpiricalDistribution(retweetsTotal.length);
		rPED.load(retweetsTotal);
		rP = rPED.cumulativeProbability((double) retweets);
		if(Double.isNaN(rP))
		{
			rP = 1.0;
		}
		toReturn[11] = "Percentile: " + rP;
		rS = (int) (rP * 25);
		toReturn[7] = rS + "/25";
		score = tDS + fDS + lS + rS;
		if((!ver || imD) || prD)
		{
			if(!ver)
				score -= 20; //20 point deduction for not having a verified profile
			if(imD)
				score -= 20; //20 point deduction for default profile image
			//if(prD) //temporarily disabled, until standards from Twitter investigated
				//score -= 5; //5 point deduction for not completing profile
			toReturn[3] = "This user's profile is not complete.";
			if(score < 0)
			{
				score = 0; //no scores below 0
			}
		}
		else
		{
			toReturn[3] = "This user's profile is complete.";
		}
		toReturn[1] = "Raw Score: " + score;
		if(score >= 80)
		{
			toReturn[0] = "A";
			toReturn[2] = "This user's Twitter profile is very active.";
		}
		else if(score >= 60)
		{
			toReturn[0] = "B";
			toReturn[2] = "This user's Twitter profile is moderately active.";
		}
		else if(score >= 40)
		{
			toReturn[0] = "C";
			toReturn[2] = "This user's Twitter profile is somewhat active.";
		}
		else if(score >= 20)
		{
			toReturn[0] = "D";
			toReturn[2] = "This user's Twitter profile is not very active.";
		}
		else
		{
			toReturn[0] = "F";
			toReturn[2] = "This user's Twitter profile is inactive.";
		}
		return toReturn;
	} 
	public ArrayList<TwitterData> getC() //compatibility: returns congressmen
	{
		return congressmen;
	}
}

class TwitterData implements Comparable<TwitterData> //convenient method for storing the data from the ScoreData.txt text file.
{
	private String myUser; //the userName of the person to be scored
	private int age; //the age of the person to be scored
	private int tweetCount; //the tweet number of the person to be scored
	private int followCount; //the follow number of the person to be scored
	private boolean verified; //the verification status of the person to be scored
	private boolean imageDef; //the image default status of the person to be scored
	private boolean profDef; //the profile default status of the person to be scored
	private int likes; //the like count of the person to be scored's past 20 tweets
	private int retweets; //the retweet count of the person to be scored's past 20 tweets
	
	public TwitterData(String u, int a, int tC, int fC, boolean v, boolean iD, boolean pD, int l, int r) //raw data constructor
	{
		myUser = u;
		age = a;
		tweetCount = tC;
		followCount = fC;
		verified = v;
		imageDef = iD;
		profDef = pD;
		likes = l;
		retweets = r;
	}
	public TwitterData(String u, String a, String tC, String fC, String v, String iD, String pD, String l, String r) //String-only constructor, useful for String.split()
	{
		myUser = u;
		age = Integer.parseInt(a);
		tweetCount = Integer.parseInt(tC);
		followCount = Integer.parseInt(fC);
		verified = Boolean.parseBoolean(v);
		imageDef = Boolean.parseBoolean(iD);
		profDef = Boolean.parseBoolean(pD);
		likes = Integer.parseInt(l);
		retweets = Integer.parseInt(r);
	}
	public String fullString() //returns all the data at once in a String, useful for debugging
	{
		return myUser + " | " + age + " | " + tweetCount + " | " + followCount + " | " + verified + " | " + imageDef + " | " + profDef + " | " + likes + " | " + retweets;
	}
	public int compareTo(TwitterData other) //Comparable method
	{
		return this.myUser.compareTo(other.myUser);
	}
	public String toString() //required for Comparable, returns myUser
	{
		return myUser;
	}
	public int getA() //returns age
	{
		return age;
	}
	public int getTC() //returns tweetCount
	{
		return tweetCount;
	}
	public int getFC() //returns followCount
	{
		return followCount;
	}
	public boolean getV() //returns verified
	{
		return verified;
	}
	public boolean getID() //returns imageDef
	{
		return imageDef;
	}
	public boolean getPD() //returns profDef
	{
		return profDef;
	}
	public int getL() //returns likes
	{
		return likes;
	}
	public int getR() //returns retweets
	{
		return retweets;
	}
}