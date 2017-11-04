// BUGS: None, works as intended

import java.util.*;
import java.io.*;
import twitter4j.*;

public class TweetScoreTester
{
	public static void main(String []args) throws TwitterException, IOException
	{  
	    System.out.println("STARTING COLLECTION"); //description, to be changed
	    System.out.println("");
	    int scoresSum = 0;
	    int scoresCount = 0;
	    Scanner scanning = new Scanner(new File("ScoreData.txt")); //scans through the pre-collected data to save API requests
	    PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("TST.txt"), "UTF-8")); //outputs to Data.txt for Excel conversion
	    TweetScore ts = new TweetScore(); //object dedicated to scoring and comparing users
	    while(scanning.hasNext())
	    {
	    	String testName = scanning.nextLine().split("\\|")[0];
	    	System.out.println(testName);
	    	writer.println(testName);
		    String[] scored = ts.score(testName); //returns as a String[12], hard coded
		    System.out.println(scored[0] + " " + scored[1] + " " + scored[2] + " " + scored[3]); //prints out user's grade, raw score, and descriptions
		    writer.println(scored[0] + " " + scored[1] + " " + scored[2] + " " + scored[3]);
		    int thisScore = Integer.parseInt(scored[1].replaceAll("Raw Score: ", ""));
		    scoresSum += thisScore;
		    for(int i = 4; i <= 7; i++) //quick way to print out everything in lines
		    {
		    	System.out.println(scored[i] + " " + scored[i+4]); //prints out the score and the percentile
		    	writer.println(scored[i] + " " + scored[i+4]);
		    }
		    scoresCount++;
		    System.out.println("");
		    writer.println("");
	    }
	    scanning.close();
	    System.out.println("ENDING COLLECTION");
	    System.out.println("Average Score: " + scoresSum/scoresCount + ", Scores Count : " + scoresCount);
	    writer.println("ENDING COLLECTION");
	    writer.println("Average Score: " + scoresSum/scoresCount + ", Scores Count : " + scoresCount);
	    writer.close();
	}
}