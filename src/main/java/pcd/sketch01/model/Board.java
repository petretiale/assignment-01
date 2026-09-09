package pcd.sketch01.model;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private static final double HOLE_RADIUS = 0.25;
    private List<Ball> balls;    
    private Ball playerBall;
    private Ball botBall;
    private Boundary bounds;
    private List<Hole> holes;
    private int humanScore;
    private int botScore;
    private boolean gameOver;
    private PlayerId winner;
    private List<Worker> workers;

    public Board(){
        holes = new ArrayList<>();
        humanScore = 0;
        botScore = 0;
        gameOver = false;
        winner = PlayerId.NONE;
    }
    
    public void init(BoardConf conf) {
    	balls = conf.getSmallBalls();    	
    	playerBall = conf.getPlayerBall();
        botBall = conf.getBotBall();
    	bounds = conf.getBoardBoundary();
        workers = createWorkers();

        // creazione delle 2 buche

        Hole leftHole  = new Hole(new P2d(bounds.x0(), bounds.y1()), HOLE_RADIUS);
        Hole rightHole = new Hole(new P2d(bounds.x1(), bounds.y1()), HOLE_RADIUS);

        holes = List.of(leftHole, rightHole);
    }
    
    public void updateState(long dt) {

        System.out.println(Runtime.getRuntime().availableProcessors());

        if (gameOver) {
            return;
        }

        for (var w : workers) {
            w.setDt(dt);
            w.start();
        }
        try {
            for (var w: workers) {
                w.join();
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }


        playerBall.updateState(dt, this);
        botBall.updateState(dt, this);
//    	for (var b: balls) {
//    		b.updateState(dt, this);
//    	}
//
//    	for (int i = 0; i < balls.size() - 1; i++) {
//            for (int j = i + 1; j < balls.size(); j++) {
//                Ball.resolveCollision(balls.get(i), balls.get(j));
//            }
//        }
    	for (var b: balls) {
    		Ball.resolveCollision(playerBall, b);
            Ball.resolveCollision(botBall, b);
    	}
        // Collisione diretta tra la pallina del giocatore e quella del bot
        Ball.resolveCollision(playerBall, botBall);

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
        // Controllo se la pallina del giocatore cade in buca (vince la pallina che non è finita in buca)
        if (isInsideAnyHole(playerBall)) {
            gameOver = true;
            this.winner = PlayerId.BOT;
        }

        if (isInsideAnyHole(botBall)) {
            gameOver = true;
            this.winner = PlayerId.HUMAN;
        }

        // controllo fine palline (vince chi ha il punteggio piu alto)
        if (balls.isEmpty()) {
            gameOver = true;
            if (humanScore > botScore) {
                this.winner = PlayerId.HUMAN;
            } else if (botScore > humanScore) {
                this.winner = PlayerId.BOT;
            } else {
                this.winner = PlayerId.NONE;
            }
        }
    }

    public void kickBall(PlayerId player, V2d impulse) {
        if (gameOver) return;
        if (player == PlayerId.HUMAN && playerBall != null) {
            playerBall.kick(impulse);
        } else if (player == PlayerId.BOT && botBall != null) {
            botBall.kick(impulse);
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

    private List<Worker> createWorkers() {

        List<Worker> workers = new ArrayList<>();
        int nWorkers = Runtime.getRuntime().availableProcessors();
        Barrier updateBallBarrier = new Barrier(nWorkers);
        Barrier resolveCollisionBarrier = new Barrier(nWorkers);


        for (int i = 0; i < nWorkers; i++) {
            workers.add(new Worker(i, nWorkers, this, updateBallBarrier, resolveCollisionBarrier));
        }
        return workers;
    }
    
    public List<Ball> getBalls(){
    	return balls;
    }

    public Ball getPlayerBall() {
    	return playerBall;
    }

    public Ball getBotBall() {
        return botBall;
    }
    
    public  Boundary getBounds(){
        return bounds;
    }

    public List<Hole> getHoles() {
        return holes;
    }

    public int getHumanScore() { return humanScore; }

    public int getBotScore() { return botScore; }

    public boolean isGameOver() { return gameOver; }

    public PlayerId getWinner() { return winner; }
}
