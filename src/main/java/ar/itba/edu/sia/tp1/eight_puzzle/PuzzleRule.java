package ar.itba.edu.sia.tp1.eight_puzzle;

import java.awt.Point;

import ar.itba.edu.sia.tp1.gps.GPSRule;

public class PuzzleRule implements GPSRule {
	private final Direction direction;

	Point destination; // XXX (mutable!)

	public PuzzleRule(Direction direction) {
		this.direction = direction;
	}

	public Direction getDirection() {
		return direction;
	}

	@Override
	public Integer getCost() {
		return 1;
	}

	@Override
	public String getName() {
		return "Move blank space " + direction.toString();
	}
}
