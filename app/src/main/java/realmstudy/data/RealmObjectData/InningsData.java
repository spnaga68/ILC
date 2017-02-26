package realmstudy.data.RealmObjectData;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by developer on 10/12/16.
 */
public class InningsData extends RealmObject {

    int run;
    boolean legal;
    boolean firstInnings;

    String scoreBoardData;
    //aggregtion --> InningsData has a relationship with BatingProfile class
    Player striker, nonStriker;
    Player currentBowler, nextBowler;
    @Index
    int index;

    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }

    int match_id;

    public boolean isFirstInnings() {
        return firstInnings;
    }

    public void setFirstInnings(boolean firstInnings) {
        this.firstInnings = firstInnings;
    }



    public Player getCurrentBowler() {
        return currentBowler;
    }

    public void setCurrentBowler(Player currentBowlingProfile) {
        this.currentBowler = currentBowlingProfile;
    }

    public Player getNextBowler() {
        return nextBowler;
    }

    public void setNextBowler(Player nextBowlingProfile) {
        this.nextBowler = nextBowlingProfile;
    }




    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public Player getStriker() {
        return striker;
    }

    public void setStriker(Player striker) {
        this.striker = striker;
    }

    public Player getNonStriker() {
        return nonStriker;
    }

    public void setNonStriker(Player nonStriker) {
        this.nonStriker = nonStriker;
    }


    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public boolean isLegal() {
        return legal;
    }

    public void setLegal(boolean legal) {
        this.legal = legal;
    }


    public String getScoreBoardData() {
        return scoreBoardData;
    }

    public void setScoreBoardData(String scoreBoardData) {
        this.scoreBoardData = scoreBoardData;
    }
}
