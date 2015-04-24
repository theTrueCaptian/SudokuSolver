package sudokusolvernetbeans;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// File-related imports
import java.io.*;
import java.util.*;

/**
 * Maeda Hanafi  2/24/10
 * Sudoku Solver
 */

public class Sudoku extends Frame implements ActionListener{
    final int MAX_ROW = 9, MAX_COL = 9;
    // File Parameters
    String dataFilePath = null;
    File dataFileName = null;
    JTextArea textArea;

    int[][] sodukoBoard = new int[MAX_ROW][MAX_COL];
    int[][] reportRow = new int[MAX_ROW][MAX_COL];
    int[][] reportCol = new int[MAX_ROW][MAX_COL];
    int[][] reportGrid = new int[MAX_ROW/3][MAX_COL/3];

    boolean solution = true;
    String result = "";
    // Retrieved command code
    String command = "";

    public static void main(String[] args){
        Frame frame = new Sudoku();

        frame.setResizable(true);
        frame.setSize(1200,800);
        frame.setVisible(true);
    }

    public Sudoku(){
        setTitle("Sudoku Solver");

        // Create Menu
        MenuBar mb = new MenuBar();
        setMenuBar(mb);

        Menu menu = new Menu("File");
        mb.add(menu);

        MenuItem miColor = new MenuItem("Open");
        miColor.addActionListener(this);
        menu.add(miColor);

        MenuItem miLine = new MenuItem("Process");
        miLine.addActionListener(this);
        menu.add(miLine);

        MenuItem miExit = new MenuItem("Exit");
        miExit.addActionListener(this);
        menu.add(miExit);

        // End program when window is closed
        WindowListener l = new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                System.exit(0);
            }
            public void windowActivated(WindowEvent ev){
                repaint();
            }
            public void windowStateChanged(WindowEvent ev){
                repaint();
            }
        };

        ComponentListener k = new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                repaint();
            }
        };

        // register listeners
        this.addWindowListener(l);
        this.addComponentListener(k);

        //to display result
        textArea = new JTextArea();
        textArea.setFont(new Font("Serif", Font.PLAIN, 16));
        textArea.setEditable(false);
        this.add(textArea);
        this.pack();
        this.setVisible(true);

        //scollbar
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);

    }

    public void actionPerformed (ActionEvent ev){
        // figure out which command was issued
        command = ev.getActionCommand();

        // take action accordingly
        if("Open".equals(command)){
            dataFilePath = null;
            dataFileName = null;

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogType(JFileChooser.OPEN_DIALOG );
            chooser.setDialogTitle("Open Data File");

            int returnVal = chooser.showOpenDialog(null);
            if( returnVal == JFileChooser.APPROVE_OPTION){
                  dataFilePath = chooser.getSelectedFile().getPath();
                  dataFileName = chooser.getSelectedFile();
            }
            repaint();
        }else if("Process".equals(command)){
             //init report
            reportRow = initReport(MAX_ROW,MAX_COL, 0);
            reportCol = initReport(MAX_ROW,MAX_COL, 0);
            reportGrid = initReport(MAX_ROW/3,MAX_COL/3, 0);
            try{
            	dumpToBoard(dataFileName);
            }catch(IOException ex){

            }
            if(solution){
                result= result + "The filled sudoku board, "+dataFileName+" is the solution";
            }else{
                result= result + "The filled sudoku board, "+dataFileName+" is not the solution";
            }
            dumpToResultFile(result);
            //display results
            repaint();
        }else{
            if("Exit".equals(command)){
                    System.exit(0);
            }
        }
    }

    public void paint(Graphics g){
    	if("Open".equals(command)){
            result = "";
            solution = true;
            // Acknowledge that file was opened
            if (dataFileName != null){
                    g.drawString("File --  "+dataFileName+"  -- was successfully opened", 400, 400);
            }else{
                    g.drawString("NO File is Open", 400, 400);
            }
            return;
        }else if("Process".equals(command)){
            // Display the results
            textArea.setText(result);
            return;
        }
    }

    public void dumpToBoard(File inFile) throws IOException{
    	Scanner reader = new Scanner(inFile);
        //row check
        for(int row = 0; row<9; row++ ){
            for(int col=0; col<9; col++){
                 sodukoBoard[col][row] = reader.nextInt();
                 setReportRow(row, sodukoBoard[col][row]-1, 1);
            }
        }
        for(int col = 0; col<9; col++ ){
           for(int row=0; row<9; row++){
               setReportCol(col, sodukoBoard[col][row]-1, 1);
           }
        }

        result= result + "Check grid\n";
       
        for(int row=0; row<MAX_ROW; row+=3){
            for(int col=0; col<MAX_COL; col+=3){
                checkUniqnessOfGrid(row, col);
            }
        }
        result= result + "Row Check\n";
        displayReport(reportRow);
        result= result + "Col Check\n";
        displayReport(reportCol);
        
    }
    public void setReportRow(int inRow, int inCol, int inVal){
        reportRow[inRow][inCol] += inVal;
        if(reportRow[inRow][inCol]!=1){
            solution = false;
        }
    }

    public void setReportCol(int inCol, int inRow, int inVal){
        reportCol[inRow][inCol] += inVal;
        if(reportCol[inRow][inCol]!=1){
            solution = false;
        }
    }

    public int[][] initReport(int inMaxRow, int inMaxCol, int inVal){
        int[][] tempArray = new int[inMaxRow][inMaxCol];
        for(int i=0; i<inMaxRow; i++){
            for(int j=0; j<inMaxCol; j++){
                tempArray[i][j] = inVal;
            }
        }
        return tempArray;
    }
    
    public void displayReport(int[][] inArray){
        for(int row=0; row<9; row++){
            for(int col=0; col<9; col++){
                result= result + " "+inArray[row][col] + " ";
            }
            result= result + "\n";
        }
     }

     public void checkUniqnessOfGrid(int row, int col){
         int[] gridChecker = new int[MAX_ROW];
         for(int i: gridChecker){
             i = 0;
         }
         for(int i=row; i<(3+row); i++){
             for(int j=col; j<(col+3); j++){
                 gridChecker[sodukoBoard[i][j]-1]++;
                 if(gridChecker[sodukoBoard[i][j]-1]!=1){
                    solution = false;
                 }
              }
         }
         displayGrid(gridChecker);
     }

     public void displayGrid(int[] gridChecker){
        for(int i=0; i<MAX_ROW; i++){
            System.out.print(" "+gridChecker[i]+" ");
            result= result + " "+gridChecker[i]+" ";
         }
         System.out.println();
         result= result + "\n";
     }

      public void dumpToResultFile(String stringResult){
    	PrintWriter output = null;
        try {
            File file = new File("F:\\SudokuSolverNetbeans\\RESULTS.txt");
            output = new PrintWriter(file);
        } catch (FileNotFoundException ex) {
        }
        output.print(stringResult);
        output.close();
    }

}




