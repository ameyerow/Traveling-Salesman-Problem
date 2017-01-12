package meyerowitz.alex;

public class Edge implements Comparable<Edge>
{
	private Node a;
	public Node getA() { return a; }
	
	private Node b;
	public Node getB() { return b; }
	
	private double weight;
	public double getWeight() { return weight; }
	
	private double id;
	public double getId() { return id;}
	
	public Edge(Node a, Node b) {
		this.a = a;
		this.b = b;
		weight = calculateWeight(a, b);
		id = Math.random();
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
