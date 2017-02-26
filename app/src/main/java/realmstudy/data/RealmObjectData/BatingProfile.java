package realmstudy.data.RealmObjectData;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by developer on 10/12/16.
 */
public class BatingProfile extends RealmObject {
    int runs;
    int ballFaced;
    int playerID;
    @PrimaryKey
    String battingProfileID;
    int currentStatus;
    int match_id;

    /**
     * 0-->free
     * 1-->batting
     * 2-->out_match_inProgress
     *
     * @return current status of batsman
     */
    public int getCurrentStatus() {
        return currentStatus;
    }

    /**
     * StatusFree = 0;
     * StatusBatting = 1;
     * StatusInMatch = 2;
     * StatusOut = 3;
     *
     * @return current status of batsman
     */

    public void setCurrentStatus(int currentStatus) {
        this.currentStatus = currentStatus;
    }


    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }


    public String getBattingProfileID() {
        return battingProfileID;
    }

    public void setBattingProfileID(String battingProfileID) {
        this.battingProfileID = battingProfileID;
    }


    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }


    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public int getBallFaced() {
        return ballFaced;
    }

    public void setBallFaced(int ballFaced) {
        this.ballFaced = ballFaced;
    }


}
