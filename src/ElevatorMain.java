import java.util.ArrayList;
import java.util.Scanner;

/******************************************************************************
*
* Name: Savion Sample
* Block: D
* Date: 10/5/15
*
* Description:
* This program will simulate the behavior of a bank of NUM_ELEVATORS elevators
* operating in a building with NUM_FLOORS floors. At each time interval several
* actions take place: people arrive at various floors to wait for the elevators,
* elevators may pick up or drop off passengers, and elevators can each more one floor. 
* 
* All elevators always head toward the top floor, then toward the bottom floor, repeat.
* the elevators are on the same ongoing cycle, regardless of people waiting on
* each floor. The first elevator starts on floor 1, and each consecutive
* elevator starts one floor higher than the last until the top floor where
* the next elevator will start back on floor 1. At the end of the program, it displays
* stats that show the people served (as in the Request got picked up from their start
* floor), and the maximum amount of people that had to wait at a time before they
* got picked up.
* 
******************************************************************************/

public class ElevatorMain {
	 
	  private static final int NUM_ELEVATORS = 2; 
	  private static final int NUM_TICKS = 17;
	  public static final int NUM_FLOORS = 5;
	  
	  //statistics variables
	  private static int peopleServed = 0;
	  private static int maxNumOfRequestsAtATime = 0;
	  
	  public static void main(String args[])
	  {
		  Scanner sc = new Scanner(System.in);
		  System.out.println("Please enter the number of ticks: ");
		  int numberTicks = sc.nextInt();
		  
		  Elevator[] elevators = new Elevator[NUM_ELEVATORS];
		  ArrayList<Request> totalRequests = new ArrayList<Request>();
		  Request[] requestsOfTimeTick;
		  establishElevators(elevators);
		  
		  if (ElevatorRequestReader.openRequestFile()) //Returns true if the file was successfully opened
		  { 
			  // runs entire program for the specified amount of ticks
			  for (int i = 0; i < numberTicks; i++)
			  {
				  requestsOfTimeTick = ElevatorRequestReader.getRequests();
				  addRequestsToTotalRequests(requestsOfTimeTick, totalRequests);
				  
				  handleOutput(requestsOfTimeTick, totalRequests, elevators, i, maxNumOfRequestsAtATime);
				  moveElevator(elevators);   
			  }
			  
			  System.out.println("\n\nSTATISTICS");
			  System.out.println("People served: " + peopleServed);
			  System.out.println("Max amount of people waiting at a time: " + maxNumOfRequestsAtATime);
		  }
	  }
	  
	  /**
	   * Deals with all the outputs of the program including the tick, 
	   * people waiting on each floor, the current floor of the elevator,
	   * whether the elevator is going up or down, and the passengers in
	   * that elevator.
	   * 
	   * @param reqOfTimeTick: an array of Requests that arrived at the current time tick
	   * @param totRequests: an Array List all the Requests still waiting to get picked up
	   * @param elevatorArray: an array of all the elevators
	   * @param tick: the current tick
	   * @param numRequests: the maximum number of people that had to wait before getting picked up
	   */
	  public static void handleOutput(Request[] reqOfTimeTick, 
			  						  ArrayList<Request> totRequests, 
			  						  Elevator[] elevatorArray, 
			  						  int tick,
			  						  int numRequests)
	  { 
		  outputTickNumber(tick);
		  outputFloorInformation(totRequests);
		  
		  //check boarding and unboarding of elevator, and checks Max People statistic
		  checkToGetOnElevator(totRequests, elevatorArray); 
		  statsFindMaxNumPeopleWaiting(totRequests, numRequests);
		  checkToGetOffElevator(totRequests, elevatorArray);
		  
		  // outputs elevator's current floor, direction, and passengers
		  for (int elevNum = 0; elevNum < NUM_ELEVATORS; elevNum++)
		  {
			  outputElevatorFloor(elevatorArray, elevNum);
			  outputElevatorDirection(elevatorArray, elevNum);
			  outputElevatorPassengers(elevatorArray, elevNum); 
		  } 
	  }
	  
