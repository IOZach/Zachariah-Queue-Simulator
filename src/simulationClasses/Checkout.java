package simulationClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Checkout is a class that conduct till queue simulation. It allows creating an
 * object of checkout as a server and run some simulation. This class demonstrates the simulation methods.
 * The class has other methods and functions with setters and getters that conduct simulation efficiently.
 * @author Zakaria Abushima
 * @version 1.0
 **/

public class Checkout {
	
	/*** This Queue LinkedList will hold the customers during simulation*/
	private Queue<String> tillQueue;
	/*** Variables to be calculate simualtion changes, such as queue current length, customersJoind etc.*/
	private int rndNumber, timestep, serverID, currentLen, JoinedCust;
	/*** ArrayList that will store queues info such currentLength and rndNumbers over the simulation.*/
	private ArrayList<Integer> lengthList = new ArrayList<Integer>(),
			rndList = new ArrayList<Integer>();
	/*** Strings that will holds the simulation text line and style*/
	private String DataLine, SimulationStyle;

	/**
	 * Checkout Constructor method
	 * @param id This passed in from GUI class to identify each server with a Unique ID
	 */
	public Checkout(int id) {
		serverID = id;
		SimulationStyle = "";
		tillQueue = new LinkedList<String>();
		currentLen = 0;
		JoinedCust = 0;
		DataLine = "<style type=\"text/css\">#whole{font-size:9px;} .koola{color:#942929; font-weight:bold; margin-bottom:5px;} .kalam{padding:5px; font-family:tahoma; color:#4f4f4f;} .aass td{color:#4f4f4f; border: 2px solid #CC3300;border-collapse: collapse;} .header {text-align:center; font-weight:bold; color:#942929;}</style><div id=\"whole\" class=\"kalam\"><div class=\"koola\">Time&#09;Rnd&#09;Queue Status</div>";
		timestep = 0;
	}

	// Methods

	/**
	 * This method conduct the simulation
	 * @param arrivals This pass in the number of arrivals to the queue per time step
	 * @param serviceTime This pass in the number of customers has been processed per time step.
	 * @return This return the simulation text line to print to the EditorPane.
	 */
	public String simulation(int arrivals, int serviceTime) {
		
		// Check if the random Number is to add or remove customer
		if (SimulationStyle(true)) {
			// Add customers as times as Interarrival Distribution
			for (int i = 0; i < arrivals; i++) {
				tillQueue.add(String.valueOf(timestep)); // Add customer to the queue LinkedList
				currentLen++; // Increase the counter of current customers in the queue
				JoinedCust++; // Increase the counter of all customers joined the queue
			}
		} else if (SimulationStyle(false)) {
			// Remove customers as times as ServiceTime
			for (int i = 0; i < serviceTime && !tillQueue.isEmpty(); i++) {
				tillQueue.remove(); // Remove customer from the LinkedList Queue
				currentLen--; // Decrease queue current length
			}
		}
		
		// Increase current Time step over the simulation
		timestep++;
		
		// If current Queue is empty then add to the JEditorPane empty queue otherwise display the queue using LinkedList toString() method.
		if (tillQueue.isEmpty()) {
			DataLine += timestep + "&#09;" + rndNumber + "&#09;"
					+ "Queue is empty<br />";
			System.out.print(timestep + "\t" + rndNumber + "\t"
					+ "Queue is empty\t\t\t\t\t");
		} else {
			DataLine += timestep + "&#09;" + rndNumber + "&#09;"
					+ tillQueue.toString() + "<br />";
			System.out.print(timestep + "\t" + rndNumber + "\t"
					+ tillQueue.toString() + "\t\t\t\t\t");
		}

		lengthList.add(new Integer(currentLen)); // Add each currentlength of queue over simualtion to the lengthList ArrayList so we can use it for statistics
		rndList.add(new Integer(rndNumber)); // Add each rndNumber to retrievedList ArrayList so we can save the Simulation.

		return DataLine;

	}
	
