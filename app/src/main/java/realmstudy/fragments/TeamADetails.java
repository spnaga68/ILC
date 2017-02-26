package realmstudy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import realmstudy.data.SessionSave;
import realmstudy.MainFragmentActivity;
import realmstudy.R;
import realmstudy.adapter.PlayerListAdapter;
import realmstudy.data.RealmObjectData.MatchDetails;
import realmstudy.data.RealmObjectData.Player;
import realmstudy.databaseFunctions.RealmDB;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by developer on 7/1/17.
 */
public class TeamADetails extends Fragment {
    private RecyclerView list_view;
    PlayerListAdapter adapter;
    Realm realm;
    int aa;
    String ss[];
    Context c;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.team_details, container, false);
        realm = ((MainFragmentActivity) (getActivity())).getRealm();
        list_view = (RecyclerView) v.findViewById(R.id.list_view);
//        notifyDataChanged(getActivity());
        list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    public void notifyDataChanged(Context c) {

        Realm.init(getActivity());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .build();
        realm = Realm.getInstance(config);

        int matchId = SessionSave.getSessionInt("DetailForMAtch", getActivity());
        MatchDetails md = RealmDB.getMatchById(getActivity(), realm, matchId);

        aa = (int) getArguments().get("team");

        if (aa == 0)
            ss = md.getHomeTeamPlayersArray();

        else
            ss = md.getAwayTeamPlayersArray();

        System.out.println("_________mdd" + matchId + "___" + ss + "____" + aa + "____" + md.getHomeTeamPlayers().toString() + "__");
        if (ss != null) {
            Integer home[] = new Integer[ss.length];
            for (int i = 0; i < ss.length; i++) {
                home[i] = (Integer.parseInt(ss[i]));
            }

            if (c != null) {
                adapter = new PlayerListAdapter((MainFragmentActivity) c, realm.where(Player.class).in("pID", home).findAll());
                list_view.setAdapter(adapter);
                System.out.println("____________notifiedsss");
            } else
                System.out.println("____________notified");

        }


    }

    @Override
    public void onAttach(android.content.Context context) {
        c = context;
        super.onAttach(context);
    }
}
