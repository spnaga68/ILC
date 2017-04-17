package realmstudy.databaseFunctions;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import realmstudy.R;
import realmstudy.data.CommanData;
import realmstudy.data.RealmObjectData.BatingProfile;
import realmstudy.data.RealmObjectData.BowlingProfile;
import realmstudy.data.RealmObjectData.InningsData;
import realmstudy.data.RealmObjectData.MatchDetails;
import realmstudy.data.RealmObjectData.Player;
import realmstudy.data.RealmObjectData.Team;
import realmstudy.data.RealmObjectData.Wicket;
import realmstudy.data.ScoreBoardData;

/**
 * Created by developer on 28/12/16.
 */
public class RealmDB {
    //have to think for thread safety


    public static BowlingProfile createBowlingProfile(Realm realm, int Pid, int mId) {
        BowlingProfile b = null;
        realm.beginTransaction();
        b = realm.createObject(BowlingProfile.class, Pid + "__" + mId);
        b.setMatch_id(mId);
        b.setPlayerID(Pid);
        realm.commitTransaction();
        return b;
    }

    public static RealmResults<Team> getAllTeam(Realm realm) {

        return realm.where(Team.class).findAll();
    }

    public static Team getTeam(Realm realm, int teamID) {

        return realm.where(Team.class).equalTo("team_id", teamID).findFirst();
    }

//    public static BowlingProfile scheduleMatch(Context c, Realm realm, int Pid, int mId) {
//        BowlingProfile b = null;
//        realm.beginTransaction();
//        b = realm.createObject(BowlingProfile.class, Pid + "__" + mId);
//        b.setMatch_id(mId);
//        b.setPlayerID(Pid);
//        realm.commitTransaction();
//        return b;
//    }


    public static int noOfWicket(Context c, Realm realm, int mId, boolean isFirstInnings) {
        return realm.where(InningsData.class).equalTo("match_id", mId).equalTo("firstInnings", isFirstInnings).isNotNull("wicket").findAll().size();
    }

    public static BatingProfile createBattingProfile(Realm realm, int Pid, int mId) {
        BatingProfile b = null;
//        if (realm.where(BatingProfile.class).findAll().size() > 0)
//            id = realm.where(BatingProfile.class).findAll().last().getBattingProfileID() + 1;

        realm.beginTransaction();
        b = realm.createObject(BatingProfile.class, Pid + "__" + mId);
        b.setMatch_id(mId);
        b.setPlayerID(Pid);
        realm.commitTransaction();
        System.out.println("_____________JJB" + b.getBattingProfileID());
        return b;
    }

    public static BatingProfile getBattingProfile(Realm realm, int Pid, int mId) {
        BatingProfile bf = realm.where(BatingProfile.class).equalTo("battingProfileID", Pid + "__" + mId).findFirst();
        if (bf == null)
            bf = RealmDB.createBattingProfile(realm, Pid, mId);
        return bf;
    }

    public static BowlingProfile getBowlingProfile(Realm realm, int Pid, int mId) {
        BowlingProfile bwf = realm.where(BowlingProfile.class).equalTo("bowlingProfileId", Pid + "__" + mId).findFirst();
        if (bwf == null)
            bwf = RealmDB.createBowlingProfile(realm, Pid, mId);
        return bwf;
    }

    public static InningsData getInningsData(Context c, Realm realm, int index, int mid, boolean isfirstInnings) {

        InningsData data = realm.where(InningsData.class).equalTo("index", index + "_" + mid + "_" + (RealmDB.getMatchById(c, realm, mid).isFirstInningsCompleted() ? "S" : "F")).findFirst();
        if (data == null) {
            realm.beginTransaction();
            data = realm.createObject(InningsData.class, index + "_" + mid + "_" + (RealmDB.getMatchById(c, realm, mid).isFirstInningsCompleted() ? "S" : "F"));
            realm.commitTransaction();
        }
        return data;

    }


    public static RealmResults<Player> getAllPlayer(Realm realm) {

        return realm.where(Player.class).findAll();


    }

