package ar.itba.edu.sia.tp1.gps.engine;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.Optional;
import java.util.Queue;
import java.util.function.Supplier;

import ar.itba.edu.sia.tp1.gps.GPSProblem;
import ar.itba.edu.sia.tp1.gps.GPSRule;
import ar.itba.edu.sia.tp1.gps.GPSState;

class GPSSolutionProcess<R extends GPSRule, S extends GPSState<R, S>> {
	private Queue<GPSNode<R, S>> openNodes;
	private final GPSProblem<R, S> problem;
	private final Supplier<Queue<GPSNode<R, S>>> queueBuilder;
	private final int maxDepth;

	private TObjectIntHashMap<S> bestCosts;
	private int currDepth;
	private long explosionCount = 0;
	private long analyzedNodes = 0;

	GPSSolutionProcess(GPSProblem<R, S> problem,
			Supplier<Queue<GPSNode<R, S>>> queueBuilder,
			SearchStrategy searchStrategy, int maxDepth) {
		this.problem = problem;
		this.maxDepth = maxDepth;
		this.currDepth = startDepthFor(searchStrategy);
		this.queueBuilder = queueBuilder;
	}

	GPSSolution<R, S> solve() {
		while (currDepth <= maxDepth) {
			initOpenNodes();
			while (!openNodes.isEmpty()) {
				GPSNode<R, S> currentNode = openNodes.poll();
				// System.out
				// .println("Analyzing node, g: " + currentNode.getGValue()
				// + " h:" + currentNode.getHValue());
				analyzedNodes++;
				if (problem.isGoal(currentNode.getState())) {
					return GPSSolution.of(currentNode, explosionCount, analyzedNodes);
				}
				explode(currentNode);
			}
			currDepth++;
		}
		return GPSSolution.failure(explosionCount, analyzedNodes);
	}

	protected void explode(GPSNode<R, S> node) {
		if (!isBetterThanCurrentBest(node)) {
			return;
		}
		updateBestCost(node);
		explosionCount++;

		for (R rule : problem.getRules()) {
			Optional<S> newStateOpt = node.getState().apply(rule);

			if (newStateOpt.isPresent()) {
				S newState = newStateOpt.get();
				int newGValue = node.getGValue() + rule.getCost();

				if (newGValue <= currDepth
						&& isBetterThanCurrentBest(newState, newGValue)) {
					int newHValue = getHValue(newState);

					GPSNode<R, S> newNode = new GPSNode<>(node, rule, newState,
							newGValue, newHValue);
					openNodes.add(newNode);
				}
			}
		}
		return;
	}

	private int startDepthFor(SearchStrategy searchStrategy) {
		switch (searchStrategy) {
			case IDDFS :
				return 0;
			default :
				return maxDepth;
		}
	}

	private void initOpenNodes() {
		S initialState = problem.getInitialState();
		int initialHValue = getHValue(initialState);

		bestCosts = new TObjectIntHashMap<S>();

		this.openNodes = queueBuilder.get();
		openNodes.add(new GPSNode<>(null, initialState, 0, initialHValue));
	}

	private int getHValue(S state) {
		return problem.getHValue(state);
	}

	// IMPORTANT: this is used to check if a node was visited (even in
	// uninformed algorithms like DFS)
	private boolean isBetterThanCurrentBest(GPSNode<R, S> node) {
		return isBetterThanCurrentBest(node.getState(), node.getGValue());
	}

	// IMPORTANT: this is used to check if a node was visited (even in
	// uninformed algorithms like DFS)
	private boolean isBetterThanCurrentBest(S state, int cost) {
		return !bestCosts.containsKey(state) || cost < bestCosts.get(state);
	}

	// IMPORTANT: this is used to check if a node was visited (even in
	// uninformed algorithms like DFS)
	private void updateBestCost(GPSNode<R, S> node) {
		bestCosts.put(node.getState(), node.getGValue());
	}
}
