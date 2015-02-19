package simulationClasses;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLDocument;

/**
 * This class demonstrates the simulation application graphical user interface.
 * 
 * @author Zakaria Abushima
 * @version 1.0
 **/
@SuppressWarnings("serial")
public class GUI extends JPanel {

	/** * File chooser component variable to be used while saving, loading files to the simulator */
	private JFileChooser fc = new JFileChooser();
	/** * The main application Menubar component variable */
	JMenuBar menubar = new JMenuBar(); 
	/** * The main menubar items */
	private JMenu toolsMenu, fileMenu; 
	/** * The items of the menubar items. */
	private JMenuItem RunSim, DisplaySim, RetrieveStats, SaveStats, 
			SaveSimulation, StopSimulation, AboutSimulator, Diagrams,
			pauseSimulationBtn, ResumeSimulationBtn;
	/** * The Toolbar component variable that will hold simulation settings text fields and labels. */
	JToolBar toolbar = new JToolBar(); 

	/*** Timer to use to schedule addition and removal of customers.*/
	private Timer timer; 
	/*** Integers to be used across the application to determine simulation settings.*/
	private int speed, timesteps, serverCount, bufferSize, processesCounter,
			serviceRate, stepCounter, bottlenecks, necksCounter,
			arrivalPerTimeStep, serviceTime;
	/*** Simulation Speed Bar JSlider .*/
	private JSlider SimulationSpeedBar;

	/*** buffered image to draw servers and customers on.*/
	private BufferedImage buffer;
	/*** buffered image fixed width .*/
	private int bufferWidth = 600; // 
	/*** buffered image dynamic height .*/
	private int bufferHeight;
	/*** ImagePanel extends from JPanel to draw graphics on .*/
	private ImagePanel GraphicDisplay = new ImagePanel();
	/*** GraphDraw extends from JPanel as well to draw graphs on .*/
	private GraphDraw Diagram;
	/*** JPanel to display text side of simulation .*/
	private JPanel TextDisplay = new JPanel();

	/**
	 * Scrollpanes used across the application to be used as a holder with fixed width and height to the content, so size of content doesnt affect outer Panels
	 * Graphics, Text display scroll pane , with Text Simulation ScrollPane.
	 */
	private JScrollPane GraphicsScrollPane, TextScrollPane, TextSimulationScrollPane;

	/***  Notification final string to be used for JOptionPane.*/
	private final String Notification = "Notification";
	/***  Copyrights string to be used in footer.*/
	private final String CopyRights = "Powered by ZAStudio";
	/***  String array to hold style of simulations .*/
	private final String[] StyleList = { "Deterministic", "Random" };
	/***  String array to hold style of statistics to be saved .*/
	private final Object[] StylingSave = { "Plain Statistics","Styled Statistics" };

	/**
	 * These are Text Fields declaration, with width 10, to be used for setting simulation configuration.
	 * Time steps field, Servers amount field, processes per time step field, 
	 */
	private JTextField timestepsField = new JTextField(10), 
			serversField = new JTextField(10), bufferSizeField = new JTextField( 
					10), serviceRateField = new JTextField(10), 
			bottlenecksField = new JTextField(10), 
			arrivalPerTimeStepField = new JTextField(10),
			serviceTimeField = new JTextField(10);
	
	/**
	 * These are labels to label text fields and be readable to the users, defining each text field with it's job.
	 * Time steps , server amount , simulation speed, simulation style , buffer size, Text display server title, customers to be processed per time step,
	 * bottlenecks , arrival per time step , service time label.
	 */
	private JLabel timestepslbl, serverslbl, simSpeedlbl, simStylelbl,
			bufferSizelbl, txtDisplayServerTitle, processesPerTimePriodlbl,
			bottleneckslbl, arrivalPerTimeSteplbl, serviceTimelbl;
	/*** Protected lables to be shared across other classes such as application title, copyrights lbl etc.*/
	protected JLabel ApplicationTitle, TextFieldsSimulation, CopyRightsLBL;
	
	/*** This is combo box to hold simulation styles that users of application navigate through to edit the style.*/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JComboBox SimulationStyle = new JComboBox(StyleList);

	/** * The checkout server that will be used to run simulation */
	public static Checkout server;
	/** * The ArrayList of servers that will be used in multiple servers */
	public ArrayList<Checkout> servers = new ArrayList<Checkout>();
	/** * The Editor pane arraylist that will hold the servers simulation to display in text format in case of multiple servers */
	public ArrayList<JEditorPane> TxtDisplaySimulationHolder;
	/** * The editorpane that will hold each simulation */
	public static JEditorPane SimulateField;
	/** * The ArrayList of integer array to hold the servers simulation random numbers, this will be used at the time of retrieving data */
	private ArrayList<int[]> rndlist = new ArrayList<int[]>();
	/** * This array will hold all media meterials are used through the media list */
	protected ArrayList<Image> mediaList = new ArrayList<Image>();
	/** * The html editor kit will be used to display simulation in text style with some HTML formats to make it nicer. */
	private HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
	/** * The hastable will be used to add to JSlider its scale labels */
	Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();

	/** * Boolean flags used to implements Semaphoeres, 
	 * saveFlag does the job to check if any simulation has been saved or no so we allow to save Simulation
	 * StatisticsCalculated Flag will check if we have calculated flag in case true then will activate save option
	 * StoppedSimulationFlag will check if we have stopped simulation or not
	 * This will check the last iteration of the simulation, so we want to print some other text when we finish simulation
	 * PauseFlag is to tell if we have paused the simulation or not so we can resume or pause
	 * JoiningLogicFlag is to determine if customers would join this server depending on simulation settings you have setup or if it has smallest queue length etc.. */
	private boolean saveFlag = false, StatisticsCalculatedFlag = false,
			stopedSimulationFlag = false, LastIterFlag = false, PauseFlag = false,
			JoiningLogicFlag = false;

	/*** Strings that will be used across the application
	 * SavedQueues holds the Queues content of random numbers to be used in case save simulation has been conducted
	 * SStyle will contain a string of the current chosen simulation style */
	private String SavedQueues, SStyle = StyleList[0];
	/*** This string array will contain the two style of simulation statistics either plain or styled */
	private String[] SavedStyled = new String[2];
	
