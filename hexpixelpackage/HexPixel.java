/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hexpixelpackage;

import java.io.*;
import java.util.Scanner;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;


public class HexPixel extends JFrame implements Runnable {

    static Window w;
    Thread relaxer;

    boolean animateFirstTime = true;

    Image image;
    Graphics2D g;
    static HexPixel frame1;
    static int numRows;
    static int numColumns;
    public static int columnWidth = 16;
    public static int rowHeight = 16;
    boolean paused = true;
    boolean nextOn = false;
    boolean running = true;
    int lastChanged[] = new int[2];
    static Board board;
    static Scanner fileScan;

    static Thread terminal;

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            board = new Board(importBoard(args[0]));
        } else {
            numRows = Integer.parseInt(args[0]);
            numColumns = Integer.parseInt(args[1]);
            board = new Board(numRows, numColumns);
        }
        w = new Window();

        frame1 = new HexPixel();
        frame1.setSize(w.WINDOW_WIDTH, w.WINDOW_HEIGHT);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setVisible(true);

        terminal = new Terminal();
        terminal.start();
    }

    public HexPixel() {

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    int xpos = e.getX() - w.SIDE_BORDER;
                    int ypos = e.getY() - w.getY(0);

                    int row = ypos / rowHeight;
                    int column = xpos / columnWidth;
                    if (row < numRows && row >= 0 && column < numColumns && column >= 0) {
                        board.changeTile(row, column, board.getActive(row, column) == 0 ? 1 : 0);
                    }
                }
                if (e.BUTTON3 == e.getButton()) {
                    reset();
                }
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    int xpos = e.getX() - w.getX(0);
                    int ypos = e.getY() - w.getY(0);

                    int row = ypos / rowHeight;
                    int column = xpos / columnWidth;
                    if (row < numRows && row >= 0 && column < numColumns && column >= 0) {
                        if (lastChanged[0] != row || lastChanged[1] != column) {
                            lastChanged = new int[] {row, column};
                            board.changeTile(row, column, board.getActive(row, column) == 0 ? 1 : 0);
                        }
                    }
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.VK_N == e.getKeyCode()) {
                    System.out.println(board);
                }
                if (e.VK_RIGHT == e.getKeyCode()) {
                }
                if (e.VK_SPACE == e.getKeyCode()) {
                }
                if (e.VK_R == e.getKeyCode()) {
                    reset();
                }
                repaint();
            }
        });
        init();
        start();
    }

    
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || w.xsize != getSize().width || w.ysize != getSize().height) {
            w.xsize = getSize().width;
            w.ysize = getSize().height;
            image = createImage(w.xsize, w.ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
        }

//fill background
        g.setColor(Color.cyan);

        g.fillRect(0, 0, w.xsize, w.ysize);

        int x[] = {w.getX(0), w.getX(w.getWidth2()), w.getX(w.getWidth2()), w.getX(0), w.getX(0)};
        int y[] = {w.getY(0), w.getY(0), w.getY(w.getHeight2()), w.getY(w.getHeight2()), w.getY(0)};
//fill border
        g.setColor(Color.white);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }


        for (int zrow = 0; zrow < numRows; zrow++) {
            for (int zcolumn = 0; zcolumn < numColumns; zcolumn++) {
                if (board.isActive(zrow, zcolumn)) {
                    g.setColor(Color.BLACK);
                    g.fillRect(w.getX(0) + zcolumn * columnWidth,
                               w.getY(0) + zrow * rowHeight,
                               columnWidth,
                               rowHeight);
                } else  {
                        g.setColor(Color.WHITE);

                    g.fillRect(w.getX(0) + zcolumn * columnWidth,
                               w.getY(0) + zrow * rowHeight,
                               columnWidth,
                               rowHeight);
                }
            }
        }
        g.setColor(Color.blue);
        //horizontal lines
        for (int zi = 1; zi < numRows; zi++) {
            g.drawLine(w.getX(0) , w.getY(0) + zi * w.getHeight2() / numRows ,
                       w.getX(w.getWidth2()) , w.getY(0) + zi * w.getHeight2() / numRows );
        }
        //vertical lines
        for (int zi = 1; zi < numColumns; zi++) {
            g.drawLine(w.getX(0) + zi * w.getWidth2() / numColumns , w.getY(0) ,
                       w.getX(0) + zi * w.getWidth2() / numColumns, w.getY(w.getHeight2())  );
        }
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
// needed for implement runnable
    public void run(){
        while (running) {
            animate();
            repaint();
            double seconds = 0.05;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void terminate() {
        running = false;
    }
/////////////////////////////////////////////////////////////////////////
    public void exit() {
        frame1.dispatchEvent(new WindowEvent(frame1, WindowEvent.WINDOW_CLOSING));
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {
        board.resetBoard();
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (w.xsize != getSize().width || w.ysize != getSize().height) {
                w.xsize = getSize().width;
                w.ysize = getSize().height;
            }
        }
    }
////////////////////////////////////////////////////////////////////////////
    public static void setNewFrame() {
        frame1.setVisible(false);
        frame1.dispose();
        frame1.terminate();
        HexPixel.w = new Window();
        HexPixel.frame1 = new HexPixel();
        HexPixel.frame1.setSize(w.WINDOW_WIDTH, w.WINDOW_HEIGHT);
        HexPixel.frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        HexPixel.frame1.setVisible(true);
    }
////////////////////////////////////////////////////////////////////////////
    public static int[][] importBoard(String fN) throws IOException {
        fileScan = new Scanner(new File(fN));
        int row = 1;
        int col = (fileScan.nextLine().split(",").length-1)*8;
        while (fileScan.hasNextLine()) {
            row++;
            fileScan.nextLine();
        }
        numColumns = col;
        numRows = row;

        int[][] board = new int[row][col];
        fileScan = new Scanner(new File(fN));
        for(int i = 0; i < row; i++) {
            String nextLine = fileScan.nextLine();
            nextLine = nextLine.substring(1, nextLine.length()-1);
            String[] Line = nextLine.split(",");
            for(int j = 0; j < Line.length; j++) {
                int f = hexTodec(Line[j].charAt(2));
                for(int k = 0; k < 4; k++) {
                    board[i][(j*8)+k] = (f >> 3-k) & 1;
                }
                int s = hexTodec(Line[j].charAt(3));
                for(int k = 0; k < 4; k++) {
                    board[i][(j*8)+k+4] = (s >> 3-k) & 1;
                }
            }
        }
        return board;
    }
////////////////////////////////////////////////////////////////////////////
    public static void exportBoard(String fN) throws IOException {
        PrintWriter template = new PrintWriter(new FileWriter(fN));
        for (int i = 0; i < numRows; i++) {
            String str = "";
            str += "{";
            for (int j = 0; j < numColumns/8; j++) {
                str += "0x";
                double first = 0;
                for (int k = 0; k < 4; k++) {
                    double f = Math.pow(2, 3-k);
                    first += f * board.getActive(i, (j*8)+k);
                }
                double second = 0;
                for (int k = 0; k < 4; k++) {
                    double s = Math.pow(2, 3-k);
                    second += s * board.getActive(i, (j*8)+k+4);
                }
                str += decTohex(first);
                str += decTohex(second);

                str += ",";
            }
            str += "},";
            template.println(str.substring(0,str.length()-1));
        }
        template.close();
    }
////////////////////////////////////////////////////////////////////////////
    public static String decTohex(double x) {
        if (x < 10)
            return "" + (int)x;
        switch ((int)x) {
            case 10: return "A";
            case 11: return "B";
            case 12: return "C";
            case 13: return "D";
            case 14: return "E";
            case 15: return "F";
            }
            return "invalid";

    }
    public static int hexTodec(char x) {
        switch ((int)x) {
            case 65: return 10;
            case 66: return 11;
            case 67: return 12;
            case 68: return 13;
            case 69: return 14;
            case 70: return 15; 
            }
            return x-48;
    }
////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
}

/////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////

class Window {
    static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static final int TOP_BORDER = 2;
    static final int SIDE_BORDER = 8;
    static final int BOTTOM_BORDER = 8;
    static final int YTITLE = 22;

    int WINDOW_WIDTH = (SIDE_BORDER * 2);
    int WINDOW_HEIGHT = TOP_BORDER + YTITLE + BOTTOM_BORDER;

    static int xsize = -1;
    static int ysize = -1;

    public Window() {
        int width = (HexPixel.numColumns * 16) + WINDOW_WIDTH;
        int height = (HexPixel.numRows * 16) + WINDOW_HEIGHT;
        int size = 16;
        if (screenSize.getHeight() < height || screenSize.getWidth() < width) {
            width = (int)((screenSize.getWidth() - WINDOW_WIDTH) / HexPixel.numColumns);
            height = (int)((screenSize.getHeight() - WINDOW_HEIGHT) / HexPixel.numRows);
            size = width > height ? height : width;
        }
        WINDOW_WIDTH += size * HexPixel.numColumns;
        WINDOW_HEIGHT += size * HexPixel.numRows;
        HexPixel.columnWidth = size;
        HexPixel.rowHeight = size;
    }

    public int getX(int x) {
        return (x + SIDE_BORDER);
    }
    public int getY(int y) {
        return (y + TOP_BORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + TOP_BORDER + YTITLE + getHeight2());
    }

    public int getWidth2() {
        return (xsize - SIDE_BORDER * 2);
    }
    public int getHeight2() {
        return (ysize - TOP_BORDER - YTITLE - BOTTOM_BORDER);
    }
}