	/**
	 * This method call Math Random to get Random number between 1 and 6 inclusive.
	 * Check if the current RetrievedList is equal to time step then nothing has been retrieved.
	 * If it is not equal then that mean we have retrieved simulaton from the File.
	 * So instead drawing the dice to get the Random we will get the first value of the retrievedList and remove same point we keep
	 * the RetrievedList as it should be.
	 */
	public void getRnd() {
		if (rndList.size() == timestep) {
			rndNumber = (int) (Math.random() * 6 + 1); // generates random
			// number between 1 and
			// 6.
		} else {
			rndNumber = (int) (Integer) rndList.get(0);
			rndList.remove(0);
		}
	}
	
	
	/**
	 * This method gets the RetrievedList ArrayList values and concatenate them into one string being separated with Comma,
	 * So then we can save the simulation and retrieve. the way that we save simulation is separate each number with comma in a string to be concatenated with comma
	 * @return This return concatenated string of all Random Numbers that is used to run Simulation.
	 */
	public String getrndNumbers() {
		String QueueRnds = "";
		for (int i = 0; i < rndList.size(); i++) {
			QueueRnds += (Integer) rndList.get(i).intValue() + ","; 
		}
		return QueueRnds;
	}
	
	/**
	 * This function calculates the probability percentage of customers joing and leaving and nothing occurs to the server.
	 * @param Joinflag This flag simulate the function to return the joining percentage.
	 * @param EmptyFlag This flag simulate the function to return the nothing occurs percentage.
	 * @return This otherwise return the leaving the queue percentage.
	 */
	public double probPercentage(boolean Joinflag, boolean EmptyFlag) {

		String allcustomers = getrndNumbers();
		String[] customer = allcustomers.split(",");
		double Join = 0, Leave = 0, Empty = 0;

		for (int i = 0; i < customer.length; i++) {
			if (Integer.parseInt(customer[i]) % 2 == 0) {
				Join++;
			} else if (Integer.parseInt(customer[i]) == 5) {
				Empty++;
			} else if (Integer.parseInt(customer[i]) % 2 != 0) {
				Leave++;
			}
		}
		if (Joinflag) {
			return (int) ((Join / (Join + Leave + Empty)) * 100);
		} else if (EmptyFlag) {
			return (int) ((Empty / (Join + Leave + Empty)) * 100);
		} else {
			// Leave Flag
			return (int) ((Leave / (Join + Leave + Empty)) * 100);
		}

	}
	/**
	 * This function plays important role during simulation, it consist to parts, one if the simulation style chosen as Deterministic,
	 * then we will not add pretty much customers to till and let the users determine amount of customers with full control, when they leave,
	 * time customers can be processed during and amount etc.
	 * The other part where randomness play the role to determine weather add or remove customers.
	 * @param determination This calls the add customer function, else calls the remove customer function
	 * @return This return either add or remove customers.
	 */
	public boolean SimulationStyle(boolean determination) {
		if (determination) {
			// Add Customer
			switch (SimulationStyle) {
			case "Deterministic":
				return rndNumber % 2 == 0 && rndNumber != 2 && rndNumber != 4;
			default:
				return rndNumber % 2 == 0;
			}
		} else {
			// Remove Customer
			switch (SimulationStyle) {
			case "Deterministic":
				return rndNumber % 2 != 0 && rndNumber != 5;
			default:
				return rndNumber % 2 != 0 && rndNumber != 5;
			}
		}
	}

	// Functions
	
	/**
	 * This function calculate the overall queue mean length
	 * @param Meanlengths This gives the ability to either pass in arraylist and find the meanlength out of or,
	 * get the mean length out of the arraylist in the scope of the class which is the default arraylist of lengths for the simulation.
	 * @return This return the value of the queue mean length.
	 */
	public double CalculateMeanLength(ArrayList<Integer> Meanlengths) {
		if (Meanlengths==null) {
			Meanlengths = lengthList;
		}
        double sum = 0.00;
        for(double a : Meanlengths)
            sum += a;
            return sum/Meanlengths.size();
	}
	
	/**
	 * This function calculates the maximum length out of the queue lengths.
	 * @return This return the value of maximum length.
	 */
	public int CalculateMaximumLength() {
		int MaximumLength = Integer.MIN_VALUE;
		for (int length : lengthList) {
			MaximumLength = Math.max(MaximumLength,length);
		}
		return MaximumLength;
	}
	
	/**
	 * This function calculates the minimum length out of the queue lengths.
	 * @return This return the value of minimum length.
	 */
	public int CalculateMinimumLength() {
		int MinimumLength = Integer.MAX_VALUE;
		for (int length : lengthList) {
			MinimumLength = Math.min(MinimumLength, length);
		}
		return MinimumLength;
	}
	
