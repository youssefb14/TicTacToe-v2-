package model;

public class Score {
    private int playerScore = 0;
    private int opponentScore = 0;

    public void incrementPlayerScore() {
        playerScore++;
    }

    public void incrementOpponentScore() {
        opponentScore++;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void resetScores() {
        playerScore = 0;
        opponentScore = 0;
    }
}
