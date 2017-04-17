package realmstudy.data.RealmObjectData;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by developer on 2/1/17.
 */
public class MatchDetails extends RealmObject {
    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Team getToss() {
        return toss;
    }

    public void setToss(Team toss) {
        this.toss = toss;
    }

    public String getChooseTo() {
        return chooseTo;
    }

    public void setChooseTo(String chooseTo) {
        this.chooseTo = chooseTo;
    }


    public int getOvers() {
        return overs;
    }

    public void setOvers(int overs) {
        this.overs = overs;
    }


    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }


    public Player addHomePlayer(Player p) {
//        if (!homeTeamPlayers.trim().equals(""))
//            homeTeamPlayers += "," + (p.getpID());
//        else
//            homeTeamPlayers = String.valueOf(p.getpID());


        if (homeTeamPlayers != null) {
            //  String playerArray[] = Addto.split(",");
            for (int i = 0; i < homeTeamPlayers.size(); i++)
                if (p.getpID() == homeTeamPlayers.get(i).getpID())
                    return p;
            for (int i = 0; i < awayTeamPlayers.size(); i++)
                if (p.getpID() == awayTeamPlayers.get(i).getpID())
                    return null;


            homeTeamPlayers.add(p);
        }


        // addPlayertoTeam(0, p);
        //getHomeTeam().addPlayer(p);
        return p;
    }

    public Player addUnAssignedPlayer(Player p) {


        if (notAssignedPlayers != null) {
            //  String playerArray[] = Addto.split(",");
            for (int i = 0; i < notAssignedPlayers.size(); i++)
                if (p.getpID() == notAssignedPlayers.get(i).getpID())
                    return p;

            notAssignedPlayers.add(p);
        }

        return p;
    }


    public RealmList<Player> getUnAssignedPlayer(Player p) {
//
        return notAssignedPlayers;
    }


    public Player addAwayPlayer(Player p) {
//        if (!awayTeamPlayers.trim().equals(""))
//            awayTeamPlayers += "," + (p.getpID());
//        else
//            awayTeamPlayers = String.valueOf(p.getpID());


        if (awayTeamPlayers != null) {
            //  String playerArray[] = Addto.split(",");
            for (int i = 0; i < homeTeamPlayers.size(); i++)
                if (p.getpID() == homeTeamPlayers.get(i).getpID())
                    return null;
            for (int i = 0; i < awayTeamPlayers.size(); i++)
                if (p.getpID() == awayTeamPlayers.get(i).getpID())
                    return p;


            awayTeamPlayers.add(p);
        }


        // addPlayertoTeam(1, p);
        // getAwayTeam().addPlayer(p);


        return p;
    }