	/**
	 * This function calculates the variance of the queue lengths.
	 * @return This return the value of the variance.
	 */
	public double CalcVrnce() {
		  double mean = CalculateMeanLength(null);
          double temp = 0;
          for(double a : lengthList)
              temp += (mean-a)*(mean-a);
              return temp/lengthList.size();
	}

	/**
	 * This function calculates the standard deviation of the queue length.
	 * @return This return the value of the standard deviation.
	 */
	public double CalcStndrdDvtion() {
		return Math.sqrt(CalcVrnce());
	}

	/**
	 * This function calculates the median queue length.
	 * @return This return the value of the median.
	 */
	public double CalculateMedian() {
		int size = lengthList.size() - 1;
		int middle = size / 2;
		if (size % 2 == 1) {
			return lengthList.get(middle).doubleValue();
		} else {
			return (lengthList.get(middle - 1).doubleValue() + lengthList.get(
					middle).doubleValue()) / 2.0;
		}
	}
	
	/**
	 * This function calculates the queue length range.
	 * @return This return the value of the range.
	 */
	public int Calculaterange() {
		if (lengthList.isEmpty()) {
			return 0;
		} else {
			return Collections.max(lengthList) - Collections.min(lengthList);
		}
	}
	
	/**
	 * This function calculate the Y position of the graphics drawn customer starting from fixed point 60.
	 * @return This return the Y position of the graphics customer in the server queue
	 */
	public int getYPositions() {
		return 60 + (130 * getServerID());
	}

	/**
	 * This function calculate the X position of the graphics drawn customer starting from fixed point 30.
	 * @return This return the X position of the graphics customer in the server queue
	 */
	public int getXPositions(int CustomerX) {
		return 30 + (95 * CustomerX);
	}
	
	/**
	 * This function calculates the mode in the queue lengths.
	 * @return This return the modes of the queue lengths, since mode can be more than one therefore we return an ArrayList.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<Integer> CalculateMode() {
		ArrayList<Integer> modes = new ArrayList<Integer>();
		HashSet hs = new HashSet();
		int size = lengthList.size() - 1;
		int maxCount = 0;
		for (int i = 0; i < size; ++i) {
			int count = 0;
			for (int j = 0; j < size; ++j) {
				if (lengthList.get(j).intValue() == lengthList.get(i)
						.intValue())
					++count;
			}
			if (count > maxCount) {
				maxCount = count;
				modes.clear();
				modes.add(lengthList.get(i).intValue());
			} else if (count == maxCount) {
				modes.add(lengthList.get(i).intValue());
			}
		}
		hs.addAll(modes);
		modes.clear();
		modes.addAll(hs);
		return modes;
	}
	
	/**
	 * This function takes in one parameter and round it up to the second parameter decimal places.
	 * @param value This is the value to be rounded
	 * @param places This is the decimal places to be rounded to
	 * @return This return the rounded value.
	 */
	public double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	// Setters
	
	/**
	 * This setter, sets the style of the simulation either Deterministic or Random.
	 * @param style This is the passed in parameter that we want to set the style with
	 */
	public void setSimStyle(String style) {
		SimulationStyle = style;
	}
	
	/**
	 * This setter gets an ArrayList of random numbers and sets the current RetrievedList ArrayList to, usually used when retrieving simulation from text file.
	 * @param retrieved This is the passed in parameter that we want to set the current ArrayList to.
	 */
	public void setRndList(ArrayList<Integer> retrieved) {
		rndList = retrieved;
	}
	
	/**
	 * This setter plays very hard role to use logical thinking of people joining the shortest till, will be used in GUI Class
	 */
	public void addCustomer() {
		rndNumber = 6;
	}
	
	/**
	 * This setter interconnected with the previous {@link #addCustomer()} method so that we can bounce customers round the server to keep them as flattened as possible.
	 * @param addException This parameter determine in case we want to remove customer and case we want to keep the queue as it's.
	 */
	public void NotaddCustomer(boolean addException) {
		if (!addException) {
			rndNumber = 5;
		} else {
			rndNumber = 3;
		}

	}

	// Getters
	
	/**
	 * This getter function gets the server ID that is useful in case of multiple servers going to be extremely useful.
	 * @return This return the current Object server ID.
	 */
	public int getServerID() {
		return serverID;
	}
	
