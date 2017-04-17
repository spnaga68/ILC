package realmstudy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import realmstudy.MainFragmentActivity;
import realmstudy.adapter.TeamListSelectionAdapter;
import realmstudy.data.RealmObjectData.Team;
import realmstudy.interfaces.DialogInterface;
import realmstudy.interfaces.MsgToFragment;
import realmstudy.R;

import io.realm.Realm;

/**
 * Created by developer on 26/12/16.
 */
public class TeamListSelectionFragment extends Fragment implements DialogInterface, MsgToFragment {

    private ListView
            list_view;
    private android.support.design.widget.FloatingActionButton add;
    private android.support.design.widget.FloatingActionButton next;
    TeamListSelectionAdapter adapter;
    private Realm realm;
    TextView selected_teams;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.team_selection_view, container, false);
        list_view = (ListView) v.findViewById(R.id.list_view);
        add = (android.support.design.widget.FloatingActionButton) v.findViewById(R.id.add);
        next = (android.support.design.widget.FloatingActionButton) v.findViewById(R.id.next);
        selected_teams = (TextView) v.findViewById(R.id.selected_teams);
        selected_teams.setSelected(true);
        realm = ((MainFragmentActivity) getActivity()).getRealm();
        adapter = new TeamListSelectionAdapter(getActivity(), realm.where(Team.class).findAll());
        list_view.setAdapter(adapter);
        if (adapter.getCount() != 0)
            list_view.addHeaderView(inflater.inflate(R.layout.list_view_header, null));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainFragmentActivity) getActivity()).showNewTeamDialog(0, TeamListSelectionFragment.this);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = adapter.selectedItem();
               // System.out.println("______________" + s);
                if (!s.trim().isEmpty()) {
                    Bundle b = new Bundle();
                    b.putString("teamIDs", s);
                    TossFragment mf = new TossFragment();
                    mf.setArguments(b);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainFrag, mf).commit();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.select_valid_home_away_team), Toast.LENGTH_SHORT).show();
                }
            }
        });


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
