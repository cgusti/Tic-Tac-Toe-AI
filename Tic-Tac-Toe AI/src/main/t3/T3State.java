package main.t3;

import java.util.*;
import java.util.stream.IntStream;

/**
 * T3 Game Board State, which includes not only which spots have numbers
 * played, but also whose turn the current board state is upon. Contains useful
 * methods to be used by a minimax agent.
 */
public class T3State {
    
    // Private Fields
    // -----------------------------------------------------------------------------
    private int[][] state; //2D array 
    private boolean oddTurn;
    
    // Private Constants
    // -----------------------------------------------------------------------------
    private static final int MAX_MOVE = 6;
    private static final int WIN_TARGET = 13;
    
    // Constructors
    // -----------------------------------------------------------------------------
    
    /**
     * Constructs a new, blank T3 board-state starting with the odd-player's turn
     * @param oddTurn Whether or not P1 places odds
     */
    public T3State (boolean oddTurn) {
        this.state = new int[3][3]; 
        this.oddTurn = oddTurn; //TRUE == OddTurn 
    }
    
    /**
     * Constructs a new T3 board-state with the given configuration (Warning:
     * not a deep copy of the 2D int array).
     * @param oddTurn Whether or not P1 places odds
     * @param state 2D array of ints with the starting game configuration
     */
   
    //use this to generate a new board state with every given move 
    public T3State (boolean oddTurn, int[][] state) {
        this.state = state;
        this.oddTurn = oddTurn;
    }
    
    // Methods
    // -----------------------------------------------------------------------------
    
    /**
     * Determines if the provided action is legal within this state, as decided by
     * 1.whether or not the col and row are in range of the board,
     * 2.if  that spot is occupied or not, and 
     * 3. whether the move number is within the set of allowable player actions on the given turn.
     * 
     * @param act The action being judged for legality
     * @return true if act is legal from this state, false otherwise
     */
    
    public boolean isValidAction (T3Action act) {
        return act.col >= 0 && act.col < state.length && //checks if col is in range
               act.row >= 0 && act.col < state[0].length && //checks if row is in range 
               act.move >= 0 && act.move <= MAX_MOVE && //checks if moves made < 13 
               ((this.oddTurn) ? act.move % 2 == 1 : act.move % 2 == 0) && //checks if action is permittable depending on odd or veven
               state[act.row][act.col] == 0;
    }
    
    /**
     * Returns the next state that would be generated from the calling one
     * after taking the provided action (assuming it is legal). 
     * Note: this returns a new state and is not a mutator of the caller.
     * @param act The action to take from the given state.
     * @return The next board state having taken the given action.
     * @throws IllegalArgumentException if the given action is invalid
     */
    public T3State getNextState (T3Action act) {
        if (!isValidAction(act)) {
            throw new IllegalArgumentException("Chosen action " + act + " is invalid!");
        }
        
        T3State nextState = this.clone();
        nextState.state[act.row][act.col] = act.move;
        nextState.oddTurn = ! this.oddTurn; //not odd turn 
        return nextState;
    }
    
    /**
     * Returns the set (in this case, just a small array) of allowable actions that
     * the current player can take from this given state.
     * @return An array of the possible numbers that can be placed, e.g. [1, 3, 5] for odds.
     */
    public int[] getMoves () {
        return IntStream.range(1, MAX_MOVE+1).filter(i -> (this.oddTurn) ? i % 2 == 1 : i % 2 == 0).toArray();
    }
    
    /**
     * Returns a Map of transitions from the current state in which the map's
     * 
     * 1. keys: are the legal actions from the calling state, and their values are
     * 2. values: the next-states that would be reached from this one by taking that action.
     * @return The map of legal actions to the next states that they lead.
     */
    public Map<T3Action,T3State> getTransitions () {
    
    	TreeMap <T3Action, T3State> result = new TreeMap<>();
    	
    	//check if state is available for to place a move 
    	for (int row = 0; row < state.length; row++) {
    		for (int col = 0; col < state.length; col ++) {
    			if (state[row][col] == 0) {
    			
    				//generate all possible moves based on player 
    				int[] possibleMoves = getMoves();
    				for (int move: possibleMoves) {
     					T3Action action = new T3Action(col, row, move);
    						T3State nextState = getNextState(action);
    						result.put(action, nextState);
    				}
    			}	
    		}
    	}
    	return result;
    	
    }
    
    
    /**
     * Determines if any of the cols, rows, and diagonals in the given state
     * sum to the WIN_TARGET amount (default: 13), and if so, concludes that this
     * is a winning state for whomever made the move that generated it.
     * @return true if the state is a winning terminal, false otherwise
     */
    
    public boolean isWin () {
        return Arrays.asList(
            IntStream.range(0, 3).map(i -> state[i][0]).sum(), // col 0
            IntStream.range(0, 3).map(i -> state[i][1]).sum(), // col 1
            IntStream.range(0, 3).map(i -> state[i][2]).sum(), // col 2
            IntStream.range(0, 3).map(i -> state[i][i]).sum(), // diag TL->BR
            IntStream.range(0, 3).map(i -> state[0+i][2-i]).sum(), // diag TR->BL
            IntStream.of(state[0]).sum(), // row0
            IntStream.of(state[1]).sum(), // row1
            IntStream.of(state[2]).sum()  // row2
        ).contains(WIN_TARGET);
    }
    
    /**
     * Determines if the board state represents a tie game, meaning that all
     * spots have been filled by moves and there is no winner.
     * @return true if a tie game, false otherwise
     */
    public boolean isTie () {
        return !(this.isWin()) && ! Arrays.stream(this.state).flatMapToInt(Arrays::stream).anyMatch(i -> i == 0);
    }
    
    @Override
    public T3State clone () {
        T3State copy = new T3State(this.oddTurn);
        for (int i = 0; i < state.length; i++) {
            copy.state[i] = state[i].clone();
        }
        return copy;
    }
    
    @Override
    public String toString () {
        String result = "";
        for (int[] r : this.state) {
            result += Arrays.toString(r) + "\n";
        }
        return result;
    }
    
    @Override
    public boolean equals (Object other) {
        if (other == this) { return true; }
        if (!(other instanceof T3State)) { return false; }
        T3State otherCast = (T3State) other;
        return this.oddTurn == otherCast.oddTurn && Arrays.deepEquals(this.state, otherCast.state);
    }
    
    @Override
    public int hashCode () {
        return Objects.hash(this.state, this.oddTurn);
    }
    
    
    //main method to test if createTransitions() is working (delete later) 
    
//    public static void main (String[] args) {
//    	T3Player t3 = new T3Player();
//        int[][] state = {
//            {2, 1, 0},
//            {0, 0, 0},
//            {0, 0, 6}
//        };
//        T3State s = new T3State(true, state);
//        System.out.println(s.getTransitions());
//    	
////        
////
//    }
    
    
    
}
