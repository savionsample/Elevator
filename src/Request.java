
/******************************************************************************
*
* Savion Sample
* Requests Class
* Stores all of the information about a person including:
* time of their arrival, their name, the floor they arrived on, and their destination floor.
* It provides only getter methods that return the information about each 
* individual Request/person.
*
******************************************************************************/

public class Request {
	
	private int time;
	private String name;
	private int startFloor;
	private int endFloor;
	
	  public Request(int myTime, String myName, int myStartFloor, int myEndFloor)
	  {
		  time = myTime;
		  name = myName;
		  startFloor = myStartFloor;
		  endFloor = myEndFloor;
	  }
	  
	  public int getStartFloor()
	  {
		  return startFloor;
	  }
	  
	  public int getEndFloor()
	  {
		  return endFloor;
	  }
	  
	  public String getName()
	  {
		  return name;
	  }
	  
	  public int getTime()
	  {
		  return time;
	  }

}
