import java.util.ArrayList;

/**
 * Since ReversiBoard has many graphical fields and variables, to use
 * ReversiBoard as the board which the AI uses to calculate its moves would be
 * inefficient, so the LogicBoard, a stripped-down version of ReversiBoard which
 * retains the same basic non-graphical functions, is used by the AI in its
 * calculations.
 * 
 * The LogicBoard tracks a board's data, which is used in calculations by the
 * AI.
 * 
 * @author Kewei Zhou
 * @version January 2014
 */
public class LogicBoard
{
	// The multipliers are "weights" which will be attached to each heuristic
	// value,and will affect the AI's overall decision-making, they will be 
	// modified as the game progresses.
	private int scoreMultiplier = 100;
	private int positionMultiplier = 0;
	private int mobilityMultiplier = 0;
	private int stabilityMultiplier = 0;

	// These variables track the Reversi board as the AI sees it.
	private int[][] logicBoard = new int[8][8];
	private int whiteScore = 0;
	private int blackScore = 0;
	private int pieceCount = 0;
	private int turn;
	private final static int BLACK = -1;
	private final static int WHITE = 1;

	// This heuristics board is used to give value to positions on a Reversi
	// board these values were determined somewhat arbitrarily, and based on 
	// a novice's opinion, but the reasoning is sound, corners are the most 
	// important parts of the board, since they act as anchors for flipping.
    // The sides and parts of the center do the same. The center is also 
	// somewhat important since it acts as an anchor for corner flips.
	private static int[][] heuristicsBoard = {
			{ 300, 0, 72, 56, 56, 72, 0, 300 }, { 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 72, 0, 21, 16, 16, 21, 0, 72 }, { 56, 0, 16, 21, 21, 16, 0, 56 },
			{ 56, 0, 16, 21, 21, 16, 0, 56 }, { 72, 0, 21, 16, 16, 21, 0, 72 },
			{ 0, 0, 0, 0, 0, 0, 0, 0 }, { 300, 0, 72, 56, 56, 72, 0, 300 } };

