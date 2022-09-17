/**
 * Sudoku.java
 * 
 * Implementation of a class that represents a Sudoku puzzle and solves
 * it using recursive backtracking.
 *
 * Computer Science 112, Boston University
 *
 * your name: Robert Parrish
 *
 */

import java.io.*;   // allows us to read from a file
import java.util.*;

public class Sudoku {    
    // The current contents of the cells of the puzzle. 
    private int[][] grid;
    
    /*
     * Indicates whether the value in a given cell is fixed 
     * (i.e., part of the initial configuration).
     * valIsFixed[r][c] is true if the value in the cell 
     * at row r, column c is fixed, and false otherwise.
     */
    private boolean[][] valIsFixed;
    
    /*
     * This 3-D array allows us to determine if a given subgrid (i.e.,
     * a given 3x3 region of the puzzle) already contains a given
     * value.  We use 2 indices to identify a given subgrid:
     *
     *    (0,0)   (0,1)   (0,2)
     *
     *    (1,0)   (1,1)   (1,2)
     * 
     *    (2,0)   (2,1)   (2,2)
     * 
     * For example, subgridHasVal[0][2][5] will be true if the subgrid
     * in the upper right-hand corner already has a 5 in it, and false
     * otherwise.
     */
    private boolean[][][] subgridHasVal;
    
    /*** ADD YOUR ADDITIONAL FIELDS HERE. ***/
    private boolean[][] rowHasVal; 
    private boolean[][] colHasVal; 

    
    /* 
     * Constructs a new Puzzle object, which initially
     * has all empty cells.
     */
    public Sudoku() {
        this.grid = new int[9][9];
        this.valIsFixed = new boolean[9][9];     
        
        /* 
         * Note that the third dimension of the following array is 10,
         * because we need to be able to use the possible values 
         * (1 through 9) as indices.
         */
        this.subgridHasVal = new boolean[3][3][10];        

        /*** INITIALIZE YOUR ADDITIONAL FIELDS HERE. ***/
        this.rowHasVal = new boolean[9][10];
        this.colHasVal = new boolean[9][10];
    }
    
    /*
     * Place the specified value in the cell with the specified
     * coordinates, and update the state of the puzzle accordingly.
     */
    public void placeVal(int val, int row, int col) {
        this.grid[row][col] = val;
        this.subgridHasVal[row/3][col/3][val] = true;
        
        /*** UPDATE YOUR ADDITIONAL FIELDS HERE. ***/

        this.rowHasVal[row][val] = true; 

        this.colHasVal[col][val] = true;
    
    }
        
    /*
     * remove the specified value from the cell with the specified
     * coordinates, and update the state of the puzzle accordingly.
     */
    public void removeVal(int val, int row, int col) {
        this.grid[row][col] = 0;
        this.subgridHasVal[row/3][col/3][val] = false;
        
        /*** UPDATE YOUR ADDITIONAL FIELDS HERE. ***/

        this.rowHasVal[row][val] = false; 

        this.colHasVal[col][val] = false;
    }  
        
