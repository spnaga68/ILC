package realmstudy.data.RealmObjectData;

import io.realm.RealmObject;

/**
 * Created by developer on 26/12/16.
 */
public class Team extends RealmObject {
    public String name;
    public String nick_name;
    public int team_id;
    String players;

    public Player addPlayer(Player p) {
        if (players != null) {
            if (players.contains(",")) {
                String playerArray[] = players.split(",");
                for (int i = 0; i < playerArray.length; i++)
                    if (p.getpID() == Integer.parseInt(playerArray[i]))
                        return null;
            } else if (p.getpID() == Integer.parseInt(players))
                return null;
            else
                players += "," + (p.getpID());
        } else
            players = String.valueOf(p.getpID());
        return p;
    }

    public int totalPlayers() {
        if (players == null)
            return 0;
        else
            return players.split(",").length;
    }

    public String getPlayers() {
        return players;
    }
}
