package realmstudy.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

/**
         * Created by developer on 7/1/17.
         */
import realmstudy.fragments.TeamADetails;


/**
 *   Created by hp1 on 21-01-2015.
 *  
 */
public class TeamViewPagerAdapter extends FragmentStatePagerAdapter {
    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    // Build a Constructor and assign the passed Values to appropriate values in the class
    private Fragment mCurrentFragment;
    public TeamViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }



    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }
    //This method return the fragment for the every position in the View Pager
    @Override

    public Fragment getItem(int position) {
        if (position == 0) // if the position is 0 we are returning the First tab
        {
            TeamADetails tab1 = new TeamADetails();
            Bundle s=new Bundle();
            s.putInt("team",0);
            tab1.setArguments(s);

            return tab1;
        }
        // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        else {
            TeamADetails tab2 = new TeamADetails();
            Bundle s=new Bundle();
            s.putInt("team",1);
            tab2.setArguments(s);

            return tab2;
        }

    }
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override

    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override


    public int getCount() {
        return NumbOfTabs;
    }
}