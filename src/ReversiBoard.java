import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * Keeps track of a Reversi game board, includes all methods that concern
 * modifying the game board or displaying images, checking valid moves, flipping
 * pieces and picks what AI moves to make
 * 
 * @author Keyhan Rezvani, Kewei Zhou for bottom 4 methods
 * @version December 2013
 */
public class ReversiBoard extends JPanel implements MouseListener
{
	// These variables keep track of the values of pieces,
	// their positions on the board, whose turn it is, and the score
	public int[][] board = new int[8][8];
	private int[][] flipBoard = new int[8][8];
	public final int BLACK = -1;
	public final int WHITE = 1;
	public final int EMPTY = 0;
	private int turn = WHITE;
	private int whiteScore = 2;
	private int blackScore = 2;

	// These variables can are used in the game and on the menu
	private boolean is1P = false;
	private boolean animationOn = true;
	private boolean gameOver = false;
	private boolean isMinimax = true;
	private int difficulty = 4;

	// These variables are used for the buttons
	private boolean isNewGameSelected = false;
	private boolean onePlayerSelected = false;
	private boolean endGamePressed, newGamePressed = false;
	private boolean onePlayerPressed, twoPlayerPressed = false;
	private boolean easyPressed, normalPressed, hardPressed, insanePressed,
			instrPressed = false;

	// noMoves will control if a 'no movews' message is to appear
	private boolean noMoves = false;
	private boolean justStarted = true;

	// These are used for the flipping animations
	private ArrayList<Image> flippingPieces = new ArrayList<Image>();
	private boolean animateDone = true;

	// All the images used
	Image boardImg, instructions, frame, blackPiece, whitePiece, endGame,
			endGameDown, newGameDown, newGame, highlight, white1, black1,
			black2, white2, black3, white3, black4, white4, toggleOn,
			toggleOff, onePlayerDown, twoPlayerDown, onePlayer, twoPlayer,
			easy, normal, hard, insane, easyDown, normalDown, hardDown,
			insaneDown, whiteWins, blackWins, tieGame, noMove, instr,
			instrDown;

