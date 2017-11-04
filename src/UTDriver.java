// BUGS: none currently (all in UserTweets and TweetScore classes)
// PROGRESS: successfully makes new UserTweets variable, prints out to file, gets some of the tweets
// TODO: basic GUI using JFrame

import java.io.IOException;
import java.util.*;
import java.io.*;
import twitter4j.*;

public class UTDriver
{
	public static void main(String []args) throws TwitterException, IOException
	{  
		//initial setup and input collection
	    System.out.println("START (Eclipse Console version of program. This will be updated to run as a web app)"); //description, to be changed
	    RateChecker check = new RateChecker(0); //set rate to 0 for testing purposes
	    check.check(); //runs checking algorithm based on stored value
	    if(check.getRunnable() == true) //checks whether the rate limit has been met. If it's true, the program will be run
	    {
	    	// SETUP CODE
		    System.out.println("");
		    Scanner scan = new Scanner(System.in);
		    System.out.print("Twitter Handle: ");
		    String twitter_handle = scan.next(); //collects handle
		    System.out.print("Tweet Number: ");
		    int tweet_number = Integer.parseInt(scan.next()); //collects tweet number on timeline
		    scan.close(); //closes scanner, no more input required
		    System.out.println(""); //line spacing
		    // SCORING CODE
		    TweetScore ts = new TweetScore(); //object dedicated to scoring and comparing users
		    String[] scored = ts.score(twitter_handle); //returns as a String[12], hard coded
		    System.out.println(scored[0] + " | " + scored[1] + " | " + scored[2] + " | " + scored[3]); //prints out user's grade, raw score, and descriptions
		    for(int i = 4; i <= 7; i++) //quick way to print out everything in lines
		    {
		    	System.out.println(scored[i] + ", " + scored[i+4]); //prints out the score and the percentile
		    }
		    System.out.println(""); //spacing
		    // REPLY CODE
		    if(tweet_number == -1) //convenient way to end program here during dev process
		    {
		    	System.out.println("END");
		    	System.exit(0); //ends program
		    }
		    UserTweets test = new UserTweets(twitter_handle); //creates a new UserTweets with the Twitter data input
		    PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("TwitterResponses.txt"), "UTF-8")); //sets up .txt file conversion. Emoji support added!
		    for(String s : test.returnReplies(tweet_number)) //runs through the ArrayList<String> returnTweets() returns
		    {
			   writer.println(s.replaceAll("[\n\r]", "")); //prints out each tweet on a separate line. No tweet number 
		    }
		    writer.close(); //closes scanner, no more input required	    
		    // FORMAT: each tweet is on an individual/separate line. Run reader program as long as there are additional lines
		    // PYTHON SCRIPT
		    String command = "python /Users/fellowliamrathke/Documents/workspace/Project/DataPasserWatson.py";
		    Process p = Runtime.getRuntime().exec(command);
	    } 
	   System.out.println("");
	   System.out.println("END");
	}
}