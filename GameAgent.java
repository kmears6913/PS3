import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeMap;

import javax.swing.Timer;


class Node{
		
	private int x;
	private int y;
	private double cost;
	private double totalcost;
//	List<Node> list;
	Node next;
	Node parent;
	private boolean visited;
	

	public Node(int x, int y) {
		this.x = x - (x%10);
		this.y = y - (y%10);
		this.cost = 0;
		this.totalcost = Integer.MAX_VALUE;
		this.visited = false;
//		this.list = new ArrayList<Node>();
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getTotalcost() {
		return totalcost;
	}
	public void setTotalcost(double totalcost) {
		this.totalcost = totalcost;
	}
	public Node getNext() {
		return next;
	}
	public void setNext(Node next) {
		this.next = next;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}


}
class NodeCostComp implements Comparator<Node>{
	 
    @Override
    public int compare(Node n1, Node n2) {
        if(n1.getTotalcost() > n2.getTotalcost()){
            return 1;
        } else {
            return -1;
        }
    }
}
class GameAgent {
	static Comparator<Node> costSorter = Comparator.comparing(Node::getCost); 
	static PriorityQueue<Node> pQ = new PriorityQueue<Node>( new NodeCostComp() );
	static HashMap<String, Node> Nodes = new HashMap<String, Node>();
	static Stack<Node> st = new Stack<Node>();
	static Stack<Node> st1;
	static Node destHold;
	//static TreeMap<Node, Node> pQ = new TreeMap<Node, Node>(new NodeCostComp());
	/*
	 * Problem Set 3
	 * 
	 * Student code should be contained within this class.
	 * 
	 */

	public void drawPlan(Graphics g, GameModel m) {
		g.setColor(Color.red);
		g.drawLine((int)m.getX(), (int)m.getY(), (int)m.getDestXValue(), (int)m.getDestYValue());
		PriorityQueue<Node> pQ1 = new PriorityQueue<Node>( new NodeCostComp() );
		st1 = (Stack<Node>) st.clone();
		pQ1.addAll(pQ);
		Node n;
		
		while(!pQ1.isEmpty())
		{
			n = pQ1.poll();
			g.drawOval(n.getX(), n.getY(), 5, 5);
		}
		while(!st1.empty())
		{
			
			n = st1.pop();
			if(n.parent != null)
			{
				g.drawLine(n.getX(), n.getY(), 
					n.getParent().getX(), n.getParent().getY());
				
			}
		}

		if((int)m.getX() == (int)m.getDestXValue() && (int)m.getY() == (int)m.getDestYValue() && !st.isEmpty())
		{
			n = st.pop();
			m.setDest((float)n.getX(), (float)n.getY());
			
//				System.out.println("m x,y: " + (int)m.getX() + "," + (int)m.getY() + 
//			"dest x,y: " + (int)m.getDestXValue() + "," + (int)m.getDestYValue());		
		}
		
//		System.out.print(pQ);
	}

	public void update(GameModel m)
	{
		GameController c = m.getController();
		while(true)
		{
			MouseEvent e = c.nextMouseEvent();
			if(e == null)
				break;
			System.out.println(e.getButton());
			pQ.clear();
			Nodes.clear();
			st.clear();
//			m.setDest(e.getX(), e.getY());
			System.out.println(m.getSpeedOfTravel(m.getX(), m.getY()));
			Node n = new Node((int)(m.getX()), (int)(m.getY()));
			Node dest = new Node((e.getX()), (e.getY()));
			String key = "" + n.getX() + "," + n.getY();
			Nodes.putIfAbsent(key, n);
			key = "" + dest.getX() + "," + dest.getY();
			Nodes.putIfAbsent(key, dest);
			destHold = dest;
			System.out.println("X " + dest.getX() + " Y "+ dest.getY());
			if(e.getButton() == 1)
				UCS(n, m, dest);
			else if(e.getButton() == 3)
				A(n, m, dest);
			//System.out.println("X value:\t" + (int)(m.getX()) + "\t Y value:\t" + (int)m.getY()/10);

		}
	}
	//Uniform Cost search
	public void UCS(Node n, GameModel m, Node dest) {
		n.setVisited(true);
		n.setTotalcost(0);
		pQ.add(n);
		
		while(!pQ.isEmpty())
		{
			Node child = pQ.poll();
//			System.out.println(pQ);
			if(child.getX() == dest.getX() && child.getY() == dest.getY())
			{
				st.push(child);
				while(child.parent != null)
				{
					st.push(child.parent);
					child = child.parent;
				}
				System.out.println(child.getX() + ", " + child.getY());

//				System.out.println("FOUND");
				break;
			}
			child.setVisited(true);
			setAdjNodes(child,m);
		}
	}
	