	/**
	 * Constructs a new Reversi board with the default piece placing. This will
	 * be used to create any new games of Reversi.
	 */
	public ReversiBoard()
	{
		addMouseListener(this);
		setFocusable(true);

		// Images are loaded
		boardImg = Toolkit.getDefaultToolkit().getImage("screen.png");
		instructions = Toolkit.getDefaultToolkit().getImage("instructions.png");
		frame = Toolkit.getDefaultToolkit().getImage("frame.png");
		blackPiece = Toolkit.getDefaultToolkit().getImage("black.png");
		whitePiece = Toolkit.getDefaultToolkit().getImage("white.png");
		endGame = Toolkit.getDefaultToolkit().getImage("endgame.png");
		endGameDown = Toolkit.getDefaultToolkit()
				.getImage("endgameclicked.png");
		newGame = Toolkit.getDefaultToolkit().getImage("newgame.png");
		newGameDown = Toolkit.getDefaultToolkit()
				.getImage("newgameclicked.png");
		onePlayer = Toolkit.getDefaultToolkit().getImage("1player.png");
		twoPlayer = Toolkit.getDefaultToolkit().getImage("2player.png");
		twoPlayerDown = Toolkit.getDefaultToolkit().getImage(
				"2playerclicked.png");
		onePlayerDown = Toolkit.getDefaultToolkit().getImage(
				"1playerclicked.png");
		easy = Toolkit.getDefaultToolkit().getImage("easy.png");
		easyDown = Toolkit.getDefaultToolkit().getImage("easyclicked.png");
		normal = Toolkit.getDefaultToolkit().getImage("normal.png");
		normalDown = Toolkit.getDefaultToolkit().getImage("normalclicked.png");
		hard = Toolkit.getDefaultToolkit().getImage("hard.png");
		hardDown = Toolkit.getDefaultToolkit().getImage("hardclicked.png");
		insane = Toolkit.getDefaultToolkit().getImage("insane.png");
		insaneDown = Toolkit.getDefaultToolkit().getImage("insaneclicked.png");
		highlight = Toolkit.getDefaultToolkit().getImage("highlight.png");
		toggleOn = Toolkit.getDefaultToolkit().getImage("toggleon.png");
		toggleOff = Toolkit.getDefaultToolkit().getImage("toggleoff.png");
		instr = Toolkit.getDefaultToolkit().getImage("help.png");
		instrDown = Toolkit.getDefaultToolkit().getImage("helpclicked.png");
		whiteWins = Toolkit.getDefaultToolkit().getImage("whitewins.png");
		blackWins = Toolkit.getDefaultToolkit().getImage("blackWins.png");
		tieGame = Toolkit.getDefaultToolkit().getImage("tiegame.png");
		noMove = Toolkit.getDefaultToolkit().getImage("nomoves.png");

		// Images of a piece flipping
		black1 = Toolkit.getDefaultToolkit().getImage("black1.png");
		white1 = Toolkit.getDefaultToolkit().getImage("white1.png");
		black2 = Toolkit.getDefaultToolkit().getImage("black2.png");
		white2 = Toolkit.getDefaultToolkit().getImage("white2.png");
		black3 = Toolkit.getDefaultToolkit().getImage("black3.png");
		white3 = Toolkit.getDefaultToolkit().getImage("white3.png");
		black4 = Toolkit.getDefaultToolkit().getImage("black4.png");
		white4 = Toolkit.getDefaultToolkit().getImage("white4.png");

		// Array of flipping piece images
		flippingPieces.add(white4);
		flippingPieces.add(white3);
		flippingPieces.add(white2);
		flippingPieces.add(white1);
		flippingPieces.add(black1);
		flippingPieces.add(black2);
		flippingPieces.add(black3);
		flippingPieces.add(black4);

		// Starting 4 pieces
		board[3][3] = WHITE;
		board[4][4] = WHITE;
		board[3][4] = BLACK;
		board[4][3] = BLACK;
	}

