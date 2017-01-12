package meyerowitz.alex;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

public class Main extends JPanel {
	private static final long serialVersionUID = 1L;

	private int numNodes = 8;
	private Node[] mNodes;
	private ArrayList<Node[]> paths;
	private Node[] optimalPath;
	private ArrayList<Node> nearestNeighborPath;
	private ArrayList<Edge> mEdges;
	private ArrayList<Edge> greedyPath;
	private JButton button;
	private JTextPane text;
	private int index;
	
	public Main() {
		index = 0;
		
		button = new JButton("switch heuristic");
		button.setVisible(true);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				index = (index + 1)%3;
				updateText();
			}
		});
		
		text = new JTextPane();
		text.setEditable(false);
		updateText();
		
		this.add(button);
		this.add(text);
		
		mNodes = new Node[numNodes];
		mEdges = new ArrayList<Edge>();
		
		for(int i = 0; i < mNodes.length; i++)
			mNodes[i] = new Node(i);
		
		for(Node a: mNodes) {
			for(Node b: mNodes) {
				if(a.getId() != b.getId()) {
					boolean c = false;
					for(Edge edge: mEdges) {
						if(edge.getA().getId() == b.getId() && 
								edge.getB().getId() == a.getId()) {
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
	}
	
	private void optimalSolution(Node[] nodes) {
		long start_time = System.currentTimeMillis();
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
		
		long end_time = System.currentTimeMillis();
		
		System.out.println("\nOptimal:");
		System.out.println((end_time - start_time) + "ms");
		System.out.println(shortestPath + "u");
	}
	
	private void permute(Node[] a, int k) {
        if (k == a.length) {
        	Node[] b = new Node[k+1];
        	for(int i = 0; i < k+1; i++) {
        		if(i != k) {
        			b[i] = a[i];
        		} else {
        			b[i] = b[0];
        		}
        	}
            paths.add(b);
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
		long start_time = System.currentTimeMillis();
		
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
		
		path.add(path.get(0));
		
		nearestNeighborPath = path;
		long end_time = System.currentTimeMillis();	
		
		for(int i = 0; i < path.size() - 1; i++) {
			length += (int)calculateDistance(path.get(i), path.get(i+1));
		}
				
		System.out.println("\nNearest Neighbor:");
		System.out.println((end_time - start_time) + "ms");
		System.out.println(length + "u");
	}
	
	private void greedyHeuristic(Node[] nodes, ArrayList<Edge> edges) {
		long start_time = System.currentTimeMillis();
		ArrayList<Edge> path = new ArrayList<Edge>();
		Collections.sort(edges);
		
		a:for(Edge edge : edges) {
			path.add(edge);
			edge.getA().addDegree();
			edge.getB().addDegree();
			
			for(Edge a : path) {
				if(a.getA().getDegree() > 2 || a.getB().getDegree() > 2) {
					path.remove(edge);
					edge.getA().subtractDegree();
					edge.getB().subtractDegree();
					continue a;
				}
			}
			
			if(path.size() > 2) {
				b:for(Node node : nodes) {
					Edge current = null;
					Node a = node;
					int n = 0;
					if(a.getDegree() < 2) continue;
					
					while(true) {
						for(Edge e : path) {
							if(e.getA().getId() == a.getId() || e.getB().getId() == a.getId()) {
								if(current == null || e.getId() != current.getId()) {
									current = e;
									n++;
									break;
								}
							}
						}
						
						a = current.getA().getId() == a.getId() ? current.getB(): current.getA(); // Node 'a' becomes the second node in the current edge
						
						if(a.getDegree() < 2) {
							break;
						}
						
						if(a.getId() == node.getId() && n < numNodes) {
							path.remove(edge);
							edge.getA().subtractDegree();
							edge.getB().subtractDegree();
							break b;
						} else if(a.getId() == node.getId() && n == numNodes) {
							break a;
						}
					}
				}
			}
		}
		
		
		greedyPath = path;
		long end_time = System.currentTimeMillis();
		
		int length = 0;
		for(Edge edge : path) {
			length += edge.getWeight();
		}

		System.out.println("\nGreedy:");
		System.out.println((end_time - start_time) + "ms");
		System.out.println(length + "u");
	}
	
	private double calculateDistance(Node a, Node b) {	
		return Math.sqrt( (a.getX() - b.getX()) * (a.getX() - b.getX()) 
				+ (a.getY() - b.getY()) * (a.getY() - b.getY()) );
	}
	
	private void updateText() {
		switch(index) {
			case 0:
				text.setText("Optimal");
				break;
			case 1:
				text.setText("Nearest Neighbor");
				break;
			case 2:
				text.setText("Greedy");
				break;
		}
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
		
		switch(index) {
			case 0:
				g2d.setColor(Color.BLUE);
				for(int i = 0; i < optimalPath.length - 1; i++) {
					g2d.drawLine(optimalPath[i].getX(), optimalPath[i].getY(),
							optimalPath[i+1].getX(), optimalPath[i+1].getY());
				}
				break;
			case 1:
				g2d.setColor(Color.RED);
				for(int i = 0; i < nearestNeighborPath.size() - 1; i++) {
					g2d.drawLine(nearestNeighborPath.get(i).getX(), nearestNeighborPath.get(i).getY(),
							nearestNeighborPath.get(i+1).getX(), nearestNeighborPath.get(i+1).getY());
				}
				break;
			case 2:
				g2d.setColor(Color.BLACK);
				for(int i = 0; i < greedyPath.size(); i++) {
					g2d.drawLine(greedyPath.get(i).getA().getX(), greedyPath.get(i).getA().getY(), 
							greedyPath.get(i).getB().getX(), greedyPath.get(i).getB().getY());	
				}
				break;
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