    public static Player getPlayer(Realm realm, int index) {

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


    public static MatchDetails UpdateorCreateMatchDetail(Context c, Realm realm, Team home_team, Team away_team,
                                                         String chosse_to, Team wonToss, int overs, String location
            , int totalPlayers, long time, int mid) {
        long tsLong;
        if (time == 0)
            tsLong = System.currentTimeMillis() / 1000;
        else
            tsLong = time;
        //String ts = tsLong.toString();
        int id = 0;
        //if (realm.where(MatchDetails.class).findAll().size() > 0)
        id = (int) tsLong;
        realm.beginTransaction();
        MatchDetails md = null;
        if (mid != -1)
            md = getMatchById(c, realm, mid);
        if (md == null)
            md = realm.createObject(MatchDetails.class, id);
//        md.setMatch_id(id);
        if (home_team != null)
            md.setHomeTeam(realm.where(Team.class).equalTo("team_id", home_team.team_id).findFirst());
        if (away_team != null)
            md.setAwayTeam(realm.where(Team.class).equalTo("team_id", away_team.team_id).findFirst());
        md.setChooseTo(chosse_to);
        if (wonToss != null)
            md.setToss(realm.where(Team.class).equalTo("team_id", wonToss.team_id).findFirst());
        md.setTime(tsLong);
        md.setMatchStatus(CommanData.MATCH_NOT_YET_STARTED);
        md.setLocation(location);
        md.setOvers(overs);
        if (home_team != null && wonToss != null) {
            boolean homeTeamWonToss = wonToss.team_id == home_team.team_id ? true : false;
            md.setHomeTeamBatting((homeTeamWonToss && chosse_to.equals("bat")) || (!homeTeamWonToss && chosse_to.equals("bowl")));
        }
        md.setTotalPlayers(totalPlayers);
        realm.commitTransaction();
        // System.out.println("_____________vvv" + realm.where(MatchDetails.class).equalTo("match_id", id).findFirst().getHomeTeam().team_id);
        return realm.where(MatchDetails.class).equalTo("match_id", md.getMatch_id()).findFirst();

    }


    public static MatchDetails createNewMatch(Context c, Realm realm, Team home_team, Team away_team,
                                              String chosse_to, Team wonToss, int overs, String location
            , int totalPlayers, long time) {
        Long tsLong;
        if (time == 0)
            tsLong = System.currentTimeMillis() / 1000;
        else
            tsLong = time;
        //String ts = tsLong.toString();
        int id = 0;
        if (realm.where(MatchDetails.class).findAll().size() > 0)
            id = realm.where(MatchDetails.class).findAll().last().getMatch_id() + 1;
        realm.beginTransaction();
        MatchDetails md = realm.createObject(MatchDetails.class, id);
        // md.setMatch_id(id);
        if (home_team != null)
            md.setHomeTeam(realm.where(Team.class).equalTo("team_id", home_team.team_id).findFirst());
        if (away_team != null)
            md.setAwayTeam(realm.where(Team.class).equalTo("team_id", away_team.team_id).findFirst());
        md.setChooseTo(chosse_to);
        if (wonToss != null)
            md.setToss(realm.where(Team.class).equalTo("team_id", wonToss.team_id).findFirst());
        md.setTime(tsLong);
        md.setMatchStatus(CommanData.MATCH_NOT_YET_STARTED);
        md.setLocation(location);
        md.setOvers(overs);
        if (home_team != null && wonToss != null) {
            boolean homeTeamWonToss = wonToss.team_id == home_team.team_id ? true : false;
            md.setHomeTeamBatting((homeTeamWonToss && chosse_to.equals("bat")) || (!homeTeamWonToss && chosse_to.equals("bowl")));
        }
        md.setTotalPlayers(totalPlayers);
        realm.commitTransaction();
        // System.out.println("_____________vvv" + realm.where(MatchDetails.class).equalTo("match_id", id).findFirst().getHomeTeam().team_id);
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
        //  System.out.println("_____________sss" + realm.where(MatchDetails.class).equalTo("match_id", id).findFirst().getHomeTeam().team_id);

        return realm.where(MatchDetails.class).equalTo("match_id", id).findFirst();

    }

    public static Player AddPlayer(Context c, Realm realm, String name, String Phno) {
        RealmResults<Player> result2 = realm.where(Player.class)
                .equalTo("ph_no", Phno)
                .findAll();
        int id = 0;
        if (result2.size() == 0) {
            realm.beginTransaction();

            if (realm.where(Player.class).findAll().size() > 0)
                id = realm.where(Player.class).findAll().last().getpID() + 1;
            Player playerObj = realm.createObject(Player.class, Phno);
            playerObj.setpID(id);
            playerObj.setName(name);
            realm.commitTransaction();
            return realm.where(Player.class).equalTo("pID", id).findFirst();

        } else {
            Toast.makeText(c, c.getString(R.string.phno_exist), Toast.LENGTH_SHORT).show();
            return realm.where(Player.class).equalTo("pID", id).findFirst();
        }
    }


    private static String Wicket(Context c, Realm realm, int batsmans, int bowlers, int type, int caughtBys, int runOutBys, String over, int match_id) {

        if (getWicket(c, realm, batsmans + "_" + match_id) == null) {
            Player batsman = getPlayer(realm, batsmans);
//            Player bowler = getPlayer(c, realm, bowlers);
//            Player caughtBy = getPlayer(c, realm, caughtBys);
//            Player runOutBy = getPlayer(c, realm, runOutBys);

            realm.beginTransaction();
            RealmDB.getBattingProfile(realm, batsmans, match_id).setCurrentStatus(CommanData.StatusOut);
//        int id = 0;
//        if (realm.where(Wicket.class).findAll().size() > 0)
//            id = realm.where(Player.class).findAll().last().getpID() + 1;
            Wicket wicketObj = realm.createObject(Wicket.class, batsmans + "_" + match_id);
            wicketObj.setBatsman(batsmans);
            wicketObj.setBowler(bowlers);
            wicketObj.setType(type);
            wicketObj.setCaughtBy(caughtBys);
            wicketObj.setRunoutBy(runOutBys);
            wicketObj.setOver(over);
            wicketObj.setMatch_id(match_id);
            //  wicketObj.setWicket_id(batsmans+"_"+match_id);
            realm.commitTransaction();
            realm.beginTransaction();
            batsman.setOutAs(realm.where(Wicket.class).equalTo("wicket_id", batsmans + "_" + match_id).findFirst());
            realm.commitTransaction();
            return batsmans + "_" + match_id;
        } else return batsmans + "_" + match_id;


    }

    public static Wicket getWicket(Context c, Realm realm, String id) {

        return realm.where(Wicket.class).equalTo("wicket_id", id).findFirst();


    }

    public static String wicketCaught(Context c, Realm realm, int batsman, int bowler, int type, int caughtBy, String over, int match_id) {

        return Wicket(c, realm, batsman, bowler, type, caughtBy, -1, over, match_id);


    }

    public static String wicketRunout(Context c, Realm realm, int batsman, int bowler, int type, int runOutBy, String over, int match_id) {

        return Wicket(c, realm, batsman, bowler, type, -1, runOutBy, over, match_id);


    }

    public static String wicketOther(Context c, Realm realm, int batsman, int bowler, int type, String over, int match_id) {

        return Wicket(c, realm, batsman, bowler, type, -1, -1, over, match_id);


    }

    public static int addNewPlayerToMatch(String name, String ph_no, Context c, Realm realm, MatchDetails matchDetails, boolean ishomeTeam) {

        int id = AddPlayer(c, realm, name, ph_no).getpID();
        return addPlayerToMatch(id, c, realm, matchDetails, ishomeTeam);

    }

    public static boolean addPlayerToMatch(RealmList<Player> players, Context c, Realm realm, MatchDetails matchDetails) {
        for (int i = 0; i < players.size(); i++) {
            addPlayerToMatch(players.get(i).getpID(), c, realm, matchDetails);
        }
        return true;
    }


    public static int addPlayerToMatch(int id, Context c, Realm realm, MatchDetails matchDetails) {

        Player dummy = RealmDB.getPlayer(realm, id);
        System.out.println("_____________" + matchDetails.getTotalPlayers() + "___" + matchDetails.getHomeTeamPlayers());


        System.out.println("_____________JJBcccc");
        BatingProfile bf = RealmDB.getBattingProfile(realm, dummy.getpID(), matchDetails.getMatch_id());
        BowlingProfile bwf = RealmDB.getBowlingProfile(realm, dummy.getpID(), matchDetails.getMatch_id());
        realm.beginTransaction();
        matchDetails.addUnAssignedPlayer(dummy);
        bf.setCurrentStatus(CommanData.StatusInMatch);
        bwf.setCurrentBowlerStatus(CommanData.StatusInMatch);
        realm.commitTransaction();
        return dummy.getpID();
        //return -1;
    }


    public static int addPlayerToMatch(int id, Context c, Realm realm, MatchDetails matchDetails, boolean ishomeTeam) {

        Player dummy = RealmDB.getPlayer(realm, id);
        String ph_no = dummy.getPh_no();
        boolean playerExtra = true;
        boolean playerInOpponent = false;
        boolean isPlayerAlreadyAdded = false;
        System.out.println("_____________" + matchDetails.getTotalPlayers() + "___" + matchDetails.getHomeTeamPlayers());

        if (ishomeTeam) {
            playerExtra = matchDetails.getTotalPlayers() > matchDetails.totalHomeplayer();
            playerInOpponent = isPlayerInOppenant(matchDetails.getAwayTeamPlayers(), ph_no, c, realm);
            isPlayerAlreadyAdded = isPlayerAlreadyAdded(matchDetails.getHomeTeamPlayers(), ph_no, c, realm);
        } else {
            playerExtra = matchDetails.getTotalPlayers() > matchDetails.totalAwayplayer();
            playerInOpponent = isPlayerInOppenant(matchDetails.getHomeTeamPlayers(), ph_no, c, realm);
            isPlayerAlreadyAdded = isPlayerAlreadyAdded(matchDetails.getAwayTeamPlayers(), ph_no, c, realm);
        }
        System.out.println("_____________JJBcccc" + isPlayerAlreadyAdded + "__" + playerInOpponent + "__" + playerExtra);
        if (!isPlayerAlreadyAdded)
            if (!playerInOpponent)
                if (playerExtra) {
                    System.out.println("_____________JJBcccc");
                    BatingProfile bf = RealmDB.createBattingProfile(realm, dummy.getpID(), matchDetails.getMatch_id());
                    BowlingProfile bwf = RealmDB.createBowlingProfile(realm, dummy.getpID(), matchDetails.getMatch_id());
                    //dummy = RealmDB.getPlayer(c,realm,id);
                    realm.beginTransaction();
                    if (ishomeTeam)
                        matchDetails.addHomePlayer(dummy);
                    else
                        matchDetails.addAwayPlayer(dummy);
                    bf.setCurrentStatus(CommanData.StatusInMatch);
                    bwf.setCurrentBowlerStatus(CommanData.StatusInMatch);
//                    dummy.setRecentBatingProfile(bf);
//                    dummy.setRecentBowlingProfile(bwf);
                    realm.commitTransaction();
                    return dummy.getpID();
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
//        if (dummy != null)
//            return dummy.getpID();
//        else
        return -1;
    }

    private static boolean isPlayerInOppenant(RealmList<Player> opponentPlayers, String id, Context c, Realm realm) {
        boolean inOponnent = false;

        if (opponentPlayers != null) {
            for (int i = 0; i < opponentPlayers.size(); i++) {
                System.out.println("________RRR" + opponentPlayers.size() + "___" + id + "__" + opponentPlayers.get(i).getpID());
                if (RealmDB.getPlayer(realm, opponentPlayers.get(i).getpID()).getPh_no().equals(id)) {

                    inOponnent = true;
                }
            }
        }
        return inOponnent;

    }

    private static boolean isPlayerAlreadyAdded(RealmList<Player> playerAlreadyAdded, String id, Context c, Realm realm) {
        boolean isPresent = false;

        if (playerAlreadyAdded != null) {
            for (int i = 0; i < playerAlreadyAdded.size(); i++) {
                System.out.println("________RRR" + playerAlreadyAdded.size() + "___" + id + "__" + playerAlreadyAdded.get(i).getpID());
                if (RealmDB.getPlayer(realm, playerAlreadyAdded.get(i).getpID()).getPh_no().equals(id)) {

                    isPresent = true;
                }
            }
        }
        return isPresent;

    }

    public static ArrayList<Player> getPlayerNotInBothTeam(Context activity, Realm realm, MatchDetails matchDetails) {
        RealmResults<Player> otherPlayers = (realm.where(Player.class).findAll());
        ArrayList<Player> otherPlayer = null;
        if (otherPlayers != null)
            otherPlayer = new ArrayList<>(otherPlayers.subList(0, otherPlayers.size()));
        RealmList<Player> home_team_players = realm.where(MatchDetails.class).equalTo("match_id", matchDetails.getMatch_id()).findFirst().getHomeTeamPlayers();

        RealmList<Player> away_team_players = realm.where(MatchDetails.class).equalTo("match_id", matchDetails.getMatch_id()).findFirst().getAwayTeamPlayers();

        otherPlayer.removeAll(away_team_players);
        otherPlayer.removeAll(home_team_players);

        return otherPlayer;
    }

    public static ArrayList<Player> getPlayerNotInHomeTeam(Context activity, Realm realm, MatchDetails matchDetails) {
        RealmResults<Player> otherPlayers = (realm.where(Player.class).findAll());
        ArrayList<Player> otherPlayer = null;
        if (otherPlayers != null)
            otherPlayer = new ArrayList<>(otherPlayers.subList(0, otherPlayers.size()));
        RealmList<Player> home_team_players = realm.where(MatchDetails.class).equalTo("match_id", matchDetails.getMatch_id()).findFirst().getHomeTeamPlayers();

        RealmList<Player> away_team_players = realm.where(MatchDetails.class).equalTo("match_id", matchDetails.getMatch_id()).findFirst().getAwayTeamPlayers();

        otherPlayer.removeAll(away_team_players);
        // otherPlayer.removeAll(home_team_players);

        return otherPlayer;
    }

    public static ArrayList<Player> getPlayerNotInAwayTeam(Context activity, Realm realm, MatchDetails matchDetails) {
        RealmResults<Player> otherPlayers = (realm.where(Player.class).findAll());
        ArrayList<Player> otherPlayer = null;
        if (otherPlayers != null)
            otherPlayer = new ArrayList<>(otherPlayers.subList(0, otherPlayers.size()));
        RealmList<Player> home_team_players = realm.where(MatchDetails.class).equalTo("match_id", matchDetails.getMatch_id()).findFirst().getHomeTeamPlayers();

        RealmList<Player> away_team_players = realm.where(MatchDetails.class).equalTo("match_id", matchDetails.getMatch_id()).findFirst().getAwayTeamPlayers();

        //  otherPlayer.removeAll(away_team_players);
        otherPlayer.removeAll(home_team_players);

        return otherPlayer;
    }

    public static String getInningsDataIndex(int i, MatchDetails matchDetails) {
        return i + "_" + matchDetails.getMatch_id() + "_" + (matchDetails.isFirstInningsCompleted() ? "S" : "F");
    }

    public static String[] getLastThreeOvers(Context activity, Realm realm, MatchDetails matchDetails, String lastDelievery) {
        RealmResults<InningsData> id = realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).equalTo("firstInnings", !matchDetails.isFirstInningsCompleted()).findAll();
        if (id != null && id.size() > 0) {
            InningsData inningsDatas = id.last();
            //  RealmResults<InningsData> inningsDatas = realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).equalTo("firstInnings", !matchDetails.isFirstInningsCompleted()).findAll();
//        int temp = inningsDatas.size() - 11;
//        int max = temp < 0 ? 0 : temp;
//        String[] ss = new String[12];
//        String sss = "";
//
//        // System.out.println("SSSSS___"+s);
//        ss[11]= lastDelievery;
//        int si = (inningsDatas.size() - 2)-max;
//        for (int i = inningsDatas.size() - 1; i > max; i--) {
//            sss += "  " + String.valueOf(inningsDatas.get(i).getIndex()) + "_" + inningsDatas.get(i).getRun();
//            String s = String.valueOf(inningsDatas.get(i).getRun());
//            ss[si] = s;
//            si-=1;
//            //  System.out.println("SSSSS___"+s);
//        }
            ArrayList<String> lb = CommanData.fromJson(inningsDatas.getScoreBoardData(), ScoreBoardData.class).getLastThreeOvers();
            ArrayList<String> ss = new ArrayList<>();
            if (lb != null)
                for (int i = 0; i < lb.size(); i++)
                    ss.add(lb.get(i));
            ss.add(lastDelievery);

            // System.out.println("SSSSS___" + sss);
            System.out.println("SSSSS___" + ss.toString() + "__" + ss.size());
            String[] ssa = new String[ss.size()];
            return ss.toArray(ssa);
        } else {
            String[] sss = new String[1];
            sss[0] = lastDelievery;
            return sss;
        }
    }

    public static int getFirstInningsTotal(Realm realm, MatchDetails matchDetails) {

        InningsData inningsDatas = realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).equalTo("firstInnings", matchDetails.isFirstInningsCompleted()).findAll().last();
        return CommanData.fromJson(inningsDatas.getScoreBoardData(), ScoreBoardData.class).getTotalRuns();
    }