	/**
	 * Basic UI paint method, used to draw images, shapes and text on the screen
	 * 
	 * @param g the Graphical settings for this game, the Component object on
	 *            which to draw, the current font and colour
	 * 
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Although this would not be my preferred solution to images not
		// loading on the first call, I can't seem to find the problem and this
		// works just fine
		// Upon loading the game, these images will be displayed only once and
		// then will be drawn over anyway
		if (justStarted)
		{
			g.drawImage(noMove, 0, 0, this);
			for (Image flippingPiece : flippingPieces)
			{
				g.drawImage(flippingPiece, 0, 0, this);
			}
			justStarted = false;
		}

		// Draw the board and background
		g.drawImage(boardImg, 0, 0, this);

		// xPx and yPx represent the x and y pixel coordinates
		// Goes through entire board drawing the pieces and possible moves
		for (int xPx = 18; xPx < 658; xPx += 80)
			for (int yPx = 20; yPx < 660; yPx += 80)
			{
				int x = (xPx - 18) / 80;
				int y = (yPx - 20) / 80;

				// Draws the given flipBoard image, depending on if the piece is
				// to be flipped to white or to black
				if (flipBoard[y][x] < 0 && animationOn)
				{
					g.drawImage(flippingPieces.get(flipBoard[y][x] + 8), xPx,
							yPx, this);
				}
				else if (flipBoard[y][x] > 0 && animationOn)
				{
					g.drawImage(flippingPieces.get(flipBoard[y][x] - 1), xPx,
							yPx, this);
				}
				// Draws the actual pieces as they are on the board
				else if (board[y][x] == -1)
					g.drawImage(blackPiece, xPx, yPx, this);
				else if (board[y][x] == 1)
					g.drawImage(whitePiece, xPx, yPx, this);
				// Highlights possible moves for the current turn, unless it's
				// the computer's turn
				else if (isMoveValid(y, x, turn)
						&& ((is1P && turn == WHITE) || !is1P) && animateDone
						&& !gameOver)
					g.drawImage(highlight, xPx, yPx, this);
			}

		// Draws the score under the corresponding piece
		// The shift is for 2 digit numbers so that they'll always be centered
		g.setFont(new Font("Century Gothic", Font.PLAIN, 40));
		g.setColor(Color.WHITE);
		int xShiftW = 0;
		int xShiftB = 0;
		if (blackScore / 10 > 0)
			xShiftB = -13;
		if (whiteScore / 10 > 0)
			xShiftW = -13;
		g.drawString(String.valueOf(blackScore), 749 + xShiftB, 250);
		g.drawString(String.valueOf(whiteScore), 900 + xShiftW, 250);

		// Draws an outline of a square over whichever player's turn it is
		if (!gameOver)
		{
			if (turn == BLACK)
			{
				g.drawRect(720, 135, 80, 80);
			}
			else if (turn == WHITE)
			{
				g.drawRect(870, 135, 80, 80);
			}

			// Will show a message box saying there are no moves if there are no
			// valid moves
			if (noMoves)
			{
				g.drawImage(noMove, 712, 450, this);
			}
		}

		if (gameOver)
		{
			// Will show a message box showing the winner, or if it was a tie
			if (getScore(BLACK) > getScore(WHITE))
				g.drawImage(blackWins, 712, 450, this);
			else if (getScore(BLACK) < getScore(WHITE))
				g.drawImage(whiteWins, 712, 450, this);
			else if (getScore(BLACK) == getScore(WHITE))
				g.drawImage(tieGame, 712, 450, this);

			// Draws the 'new game' button if the game is over
			// Also draws a 'pressed' button if it's pressed
			if (!isNewGameSelected)
				if (newGamePressed)
					g.drawImage(newGameDown, 740, 300, this);
				else
					g.drawImage(newGame, 740, 300, this);

			// If the '1 player' option is selected it will draw the buttons for
			// the different difficulties, and pressed versions if one is
			// pressed
			else if (onePlayerSelected)
			{
				if (easyPressed)
					g.drawImage(easyDown, 740, 300, this);
				else
					g.drawImage(easy, 740, 300, this);
				if (normalPressed)
					g.drawImage(normalDown, 840, 300, this);
				else
					g.drawImage(normal, 840, 300, this);
				if (hardPressed)
					g.drawImage(hardDown, 740, 350, this);
				else
					g.drawImage(hard, 740, 350, this);
				if (insanePressed)
					g.drawImage(insaneDown, 840, 350, this);
				else
					g.drawImage(insane, 840, 350, this);
			}
			else
			{
				// Draws the '1 player' or '2 player' buttons, and pressed if
				// they are being pressed
				if (onePlayerPressed)
				{
					g.drawImage(onePlayerDown, 740, 300, this);
					g.drawImage(twoPlayer, 840, 300, this);
				}
				else if (twoPlayerPressed)
				{
					g.drawImage(twoPlayerDown, 840, 300, this);
					g.drawImage(onePlayer, 740, 300, this);
				}
				else
				{
					g.drawImage(onePlayer, 740, 300, this);
					g.drawImage(twoPlayer, 840, 300, this);
				}
			}

		}
		else
		{
			// Draws the 'end game' button if the game is not over, and again,
			// pressed if it is being pressed
			if (endGamePressed)
				g.drawImage(endGameDown, 740, 300, this);
			else
				g.drawImage(endGame, 740, 300, this);
		}

		// To toggle animations on and off
		if (animationOn)
			g.drawImage(toggleOn, 842, 645, this);
		else
			g.drawImage(toggleOff, 842, 645, this);

		// Shows the instructions on screen if pressed, also changes the button
		// to a pressed image
		if (!instrPressed)
			g.drawImage(instr, 943, 625, this);
		else
		{
			g.drawImage(instrDown, 943, 625, this);
			g.drawImage(instructions, 0, 0, this);
		}

		// Draws the frame over everything else, only needed for the corners
		// where the highlight would overlap
		g.drawImage(frame, 0, 0, this);
	}

	/**
	 * Adds a delay when called, delay is milli ms long
	 * 
	 * @param milli the milliseconds to delay by
	 */
	public void delay(int milli)
	{
		try
		{
			Thread.sleep(milli);
		}
		catch (InterruptedException e)
		{
		}
	}

