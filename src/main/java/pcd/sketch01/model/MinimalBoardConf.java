package pcd.sketch01.model;

import java.util.ArrayList;
import java.util.List;

public class MinimalBoardConf implements BoardConf {

	@Override
	public Ball getPlayerBall() {
    	return new Ball(0, new P2d(0, 0), 0.05, 1.5, new V2d(0,0), PlayerId.HUMAN);
	}

	@Override
	public List<Ball> getSmallBalls() {		
        var balls = new ArrayList<Ball>();
    	var b1 = new Ball(2, new P2d(0, 0.5), 0.05, 0.75, new V2d(0,0), PlayerId.NONE);
    	var b2 = new Ball(3, new P2d(0.05, 0.55), 0.025, 0.25, new V2d(0,0), PlayerId.NONE);
    	balls.add(b1);
    	balls.add(b2);
    	return balls;
	}

    @Override
    public Ball getBotBall() {
        return new Ball(1, new P2d(0.5, 0.5), 0.05, 1.5, new V2d(0,0), PlayerId.BOT);
    }

    @Override
	public Boundary getBoardBoundary() {
        return new Boundary(-1.5,-1.0,1.5,1.0);
	}

}
