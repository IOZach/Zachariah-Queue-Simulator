package simulationClasses;

/**
 * importing java packages
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * GraphDraw is a class that draws Probability Distribution Diagram.
 * GraphDraw Class gets simulation data and depending on it draws a graph diagram with a stroke line start from
 * time step 1 to last time step showing off the mean length of the queues along the processes of simulation
 * i.e. state probablility distribution.
 * @author Zakaria Abushima
 * @version 1.0
 **/
@SuppressWarnings("serial")
public class GraphDraw extends JPanel {
	
	 /** * This parameter determines the padding between diagram box and all sides to start to start drawing*/ 
	private int padding = 80;
	 /** * This parameter determines the padding between x and y axes labels */ 
	private int labelPadding = 60;
	 /** * This color variables used in drawing the strings and titles*/ 
	private Color lineColor = new Color(148, 41, 41);
	 /** * This color variables used in drawing the plotted points circles.*/ 
	private Color pointColor = new Color(0, 0, 0, 100);
	 /** * This color variables used in drawing the diagram grid lines.*/ 
	private Color gridColor = new Color(200, 200, 200, 200);
	 /** * This stroke variable will be used to the draw the lines of the grid, between the points over time steps.*/ 
	private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
	 /** * This determines the size of the points plot circles.*/ 
	private int pointWidth = 6;
	 /** * This variables determines the number of Y grid lines.*/ 
	private int numberYDivisions = 15;
	 /** * This double array list will carry queue lengths mean from the constructor to whole class.*/ 
	private ArrayList<Double> lengthsMean;
	 /** * This final string for the Y axes title*/ 
	public static final String vertTitle = "Queues lengths mean";
	 /** * This final string for the X axes title*/ 
	public static final String horizTitle = "Time steps";
	 /** * This variable for the server diagram ID*/ 
	int ID = 0;
	 /** * This variable to carry the standard deviation from the constructor to the whole class.*/ 
	double stndardDeviation= 0;
	 /** * This variable to carry the queue mean length from the constructor to the whole class.*/ 
	double meanLength=0;
	
	/**
	 * Graphdraw Constrcutor which will declare its object with its parameters.
	 * @param lengthsMean This parameter gets the queues simulation lengths mean.
	 * @param id This parameter gets the ID number of the server that simulation has been done on
	 * @param meanLength This parameter returns the overall queue mean length of the server over the simulation
	 * @param stndardDeviation This parameter returns the overall queue lengths standard deviation during the simulation
	 */
	public GraphDraw(ArrayList<Double> lengthsMean, int id, double meanLength, double stndardDeviation) {
		this.lengthsMean = lengthsMean;
		ID = id + 1;
		this.stndardDeviation = stndardDeviation;
		this.meanLength = meanLength;
		repaint();
	}