	public void checkCost(Node parent,Node child, GameModel m)
	{
		double distance = (float)(Math.sqrt(Math.pow((parent.getX() - child.getX()),2) 
				+ Math.pow((parent.getY() - child.getY()),2)));		
		double speed = m.getSpeedOfTravel(child.getX(), child.getY());
		double costFromParent = distance/speed + parent.getTotalcost();
		
		if(!pQ.contains(child) && child.isVisited() == false)
		{
//			System.out.println("true");
			child.setTotalcost(costFromParent);
			child.setParent(parent);
			pQ.add(child);
		}
			
		else if(costFromParent < child.getTotalcost())
		{
			child.setTotalcost(costFromParent);
			child.setParent(parent);
		}
		
	}
	
	//Set nodes in adjacency list for all 8 directions
	public void setAdjNodes(Node n, GameModel m){
		//left node
		if(n.getX() >= 20)
		{
			int x = n.getX()-10;
			String key = "" + x + "," + n.getY();
//			System.out.println(key);
			Node b = new Node(x, n.getY());
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCost(n, b, m);
		}
		//right node
		if(n.getX() <= 1180)
		{
			int x = n.getX()+10;
			String key = "" + x + "," + n.getY();
//			System.out.println(key);
			Node b = new Node(x, n.getY());
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCost(n, b, m);
		}
		//top node
		if(n.getY() >= 20)
		{
			int y = n.getY()-10;
			String key = "" + n.getX() + "," + y;
//			System.out.println(key);
			Node b = new Node(n.getX(), y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCost(n, b, m);
		}
		//bottom node
		if(n.getY() <= 580)
		{
			int y = n.getY()+10;
			String key = "" + n.getX() + "," + y;
//			System.out.println(key);
			Node b = new Node(n.getX(), y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCost(n, b, m);
		}
//		top left node
		if(n.getX() >= 20 && n.getY() <= 580)
		{
			int y = n.getY()+10;
			int x = n.getX()-10;
			String key = "" + x + "," + y;
			Node b = new Node(x, y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCost(n, b, m);
		}
//		top right node
		if(n.getX() <= 1180 && n.getY() <= 580)
		{
			int y = n.getY()+10;
			int x = n.getX()+10;
			String key = "" + x + "," + y;
			Node b = new Node(x, y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCost(n, b, m);
		}
//		Bottom right node
		if(n.getX() <= 1180 && n.getY() >= 20)
		{
			int y = n.getY()-10;
			int x = n.getX()+10;
			String key = "" + x + "," + y;
			Node b = new Node(x, y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCost(n, b, m);
		}
//		Bottom left node
		if(n.getX() >= 20 && n.getY() >= 20)
		{
			int y = n.getY()-10;
			int x = n.getX()-10;
			String key = "" + x + "," + y;
			Node b = new Node(x, y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCost(n, b, m);
		}
		
		
	}
	
	public void A(Node n, GameModel m, Node dest) {
		n.setVisited(true);
		n.setTotalcost(0);
		pQ.add(n);
		
		while(!pQ.isEmpty())
		{
			Node child = pQ.poll();
//			System.out.println(pQ);
			if(child.getX() == dest.getX() && child.getY() == dest.getY())
			{
				st.push(child);
				while(child.parent != null)
				{
					st.push(child.parent);
					child = child.parent;
				}
				System.out.println(child.getX() + ", " + child.getY());

//				System.out.println("FOUND");
				break;
			}
			child.setVisited(true);
			setAdjNodesA(child,m);
		}
	}
	
	public void checkCostA(Node parent,Node child, GameModel m)
	{
		double distance = (float)(Math.sqrt(Math.pow((parent.getX() - child.getX()),2) 
				+ Math.pow((parent.getY() - child.getY()),2)));		
		double speed = m.getSpeedOfTravel(child.getX(), child.getY());
		double distanceToDest = (float)(Math.sqrt(Math.pow((child.getX() - destHold.getX()),2) 
				+ Math.pow((child.getY() - destHold.getY()),2)));
		double costFromParent = distance/speed + parent.getTotalcost() + (.2 * distanceToDest);
		
		if(!pQ.contains(child) && child.isVisited() == false)
		{
//			System.out.println("true");
			child.setTotalcost(costFromParent);
			child.setParent(parent);
			pQ.add(child);
		}
			
		else if(costFromParent < child.getTotalcost())
		{
			child.setTotalcost(costFromParent);
			child.setParent(parent);
		}
		
	}
	
	//Set nodes in adjacency list for all 8 directions
	public void setAdjNodesA(Node n, GameModel m){
		//left node
		if(n.getX() >= 20)
		{
			int x = n.getX()-10;
			String key = "" + x + "," + n.getY();
//			System.out.println(key);
			Node b = new Node(x, n.getY());
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCostA(n, b, m);
		}
		//right node
		if(n.getX() <= 1180)
		{
			int x = n.getX()+10;
			String key = "" + x + "," + n.getY();
//			System.out.println(key);
			Node b = new Node(x, n.getY());
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCostA(n, b, m);
		}
		//top node
		if(n.getY() >= 20)
		{
			int y = n.getY()-10;
			String key = "" + n.getX() + "," + y;
//			System.out.println(key);
			Node b = new Node(n.getX(), y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCostA(n, b, m);
		}
		//bottom node
		if(n.getY() <= 580)
		{
			int y = n.getY()+10;
			String key = "" + n.getX() + "," + y;
//			System.out.println(key);
			Node b = new Node(n.getX(), y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCostA(n, b, m);
		}
//		top left node
		if(n.getX() >= 20 && n.getY() <= 580)
		{
			int y = n.getY()+10;
			int x = n.getX()-10;
			String key = "" + x + "," + y;
			Node b = new Node(x, y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCostA(n, b, m);
		}
//		top right node
		if(n.getX() <= 1180 && n.getY() <= 580)
		{
			int y = n.getY()+10;
			int x = n.getX()+10;
			String key = "" + x + "," + y;
			Node b = new Node(x, y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCostA(n, b, m);
		}
//		Bottom right node
		if(n.getX() <= 1180 && n.getY() >= 20)
		{
			int y = n.getY()-10;
			int x = n.getX()+10;
			String key = "" + x + "," + y;
			Node b = new Node(x, y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCostA(n, b, m);
		}
//		Bottom left node
		if(n.getX() >= 20 && n.getY() >= 20)
		{
			int y = n.getY()-10;
			int x = n.getX()-10;
			String key = "" + x + "," + y;
			Node b = new Node(x, y);
			Nodes.putIfAbsent(key, b);
			b = Nodes.get(key);
			checkCostA(n, b, m);
		}
		
		
	}

	public static void main(String[] args) throws Exception
	{
		GameController c = new GameController();
		c.initialize();
		
		// This will instantiate a new instance of JFrame.  Each will spawn in another thread to generate events
		//and keep the entire program running until the JFrame is terminated.
		c.view = new GameView(c, c.model);
		
		// this will create an ActionEvent at fairly regular intervals.   Each of the events are handled by
		// GameView.actionPerformed()
		new Timer(20, c.view).start(); 
	}
}
