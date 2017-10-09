/**
 * 
 */
package se_lab1;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.*;

public class Lab1 extends JComponent {
	private static final long serialVersionUID = -4654513992552014113L;
	public static MyFrame f;
	public static String fileUrl;
	public static String[] words;
	public static Tree t;
	public static int imgState = 0;
	
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
	 * Add changes for B1 branch
	 * @param str 读入的字符串
	 * @return 处理后的字符串
	 */
	public static String replaceStr(String str){
		return str.replaceAll("[^a-zA-Z]", " ").toLowerCase();
	}
	
	public static void main(String[] args) {
		f = new MyFrame();
	}
	
	public static Tree createDirectedGraph(String[] words) {
		Tree t = new Tree(words);
		return t;
	}
	
	public static void showDirectedGraph(Tree t) {
		// showDirectedGraph方法由于涉及到GUI内容
		// 所以不便在主程序类中进行定义
		// 由按键进行事件监听，获得生成的地址path后，传给MyFrame类的子类picDisplayPanel
		// 子类picDisplayPanel 调用setPic方法对GUI层进行repaint显示层
	}
	
	public static String queryBridgeWords(Tree t,String word1,String word2) {
		String res = "";
		TreeNodeList<TreeNode> retNodes = t.calculateBridge(word1,word2);
		if(retNodes == null) {
			// No word1 or word2 in the graph!
			res += "No "+word1+" or "+word2+" in the graph!";
		}
		else if(retNodes.size() == 0) {
			// No bridge words from word1 to word2!
			res += "No bridge words from "+word1+" to "+word2+"!";
		}
		else {
			if(retNodes.size() == 1) {
				// The bridge word from word1 to word2 is: .
				res += "The bridge word from "+word1+" to "+word2+" is: "+retNodes.get(0).getWord()+".";
			} 
			else {
				// The bridge words from word1 to word2 are: xxx, xxx, and xxx.
				res += "The bridge words from "+word1+" to "+word2+" are:";
				for(int i = 0;i<retNodes.size()-1;i++) {
					res += " " + retNodes.get(i).getWord() + ",";
				}
				res += "and" + " " + retNodes.get(retNodes.size()-1).getWord() + ".";
			}
		}
		return res;
	}
	
	public static String generateNewText(Tree t,String inputText) {
		String res = "",word1,word2;
		String wordsStr = replaceStr(inputText);
		String[] words = wordSplit(wordsStr);
		TreeNodeList<TreeNode> retNodes;
		for(int i = 0;i<words.length-1;i++) {
			word1 = words[i];
			word2 = words[i+1];
			res += word1 + " ";
			retNodes = t.calculateBridge(word1,word2);
			if(retNodes != null && retNodes.size() != 0) {
				if(retNodes.size() == 1) {
					res += "[" + retNodes.get(0).getWord() + "] "; 
				}
				else {
					Random random = new Random();
					int s = random.nextInt(retNodes.size()-1);
					res += "[" + retNodes.get(s).getWord() + "] ";
				}
			}
		}
		res += words[words.length-1];
		return res;
	}

	public static String calcShortestPath(Tree t,String word1, String word2,PathGraphAssist pga) throws CloneNotSupportedException {
		String res = "";
		TreeNode startNode = t.TreeNodes.nodeCheck(word1),
				endNode = t.TreeNodes.nodeCheck(word2);
		// 迭代遍历的路径点列表
		PathNodeList<PathNode> findPaths = new PathNodeList<PathNode>();
		// 确认的路径列表
		PathNodeList<PathNode> certainPaths = new PathNodeList<PathNode>();
		// 添加初始节点
		findPaths.add(new PathNode(startNode));
		// 进行迭代获得确认路径列表
		while(findPaths.size() != 0) {
			PathNode popPathNode = findPaths.pop();
			TreeNode presentNode = popPathNode.presentNode;
			// 开始迭代克隆子节点路径节点
			for(int i = 0;i < presentNode.childList.size();i++) {
				// 获取当前路径分支的节点
				TreeNode childNode = presentNode.childList.get(i);
				// 确认当前已经遍历路径节点里无该节点
				if(popPathNode.path.nodeCheck(childNode.getWord()) == null) {
					// 对分支进行深度克隆（包括实例变量路径记录节点列表的克隆）
					PathNode branchNode = (PathNode) popPathNode.clone();
					// 获取当前分支需要加的权值数
					int bridgeWeightValue = presentNode.getWeightOfNode(childNode);
					
					// 修改克隆分支来创建下一层分支
					branchNode.pathLength += bridgeWeightValue;
					branchNode.path.add(childNode);
					branchNode.presentNode = childNode;
					
					// 对当前路径分支进行判断是否已经到目标节点
					if(childNode.equals(endNode)) {
						// 结束则添加到确认路径列表中
						certainPaths.push(branchNode);
					}
					else {
						// 添加到迭代列表前先根据已有确认路径节点权值进行爬山法筛选
						if(certainPaths.size() != 0) {
							PathNode path = certainPaths.getShortestPath();
							if(path.pathLength > branchNode.pathLength) {
								// 当前分支已有的权值数小于已知最短路径才进行迭代
								findPaths.push(branchNode);
							}
						}
						else {
							// 继续添加到需要迭代的路径列表
							findPaths.push(branchNode);
						}
					}
				}
			}
		}
		
		// 迭代完毕进行路径检查和输出
		if(certainPaths.size() != 0) {
			// 即有确认的路径(可能不只一条)
			for(int i = 0;i<certainPaths.size();i++) {
				res += "Path " + i + " :";
				TreeNodeList<TreeNode> path = certainPaths.get(i).path;
				for(int j = 0;j<path.size() - 1;j++) {
					res += path.get(j).getWord() + "->";
				}
				res += endNode.getWord() + ".\n";
			}
		}
		else {
			// 不存在的
			res += "There's no path from "+word1+" to "+word2+".";
		}
		pga.AllPaths = certainPaths;
		return res;
	}

	public static String randomWalk(Tree t) {
		String ret = "";
		Random random = new Random();
		int randomNodeIndex;
		if(t.TreeNodes.size() == 1) {
			randomNodeIndex = 0;
		} else {
			randomNodeIndex = random.nextInt(t.TreeNodes.size()-1);
		}
		TreeNode startNode = t.TreeNodes.get(randomNodeIndex),walkNode;
		walkNode = startNode;
		TreeNodeList<TreeNode> walkNodes = new TreeNodeList<TreeNode>();
		walkNodes.add(walkNode);
		while(walkNode.childList.size() != 0) {
			boolean endState = false;
			ret += walkNode.getWord() + " ";
			if(walkNode.childList.size() == 1) {
				randomNodeIndex = 0;
			}
			else {
				randomNodeIndex = random.nextInt(walkNode.childList.size()-1);
			}
			TreeNode nextNode = walkNode.childList.get(randomNodeIndex);
			ArrayList<Integer> multiIndex = walkNodes.multiIndexOf(walkNode);
			for(int i = 0;i<multiIndex.size()-1;i++) {
				int index = multiIndex.get(i).intValue();
				if(walkNodes.get(index+1).equals(nextNode)) {
					// 检测到相同边，进行循环跳出
					endState = true;
					break;
				}
			}
			if(endState) {
				ret += nextNode.getWord();
				break;
			}
			else {
				walkNode = nextNode;
				walkNodes.add(walkNode);
			}
		}
		return ret;
	}
	
}