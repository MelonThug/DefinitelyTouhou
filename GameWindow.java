import javax.swing.*;			// need this for GUI objects
import java.awt.*;			// need this for Layout Managers
import java.awt.event.*;		// need this to respond to GUI events
	
public class GameWindow extends JFrame implements ActionListener, KeyListener, MouseListener {
	private JButton startB;
	private JButton pauseB;
	private JButton endB;
	private JButton exitB;

	private Container c;
	private JPanel mainPanel;
	private GamePanel gamePanel;

	public GameWindow() {
		setTitle ("DefinitelyTouhou");
		setSize (600, 700);

	    startB = new JButton ("Start Game");
	    pauseB = new JButton ("Pause Game");
	    endB = new JButton ("End Game");
		exitB = new JButton ("Exit");

		startB.addActionListener(this);
		pauseB.addActionListener(this);
		endB.addActionListener(this);
		exitB.addActionListener(this);
		
		mainPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		mainPanel.setLayout(flowLayout);

		GridLayout gridLayout;
		gamePanel = new GamePanel(500, 570);

		JPanel buttonPanel = new JPanel();
		gridLayout = new GridLayout(1, 4);
		buttonPanel.setLayout(gridLayout);
		buttonPanel.add (startB);
		buttonPanel.add (pauseB);
		buttonPanel.add (endB);
		buttonPanel.add (exitB);

		JTextArea controlsTA = new JTextArea();
		controlsTA.setAlignmentX(JTextArea.CENTER_ALIGNMENT);
		controlsTA.setAlignmentY(JTextArea.CENTER_ALIGNMENT);
		controlsTA.setEditable(false);
		controlsTA.setFont(new Font("SansSerif", Font.BOLD, 12));
		controlsTA.setText("Arrow Keys: Move\n     LeftShift: Slow movement (can hold)\n                  Z: Shoot (can hold)");
		controlsTA.setBackground(Color.GRAY);

		mainPanel.add(gamePanel);
		mainPanel.add(buttonPanel);
		mainPanel.add(controlsTA);
		mainPanel.setBackground(Color.GRAY);

		gamePanel.addMouseListener(this);
		mainPanel.addKeyListener(this);

		c = getContentPane();
		c.add(mainPanel);

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals(startB.getText())) {
			gamePanel.startGame();
		}

		if (command.equals(pauseB.getText())) {
			gamePanel.pauseGame();
			if (command.equals("Pause Game"))
				pauseB.setText ("Resume");
			else
				pauseB.setText ("Pause Game");

		}
		
		if (command.equals(endB.getText())) {
			gamePanel.endGame();
		}

		if (command.equals(exitB.getText()))
			System.exit(0);

		mainPanel.requestFocus();
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_LEFT) {
			gamePanel.updateDirections(1);
		}

		if (keyCode == KeyEvent.VK_RIGHT) {
			gamePanel.updateDirections(2);
		}

		if (keyCode == KeyEvent.VK_UP) {
			gamePanel.updateDirections(3);
		}

		if (keyCode == KeyEvent.VK_DOWN) {
			gamePanel.updateDirections(4);
		}

		if (keyCode == KeyEvent.VK_Z) {
			gamePanel.getPlayer().setIsShooting(true);
		}

		if (keyCode == KeyEvent.VK_SHIFT){
			gamePanel.getPlayer().setIsSlowed(true);
		}
	}

	public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if(keyCode == KeyEvent.VK_LEFT){
            gamePanel.updateDirections(-1);
        }

        if (keyCode == KeyEvent.VK_RIGHT) {
			gamePanel.updateDirections(-2);
		}

        if (keyCode == KeyEvent.VK_UP) {
			gamePanel.updateDirections(-3);
		}

        if (keyCode == KeyEvent.VK_DOWN) {
			gamePanel.updateDirections(-4);
		}

		if (keyCode == KeyEvent.VK_Z) {
			gamePanel.getPlayer().setIsShooting(false);
		}

		if (keyCode == KeyEvent.VK_SHIFT){
			gamePanel.getPlayer().setIsSlowed(false);
		}
    }

	public void keyTyped(KeyEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}