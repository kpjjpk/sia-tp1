package ar.itba.edu.sia.tp1.eight_puzzle;

import java.awt.Point;

import ar.itba.edu.sia.tp1.gps.GPSRule;

public class PuzzleRule implements GPSRule {
	Direction direction;

	Point destination;

	public PuzzleRule(Direction direction) {
		this.direction = direction;
	}

	@Override
	public Integer getCost() {
		return 1;
	}

	@Override
	public String getName() {
		return "Move blank space " + direction.toString();
	}

	/*
	 * public Optional<GPSState> evalRule(GPSState state) { PuzzleState
	 * puzzleState = (PuzzleState) state; Point delta = direction.getDelta();
	 * Point blank = puzzleState.getBlankCoords(); destination = (Point)
	 * blank.clone(); destination.translate(delta.x, delta.y); if (!isValid()) {
	 * return Optional.empty(); }
	 * 
	 * int[][] newMap = Copies.deepCopy(puzzleState.map);
	 * newMap[blank.x][blank.y] = newMap[destination.x][destination.y];
	 * newMap[destination.x][destination.y] = PuzzleState.BLANK; return
	 * Optional.of(new PuzzleState(newMap)); }
	 */
}
