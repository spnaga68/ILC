package realmstudy.databaseFunctions;

import android.content.Context;
import android.widget.Toast;

import io.realm.RealmResults;
import realmstudy.R;
import realmstudy.data.CommanData;
import realmstudy.data.RealmObjectData.BatingProfile;
import realmstudy.data.RealmObjectData.InningsData;
import realmstudy.data.RealmObjectData.MatchDetails;
import realmstudy.data.RealmObjectData.Team;
import realmstudy.data.RealmObjectData.Wicket;
import realmstudy.data.RealmObjectData.BowlingProfile;
import realmstudy.data.RealmObjectData.Player;

import io.realm.Realm;
import realmstudy.data.ScoreBoardData;

/**
 * Created by developer on 28/12/16.
 */
public class RealmDB {
    //have to think for thread safety


    public static BowlingProfile createBowlingProfile(Context c, Realm realm,int Pid,int mId) {
        BowlingProfile b = null;
//        if (realm.where(BowlingProfile.class).findAll().size() > 0)
//            id = realm.where(BowlingProfile.class).findAll().last().getBowlingProfileId() + 1;


        realm.beginTransaction();
        b = realm.createObject(BowlingProfile.class,Pid+"__"+mId);
        b.setMatch_id(mId);
        b.setPlayerID(Pid);
       // b.setBowlingProfileId(id);
        realm.commitTransaction();


        return b;
    }

    public static BatingProfile createBattingProfile(Context c, Realm realm,int Pid,int mId) {
        BatingProfile b = null;
//        if (realm.where(BatingProfile.class).findAll().size() > 0)
//            id = realm.where(BatingProfile.class).findAll().last().getBattingProfileID() + 1;

        realm.beginTransaction();
        b = realm.createObject(BatingProfile.class,Pid+"__"+mId);
        b.setMatch_id(mId);
        b.setPlayerID(Pid);
        realm.commitTransaction();

        return b;
    }
    public static BatingProfile getBattingProfile(Context c, Realm realm,int Pid,int mId) {
        //realm.where(BatingProfile.class).equalTo("",Pid+"__"+mId).findFirst();



        return realm.where(BatingProfile.class).equalTo("battingProfileID",Pid+"__"+mId).findFirst();
    }
    public static BowlingProfile getBowlingProfile(Context c, Realm realm,int Pid,int mId) {
       // realm.where(BowlingProfile.class).equalTo("",Pid+"__"+mId).findFirst();
        return realm.where(BowlingProfile.class).equalTo("bowlingProfileId",Pid+"__"+mId).findFirst();
    }

    public static RealmResults<Player> getAllPlayer(Realm realm) {

        return realm.where(Player.class).findAll();


    }

    public static Player getPlayer(Context c, Realm realm, int index) {

        return realm.where(Player.class).equalTo("pID", index).findFirst();


    }

//    public static Player getPlayerWithNewRecentBattingProfile(Context c, Realm realm, int index) {
//
//        Player p = realm.where(Player.class).equalTo("pID", index).findFirst();
//        p.setRecentBatingProfile(createBattingProfile(c, realm, p));
//        return p;
//
//    }
//
//
//    public static Player getPlayerWithNewRecentBowlingProfile(Context c, Realm realm, int index) {
//        Player p = realm.where(Player.class).equalTo("pID", index).findFirst();
//        p.setRecentBowlingProfile(createBowlingProfile(c, realm, p));
//        return p;
//
//
//    }

    public static MatchDetails createNewMatch(Context c, Realm realm, Team home_team, Team away_team,
                                              String chosse_to, Team wonToss, int overs, String location
            , int totalPlayers) {

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        int id = 0;
        if (realm.where(MatchDetails.class).findAll().size() > 0)
            id = realm.where(MatchDetails.class).findAll().last().getMatch_id() + 1;
        realm.beginTransaction();
        MatchDetails md = realm.createObject(MatchDetails.class);
        md.setMatch_id(id);
        md.setHomeTeam(realm.where(Team.class).equalTo("team_id", home_team.team_id).findFirst());
        md.setAwayTeam(realm.where(Team.class).equalTo("team_id", away_team.team_id).findFirst());
        md.setChooseTo(chosse_to);
        md.setToss(realm.where(Team.class).equalTo("team_id", wonToss.team_id).findFirst());
        md.setTime(ts);
        md.setMatchStatus(0);
        md.setLocation(location);
        md.setOvers(overs);
        md.setHomeTeamBatting(wonToss.team_id == home_team.team_id ? true : false);
        md.setTotalPlayers(totalPlayers);
        realm.commitTransaction();
        System.out.println("_____________vvv" + realm.where(MatchDetails.class).equalTo("match_id", id).findFirst().getHomeTeam().team_id);
        return realm.where(MatchDetails.class).equalTo("match_id", id).findFirst();

    }

