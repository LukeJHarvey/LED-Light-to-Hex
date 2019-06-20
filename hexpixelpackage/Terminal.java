package hexpixelpackage;
import java.io.*;
import java.util.Scanner;
public class Terminal extends Thread {
	static Scanner terminalScan;
	public void run() {
		terminalScan = new Scanner(System.in);
		while(true) {
			try {
				while(terminalScan.hasNextLine()) {
		            String[] nextLine = terminalScan.nextLine().split(" ");
		            if(nextLine[0].equals("import")) {
		                HexPixel.board = new Board(HexPixel.importBoard("Templates/" + nextLine[1]));
		                HexPixel.setNewFrame();
		            }
		            else if(nextLine[0].equals("export")) {
		            	HexPixel.exportBoard("Templates/" + nextLine[1]);
		            }
		            else if(nextLine[0].equals("change")) {
		            	HexPixel.board = nextLine.length == 2 ? new Board(Integer.parseInt(nextLine[1]), Integer.parseInt(nextLine[1])) : new Board(Integer.parseInt(nextLine[1]), Integer.parseInt(nextLine[2]));
		            	HexPixel.numRows = HexPixel.board.getRows();
		            	HexPixel.numColumns = HexPixel.board.getColumns();
		            	HexPixel.setNewFrame();
		            }
		            else if(nextLine[0].equals("exit")) {
		            	HexPixel.frame1.exit();
		            }
		            else {
		            	System.out.println("Import filename \nExport filename \nChange size [column size]");
		            }
		        }
		    }
		    catch(Exception e) {
		    	System.out.println("Exception: " + e);
		    }
		}
	}
}