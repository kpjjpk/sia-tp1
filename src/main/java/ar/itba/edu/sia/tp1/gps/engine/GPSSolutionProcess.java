package ar.itba.edu.sia.tp1.gps.engine;

import java.util.Optional;
import java.util.Queue;

import ar.itba.edu.sia.tp1.gps.GPSProblem;
import ar.itba.edu.sia.tp1.gps.GPSRule;
import ar.itba.edu.sia.tp1.gps.GPSState;
import gnu.trove.map.hash.TObjectIntHashMap;

class GPSSolutionProcess<R extends GPSRule, S extends GPSState<R, S>> {
	// TODO: merge back into GPSEngine
	private final Queue<GPSNode<R, S>> openNodes;
	private final GPSProblem<R, S> problem;

	private final TObjectIntHashMap<S> bestCosts = new TObjectIntHashMap<S>();

	private long explosionCount = 0;

	GPSSolutionProcess(GPSProblem<R, S> problem,
			Queue<GPSNode<R, S>> openNodes) {
		this.problem = problem;
		this.openNodes = openNodes;
		S initialState = problem.getInitialState();
		int initialHValue = getHValue(initialState); // should always be 0
		openNodes.add(new GPSNode<>(null, initialState, 0, initialHValue));
	}

	GPSSolution<R, S> solve() {
		while (!openNodes.isEmpty()) {
			GPSNode<R, S> currentNode = openNodes.poll();
			if (problem.isGoal(currentNode.getState())) {
				return GPSSolution.of(currentNode, explosionCount);
			}
			explode(currentNode);
		}
		return GPSSolution.failure(explosionCount);
	}

	private void explode(GPSNode<R, S> node) {
		explosionCount++;
		if (!isBetterThanCurrentBest(node)) {
			return;
		}
		updateBestCost(node);
		for (R rule : problem.getRules()) {
			Optional<S> newStateOpt = node.getState().apply(rule);

			if (newStateOpt.isPresent()) {
				S newState = newStateOpt.get();
				int newGValue = node.getGValue() + rule.getCost();

				if (isBetterThanCurrentBest(newState, newGValue)) {
					int newHValue = getHValue(newState);

					GPSNode<R, S> newNode = new GPSNode<>(node, rule, newState,
							newGValue, newHValue);
					openNodes.add(newNode);
				}
			}
		}
		return;
	}

	private int getHValue(S state) {
		return problem.getHValue(state);
	}

	// IMPORTANT: this is used to check if a node was visited (even in uninformed algorithms like DFS)
	private boolean isBetterThanCurrentBest(GPSNode<R, S> node) {
		return isBetterThanCurrentBest(node.getState(), node.getGValue());
	}

	// IMPORTANT: this is used to check if a node was visited (even in uninformed algorithms like DFS)
	private boolean isBetterThanCurrentBest(S state, int cost) {
		return !bestCosts.containsKey(state) || cost < bestCosts.get(state);
	}

	// IMPORTANT: this is used to check if a node was visited (even in uninformed algorithms like DFS)
	private void updateBestCost(GPSNode<R, S> node) {
		bestCosts.put(node.getState(), node.getGValue());
	}
}
