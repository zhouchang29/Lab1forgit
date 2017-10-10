package se_lab1;

/** 一个路径图辅助类用于查询当前图中两个点之间的边应该是什么颜色
 * @author bubbl
 *
 */
public class PathGraphAssist {
	
	TreeNodeList<TreeNode> AllNodes;
	PathNodeList<PathNode> AllPaths;
	
	@SuppressWarnings("unchecked")
	public PathGraphAssist(TreeNodeList<TreeNode> TreeNodes) {
		this.AllNodes = (TreeNodeList<TreeNode>) TreeNodes.clone();
	}
	
	public int QueryNodeToNode(TreeNode node1,TreeNode node2) {
		int state = -2;
		// 查询两个点的状态 s-2 无边桥接 ；s-1 有边桥接但不在路径上； s0在某一条路径上(返回索引值)
		//			  s-int max 在多条路径上
		for(int i=0;i<node1.childList.size();i++) {
			if(node1.childList.get(i).equals(node2)) {
				state = -1;
				break;
			}
		}
		if(state == -1) {
			for(int i = 0;i<AllPaths.size();i++) {
				for(int j = 0;j<AllPaths.get(i).path.size()-1;j++) {
					if(AllPaths.get(i).path.get(j).equals(node1)
							&& AllPaths.get(i).path.get(j+1).equals(node2)) {
						if(state == -1) {
							state = i;
						}
						else {
							state = Integer.MAX_VALUE;
						}
					}
				}
			}
		}
		
		return state;
	}
}
