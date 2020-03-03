
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

class GameController implements MouseListener {
	GameAgent agent;
	GameModel model; // maintains all the game data
	GameView view; // this is the graphical user interface
	LinkedList<MouseEvent> events; // this is a queue of mouse events

	// Constructor

	public GameController() {
		this.agent = new GameAgent();
		this.events = new LinkedList<MouseEvent>();
	}

	public void initialize() throws Exception {
		this.model = new GameModel(this);
		this.model.initializeGame();
	}

	public boolean update() {
		agent.update(model);
		model.update();
		return true;
	}

	public GameModel getModel() {
		return model;
	}

	public MouseEvent nextMouseEvent() {
		if (events.size() == 0)
			return null;
		return events.remove();
	}

	public void mousePressed(MouseEvent e) {
		if (e.getY() < 600) {
			events.add(e);
			if (events.size() > 20) // remove events if queue becomes too large
				events.remove();
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

}