	  /**
	   * Outputs the current floor of each elevator in the array
	   * 
	   * @param elev: an array of all the elevators
	   * @param elevatorNumber: the elevator that's getting its info displayed
	   */
	  public static void outputElevatorFloor(Elevator[] elev, int elevatorNumber)
	  {
		  System.out.print("Elevator " + (elevatorNumber + 1) + " is on floor " + elev[elevatorNumber].getCurrentFloor());
	  }
	  
	  /**
	   * outputs the direction each elevator is going (up or down)
	   * 
	   * @param elev: an array of all the elevators
	   * @param elevatorNumber: the elevator that's getting its info displayed
	   */
	  public static void outputElevatorDirection(Elevator[] elev, int elevatorNumber)
	  {
		  if (elev[elevatorNumber].directionUp())
		  {
			  System.out.print(", is going up");
		  }
		  else
		  {
			  System.out.print(", is going down");
		  }
	  }
	  
	  /**
	   * Outputs all the passengers inside of each elevator, and their goal floor
	   * 
	   * @param elevArray: the array of elevators
	   * @param elevNumber: the elevator that's getting its info displayed
	   */
	  public static void outputElevatorPassengers(Elevator[] elevArray, int elevNumber)
	  {
		  ArrayList<Request> passengerArr = elevArray[elevNumber].passengers;
		  System.out.print(", and has: ");
		  for (int i = 0; i < passengerArr.size(); i++)
		  {
			  System.out.print(passengerArr.get(i).getName() +
					  " who wants to go to FLOOR " + passengerArr.get(i).getEndFloor() +
					  "\n\t\t\t\t\t\t");
		  }
		  System.out.println("\n");
	  }
	  
	  /**
	   * Outputs the floor number and the people waiting on that floor
	   * 
	   * @param totalReqs: array list of all the requests up until that time tick
	   */
	  public static void outputFloorInformation(ArrayList<Request> totalReqs)
	  {	
		  for (int floor = 1; floor <= NUM_FLOORS; floor++)
		  {
			  System.out.println("FLOOR " + floor);
			  outputPeopleWaitingOnFloor(totalReqs, floor); 
		  }
		  System.out.println();
	  }
	  
	  /**
	   * Outputs the people waiting, and their goal floor
	   * 
	   * @param totReq: array list of the people still waiting to get picked up
	   * @param floorNumber: the elevator that's getting its info displayed
	   */
	  public static void outputPeopleWaitingOnFloor(ArrayList<Request> totReq, int floorNumber)
	  {
		  for (int i = 0; i < totReq.size(); i++)
		  {
			  if (totReq.get(i).getStartFloor() == floorNumber)
			  {
				  System.out.println("--Person " + totReq.get(i).getName() +
				  			 " wants to go to FLOOR " + totReq.get(i).getEndFloor());  
			  }
		  }
	  }
	  
	  /**
	   * picks up Requests by adding them into the elevator and removing them
	   * from totalRequests for each elevator
	   * 
	   * @param totReq: array list of the people still waiting to get picked up
	   * @param elev: the array of elevators
	   */
	  public static void checkToGetOnElevator(ArrayList<Request> totReq, Elevator[] elev)
	  {
		  for (int elevNumber = 0; elevNumber < NUM_ELEVATORS; elevNumber++)
		  {  
			  // copies Request into passenger array when Request's start floor reached
			  for (int i = 0; i < totReq.size(); i++)
			  {
				  if (totReq.get(i).getStartFloor() == elev[elevNumber].getCurrentFloor())
				  {  
					  elev[elevNumber].addPersonToElevator(totReq.get(i));
					  System.out.println(totReq.get(i).getName() + " got on elevator " +
							  (elevNumber + 1) + " at floor " + totReq.get(i).getStartFloor());
					  peopleServed++;
				  }
			  }
			  
			  // then it goes back and removes it from the waiting passengers array list.
			  // removes from totalRequests from back because of ArrayList shifting.
			  for (int i = totReq.size() - 1; i >= 0; i--)
			  {
				  if (totReq.get(i).getStartFloor() == elev[elevNumber].getCurrentFloor())
				  {  
					  totReq.remove(i);
				  }
			  }
		  }
	  }
	  
