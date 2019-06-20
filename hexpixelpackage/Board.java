
package hexpixelpackage;
import java.lang.Math.*;
public class Board {
	private class Tile {
		int active;				//-1 = dead, 1 = alive, 2 = special
		int nextTo = 0;			//Have to keep track of nextTo for nextMove() to work correctly
		Tile() {
			this.active = 0;
		}
		Tile(int active) {
			this.active = active;
		}
	}
	private Tile board[][];

	Board() {
		board = new Tile[16][16];
		initializeBoard();
	}
	Board(int row, int col) {
		board = new Tile[row][col];
		initializeBoard();
	}
	Board(int[][] pBoard) {
		board = new Tile[pBoard.length][pBoard[0].length];
		initializeBoard();
		for (int i = 0; i < pBoard.length; i++) {
			for (int j = 0; j < pBoard[0].length; j++) {
				board[i][j] = new Tile(pBoard[i][j]);
			}
		}
	}
	private void initializeBoard() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				board[i][j] = new Tile();
			}
		}
	}
	//Changes Tile based on active
	public void changeTile(int row, int col, int active) {
		Tile piece = board[row][col];
		piece.active = active;
	}
	public boolean isActive(int row, int col) {
		return board[row][col].active != 0;
	}
	public int getActive(int row, int col) {
		return board[row][col].active;
	}
	public void resetBoard() {
		board = new Tile[board.length][board[0].length];
		initializeBoard();
	}
	public void resetBoard(int row, int col) {
		board = new Tile[row][col];
		initializeBoard();
	}
	public int getRows() {
		return board.length;
	}
	public int getColumns() {
		return board[0].length;
	}
	public String toString() { 
		String str = "";
		str += "{\n";
		for(int i = 0; i < this.getRows(); i++) {
			str += "	{";
			for (int j = 0; j < (this.getColumns()/8); j++) {
				str += " 0x";
				double first = 0;
				for (int k = 0; k < 4; k++) {
					double f = Math.pow(2, 3-k);
					first += f * board[i][(j*8)+k].active;
				}
				str += HexPixel.decTohex(first);
				double second = 0;
				for (int k = 0; k < 4; k++) {
					double s = Math.pow(2, 3-k);
					second += s * board[i][(j*8)+k+4].active;
				}
				str += HexPixel.decTohex(second);
				if(j+1 != this.getColumns()/8)
					str += ",";
				else
					str += " ";
			}
			str += "},\n";
		}
		str += "}";
		return str;
	}

}