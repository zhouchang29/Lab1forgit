/**
 * ！！使用前须知！！ 本程序需要提前安装好Graphviz软件，并将程序所在安装目录下的bin文件夹加入到PATH环境变量中
 *  后才能正常使用！！
 */
package se_lab1;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

import javax.swing.*;

public class Lab1 extends JComponent {
	private static final long serialVersionUID = -4654513992552014113L;
	public static MyFrame f;
	public static String fileUrl;
	public static String[] words;
	public static Tree t;
	public static int imgState = 0;

	/** 从文本文档(txt)中读取单词
	 *
	 */
	public static void readInFile(){
		File file = new File(fileUrl);
		String wordsStr = "";
		Scanner in;
		try {
			in = new Scanner(file);
			while(in.hasNextLine()){
				String str = in.nextLine();
				wordsStr = wordsStr.concat(replaceStr(str)+" ");
			}
			words = wordSplit(wordsStr);
			t = new Tree(words);
			DirectedGraph.createDirectedGraph(t, fileUrl, "Verdana", 12);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Lab1() {
		setBackground(Color.WHITE);
	}
	
	/** 切割字符串
	 * @param str 读入的字符串
	 * @return 切割后的String[]
	 */
	public static String[] wordSplit(String str) {
		return str.split("\\s+");
	}
	
	/** 处理读入的字符串(删除标点符号，并转换为小写)
	 * @param str 读入的字符串
	 * @return 处理后的字符串
	 */
	public static String replaceStr(String str){
		return str.replaceAll("[^a-zA-Z]", " ").toLowerCase();
	}
	
	public static void main(String[] args) {
		f = new MyFrame();
	}
}