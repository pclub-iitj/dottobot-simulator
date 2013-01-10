package dottobot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class SampleBot1 {
	public static void main(String s[]) throws IOException {
		InputStream inputStream = new FileInputStream("C:/Users/Sankha/Documents/programming club/Nimble/bin/theMain");
		InputReader in = new InputReader(inputStream);
		int size=5;
		int mat[][][]=new int[size][size][2];
		for(int I=0;I<size;I++){
			for(int J=0;J<size;J++){
				int x=in.readInt();
				if(x==00){
					mat[I][J][0]=0;
					mat[I][J][1]=0;
				}
				else if(x==10){
					mat[I][J][0]=1;
					mat[I][J][1]=0;
				}
				else if(x==11){
					mat[I][J][0]=1;
					mat[I][J][1]=1;
				}
				else if(x==01){
					mat[I][J][0]=0;
					mat[I][J][1]=1;
				}
			}
		}
		boolean breaker=true;
		int ans[]=new int[3];
		for(int I=0;I<size;I++){
			for(int J=0;J<size;J++){
				if(J<size-1&&mat[I][J][0]==0){
					//if(J<size-1){
					ans[0]=I;
					ans[1]=J;
					ans[2]=0;
					breaker=false;
				}
				else if(I<size-1&&mat[I][J][1]==0){
					ans[0]=I;
					ans[1]=J;
					ans[2]=1;
					breaker=false;
				}
				if(!breaker){break;}
			}if(!breaker){
				break;
			}
		}
		System.out.println(ans[0]+" "+ans[1]+" "+ans[2]);
	}
	
}
