package edu.upes.lostfound.model;

public class MatchResult {
    private final ItemReport item;
    private final double score;
    private final String reason;

    public MatchResult(ItemReport item, double score, String reason) {
        this.item = item;
        this.score = score;
        this.reason = reason;
    }

    public ItemReport getItem() {
        return item;
    }

    public double getScore() {
        return score;
    }

    public String getReason() {
        return reason;
    }

    public String getScoreLabel() {
        return Math.round(score * 100) + "%";
    }
}
