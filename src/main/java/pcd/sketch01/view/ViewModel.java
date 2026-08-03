package pcd.sketch01.view;

import pcd.sketch01.model.Board;
import pcd.sketch01.model.Hole;
import pcd.sketch01.model.P2d;

import java.util.ArrayList;
import java.util.List;

record BallViewInfo(P2d pos, double radius) {}

public class ViewModel {

	private ArrayList<BallViewInfo> balls;
	private BallViewInfo player;
	private int framePerSec;
    private List<Hole> holes;
    private int humanScore;
    private int botScore;
	
	public ViewModel() {
		balls = new ArrayList<>();
        holes = new ArrayList<>();
		framePerSec = 0;
        botScore = 0;
        humanScore = 0;
	}
	
	public synchronized void update(Board board, int framePerSec) {
		balls.clear();
		for (var b: board.getBalls()) {
			balls.add(new BallViewInfo(b.getPos(), b.getRadius()));
		}
		this.framePerSec = framePerSec;
		var p = board.getPlayerBall();
		player = new BallViewInfo(p.getPos(), p.getRadius());

        this.holes = new ArrayList<>(board.getHoles());
        humanScore = board.getHumanScore();
        botScore = board.getBotScore();
	}
	
	public synchronized ArrayList<BallViewInfo> getBalls(){
		var copy = new ArrayList<BallViewInfo>();
		copy.addAll(balls);
		return copy;
		
	}

	public synchronized int getFramePerSec() {
		return framePerSec;
	}

	public synchronized BallViewInfo getPlayerBall() {
		return player;
	}

    public synchronized List<Hole> getHoles() {
        return holes;
    }

    public synchronized int getHumanScore() {
        return humanScore;
    }

    public synchronized int getBotScore() {
        return botScore;
    }
}