	  /**
	   * drops off Requests on their goal floor (no longer keeps track of them)
	   * 
	   * @param totReq:array list of the people still waiting to get picked up
	   * @param elev: the array of elevators
	   */
	  public static void checkToGetOffElevator(ArrayList<Request> totReq, Elevator[] elev)
	  {
		  for (int elevNumber = 0; elevNumber < NUM_ELEVATORS; elevNumber++)
		  {
			  for (int i = elev[elevNumber].passengers.size() - 1; i >= 0; i--)
		  	  {
				  // removes the request from the passenger array when the goal floor is reached
				  if (elev[elevNumber].passengers.get(i).getEndFloor() == elev[elevNumber].getCurrentFloor())
				  {
					  System.out.println(elev[elevNumber].passengers.get(i).getName() + 
							  " got off elevator " + (elevNumber + 1) + " at floor " + 
							  elev[elevNumber].getCurrentFloor());
					  elev[elevNumber].passengers.remove(elev[elevNumber].passengers.get(i));
				  }
			  }   
		  }
	  }
	  
	  /**
	   * adds the requests that came at that time tick to an Array List
	   * of all Requests still waiting to get picked up
	   * 
	   * @param reqOfTimeTick: an array of requests at that time tick
	   * @param totRequests: array list of the people still waiting to get picked up
	   */
	  public static void addRequestsToTotalRequests(Request[] reqOfTimeTick, ArrayList<Request> totRequests)
	  {
		  for (int i = 0; i < reqOfTimeTick.length; i++)
		  {
			  totRequests.add(reqOfTimeTick[i]);
		  }
	  }
  
	  /**
	   * establishes all the elevators, each starting on a 1 floor difference
	   * (when the count reaches the top floor, it starts back on floor 1)
	   * 
	   * @param elev: an array of all the elevators
	   */
	  public static void establishElevators(Elevator[] elev)
	  {
		  int count = 1;
		  for (int i = 0; i < elev.length; i++)
		  {
			  int elevatorStartFloor = count % NUM_FLOORS;
			  elev[i] = new Elevator(elevatorStartFloor);
			  count++;
		  }
	  }
	  
	  /**
	   * moves each elevator depending on whether it's heading up or down
	   * 
	   * @param elev: an array of all the elevators
	   */
	  private static void moveElevator(Elevator[] elev)
	  {
		  for (int elevNumber = 0; elevNumber < elev.length; elevNumber++)
		  {
			  if (elev[elevNumber].directionUp())
			  {
				  elev[elevNumber].moveElevatorUp();
				  
				  // switches the direction downward once it reaches the top floor
				  if (elev[elevNumber].getCurrentFloor() + 1 > NUM_FLOORS)
				  {
					  elev[elevNumber].setElevatorDirectionDown(); 
				  }  
			  }
			  else
			  {
				  elev[elevNumber].moveElevatorDown();
				  
				  // switches the direction upward once it reaches the bottom floor
				  if (elev[elevNumber].getCurrentFloor() - 1 < 1)
				  {
					  elev[elevNumber].setElevatorDirectionUp(); 
				  }  
			  }
		  }
	  }
	  
	  /**
	   * finds the maximum amount of people that had to wait before getting picked up
	   * 
	   * @param totRequests: array list of the people still waiting to get picked up
	   * @param maxNumRequests: max amount. of people that had to wait before getting picked up
	   */
	  public static void statsFindMaxNumPeopleWaiting(ArrayList<Request> totRequests, int maxNumRequests)
	  {
		  if (totRequests.size() > maxNumOfRequestsAtATime)
		  {
			  maxNumOfRequestsAtATime = totRequests.size();
		  }
	  }

	  /**
	   * outputs the current tick
	   * 
	   * @param tick: the current tick number
	   */
	  private static void outputTickNumber(int tick)
	  {
		  System.out.println("TICK " + tick + "  *************************************************************************\n");
	  }
	
}
