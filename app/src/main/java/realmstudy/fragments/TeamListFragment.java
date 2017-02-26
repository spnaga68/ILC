package realmstudy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import realmstudy.MainFragmentActivity;
import realmstudy.R;
import realmstudy.adapter.TeamListAdapter;
import realmstudy.adapter.TeamListSelectionAdapter;
import realmstudy.data.RealmObjectData.Team;
import realmstudy.interfaces.DialogInterface;
import realmstudy.interfaces.MsgToFragment;

/**
 * Created by developer on 21/2/17.
 */
public class TeamListFragment extends Fragment implements DialogInterface,MsgToFragment{
    private RecyclerView
            list_view;
    private android.support.design.widget.FloatingActionButton add;
    private android.support.design.widget.FloatingActionButton next;
    TeamListAdapter adapter;
    private Realm realm;
    TextView selected_teams;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.team_list_view, container, false);
        list_view = (RecyclerView) v.findViewById(R.id.list_view);
        add = (android.support.design.widget.FloatingActionButton) v.findViewById(R.id.add);
        next = (android.support.design.widget.FloatingActionButton) v.findViewById(R.id.next);
        selected_teams = (TextView) v.findViewById(R.id.selected_teams);
        selected_teams.setSelected(true);
        realm = ((MainFragmentActivity) getActivity()).getRealm();
        adapter = new TeamListAdapter(getActivity(), realm.where(Team.class).findAll());
        list_view.setAdapter(adapter);
        list_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainFragmentActivity) getActivity()).showNewTeamDialog(0, TeamListFragment.this);
            }
        });
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String s = adapter.selectedItem();
//                System.out.println("______________" + s);
//                if (!s.trim().isEmpty()) {
//                    Bundle b = new Bundle();
//                    b.putString("teamIDs", s);
//                    TossFragment mf = new TossFragment();
//                    mf.setArguments(b);
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainFrag, mf).commit();
//                } else {
//                    Toast.makeText(getActivity(), getString(R.string.select_valid_home_away_team), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        return v;
    }

    @Override
    public void onSuccess(String result, boolean success) {

    }

    @Override
    public void msg(String s) {
        selected_teams.setText(s);
    }
}
