package pcd.sketch01.model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private static final double HOLE_RADIUS = 0.25;
    private List<Ball> balls;    
    private Ball playerBall;
    private Boundary bounds;
    private List<Hole> holes;
    private int humanScore;
    private int botScore;

    public Board(){
        holes = new ArrayList<>();
        humanScore = 0;
        botScore = 0;
    }
    
    public void init(BoardConf conf) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall(); 
    	bounds = conf.getBoardBoundary();

        // creazione delle 2 buche

        Hole leftHole  = new Hole(new P2d(bounds.x0(), bounds.y1()), HOLE_RADIUS);
        Hole rightHole = new Hole(new P2d(bounds.x1(), bounds.y1()), HOLE_RADIUS);

        holes = List.of(leftHole, rightHole);
    }
    
    public void updateState(long dt) {

    	playerBall.updateState(dt, this);
    	for (var b: balls) {
    		b.updateState(dt, this);
    	}       	
    	
    	for (int i = 0; i < balls.size() - 1; i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                Ball.resolveCollision(balls.get(i), balls.get(j));
            }
        }
    	for (var b: balls) {
    		Ball.resolveCollision(playerBall, b);
    	}
        // toglie tutti gli elementi della lista che soddisfano il predicato
//        balls.removeIf(ball -> isInsideAnyHole(ball));
        var iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball b = iterator.next();
            if (isInsideAnyHole(b)) {
                if (b.getLastTouchedBy() == PlayerId.HUMAN) {
                    humanScore++;
                } else if (b.getLastTouchedBy() == PlayerId.BOT) {
                    botScore++;
                }
                iterator.remove();
            }
        }

        if (isInsideAnyHole(playerBall)) {
            // finita la partita
            //todo: da gestire la fine della partita se uno dei playrer finisce in buca
        }

    }

    private boolean isInsideAnyHole(Ball ball) {
        for (var h : holes) {
            double dx   = ball.getPos().x() - h.pos().x();
            double dy   = ball.getPos().y() - h.pos().y();
            double dist = Math.hypot(dx, dy);
            if(dist < h.radius()) return true;
        }
        return false;
    }
    
    public List<Ball> getBalls(){
    	return balls;
    }
    
    public Ball getPlayerBall() {
    	return playerBall;
    }
    
    public  Boundary getBounds(){
        return bounds;
    }

    public List<Hole> getHoles() {
        return holes;
    }

    public int getHumanScore() { return humanScore; }

    public int getBotScore() { return botScore; }
}