	/**
	 * Releasing the mouse gnereally signifies the confirmation of making a
	 * choice on this board
	 * 
	 * @param event the x and y co-ordinates of the point the mouse button was
	 *            released
	 */
	@Override
	public void mouseReleased(MouseEvent event)
	{
		int x = event.getX();
		int y = event.getY();

		// Ends the game if the end game button is pressed and released
		if (!gameOver)
		{
			if (x > 740 && y > 300 && x < 940 && y < 350 && endGamePressed)
			{
				gameOver = true;
			}
		}
		else if (gameOver)
		{
			// Shows the 'new game' options if the 'new game' button is used
			if (x > 740 && y > 300 && x < 940 && y < 350 && !isNewGameSelected
					&& newGamePressed)
			{
				isNewGameSelected = true;
				repaint();
			}
			// If the '1 player' option has been selected the difficulty buttons
			// are shown
			// The difficulty is set based off which button is clicked
			else if (onePlayerSelected && x > 740 && x < 940 && y > 300
					&& y < 400)
			{
				if (x > 740 && x < 840 && y > 300 && y < 350 && easyPressed)
				{
					difficulty = 1;
					is1P = true;
					newGame();
				}
				else if (x > 840 && x < 940 && y > 300 && y < 350
						&& normalPressed)
				{
					difficulty = 2;
					is1P = true;
					newGame();
				}
				else if (x > 740 && x < 840 && y > 350 && y < 400
						&& hardPressed)
				{
					difficulty = 3;
					is1P = true;
					newGame();
				}
				else if (x > 840 && x < 940 && y > 350 && y < 400
						&& insanePressed)
				{
					difficulty = 4;
					is1P = true;
					newGame();
				}
			}
			// If 'new game' is selected the option of 1 player or 2 player is
			// shown
			else if (x > 740 && y > 300 && x < 940 && y < 350
					&& isNewGameSelected)
			{
				// The buttons must be pressed before they can be released
				if (onePlayerPressed || twoPlayerPressed)
				{
					if (x > 740 && x < 840 && y > 300 && y < 350
							&& onePlayerPressed)
					{
						onePlayerSelected = true;
						repaint();
					}
					else if (x > 840 && x < 940 && y > 300 && y < 350
							&& twoPlayerPressed)
					{
						is1P = false;
						newGame();
					}
				}
			}
		}

		// When the mouse is released, nothing can be pressed
		easyPressed = false;
		normalPressed = false;
		hardPressed = false;
		insanePressed = false;
		onePlayerPressed = false;
		twoPlayerPressed = false;
		endGamePressed = false;
		newGamePressed = false;
		repaint();
	}

