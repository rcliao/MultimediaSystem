package models;

/**
 * Huffman Tree
 */
public class Tree implements Comparable<Tree> {
	/**
	 * Pointer to the root
	 */
	Node root;
	
	/**
	 * Constructor to combine two leaf(tree) into one tree
	 * 
	 * @param  t1 Tree 1
	 * @param  t2 Tree 2
	 */
	public Tree (Tree t1, Tree t2) {
		root = new Node();
		root.left = t1.root;
		root.right = t2.root;
		root.weight = t1.root.weight + t2.root.weight;
	}
	
	/**
	 * Given the frequency of the character build a singleton tree
	 * 
	 * @param  weight  The frequency of the character
	 * @param  element The character
	 */
	public Tree (int weight, char element) {
		root = new Node(weight, element);
	}


	public Node getRoot() {
		return root;
	}
	 
	public void setRoot(Node root) {
		this.root = root;
	}
	
	/**
	 * Unimplement the comparable, compare the frequency
	 * 
	 * @param  o Other tree
	 * @return   If the tree is bigger than the other tree
	 */
	public int compareTo(Tree o) {
		if (root.weight < o.root.weight)
			return 1;
		else if (root.weight == o.root.weight)
			return 0;
		else
			return -1;
	}
	
	/**
	 * Helper class for Huffman tree
	 */
	public class Node {
		char element;
		int weight;
		Node left;
		Node right;
		String code = "";
		
		public Node() {
			
		}
		
		public Node (int weight, char element) {
			this.weight = weight;
			this.element = element;
		}
	}


}