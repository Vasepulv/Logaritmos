package base;
import base.StringGen;
import base.Algorithm;

import java.io.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.Math;
import java.nio.*;

public class SubDivisionSolver{
	// Size of integers in bytes
	static final int INT_SIZE = Integer.SIZE/8;

	String currentDir =  System.getProperty("user.dir");
	String filesDir = currentDir + "/../files";
	String fileNameSup = "fileSup.bin";
	String fileNameLeft = "fileLeft.bin";

	// RAM and block sizes
	int M;
	int B;

	// Number of rows and columns for the total array
	int cols;
	int rows;

	// number of characters read for x and y
	int readX = 0;
	int readY = 0;

	// Side length in bytes
	int sideSize;

	// length of the current column in bytes is initially the side size
	int hSize;
	// The number of rows read in a block is the number of integers in sideSize
	int vLength;

	base.StringGen X;
	base.StringGen Y;

	FileInputStream fisTop;
	FileInputStream fisLeft;

	FileOutputStream fosTop;
	FileOutputStream fosLeft;

	BufferedInputStream biTop;
	BufferedInputStream biLeft;

	BufferedOutputStream boTop;
	BufferedOutputStream boLeft;


	int ans = 0; // The down right value of the latest solution



	/**
	* Inicializador para la clase
	* de la izquierda al bloque actual.
	* @param M Tamaño de la memoria RAM.
	* @param B Tamaño de bloque.
	* @param X Generador de string horizontal
	* @param Y Generador de string vertical
	* @param cols Length of the X string
	* @param rows Length of the Y string
	*/
	public SubDivisionSolver(int M, int B, base.StringGen X, base.StringGen Y,
		int cols, int rows){

		this.M = M;
		this.B = B;

		this.X = X;
		this.Y = Y;

		this.cols = cols;
		this.rows = rows;

		this.sideSize = floorSqrt(this.M);

		// length of the current column in bytes is initially the side size
		this.hSize = this.sideSize;
		// The number of rows read is the number of ints in the side size
		this.vLength = this.sideSize/INT_SIZE;

		// The total bytes on the column read must be a block multiple
		this.hSize -= this.hSize % this.B;

		// The number of read rows must be an integer
		this.vLength -= Math.floor(this.vLength);

		try { // Write the initial files for both used files and open output buffers
			// Init top row
		    this.fosTop = new FileOutputStream(this.filesDir + "\\" + this.fileNameSup);
		    this.boTop = new BufferedOutputStream(this.fosTop, this.B);

		    for (int i=0; i < this.cols; i++)
		    	this.boTop.write(toBytes(i), 0, INT_SIZE);


		    // Init left col
		    this.fosLeft = new FileOutputStream(this.filesDir + "\\" + this.fileNameLeft);
		    this.boLeft = new BufferedOutputStream(this.boLeft);

		    for (int i=0; i < this.vLength; i++)
		    	this.boLeft.write(toBytes(i), 0, INT_SIZE);

		} catch (IOException e) {
		    e.printStackTrace();
		}

		// Open input buffers
		try {

			this.fisTop = new FileInputStream(this.filesDir + "\\" + this.fileNameSup);
			this.biTop = new BufferedInputStream(this.fisTop, this.B);
			this.biTop.mark(0);

			this.fisLeft = new FileInputStream(this.filesDir + "\\" + this.fileNameLeft);
			this.biLeft = new BufferedInputStream(this.fisLeft, this.B);
			this.biLeft.mark(0);

		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

	// Returns floor of square root of x 
	static int floorSqrt(int x) 
	{ 
	    // Base cases 
	    if (x == 0 || x == 1) 
	        return x; 
	
	    // Staring from 1, try all numbers until 
	    // i*i is greater than or equal to x. 
	    int i = 1, result = 1; 
	      
	    while (result <= x) { 
	        i++; 
	        result = i * i; 
	    } 

	    return i-1;
	}
	   


	static byte[] toBytes(int i)
	{
	/**
	* Converts an integer to byte array
	* @param i Integer to be converted.
	* @return the byte array.
	*/
	  byte[] result = new byte[4];

	  result[0] = (byte) (i >> 24);
	  result[1] = (byte) (i >> 16);
	  result[2] = (byte) (i >> 8);
	  result[3] = (byte) (i /*>> 0*/);

	  return result;
	}

	static int byteArrayToInt(byte[] bytes)
	/**
	* Converts a byte array to integer
	* @param bytes Byte array with the integer.
	* @return The integer in the byte array.
	*/
	{
	    return ByteBuffer.wrap(bytes).getInt();
	}

	int solve(){
		/**
		* Solves the problem and returns the solution
		*/
		while(this.readY < this.rows)
		{
			int rowsRead = solveBlock(); // Será igual para toda los bloques de una fila
			// Si ya terminó la fila de bloques
			if(this.readX == cols){
				try{
					this.biTop.reset(); // Resets the top input
					this.boTop = new BufferedOutputStream(this.fosTop, this.B); // Resets the top output
				} catch (IOException e) {
				    e.printStackTrace();
				}
				this.readX = 0; // Resets the col count
				this.readY += rowsRead; // updates the read rows.
			}
		}
		return this.ans;
	}


	int solveBlock(){
		/**
		* Overwrites the left and top files, and returns the read rows
		*/
		// Top and left loaded sections
		ArrayList<Integer> top = new ArrayList<Integer>();
		ArrayList<Integer> left = new ArrayList<Integer>();

		// x and y loaded sections. len(x) +  readX can't go over cols
		String x = X.getSubString(this.readX, this.readX + Math.round(this.hSize/INT_SIZE));
		String y = Y.getSubString(this.readY, this.readY + this.vLength);

		// resets the left input buffer and writer
		try{
			this.biLeft.reset();
			this.boLeft = new BufferedOutputStream(this.fosTop, this.B);
		} catch (IOException e) {
		    e.printStackTrace();
		}


		int hSize = this.hSize;
		int vLength = this.vLength;

		if(x.length() + this.readX >= cols ){ // Right border
			// The number of read blocks is updated
			hSize = (cols - this.readX) * INT_SIZE;
			x = x.substring(0, hSize/INT_SIZE - 1);
		}
		if(y.length() +  this.readY >= rows){ // Lower border
			// The number of read rows is updated
			vLength = rows - this.readY;
			y = y.substring(0, vLength - 1);
		}

		int readSize;
		byte[] b = new byte[4]; // Read bytes

		try
		{
			// Read the top row
			readSize = 0;
			while(readSize < hSize){
				this.biTop.read(b, 0, INT_SIZE);
				top.add(byteArrayToInt(b));
				readSize += INT_SIZE;
			}

			// read the left semi column
			readSize = 0;
			while(readSize < vLength * INT_SIZE){
				this.biLeft.read(b, 0, INT_SIZE);
				left.add(byteArrayToInt(b));
				readSize += INT_SIZE;
			}
		} catch (IOException e) {
		    e.printStackTrace();
		}
		// solve the problem
		ArrayList<ArrayList<Integer>> sol = base.Algorithm.solucionar(left, top, x, y);
		top = sol.get(0);
		left = sol.get(1);

		int writtenSize;
		int toWrite = 0;
		try
		{
			// overwrite the top row
			writtenSize = 0;
			while(writtenSize < hSize){
				toWrite = top.get(0);
				top.remove(0); // Consumes the top array
				this.boTop.write(toBytes(toWrite), 0, INT_SIZE);
				writtenSize += INT_SIZE;
			}


			// overwrite the left column
			writtenSize = 0;
			while(writtenSize < vLength * INT_SIZE){
				toWrite = left.get(0);
				left.remove(0); // Consumes the top array
				this.boLeft.write(toBytes(toWrite), 0, INT_SIZE);
				writtenSize += INT_SIZE;
			}

			this.ans = toWrite; // The last written is the down right section
		} catch (IOException e) {
		    e.printStackTrace();
		}

		// update the read values
		this.readX += hSize/INT_SIZE;

		return vLength;
	}
}