	/**
	 * Records x and y co-ordinates of mouse when pressed
	 * 
	 * @param event records the position of the pointer when the mouse button is
	 *            pressed
	 */
	public void mousePressed(MouseEvent event)
	{
		int x = event.getX();
		int y = event.getY();

		// For making moves, shouldn't work when the instruction screen is up
		if (!gameOver && !instrPressed)
		{
			// If the mouse is clicked inside the board, and the move is valid,
			// make the move
			if (x > 20 && x < 660 && y > 18 && y < 658)
			{
				int xMove = (x - 20) / 80;
				int yMove = (y - 18) / 80;
				if (isMoveValid(yMove, xMove, turn))
					makeMove(yMove, xMove, turn);
				repaint();
			}

			// Pressing that 'end game' button
			if (x > 740 && y > 300 && x < 940 && y < 350)
			{
				endGamePressed = true;
				repaint();
			}
		}
		else
		{
			// For '1P' or '2P' button, which appears after 'new game' is shown
			if (isNewGameSelected)
			{
				if (x > 740 && x < 840 && y > 300 && y < 350)
				{
					onePlayerPressed = true;
					repaint();
				}
				else if (x > 840 && x < 940 && y > 300 && y < 350)
				{
					twoPlayerPressed = true;
					repaint();
				}
			}

			// For 'new game' button
			else if (x > 740 && y > 300 && x < 940 && y < 350
					&& !isNewGameSelected)
			{
				newGamePressed = true;
				repaint();
			}
			// These are the difficulty buttons which appear after '1 player' is
			// selected
			if (onePlayerSelected && x > 740 && x < 940 && y > 300 && y < 400)
			{
				if (x > 740 && x < 840 && y > 300 && y < 350)
				{
					easyPressed = true;
				}
				else if (x > 840 && x < 940 && y > 300 && y < 350)
				{
					normalPressed = true;
				}
				else if (x > 740 && x < 840 && y > 350 && y < 400)
				{
					hardPressed = true;
				}
				else if (x > 840 && x < 940 && y > 350 && y < 400)
				{
					insanePressed = true;
				}
				repaint();
			}
		}

		// The animation toggle button
		if (x > 842 && x < 861 && y > 645 && y < 664)
			if (animationOn)
				animationOn = false;
			else
				animationOn = true;

		// The instruction button
		else if (x > 943 && x < 993 && y > 625 && y < 675)
			if (instrPressed)
				instrPressed = false;
			else
				instrPressed = true;
		repaint();
	}

	/**
	 * Needs to be overridden for other mouse events to be used
	 * 
	 * @param event records the position of the pointer when it enters the
	 *            screen
	 */
	@Override
	public void mouseEntered(MouseEvent event)
	{
	}

	/**
	 * Needs to be overridden for other mouse events to be used
	 * 
	 * @param event records the position of the pointer when it exits the screen
	 */
	@Override
	public void mouseExited(MouseEvent event)
	{
	}

	/**
	 * Needs to be overridden for other mouse events to be used
	 * 
	 * @param event records the position of the pointer when the mouse button is
	 *            clicked
	 */
	@Override
	public void mouseClicked(MouseEvent event)
	{
	}

	/**
	 * Stars a new game, resets values to default so as to not interfere with
	 * the new game
	 */
	public void newGame()
	{
		isNewGameSelected = false;
		onePlayerSelected = false;
		clearBoard();
		updateScore();
		turn = WHITE;
		gameOver = false;
		repaint();
	}

	/**
	 * Clears the current reversi board, and flipBoard so that we don't run into
	 * problems involving animations when a game is ended
	 */
	public void clearBoard()
	{
		// Resets the board to all clear
		for (int x = 0; x < 8; x++)
			for (int y = 0; y < 8; y++)
			{
				board[y][x] = 0;
				flipBoard[y][x] = 0;
			}

		// Sets the starting pieces
		board[3][3] = WHITE;
		board[4][4] = WHITE;
		board[3][4] = BLACK;
		board[4][3] = BLACK;
	}

	/**
	 * Checks whether or not a position is on the board
	 * 
	 * @param yPos the y position of the square in the board array
	 * @param xPos the x position of the square in the board array
	 * @return true if the position exists on the board array, false if the
	 *         position does not exist on the board array
	 */
	public static boolean isOnBoard(int yPos, int xPos)
	{
		return (yPos <= 7 && xPos <= 7 && yPos >= 0 && xPos >= 0);
	}

