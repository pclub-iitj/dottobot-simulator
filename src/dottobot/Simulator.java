package dottobot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.InputMismatchException;

public class Simulator {
	private int sizeOfBoard;
	private int matTheMain[][][];
	private int boxes[][];
	private String player1, player2;
	private String typeOfPlayer1, typeOfPlayer2;
	private File theMain;
	private int scoreOfPlayer1;
	private int scoreOfPlayer2;
	private String looser="NONE";
	private boolean stateOfGame=true;
	private String currentPlayer;
	private String tossWinner;
	private int linesLeft;
	private ProcessBuilder builder1, builder2;
	private InputReader reader1, reader2;
	private PrintWriter writer1, writer2;
	private DottoGUI gui;
	
	public void setPlayers(String file1, String file2){
		player1=file1;
		player2=file2;
	}
	public String getPlayer1(){
		return player1;
	}
	public String getPlayer2(){
		return player2;
	}
	public void setType(String type1, String type2){
		typeOfPlayer1=type1;
		typeOfPlayer2=type2;
	}
	public String getTypeOfPlayer1(){
		return typeOfPlayer1;
	}
	public String getTypeOfPlayer2(){
		return typeOfPlayer2;
	}
	public int getSize(){
		return sizeOfBoard;
	}
	
	public void setSize(int N){
		sizeOfBoard=N;
	}
	
	public void initGame() throws IOException{
		int toss=((int)(Math.random()*9999))%2;
		if(toss==0)tossWinner=getPlayer1();
		else tossWinner=getPlayer2();
		theMain=new File("theMain");
		matTheMain=new int[getSize()][getSize()][2];
		for(int i=0;i<getSize();i++){
			for(int j=0;j<getSize();j++){
				matTheMain[i][j][0]=0;
				matTheMain[i][j][1]=0;
			}
		}
		boxes=new int[getSize()-1][getSize()-1];
		for(int i=0;i<getSize()-1;i++){
			for(int j=0;j<getSize()-1;j++){
				boxes[i][j]=0;
				boxes[i][j]=0;
			}
		}
		initFile();
		int N=getSize();
		linesLeft=2*N*(N-1);
		builder1=new ProcessBuilder("player1.bat");//Complete it
		builder2=new ProcessBuilder("player2.bat");//Complete it
		gui=new DottoGUI(getSize());
	}
	public void initFile() throws IOException{
		reWriteTheMain();
	}
	public void switchPlayers(){
		String temp=player1;
		player1=player2;
		player2=temp;
		temp=typeOfPlayer1;
		typeOfPlayer1=typeOfPlayer2;
		typeOfPlayer2=temp;
	}
	public void startGame() throws IOException, InterruptedException{
		while(stateOfGame){
			/*if(tossWinner==player2){
				switchPlayers();
			}*/
			int ans[];
			boolean run=true;
			
			while(run&&stateOfGame)
			{
				currentPlayer=player1;
				ans=runPlayer1();
				run=updateTheMain(ans[0], ans[1], ans[2]);
				if(run==true){scoreOfPlayer1 +=noOfChangedBoxes;
				noOfChangedBoxes=0;
				}linesLeft--;
				if(linesLeft==0){stateOfGame=false;}
			}
			run=true;
			while(run&&stateOfGame){
				currentPlayer=player2;
				ans=runPlayer2();
				run=updateTheMain(ans[0],ans[1],ans[2]);
				if(run==true){scoreOfPlayer2 +=noOfChangedBoxes;
					noOfChangedBoxes=0;
					}
				linesLeft--;
				if(linesLeft==0){stateOfGame=false;}
			}
		}
	}
	
	public int[] runPlayer1() throws IOException, InterruptedException{
		int ans[]=new int[3];
		Process p = builder1.start();
		p.waitFor();
		//read code
		FileInputStream f=new FileInputStream("prnout.txt");
		reader1=new InputReader(f);
		ans[0]=reader1.readInt();
		ans[1]=reader1.readInt();
		ans[2]=reader1.readInt();
		return ans;
	}
	