	/*** The constructor to setup the GUI that extends from JPanel */
	public GUI() {
		
		setStarter();
		instantTimer();
		setBufferedSize();
		
		// About simulator menu item
		AboutSimulator = new JMenuItem("About Simulator");
		AboutSimulator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								null,
								"Zakaria's Queue Simulator is a smart well coded simulation environment,\nconduct simulations experimentally on checkout tills to give potential probability\nfor queue lengths over deterministic and random style. This application was\nthe first of its type for Zakaria to programm for his second year\nJava module assigment.\n\nThis Application's version 1.0. 01-07-2014 \nCheck www.zastudio.net for new updates.\nEmail: zak@zastudio.net for enquiries.",
								"About Zakaria's Queue Simulator",
								JOptionPane.WARNING_MESSAGE, new ImageIcon(
										mediaList.get(5)));
			}
		});
		
		// New Simulation menu item
		RunSim = new JMenuItem("New Simulation");
		RunSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startSimulation();
			}
		});
		
		// Pause Simulation menu item
		pauseSimulationBtn = new JMenuItem("Pause Simulation");
		pauseSimulationBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pauseSimulation();
			}
		});
		
		// Resume Simulation menu item
		ResumeSimulationBtn = new JMenuItem("Resume Simulation");
		ResumeSimulationBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resumeSimulation();
			}
		});
		
		// Stop simulation menu item
		StopSimulation = new JMenuItem("Finish Simulation");
		StopSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopSimulation();
			}
		});
		
		// Calculate Statistics menu item
		DisplaySim = new JMenuItem("Calculate Statistics");
		DisplaySim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getStatistics();
			}
		});
		
		// Retrieve Simulation menu item
		RetrieveStats = new JMenuItem("Retrieve Simulation");
		RetrieveStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				retrieveSimulation();
			}
		});
		
		// Save Simulation menu item.
		SaveSimulation = new JMenuItem("Save Simulations");
		SaveSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSimulation();
			}
		});
		
		// Save statistics menu item.
		SaveStats = new JMenuItem("Save Statistics");
		SaveStats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveStatistics();
			}
		});
		
		// Get diagrams menu item
		Diagrams = new JMenuItem("Get Diagrams");
		Diagrams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getDiagram();
			}
		});
		
		// Buffer size text field
		ClearTextField(bufferSizeField);
		bufferSizeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setBufferSize();
			}
		});
		
		// time steps text field
		ClearTextField(timestepsField);
		timestepsField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTimeSteps();
			}
		});
		
		// service time field
		ClearTextField(serviceTimeField);
		serviceTimeField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setServiceTime();
			}
		});

		// processes per time period field
		ClearTextField(serviceRateField);
		serviceRateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setServiceRate();
			}
		});
		
		// interarrival distribution field
		ClearTextField(arrivalPerTimeStepField);
		arrivalPerTimeStepField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setarrivalPerTimeStep();
			}
		});
		
		// bottlenecks field
		ClearTextField(bottlenecksField);
		bottlenecksField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setBottleNecks();
			}
		});
		
		// servers field
		ClearTextField(serversField);
		serversField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setServersCount();
			}
		});
		
		// Simulation speed bar slider
		SimulationSpeedBar.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setSimulationSpeed((JSlider) e.getSource());
			}
		});
		
		// simulation style combo box
		SimulationStyle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSimulationStyle();
			}
		});

		setupComponent();
	}
	
	/**
	 * This method declare and assign starter text fields, labels and integers, this method should be run at the very first moment of the application or it will crash.
	 * This is the starter of the application
	 */
	private void setStarter() {
		
		// Declare and assign text fields
		timestepslbl = new JLabel("Time Steps");
		serverslbl = new JLabel("Servers");
		simSpeedlbl = new JLabel("Speed");
		simStylelbl = new JLabel("Style");
		bufferSizelbl = new JLabel("Buffer Size");
		processesPerTimePriodlbl = new JLabel("Service Rate");
		arrivalPerTimeSteplbl = new JLabel("Interarrival Distribution");
		bottleneckslbl = new JLabel("Bottlenecks");
		serviceTimelbl = new JLabel("Service Time");
		
		// Declare and assign speed slider
		SimulationSpeedBar = new JSlider(JSlider.HORIZONTAL, 0, 500, 100);
		SimulationSpeedBar.setInverted(true);
		Font Sliderfont = new Font("Serif", Font.ITALIC, 0);
		SimulationSpeedBar.setFont(Sliderfont);
		SimulationSpeedBar.setMajorTickSpacing(10);
		SimulationSpeedBar.setOpaque(false);
		SimulationSpeedBar.setPaintTicks(true);
		SimulationSpeedBar.setPaintLabels(true);
		SimulationSpeedBar.setPaintLabels(true);
		
		// delcare and assign panels and image panels and scroll panes
		TextDisplay.setBackground(Color.white);
		GraphicsScrollPane = new JScrollPane(GraphicDisplay);
		TextScrollPane = new JScrollPane(TextDisplay);
		GraphicsScrollPane.setBackground(Color.white);
		
		// declare and assign integers.
		timesteps = Integer.MAX_VALUE;
		bufferSize = Integer.MAX_VALUE;
		bottlenecks = Integer.MAX_VALUE;
		serviceRate = Integer.MAX_VALUE;
		arrivalPerTimeStep = 1;
		serverCount = 1;
		speed = 50;
		serviceTime = 1;
		
		// set shotooltip delay to 0
		ToolTipManager.sharedInstance().setInitialDelay(0);

		// declare and assign labels.
		ApplicationTitle = paintLbl(ApplicationTitle,
				"Zakaria's Queue Simulator System", new Font("Serif",
						Font.PLAIN, 35), new EmptyBorder(10, 10, 10, 10),
				new Color(148, 41, 41), "Where simulation is so simple!");
		TextFieldsSimulation = paintLbl(TextFieldsSimulation,
				"Simulation Text Display", new Font("Tahoma", Font.BOLD, 18),
				new EmptyBorder(10, 10, 7, 10), new Color(148, 41, 41), null);
		CopyRightsLBL = paintLbl(CopyRightsLBL, CopyRights, new Font(
				"Sans Serif", Font.BOLD, 12), new EmptyBorder(5, 0, 5, 0),
				new Color(81, 81, 81), null);
		
		// adding paddings to labels
		serverslbl.setBorder(new EmptyBorder(0, 5, 0, 5));
		timestepslbl.setBorder(new EmptyBorder(0, 5, 0, 5));
		simSpeedlbl.setBorder(new EmptyBorder(0, 5, 0, 5));
		simStylelbl.setBorder(new EmptyBorder(0, 5, 0, 5));
		bufferSizelbl.setBorder(new EmptyBorder(0, 5, 0, 5));
		bottleneckslbl.setBorder(new EmptyBorder(0, 5, 0, 5));
		arrivalPerTimeSteplbl.setBorder(new EmptyBorder(0, 5, 0, 5));
		serviceTimelbl.setBorder(new EmptyBorder(0, 5, 0, 5));
		processesPerTimePriodlbl.setBorder(new EmptyBorder(0, 5, 0, 5));
		
		// load up all media materals.
		try {
			for (int i = 0; i < 13; i++) {
				mediaList.add(ImageIO.read(new File("src/media/" + i + ".png")));
			}
		} catch (IOException e1) {
			JOptionPane
					.showMessageDialog(
							null,
							"Simlator will not be running as expected.\nCouldn't load graphics materials.\nLocated in 'media' directory.",
							Notification, JOptionPane.WARNING_MESSAGE);
		}

	}
	
	/**
	 * This method declare and assign the buffered image that will contain graphic simulation with the appropriate size, when ever start simulation it will be called
	 */
	private void setBufferedSize() {

		if (servers.size() > 3) {
			bufferHeight = 145 * servers.size();
			if (servers.size() >= 10) {
				bufferHeight = bufferHeight - (servers.size() * 7);
			}
		} else {
			bufferHeight = 455;
		}
		buffer = new BufferedImage(bufferWidth, bufferHeight,
				BufferedImage.TYPE_INT_ARGB);
		TextDisplay.invalidate();
		TextDisplay.setPreferredSize(new Dimension(420, bufferHeight));
		GraphicDisplay.setPreferredSize(new Dimension(600, bufferHeight));
		GraphicDisplay.drawBackground(buffer.createGraphics());
	}
	

	/**
	 * This method responsible for running simulation every time you click new simulation
	 * Clears previous unwanted parameters and dispatch the simulation 
	 * This method also responsible for half the job of retrieving simulations, {@link GUI#rndlist} , using the ArrayList of int array having all numbers need to be added to servers.
	 * This method also , prints the starter of simulation at the console and then leave the rest to the timer.
	 */
	private void startSimulation() {

		processesCounter = 0;
		stepCounter = 0;
		necksCounter = 0;
		StopSimulation.doClick();
		TextDisplay.removeAll();
		instantTimer();
		TextDisplay.add(TextFieldsSimulation);
		servers = new ArrayList<Checkout>();
		TxtDisplaySimulationHolder = new ArrayList<JEditorPane>();

		for (int i = 0; i < serverCount; i++) {

			SimulateField = new JEditorPane();
			SimulateField.setEditable(false);
			SimulateField.setContentType("text/html");

			TextSimulationScrollPane = new JScrollPane(SimulateField);
			TextSimulationScrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			TextSimulationScrollPane.setPreferredSize(new Dimension(375, 100));

			TextDisplay.add(paintLbl(txtDisplayServerTitle, "Server(" + (i + 1)
					+ ") Queue Simulation.",
					new Font("Tahoma", Font.PLAIN, 15), new EmptyBorder(0, 0,
							0, 193), new Color(148, 41, 41), null));
			TextDisplay.add(TextSimulationScrollPane);

			server = new Checkout(i);

			if (!rndlist.isEmpty()) {

				ArrayList<Integer> RNDlist = new ArrayList<>(
						rndlist.get(i).length);

				for (int val : rndlist.get(i)) {
					RNDlist.add(val);
				}

				timesteps = RNDlist.size();
				server.setRndList(RNDlist);
			}

			server.setSimStyle(SStyle);

			servers.add(server);
			TxtDisplaySimulationHolder.add(SimulateField);
			System.out.print("Server " + (i + 1)
					+ " Queue Simulation\t\t\t\t\t");
		}

		System.out.println("\n");

		for (int i = 0; i < serverCount; i++) {

			System.out.print("Time\tRnd\tQueue Status\t\t\t\t");

		}

		System.out.println("\n");

		setBufferedSize();
		TextDisplay.revalidate();
		TextDisplay.updateUI();
		GraphicsScrollPane.getViewport().revalidate();
		TextScrollPane.getViewport().revalidate();
		timer.start();
		saveFlag = true;
	}
	
	/*** This method takes the reponsibility of pausing the simulation,
	 * basically , it tells the other methods that we have paused the simulation using a flag , then stop the timer */
	private void pauseSimulation() {
		if (timer.isRunning()) {
			PauseFlag = true;
			timer.stop();
		}
	}
	
	/*** This resume the simulation continueing from last moment of the pause, method working in collaboration with {@link #pauseSimulation()} method so that it check if the {@link #PauseFlag} is true then it will
	 * return the {@link #PauseFlag} to false and instantiate a new timer and start it */
	private void resumeSimulation() {
		if (!timer.isRunning() && PauseFlag) {
			PauseFlag = false;
			instantTimer();
			timer.start();
		}
	}
	
	/**
	 * This method stop the simulation and run the last iteraton step to tell on the grpahics side that is we finished,
	 * Also this method tell other methods that simulation is finished.
	 */
	private void stopSimulation() {
		if (timer.isRunning() || PauseFlag) {
			timer.stop();
			PauseFlag = false;
			stopedSimulationFlag = true;
			rndlist.clear();
			LastIterFlag = true;
			GraphicDisplay.drawEverything(buffer.getGraphics());
			LastIterFlag = false;
			System.out.println();
		}
	}
	
	/**
	 * This method responsible for the work of the simulation speed slider
	 * it has been surrounded with a try catch in case of any issues it catch it when it should be impossible
	 * This method deals with the {@link #reSetTimer()} method so that each time I change the speed it stop timer and then re-instantiate the timer
	 * @param source this source return the value of current chosen speed using #getValue() built-in method.
	 */
	private void setSimulationSpeed(JSlider source) {
		try {
			speed = (int) source.getValue();
			reSetTimer();
		} catch (Exception e1) {
			JOptionPane
					.showMessageDialog(
							null,
							"Unrecognised procedure gone wrong.\nContact zak@zastudio.net",
							Notification, JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * This method working with the {@link #serversField} text field and setting the amount of servers in the simulation
	 * It takes an integer in case it zero then servers will be set to 1 otherwise as specified.
	 */
	private void setServersCount() {
		StopSimulation.doClick();
		try {

			if (Integer.parseInt(serversField.getText()) == 0
					|| serversField.getText() == null) {
				JOptionPane
						.showMessageDialog(
								null,
								"The simulation will be running with 1 Server, \nunless you assign a number in the Servers\ncount field that ranges from 1 to infinity.\nRun New Simulation from Tools Menu.",
								Notification, JOptionPane.WARNING_MESSAGE);
				serverCount = 1;
			} else {
				serverCount = Integer.parseInt(serversField.getText());
				JOptionPane
						.showMessageDialog(
								null,
								"Successfully, Defined the server amount as "
										+ serverCount
										+ " servers.\nRun New Simulation from Tools Menu.",
								Notification, JOptionPane.WARNING_MESSAGE);
			}
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "Integers number only",
					Notification, JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * This method working with the {@link #serviceTimeField} text field and setting the service time in the simulation
	 * It takes an integer in case it zero then service time will be random otherwise as specified.
	 * The service time means that the amount of time taken to for each customer to be served, in other word the amount of customers to leave the till per time step
	 */
	private void setServiceTime() {
		StopSimulation.doClick();
		try {
			if (Integer.parseInt(serviceTimeField.getText()) <= 1) {
				JOptionPane
						.showMessageDialog(
								null,
								"The simulation will be running unlimitedly,\naccording to buffer size / time steps unless you stop\nfrom the top tools menu, or assign a number\nin time steps field to limit simulation.\nRun New Simulation from Tools Menu.",
								Notification, JOptionPane.WARNING_MESSAGE);
				serviceTime = 1;
			} else {
				serviceTime = Integer.parseInt(serviceTimeField.getText());
				JOptionPane.showMessageDialog(null,
						"Successfully, defined service time as " + serviceTime
								+ " processes per time step.", Notification,
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "Integers number only",
					Notification, JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * This method working with the {@link #timesteps} text field and setting the amount of time step in the simulation
	 * It takes an integer in case it zero then then simulation will be unlimitedly running otherwise as specified.
	 * The time steps can be anything , 3 minutes , per minutes , per seconds , per half seconds , so simulation will be running just as the number you provide ignoring the measurement
	 */
	private void setTimeSteps() {
		try {
			if (Integer.parseInt(timestepsField.getText()) == 0) {
				JOptionPane
						.showMessageDialog(
								null,
								"The simulation will be running unlimitedly,\naccording to customers rate unless you stop\nfrom the top tools menu, or assign a number\nin time steps field to limit simulation.\nRun New Simulation from Tools Menu.",
								Notification, JOptionPane.WARNING_MESSAGE);
				timesteps = Integer.MAX_VALUE;
			} else {
				timesteps = Integer.parseInt(timestepsField.getText());
				bufferSize = Integer.MAX_VALUE;
				JOptionPane.showMessageDialog(null,
						"Successfully, defined time steps as " + timesteps
								+ " steps.\nto run unlimitedly enter 0.",
						Notification, JOptionPane.WARNING_MESSAGE);
			}
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "Integers number only",
					Notification, JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * This method working with the {@link #bufferSizeField} text field and setting the overall amount of customers going to join in the servers
	 * It takes an integer in case it zero then then simulation will be unlimitedly running otherwise as specified.
	 * The amount of customers called as buffer size (k) will run the simulation until alltogether servers joined customers reach the buffer size.
	 */
	private void setBufferSize() {
		try {
			if (Integer.parseInt(bufferSizeField.getText()) == 0) {
				JOptionPane
						.showMessageDialog(
								null,
								"The simulation will be running unlimitedly,\naccording to the time steps uless you stop\nfrom the top tools menu, or assign a number\nin buffer size field to limit simulation.\nRun New Simulation from Tools Menu.",
								Notification, JOptionPane.WARNING_MESSAGE);
				bufferSize = Integer.MAX_VALUE;
			} else {
				bufferSize = Integer.parseInt(bufferSizeField.getText());
				timesteps = Integer.MAX_VALUE;
				JOptionPane.showMessageDialog(null,
						"Successfully, Defined pool of customers as "
								+ bufferSize
								+ " customers.\nto run unlimitedly enter 0.\n Note this buffersize might not end simulation exactly as it should be due to having interarrvial distribution greater than the average, yet\nit might cause a very minor difference, considered as nothing.",
						Notification, JOptionPane.WARNING_MESSAGE);
			}
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "Integers number only",
					Notification, JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * This method working with the {@link #serviceRateField} text field and setting the amount of customers shouldn't be exceeded by any server every single simulation.
	 * It takes an integer in case it zero then then server lengths will not have any limits on otherwise as specified.
	 * Service Rate means that each server each simulation will have at their current length a maximum of service rate maybe 3 customers etc and won't exceed, unless interarrival distribution greater to exceed
	 */
	private void setServiceRate() {
		StopSimulation.doClick();
		try {
			if (Integer.parseInt(serviceRateField.getText()) == 0) {
				JOptionPane
						.showMessageDialog(
								null,
								"The simulation will be running unlimitedly,\naccording to time steps and buffer size unless you stop\nfrom the top tools menu, or assign a number\nin processes/timesteps field to limit simulation.\nRun New Simulation from Tools Menu.",
								Notification, JOptionPane.WARNING_MESSAGE);
				serviceRate = Integer.MAX_VALUE;
			} else {
				serviceRate = Integer
						.parseInt(serviceRateField.getText());
				JOptionPane
						.showMessageDialog(
								null,
								"Successfully, Limited service rate of servers as "
										+ serviceRate
										+ " processers per time period.\nto run unlimitedly enter 0.",
								Notification, JOptionPane.WARNING_MESSAGE);
			}
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "Integers number only",
					Notification, JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * This method working with the {@link #arrivalPerTimeStepField} text field and setting the amount of customers at once can joing the tills
	 * It takes an integer in case it zero then then server arrival amount will be 1 otherwise as specified.
	 * Interarrival distribution means that each server at each simulation will recieve a number of customers joining the tell in every single simulation step which is called interarrival distribution.
	 */
	private void setarrivalPerTimeStep() {
		StopSimulation.doClick();
		try {
			if (Integer.parseInt(arrivalPerTimeStepField.getText()) <= 1) {
				JOptionPane
						.showMessageDialog(
								null,
								"The simulation will be running at 1 customers each time step unless you\nassign a number greater than 1.\nRun New Simulation from Tools Menu.",
								Notification, JOptionPane.WARNING_MESSAGE);
				arrivalPerTimeStep = 1;
			} else {
				arrivalPerTimeStep = Integer.parseInt(arrivalPerTimeStepField
						.getText());
				JOptionPane.showMessageDialog(null,
						"Successfully, Defined arrival time of processes as "
								+ arrivalPerTimeStep
								+ " per time step.\nDefine as 0 to default.",
						Notification, JOptionPane.WARNING_MESSAGE);
			}
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "Integers number only",
					Notification, JOptionPane.WARNING_MESSAGE);
		}

	}

	/**
	 * This method working with the {@link #bottlenecksField} text field and setting the amount of queue length at once can be done in if something exceed, all the system will stop working
	 * and is known as one server has been saturated then all server in the system will stop working which this what bottlenecks option here does if any server exceed this limit simulation will stop imediately
	 * It takes an integer in case it zero then the bottleneck will be unlimited otherwise as specified.
	 * Bottlenecks means at any time any server exceed the bottleneck then it been treated as saturated and all system is down.
	 */
	private void setBottleNecks() {
		StopSimulation.doClick();
		try {
			if (Integer.parseInt(bottlenecksField.getText()) == 0) {
				JOptionPane
						.showMessageDialog(
								null,
								"The simulation will be running unlimitedly,\naccording to time steps and processes unless you stop\nfrom the top tools menu, or assign a number\nin processes/timesteps field to limit simulation.\nRun New Simulation from Tools Menu.",
								Notification, JOptionPane.WARNING_MESSAGE);
				bottlenecks = Integer.MAX_VALUE;
			} else {
				bottlenecks = Integer.parseInt(bottlenecksField.getText());
				JOptionPane
						.showMessageDialog(
								null,
								"Successfully, Servers utilization point as "
										+ bottlenecks
										+ " current processers per time.\nto run unlimitedly enter 0.",
								Notification, JOptionPane.WARNING_MESSAGE);
			}
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "Integers number only",
					Notification, JOptionPane.WARNING_MESSAGE);
		}

	}
	
	/**
	 * This method working with the {@link #SStyle} string of combobox and set the style of simulation either using random numbers or deterministic values 
	 * The combo box has two options 
	 * <ul>Simulation Style
	 * <li>Deterministic</li>
	 * <li>Random </li></ul>
	 * The random uses random numbers at all times to generate customers joining and leaving, when deterministic rarely add numbers giving you 99% off chance to determine the customers
	 * where to join and leave at what rate using the simulation toolbar, althught you still can run random and limit using the toolbar yet I made the application felt it would be good 
	 * if you use random for all giving random statistics and deterministic when you want certain option or know some before hand info so you run simulation on something is real.
	 */
	private void setSimulationStyle() {
		StopSimulation.doClick();
		SStyle = SimulationStyle.getSelectedItem().toString();
		JOptionPane
				.showMessageDialog(
						null,
						"Successfuly defined Simulation Style as "
								+ SStyle
								+ ".\nDeterministic Style means you can fully control the\nsimulation parameter, using service time, interarrival distribution etc.\nRandom style means just randomaly add a customers",
						Notification, JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * This method check if simulation has been ran if yes then, get how many servers are availables, create frame, dispatch, and conduct GraphDraw class on
	 * The frame added with feature of resizing the graph make it bigger, and save it.
	 * If you click save this diagram then save dialog will pop up and chooese the place to save 
	 */
	public void getDiagram() {
		try {
			StopSimulation.doClick();
			if (!saveFlag) {
				JOptionPane.showMessageDialog(null, "Run Simulation First.",
						Notification, JOptionPane.ERROR_MESSAGE);
			} else {
				String[] nums = new String[servers.size()];
				for (int i = 0; i < nums.length; i++) {
					nums[i] = "" + (i + 1);
				}
				String s = (String) JOptionPane.showInputDialog(null,
						"Choose the server number:-\n", "Statistics Diagram",
						JOptionPane.PLAIN_MESSAGE, null, nums, "");
				if (s != null) {
					JFrame frame = new JFrame(ApplicationTitle.getText()
							+ " - Diagrams");
					Checkout server = servers.get(Integer.parseInt(s) - 1);
					Diagram = new GraphDraw(getMeanList(server),server.getServerID(),server.CalculateMeanLength(null),server.CalcStndrdDvtion());
					Diagram.setBackground(Color.white);
					frame.setLayout(new BorderLayout());
					JButton save = new JButton("Save this Diagram");
					save.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							saveFile(null, false, true, getScreenShot(Diagram));
						}
					});
					save.setHorizontalAlignment(SwingConstants.CENTER);
					frame.getContentPane().add(Diagram, BorderLayout.CENTER);
					frame.getContentPane().add(save, BorderLayout.SOUTH);
					frame.setSize(800, 700);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				}
			}
		} catch (NumberFormatException | HeadlessException e1) {

		}
	}
	
	/**
	 * This method gets mean length of the queues at every single moment from the first step to last
	 * @param server The passed in server to get its length list and calculate mean from first step to last
	 * @return This return an double arraylist holds the calculated mean from first step to last
	 */
	public ArrayList<Double> getMeanList(Checkout server){
		ArrayList<Integer>lengths = new ArrayList<Integer>();
		ArrayList<Double> Meanlengths = new ArrayList<Double>();
		for (int temp : server.getLengthList()) {
			lengths.add(temp);
			Meanlengths.add(server.CalculateMeanLength(lengths));
		}
		return Meanlengths;
	}
	
	/**
	 * This method gets JPanel and return screenshot of
	 * @param graphs The passed in parameter of the JPanel we wanted to have a screenshot of
	 * @return This will return a buffered image that contains the screen shot, so we can save it or draw it.
	 */
	public static BufferedImage getScreenShot(JPanel graphs) {

		BufferedImage image = new BufferedImage(graphs.getWidth(),
				graphs.getHeight(), BufferedImage.TYPE_INT_RGB);

		// call the Component's paint method, using
		// the Graphics object of the image.
		graphs.paint(image.getGraphics());
		return image;
	}
	
	/**
	 * This methods loop through each server, gets its statistics data, wrap it in {@link #htmlEditorKit} and in plain string,
	 * If there is more than one server, i.e. multiple servers, then we will be adding comprehensive statistics on all queues,
	 * so we are going to retrieve all statistics using #getWrappedStatistics at Checkout Class , concurrently we will be adding
	 * all servers statistics together and make 4 string for the comprehinsive statistics
	 * <ul>Strings type
	 * <li>StyledComprehensiveStats, which has styled content of the Comprehensive stats</li>
	 * <li>PlainComprehensiveStats, which has plain content of the Comprehensive stats</li>
	 * <li>StyledComprehensiveComment, which has styled content of the commentary</li>
	 * <li>PlainComprehensiveComment, which has styled content of the stats</li></ul>
	 * 
	 * Once statistics has been all wrapped application will store then display plain one on console and styled one on application
	 * and then you're having the option to save either plain simulation statistics or styled HTML format statistics.
	 * 
	 */
	private void getStatistics() {

		StopSimulation.doClick();

		if (saveFlag && stopedSimulationFlag) {

			SavedStyled[0] = "";
			SavedStyled[1] = "";

			double[] Values = new double[11];
			String StyledComprehensiveStats = "", StyledComprehensiveComment = "", performance = "", PlainComprehensiveStats = "", PlainComprehensiveComment = "";

			Iterator<Checkout> it1 = servers.iterator();
			Iterator<JEditorPane> it2 = TxtDisplaySimulationHolder.iterator();

			while (it1.hasNext() && it2.hasNext()) {
				Checkout server = it1.next();
				JEditorPane simulateField = it2.next();

				double[] first = server.getStatistucsValues();

				for (int n = 0; n < Values.length; n++) {
					Values[n] += first[n];
				}

				String[] Wrappedstats = server.getWrappedStatistics();

				simulateField.setEditorKit(htmlEditorKit);
				String HTMLstats = server.getDataLine() + Wrappedstats[1]
						+ "</div>";
				Document doc = simulateField.getDocument();

				try {
					htmlEditorKit.insertHTML((HTMLDocument) doc,
							doc.getLength(), HTMLstats, 0, 0, null);
				} catch (BadLocationException | IOException e) {
				}

				SavedStyled[0] += Wrappedstats[0];
				SavedStyled[1] += Wrappedstats[1];
			}

			if ((Values[10] / servers.size() - 1) > 5) {
				performance = "need other checkout server(till) to be\noperated to improve your business performance.";
			} else {
				performance = "don't need other checkout till\nto be operated as the mean length is sensibly can be handled\nby the current checkout servers(tills) amount.";
			}

			if (servers.size() > 1) {
				PlainComprehensiveComment = "Through queue mean length, we can see you "
						+ performance + "\n";
				PlainComprehensiveStats = "\nServers Comprehensive Statistics\n\n"
						+ "Customers Joind Queues \t\t"
						+ Values[0]
						+ "\n\n"
						+ "Queues Mean Length\t\t"
						+ (int) Values[10]
						+ "\n\n"
						+ "Queues Maximum Length\t\t"
						+ Values[4]
						+ "\n\n"
						+ "Queues Minimum Length\t\t"
						+ Values[5]
						+ "\n\n"
						+ "Queues Median Length\t\t"
						+ Values[6]
						+ "\n\n"
						+ "Queues Range Length\t\t"
						+ Values[8]
						+ "\n\n"
						+ "Queues Length Variance\t\t"
						+ Values[7]
						+ "\n\n"
						+ "Queues Length Standard Deviation\t"
						+ Values[9]
						+ "\n\n"
						+ "Queues Join Probability\t\t"
						+ (Values[1] / servers.size() - 1)
						+ "%\n\n"
						+ "Queues Leave Probability\t\t"
						+ (Values[2] / servers.size() - 1)
						+ "%\n\n"
						+ "Queues Nothing Occur Probability "
						+ (Values[3] / servers.size() - 1)
						+ "%\n\n"
						+ "\t\tOverall Statistics Commentary\n\n"
						+ PlainComprehensiveComment;
				StyledComprehensiveComment = "<div class=\"header\" style=\"margin-top:5px;\">Overall Statistics Commentary</div><p style=\"text-align:left; margin-top:2px;\">Through queue mean length, we can see you "
						+ performance + "</p>";
				StyledComprehensiveStats = "<div class=\"kalam\"><table class=\"aass\" cellpadding=\"5\" style=\"width:100%;\"><tr><td class=\"header\" colspan=\"2\">Servers Comprehensive Statistics</td></tr><tr><td>Customers Joined Queue</td><td>"
						+ Values[0]
						+ "</td></tr><tr><td>Queue Mean Length</td><td>"
						+ (int) Values[10]
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
						+ (Values[1] / servers.size() - 1)
						+ "%</td></tr><tr><td>Queue Leaving Probability</td><td>"
						+ (Values[2] / servers.size() - 1)
						+ "%</td></tr><tr><td>Queue Nothing Occurs Probability</td><td>"
						+ (Values[3] / servers.size() - 1)
						+ "%</td></tr></table>"
						+ StyledComprehensiveComment
						+ "</div>";
			} else {
				PlainComprehensiveComment = "\tOverall Statistics Commentary\n\nThrough the queue mean length, we can see you "
						+ performance + "\n";
				StyledComprehensiveComment = "<div class=\"header\" style=\"margin-top:5px;\">Overall Statistics Commentary</div><p style=\"text-align:left; margin-top:2px;\">Through queue mean length, we can see you "
						+ performance + "</p>";
				PlainComprehensiveStats = PlainComprehensiveComment;
				StyledComprehensiveStats = StyledComprehensiveComment
						+ "</div>";
			}

			SavedStyled[1] = "<html><head><title>Simulation Statistics</title></head><body><style type=\"text/css\">#whole{font-size:14pt; font-family:tahoma; color:#942929;} .koola{color:#942929; font-weight:bold; margin-bottom:5px;} .kalam{padding:5px; font-family:tahoma; color:#4f4f4f;} .aass td{color:#4f4f4f;border: 2px solid #CC3300; border-collapse: collapse;} .header {text-align:center; font-weight:bold; color:#942929;}</style><div id=\"whole\"><div style=\"text-align:center; font-weight:bold;\"><img style=\"vertical-align:middle;\" src=\"http://www.zastudio.net/logo.png\" title=\"Zakaria's Simulator\" alt=\"icon\" />Zakaria's Queue Simulator Results</div><br />"
					+ StyledComprehensiveStats
					+ SavedStyled[1]
					+ "</div></body></html>";
			SavedStyled[0] = PlainComprehensiveStats + SavedStyled[0];

			System.out.println(SavedStyled[0]);
			StatisticsCalculatedFlag = true;

		} else {
			JOptionPane.showMessageDialog(null, "Run New Simulation First!",
					Notification, JOptionPane.WARNING_MESSAGE);
		}
		
	}
	
	/**
	 * This simulation working along with {@link #getStatistics()} method where getStatistics gets and wraps the data and then display it, and
	 * save Statistics will call dialog box choose the stats style to be saved and the available styles are :-
	 * <ul>Styles:-
	 * <li>Plain type, The plain type saves the simulation into text file with no style</li>
	 * <li>Styled type, The style type saves the simulation into HTML format with styles</li></ul>
	 * Once you have choose the save stats type {@link #saveFile(String, boolean, boolean, BufferedImage)} method will be called.
	 */
	private void saveStatistics() {
		StopSimulation.doClick();
		if (!StatisticsCalculatedFlag) {
			JOptionPane.showMessageDialog(null, "Calculate statistics first.",
					Notification, JOptionPane.ERROR_MESSAGE);
		} else {
			String s = (String) JOptionPane.showInputDialog(this,
					"Choose the statistics type:-\n", "Save Option",
					JOptionPane.PLAIN_MESSAGE, null, StylingSave, "");
			if (StylingSave[0] == s) {
				saveFile(SavedStyled[0], false, false, null);
			} else if (StylingSave[1] == s) {
				saveFile(SavedStyled[1], true, false, null);
			}
		}

	}
	
	/**
	 * This method works similarly to {@link #saveStatistics()} method yet his method save the simulation so it can be retrieved.
	 * This method loops through all servers and call #getrndNumbers() method from Checkout class to save simulation in a format that we can retrieve.
	 * {@link #saveFile(String, boolean, boolean, BufferedImage)} method gets called to save the simulation
	 * The order rule we follow in saving simlation is that in following
	 * <ol>Order Rule:-
	 * <li>Server Simulation Random Numbers</li>
	 * <li>Simulation Style</li>
	 * <li>Simulation Service Rate</li>
	 * <li>Interarrival Distribution</li>
	 * <li>Service time</li>
	 * </ol>
	 */
	private void saveSimulation() {
		StopSimulation.doClick();
		if (saveFlag) {
			SavedQueues = "";
			for (Iterator<Checkout> i = servers.iterator(); i.hasNext();) {
				Checkout server = i.next();
				SavedQueues = SavedQueues + server.getrndNumbers() + "/";
			}
			if (saveFile(SavedQueues + SStyle + "/" + serviceRate+ "/" +arrivalPerTimeStep+ "/"+serviceTime, false, false, null)) {
				SavedQueues = "";
			}
		} else {
			JOptionPane.showMessageDialog(null, "Run New Simulation First!",
					Notification, JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * This method does the job to retrieve simulation, first load up a text file using {@link #openFile()} method so checks if file is valid
	 * if file is valid e.g. it's extension should be text file, then bring it up to #content String then check if content string is empty
	 * if its not empty then conduct the retrieval precedure, our precedure is that following the code, is that we split the important chunks of data
	 * into parts using slash "/" which is using the split function we tokenize the parts into a string array, so now we all parts we need but needs some clean up
	 * We first retrieve the elements starting from the end to the start following the order explained in the following {@link #saveSimulation()}
	 * Once we retrieve all this, we still have the random numbers splitted into commas for each server so we need to clean it up, we take each server element from the array,
	 * split it as well into string array as comma needs to be removed, then we loop through the string adding each value to an int array surrounded with parseInt, then add to {@link #rndlist}
	 * that then run simulation will extract each array and parseit back into arraylist then set to the appropriate server.
	 */
	private void retrieveSimulation() {

		// open file
		String content = openFile();

		if (content != "") {
			try {
				StopSimulation.doClick();
				String[] randomssplit = content.split("/");
				SStyle = randomssplit[randomssplit.length - 4];
				serviceRate = Integer.parseInt(randomssplit[randomssplit.length - 3]);
				arrivalPerTimeStep = Integer.parseInt(randomssplit[randomssplit.length - 2]);
				serviceTime = Integer.parseInt(randomssplit[randomssplit.length - 1]);
				serverCount = randomssplit.length - 4;
				for (int i = 0; i < serverCount; i++) {
					String[] rndNums = randomssplit[i].split(",");
					int[] rndHolder = new int[rndNums.length];
					for (int k = 0; k < rndHolder.length; k++) {
						rndHolder[k] = Integer.parseInt(rndNums[k]);
					}

					rndlist.add(rndHolder);
				}

				RunSim.doClick();
			} catch (Exception ea) {
				JOptionPane.showMessageDialog(null,
						
						"Unsuccessful, the wrong file has been loaded.",
						Notification, JOptionPane.WARNING_MESSAGE);
			}
		}

	}

	/**
	 * This method instantiate new timer with specified speed and event
	 */
	private void instantTimer() {
		TimerEvent stepTimer = new TimerEvent();
		timer = new Timer(speed, stepTimer);
	}
	
	/**
	 * This nested class in GUI Class related to the timer which will be invoked each time the timer is running using the implements of actionlistner, and declaring an instance of.
	 * The class takes the responsibility to conduct simulations, and draw the graphs on the #ImagePanel bufferedImage.
	 * @author Zakaria Abushima
	 * @version 1.0
	 */
	private class TimerEvent implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			stepCounter++; // count the time steps occured to make sure it's correct.
			GraphicDisplay.drawEverything(buffer.createGraphics()); // The graphic display is the imagepanel to drawEverything on from, title , to servers, and customers.
			if (stepCounter >= timesteps || processesCounter >= bufferSize
					|| necksCounter >= bottlenecks) { // if statement to keep checking every timer run if we shall stop timer or not, greater than sign used to prevent in case you change the 
				StopSimulation.doClick(); // buffer size or timesteps to be less than the current specified so it will run forever.
			}
		}
	}
	
	/**
	 * This method gets a server of Checkout type and find weather its the smallest current queue length server in the servers arraylist
	 * @param server This is the passed in checkout type parameter we treat as server , that will be compared with other servers
	 * @return This method returns a boolean result, in case the server is found that is less than the other servers in terms of current length
	 * then we return true.
	 */
	private boolean smallestQueue(Checkout server) {
		int c = 0;
		for (Iterator<Checkout> i = servers.iterator(); i.hasNext();) {
			Checkout other = i.next();
			if (server.getCurrentLength() <= other.getCurrentLength()) {
				c++;
			}
		}
		return c == servers.size();
	}

	/**
	 * This nested class in GUI demonstrates the ImagePanel that extends from JPanel were has paintComponent in it that will by any means draw whatsoever in paintComponent
	 * Other method could draw on the ImagePanel yet but have to call repaint() which automatically calls paintComponent.
	 * @author Zakaria Abushima
	 * @version 1.0
	 */
	private class ImagePanel extends JPanel {
		
		/**
		 * Constructor of ImagePanel Class, we don't have anything to pass in since the BufferedImage we will be drawing on is in wider scope that covers this class
		 */
		public ImagePanel() {

		}

		/**
		 * The magic method, which paint whatsoever to the mother panel where it extends from JPanel as component variable
		 */
		protected void paintComponent(Graphics g) {

			super.paintComponents(buffer.getGraphics()); // get graphics of this panel to draw on
			setPreferredSize(new Dimension(bufferWidth, bufferHeight)); // set prefered size of the panel
			g.drawImage(buffer, 0, 0, this); // draw buffered image

		}
		
		/**
		 * This method demonstrates drawing of the server graphics with the customers joining and leaving the tills
		 * @param g1 This is the passed in parameter of graphics from the bufferedImage where we will be drawing on.
		 */
		private void drawEverything(Graphics g1) {
			Graphics2D g = (Graphics2D) g1; // cast graphics to 2D graphics to take the advantage of antialising and clear rectangle.
			g = drawBackground(g); // we draw blank white background on to draw the clean nice looking current state of the servers

			Font Font1 = new Font("Tahoma", Font.PLAIN, 15); // determine the type of font to use while drawing strings
			g.setFont(Font1); // set font type

			Iterator<Checkout> it1 = servers.iterator(); // setup iterator through servers arraylist 
			Iterator<JEditorPane> it2 = TxtDisplaySimulationHolder.iterator(); // setup iterator through servers text display holders

			// start the while loop iteration through servers and text display holders
			while (it1.hasNext() && it2.hasNext()) {
				
				Checkout server = it1.next(); // declare iterated server
				JEditorPane simulateField = it2.next(); // declare iterated simulation text display EditorPane

				// check if this is the last iteration, as we want to display something different always in the last iteration
				if (LastIterFlag) { 
					// if last iteration display some text saying results
					g.drawString(
							"Server(" + (server.getServerID() + 1) 
									+ ") Simulation ends with "
									+ server.getCurrentLength()
									+ " current customers and "
									+ server.getProcessesCount()
									+ " overall joined.",
							server.getXPositions(0), server.getYPositions());
				} else {
					// if not last iteration keep display current states and update graphics and text editorpane holders.
					server = organiseServer(server); // we call here some organising staff going on each server to check whether we should add or remove customer.
					simulateField.setText(server.simulation(arrivalPerTimeStep, // display text simulation onto the editorpane
							serviceTime) + "</div>");
					if (server.getCurrentLength() > necksCounter) { // check if bottleneck has been exceeded
						necksCounter = server.getCurrentLength();
					}
					g.drawString("Server(" + (server.getServerID() + 1) // draw string tells server status
							+ ") has " + server.getCurrentLength()
							+ " customers in the queue.",
							server.getXPositions(0), server.getYPositions());
				}

				g.drawImage(mediaList.get(10), (server.getXPositions(0)), // draw server tills
						server.getYPositions() + 10, null);

				for (int x = 0; x < server.getCurrentLength() && x < 5; x++) { // draw server queue current length
					g.drawImage(mediaList.get(9),
							server.getXPositions(x) + 25,
							server.getYPositions() + 23, null);
				}

				if (server.getCurrentLength() > 5) { // if server current length greater than 5 then draw an arrow which indicates this for quality purposes
					g.drawImage(mediaList.get(12),
							server.getXPositions(5) + 19,
							server.getYPositions() + 38, null);
				}
			}
			System.out.println();
			repaint(); // horray we are calling paintComponent in other language ^^
		}

		protected Graphics2D drawBackground(Graphics g1) { // this method draws the background and clearRect to overide current bufferedimage with white background

			Graphics2D g = (Graphics2D) g1;
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g.setColor(new Color(148, 41, 41));
			g.setBackground(Color.white);

			g.clearRect(0, 0, bufferWidth, bufferHeight);
			Font Font2 = new Font("Tahoma", Font.BOLD, 18);

			g.setFont(Font2);
			g.drawString("Simulation Graphics Display", 160, 32);

			repaint();

			return g;

		}

	}

	/**
	 * This method does a crazy job, where it checks complicatedly each queue length and bounce the customers between to meets the simulation specified options
	 * Every single server iteration, it checks if we are adding customer to the queue or not and if yes bounce the customers around servers to be logically reasonable.
	 * Scenario : if in real life customer about to join a till checkout , one of the priorities server the ones have less queue length and the probability of customers joining
	 * the shortest till queue length is indoubtly ,  therefore we going some bouncing jobs so we check each time and find the right server to add customer, if not the right server we have logic flag
	 * which will set to true in case we wanted to add customer server is not smallest and the random number is not adding, it will remain true and that show that we have customer left to bounce
	 * we carry on our simulation and in case the next step luckily came customer adding and the service rate is contradicting then we carry on keeping the login true until we reach the point where
	 * its the smallest queue and not adding a customer so we force to add , as we prior to forced to not add because is not smallest queue, so we kind of balancing the servers.
	 * @param server This is the passed in server that we will be checking and organise adding 
	 * @return This return the server after organisation has been done.
	 */
	private Checkout organiseServer(Checkout server) {
		server.getRnd();
		if (server.SimulationStyle(true) && smallestQueue(server)&& (server.getCurrentLength() <= (serviceRate - 1))) {
			JoiningLogicFlag = false;
		} else if (!smallestQueue(server) && server.SimulationStyle(true)|| (server.getCurrentLength() >= (serviceRate - 1))) {
			if (server.getCurrentLength() >= (serviceRate - 1)) {
				server.NotaddCustomer(true);
			} else {
				server.NotaddCustomer(false);
			}
			JoiningLogicFlag = true;
		} else if (smallestQueue(server) && !server.SimulationStyle(true)&& JoiningLogicFlag&& (server.getCurrentLength() <= (serviceRate - 1))) {
			server.addCustomer();
			JoiningLogicFlag = false;
		}
		if (server.SimulationStyle(true)) {
			processesCounter += arrivalPerTimeStep;
		}
		return server;
	}
	
	/**
	 * This methods called after {@link #setStarter()} and couple methods are done , so we setup component in the this.JPanel.
	 * adding menu bar, Managing layout using BorderLayout.
	 */
	private void setupComponent() {

		// File Menu
		fileMenu = new JMenu("File");
		fileMenu.setIcon(new ImageIcon(mediaList.get(1)));
		fileMenu.add(SaveSimulation);
		SaveSimulation.setIcon(new ImageIcon(mediaList.get(2)));
		fileMenu.addSeparator();
		fileMenu.add(RetrieveStats);
		RetrieveStats.setIcon(new ImageIcon(mediaList.get(6)));
		fileMenu.addSeparator();
		fileMenu.add(SaveStats);
		SaveStats.setIcon(new ImageIcon(mediaList.get(2)));
		fileMenu.addSeparator();
		fileMenu.add(AboutSimulator);
		AboutSimulator.setIcon(new ImageIcon(mediaList.get(7)));

		// Tools Menu
		toolsMenu = new JMenu("Tools");
		toolsMenu.setIcon(new ImageIcon(mediaList.get(0)));
		toolsMenu.add(RunSim);
		toolsMenu.addSeparator();
		RunSim.setIcon(new ImageIcon(mediaList.get(3)));
		pauseSimulationBtn.setIcon(new ImageIcon(mediaList.get(4)));
		toolsMenu.add(pauseSimulationBtn);
		toolsMenu.addSeparator();
		ResumeSimulationBtn.setIcon(new ImageIcon(mediaList.get(3)));
		toolsMenu.add(ResumeSimulationBtn);
		toolsMenu.addSeparator();
		toolsMenu.add(StopSimulation);
		StopSimulation.setIcon(new ImageIcon(mediaList.get(4)));
		toolsMenu.addSeparator();
		toolsMenu.add(DisplaySim);
		DisplaySim.setIcon(new ImageIcon(mediaList.get(7)));
		toolsMenu.addSeparator();
		Diagrams.setIcon(new ImageIcon(mediaList.get(8)));
		toolsMenu.add(Diagrams);

		// Menu Bar
		menubar.add(fileMenu);
		menubar.add(toolsMenu);

		// Toolbar
		toolbar.setFloatable(false);
		toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
				new Color(153, 153, 153)));
		toolbar.add(timestepslbl);
		toolbar.add(timestepsField);
		toolbar.add(serverslbl);
		toolbar.add(serversField);
		toolbar.add(bufferSizelbl);
		toolbar.add(bufferSizeField);
		toolbar.add(processesPerTimePriodlbl);
		toolbar.add(serviceRateField);
		toolbar.add(serviceTimelbl);
		toolbar.add(serviceTimeField);
		toolbar.add(arrivalPerTimeSteplbl);
		toolbar.add(arrivalPerTimeStepField);
		toolbar.add(bottleneckslbl);
		toolbar.add(bottlenecksField);
		toolbar.add(simStylelbl);
		toolbar.add(SimulationStyle);
		toolbar.add(simSpeedlbl);
		toolbar.add(SimulationSpeedBar);
		toolbar.setBorder(new EmptyBorder(3, 0, 3, 0));

		// App Header Label
		ApplicationTitle.setIcon(new ImageIcon(mediaList.get(5)));
		add(ApplicationTitle);
		TextDisplay.add(TextFieldsSimulation);
		setLayout(new BorderLayout());
		add(GraphicsScrollPane, BorderLayout.CENTER);
		add(menubar, BorderLayout.NORTH);
		add(toolbar, BorderLayout.SOUTH);
		add(TextScrollPane, BorderLayout.EAST);
		GraphicsScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		TextScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

	}
	
	/**
	 * This method gets in field and checks the type of and return what it has been assigned to.
	 * To test out the actual work of this method, just basically hover over in simulation option toolbar text fields and horray Magic !
	 * @param field This is the passed in field parameter where checking will be done.
	 */
	private void ClearTextField(final JTextField field) {
		field.addMouseListener(new MouseAdapter() {

			public void mouseExited(MouseEvent e) {

				if (e.getSource() == timestepsField) {
					if (timesteps == Integer.MAX_VALUE) {
						field.setText("\u221E");
					} else {
						field.setText(timesteps + "");
					}
				} else if (e.getSource() == serversField) {
					field.setText("" + serverCount);
				} else if (e.getSource() == bufferSizeField) {
					if (bufferSize == Integer.MAX_VALUE) {
						field.setText("\u221E");
					} else {
						field.setText("" + bufferSize);
					}
				} else if (e.getSource() == serviceRateField) {
					if (serviceRate == Integer.MAX_VALUE) {
						field.setText("\u221E");
					} else {
						field.setText("" + serviceRate);
					}
				} else if (e.getSource() == bottlenecksField) {
					if (bottlenecks == Integer.MAX_VALUE) {
						field.setText("\u221E");
					} else {
						field.setText("" + bottlenecks);
					}
				} else if (e.getSource() == arrivalPerTimeStepField) {
					field.setText("" + arrivalPerTimeStep);
				} else if (e.getSource() == serviceTimeField) {
					field.setText("" + serviceTime);
				}

				field.setToolTipText("Submit by type in and press enter in your keyboard");
				field.selectAll();
			}

			public void mouseEntered(MouseEvent e) {
				field.setToolTipText("Submit by type in and press enter in your keyboard");
			}

			public void mouseClicked(MouseEvent e) {
				field.setText("");
				field.setToolTipText("Submit by type in and press enter in your keyboard");
			}
		});
	}
	
	/**
	 * This method reset the timer, and check if timer is running we don't want to get an exception by starting timer after we finish and vice versa
	 * So we want to instantiate i.e reset timer, if its running we want to stop it before instantiating new one and avoiding exceptions error, and we want only to start it
	 * if it was running, basically , check if timer is running, if yes stop it and assing flag to true, go to next chunk, instantiate new timer, check if we have stoppped the timer
	 * using previous flag if true the start it back again.
	 */
	private void reSetTimer() {
		boolean check = false;
		if (timer.isRunning()) {
			timer.stop();
			check = true;
		}
		instantTimer();
		if (check) {
			timer.start();
		}
	}
	
	/**
	 * This function gets label passed in and style it, centralised as the passed in parameters and return back.
	 * @param label This is the label passed in where all customization will be done on.
	 * @param Title This is the label text/title where its passed and then assigned to it.
	 * @param font This is the label font type set to.
	 * @param border This is the label margins
	 * @param colorcode This is the label text color.
	 * @param tooltip This is the label tooltip
	 * @return This return the fully styled assigned label
	 */
	private JLabel paintLbl(JLabel label, String Title, Font font,
			EmptyBorder border, Color colorcode, String tooltip) {
		label = new JLabel(Title);
		label.setFont(font);
		label.setForeground(colorcode);
		label.setBorder(border);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		if (tooltip != null) {
			label.setToolTipText("Where simulation is so simple!");
		}
		return label;
	}
	
	/**
	 * This method load up a file to the simulator, it accepts only text file extension, it reads the content of the file
	 * @return This method return the content of the textfile in a string format
	 */
	private String openFile() {
		String content = "";
		int result = fc.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			String extension = fc.getSelectedFile().getPath()
					.substring(fc.getSelectedFile().getPath().lastIndexOf('.'));
			if (extension.equals(".txt")) {
				try {
					// load file stuff
					BufferedReader br = new BufferedReader(new FileReader(fc
							.getSelectedFile().getPath()));
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();
					while (line != null) {
						sb.append(line);
						line = br.readLine();
					}
					content = sb.toString();
					br.close();
				} catch (Exception nfe) {
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Unsuccessful, Allowed file extensions are: '.txt'",
						Notification, JOptionPane.WARNING_MESSAGE);
			}
		}
		return content;

	}

	/**
	 * This is saveFile cross platform method that saves file of three different types, HTML, Text file, or PNG image.
	 * @param SavedData This parameter will be set to a string in case we want to save either text file or HTML but not PNG Image
	 * @param style This boolean parameter we tell whether we want to save HTML or text file
	 * @param image This parameter is boolean that tell if we want to save an image or something else
	 * @param img This is the bufferedimage variable that we will save the image from.
	 * @return This method return either true or false, in case save has been done successfuly will return true.
	 */
	private boolean saveFile(String SavedData, boolean style, boolean image,
			BufferedImage img) {
		int result = fc.showSaveDialog(this);
		boolean saved = false;
		BufferedWriter bw = null;
		if (result == JFileChooser.APPROVE_OPTION) {
			File myfile = fc.getSelectedFile();
			try {
				if (style && !image) {
					bw = new BufferedWriter(new FileWriter(
							myfile.getAbsoluteFile() + ".html")); // Construct
																	// buffered
																	// writer
				} else if (!style && image) {
					try {
						// write the image as a PNG
						ImageIO.write(img, "png", new File(myfile + ".png"));
					} catch (Exception ea) {
					}
				} else {
					bw = new BufferedWriter(new FileWriter(
							myfile.getAbsoluteFile() + ".txt")); // Construct
																	// buffered
																	// writer
				}
				if (!(!style && image)) {
					try {
						bw.write(SavedData); // Save statistics to Disk
						saved = true;
					} finally {
						bw.close();
						// save file stuff
						JOptionPane.showMessageDialog(null,
								"Has been saved successfully.", Notification,
								JOptionPane.WARNING_MESSAGE);
					}
				}
			} catch (Exception e) { // Catch any IOException
			}
		}
		return saved; // Return true if the data saved!
	}

}
