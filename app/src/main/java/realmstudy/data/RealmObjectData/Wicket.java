package realmstudy.data.RealmObjectData;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import realmstudy.data.CommanData;

/**
 * Created by developer on 5/1/17.
 */
public class Wicket extends RealmObject {
    int match_id;
    int batsman;
    int bowler;
    String over;
    int type;
    int caughtBy;
    int runoutBy;
    @PrimaryKey
    String wicket_id;
    boolean isStrikerOut=true;
    
    
    
    public void setStrikerOut(boolean strikerOut) {
        isStrikerOut = strikerOut;
    }

    public boolean isStrikerOut() {
        return isStrikerOut;
    }



    public String getWicket_id() {
        return wicket_id;
    }

    public void setWicket_id(String wicket_id) {
        this.wicket_id = wicket_id;
    }




    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }

    public int getBatsman() {
        return batsman;
    }

    public void setBatsman(int batsman) {
        this.batsman = batsman;
    }

    public int getBowler() {
        return bowler;
    }

    public void setBowler(int bowler) {
        this.bowler = bowler;
    }

    public String getOver() {
        return over;
    }

    public void setOver(String over) {
        this.over = over;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCaughtBy() {
        return caughtBy;
    }

    public void setCaughtBy(int caughtBy) {
        this.caughtBy = caughtBy;
    }

    public int getRunoutBy() {
        return runoutBy;
    }

    public void setRunoutBy(int runoutBy) {
        this.runoutBy = runoutBy;
    }

}
