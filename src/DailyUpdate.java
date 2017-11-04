// Code to track Tweet, Follower, Like, and Retweet changes of Congressional Twitter accounts. Keep running, the text file will update every certain amount of time.
// Outputs the results every day to "DailyResults.txt". Access this file with your program/manually to collect the results.

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.random.EmpiricalDistribution;

import twitter4j.IDs;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class DailyUpdate
{
	public static void main(String []args) throws TwitterException, IOException, InterruptedException, FileNotFoundException
	{
		int waitTime; //the amount of time in milliseconds between each test
		
	    Scanner scan = new Scanner(System.in);
	    System.out.println("Good morning! I'll help you set up this program so that it can collect Twitter data every day."); //describes program to end user
	    System.out.println("I can only collect this data while I'm running. If you close me, the data of the most recent interval will be saved.");
	    System.out.println("Do you want the interval to be in minutes or hours? Type M for minutes or H for hours. Alternatively, type anything else to exit.");
	    System.out.print("Minutes (M) or Hours (H): ");
	    String mh = scan.next(); //collects handle
	    System.out.print("How many minutes or hours should each interval be? ");
	    int interval = Integer.parseInt(scan.next()); //collects tweet number on timeline
	    scan.close();
	    if(mh.contains("m") || mh.contains("M"))
	    {
	    	System.out.println("Got it! I'll wait " + interval + " minutes between each data collection period.");
	    	waitTime = interval * 1000 * 60; //minutes to millisecond conversion
	    }
	    else if(mh.contains("h") || mh.contains("H"))
	    {
	    	System.out.println("Got it! I'll wait " + interval + " hours between each data collection period.");
	    	waitTime = interval * 1000 * 60 * 60; //hours to millisecond conversion
	    }
	    else
	    {
	    	waitTime = 0;
	    	System.out.println("Oops, you didn't enter \"M\" or \"H\"! Exiting program!");
	    	System.exit(0);
	    }
	    System.out.println("");
	    
		ConfigurationBuilder cb = new ConfigurationBuilder(); //code to authorize @P2A_LiRa 
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("Akrbil1kDfds7smxklHJqBDFI")
		.setOAuthConsumerSecret("WfiIdQZiAH9D1GDT1oODUejIq6WGH2HHvb7aTx5R8UarrDL6wD")
		.setOAuthAccessToken("879695567697448960-6HaZs6ghyPJLseiHUeCxH2NhgQf3Ady")
		.setOAuthAccessTokenSecret("vRU898HftZytViaQ3pAVAa6FnLKesC1kUZtdhFMBdJFRu");		
		Twitter myTwitter = new TwitterFactory(cb.build()).getInstance(); //required in Twitter4J
		
		if(!(new File("ScoreData.txt").isFile()))
		{
			long sT = (new Date()).getTime(); //find time it took to run program
			PrintWriter scoreDataSetter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("ScoreData.txt"), false), "UTF-8")); //writer to update data daily
			System.out.println("ScoreData.txt not found. Making and filling new .txt. This will take one interval.");
			
			long cursor = -1; //for some reason this is required
			IDs ids = myTwitter.getFriendsIDs(cursor); //gets the friend IDs of @P2A_LiRa
			long[] idData = ids.getIDs(); //get IDs of friends of user
			for(long id : idData) //runs through IDs
			{
				User u = myTwitter.showUser(id); //makes new user based on long 
			    int days = Integer.parseInt("" + ((sT - u.getCreatedAt().getTime())/86400000)); //the number of days the account has existed
				int tweetsCount = u.getStatusesCount(); //the number of tweets of the user
				long followersCount = u.getFollowersCount(); //the number of followers of the user
				boolean verified = u.isVerified(); //whether or not the user is verified
				boolean defaultProfileImage = u.isDefaultProfileImage(); //whether or not the user has the default profile image
				boolean defaultProfile = u.isDefaultProfile(); //whether or not the user has the default profile (general)
				long likes = 0; //counts the number of likes of the top 20 tweets on the user's timeline (most recent)
				long retweets = 0; //counts the number of retweets of the top 20 tweets on the user's timeline
				List<Status> tweets = myTwitter.getUserTimeline(id); //collects the top 20 tweets on the user timeline
				for(Status sta: tweets)
				{
					if(!sta.isRetweet())
					{
						likes += sta.getFavoriteCount(); //increments likes
						retweets += sta.getRetweetCount(); //increments retweets
					}
				}
				scoreDataSetter.println(u.getScreenName() + "|" + days + "|" + tweetsCount + "|" + followersCount + "|" + verified + "|" + defaultProfileImage + "|" + defaultProfile + "|" + likes + "|" + retweets); //print out to updated text file
			}
			
			scoreDataSetter.close();
			long eT = (new Date()).getTime(); //find time it took to run program
			Thread.sleep(waitTime - (eT-sT)); //guarantees even intervals
		}
		if(!Files.isDirectory(Paths.get("ScoreDataArchive")))
		{
			System.out.println("ScoreDataArchive not found. Making new folder.");
			File f1 = new File("ScoreDataArchive");
			f1.mkdir();
		}
		if(!Files.isDirectory(Paths.get("ResultArchive")))
		{
			System.out.println("ResultArchive not found. Making new folder.");
			File f1 = new File("ResultArchive");
			f1.mkdir();
		}
		if(!Files.isDirectory(Paths.get("TweetsArchive")))
		{
			System.out.println("TweetArchive not found. Making new folder.");
			File f1 = new File("TweetArchive");
			f1.mkdir();
		}

		while(true)
		{
			try
			{
				long startTime = (new Date()).getTime();
				Scanner scanning = new Scanner(new File("ScoreData.txt")); //scans through the pre-collected data to save API requests
				String sdName = "ScoreDataArchive/" + new SimpleDateFormat("MM-dd-yyy'_'HHmma").format(startTime) + ".txt";
				String rName = "ResultArchive/" + new SimpleDateFormat("MM-dd-yyy'_'HHmma").format(startTime) + ".txt";
				String tName = "TweetArchive/" + new SimpleDateFormat("MM-dd-yyy'_'HHmma").format(startTime) + ".txt";
				System.out.println("The raw data will be stored in " + sdName);
				System.out.println("The results will be stored in " + rName);
				System.out.println("The tweets will be stored in " + tName);

				
				ArrayList<TD> scanned = new ArrayList<TD>(); //holds all of the data from database
				ArrayList<TD> updated = new ArrayList<TD>(); //holds all of the current data, for this infinite while loop
				ArrayList<TD> inCommon = new ArrayList<TD>(); //holds all of the data in common, to prevent changes when user follows/unfollows someone
				
				System.out.println("Collecting archived data.");
				while(scanning.hasNext()) //runs through entire text file
				{
					String input = scanning.next(); //data from database
					String[] s = input.split("\\|"); //groups data into an array
					TD toAdd = new TD(s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7], s[8]);
					scanned.add(toAdd); //constructor set up to easily accept input
				}
				System.out.println("Archived data collected. " + scanned.size() + " users added to list.");
				System.out.println("");
				scanning.close();
				
				if(scanned.size() == 0)
				{
					System.out.println("No archived data found. Generating archive. This will take one interval.");
					long sT = (new Date()).getTime(); //find time it took to run program
					PrintWriter scoreDataSetter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File("ScoreData.txt")), "UTF-8")); //writer to update data daily
					
					long cursor = -1; //for some reason this is required
					IDs ids = myTwitter.getFriendsIDs(cursor); //gets the friend IDs of @P2A_LiRa
					long[] idData = ids.getIDs(); //get IDs of friends of user
					for(long id : idData) //runs through IDs
					{
						User u = myTwitter.showUser(id); //makes new user based on long 
					    int days = Integer.parseInt("" + ((sT - u.getCreatedAt().getTime())/86400000)); //the number of days the account has existed
						int tweetsCount = u.getStatusesCount(); //the number of tweets of the user
						long followersCount = u.getFollowersCount(); //the number of followers of the user
						boolean verified = u.isVerified(); //whether or not the user is verified
						boolean defaultProfileImage = u.isDefaultProfileImage(); //whether or not the user has the default profile image
						boolean defaultProfile = u.isDefaultProfile(); //whether or not the user has the default profile (general)
						long likes = 0; //counts the number of likes of the top 20 tweets on the user's timeline (most recent)
						long retweets = 0; //counts the number of retweets of the top 20 tweets on the user's timeline
						List<Status> tweets = myTwitter.getUserTimeline(id); //collects the top 20 tweets on the user timeline
						for(Status sta: tweets)
						{
							if(!sta.isRetweet())
							{
								likes += sta.getFavoriteCount(); //increments likes
								retweets += sta.getRetweetCount(); //increments retweets
							}
						}
						scoreDataSetter.println(u.getScreenName() + "|" + days + "|" + tweetsCount + "|" + followersCount + "|" + verified + "|" + defaultProfileImage + "|" + defaultProfile + "|" + likes + "|" + retweets); //print out to updated text file
					}
					
					scoreDataSetter.close();
					long eT = (new Date()).getTime(); //find time it took to run program
					Thread.sleep(waitTime - (eT-sT)); //guarantees even intervals
				}
				PrintWriter updater = new PrintWriter(new OutputStreamWriter(new FileOutputStream("ScoreData.txt"), "UTF-8")); //writer to update data daily
				PrintWriter archives = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(sdName), false), "UTF-8")); //writer to add new text file to archives
				PrintWriter results = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(rName), false), "UTF-8")); //outputs to Data.txt for Excel conversion
				PrintWriter tweeteds = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(tName), false), "UTF-8")); //outputs to Data.txt for Excel conversion

				System.out.println("Collecting current data. This takes approximately three minutes.");
				long cursor = -1; //for some reason this is required
				IDs ids = myTwitter.getFriendsIDs(cursor); //gets the friend IDs of @P2A_LiRa
				long[] idData = ids.getIDs(); //get IDs of friends of user
				for(long id : idData) //runs through IDs
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
					for(Status sta: tweets)
					{
						if(!sta.isRetweet())
						{
							likes += sta.getFavoriteCount(); //increments likes
							retweets += sta.getRetweetCount(); //increments retweets
						}
					}
					TD toAdd = new TD(u.getScreenName(),  days, tweetsCount, (int)followersCount, verified, defaultProfileImage, defaultProfile, (int)likes, (int)retweets); //add data to ArrayList<TD> of new data
					for(Status sta: tweets)
					{
						if(!sta.isRetweet())
						{
							toAdd.addStatus(sta);
						}
					}
					updated.add(toAdd);
					updater.println(u.getScreenName() + "|" + days + "|" + tweetsCount + "|" + followersCount + "|" + verified + "|" + defaultProfileImage + "|" + defaultProfile + "|" + likes + "|" + retweets); //print out to updated text file
					archives.println(u.getScreenName() + "|" + days + "|" + tweetsCount + "|" + followersCount + "|" + verified + "|" + defaultProfileImage + "|" + defaultProfile + "|" + likes + "|" + retweets); //print out to archived text file
				}
				System.out.println("Current data collected.");
				System.out.println("");
				
				archives.close(); //no more need for archiving OutputStream
				
				System.out.println("Identifying common users. Past data users: " + scanned.size() + ", current users: " + updated.size() + ".");
				for(TD a : scanned) //nested for-each loops that find all common users between old and new data. Useful if @P2A_LiRa follows/unfollows an account on either list
				{
					for(TD b : updated)
					{
						if(a.toString().equals(b.toString()))
						{ 	
							TD toAdd = new TD(a.toString(), b.getA() - a.getA(), b.getTC() - a.getTC(), b.getFC() - a.getFC(), b.getV(), b.getID(), b.getPD(), b.getL() - a.getL(), b.getR() - a.getR(), b.getML(), b.getMR(), b.getLD(), b.getRD());
							inCommon.add(toAdd); //adds common elements to inCommon
						}
					}
				}
				System.out.println(inCommon.size() + " common users identified.");
				System.out.println("");
				
				System.out.println("Starting data analysis.");
				int iCS = inCommon.size(); //int to save space 
				double[] tweetsChange = new double[iCS]; //initializes array to store tweetsChange to be submitted to empirical distribution
				double[] followersChange = new double[iCS]; //initializes array to store followersChange to be submitted to empirical distribution
				double[] likesChange = new double[iCS]; //initializes array to store likesChange to be submitted to empirical distribution
				double[] retweetsChange = new double[iCS]; //initializes array to store retweetsChange to be submitted to empirical distribution
				int iCC = 0; //int to count places in inCommon
				for(TD iC : inCommon) //loops through inCommon
				{
					tweetsChange[iCC] = (double) iC.getTC(); //add data to tweetsChange
					followersChange[iCC] = (double) iC.getFC(); //add data to followersChange
					likesChange[iCC] = (double) iC.getL(); //add data to likesChange
					retweetsChange[iCC] = (double) iC.getR(); //add data to retweetsChange
					iCC++; //increment counting int
				}
				
				String[] gTTtC = getTopThree(inCommon, tweetsChange); //get top 3 tweet changes
				String[] gTTfC = getTopThree(inCommon, followersChange); //get top 3 follower changes
				String[] gTTlC = getTopThree(inCommon, likesChange); //get top 3 like changes
				String[] gTTrC = getTopThree(inCommon, retweetsChange); //get top 3 retweet changes
				
				System.out.println("The results for the interval have been collected.");
				System.out.printf("%-30s %-50s %-50s %-50s\n", "Most Tweets for interval: ", gTTtC[0], gTTtC[1], gTTtC[2]);
				results.printf("%-30s %-50s %-50s %-50s\n", "Most Tweets for interval: ", gTTtC[0], gTTtC[1], gTTtC[2]);
				System.out.printf("%-30s %-50s %-50s %-50s\n", "Most Followers for interval: ", gTTfC[0], gTTfC[1], gTTfC[2]);
				results.printf("%-30s %-50s %-50s %-50s\n", "Most Followers for interval: ", gTTfC[0], gTTfC[1], gTTfC[2]);
				System.out.printf("%-30s %-50s %-50s %-50s\n", "Most Likes for interval: ", gTTlC[0], gTTlC[1], gTTlC[2]);
				results.printf("%-30s %-50s %-50s %-50s\n", "Most Likes for interval: ", gTTlC[0], gTTlC[1], gTTlC[2]);
				System.out.printf("%-30s %-50s %-50s %-50s\n", "Most Retweets for interval: ", gTTrC[0], gTTrC[1], gTTrC[2]);
				results.printf("%-30s %-50s %-50s %-50s\n", "Most Retweets for interval: ", gTTrC[0], gTTrC[1], gTTrC[2]);
				System.out.println("");
				results.println("");
				
				System.out.println("Percentile, Like, and Retweet breakdown by users: ");
				results.println("Percentile breakdown by users: ");
				
				DoubleShift dStC = new DoubleShift(tweetsChange); //increments tweetsChange
				DoubleShift dSfC = new DoubleShift(followersChange); //increments followersChange
				DoubleShift dSL = new DoubleShift(likesChange); //increments likesChange
				DoubleShift dSR = new DoubleShift(retweetsChange); //increments retweetsChange
				
				EmpiricalDistribution tC = new EmpiricalDistribution(tweetsChange.length); //set up new EmpiricalDistribution for tweetsChange
				tC.load(dStC.getD()); //add tweetsChange as data to this EmpiricalDistribution		
				EmpiricalDistribution fC = new EmpiricalDistribution(followersChange.length); //set up new EmpiricalDistribution for followersChange
				fC.load(dSfC.getD()); //add followersChange as data to this EmpiricalDistribution
				EmpiricalDistribution lC = new EmpiricalDistribution(likesChange.length); //set up new EmpiricalDistribition for likesChange
				lC.load(dSL.getD()); //add likesChange as data to this EmpiricalDistribution
				EmpiricalDistribution rC = new EmpiricalDistribution(retweetsChange.length); //set up new EmpiricalDistribution for retweetsChange
				rC.load(dSR.getD()); //add retweetsChange as data to this EmpiricalDistribution
	
				System.out.println("");
				results.println("");
				
				for(TD ed : inCommon) //formatted way to print out results 
				{
					System.out.printf("%-30s %-10s %-22s %-10s %-22s %-10s %-22s %-10s %-22s\n", ed.toString(), "" + ed.getTC(), "" + tC.cumulativeProbability(ed.getTC() + dStC.getS()), "" + ed.getFC(), "" + fC.cumulativeProbability(ed.getFC() + dSfC.getS()), "" + ed.getL(), "" + lC.cumulativeProbability(dSL.getS() + ed.getL()), "" + ed.getR(), "" + rC.cumulativeProbability(dSR.getS() + ed.getR()));
					//System.out.println("L:" + ed.getML().getText().replaceAll("[\n\r]", "") + " | R:" + ed.getMR().getText().replaceAll("[\n\r]", ""));
					results.printf("%-30s %-10s %-22s %-10s %-22s %-10s %-22s %-10s %-22s\n", ed.toString(), "" + ed.getTC(), "" + tC.cumulativeProbability(ed.getTC() + dStC.getS()), "" + ed.getFC(), "" + fC.cumulativeProbability(ed.getFC() + dSfC.getS()), "" + ed.getL(), "" + lC.cumulativeProbability(dSL.getS() + ed.getL()), "" + ed.getR(), "" + rC.cumulativeProbability(dSR.getS() + ed.getR()));
				}
				
				System.out.println("");
				Status mltStatus = mLT(inCommon);
				Status mrtStatus = mRT(inCommon);
				if(mltStatus != null)
				{
					System.out.println("Most Liked Tweet: [" + mltStatus.getUser().getScreenName() + "] " + mltStatus.getText().replaceAll("[\n\r]", "") + " (" + mltStatus.getFavoriteCount() + ")");
					tweeteds.println("Most Liked Tweet: [" + mltStatus.getUser().getScreenName() + "] " + mltStatus.getText().replaceAll("[\n\r]", "") + " (" + mltStatus.getFavoriteCount() + ")");
				}
				if(mrtStatus != null)
				{
					System.out.println("Most Retweeted Tweet: [" + mrtStatus.getUser().getScreenName() + "] " + mrtStatus.getText().replaceAll("[\n\r]", "") + " (" + mrtStatus.getRetweetCount() + ")");
					tweeteds.println("Most Retweeted Tweet: [" + mrtStatus.getUser().getScreenName() + "] " + mrtStatus.getText().replaceAll("[\n\r]", "") + " (" + mrtStatus.getRetweetCount() + ")");
				}
				System.out.println("");
				tweeteds.println("");
				
				TD tempLTD = bLT(inCommon);
				TD tempRTD = bRT(inCommon);
				System.out.println("Most Liked Tweet, proportionally: [" + tempLTD.getML().getUser().getScreenName() + "] " + tempLTD.getML().getText().replaceAll("[\n\r]", "") + " (" + tempLTD.getLD() + ")");
				tweeteds.println("Most Liked Tweet, proportionally: [" + tempLTD.getML().getUser().getScreenName() + "] " + tempLTD.getML().getText().replaceAll("[\n\r]", "") + " (" + tempLTD.getLD() + ")");
				System.out.println("Most Retweeted Tweet, proportionally: [" + tempRTD.getMR().getUser().getScreenName() + "] " + tempRTD.getMR().getText().replaceAll("[\n\r]", "") + " (" + tempRTD.getRD() + ")");
				tweeteds.println("Most Retweeted Tweet, proportionally: [" + tempRTD.getMR().getUser().getScreenName() + "] " + tempRTD.getMR().getText().replaceAll("[\n\r]", "") + " (" + tempRTD.getRD() + ")");
				System.out.println("");
				tweeteds.println("");
				
				for(TD ed2 : inCommon)
				{
					if(ed2.getML() != null && ed2.getMR() != null)
					{
						Status tempMLT = ed2.getML();
						Status tempMRT = ed2.getMR();
						System.out.printf("%-3s %-20s %-170s %-20s\n", "L", tempMLT.getUser().getScreenName(), tempMLT.getText().replaceAll("[\n\r]", ""), "" + ed2.getLD());
						tweeteds.printf("%-3s %-20s %-170s %-20s\n", "L", tempMLT.getUser().getScreenName(), tempMLT.getText().replaceAll("[\n\r]", ""), "" + ed2.getLD());
						System.out.printf("%-3s %-20s %-170s %-20s\n", "R", tempMRT.getUser().getScreenName(), tempMRT.getText().replaceAll("[\n\r]", ""), "" + ed2.getRD());
						tweeteds.printf("%-3s %-20s %-170s %-20s\n", "R", tempMRT.getUser().getScreenName(), tempMRT.getText().replaceAll("[\n\r]", ""), "" + ed2.getRD());
					}
					else
					{
						if(ed2.getML()!= null)
						{
							Status tempMLT = ed2.getML();
							System.out.printf("%-3s %-20s %-170s %-20s\n", "L", tempMLT.getUser().getScreenName(), tempMLT.getText().replaceAll("[\n\r]", ""), "" + ed2.getLD());
							tweeteds.printf("%-3s %-20s %-170s %-20s\n", "L", tempMLT.getUser().getScreenName(), tempMLT.getText().replaceAll("[\n\r]", ""), "" + ed2.getLD());
						}
						else
						{
							System.out.printf("%-3s %-20s %-170s %-20s\n", "L", ed2.toString(), "This user has no Tweets!", "N/A");
							tweeteds.printf("%-3s %-20s %-170s %-20s\n", "L", ed2.toString(), "This user has no Tweets!", "N/A");
						}
						if(ed2.getMR()!= null)
						{
							Status tempMRT = ed2.getMR();
							System.out.printf("%-3s %-20s %-170s %-20s\n", "R", tempMRT.getUser().getScreenName(), tempMRT.getText().replaceAll("[\n\r]", ""), "" + ed2.getRD());
							tweeteds.printf("%-3s %-20s %-170s %-20s\n", "R", tempMRT.getUser().getScreenName(), tempMRT.getText().replaceAll("[\n\r]", ""), "" + ed2.getRD());
						}
						else
						{
							System.out.printf("%-3s %-20s %-170s %-20s\n", "R", ed2.toString(), "This user has no Tweets", "N/A");
							tweeteds.printf("%-3s %-20s %-170s %-20s\n", "R", ed2.toString(), "This user has no Tweets", "N/A");
						}
					}
				}
				
				scanning.close(); //save scanning
				updater.close(); //save updater
				results.close(); //save results
				tweeteds.close(); //save tweets
				
				long endTime = (new Date()).getTime(); //find time it took to run program
				Thread.sleep(waitTime - (endTime - startTime)); //guarantees even intervals
				System.out.println(""); //
			}
			catch(TwitterException e)
			{
				System.out.println("Something went wrong. Waiting 15 minutes for it to be fixed.");
				Thread.sleep(15 * 60 * 60 * 1000);
			}
		}		
		
	}
	
	public static String[] getTopThree(ArrayList<TD> twitterData, double[] d) //returns the top 3 values of an array, formatted to match other data with corresponding indices
	{
		String[] toReturn = new String[3]; //set up returnable format, with a size of 3
		double[] tempDoubleArray = d; //make a copy of input array to prevent making any changes
		for(int i = 0; i < toReturn.length; i++) //run through array n times, which in this case is 3
		{
			int currentMax = maxIndex(tempDoubleArray); //finds the index of the largest value in the array
			toReturn[i] = twitterData.get(currentMax).toString() + " (" + (int)tempDoubleArray[currentMax] + ")"; //format data 
			tempDoubleArray[currentMax] = Integer.MIN_VALUE; //since we already have the maximum value, we have no further need for it
		}
		return toReturn; //return array, formatted correctly
	}
	public static int maxIndex(double[] d) //simple way of finding the index of the largest number in an array of doubles
	{
		double maxDouble = 0.0; //largest double
		int maxDoubleIndex = 0; //largest index
		for(int i = 0; i < d.length; i++) //loop through array
		{
			if(d[i] > maxDouble) //if the current value is larger than the past value
			{
				maxDouble = d[i]; //replace max value
				maxDoubleIndex = i; //replace max index
			}
		}
		return maxDoubleIndex; //return index
	}
	public static Status mLT(ArrayList<TD> al) //finds the most liked tweet of all legislators
	{
		Status mostLikes = findFirstNonNull(al);
		for(TD td : al)
		{
			if((td.getML() != null) && (td.getML().getFavoriteCount() > mostLikes.getFavoriteCount()))
			{
				mostLikes = td.getML();
			}
		}
		return mostLikes;
	}
	public static Status mRT(ArrayList<TD> al) //finds the most retweeted tweet of all legislators
	{
		Status mostRetweets = findFirstNonNull(al);
		for(TD td : al)
		{
			if((td.getMR() != null) && (td.getMR().getRetweetCount() > mostRetweets.getRetweetCount()))
			{
				mostRetweets = td.getMR();
			}
		}
		return mostRetweets;
	}	
	public static TD bLT(ArrayList<TD> al) //finds the largest change in likes of all legislators by average likes/tweet
	{
		TD biggestLikeChange = findFirstNonNullTD(al);
		for(TD td : al)
		{
			if(td.getLD() > biggestLikeChange.getLD())
			{
				biggestLikeChange = td;
			}
		}
		return biggestLikeChange;
	}
	public static TD bRT(ArrayList<TD> al) //finds the largest change in retweets of all legislators by average retweets/tweet
	{
		TD biggestRetweetChange = al.get(0);
		for(TD td : al)
		{
			if(td.getLD() > biggestRetweetChange.getRD())
			{
				biggestRetweetChange = td;
			}
		}
		return biggestRetweetChange;
	}
	public static Status findFirstNonNull(ArrayList<TD> al) //guarantees that first status found is not null
	{
		for(TD td : al)
		{
			if(td.getML() != null)
			{
				return td.getML();
			}
		}
		System.out.println("No Tweets found.");
		return null;
	}
	public static TD findFirstNonNullTD(ArrayList<TD> al) //guarantees that first status found is not null
	{
		for(TD td : al)
		{
			if(td.getML() != null)
			{
				return td;
			}
		}
		System.out.println("No Tweets found.");
		return null;
	}

}

