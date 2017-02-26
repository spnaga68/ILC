package realmstudy.data.RealmObjectData;

import io.realm.RealmObject;

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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
        addPlayertoString(0, p);
        getHomeTeam().addPlayer(p);
        return p;
    }

    public Player addAwayPlayer(Player p) {
//        if (!awayTeamPlayers.trim().equals(""))
//            awayTeamPlayers += "," + (p.getpID());
//        else
//            awayTeamPlayers = String.valueOf(p.getpID());
        addPlayertoString(1, p);
        getAwayTeam().addPlayer(p);


        return p;
    }

    /**
     * To add player id to string
     *
     * @param tochange 0-hometeam,1-awayteam
     * @param p-player
     * @return same player
     */
    public Player addPlayertoString(int tochange, Player p) {

        String Addto = null;
        if (tochange == 0)
            Addto = homeTeamPlayers;
        else
            Addto = awayTeamPlayers;
        if (Addto != null) {
            if (Addto.contains(",")) {
                String playerArray[] = Addto.split(",");
                for (int i = 0; i < playerArray.length; i++)
                    if (p.getpID() == Integer.parseInt(playerArray[i]))
                        return null;
            } else if (p.getpID() == Integer.parseInt(Addto))
                return null;
            else
                Addto += "," + (p.getpID());
        } else
            Addto = String.valueOf(p.getpID());

        if (tochange == 0)
            homeTeamPlayers = Addto;
        else
            awayTeamPlayers = Addto;
        return p;
    }

    public int totalHomeplayer() {
        return homeTeamPlayers.split(",").length;
    }

    public int totalAwayplayer() {
        return awayTeamPlayers.split(",").length;
    }

    public String getHomeTeamPlayers() {
        return homeTeamPlayers;
    }

    public String[] getHomeTeamPlayersArray() {
        String[] s;
        if (homeTeamPlayers.equals("")) {
            s = null;
        } else {
            s = homeTeamPlayers.split(",");
        }
        return s;
    }

    public String[] getAwayTeamPlayersArray() {
        String[] s;
        if (awayTeamPlayers.equals("")) {
            s = null;
        } else {
            s = awayTeamPlayers.split(",");
        }
        return s;
    }

    public String getAwayTeamPlayers() {
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


    public String getBattingTeamPlayer() {
        if (isHomeTeamBatting())
            return getHomeTeamPlayers();
        else
            return getAwayTeamPlayers();
    }

    public String getBowlingTeamPlayer() {
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

    private int match_id;
    private int overs;
    private int totalPlayers;
    private String time, location;
    private Team homeTeam, awayTeam, toss;
    private String chooseTo;
    private boolean firstInningsCompleted;


    private boolean homeTeamBatting;
    private String homeTeamPlayers;
    private String awayTeamPlayers;

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

    public void setAwayTeamPlayers(String awayTeamPlayers) {
        this.awayTeamPlayers = awayTeamPlayers;
    }

    public void setHomeTeamPlayers(String homeTeamPlayers) {
        this.homeTeamPlayers = homeTeamPlayers;
    }

    public void setHomeTeamBatting(boolean homeTeamBatting) {
        this.homeTeamBatting = homeTeamBatting;
    }

    private int matchStatus;

}