	/**
	 * Checks whether or not a piece can be added to a certain position on the
	 * board
	 * 
	 * @param yPos the y position of the square in the board array to add to
	 * @param xPos the x position of the square in the board array to add to
	 * @param colour the colour of the piece to be added
	 * @return true if the piece can be added to the position false if the
	 *         position cannot be added to the position
	 */
	public boolean isMoveValid(int yPos, int xPos, int colour)
	{
		// If the position is occupied by another piece
		if (board[yPos][xPos] != 0)
		{
			return false;
		}
		else
		{
			// Checks in all 8 directions from the designated position
			// for an enemy piece in the immediate surroundings
			for (int yShift = -1; yShift < 2; yShift++)
				for (int xShift = -1; xShift < 2; xShift++)
					// Bypasses the self-check
					if (!(yShift == 0 && xShift == 0))
					{
						// Sets co-ordinates of the immediate surroundings
						int newY = yPos + yShift;
						int newX = xPos + xShift;

						// Sometimes the new co-ordinates may not be on the
						// board and only if there are surrounding enemy pieces
						// can a piece be placed in that directional line
						if (isOnBoard(newY, newX)
								&& board[newY][newX] == -colour)

							// Looks for the co-ordinates of the next
							// occurrence of the same coloured piece
							// but stopping at any empty squares - if a same
							// coloured piece exists along
							// the same direction, then a piece can be
							// placed in the designated position
							while (isOnBoard(newY, newX)
									&& board[newY][newX] != EMPTY)
							{
								if (board[newY][newX] == colour)
									return true;
								newY += yShift;
								newX += xShift;
							}
					}
		}

		// If all eight directions have been checked without any positives,
		// then no possible move exists
		return false;
	}

	/**
	 * Places a piece in a position and adjusts the board accordingly
	 * 
	 * @param yPos the y position of the square in the board array to add to
	 * @param xPos the x position of the square in the board array to add to
	 * @param color the color of the piece to be added
	 */
	public void makeMove(int yPos, int xPos, int colour)
	{
		// Sets piece on board
		board[yPos][xPos] = colour;

		// Goes through all eight directions
		for (int yShift = -1; yShift < 2; yShift++)
			for (int xShift = -1; xShift < 2; xShift++)
				// Bypasses the self-check
				if (!(yShift == 0 && xShift == 0))
				{
					// Sets co-ordinates of the next square in the
					// directional lane
					int newY = yPos + yShift;
					int newX = xPos + xShift;

					// Checks if the square has an enemy piece on it and
					// exists on the board
					if (isOnBoard(newY, newX) && board[newY][newX] == -colour)
					{
						// Goes through the directional lane, to find the
						// first occurrence
						// of a same coloured piece, then flips all enemy
						// pieces in between,
						// also stops when the search reaches an empty
						// square
						while (isOnBoard(newY, newX)
								&& board[newY][newX] != EMPTY)
						{
							if (board[newY][newX] == colour)
							{
								flipAllBetween(yPos, xPos, newY, newX, colour,
										yShift, xShift);
								newY += 25;
							}
							newY += yShift;
							newX += xShift;
						}
					}
				}

		// After the move is made, and all pieces are flipped, update the score
		// and animate the flips if animations are on
		turn *= -1;
		updateScore();

		// Sets the originally placed piece as a non-flip
		flipBoard[yPos][xPos] = 0;

		// Animate pieces if animations are toggled on
		if (animationOn)
			animatePieces();

		repaint();

		// If neither player can move, the game is over
		// Else, just skip the current player's turn
		if (noOfMovesPossible(turn) == 0)
		{
			if (noOfMovesPossible(-turn) == 0)
				gameOver = true;
			else
			{
				// Displays the 'no moves' text for 1.2 seconds
				noMoves = true;
				repaintImmediately();
				delay(1200);
				turn *= -1;
				noMoves = false;
			}
		}

		// The AI's turn comes right after the user's
		if (is1P && turn == BLACK && !gameOver)
		{
			// Adds a delay before the AI's move if animations are on
			if (animationOn)
				delay(320);
			AIMove();
		}
		repaint();
	}

