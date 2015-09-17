import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Creates the Reversi JFrame, plays the game based on instructions from ReversiBoard
 * @author Keyhan Rezvani
 * @version January 2014
 */
public class ReversiGame extends JPanel
{
	public static void main(String[] args)
	{
		// Frame title and icon are set
		JFrame frame = new JFrame("Reversi");
		ImageIcon icon = new ImageIcon("icon.png");
		frame.setIconImage(icon.getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set the frame size
		frame.setSize(1008, 711);
		frame.setLocationRelativeTo(null);
		ReversiBoard panel = new ReversiBoard();
		frame.setContentPane(panel);
		frame.setVisible(true);
	}
}