    private static int getMatchStatusAfterThisBall(MatchDetails md, Realm realm, boolean legal) {
        int match_status;
        RealmResults<InningsData> datas = realm.where(InningsData.class).equalTo("match_id", md.getMatch_id()).findAll().sort("index");
        if (datas.size() > 0) {
            InningsData idata = datas.last();
            ScoreBoardData sdata = CommanData.fromJson(idata.getScoreBoardData(), ScoreBoardData.class);
            if (md.getMatchStatus() == 0)
                return CommanData.MATCH_STARTED_FI;
            if ((md.getOvers() * 6) > (sdata.getTotalBalls() + 1) && md.getMatchStatus() == CommanData.MATCH_STARTED_FI) {
                return CommanData.MATCH_BREAK_FI;
            } else if ((md.getOvers() * 6) > (sdata.getTotalBalls() + 1) && md.getMatchStatus() == CommanData.MATCH_STARTED_SI) {
                return CommanData.MATCH_COMPLETED;
            } else if ((sdata.getTotalBalls() == 0) && md.getMatchStatus() == CommanData.MATCH_STARTED_FI) {
                return CommanData.MATCH_STARTED_SI;
            }
        }
        return 0;
    }

    public static MatchDetails getMatchById(Context c, Realm realm, int id) {
        System.out.println("_____________sss" + realm.where(MatchDetails.class).equalTo("match_id", id).findFirst().getHomeTeam().team_id);

        return realm.where(MatchDetails.class).equalTo("match_id", id).findFirst();

    }

