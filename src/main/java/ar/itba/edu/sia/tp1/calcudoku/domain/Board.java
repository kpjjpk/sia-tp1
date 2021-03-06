package ar.itba.edu.sia.tp1.calcudoku.domain;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by scamisay on 02/04/16.
 *
 * Tablero Alto Nivel
 *
 * 2 1 3 1 3 2 3 2 1
 *
 *
 * Tablero en Binario
 *
 * 010 100 001 100 001 010 001 010 100
 *
 *
 * Tablero en BitSet
 *
 * 010100001100001010001010100
 *
 *
 */
public class Board {
	private final BitSet data;
	private final int n;
	private final List<Group> groups;

	public Board(int n, List<Group> groups) {
		this.n = n;
		Collections.sort(groups, (g1, g2) -> Integer
				.compare(g1.getPositions().size(), g2.getPositions().size()));
		this.groups = groups;
		this.data = new BitSet(n * n * n);
	}

	private Board(Board baseBoard) {
		this.n = baseBoard.n;
		this.groups = baseBoard.groups;
		this.data = baseBoard.data.get(0, baseBoard.data.size());
	}

	public Board deepCopy() {
		return new Board(this);
	}

	/**
	 * Precondicion: n >= 1 && 0 < value <= n
	 * 
	 * @param position
	 * @param value
	 */
	public void put(Position position, int value) {
		assert value <= n;
		put(position.getRow(), position.getCol(), value);
	}

	public void put(int row, int col, int value) {
		// representation of value in n bits
		BitSet bitValue = new BitSet(n);

		// index starts at 0
		bitValue.set(value - 1);

		putBitSetValue(row, col, bitValue);
	}

	public boolean isValid() {
		return areRowsValid() && areColsValid() && areGroupsValid();
	}

	/**
	 * The binary OR between all elements of each row must be equal to n 1s
	 * 
	 * @return
	 */
	private boolean areRowsValid() {
		BitSet op = new BitSet(n);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				op.or(getCell(i, j));
			}

			// cardinality counts the number of 1s in the BitSet
			if (op.cardinality() < n) {
				return false;
			}
			op.clear();
		}
		return true;
	}

	/**
	 * The binary OR between all elements of each col must be equal to n 1s
	 * 
	 * @return
	 */
	private boolean areColsValid() {
		BitSet op = new BitSet(n);
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				op.or(getCell(i, j));
			}

			// cardinality counts the number of 1s in the BitSet
			if (op.cardinality() < n) {
				return false;
			}
			op.clear();
		}
		return true;
	}

	private boolean areGroupsValid() {
		for (Group aGroup : getCompleteGroups()) {
			if (!aGroup.isCorrect(getValuesForGroup(aGroup))) {
				return false;
			}
		}
		return true;
	}

	public List<Integer> getAllValues() {
		List<Integer> values = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				values.add(getCellValue(i, j));
			}
		}
		return values;
	}

	// cantidad de grupos inválidos
	public int invalidGroupsCount() {
		int count = 0;
		for (Group aGroup : getCompleteGroups()) {
			if (!aGroup.isCorrect(getValuesForGroup(aGroup))) {
				count++;
			}
		}
		return count;
	}

	// cantida de columnas inválidas
	public int invalidColumnsCount() {
		BitSet op = new BitSet(n);
		int count = 0;
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				op.or(getCell(i, j));
			}

			// cardinality counts the number of 1s in the BitSet
			if (op.cardinality() < n) {
				count++;
			}
			op.clear();
		}
		return count;
	}

	private List<Integer> getValuesForGroup(Group aGroup) {
		return aGroup.getPositions().stream()
				.map(aPosition -> getCellValue(aPosition))
				.collect(Collectors.toList());
	}

	private BitSet getCell(int i, int j) {
		int beginning = getBeginningOfCell(i, j);
		return data.get(beginning, beginning + n);
	}

	/**
	 * put the 'bitvalue' at the index given by 'position' in data
	 * 
	 * @param position
	 * @param bitValue
	 */
	private void putBitSetValue(int row, int col, BitSet bitValue) {
		int beginning = getBeginningOfCell(row, col);

		for (int i = 0; i < n; i++) {
			data.set(beginning + i, bitValue.get(i));
		}
	}

	/**
	 * devuelve index en el bitset para el inicio del dato en el casillero
	 */
	private int getBeginningOfCell(int row, int col) {
		return row * n + col * n * n;
	}

	public Integer getCellValue(Position position) {
		return getCellValue(position.getRow(), position.getCol());
	}

	public Integer getCellValue(int i, int j) {
		BitSet cell = getCell(i, j);
		for (int index = 0; index < n; index++) {
			if (cell.get(index)) {
				return index + 1;
			}
		}
		return null;
	}

	public void swapCellValues(Position from, Position to) {
		int aux = getCellValue(to);
		put(to, getCellValue(from));
		put(from, aux);
	}

	public List<Group> getCompleteGroups() {
		List<Group> completeGroups = new ArrayList<>(groups.size());
		for (Group group : groups) {
			if (isACompleteGroup(group)) {
				completeGroups.add(group);
			}
		}
		return completeGroups;
	}

	private boolean isACompleteGroup(Group aGroup) {
		for (Position aPosition : aGroup.getPositions()) {
			if (getCellValue(aPosition) == null) {
				return false;
			}
		}
		return true;
	}

	public int getN() {
		return n;
	}

	public List<Group> getGroups() {
		return groups;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Board board = (Board) o;

		return data.equals(board.data);

	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("\n");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				sb.append(getCellValue(i, j));
				if (j < n - 1) {
					sb.append(" ");
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public String fullToString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				sb.append(getCellValue(i, j));
				if (j < n - 1) {
					sb.append("\t");
				}
			}
			sb.append("\n");
		}

		sb.append("\n\nGroups:\n");
		for (Group aGroup : groups) {
			sb.append(aGroup.toString() + "\n");
		}

		return sb.toString();
	}
}