class DoubleShift //convenient method for storing data when array values are shifted. Prevents values below 0, important for normal distributions
{	
	double[] myD;
	double shift;
	
	public DoubleShift(double[] d) //initializes the shift
	{
		double minDouble = d[0]; //largest double
		for(int i = 0; i < d.length; i++) //loop through array
		{
			if(d[i] < minDouble) //if the current value is larger than the past value
			{
				minDouble = d[i]; //replace max value
			}
		}
		double toReturn = Math.abs(minDouble);
		for(int i = 0; i < d.length; i++)
		{
			d[i] += toReturn;
		}
		myD = d;
		shift = toReturn;
	}
	public double[] getD() //get array of doubles
	{
		return myD;
	}
	public double getS() //get shift
	{
		return shift;
	}
}

class TD implements Comparable<TD> //convenient method for storing the data from the ScoreData.txt text file
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
	private Status mostLikes; //the most liked tweet of the person to be scored, of their past 20 tweets
	private Status mostRetweets; //the most retweeted tweet of the person to be scored, of their past 20 tweets
	private double lDiff; //most liked post divided by average likes per tweet
	private double rDiff; //most retweeted post divided by average retweets per tweet
	
	public TD(String u, int a, int tC, int fC, boolean v, boolean iD, boolean pD, int l, int r) //raw data constructor
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
	public TD(String u, String a, String tC, String fC, String v, String iD, String pD, String l, String r) //String-only constructor, useful for String.split()
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
	public TD(String u, int a, int tC, int fC, boolean v, boolean iD, boolean pD, int l, int r, Status mL, Status mR, double lDi, double rDi) //String-only constructor, useful for String.split()
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
		mostLikes = mL;
		mostRetweets = mR;
		lDiff = lDi;
		rDiff = rDi;
	}
	public String fullString() //returns all the data at once in a String, useful for debugging
	{
		return myUser + " | " + age + " | " + tweetCount + " | " + followCount + " | " + verified + " | " + imageDef + " | " + profDef + " | " + likes + " | " + retweets;
	}
	public int compareTo(TD other) //Comparable method
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
	public Status getML() //returns most liked post
	{
		return mostLikes;
	}
	public Status getMR() //returns most retweeted post
	{
		return mostRetweets;
	}
	public void addStatus(Status s) //checks whether to add a status to mostLikes or mostRetweets
	{
		if(mostLikes == null || (s.getFavoriteCount() > mostLikes.getFavoriteCount()))
		{
			mostLikes = s;
			lDiff = mostLikes.getFavoriteCount()/((double)(likes/20));
			//System.out.println("L" + s.getFavoriteCount() + " [" + s.getUser().getScreenName() + "] " + s.getText().replaceAll("[\n\r]", "") + ", " + lDiff);
		}
		if(mostRetweets == null || (s.getRetweetCount() > mostRetweets.getRetweetCount()))
		{
			mostRetweets = s;
			rDiff = mostRetweets.getRetweetCount()/((double)(retweets/20));
			//System.out.println("R" + s.getRetweetCount() + " [" + s.getUser().getScreenName() + "] " + s.getText().replaceAll("[\n\r]", "") + ", " + rDiff);
		}
		//System.out.println("L" + mostLikes.getFavoriteCount() + " [" + mostLikes.getUser().getScreenName() + "] " + mostLikes.getText().replaceAll("[\n\r]", "") + ", " + lDiff);
		//System.out.println("R" + mostRetweets.getFavoriteCount() + " [" + mostRetweets.getUser().getScreenName() + "] " + mostRetweets.getText().replaceAll("[\n\r]", "") + ", " + rDiff);
	}
	public double getLD() //returns quotient of most liked post and average likes per post
	{
		return lDiff;
	}
	public double getRD() //returns quotient of most retweeted post and average retweets per post
	{
		return rDiff;
	}
}