//    /**
//     * To add player id to string
//     *
//     * @param tochange 0-hometeam,1-awayteam
//     * @param p-player
//     * @return same player
//     */
//    public Player addPlayertoTeam(int tochange, Player p) {
//
//        RealmList<Player> Addto = null;
//        if (tochange == 0)
//            Addto = homeTeamPlayers;
//        else
//            Addto = awayTeamPlayers;
//        if (Addto != null) {
//              //  String playerArray[] = Addto.split(",");
//                for (int i = 0; i < Addto.size(); i++)
//                    if (p.getpID() == Addto.get(i).getpID())
//                        return null;
//
//            else
//                Addto.add(p);
//        } else
//            Addto.add(p);
//
//        if (tochange == 0)
//            homeTeamPlayers = Addto;
//        else
//            awayTeamPlayers = Addto;
//        return p;
//    }

    public int totalHomeplayer() {
        return homeTeamPlayers.size();
    }

    public int totalAwayplayer() {
        return awayTeamPlayers.size();
    }

    public RealmList<Player> getHomeTeamPlayers() {
        return homeTeamPlayers;
    }

    public Integer[] getHomeTeamPlayersArray() {
        if (awayTeamPlayers != null) {
            Integer[] s = new Integer[homeTeamPlayers.size()];
            for (int i = 0; i < homeTeamPlayers.size(); i++) {
                s[i] = homeTeamPlayers.get(i).getpID();
            }
            return s;
        }
        return null;
    }

    public Integer[] getAwayTeamPlayersArray() {
        if (awayTeamPlayers != null) {
            Integer[] s = new Integer[awayTeamPlayers.size()];
            for (int i = 0; i < awayTeamPlayers.size(); i++) {
                s[i] = awayTeamPlayers.get(i).getpID();
            }
            return s;
        }
        return null;
    }

    public RealmList<Player> getAwayTeamPlayers() {
        return awayTeamPlayers;
    }

    public Team getCurrentBattingTeam() {
        if (!firstInningsCompleted) {
            if (toss.team_id == homeTeam.team_id) {
                if (chooseTo.equals("bat")) {
                    return homeTeam;
                } else {
                    return awayTeam;
                }
            } else {
                if (chooseTo.equals("bat")) {
                    return awayTeam;
                } else {
                    return homeTeam;
                }
            }
        } else {
            if (toss.team_id == homeTeam.team_id) {
                if (chooseTo.equals("bat")) {
                    return awayTeam;
                } else {
                    return homeTeam;
                }
            } else {
                if (chooseTo.equals("bat")) {
                    return homeTeam;
                } else {
                    return awayTeam;
                }
            }
        }


    }


    public Team getBowling() {
        if (!firstInningsCompleted) {
            if (toss.team_id == homeTeam.team_id) {
                if (chooseTo.equals("bat")) {
                    return homeTeam;
                } else {
                    return awayTeam;
                }
            } else {
                if (chooseTo.equals("bat")) {
                    return awayTeam;
                } else {
                    return homeTeam;
                }
            }
        } else {
            if (toss.team_id == homeTeam.team_id) {
                if (chooseTo.equals("bat")) {
                    return awayTeam;
                } else {
                    return homeTeam;
                }
            } else {
                if (chooseTo.equals("bat")) {
                    return homeTeam;
                } else {
                    return awayTeam;
                }
            }
        }


    }

    public boolean isHomeTeamBatting() {

        return homeTeamBatting;
    }


    public boolean isFirstInningsCompleted() {
        return firstInningsCompleted;
    }

    public void setFirstInningsCompleted(boolean firstInningsCompleted) {
        this.firstInningsCompleted = firstInningsCompleted;
    }


    public RealmList<Player> getBattingTeamPlayer() {
        if (isHomeTeamBatting())
            return getHomeTeamPlayers();
        else
            return getAwayTeamPlayers();
    }

    public RealmList<Player> getBowlingTeamPlayer() {
        if (isHomeTeamBatting())
            return getAwayTeamPlayers();
        else
            return getHomeTeamPlayers();
    }


    public String[] PlayerWhoLossWicketArray() {
        String[] s;
        if (playerWhoLoseWicket.equals("")) {
            s = null;
        } else {
            s = playerWhoLoseWicket.split(",");
        }
        return s;
    }






@PrimaryKey
    private int match_id;
    private int overs;
    private int totalPlayers;
    private Long time;
            private String location;
    private Team homeTeam, awayTeam, toss;
    private String chooseTo;
    private boolean firstInningsCompleted;
    private boolean homeTeamBatting;

    private RealmList<Player> homeTeamPlayers = new RealmList<>();
    private RealmList<Player> awayTeamPlayers = new RealmList<>();
    private RealmList<Player> notAssignedPlayers = new RealmList<>();

    public String getPlayerWhoLoseWicket() {
        return playerWhoLoseWicket;
    }

    public void setPlayerWhoLoseWicket(String playerWhoLoseWicket) {
        this.playerWhoLoseWicket = playerWhoLoseWicket;
    }

    private String playerWhoLoseWicket = "";

    public int getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(int matchStatus) {
        this.matchStatus = matchStatus;
    }

    public void setAwayTeamPlayers(Player awayTeamPlayers) {
        this.awayTeamPlayers.add(awayTeamPlayers);
    }

    public void setHomeTeamPlayers(Player homeTeamPlayers) {
        this.homeTeamPlayers.add(homeTeamPlayers);
    }

    public void setHomeTeamBatting(boolean homeTeamBatting) {
        this.homeTeamBatting = homeTeamBatting;
    }

    private int matchStatus;

}
