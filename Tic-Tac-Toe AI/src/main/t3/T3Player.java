package main.t3;

import java.util.*;
import java.io.*;

/**
 * Artificial Intelligence responsible for playing the game of T3! Implements
 * the alpha-beta-pruning mini-max search algorithm
 */
public class T3Player {

	/**
	 * Workhorse of an AI T3Player's choice mechanics that, given a game state,
	 * makes the optimal choice from that state as defined by the mechanics of the
	 * game of Tic-Tac-Total. Note: In the event that multiple moves have
	 * equivalently maximal minimax scores, ties are broken by move col, then row,
	 * then move number in ascending order (see spec and unit tests for more info).
	 * The agent will also always take an immediately winning move over a delayed
	 * one (e.g., 2 moves in the future).
	 * 
	 * @param state The state from which the T3Player is making a move decision.
	 * @return The T3Player's optimal action.
	 */

	// this function evaluates all the available moves for the player using
	// minimax() and returns the optimal move
	public T3Action choose(T3State state) {
		minimaxOutput result = minimax(true, Integer.MIN_VALUE, Integer.MAX_VALUE, state);
		return result.move;
	}

	/**
	 * minimax algorithm that implements alpha - beta pruning technique
	 * 
	 * @param boolean maximizingPlayer flag to indicate maximizer/minimizer player
	 * @param int     alpha value
	 * @param int     beta value
	 * @param T3State position board state
	 * @return minimaxOutput object
	 * 
	 */

	static minimaxOutput minimax(boolean maximizingPlayer, int alpha, int beta, T3State position) {

		int score;
		T3Action bestMove = new T3Action(0, 0, 0);

		// terminating condition: when we have reached a leaf node, we return the
		// utility score of the node
		if (position.isWin() || position.isTie()) {
			score = evaluate(position, maximizingPlayer);
			return new minimaxOutput(score, bestMove);
		}
		
		if (maximizingPlayer) {
			int maxEval = Integer.MIN_VALUE;
			Map<T3Action, T3State> transitions = position.getTransitions();
			for (Map.Entry<T3Action, T3State> entry : transitions.entrySet()) {
				T3Action move = entry.getKey();
				T3State state = entry.getValue();

				// the agent must choose a move that will win immediately.
				if (state.isWin()) {
					return new minimaxOutput(1, move);
				}

				int currentEval = minimax(false, alpha, beta, state).score;
				if (currentEval > maxEval) {
					maxEval = currentEval;
					bestMove = move;
				}

				if (maxEval > alpha) {
					alpha = maxEval;

					
				}
				// alpha-beta pruning
				if (beta <= alpha)
					break;
			}

			return new minimaxOutput(maxEval, bestMove);
		}

		// minimizing player
		else {
			int minEval = Integer.MIN_VALUE;

			Map<T3Action, T3State> transitions = position.getTransitions();

			for (Map.Entry<T3Action, T3State> entry : transitions.entrySet()) {
				T3Action move = entry.getKey();
				T3State state = entry.getValue();

				// killer move
				if (state.isWin()) {
					return new minimaxOutput(-1, move);
				}

				int currentEval = minimax(true, alpha, beta, state).score;
				if (currentEval > minEval) {
					minEval = currentEval;
					bestMove = move;
				}

				if (minEval < beta) {
					beta = minEval;

					
				}
				// alpha-beta pruning
				if (beta <= alpha)
					break;
			}
			return new minimaxOutput(minEval, bestMove);
		}

	}

	/**
	 * Evaluation helper method to assign scores to terminal states in the game tree
	 * 
	 * @param state
	 * @param maximizingPlayer
	 * @return utility score of terminal states
	 * 
	 */
	static int evaluate(T3State state, boolean maximizingPlayer) {
		if (state.isWin()) {
			if (maximizingPlayer) {
				// minimizer wins (max node && win state)
				return -1;
			}
			// maximizer wins (min node && win state)
			return 1;
		}
		// maximizer and minimizer are tied
		return 0;
	}

	/**
	 * private inner class contains int score and T3Action move fields
	 */

	static private class minimaxOutput {
		public int score;
		public T3Action move;

		public minimaxOutput(int score, T3Action move) {
			this.score = score;
			this.move = move;
		}
	}
}