package realmstudy.fragments.DialogFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import realmstudy.MainFragmentActivity;
import realmstudy.R;
import realmstudy.adapter.MultiplayerListAdapter;
import realmstudy.data.CommanData;
import realmstudy.data.RealmObjectData.MatchDetails;
import realmstudy.data.RealmObjectData.Player;
import realmstudy.databaseFunctions.RealmDB;

/**
 * Created by developer on 18/2/17.
 */
public class SelectMultiPlayerDialog extends DialogFragment {
    private boolean ishomeTeam;
    private Realm realm;
    private MatchDetails matchDetails;
    private Button butConfirmTime;
    private MultiplayerListAdapter multiplayerListAdapter;
    TextView header_txt;
    private int match_id;
    private LinearLayout overall_lay;

    public static SelectMultiPlayerDialog newInstance(int match_id, boolean ishomeTeam) {
        SelectMultiPlayerDialog f = new SelectMultiPlayerDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("match_id", match_id);
        args.putBoolean("ishomeTeam", ishomeTeam);
        //args.putInt("current_bowler_id", current_bowler_id);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        match_id = getArguments().getInt("match_id");
        ishomeTeam = getArguments().getBoolean("ishomeTeam");
        System.out.println("______________cccc1");

        Realm.init(getActivity());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .build();
        realm = Realm.getInstance(config);
        if (match_id != -1)
            matchDetails = RealmDB.getMatchById(getActivity(), realm, match_id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_listview_dialog, null);
        ListView lv = (ListView) v.findViewById(R.id.list_view);
        butConfirmTime = (Button) v.findViewById(R.id.butConfirmTime);
        header_txt = (TextView) v.findViewById(R.id.header_txt);
        header_txt.setText(getString(R.string.add_player));


        RealmResults<Player> playerList = RealmDB.getAllPlayer(realm);
        multiplayerListAdapter = new MultiplayerListAdapter(getActivity(), playerList);
        lv.setAdapter(multiplayerListAdapter);
        butConfirmTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ArrayList<Integer> data = multiplayerListAdapter.selectedPlayersID();
                String ss = "";
                if (data.size() > 0)
                    System.out.println("____________" + data.toString());
                for (int i = 0; i < data.size(); i++) {
                    ss = ss+RealmDB.getPlayer(realm, data.get(i)).getName() + " , ";
                    if (match_id != -1)
                        RealmDB.addPlayerToMatch(data.get(i), getActivity(), realm, matchDetails, ishomeTeam);

                }
                ((MainFragmentActivity) getActivity()).messageFromDialog(CommanData.DIALOG_SELECT_MULTI_PLAYER, true, data, "sep_commo");
                dismiss();
            }
        });
        return v;
    }
}