	/**
	 * This getter returns the current simulation queue length.
	 * @return This returns current queue length.
	 */
	public int getCurrentLength() {
		return currentLen;
	}
	
	/**
	 * This getter returns the simulation text line to print to the console.
	 * @return this return a steam of simulation text that will be printed to the console.
	 */
	public String getDataLine() {
		return DataLine;
	}
	
	/**
	 * This getter return the current number of customers joined queue over the simulation.
	 * @return This return the value of the customers joined the queue.
	 */
	public int getProcessesCount() {
		return JoinedCust;
	}

	/**
	 * This getter return the time step that simulation currently at.
	 * @return This return the current time step simulation is at.
	 */
	public int getStep() {
		return timestep;

	}
	
	/**
	 * This getter return the ArrayList of length lists that currenttly simulatio recorded.
	 * @return This return an Array List of simulation queues lengths.
	 */
	public ArrayList<Integer> getLengthList() {
		return lengthList;
	}
	
	/**
	 * This getter method calculates the statistics of the queues and pack them up in a double array.
	 * @return This return the all statistics in one double array.
	 */
	public double[] getStatistucsValues() {

		double[] StatisticsValues = new double[11];

		StatisticsValues[0] = getProcessesCount();
		StatisticsValues[1] = probPercentage(true, false);
		StatisticsValues[2] = probPercentage(false, false);
		StatisticsValues[3] = probPercentage(false, true);
		StatisticsValues[4] = CalculateMaximumLength();
		StatisticsValues[5] = CalculateMinimumLength();
		StatisticsValues[6] = CalculateMedian();
		StatisticsValues[7] = round(CalcVrnce(), 2);
		StatisticsValues[8] = Calculaterange();
		StatisticsValues[9] = round(CalcStndrdDvtion(), 2);
		StatisticsValues[10] = round(CalculateMeanLength(null), 2);

		return StatisticsValues;
	}
	
