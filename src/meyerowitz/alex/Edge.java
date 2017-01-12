package meyerowitz.alex;

public class Edge implements Comparable<Edge>
{
	private Node mNodeA;
	public Node getNode1() { return mNodeA; }
	
	private Node mNodeB;
	public Node getNode2() { return mNodeB; }
	
	private double mWeight;
	public double getWeight() { return mWeight; }
	
	public Edge(Node nodeA, Node nodeB) {
		mNodeA = nodeA;
		mNodeB = nodeB;
		mWeight = calculateWeight(mNodeA, mNodeB);
	}
	
	private double calculateWeight(Node a, Node b) {	
		return Math.sqrt( (a.getX() - b.getX()) * (a.getX() - b.getX()) 
				+ (a.getY() - b.getY()) * (a.getY() - b.getY()) );
	}

	@Override
	public int compareTo(Edge edge) {
		return (this.getWeight()  < edge.getWeight() ? -1 : 
			   (this.getWeight() == edge.getWeight() ? 0 : 1));
	}
}