    /*
     * read in the initial configuration of the puzzle from the specified 
     * Scanner, and use that config to initialize the state of the puzzle.  
     * The configuration should consist of one line for each row, with the
     * values in the row specified as integers separated by spaces.
     * A value of 0 should be used to indicate an empty cell.
     * 
     * You should not change this method.
     */
    public void readConfig(Scanner input) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int val = input.nextInt();
                this.placeVal(val, r, c);
                if (val != 0) {
                    this.valIsFixed[r][c] = true;
                }
            }
            input.nextLine();
        }
    }
                
    /*
     * Displays the current state of the puzzle.
     * You should not change this method.
     */        
    public void printGrid() {
        for (int r = 0; r < 9; r++) {
            this.printRowSeparator();
            for (int c = 0; c < 9; c++) {
                System.out.print("|");
                if (this.grid[r][c] == 0) {
                    System.out.print("   ");
                } else {
                    System.out.print(" " + this.grid[r][c] + " ");
                }
            }
            System.out.println("|");
        }
        this.printRowSeparator();
    }
        
    // A private helper method used by display() 
    // to print a line separating two rows of the puzzle.
    private static void printRowSeparator() {
        for (int i = 0; i < 9; i++) {
            System.out.print("----");
        }
        System.out.println("-");
    }
    
    /*** ADD ANY ADDITIONAL METHODS HERE. ***/

    // returns true if it is safe for val to be placed in position (row, col) 
    private boolean isSafe(int row, int col, int val){
        //if any of the following are true, then you cannot place a value there
        //if all of the following are false, then you can
        return !(this.subgridHasVal[row/3][col/3][val] || this.rowHasVal[row][val] 
                || this.colHasVal[col][val] || this.valIsFixed[row][col]);
    }

    public void test() {
        System.out.println(this.isSafe(0, 0, 9)); 
        System.out.println(this.isSafe(0, 0, 7));
        System.out.println(this.isSafe(0, 1, 2));
        System.out.println(this.isSafe(0, 2, 2));
        System.out.println(this.isSafe(0, 3, 2));
        // add other similar lines here
    }
         
    /*
     * This is the key recursive-backtracking method.  Returns true if
     * a solution has already been found, and false otherwise.
     * 
     * Each invocation of the method is responsible for finding the
     * value of a single cell of the puzzle. The parameter n
     * is the number of the cell that a given invocation of the method
     * is responsible for. We recommend that you consider the cells
     * one row at a time, from top to bottom and left to right,
     * which means that they would be numbered as follows:
     *
     *     0  1  2  3  4  5  6  7  8
     *     9 10 11 12 13 14 15 16 17
     *    18 ...
     *    27
     *    36
     *    45
     *    54
     *    63
     *    72 73 74 75 76 77 78 79 80
     */
    private boolean solveRB(int n) {
        
        int row = n / 9; //integer division gives 0 if n < 9, 1 if 9 <= n < 18, 2 if 18 <= n < 27
        //System.out.println(row);
        int col = n % 9; // modulus gives the distance from a multiple of 9 (ie distance from column 0) 
        //System.out.println(col);
        int[] vals = {1, 2, 3, 4, 5, 6, 7, 8, 9};    
        /* 
         * The following return statement allows the initial code to
         * compile.  Replace it with your full implementation of the
         * recursive-backtracking method.
         */
        System.out.println("n = " + n);

        if (n == 81) //this means we have passed the final position. 
            return true; //If we get here, we solved the puzzle! 
        
        if (valIsFixed[row][col]){
            return solveRB(n+1);
        }

        for (int valIndex = 0; valIndex < vals.length; valIndex++){
            int val = vals[valIndex];

            //System.out.println(this.isSafe(row, col, val));
            if (this.isSafe(row, col, val)){ 
                this.placeVal(val, row, col);

                //this.printGrid();
            

                // Move onto the next cell n+1 by making a recursive call.
                // If the call returns true, we've already found the solution,
                // so we just keep returning true.
                //System.out.println(this.solveRB(n+1));
                if (this.solveRB(n+1) == true){
                    return true;
                }

                // Otherwise, remove the value from that 
                // cell and continue in the loop onto the next val to 
                // see if that works in cell n 
                System.out.println("backtrack");
                this.removeVal(val, row, col);

            }

            

        }//for 

        // in this case, backtrack because you can't place any val in position n  
        System.out.println("ah");
        return false; 

    }//solveRB
    
    /*
     * public "wrapper" method for solveRB().
     * Makes the initial call to solveRB, and returns whatever it returns.
     */
    public boolean solve() { 
        boolean foundSol = this.solveRB(0);
        return foundSol;
    }
    
    public static void main(String[] args) {

        
        
        
        Scanner scan = new Scanner(System.in);
        Sudoku puzzle = new Sudoku();
        
        System.out.print("Enter the name of the puzzle file: ");
        String filename = scan.nextLine();
        
        try {
            Scanner input = new Scanner(new File(filename));
            puzzle.readConfig(input);
        } catch (IOException e) {
            System.out.println("error accessing file " + filename);
            System.out.println(e);
            System.exit(1);
        }
        
        System.out.println();
        System.out.println("Here is the initial puzzle: ");
        puzzle.printGrid();
        System.out.println();
        
        puzzle.test();

        
        if (puzzle.solve()) {
            System.out.println("Here is the solution: ");
        } else {
            System.out.println("No solution could be found.");
            System.out.println("Here is the current state of the puzzle:");
        }
        puzzle.printGrid();  
        
        
        
    } 

}