	/**
	 * This calls the previous getter {@link #getStatistucsValues()} and style and Wrap the statistics in both plain and HTML format.
	 * @return This return an array of 2 length , that contains two strings one for plain statistics and one for styled with HTML format.
	 */
	public String[] getWrappedStatistics() {

		String cm = CalculateMode().toString();
		int Tllid = getServerID() + 1;
		double[] Values = getStatistucsValues();

		String StyledCommentary = "<div class=\"header\" style=\"margin-top:5px;\">Statistics Commentary</div><p style=\"text-align:left; margin-top:2px;\">Till "
		+ Tllid
		+ " Queue Simulation has beem conducted and laid on the table some valuable statistics. The till checkout has been joined by "
		+ Values[0]
		+ " customers throughout the simulation. The queue length changes randomly every simulation cylce to give vital proportion probability of queue mean length, which has been calculated as "
		+ Values[10]
		+ ".Recording the qeueu length is so important, as we can calculate other statistics such as mode length, to find the most occured queue length throught the simulation, which has been calculated as "
		+ cm
		+ ", remember mode can be multimodal i.e. more than one. The Range of the queue length has been calculated as "
		+ Values[8]
		+ ", This means that the queue length must not be less than or greater than this range, to show the smallest queue length interval. The median queue length has been calculated as "
		+ Values[6]
		+ ". This means we can proportionally find out the probability distribution of variability, absolute deviation in the queue lengths. The queue length variance has been calculated as "
		+ Values[7]
		+ ". Collaborating with the previous calculated median we can make such a solider decicion by determining the most accurate percentage between both values. The queue length standard deviation has been calculated as "
		+ Values[9]
		+ ". Collaborating with this along with both previous median and length variance discussion we can backup the decision we have made to see any contradiction between decision and standard deviation. The queue maximum length has been calculated as "
		+ Values[4]
		+ ". The queue minimum length "
		+ Values[5]
		+ ". The customers queue joining probability has been calculated as "
		+ Values[1]
		+ "%. The customers queue leaving probability has been calculated as "
		+ Values[2]
		+ "%. The chance nothing occurs probability has been calculated as "
		+ Values[3] + "%.";
		
		String StyledStatistics = "<div class=\"kalam\"><table class=\"aass\" cellpadding=\"5\" style=\"width:100%;\"><tr><td class=\"header\" colspan=\"2\">Till "
		+ Tllid
		+ " Queue Simulation Statistics</td></tr><tr><td>Customers Joined Queue</td><td>"
		+ Values[0]
		+ "</td></tr><tr><td>Queue Mean Length</td><td>"
		+ Values[10]
		+ "</td></tr><tr><td>Queue Mode Length</td><td>"
		+ cm
		+ "</td></tr><tr><td>Queue Range Length</td><td>"
		+ Values[8]
		+ "</td></tr><tr><td>Queue Median Length</td><td>"
		+ Values[6]
		+ "</td></tr><tr><td>Queue Maximum Length</td><td>"
		+ Values[4]
		+ "</td></tr><tr><td>Queue Minimum Length</td><td>"
		+ Values[5]
		+ "</td></tr><tr><td>Queue Length Variance</td><td>"
		+ Values[7]
		+ "</td></tr><tr><td>Length Standard Deviation</td><td>"
		+ Values[9]
		+ "</td></tr><tr><td>Queue Join Probability</td><td>"
		+ Values[1]
		+ "%</td></tr><tr><td>Queue Leaving Probability</td><td>"
		+ Values[2]
		+ "%</td></tr><tr><td>Queue Nothing Occurs Probability</td><td>"
		+ Values[3] + "%</td></tr></table>" + StyledCommentary;

		String PlainCommentary = "Till "
				+ Tllid
				+ " Queue Simulation has been conducted and laid on the table some valuable statistics.\nThe till checkout has been joined by "
				+ Values[0]
				+ " customers throughout the simulation.\nThe queue length changes randomly every simulation cylce to\ngive vital proportion probability of queue mean length, which has been calculated as "
				+ Values[10]
				+ ".\nRecording the qeueu length is so important, as we can calculate\nother statistics such as mode length, to find the most occured queue length throught the simulation,\nwhich has been calculated as "
				+ cm
				+ ", remember mode\ncan be multimodal i.e. more than one. The Range of the queue length\nhas been calculated as "
				+ Values[8]
				+ ", This means that  the queue length must not be less than or greater than this range,\nto show the smallest queue length interval. The median queue length has\nbeen calculated as "
				+ Values[6]
				+ ". This means we can proportionally find out the probability\ndistribution of variability, absolute deviation in the queue lengths. The queue length variance has been calculated as "
				+ Values[7]
				+ ".\nCollaborating with the previous calculated median we can make such a solider decicion by\ndetermining the most accurate percentage between both values.\nThe queue length standard deviation has been calculated as "
				+ Values[9]
				+ ".\nCollaborating with this along with both previous median and length variance\ndiscussion we can backup the decision we have made to see any contradiction\nbetween decision and standard deviation. The queue maximum length\nhas been calculated as "
				+ Values[4]
				+ ". The queue minimum length "
				+ Values[5]
				+ ". The customers queue\njoining probability has been calculated as "
				+ (int)Values[1]
				+ "%. The customers\nqueue leaving probability has been calculated as "
				+ (int)Values[2]
				+ "%. The chance\nnothing occurs probability has been calculated as "
				+ (int)Values[3] + "%.\n";
		
		String PlainStatistics = "\nTill " + Tllid
				+ " Queue Simulation Statistics\n\n"
				+ "Customers Joind Queue \t\t" + Values[0] + "\n\n"
				+ "Queue Mean Length\t\t" + Values[10] + "\n\n"
				+ "Queue Maximum Length\t\t" + (int)Values[4] + "\n\n"
				+ "Queue Minimum Length\t\t" + (int)Values[5] + "\n\n"
				+ "Queue Mode Length\t\t" + cm + "\n\n"
				+ "Queue Median Length\t\t" + Values[6] + "\n\n"
				+ "Queue Range Length\t\t" + Values[8] + "\n\n"
				+ "Queue Length Variance\t\t" + Values[7] + "\n\n"
				+ "Queue Length Standard Deviation\t" + Values[9] + "\n\n"
				+ "Queue Join Probability\t\t" + (int)Values[1] + "%\n\n"
				+ "Queue Leave Probability\t\t" + (int)Values[2] + "%\n\n"
				+ "Queue Nothing Occur Probability " + (int)Values[3] + "%\n\n"
				+ "\t\tStatistics Commentary\n\n" + PlainCommentary;

		String[] ReadyStatistics = { PlainStatistics, StyledStatistics };

		return ReadyStatistics;

	}

}
