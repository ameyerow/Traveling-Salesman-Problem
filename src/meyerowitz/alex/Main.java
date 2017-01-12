package meyerowitz.alex;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Main extends JPanel {
	private static final long serialVersionUID = 1L;

	private int numNodes = 10;
	private Node[] mNodes;
	private ArrayList<Node[]> paths;
	private Node[] optimalPath;
	private ArrayList<Node> nearestNeighborPath;
	private ArrayList<Edge> mEdges;
	private ArrayList<Edge> greedyPath;
	
	public Main() {
		mNodes = new Node[numNodes];
		mEdges = new ArrayList<Edge>();
		
		for(int i = 0; i < mNodes.length; i++)
			mNodes[i] = new Node(i);
		
		for(Node a: mNodes) {
			for(Node b: mNodes) {
				if(a.getId() != b.getId()) {
					boolean c = false;
					for(Edge edge: mEdges) {
						if(edge.getNode1().getId() == b.getId() && 
								edge.getNode2().getId() == a.getId()) {
							c = true;
						}
					}	
					if(!c) {
						mEdges.add(new Edge(a, b));
					} 
				}
			}
		}
		
		optimalSolution(mNodes);
		nearestNeighborHeuristic(mNodes);
		greedyHeuristic(mNodes, mEdges);
		
		Runnable runnable = new Runnable(){
			@Override
			public void run() {
				repaint();	
			}	
		};
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(runnable, 0, 33, TimeUnit.MILLISECONDS);
		
		greedyHeuristic(mNodes, mEdges);
	}
	
	private void optimalSolution(Node[] nodes) {
		long startTime = System.currentTimeMillis();
		int shortestPath = Integer.MAX_VALUE;
		paths = new ArrayList<Node[]>();
		
		permute(nodes, 0);
		
		for(Node[] a: paths) {
			int length = 0;
			
			for(int i = 0; i < a.length - 1; i++) {
				length += (int)calculateDistance(a[i], a[i+1]);
			}
			
			if(length < shortestPath) {
				shortestPath = length;
				optimalPath = a;
			
			}
		}
	
		long endTime = System.currentTimeMillis();
		
		System.out.println("Optimal Solution Length: " + shortestPath);
		System.out.println("Optimal Solution Compute Time: " + (endTime - startTime));
	}
	
	private void permute(Node[] a, int k) {
        if (k == a.length){
            for (int i = 0; i < a.length; i++) {
                paths.add(a);
            }
        } else {
            for (int i = k; i < a.length; i++) {
                Node temp = a[k];
                a[k] = a[i];
                a[i] = temp;
 
                permute(a, k + 1);
 
                temp = a[k];
                a[k] = a[i];
                a[i] = temp;
            }
        }
    }
	
	private void nearestNeighborHeuristic(Node[] nodes) {
		long startTime = System.currentTimeMillis();
		
		ArrayList<Node> path = new ArrayList<Node>();
		int length = 0;
		nodes[0].setConnected(true);
		path.add(nodes[0]);
			
		for(int i = 0; i < nodes.length -1; i++) {
			int minDist = Integer.MAX_VALUE;
			Node next = null;
			for(Node a: nodes) {
				if(!a.getConnected()) {
					int dist = (int) calculateDistance(path.get(path.size() - 1), a);
						
					if(dist < minDist) {
						next = a;
					}
				}
			}
			next.setConnected(true);
			path.add(next);
		}
		
		nearestNeighborPath = path;
		
		for(int i = 0; i < path.size() - 1; i++) {
			length += (int)calculateDistance(path.get(i), path.get(i+1));
		}
		long endTime = System.currentTimeMillis();			
		System.out.println("Nearest Neighbor Solution Length: " + length);
		System.out.println("Nearest Neighbor Solution Compute Time: " + (endTime - startTime));
	}
	
	private void greedyHeuristic(Node[] nodes, ArrayList<Edge> edges) {
		long startTime = System.currentTimeMillis();
		ArrayList<Edge> path = new ArrayList<Edge>();
		ArrayList<Node> nodeSequence = new ArrayList<Node>();
		Collections.sort(edges);

		for(Edge edge: edges) {
			int a = 0;
			int b = 0;
			
			for(int i = 0; i < nodes.length; i++) {
				if(edge.getNode1().getId() == nodes[i].getId()) {
					a = i;
				}
				if(edge.getNode2().getId() == nodes[i].getId()) {
					b = i;
				}
			}
			
			if(nodes[a].getDegree() < 2 && nodes[b].getDegree() < 2) {
				nodeSequence.add(nodes[b]);
				
				boolean cycle = false;
				
				loop: for(int i = 0; i < numNodes; i++) {
					int d = 0;
					for(Node node: nodeSequence) {
						if(node.getId() == i) {
							d++;
						}
						
						if(d == 2) {
							cycle = true;
							nodeSequence.remove(nodeSequence.size() - 1);
							break loop;
						}
					}
				}
				
				if(!cycle) {
					path.add(edge);
					nodes[a].addDegree();
					nodes[b].addDegree();
					greedyPath = path;
				}
			}	
		}
		
		int length = 0;
		
		for(int i = 0; i < path.size() - 1; i++) {
			length += path.get(i).getWeight();
		}
		
		greedyPath = path;
		
		long endTime = System.currentTimeMillis();
		System.out.println("Greedy Solution Length: " + length);
		System.out.println("Greedy Solution Compute Time: " + (endTime - startTime));
	}
	
	private double calculateDistance(Node a, Node b) {	
		return Math.sqrt( (a.getX() - b.getX()) * (a.getX() - b.getX()) 
				+ (a.getY() - b.getY()) * (a.getY() - b.getY()) );
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, this.WIDTH, this.HEIGHT);
		g2d.setColor(Color.RED);
		
		for(Node node: mNodes)
			g2d.fillOval(node.getX(), node.getY(), 10, 10);
		
		/*g2d.setColor(Color.BLUE);
		for(int i = 0; i < optimalPath.length - 1; i++) {
			g2d.drawLine(optimalPath[i].getX(), optimalPath[i].getY(),
					optimalPath[i+1].getX(), optimalPath[i+1].getY());
		}
		
		g2d.setColor(Color.RED);
		for(int i = 0; i < nearestNeighborPath.size() - 1; i++) {
			g2d.drawLine(nearestNeighborPath.get(i).getX(), nearestNeighborPath.get(i).getY(),
					nearestNeighborPath.get(i+1).getX(), nearestNeighborPath.get(i+1).getY());
		}*/
		
		g2d.setColor(Color.BLACK);
		for(int i = 0; i < greedyPath.size(); i++) {
			g2d.drawLine(greedyPath.get(i).getNode1().getX(), greedyPath.get(i).getNode1().getY(), 
					greedyPath.get(i).getNode2().getX(), greedyPath.get(i).getNode2().getY());	
		}
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("TSP");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.setResizable(false);
		frame.add(new Main());
		frame.setLocationRelativeTo(null);
		frame.setVisible(true); 
	}
}