    public static Player AddPlayer(Context c, Realm realm, String name, String Phno) {
        RealmResults<Player> result2 = realm.where(Player.class)
                .equalTo("ph_no", Phno)
                .findAll();
        if (result2.size() == 0) {
            realm.beginTransaction();
            int id = 0;
            if (realm.where(Player.class).findAll().size() > 0)
                id = realm.where(Player.class).findAll().last().getpID() + 1;
            Player playerObj = realm.createObject(Player.class, Phno);
            playerObj.setpID(id);
            playerObj.setName(name);
            realm.commitTransaction();
            return realm.where(Player.class).equalTo("pID", id).findFirst();

        } else {
            Toast.makeText(c, c.getString(R.string.phno_exist), Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    private static int Wicket(Context c, Realm realm, Player batsman, Player bowler, int type, Player caughtBy, Player runOutBy, String over, int match_id) {

        realm.beginTransaction();
        batsman.getRecentBatingProfile().setCurrentStatus(CommanData.StatusOut);
        int id = 0;
        if (realm.where(Wicket.class).findAll().size() > 0)
            id = realm.where(Player.class).findAll().last().getpID() + 1;
        Wicket wicketObj = realm.createObject(Wicket.class);
        wicketObj.setBatsman(batsman);
        wicketObj.setBowler(bowler);
        wicketObj.setType(type);
        wicketObj.setCaughtBy(caughtBy);
        wicketObj.setRunoutBy(runOutBy);
        wicketObj.setOver(over);
        wicketObj.setMatch_id(match_id);
        wicketObj.setWicket_id(id);
        realm.commitTransaction();
        realm.beginTransaction();
        batsman.setOutAs(realm.where(Wicket.class).equalTo("wicket_id", id).findFirst());
        realm.commitTransaction();
        return id;


    }

    public static int wicketCaught(Context c, Realm realm, Player batsman, Player bowler, int type, Player caughtBy, String over, int match_id) {

        return Wicket(c, realm, batsman, bowler, type, caughtBy, null, over, match_id);


    }

    public static int wicketRunout(Context c, Realm realm, Player batsman, Player bowler, int type, Player runOutBy, String over, int match_id) {

        return Wicket(c, realm, batsman, bowler, type, null, runOutBy, over, match_id);


    }

    public static int wicketOther(Context c, Realm realm, Player batsman, Player bowler, int type, String over, int match_id) {

        return Wicket(c, realm, batsman, bowler, type, null, null, over, match_id);


    }

    public static int addNewPlayerToMatch(String name, String ph_no, Context c, Realm realm, MatchDetails matchDetails, boolean ishomeTeam) {

        int id = AddPlayer(c, realm, name, ph_no).getpID();
        return addPlayerToMatch(id, c, realm, matchDetails, ishomeTeam);

    }

    public static int addPlayerToMatch(int id, Context c, Realm realm, MatchDetails matchDetails, boolean ishomeTeam) {

        Player dummy = RealmDB.getPlayer(c, realm, id);
        String ph_no = dummy.getPh_no();
        boolean playerExtra = true;
        boolean playerInOpponent = false;
        boolean isPlayerAlreadyAdded = false;
        System.out.println("_____________" + matchDetails.getTotalPlayers() + "___" + matchDetails.getHomeTeamPlayers());

        if (ishomeTeam) {
            playerExtra = matchDetails.getTotalPlayers() > matchDetails.totalHomeplayer();
            playerInOpponent = isPlayerInOppenant(matchDetails.getAwayTeamPlayersArray(), ph_no, c, realm);
            isPlayerAlreadyAdded = isPlayerAlreadyAdded(matchDetails.getHomeTeamPlayersArray(), ph_no, c, realm);
        } else {
            playerExtra = matchDetails.getTotalPlayers() > matchDetails.totalAwayplayer();
            playerInOpponent = isPlayerInOppenant(matchDetails.getHomeTeamPlayersArray(), ph_no, c, realm);
            isPlayerAlreadyAdded = isPlayerAlreadyAdded(matchDetails.getAwayTeamPlayersArray(), ph_no, c, realm);
        }
        if (!isPlayerAlreadyAdded)
            if (!playerInOpponent)
                if (playerExtra) {
                    BatingProfile bf = RealmDB.createBattingProfile(c, realm,dummy.getpID(),matchDetails.getMatch_id());
                    //dummy = RealmDB.getPlayer(c,realm,id);
                    realm.beginTransaction();
                    if (ishomeTeam)
                        matchDetails.addHomePlayer(dummy);
                    else
                        matchDetails.addAwayPlayer(dummy);
                    bf.setCurrentStatus(CommanData.StatusBatting);
                    dummy.setRecentBatingProfile(bf);
                    realm.commitTransaction();
                } else {
                    Toast.makeText(c, "Already added", Toast.LENGTH_SHORT).show();
                }
            else {
                Toast.makeText(c, c.getString(R.string.player_op), Toast.LENGTH_SHORT).show();
            }
        else {
            Toast.makeText(c, c.getString(R.string.player_already_in), Toast.LENGTH_SHORT).show();
        }
//            if (getDialog() != null)
//                dismiss();
//            dialogInterface.onSuccess("hii", true);
        //  updateUI();
        if (dummy != null)
            return dummy.getpID();
        else
            return -1;
    }

    private static boolean isPlayerInOppenant(String opponentPlayers[], String id, Context c, Realm realm) {
        boolean inOponnent = false;

        if (opponentPlayers != null) {
            for (int i = 0; i < opponentPlayers.length; i++) {
                System.out.println("________RRR" + opponentPlayers.length + "___" + id + "__" + opponentPlayers[i]);
                if (RealmDB.getPlayer(c, realm, Integer.parseInt(opponentPlayers[i])).getPh_no().equals(id)) {

                    inOponnent = true;
                }
            }
        }
        return inOponnent;

    }

    private static boolean isPlayerAlreadyAdded(String playerAlreadyAdded[], String id, Context c, Realm realm) {
        boolean isPresent = false;

        if (playerAlreadyAdded != null) {
            for (int i = 0; i < playerAlreadyAdded.length; i++) {
                System.out.println("________RRR" + playerAlreadyAdded.length + "___" + id + "__" + playerAlreadyAdded[i]);
                if (RealmDB.getPlayer(c, realm, Integer.parseInt(playerAlreadyAdded[i])).getPh_no().equals(id)) {

                    isPresent = true;
                }
            }
        }
        return isPresent;

    }
}

