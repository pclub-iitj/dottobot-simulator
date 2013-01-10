package dottobot;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DottoGUI extends JFrame{
	
	int size;
	JPanel grid;
	JButton btns[][];
	
	DottoGUI(int size){
		super("DottoBot");
		this.size=size;
		setSize(500,500);
		setElements();
		setVisible(true);
	}
	
	void setElements(){
		grid=new JPanel();
		grid.setLayout(new GridLayout(size-1,size-1));
		btns = new JButton[size-1][size-1];
		for(int i=0;i<size-1;i++){
			for(int j=0;j<size-1;j++){
					btns[i][j]=new JButton();
					grid.add(btns[i][j]);
			}
		}
		add(grid);
	}
}