	/**
	 * Used to tidy up the makeMove() code, decides which move the AI should
	 * make based off of selected difficulty
	 */
	public void AIMove()
	{
		// isMinimax used to be vital to stop recursion, but it's not necessary
		// now that a logic board is made
		// It's only still there just in case
		if (isMinimax && turn == BLACK)
		{
			int xAndY;

			// Makes a move depending on the difficulty selected
			if (difficulty == 1)
			{
				xAndY = stupid(BLACK);
			}
			else if (difficulty == 2)
			{
				xAndY = somewhatUnbeatable(BLACK);
			}
			else if (difficulty == 3)
			{
				xAndY = prettyUnbeatable(BLACK);
			}
			else
			{
				xAndY = prettyDamnUnbeatable(BLACK);
			}
			int x = xAndY / 10;
			int y = xAndY % 10;

			makeMove(y, x, BLACK);
		}
	}

	/**
	 * Flips all pieces between two sets of co-ordinates
	 * 
	 * @param startY the y position of the first piece
	 * @param startX the x position of the first piece
	 * @param endY the y position of the second piece
	 * @param endX the x position of the first piece
	 * @param yShift the rise of the line extending from the first to second
	 *            piece
	 * @param xShift the run of the line extending from the first to second
	 *            piece
	 * @param colour the colour of the two pieces
	 */
	public void flipAllBetween(int startY, int startX, int endY, int endX,
			int colour, int yShift, int xShift)
	{
		// Calculates the distances between the two pieces
		int distance = Math.max(Math.abs(startY - endY),
				Math.abs(startX - endX));

		// Flips any piece between the two pieces
		for (int flips = 0; flips < distance; flips++)
		{
			// If animations are on, marks each flipping piece on the flipboard
			board[startY][startX] = colour;
			if (animationOn)
			{
				flipBoard[startY][startX] = colour * 8;
			}
			startY += yShift;
			startX += xShift;
		}
	}

	/**
	 * Updates the image that should be shown to 'animate' the flipping of the
	 * pieces
	 */
	public void animatePieces()
	{
		// This boolean is mainly used so that 'possible moves' aren't
		// highlighted when pieces are being flipped
		animateDone = false;

		// There are 8 images in the 'flip'
		for (int seq = 0; seq < 9; seq++)
		{
			// Flips every piece just a little, all at the same time, repaints,
			// then changes the image until the flip is complete
			for (int x = 0; x < 8; x++)
			{
				for (int y = 0; y < 8; y++)
				{
					if (flipBoard[y][x] > 0)
					{
						flipBoard[y][x]--;
					}
					else if (flipBoard[y][x] < 0)
					{
						flipBoard[y][x]++;
					}
				}
			}
			repaintImmediately();
			delay(20);
		}
		animateDone = true;
	}

	/**
	 * Refresh the drawing area immediately, immediate refresh is needed to show
	 * the animation properly
	 */
	public void repaintImmediately()
	{
		this.paintImmediately(new Rectangle(0, 0, this.getWidth(), this
				.getHeight()));
	}

	/**
	 * Determines number of possible moves for a player
	 * 
	 * @param colour the player whose possibility of moves will be checked
	 * @return the number of possible moves the given player has
	 */
	public int noOfMovesPossible(int colour)
	{
		// Tracks number of possible moves
		int possibleMoves = 0;

		// Goes through entire board
		for (int yPos = 0; yPos < 8; yPos++)
			for (int xPos = 0; xPos < 8; xPos++)
				// When an empty square is found, a piece
				// may potentially be placed
				if (board[yPos][xPos] == 0)
				{
					// If a piece can be placed at this square
					// then a move is possible
					if (isMoveValid(yPos, xPos, colour))
					{
						possibleMoves++;
					}
				}
		return possibleMoves;
	}

	/**
	 * Updates the player scores
	 */
	public void updateScore()
	{
		whiteScore = getScore(WHITE);
		blackScore = getScore(BLACK);
	}

	/**
	 * Gets the score of a certain player
	 * 
	 * @param colour the player whose score will be checked
	 * @return the score of the player
	 */
	public int getScore(int colour)
	{
		int score = 0;

		// Goes through entire board and counts the number of pieces
		for (int yPos = 0; yPos < 8; yPos++)
			for (int xPos = 0; xPos < 8; xPos++)
				if (board[yPos][xPos] == colour)
					score++;
		return score;
	}

