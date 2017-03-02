package realmstudy.data.RealmObjectData;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by developer on 24/12/16.
 */
public class BowlingProfile extends RealmObject {

    int runsGranted;
    int ballsBowled;
    int wide, byes, noBall;
    int playerID;
    int currentBowlerStatus;
    @PrimaryKey
    String bowlingProfileId;

    public int getCurrentBowlerStatus() {
        return currentBowlerStatus;
    }

    public void setCurrentBowlerStatus(int currentBowlerStatus) {


        this.currentBowlerStatus = currentBowlerStatus;
    }


    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }

    int match_id;

    public String getBowlingProfileId() {
        return bowlingProfileId;
    }

    public void setBowlingProfileId(String bowlingProfileId) {
        this.bowlingProfileId = bowlingProfileId;
    }


    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }


    public int getRunsGranted() {
        return runsGranted;
    }

    public void setRunsGranted(int runsGranted) {
        this.runsGranted = runsGranted;
    }

    public int getBallsBowled() {
        return ballsBowled;
    }

    public void setBallsBowled(int ballsBowled, Context c, Realm realm, MatchDetails matchDetails) {
        if (matchDetails == null)
            this.ballsBowled = ballsBowled;
//        else
//        {
//            if(ballsBowled==matchDetails.get)
//        }
    }

    public void setBallsBowled(int ballsBowled) {
        this.ballsBowled = ballsBowled;

    }

    public int getWide() {
        return wide;
    }

    public void setWide(int wide) {
        this.wide = wide;
    }

    public int getByes() {
        return byes;
    }

    public void setByes(int byes) {
        this.byes = byes;
    }

    public int getNoBall() {
        return noBall;
    }

    public void setNoBall(int noBall) {
        this.noBall = noBall;
    }
}