	/**
	 * Constructs a LogicBoard using a ReversiBoard, copying over essential
	 * information such as turn, piece positions, piece count,and score.
	 * 
	 * @param currentBoard the ReversiBoard to be copied
	 * @param playerTurn whose turn it was on the ReversiBoard
	 */
	public LogicBoard(ReversiBoard currentBoard, int playerTurn)
	{
		turn = playerTurn;

		for (int yPos = 0; yPos < 8; yPos++)
		{
			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (currentBoard.board[yPos][xPos] != 0)
				{
					logicBoard[yPos][xPos] = currentBoard.board[yPos][xPos];
					pieceCount++;

					if (currentBoard.board[yPos][xPos] == WHITE)
					{
						whiteScore++;
					}
					else
					{
						blackScore++;
					}
				}
			}
		}
	}

	/**
	 * Constructs a LogicBoard using a ReversiBoard, copying over essential
	 * information such as turn, piece positions, piece count, and score.
	 * 
	 * @param currentBoard the LogicBoard to be copied
	 * @param playerTurn whose turn it was on the ReversiBoard
	 */
	public LogicBoard(LogicBoard currentBoard, int playerTurn)
	{
		turn = playerTurn;

		for (int yPos = 0; yPos < 8; yPos++)
		{
			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (currentBoard.logicBoard[yPos][xPos] != 0)
				{
					logicBoard[yPos][xPos] = currentBoard.logicBoard[yPos][xPos];
					pieceCount++;

					if (currentBoard.logicBoard[yPos][xPos] == WHITE)
					{
						whiteScore++;
					}
					else
					{
						blackScore++;
					}
				}
			}
		}
	}

	/**
	 * This is used in the minimax when the maximum depth or base case is
	 * reached to modify the multipliers depending on the state of the board.
	 * Certain heuristic values carry more weight than others during different
	 * stages of the game.
	 * 
	 * @param notHeuristic used to determine whether or not multiplier
	 *            modification is needed
	 */
	public void modifyMultipliers()
	{
		// The main weakness of many Reversi AI, is that they expand far too
		// much, far too fast, until they hit a corner/edge and run out of 
		// moves, allowing the enemy to flip them en-masse repeatedly.
		// To prevent this from happening, in the beginning, the AI prioritizes
		// mobility, so it is willing to sacrifice the score-lead in the 
		// beginning to open up chances to make moves that will be decisive
		// later on in the game.
		if (pieceCount < 16)
		{
			scoreMultiplier = 0;
			positionMultiplier = 0;
			mobilityMultiplier = 100;
			stabilityMultiplier = 0;
		}
		else if (pieceCount < 24)
		{
			scoreMultiplier = 0;
			positionMultiplier = 30;
			mobilityMultiplier = 70;
			stabilityMultiplier = 0;
		}
		// In the mid-game, maintaining a constant presence and possibilities of
		// attack through position/mobility is the most important.
		else if (pieceCount < 32)
		{
			scoreMultiplier = 30;
			positionMultiplier = 30;
			mobilityMultiplier = 30;
			stabilityMultiplier = 10;
		}
		else if (pieceCount < 50)
		{
			scoreMultiplier = 30;
			positionMultiplier = 20;
			mobilityMultiplier = 20;
			stabilityMultiplier = 30;
		}
		// Approaching the end-game, the only thing that matters is having the
		// most amount of pieces, and along the approach, stability must be maintained 
		// in the early end-game so that the enemy won't be able to flip the AI's 
		// pieces en-masse and suddenly "turn the tables".
		else if (pieceCount < 56)
		{
			scoreMultiplier = 60;
			positionMultiplier = 10;
			mobilityMultiplier = 10;
			stabilityMultiplier = 20;
		}
		else if (pieceCount < 64)
		{
			scoreMultiplier = 100;
			positionMultiplier = 0;
			mobilityMultiplier = 0;
			stabilityMultiplier = 0;
		}
	}

	/**
	 * Prunes a given list of possible moves/boards. The aim of this prune is to
	 * prioritize any moves that allow the player to take a corner, and also to
	 * eliminate any moves that allow the enemy to take a corner.
	 * 
	 * @param boards the ArrayList of boards to prune
	 * @param color the player that the prune favours
	 */
	public void cornerPrune(ArrayList<LogicBoard> boards, int color)
	{
		// Creates an ArrayList to track possible moves/boards that deny enemy
		// corners
		ArrayList<LogicBoard> outcomes = new ArrayList<LogicBoard>();

		// Looks for potential moves/boards that give the player control of the
		// corner
		for (LogicBoard board : boards)
		{
			if (this.logicBoard[0][0] == 0 && board.logicBoard[0][0] == color
					|| this.logicBoard[7][0] == 0
					&& board.logicBoard[7][0] == color
					|| this.logicBoard[0][7] == 0
					&& board.logicBoard[0][7] == color
					|| this.logicBoard[7][7] == 0
					&& board.logicBoard[7][7] == color)
			{
				outcomes.add(board);
			}
		}

		// If even one move is found, the given ArrayList's reference is
		// switched to the new ArrayList, and the mini-max will proceed 
		// with the new ArrayList
		if (outcomes.size() != 0)
		{
			boards = outcomes;
		}
		// If no move is found that gives the player control of the corner, then
		// it looks for moves that deny the enemy control of the corner
		else
		{
			for (LogicBoard board : boards)
			{
				if (!(board.isMoveValid(0, 0, -color) == true
						|| board.isMoveValid(0, 7, -color) == true
						|| board.isMoveValid(7, 0, -color) == true || board
							.isMoveValid(7, 7, -color) == true))
				{
					outcomes.add(board);
				}
			}

			if (outcomes.size() != 0)
			{
				boards = outcomes;
			}
		}

		// If absolutely no moves are found that give player control of the
		// corners or deny the enemy corners, then the given ArrayList of 
		// potential moves/boards is unchanged
	}

	/**
	 * Prunes a given list of possible moves/boards. The aim of this prune is to
	 * prioritize any moves that allow the player to take a corner, and also to
	 * eliminate any moves that allow the enemy to take a corner. This method is
	 * similar to the cornerPrune method, only it also prunes a given ArrayList
	 * of moves - this is used in the beginning of the zero depth mini-max since
	 * we want the ArrayLists of boards and moves to be parallel to each other,
	 * so that we can use indexes to find the best move to return.
	 * 
	 * @param boards the ArrayList of boards to prune
	 * @param moves the ArrayList of moves to prune
	 * @param color the player that the prune favours
	 */
	public void beginningCornerPrune(ArrayList<LogicBoard> boards,
			ArrayList<Integer> moves, int color)
	{
		// Creates an ArrayList to track possible moves/boards that deny enemy
		// corners
		ArrayList<LogicBoard> prunedBoards = new ArrayList<LogicBoard>();
		ArrayList<Integer> prunedMoves = new ArrayList<Integer>();

		// Looks for potential moves/boards that give the player control of the
		// corner
		for (LogicBoard board : boards)
		{
			if (this.logicBoard[0][0] == 0 && board.logicBoard[0][0] == color
					|| this.logicBoard[7][0] == 0
					&& board.logicBoard[7][0] == color
					|| this.logicBoard[0][7] == 0
					&& board.logicBoard[0][7] == color
					|| this.logicBoard[7][7] == 0
					&& board.logicBoard[7][7] == color)
			{
				prunedBoards.add(board);
				prunedMoves.add(moves.get(prunedBoards.indexOf(board)));
			}
		}

		// If even one move is found, both given ArrayList's reference is
		// switched to the new ArrayList, and the mini-max will proceed 
		// with the new ArrayLists
		if (prunedBoards.size() != 0)
		{
			boards = prunedBoards;
			moves = prunedMoves;
		}
		// If no move is found that gives the player control of the corner, then
		// it looks for moves that deny the enemy control of the corner
		else
		{
			for (LogicBoard board : boards)
			{
				if (!(board.isMoveValid(0, 0, -color) == true
						|| board.isMoveValid(0, 7, -color) == true
						|| board.isMoveValid(7, 0, -color) == true || board
							.isMoveValid(7, 7, -color) == true))
				{
					prunedBoards.add(board);
					prunedMoves.add(moves.get(prunedBoards.indexOf(board)));
				}
			}

			if (prunedBoards.size() != 0)
			{
				boards = prunedBoards;
				moves = prunedMoves;
			}
		}

		// If absolutely no moves/boards are found that give player control of
		// the corners or deny the enemy corners, then the given ArrayLists of 
		// potential moves/boards reference is unchanged.
	}

	/**
	 * The minimax initializer, due to the way the game is implemented
	 * graphically, we cannot simply repaint entire boards over and over, and so
	 * we use a method to get all possible moves, which is closely related to a
	 * method which gets all possible boards (they are related arrayLists), and
	 * track the index of the best boards, which will be used to get the best
	 * move from the moves array.
	 * 
	 * @param depth the current depth
	 * @param maxDepth the maximum depth
	 * @param notHeuristic determines whether or not minimax will be score or
	 *            heuristics based
	 * @returns the index of the best board
	 */
	public int zeroDepthHeuristicMaximize(int depth, int maxDepth)
	{
		int bestValue = Integer.MIN_VALUE;
		int index = 0;
		int bestIndex = 0;

		ArrayList<LogicBoard> boards = getAllImmediateBoards(BLACK);
		ArrayList<Integer> moves = getAllImmediateMoves(BLACK);

		// Does not prune near the end-game because, because corners are not necessarily
		// the best squares to take, since only score matters 
		if (pieceCount < 56)
		{
			beginningCornerPrune(boards, moves, BLACK);
		}

		for (LogicBoard outcome : boards)
		{
			int value = outcome.heuristicMinimize(depth + 1, maxDepth);

			if (value > bestValue)
			{
				bestValue = value;
				bestIndex = index;
			}
			index++;
		}

		return moves.get(bestIndex);
	}

	/**
	 * The maximizer in the minimax, looks to maximize the AI's heuristic board
	 * values.
	 * 
	 * @param depth the current depth
	 * @param maxDepth the maximum depth
	 * @param notHeuristic determines whether or not minimax will be score or
	 *            heuristics based
	 * @returns the board value once base case is reached the best board value
	 *          for AI so far if base case is not reached
	 */
	public int heuristicMaximize(int depth, int maxDepth)
	{
		if ((noOfMovesPossible(BLACK) == 0 && noOfMovesPossible(WHITE) == 0)
				|| depth == maxDepth)
		{
			modifyMultipliers();
			return evaluateBoard();
		}

		int bestValue = Integer.MIN_VALUE;

		ArrayList<LogicBoard> boards = getAllImmediateBoards(BLACK);
		
		// Does not prune near the end-game because, because corners are not necessarily
		// the best squares to take, since only score matters 
		if (pieceCount < 56)
		{
			cornerPrune(boards, BLACK);
		}

		for (LogicBoard outcome : boards)
		{
			int value = outcome.heuristicMinimize(depth + 1, maxDepth);

			if (value > bestValue)
			{
				bestValue = value;
			}
		}

		return bestValue;
	}

	/**
	 * The minimizer in the minimax, looks to minimize the AI's heuristic board
	 * values. It assumes that the human player makes the optimal heuristic
	 * decision every time.
	 * 
	 * @param depth the current depth
	 * @param maxDepth the maximum depth
	 * @param notHeuristic determines whether or not minimax will be score or
	 *            heuristics based
	 * @returns the board value once base case is reached the best board value
	 *          for AI so far if base case is not reached
	 */
	public int heuristicMinimize(int depth, int maxDepth)
	{
		if ((noOfMovesPossible(BLACK) == 0 && noOfMovesPossible(WHITE) == 0)
				|| depth == maxDepth)
		{
			modifyMultipliers();
			return evaluateBoard();
		}

		int bestValue = Integer.MAX_VALUE;

		ArrayList<LogicBoard> boards = getAllImmediateBoards(WHITE);

		// Does not prune near the end-game because, because corners are not necessarily
		// the best squares to take, since only score matters 
		if (pieceCount < 56)
		{
			cornerPrune(boards, WHITE);
		}

		for (LogicBoard outcome : boards)
		{
			int value = outcome.heuristicMaximize(depth + 1, maxDepth);

			if (value < bestValue)
			{
				bestValue = value;
			}
		}

		return bestValue;
	}

	/**
	 * Looks for all possible boards, and then all the possible moves associated
	 * with that board. This ArrayList is related to the ArrayList generated by
	 * getAllImmediateBoards as a result.
	 * 
	 * @param playerTurn whose turn it is on the board
	 * @return the ArrayList of all possible moves
	 */
	public ArrayList<Integer> getAllImmediateMoves(int playerTurn)
	{
		ArrayList<Integer> allImmediateMoves = new ArrayList<Integer>();
		ArrayList<LogicBoard> allImmediateBoards = getAllImmediateBoards(playerTurn);

		// Generates all the possible boards, and compares this board with the
		// results to see which moves to which squares lead to which results
		for (LogicBoard result : allImmediateBoards)
		{
			for (int compareYPos = 0; compareYPos < 8; compareYPos++)
			{
				for (int compareXPos = 0; compareXPos < 8; compareXPos++)
				{
					if (result.logicBoard[compareYPos][compareXPos] == playerTurn
							&& this.logicBoard[compareYPos][compareXPos] == 0)
					{
						allImmediateMoves.add(compareXPos * 10 + compareYPos);
					}
				}
			}
		}

		return allImmediateMoves;
	}

	/**
	 * Looks for all possible boards. This ArrayList is related to the ArrayList
	 * generated by getAllImmediateMoves as a result.
	 * 
	 * @param playerTurn whose turn it is on the board
	 * @return the ArrayList of all possible boards
	 */
	public ArrayList<LogicBoard> getAllImmediateBoards(int playerTurn)
	{
		ArrayList<LogicBoard> allImmediateBoards = new ArrayList<LogicBoard>();

		for (int yPos = 0; yPos < 8; yPos++)
		{
			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (logicBoard[yPos][xPos] == 0)
				{
					if (isMoveValid(yPos, xPos, playerTurn))
					{
						LogicBoard result = new LogicBoard(this, playerTurn);
						result.makeMove(yPos, xPos, playerTurn);
						result.turn *= -1;
						result.pieceCount++;
						allImmediateBoards.add(result);
					}
				}
			}
		}

		return allImmediateBoards;
	}

	/**
	 * Evaluates the logicBoard based upon percentage ratio/differences in
	 * heuristic values instead of raw values, giving all heuristic values
	 * similar weight in the beginning, making it easier to attach
	 * multipliers/weights, to modify AI behaviour depending on game stages.
	 * This method only evaluates from the BLACK/AI player's point of view.
	 * 
	 * @return the value of the board
	 * 
	 *         Pre-condition: the evaluation must be from BLACK's point of view.
	 */
	public int evaluateBoard()
	{
		// Returns highest and lowest possible values for victory and defeat
		// boards respectively
		if (noOfMovesPossible(BLACK) == 0 && noOfMovesPossible(WHITE) == 0)
		{
			if (blackScore > whiteScore)
			{
				return Integer.MAX_VALUE;
			}
			else
			{
				return Integer.MIN_VALUE;
			}
		}

		// Tracks/calculates the positional value, score, possible moves, and
		// stability value differences of both players
		int maxPlayerPosition = 0;
		int minPlayerPosition = 0;
		int maxPlayerScore = blackScore;
		int minPlayerScore = whiteScore;
		int maxPlayerMoves = noOfMovesPossible(BLACK);
		int minPlayerMoves = noOfMovesPossible(WHITE);
		int maxPlayerStableDiscs = getStableDiscValue(BLACK);
		int minPlayerStableDiscs = getStableDiscValue(WHITE);

		// Goes through the current LogicBoard incrementing positional values
		for (int yPos = 0; yPos < 8; yPos++)
		{
			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (logicBoard[yPos][xPos] == BLACK)
				{
					maxPlayerPosition += heuristicsBoard[yPos][xPos];
				}
				else if (logicBoard[yPos][xPos] == WHITE)
				{
					minPlayerPosition += heuristicsBoard[yPos][xPos];
				}
			}
		}

		// Calculates the differences as a percentage of whole instead of raw
		// values, giving all heuristic values similar weight in the
		// beginning, making it easier to give one or other more importance in
		// different stages of the game by attaching a multiplier.
		int scoreValue = (int) (100.0 * (maxPlayerScore - minPlayerScore) / (maxPlayerScore + minPlayerScore));
		int mobilityValue = (int) (100.0 * (maxPlayerMoves - minPlayerMoves) / (maxPlayerMoves + minPlayerMoves));
		int positionalValue = (int) (100.0 * (maxPlayerPosition - minPlayerPosition) / (maxPlayerPosition + minPlayerPosition));
		int stabilityValue = (int) (100.0 * (maxPlayerStableDiscs - minPlayerStableDiscs) / (maxPlayerStableDiscs + minPlayerStableDiscs));

		return scoreValue * scoreMultiplier + mobilityValue
				* mobilityMultiplier + positionalValue * positionMultiplier
				+ stabilityValue * stabilityMultiplier;
	}

	/**
	 * Calculates the stability of the board for a given player.
	 * 
	 * @param color the player whose board stability will be calculated
	 * @return the number of weighted stable discs
	 */
	public int getStableDiscValue(int color)
	{
		// Tracks occurences of stable pieces
		int[][] stableArray = new int[8][8];
		int stableCount = 0;

		// Looks for stable pieces at and extending from upper left corner
		// horizontally, vertically, and immediately diagonal
		if (logicBoard[0][0] == color)
		{
			stableArray[0][0] = 1;
			stableCount += 2;

			for (int yPos = 0; yPos < 8; yPos++)
			{
				if (logicBoard[yPos][0] == color)
				{
					stableArray[yPos][0] = 1;
				}
				else
				{
					break;
				}
			}

			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (logicBoard[0][xPos] == color)
				{
					stableArray[0][xPos] = 1;
				}
				else
				{
					break;
				}
			}

			if (logicBoard[0][1] == color && logicBoard[1][1] == color
					&& logicBoard[1][0] == color)
			{
				stableCount++;
			}
		}

		// Looks for stable pieces at and extending from bottom right corner
		// horizontally, vertically, and immediately diagonal
		if (logicBoard[7][7] == color)
		{
			stableArray[7][7] = 1;
			stableCount += 2;

			for (int yPos = 0; yPos < 8; yPos++)
			{
				if (logicBoard[7 - yPos][7] == color)
				{
					stableArray[7 - yPos][7] = 1;
				}
				else
				{
					break;
				}
			}

			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (logicBoard[7][7 - xPos] == color)
				{
					stableArray[7][7 - xPos] = 1;
				}
				else
				{
					break;
				}
			}

			if (logicBoard[6][7] == color && logicBoard[7][6] == color
					&& logicBoard[7][7] == color)
			{
				stableCount++;
			}
		}

		// Looks for stable pieces at and extending from top right corner
		// horizontally, vertically, and immediately diagonal
		if (logicBoard[0][7] == color)
		{
			stableArray[0][7] = 1;
			stableCount += 2;

			for (int yPos = 0; yPos < 8; yPos++)
			{
				if (logicBoard[yPos][7] == color)
				{
					stableArray[yPos][7] = 1;
				}
				else
				{
					break;
				}
			}

			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (logicBoard[0][7 - xPos] == color)
				{
					stableArray[0][7 - xPos] = 1;
				}
				else
				{
					break;
				}
			}

			if (logicBoard[1][7] == color && logicBoard[1][6] == color
					&& logicBoard[0][6] == color)
			{
				stableCount++;
			}
		}

		// Looks for stable pieces at and extending from top right corner
		// horizontally, vertically, and immediately diagonal
		if (logicBoard[7][0] == color)
		{
			stableArray[7][0] = 1;
			stableCount += 2;

			for (int yPos = 0; yPos < 8; yPos++)
			{
				if (logicBoard[7 - yPos][0] == color)
				{
					stableArray[7 - yPos][0] = 1;
				}
				else
				{
					break;
				}
			}

			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (logicBoard[7][xPos] == color)
				{
					stableArray[7][xPos] = 1;
				}
				else
				{
					break;
				}
			}

			if (logicBoard[7][1] == color && logicBoard[6][0] == color
					&& logicBoard[6][1] == color)
			{
				stableCount++;
			}
		}

		// Looks for certain stable pieces all over the board using crude
		// heuristics, by looking at pieces that are surrounded in all 
		// directions. Edges and corner will be counted again here, 
		// increasing the value associated with them, this is intended, 
		// because pieces in the edges and corners are the most
		// important stability-wise, as they prevent enemies from anchoring, 
		// while providing anchors for the AI to attack from.
		for (int yPos = 0; yPos < 8; yPos++)
		{
			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (logicBoard[yPos][xPos] != 0)
				{
					boolean stable = true;

					for (int upDown = -1; upDown < 2; upDown++)
					{
						for (int leftRight = -1; leftRight < 2; leftRight++)
						{
							if (!(upDown == 0 && leftRight == 0))
							{
								int dummyY = yPos;
								int dummyX = xPos;
								dummyY += upDown;
								dummyX += leftRight;

								while (isOnBoard(dummyY, dummyX))
								{
									if (logicBoard[dummyY][dummyX] == 0)
									{
										stable = false;
										break;
									}

									dummyY += upDown;
									dummyX += leftRight;
								}
							}
						}
					}

					if (stable)
					{
						stableCount++;
					}
				}

			}
		}

		// Goes through the stable array which tracked stable pieces on the
		// corners and edges of the board, and increments stable piece count 
		// for each 1 found.
		for (int yPos = 0; yPos < 8; yPos++)
		{
			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (stableArray[yPos][xPos] == 1)
				{
					stableCount += 2;
				}
			}
		}

		return stableCount;
	}

	/**
	 * Compares the current and another LogicBoard based upon piece positions
	 * 
	 * @param compare the other LogicBoard
	 * @return true if all pieces are in the same positions false if even one of
	 *         the pieces are not in the same positions
	 */
	public boolean equals(LogicBoard compare)
	{
		for (int yPos = 0; yPos < 8; yPos++)
		{
			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (logicBoard[yPos][xPos] != compare.logicBoard[yPos][xPos])
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Checks whether or not a position is on the board
	 * 
	 * @param yPos the y position of the square in the board array
	 * @param xPos the x position of the square in the board array
	 * @return true if the position exists on the board array false if the
	 *         position does not exist on the board array
	 */
	public boolean isOnBoard(int yPos, int xPos)
	{
		return (yPos <= 7 && xPos <= 7 && yPos >= 0 && xPos >= 0);
	}

	/**
	 * Places a piece in a position and adjusts the board accordingly
	 * 
	 * @param yPos the y position of the square in the logicBoard to check
	 * @param xPos the x position of the square in the logicBoard to check
	 * @param color the color of the player planning to move to the co-ordinates
	 * 
	 *            Precondition: the co-ordinate parameters must exist on the
	 *            board
	 */
	public boolean isMoveValid(int yPos, int xPos, int colour)
	{
		// If the position is occupied by another piece, cannot be added
		if (logicBoard[yPos][xPos] != 0)
		{
			return false;
		}
		else
		{
			// Checks in all 8 directions from the designated position
			// for an enemy piece in the immediate surroundings
			for (int yShift = -1; yShift < 2; yShift++)
			{
				for (int xShift = -1; xShift < 2; xShift++)
				{
					if (!(yShift == 0 && xShift == 0))
					{
						// Sets co-ordinates of the immediate surroundings
						int newY = yPos + yShift;
						int newX = xPos + xShift;

						// Sometimes the new co-ordinates may not be on the
						// board and only if there are surrounding enemy pieces
						// can a piece be placed in that directional line
						if (isOnBoard(newY, newX)
								&& logicBoard[newY][newX] == -colour)
						{

							// Looks for the co-ordinates of the next occurrence
							// of the same coloured piece but stopping at any 
							// empty squares - if a same coloured piece exists 
							// along the same direction, then a piece can be placed 
							// in the designated position
							while (isOnBoard(newY, newX)
									&& logicBoard[newY][newX] != 0)
							{
								if (logicBoard[newY][newX] == colour)
									return true;
								newY += yShift;
								newX += xShift;
							}
						}
					}
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
	 * @param yPos the y position of the square in the logicBoard to add to
	 * @param xPos the x position of the square in the logicBoard to add to
	 * @param color the color of the piece to be added
	 * 
	 *            Precondition: the co-ordinate parameters must exist on the
	 *            board it must be colour's turn the co-ordinates must lead to
	 *            an empty square
	 */
	public void makeMove(int yPos, int xPos, int colour)
	{
		logicBoard[yPos][xPos] = colour;

		for (int yShift = -1; yShift < 2; yShift++)
		{
			for (int xShift = -1; xShift < 2; xShift++)
			{
				if (!(yShift == 0 && xShift == 0))
				{
					// Sets co-ordinates of the next square in the directional
					// lane
					int newY = yPos + yShift;
					int newX = xPos + xShift;

					// Checks if the square has an enemy piece on it and exists
					// on the board
					if (isOnBoard(newY, newX)
							&& logicBoard[newY][newX] == -colour)
					{
						// Goes through the directional lane, to find the first
						// occurrence of a same coloured piece, then flips all 
						// enemy pieces in between, also stops when the search 
						// reaches an empty square, because an empty square 
						// cannot be between a new piece and an existing one
						while (isOnBoard(newY, newX)
								&& logicBoard[newY][newX] != 0)
						{
							if (logicBoard[newY][newX] == colour)
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
			}
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
	 * 
	 *            Precondition: the co-ordinates must exist on the board
	 */
	public void flipAllBetween(int startY, int startX, int endY, int endX,
			int colour, int yShift, int xShift)
	{
		int distance = Math.max(Math.abs(startY - endY),
				Math.abs(startX - endX));

		for (int flips = 0; flips < distance; flips++)
		{
			// Adjusts the score based on what colour discs have been flipped
			if (logicBoard[startY][startX] == BLACK && colour == WHITE)
			{
				blackScore--;
				whiteScore++;
			}
			else if (logicBoard[startY][startX] == WHITE && colour == BLACK)
			{
				blackScore++;
				whiteScore--;
			}

			logicBoard[startY][startX] = colour;
			startY += yShift;
			startX += xShift;
		}
	}

	/**
	 * Goes through entire LogicBoard to determine number of possible moves for
	 * a player
	 * 
	 * @param colour the player whose possibility of moves will be checked
	 * @return number of possible moves
	 */
	public int noOfMovesPossible(int colour)
	{
		int possibleMoves = 0;

		for (int yPos = 0; yPos < 8; yPos++)
		{
			for (int xPos = 0; xPos < 8; xPos++)
			{
				if (logicBoard[yPos][xPos] == 0)
				{
					if (isMoveValid(yPos, xPos, colour))
					{
						possibleMoves++;
					}
				}
			}
		}

		return possibleMoves;
	}
}