    public static String getFirstInningsOver(Realm realm, MatchDetails matchDetails) {

        InningsData inningsDatas = realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).equalTo("firstInnings", matchDetails.isFirstInningsCompleted()).findAll().last();
        return CommanData.fromJson(inningsDatas.getScoreBoardData(), ScoreBoardData.class).getTotalOver();

    }

    public static void updateBattingBowlingProfile(Realm realm, MatchDetails matchDetails) {

        RealmResults<InningsData> inningsDatas = realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).equalTo("firstInnings", matchDetails.isFirstInningsCompleted()).findAll();


    }

    public static void updateBattingProfile(Realm realm, MatchDetails matchDetails, int player_id) {
        RealmResults<InningsData> batsmanData = realm.where(InningsData.class)
                .equalTo("match_id", matchDetails.getMatch_id())
                .equalTo("striker", player_id)
                .notEqualTo("delivery", 0)
                .findAll();

        int runs = 0;
        int balls = 0;

        for (InningsData data : batsmanData) {
            if (data.getBallType() == CommanData.BALL_LEGAL ? true : (data.getBallType() == CommanData.BALL_NO_BALL || data.getBallType() == CommanData.BALL_NO_OVER_STEP)) {
                runs += data.getRun();
                balls += 1;
            }
        }
        BatingProfile bf = getBattingProfile(realm, player_id, matchDetails.getMatch_id());
        realm.beginTransaction();
        bf.setBallFaced(balls);
        bf.setRuns(runs);
        Log.d("updateBattingProfile", "updateBattingProfile: "+batsmanData.size());
        realm.commitTransaction();
        //inningsDatas
    }


    public static void updateBowlingProfile(Realm realm, MatchDetails matchDetails, int player_id) {
        RealmResults<InningsData> bowlerdata = realm.where(InningsData.class)
                .equalTo("match_id", matchDetails.getMatch_id())
                .equalTo("currentBowler", player_id)
                .notEqualTo("delivery", 0)
                .findAll();

        int runs = 0;
        int balls = bowlerdata.size();
        int wide = bowlerdata.where().equalTo("ballType", CommanData.BALL_WIDE).findAll().size();
        int no_balls = bowlerdata.where().equalTo("ballType", CommanData.BALL_NO_BALL).findAll().size();
        int byes = bowlerdata.where().equalTo("ballType", CommanData.BALL_LEGAL_BYES).findAll().size();
        ;
        for (InningsData data : bowlerdata) {
            if (data.getBallType() != CommanData.BALL_LB && data.getBallType() != CommanData.BALL_LEGAL_BYES) {
                runs += data.getRun();
              //  System.out.println("___________dddd" + bowlerdata.size() + "_" + runs);
            }
        }

        BowlingProfile bf = getBowlingProfile(realm, player_id, matchDetails.getMatch_id());
        realm.beginTransaction();
        bf.setBallsBowled(balls);
        bf.setRunsGranted(runs);
        bf.setByes(byes);
        bf.setNoBall(no_balls);
        bf.setWide(wide);
        realm.commitTransaction();

    }
    public static void getLegalBallsByBowler(Realm realm, MatchDetails matchDetails, int player_id) {
        RealmResults<InningsData> bowlerdata = realm.where(InningsData.class)
                .equalTo("match_id", matchDetails.getMatch_id())
                .equalTo("currentBowler", player_id)
                .notEqualTo("delivery", 0)
                .findAll();
        System.out.println("___________dddd" + bowlerdata.size() + "_" + player_id);
        int runs = 0;
        int balls = bowlerdata.size();
        int wide = bowlerdata.where().equalTo("ballType", CommanData.BALL_WIDE).findAll().size();
        int no_balls = bowlerdata.where().equalTo("ballType", CommanData.BALL_NO_BALL).findAll().size();
        int byes = bowlerdata.where().equalTo("ballType", CommanData.BALL_LEGAL_BYES).findAll().size();
        ;
        for (InningsData data : bowlerdata) {
            if (data.getBallType()==CommanData.BALL_LEGAL ||(data.getBallType() != CommanData.BALL_LB && data.getBallType() != CommanData.BALL_LEGAL_BYES)) {
                runs = +data.getRun();

            }
        }
        BowlingProfile bf = getBowlingProfile(realm, player_id, matchDetails.getMatch_id());
        realm.beginTransaction();
        bf.setBallsBowled(balls);
        bf.setRunsGranted(runs);
        bf.setByes(byes);
        bf.setNoBall(no_balls);
        bf.setWide(wide);
        realm.commitTransaction();

    }





    public static float BallinWhichOver(Realm realm,MatchDetails matchDetails) {
//        float over = (lastInningsDataItem.getOver() - Math.floor(lastInningsDataItem.getOver())) >= 0.5 ? (float) (Math.ceil(lastInningsDataItem.getOver())) : CommanData.round2(((float) (lastInningsDataItem.getOver() + .1)), 1);
//        if (lastInningsDataItem.getOver() == 0)
//            over = (float) 0.1;
        int overs= realm.where(InningsData.class).equalTo("match_id",matchDetails.getMatch_id())
                .equalTo("firstInnings",!matchDetails.isFirstInningsCompleted()).notEqualTo("delivery",0).findAll().size();
        int wide= realm.where(InningsData.class).equalTo("match_id",matchDetails.getMatch_id())
                .equalTo("ballType",CommanData.BALL_WIDE).equalTo("firstInnings",!matchDetails.isFirstInningsCompleted()).findAll().size();
        int noBall= realm.where(InningsData.class).equalTo("match_id",matchDetails.getMatch_id())
                .equalTo("ballType",CommanData.BALL_NO_BALL) .equalTo("firstInnings",!matchDetails.isFirstInningsCompleted()).findAll().size();

        return ballsToOver(overs-(wide+noBall));
    }

    private static Float ballsToOver(int balls) {
        String over = "0.0";
        if (balls >= 6) {
            over = (balls / 6) + "." + (balls % 6);
        } else {
            over = "0." + balls;
        }
        return Float.parseFloat(over);
    }
}

