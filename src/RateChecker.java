// BUGS: none currently. Works as intended
// PROGRESS: successfully restricts the times UTDriver.java can run

import java.io.*;

public class RateChecker //used to make sure that the program is run in 15-minute intervals
{
	private boolean tf;
	private int rate;

	public RateChecker(int r) throws IOException //constructor
	{ 
	    tf = false;
	    rate = r;
	} 	
	public void check() throws IOException
    {
			try //LastTimeStartUp.txt exists
		    {
		    	BufferedReader startUpCheck = new BufferedReader(new FileReader("LastTimeStartUp.txt"));
		    	long lastTimeMillis = Long.parseLong(startUpCheck.readLine());
		    	startUpCheck.close();
		    	long currentTimeMillis = System.currentTimeMillis();
		    	if((currentTimeMillis - lastTimeMillis) > (rate * 60000)) //checks whether it's been 15 minutes since the last run
		    	{
		    		//System.out.print("Try, " + tf + "/");
		    		PrintWriter lastTimeStartUp = new PrintWriter(new OutputStreamWriter(new FileOutputStream("LastTimeStartUp.txt"), "UTF-8"));
			    	lastTimeStartUp.println("" + System.currentTimeMillis());
			    	lastTimeStartUp.close();
			    	tf = true;
			    	//System.out.println(tf + "");
		    	}
		    	else
		    	{
		    		long timeUntil = currentTimeMillis-lastTimeMillis;
		    		String minutes = rate - (timeUntil / 60000) + "";
		    		System.out.println("");
		    		if(Integer.parseInt(minutes) > 1)
		    			System.out.println("You must wait " + minutes + " minutes until starting this program again.");
		    		else
		    			System.out.println("You must wait " + minutes + " minute until starting this program again. Almost ready!");
		    	}
		    }
		    catch (FileNotFoundException e) //LastTimeStartUp.txt does not exist
		    {
	    		//System.out.print("Catch, " + tf + "/");
		    	PrintWriter lastTimeStartUp = new PrintWriter(new OutputStreamWriter(new FileOutputStream("LastTimeStartUp.txt"), "UTF-8"));
		    	lastTimeStartUp.println("" + System.currentTimeMillis());
		    	lastTimeStartUp.close();
		    	tf = true;
		    	//System.out.println(tf + "");
		    }	
    	
    }
	public boolean getRunnable()
	{
		return tf;
	}
}

