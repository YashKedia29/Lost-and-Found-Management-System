package edu.upes.lostfound.model;

public class DashboardStats {
    private int totalLost;
    private int totalFound;
    private int pendingClaims;
    private int matchedItems;
    private int claimedItems;

    public int getTotalLost() {
        return totalLost;
    }

    public void setTotalLost(int totalLost) {
        this.totalLost = totalLost;
    }

    public int getTotalFound() {
        return totalFound;
    }

    public void setTotalFound(int totalFound) {
        this.totalFound = totalFound;
    }

    public int getPendingClaims() {
        return pendingClaims;
    }

    public void setPendingClaims(int pendingClaims) {
        this.pendingClaims = pendingClaims;
    }

    public int getMatchedItems() {
        return matchedItems;
    }

    public void setMatchedItems(int matchedItems) {
        this.matchedItems = matchedItems;
    }

    public int getClaimedItems() {
        return claimedItems;
    }

    public void setClaimedItems(int claimedItems) {
        this.claimedItems = claimedItems;
    }
}
