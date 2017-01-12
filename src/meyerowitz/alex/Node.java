package meyerowitz.alex;

public class Node 
{
	private int mDegree;
	public int getDegree() {return mDegree;}
	public void addDegree() {mDegree+=1;}
	
	private int mX;
	public int getX() { return mX; }
	
	private int mY;
	public int getY() { return mY; }
	
	private int mId;
	public int getId() { return mId; }
	
	private boolean mConnected;
	public boolean getConnected() { return mConnected; }
	public void setConnected(boolean connected) { mConnected = connected; }
	
	public Node(int Id) {
		mId = Id;
		mX = (int)(Math.random() * 900) + 50; // {0, 1, ... , 999}
		mY = (int)(Math.random() * 900) + 50; // {0, 1, ... , 999}
		mConnected = false;
		mDegree = 0;
	}
}
