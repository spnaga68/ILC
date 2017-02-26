package realmstudy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import realmstudy.MainFragmentActivity;
import realmstudy.interfaces.DialogInterface;
import realmstudy.R;
import realmstudy.data.RealmObjectData.Player;

import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by developer on 27/12/16.
 */
public class CurrentTeam extends Fragment implements DialogInterface {

    public static int currentTeamPlayersCount = 11;
    LinearLayout root_lay;
    int forPosition;
    Realm realm;
    ArrayList<PlayerLocal> playerName = new ArrayList<>();
    ArrayList<Integer> playerID = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.current_team, container, false);
        root_lay = (LinearLayout) v.findViewById(R.id.root_lay);
        realm = ((MainFragmentActivity) getActivity()).getRealm();

        getPlayerList();
        for (int i = 0; i < currentTeamPlayersCount; i++) {
            View child = inflater.inflate(R.layout.current_team_item, root_lay, false);
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) child.findViewById(R.id.autoCompleteTextView);
            ImageButton new_player = (ImageButton) child.findViewById(R.id.new_player);
            ImageButton from_contacts = (ImageButton) child.findViewById(R.id.from_contacts);
            new_player.setTag(i);
            from_contacts.setTag(i);
            new_player.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    forPosition = (int) view.getTag();
                    ((MainFragmentActivity) getActivity()).showNewTeamDialog(1, CurrentTeam.this);
                }
            });
            from_contacts.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    forPosition = (int) view.getTag();
                }
            });

            ArrayAdapter<PlayerLocal> adapter = new ArrayAdapter<>(
                    getActivity(), android.R.layout.simple_dropdown_item_1line, playerName);
            autoCompleteTextView.setAdapter(adapter);

            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    PlayerLocal selected = (PlayerLocal) arg0.getAdapter().getItem(arg2);
//                    Toast.makeText(MainActivity.this,
//                            "Clicked " + arg2 + " name: " + selected.name,
//                            Toast.LENGTH_SHORT).show();
                }
            });

        }
        return v;
    }

    private void getPlayerList() {
        RealmResults<Player> allPlayer = realm.where(Player.class).findAll();
        playerName.clear();
        playerID.clear();
        for (Player s : allPlayer) {
            PlayerLocal bb = new PlayerLocal();
            bb.pid = s.getpID();
            bb.playerName = s.getName();
            playerName.add(bb);
            //  playerID.add(s.getpID());
        }
    }

    class PlayerLocal {
        int pid;
        String playerName;
    }

    @Override
    public void onSuccess(String result, boolean success) {
        if (!result.trim().isEmpty()) {
            String s[] = result.split("____");
            View layy = root_lay.getChildAt(forPosition);


        }
    }
}
