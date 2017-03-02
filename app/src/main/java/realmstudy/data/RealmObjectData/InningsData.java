package realmstudy.data.RealmObjectData;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import realmstudy.MainActivity;
import realmstudy.data.CommanData;

/**
 * Created by developer on 10/12/16.
 */
public class InningsData extends RealmObject {

    int run;
    @Ignore
    boolean legal;
    boolean firstInnings;

    String scoreBoardData;
    //aggregtion --> InningsData has a relationship with BatingProfile class
    Player striker, nonStriker;
    Player currentBowler, nextBowler;
    @PrimaryKey
    String index;
    boolean boundaries;
    Wicket wicket;
    int ballType;
    public boolean isBoundaries() {
        return boundaries;
    }

    public void setBoundaries(boolean boundaries) {
        this.boundaries = boundaries;
    }

    public Wicket getWicket() {
        return wicket;
    }

    public void setWicket(Wicket wicket) {
        this.wicket = wicket;
    }

    public int getBallType() {
        return ballType;
    }

    public void setBallType(int ballType) {
        this.ballType = ballType;
    }


    public void setBallType(CommanData.typeExtraEnum ballType) {
      //  this.ballType = ballType;

        if(ballType==CommanData.typeExtraEnum.NO_BALL)
            this.ballType=CommanData.BALL_NO_BALL;
        else if(ballType==CommanData.typeExtraEnum.WIDE)
            this.ballType=CommanData.BALL_WIDE;
        else if(ballType==CommanData.typeExtraEnum.L_BYES)
            this.ballType=CommanData.BALL_LEGAL_BYES;
        else if(ballType==CommanData.typeExtraEnum.LEG_BYES)
            this.ballType=CommanData.BALL_LB;
        else if(ballType==CommanData.typeExtraEnum.W_BYES)
            this.ballType=CommanData.BALL_WIDE_BYES;
        else if(ballType==CommanData.typeExtraEnum.NB_BYES)
            this.ballType=CommanData.BALL_N0_BALL_BYES;
        else if(ballType==CommanData.typeExtraEnum.STEP_NO_BALL)
            this.ballType=CommanData.BALL_NO_OVER_STEP;
    }

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


    /**
     * returns index number which is primary key in default it merge with match id
     * eg: in db: 09_88
     * returns 09
     * @return index
     */

    public int getIndex() {
        if(index.contains("_"))
        return Integer.parseInt(index.split("_")[0]);
        else
            return  -1;
    }

    public void setIndex(String index) {
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