	/**
	 * Counts the number of pieces on the board
	 * 
	 * @returns the number of pieces on the board
	 */
	public int noOfPieces()
	{
		int count = 0;

		// Goes through the entire board counting all the pieces
		for (int yPos = 0; yPos < 8; yPos++)
			for (int xPos = 0; xPos < 8; xPos++)
				if (board[yPos][xPos] != 0)
					count++;
		return count;
	}

	/**
	 * The hardest AI, uses a combination of mini-max, heuristics, and pruning
	 * to achieve victory.
	 * 
	 * @param color the color of the AI player
	 * @returns the best move determined by heuristic mini-max
	 */
	public int prettyDamnUnbeatable(int color)
	{
		LogicBoard game = new LogicBoard(this, color);
		int noOfPieces = noOfPieces();

		// Not too many possibilities in the beginning, so a deeper search is
		// fine
		if (noOfPieces >= 4 && noOfPieces < 12)
		{
			return game.zeroDepthHeuristicMaximize(0, 5);
		}
		// Many possibilities even with pruning in the middle, so a shallow
		// search is used
		else if (noOfPieces >= 12 && noOfPieces < 48)
		{
			return game.zeroDepthHeuristicMaximize(0, 4);
		}
		// Nearing the end, possibilities for moves dwindles, so a deep search
		// can be used
		else if (noOfPieces >= 48 && noOfPieces < 54)
		{
			return game.zeroDepthHeuristicMaximize(0, 6);
		}

		// At the end, there are very few possibilities left, so a very deep
		// search is used
		return game.zeroDepthHeuristicMaximize(0, 12);
	}

	/**
	 * The hard AI, uses a combination of mini-max, heuristics, and pruning to
	 * achieve victory. Similar to the hardest AI, only less depth of search
	 * (i.e. shortsighted).
	 * 
	 * @param color the color of the AI player
	 * @returns the best move determined by heuristic mini-max
	 */
	public int prettyUnbeatable(int color)
	{
		LogicBoard game = new LogicBoard(this, color);

		// Uses a shallow mini-max search
		return game.zeroDepthHeuristicMaximize(0, 3);
	}

	/**
	 * The normal AI, uses a combination of heuristics and greedy algorithm to
	 * achieve victory.
	 * 
	 * @param color the color of the AI player
	 * @returns the best move determined by greedy heuristics
	 */
	public int somewhatUnbeatable(int color)
	{
		LogicBoard game = new LogicBoard(this, color);
		ArrayList<LogicBoard> outcomes = game.getAllImmediateBoards(color);
		ArrayList<Integer> moves = game.getAllImmediateMoves(color);
		int highestBoardValue = Integer.MIN_VALUE;
		int bestIndex = 0;

		// Due to importance of corners, any move that allows the AI to take a
		// corner
		// is returned instantly once found
		if (moves.contains(new Integer(70)))
		{
			return 70;
		}
		else if (moves.contains(new Integer(7)))
		{
			return 7;
		}
		else if (moves.contains(new Integer(0)))
		{
			return 0;
		}
		else if (moves.contains(new Integer(77)))
		{
			return 77;
		}

		// Looks for the best board heuristic-value wise for the AI
		for (LogicBoard outcome : outcomes)
		{
			int value = outcome.evaluateBoard();

			if (value > highestBoardValue)
			{
				highestBoardValue = value;
				bestIndex = outcomes.indexOf(outcome);
			}
		}
		return moves.get(bestIndex);
	}

	/**
	 * The easy AI, makes moves at random.
	 * 
	 * @param color the color of the AI player
	 * @returns the randomly determined move
	 */
	public int stupid(int color)
	{
		LogicBoard game = new LogicBoard(this, color);
		ArrayList<Integer> moves = game.getAllImmediateMoves(color);
		int index = (int) (Math.random() * moves.size());

		return moves.get(index);
	}
}
