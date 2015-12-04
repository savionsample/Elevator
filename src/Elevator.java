import java.util.ArrayList;

/******************************************************************************
* Savion Sample
* Elevator Class
* Stores all of the information about a single elevator including:
* the Requests/people inside of the elevator, the direction it's going, and
* its current floor.
* It provides methods to move the elevator and and down, to change the elevators
* direction,  and to add a Request to the array list of passengers.
*
******************************************************************************/
public class Elevator {
	
	public ArrayList<Request> passengers = new ArrayList<Request>();
	public boolean directionUp; // elevator starts moving upward
	private int currentFloor;
	
	public Elevator(int startingFloor)
	{
		  currentFloor = startingFloor;
		  directionUp = true;
	}
	
	public void moveElevatorUp()
	{
		currentFloor++;
	}
	
	public void moveElevatorDown()
	{
		currentFloor--;
	}
	
	// getter method
	public int getCurrentFloor()
	{
		return currentFloor;
	}
	
	// setter methods
	public void setElevatorDirectionDown()
	{
		directionUp = false;
	}
	
	public void setElevatorDirectionUp()
	{
		directionUp = true;
	}
	
	public void addPersonToElevator(Request person)
	{
		passengers.add(person);
	}
	
	public boolean directionUp()
	{
		return directionUp;
	}

}