	/**
	 * paintComponent method which will be called when you open the frame or resize or call repaint() method.
	 * The drawing diagram functionalities has all been integrated in this method and commented explaining each task.
	 */
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g); // call super to get the current graphics that will be drawing on
		Graphics2D g2 = (Graphics2D) g; // cast graphics into graphic 2D so can use features of graphics 2D e.g. antialiasing.
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		double xScale = ((double) getWidth() - (2 * padding) - labelPadding)
				/ (lengthsMean.size() - 1); // get the x scale axes that we will be drawing each component depending on
		double yScale = ((double) getHeight() - 2 * padding - labelPadding) // get the y axes scale that we will be drawing each component  depending on.
				/ (getMaxLength() - getMinLength());
		
		// declare an array list of points generic so that we will store mean distribution probability for each time step in
		ArrayList<Point> graphPoints = new ArrayList<Point>(); 
		
		// loop through the lengthsMean array list to get each time step queue length mean and store it in the graph points Point array list
		// so that using the previous declared x and y scale will organise the points of each mean length according to the time step.
		for (int i = 0; i < lengthsMean.size(); i++) {
			int x1 = (int) (i * xScale + padding + labelPadding);
			int y1 = (int) ((getMaxLength() - lengthsMean.get(i)) * yScale + padding);
			graphPoints.add(new Point(x1, y1));
		}
		
		// draw graph title
		g2.setFont(new Font("Tahoma", Font.BOLD, 20));
		g2.setColor(lineColor);
		g2.drawString("Server " + ID + " Processes Probability distribution",
				getWidth() / 2 - 190, 40);
		
		// draw x and y axes title
		g2.setColor(lineColor);
		g2.setFont(new Font("Arial", Font.BOLD, 15));
		for (int i = 0; i < vertTitle.length(); i++) {
			FontMetrics metrics = g2.getFontMetrics();
			String yLabel = vertTitle.substring(i, i + 1);
			int labelWidth = metrics.stringWidth(yLabel);
			g2.drawString(yLabel, 50-(labelWidth/2), (metrics.getHeight()+90)+ (i * 20));
		}
		g2.drawString(horizTitle, getWidth() / 2 - 10, getHeight() - 80);

		// draw graph bottom bar that contains queue length standard deviation and overall mean length
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", Font.PLAIN, 15));
		g2.drawString("Queue length standard deviation = √1∕n-1N∑i =1(i^x - µ)^2 = "+stndardDeviation,
				getWidth() / 2 - 350,getHeight()-50);
		g2.drawString("Mean length of the queue: ∑Ki=c(i−c)×πi = "+meanLength,
				getWidth() / 2 - 350,getHeight()-20);

		// draw white background so to plot on the mean length probability distribution
		g2.setFont(new Font("Arial", Font.PLAIN, 10));
		g2.setColor(Color.WHITE);
		g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding)
				- labelPadding, getHeight() - 2 * padding - labelPadding);
		g2.setColor(Color.BLACK);

		// draw on the background a hatch marks and grid lines for y axis.
		for (int i = 0; i < numberYDivisions + 1; i++) {
			int x0 = padding + labelPadding;
			int x1 = pointWidth + padding + labelPadding;
			int y0 = getHeight()
					- ((i * (getHeight() - padding * 2 - labelPadding))
							/ numberYDivisions + padding + labelPadding);
			int y1 = y0;
			if (lengthsMean.size() > 0) {
				g2.setColor(gridColor);
				g2.drawLine(padding + labelPadding + 1 + pointWidth, y0,
						getWidth() - padding, y1);
				g2.setColor(Color.BLACK);
				String yLabel = ((int) ((getMinLength() + (getMaxLength() - getMinLength())
						* ((i * 1.0) / numberYDivisions)) * 100))
						/ 100.0 + "";
				FontMetrics metrics = g2.getFontMetrics();
				int labelWidth = metrics.stringWidth(yLabel);
				g2.drawString(yLabel, x0 - labelWidth - 5,
						y0 + (metrics.getHeight() / 2) - 3);
			}
			g2.drawLine(x0, y0, x1, y1);
		}

		// and for x axis
		for (int i = 0; i < lengthsMean.size(); i++) {
			if (lengthsMean.size() > 1) {
				int x0 = i * (getWidth() - padding * 2 - labelPadding)
						/ (lengthsMean.size() - 1) + padding + labelPadding;
				int x1 = x0;
				int y0 = getHeight() - padding - labelPadding;
				int y1 = y0 - pointWidth;
				if ((i % ((int) ((lengthsMean.size() / 20)) + 1)) == 0) {
					g2.setColor(gridColor);
					g2.drawLine(x0, getHeight() - padding - labelPadding - 1
							- pointWidth, x1, padding);
					g2.setColor(Color.BLACK);
					String xLabel = (i + 1) + "";
					FontMetrics metrics = g2.getFontMetrics();
					int labelWidth = metrics.stringWidth(xLabel);
					g2.drawString(xLabel, x0 - labelWidth / 2,
							y0 + metrics.getHeight() + 3);
				}
				g2.drawLine(x0, y0, x1, y1);
			}
		}

		// create x and y axes
		g2.drawLine(padding + labelPadding, getHeight() - padding
				- labelPadding, padding + labelPadding, padding);
		g2.drawLine(padding + labelPadding, getHeight() - padding
				- labelPadding, getWidth() - padding, getHeight() - padding
				- labelPadding);
		
		// draw on stroke line that start from timestep 1 to last step and plot off the mean lengths over the queue simulation each step
		// and how does effects of state probability distribution take place.
		Stroke oldStroke = g2.getStroke();
		g2.setColor(lineColor);
		g2.setStroke(GRAPH_STROKE);
		for (int i = 0; i < graphPoints.size() - 1; i++) {
			int x1 = graphPoints.get(i).x;
			int y1 = graphPoints.get(i).y;
			int x2 = graphPoints.get(i + 1).x;
			int y2 = graphPoints.get(i + 1).y;
			g2.drawLine(x1, y1, x2, y2);
		}
		
		// plot on each point of distribution a cirlce so that will show up clearly each point.
		g2.setStroke(oldStroke);
		g2.setColor(pointColor);
		for (int i = 0; i < graphPoints.size(); i++) {
			int x = graphPoints.get(i).x - pointWidth / 2;
			int y = graphPoints.get(i).y - pointWidth / 2;
			int ovalW = pointWidth;
			int ovalH = pointWidth;
			g2.fillOval(x, y, ovalW, ovalH);
		}
	}
	
	/**
	 * This function will return the minimum mean in the length list
	 * This function helps the scale determination from which point to start and to end
	 * @return the minimum queue lengths mean
	 */
	private double getMinLength() {
		double minMean = Integer.MAX_VALUE;
		for (Double mean : lengthsMean) {
			minMean = Math.min(minMean, mean);
		}
		return minMean;
	}

	/**
	 * This function will return the maximum mean in the length list
	 * This function helps the scale determination from which point to start and to end
	 * @return The maximum queue lengths mean
	 */
	private double getMaxLength() {
		double maxMean = Integer.MIN_VALUE;
		for (Double mean : lengthsMean) {
			maxMean = Math.max(maxMean, mean);
		}
		return maxMean;
	}
}