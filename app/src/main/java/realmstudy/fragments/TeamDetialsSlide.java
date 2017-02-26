package realmstudy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import realmstudy.data.SessionSave;
import realmstudy.interfaces.DialogInterface;
import realmstudy.MainFragmentActivity;
import realmstudy.R;
import realmstudy.adapter.TeamViewPagerAdapter;
import realmstudy.data.RealmObjectData.MatchDetails;
import realmstudy.databaseFunctions.RealmDB;
import realmstudy.extras.SlidingTabLayout;

import io.realm.Realm;
import realmstudy.interfaces.MsgToFragment;

/**
 * Created by developer on 7/1/17.
 */
public class TeamDetialsSlide extends Fragment implements MsgToFragment {
    private TeamViewPagerAdapter adapter;
    CharSequence Titles[] = {"TeamA", "TeamB"};
    int Numboftabs = 2;
    ViewPager pager;
    SlidingTabLayout tabs;
    private FloatingActionButton add_team_player;
    private MatchDetails matchDetails;
    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.both_team_players, container, false);

        add_team_player = (FloatingActionButton) v.findViewById(R.id.add_team_player);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.

        realm = ((MainFragmentActivity) (getActivity())).getRealm();
        try {
            Bundle b = getArguments();
            int match_id = b.getInt("match_id");

            matchDetails = RealmDB.getMatchById(getActivity(), realm, match_id);
            Titles=new String[2];
            Titles[0]=matchDetails.getHomeTeam().name;
            Titles[1]=matchDetails.getAwayTeam().name;
            SessionSave.saveSession("DetailForMAtch",match_id,getActivity());
            System.out.println("TeamName____" + matchDetails.getHomeTeam().name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter = new TeamViewPagerAdapter(getActivity().getSupportFragmentManager(), Titles, Numboftabs);
        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorPrimaryDark);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        add_team_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pager.getCurrentItem();
                int id = pager.getCurrentItem();
                ;
                System.out.println("_____working" + pager.getCurrentItem());
                if (id == 0)
                    ((MainFragmentActivity) getActivity()).showMultiPlayerSelect(matchDetails.getMatch_id(), true, 0);
                else
                    ((MainFragmentActivity) getActivity()).showMultiPlayerSelect(matchDetails.getMatch_id(), false, 0);
            }
        });
        return v;
    }

//    @Override
//    public void onSuccess(String result, boolean success) {
//        System.out.println("_____working" + result);
//        Fragment page = adapter.getCurrentFragment();
//        if (page != null) {
//            ((TeamADetails) page).notifyDataChanged(getActivity());
//        }
//    }

    @Override
    public void msg(String s) {
       // RealmDB.
        Fragment page = adapter.getCurrentFragment();
        if (page != null) {
            ((TeamADetails) page).notifyDataChanged(getActivity());
        }
    }
}
