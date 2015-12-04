import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*********************************************************************************
 * 
 * Class ElevatorRequestReader
 * 
 * Description:
 * 
 * <p>This class manages the reading of data from a "request.txt" file for the
 * ElevatorSimulator program. It assumes that the file format is valid. Here are
 * the rules for the file:</p>
 * 
 * <ul>
 *   <li>Lines starting with 'T' indicate a time click. Everything else on that 
 *   		line is ignored as a comment.</li>
 *   <li>Lines starting with 'R' are requests that are associated with the previous 
 *   		time click line. They indicate person name, starting floor, and 
 *   		requested floor. Everything else on that line is ignored as a comment. 
 *   		A time click may be followed by zero or more requests. No request may 
 *   		appear before the first time click.</li>
 *   <li>Lines starting with anything other than 'T' or 'R' are ignored as 
 *   		comments.</li>
 *   <li>Any leading whitespace in a line (blanks or tabs) is ignored.</li>
 * </ul>
 * 
 * 	<p>To use this class, first make a single call to
 * ElevatorRequestReader.openRequestFile() to open the file. The boolean 
 * return value will indicate whether the open was successful.</p> 
 * 
 * 	<p>Then keep calling ElevatorRequestReader.getRequest(). Each call returns an 
 * array filled with the requests at the next time. It returns an array of length 
 * 0 if there are no requests at this time click or if the file contains no more 
 * time clicks.</p>
 * 				
 ***********************************************************************************/
public class ElevatorRequestReader 
{
	private static int time = -1;
	private static Scanner file = null;
	private static final String FILE_NAME = "requests.txt";

	private static final char TIME = 'T';
	private static final char REQUEST = 'R';

	/**
	 * Opens the request file. Call this method only once. Call it before the
	 * first call to getRequests().
	 * 
	 * @return		true if the file was opened successfully, false otherwise
	 */
	public static boolean openRequestFile()
	{
		// Do the actual work of opening the file.
		file = openTheFile();

		// Skip file's lines until the first time click line (or at the end of 
		//	file). That prepares us to read request data at the next 
		//	getRequests() call.
		if (file != null)
		{
			boolean timeClickOrFileEnd = false;
			do
			{
				timeClickOrFileEnd = readOneLine(file);
			}
			while (!timeClickOrFileEnd);
		}

		return (file != null);
	}

	/**
	 * Open the request file. Leave file null if the open fails.
	 * 
	 * @param		the newly opened file, or null if opening failed
	 */
	private static Scanner openTheFile()
	{
		Scanner file = null;
		
		// Open the file.  Note that Eclipse looks for the file in your 
		//	workspace inside your project folder (NOT in your src folder.)
		try 
		{
			file = new Scanner(new File(FILE_NAME));
		}
		catch (IOException e)
		{	
			// Something went wrong!
			System.out.println("File error - file not found");
		}
		
		return file;
	}

	/**
	 * Read one line from the file if possible.
	 * 
	 * @param		file to read
	 * @return		true - line is a time click line or the file has ended, 
	 * 				false - anything else. Treat line as a comment, ignoring
	 * 						 it and reporting an error if was a request.
	 */
	private static boolean readOneLine(Scanner file)
	{
		boolean done = false;
		if (!file.hasNext())
		{
			// Nothing left in the file to read!
			done = true;
		}
		else
		{
			// Read the line and see if it's a time click.
			String nextLineType = file.next();
			file.nextLine();
			if (nextLineType.charAt(0) == TIME)
			{
				done = true;
			}
			else if (nextLineType.charAt(0) == REQUEST)
			{
				System.out.println("Invalid file format - Request before the first Time Click");
			}
			// else it's a comment, so return false
		}
		return done;		
	}

	/**
	 * Gets an array of requests for the next time click. The array length
	 * will be the number of requests at this time click. The length will
	 * be zero if there are no requests at this time click (or if there are
	 * no remaining time clicks in the file.)  
	 * 
	 * @return		array of requests at this time click. It may contain
	 * 				>0 requests or 0 requests but will never be null.
	 */
	public static Request[] getRequests() 
	{
		time++;
		
		ArrayList<Request> requestArrayList = new ArrayList<Request>();

		// If the file never opened, leave the ArrayList empty.
		if (file != null)
		{
			// Fill the ArrayList with all Requests (0 or more) for this 
			//	time click.
			boolean finishedTimeClick = false;
			while (!finishedTimeClick)
			{
				finishedTimeClick = processOneLine(file, time, requestArrayList);
			}
		}

		// Convert the ArrayList to an array and return it.
		Request[] newRequests = requestArrayList.toArray(new Request[0]);
		return newRequests;
	}
			
	/**
	 * Processes the next line in the file (if any), adding any request
	 * found to the requestArrayList, and returning true if we've reached
	 * the end of this time click (including the end of file case.)
	 * 
	 * @param file				file to read
	 * @param time				time click count (0 for first time click)
	 * @param requestArrayList	ArrayList in which to store the requests for 
	 * 								this time click
	 * @return					true if we're at the end of this time click
	 */
	private static boolean processOneLine(
				Scanner file, int time, ArrayList<Request> requestArrayList)
	{
		boolean finishedTimeClick = false;
		if (!file.hasNext())
		{
			// Give up if the file doesn't exist or has ended.
			finishedTimeClick = true;
		}
		else
		{
			// Read the line type
			String nextLineType = file.next();
			if (nextLineType.charAt(0) == TIME)
			{
				// Stop at the next TIME
				finishedTimeClick = true;
			}
			else if (nextLineType.charAt(0) == REQUEST)
			{
				// It's a request. Add the resulting request to the list.
				addOneRequest(file, time, requestArrayList);
			}
			// else it's a comment. Ignore COMMENT lines
			
			// Throw away the rest of the line.
			if (file.hasNext())
			{
				file.nextLine();
			}
		}

		return finishedTimeClick;
	}

	/**
	 * This is a request. Create the Request object and add it.
	 * 
	 * @param file				file to read
	 * @param time				time click count (0 for first time click)
	 * @param requestArrayList	ArrayList in which to store the requests for 
	 * 								this time click
	 */
	private static void addOneRequest(Scanner file, int time, ArrayList<Request> requestArrayList)
	{
		String personName = "Invalid Request";
		int startFloor = 1;
		int destinationFloor = 1;
		
		// Read the request data
		if (file.hasNext())
		{
			personName = file.next();
		}
		if (file.hasNext())
		{
			startFloor = file.nextInt();
		}
		if (file.hasNext())
		{
			destinationFloor = file.nextInt();
		}
		
		// Make the request and add it to the ArrayList
		Request newRequest = new Request(time, personName, startFloor, destinationFloor);
		requestArrayList.add(newRequest);
	}

}
