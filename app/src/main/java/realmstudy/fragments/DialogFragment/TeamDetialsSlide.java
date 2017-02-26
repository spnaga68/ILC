//package com.example.developer.realmstudy.fragments.DialogFragment;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.developer.realmstudy.R;
//import TeamViewPagerAdapter;
//import SlidingTabLayout;
//
///**
// * Created by developer on 7/1/17.
// */
//public class TeamDetialsSlide extends Fragment {
//    private TeamViewPagerAdapter adapter;
//    CharSequence Titles[] = {"TeamA", "TeamB"};
//    int Numboftabs = 2;
//    ViewPager pager;
//    SlidingTabLayout tabs;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.team_details, container, false);
//
//
//        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
//        adapter = new TeamViewPagerAdapter(getActivity().getSupportFragmentManager(), Titles, Numboftabs);
//        // Assigning ViewPager View and setting the adapter
//        pager = (ViewPager) v.findViewById(R.id.pager);
//        pager.setAdapter(adapter);
//
//        // Assiging the Sliding Tab Layout View
//        tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
//        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
//
//        // Setting Custom Color for the Scroll bar indicator of the Tab View
//        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
//            @Override
//            public int getIndicatorColor(int position) {
//                return getResources().getColor(R.color.colorAccent);
//            }
//        });
//
//        // Setting the ViewPager For the SlidingTabsLayout
//        tabs.setViewPager(pager);
//        return v;
//    }
//}