	public int[] runPlayer2() throws IOException, InterruptedException{
		int ans[]=new int[3];
		Process p = builder1.start();
		p.waitFor();
		//read code
		FileInputStream f=new FileInputStream("prnout.txt");
		reader2=new InputReader(f);
		ans[0]=reader2.readInt();
		ans[1]=reader2.readInt();
		ans[2]=reader2.readInt();
		return ans;
		}
	/*handle tempering by rewriting theMain file again */
	public boolean updateTheMain(int x, int y, int dir) throws IOException{
		//Check if possible, and gives a true solution 
		//update Matrix first and then rewrite the file.
		boolean toReturn;
		try{
			if(matTheMain[x][y][dir]==0)
			matTheMain[x][y][dir]=1;
			else{System.out.println(currentPlayer+" looses");
			stateOfGame=false;
				throw new Exception();
			}
		}
		catch(Exception e){
			stateOfGame=false;
			looser=currentPlayer;
		}
		toReturn=updateBoxes(x,y,dir);
		reWriteTheMain();
		return toReturn;
	}
	
	public void reWriteTheMain() throws IOException{
		PrintWriter pw=new PrintWriter(new FileWriter("theMain"));
		for(int I=0;I<matTheMain.length;I++){
			for(int J=0;J<matTheMain.length;J++){
				for(int K=0;K<2;K++){
					pw.print(matTheMain[I][J][K]);
				}
				if(J!=matTheMain.length-1)
				pw.print(" ");
			}
			pw.println();
		}
		pw.close();
	}
	public void checkCollision(int x,int y){
		if(boxes[x][y]!=0)System.exit(1);
	}
	public boolean updateBoxes(int x, int y, int dir){
		boolean truth=false;
		if(dir==1)truth=checkHorizontalBoxes(x,y);
		if(dir==0)truth=checkVerticalBoxes(x,y);
		return truth;
	}
	int noOfChangedBoxes=0;
	//Check for line over writing
	public boolean checkHorizontalBoxes(int x, int y){
		boolean toReturn=false;
		boolean leftSideUp=false, leftSideDown=false, leftSideParallel=false;
		if(y>0){
			if(matTheMain[x][y-1][0]==1)leftSideUp=true;
		if(matTheMain[x][y-1][1]==1)leftSideParallel=true;
		if(matTheMain[x+1][y-1][0]==1)leftSideDown=true;
		if(leftSideUp&&leftSideDown&&leftSideParallel){
			//checkCollision(x,y-1);
			if(currentPlayer==player1){
				boxes[x][y-1]=1;
				gui.btns[x][y-1].setLabel(player1);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			else{
				boxes[x][y-1]=2;
				gui.btns[x][y-1].setLabel(player2);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			noOfChangedBoxes++;
		toReturn=true;
		}
		}
		if(y<getSize()-1){
		boolean rightSideUp=false, rightSideDown=false, rightSideParallel=false;
		if(matTheMain[x][y][0]==1)rightSideUp=true;
		if(matTheMain[x][y+1][1]==1)rightSideParallel=true;
		if(matTheMain[x+1][y][0]==1)rightSideDown=true;
		
		if(rightSideUp&&rightSideDown&&rightSideParallel){
			//checkCollision(x,y);
			if(currentPlayer==player1){
				boxes[x][y]=1;
			gui.btns[x][y].setLabel(player1);
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
			else{
				boxes[x][y]=2;
				gui.btns[x][y].setLabel(player2);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			noOfChangedBoxes++;
		toReturn=true;
		}
		}
		return toReturn;
	}
	
	public boolean checkVerticalBoxes(int x, int y){
		boolean toReturn=false;
		if(x>0){
		boolean upSideLeft=false, upSideRight=false, upSideParallel=false;
		if(matTheMain[x-1][y][1]==1)upSideLeft=true;
		if(matTheMain[x-1][y+1][1]==1)upSideRight=true;
		if(matTheMain[x-1][y][0]==1)upSideParallel=true;
		if(upSideParallel&&upSideLeft&&upSideRight){
			//checkCollision(x-1,y);
			if(currentPlayer==player1) {
				boxes[x-1][y]=1;
				gui.btns[x-1][y].setLabel(player1);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			else{
				boxes[x-1][y]=2;
				gui.btns[x-1][y].setLabel(player2);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			noOfChangedBoxes++;
			toReturn=true;
		}
		}
		if(x<getSize()-1){
		boolean downSideRight=false, downSideLeft=false, downSideParallel=false;
		if(matTheMain[x][y][1]==1)downSideLeft=true;
		if(matTheMain[x+1][y][0]==1)downSideParallel=true;
		if(matTheMain[x][y+1][1]==1)downSideRight=true;
		
		if(downSideLeft&&downSideRight&&downSideParallel){
			//checkCollision(x,y);
			if(currentPlayer==player1){
				boxes[x][y]=1;
				gui.btns[x][y].setLabel(player1);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				boxes[x][y]=2;
				gui.btns[x][y].setLabel(player2);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			noOfChangedBoxes++;
		toReturn=true;
		}}
		return toReturn;
	}

	public String endGameAndShowResult(){
		String result;
		if(looser=="NONE"){
			if(scoreOfPlayer1==scoreOfPlayer2){
				result="Tie";
			}
			else if(scoreOfPlayer1>scoreOfPlayer2){
				result=player1+" Wins";
			}
			else{
				result=player2 +"Wins";
			}
		}
		else{
			if(looser==player1)result=player2+" Wins";
			else result=player1+" Wins";
		}
		return result+" "+scoreOfPlayer1+" "+scoreOfPlayer2;
	}
	InputStream inputStream; OutputStream outputstream; InputReader in; OutputWriter out;
	public String process(InputStream input, OutputStream output, InputReader i, OutputWriter o) throws IOException, InterruptedException{
		this.inputStream=input;
		this.outputstream=output;
		this.in=i;
		this.out=o;
		int N=in.readInt();
		setSize(N);
		
		String typeOfFile1="run.bat";//in.readString();
		String file1=in.readString();
		String typeOfFile2="run.bat";//in.readString();
		String file2=in.readString();
		setPlayers(file1,file2);
		setType(typeOfFile1,typeOfFile2);
		initGame();
		startGame();
	String ans=	endGameAndShowResult();
	return ans;}
	
	public static void main(String s[]) throws IOException {
	Simulator sim=new Simulator();
		InputStream inputStream = System.in;
		OutputStream outputStream = System.out;
		InputReader in = new InputReader(inputStream);
		OutputWriter out = new OutputWriter(outputStream);
		try {
			out.printLine(sim.process(inputStream, outputStream, in, out));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
out.close();}

}

class InputReader {
	private InputStream stream;
	private byte[] buf = new byte[1024];
	private int curChar;
	private int numChars;
 
	public InputReader(InputStream stream) {
		this.stream = stream;
	}
 
	public int read() {
		if (numChars == -1)
			throw new InputMismatchException();
		if (curChar >= numChars) {
			curChar = 0;
			try {
				numChars = stream.read(buf);
			} catch (IOException e) {
				throw new InputMismatchException();
			}
			if (numChars <= 0)
				return -1;
		}
		return buf[curChar++];
	}
 
	public int readInt() {
		int c = read();
		while (isSpaceChar(c))
			c = read();
		int sgn = 1;
		if (c == '-') {
			sgn = -1;
			c = read();
		}
		int res = 0;
		do {
			if (c < '0' || c > '9')
				throw new InputMismatchException();
			res *= 10;
			res += c - '0';
			c = read();
		} while (!isSpaceChar(c));
		return res * sgn;
	}
	public long readLong() {
		int c = read();
		while (isSpaceChar(c))
			c = read();
		int sgn = 1;
		if (c == '-') {
			sgn = -1;
			c = read();
		}
		long res = 0;
		do {
			if (c < '0' || c > '9')
				throw new InputMismatchException();
			res *= 10;
			res += c - '0';
			c = read();
		} while (!isSpaceChar(c));
		return res * sgn;
	}
 
	public String readString() {
		int c = read();
		while (isSpaceChar(c))
			c = read();
		StringBuffer res = new StringBuffer();
		do {
			res.appendCodePoint(c);
			c = read();
		} while (!isSpaceChar(c));
		return res.toString();
	}
 
	public static boolean isSpaceChar(int c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == -1;
	}
 
	public String next() {
		return readString();
	}
}
 
class OutputWriter {
	private final PrintWriter writer;
 
	public OutputWriter(OutputStream outputStream) {
		writer = new PrintWriter(outputStream);
	}
 
	public OutputWriter(Writer writer) {
		this.writer = new PrintWriter(writer);
	}
 
	public void print(Object...objects) {
		for (int i = 0; i < objects.length; i++) {
			if (i != 0)
				writer.print(' ');
			writer.print(objects[i]);
		}
	}
 
	public void printLine(Object...objects) {
		print(objects);
		writer.println();
	}
 
	public void close() {
		writer.close();
	}
 
}