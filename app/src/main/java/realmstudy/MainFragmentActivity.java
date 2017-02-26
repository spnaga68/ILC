package realmstudy;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import realmstudy.data.CommanData;
import realmstudy.data.RealmObjectData.InningsData;
import realmstudy.data.RealmObjectData.Player;
import realmstudy.fragments.DialogFragment.NewPlayerDialog;
import realmstudy.fragments.DialogFragment.OutDialogFragment;
import realmstudy.fragments.DialogFragment.SelectMultiPlayerDialog;
import realmstudy.fragments.MenuActivity;
import realmstudy.interfaces.DialogInterface;
import realmstudy.fragments.DialogFragment.NewTeamDialog;
import realmstudy.fragments.DialogFragment.SelectPlayerDialog;
import realmstudy.interfaces.MsgFromDialog;
import realmstudy.interfaces.MsgToFragment;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by developer on 26/12/16.
 */
public class MainFragmentActivity extends AppCompatActivity implements MsgToFragment, MsgFromDialog {

    private FrameLayout content_frame, shadow;
    private android.support.v4.widget.DrawerLayout drawer_layout;
    private android.support.v7.widget.Toolbar tool_bar;
    private ImageView toolbar_logo;
    private TextView toolbar_title;
    private LinearLayout toolbar_titletm;
    private ImageView imageee;
    private ImageButton left_icon;
    private ImageButton right_icon;
    private TextView cancel_b;
    private android.support.v7.widget.SwitchCompat switch_right_icon;
    private FrameLayout mainFrag;
    private LinearLayout left_drawer;
    public Realm realm;
    private int dialogType;
    public static DialogInterface dialogInterface;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(realmstudy.R.layout.home_fragment);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .build();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            if (bundle.getString("fragmentToLoad") != null)
                setFragment(bundle.getString("fragmentToLoad"));

        realm = Realm.getInstance(config);
//        realm.beginTransaction();
//        realm.delete(InningsData.class);
//        realm.commitTransaction();
        content_frame = (FrameLayout) findViewById(realmstudy.R.id.content_frame);
        drawer_layout = (android.support.v4.widget.DrawerLayout) findViewById(realmstudy.R.id.drawer_layout);
        tool_bar = (android.support.v7.widget.Toolbar) findViewById(realmstudy.R.id.tool_bar);
        toolbar_logo = (ImageView) findViewById(realmstudy.R.id.toolbar_logo);
        toolbar_title = (TextView) findViewById(realmstudy.R.id.toolbar_title);
        toolbar_titletm = (LinearLayout) findViewById(realmstudy.R.id.toolbar_titletm);
        imageee = (ImageView) findViewById(realmstudy.R.id.imageee);
        left_icon = (ImageButton) findViewById(realmstudy.R.id.left_icon);
        right_icon = (ImageButton) findViewById(realmstudy.R.id.right_icon);
        cancel_b = (TextView) findViewById(realmstudy.R.id.cancel_b);
        switch_right_icon = (android.support.v7.widget.SwitchCompat) findViewById(realmstudy.R.id.switch_right_icon);
        mainFrag = (FrameLayout) findViewById(realmstudy.R.id.mainFrag);
        shadow = (FrameLayout) findViewById(realmstudy.R.id.shadow);
        left_drawer = (LinearLayout) findViewById(realmstudy.R.id.left_drawer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//     getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
        //  return super.onCreateOptionsMenu(menu);
    }

    private void setFragment(String id) {
        switch (id) {
            case CommanData.AddNewTeam:
                getSupportFragmentManager().beginTransaction()
                        .add(realmstudy.R.id.mainFrag, new MenuActivity())
                        .commit();
                break;
        }
    }


    public void homePage_title(String title) {
        toolbar_title.setVisibility(View.VISIBLE);
        toolbar_title.setText(title);
        toolbar_titletm.setVisibility(View.VISIBLE);
        toolbar_logo.setVisibility(View.GONE);


    }


    public void onClick(View v) {
        Fragment f = getSupportFragmentManager().findFragmentById(realmstudy.R.id.mainFrag);
        if (f instanceof MainActivity)
            ((MainActivity) f).onClick(v);
        else if (f instanceof MenuActivity)
            ((MenuActivity) f).onClick(v);

    }

    public Realm getRealm() {
        return realm;
    }

    /**
     * create and show dialog for add team and players
     *
     * @param type 0-->TeamDialog
     *             1-->PlayerDialog
     */
    public void showNewTeamDialog(int type, DialogInterface dialogInterface) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.

        this.dialogInterface = dialogInterface;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        System.out.println("_______DDDDDD__"+prev);
        if (prev != null) {
            System.out.println("_______DDDDDD__");
            ft.remove(prev);
          //  ((DialogFragment)prev).dismiss();
        }
       // ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment;
        if (type == 0)
            newFragment = NewTeamDialog.newInstance(0);
        else
            newFragment = NewPlayerDialog.newInstance();

        newFragment.show(ft, "dialog");


    }
    public void closePrevSelectPlayer(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        System.out.println("_________________prev"+prev);
        if (prev != null) {
            System.out.println("_________________prevrr"+prev);
            ft.remove(prev);
            ((DialogFragment)prev).dismiss();
        }
    }

    public void showSelectplayer(int match_id, boolean ishomeTeam, Player current_bowler, String title) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.

        this.dialogInterface = dialogInterface;

       // ft.addToBackStack(null);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        closePrevSelectPlayer();
        int cb = -1;
        if (current_bowler != null)
            cb = current_bowler.getpID();
        // Create and show the dialog.
        DialogFragment newFragment = SelectPlayerDialog.newInstance(match_id, ishomeTeam, cb, title);
        // ((SelectPlayerDialog) newFragment).setDialogInterface(dialogInterface, match_id, ishomeTeam, current_bowler,title);
//        if (type == 0)
//            newFragment = NewTeamDialog.newInstance();
//        else
//            newFragment = NewPlayerDialog.newInstance().setDialogInterface(dialogInterface);

        newFragment.show(ft, "dialog");


    }

    public void showOutDialog(int striker, int non_striker, int assignToPlayer, int matchDetails) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.

        this.dialogInterface = dialogInterface;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = OutDialogFragment.newInstance(striker, non_striker, assignToPlayer, matchDetails);
        //   ((SelectMultiPlayerDialog) newFragment).setDialogInterface(dialogInterface, match_id, ishomeTeam, current_bowler);


        newFragment.show(ft, "dialog");


    }

    public void showMultiPlayerSelect(int match_id, boolean ishomeTeam, int current_bowler) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.

        this.dialogInterface = dialogInterface;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = SelectMultiPlayerDialog.newInstance(match_id, ishomeTeam);
        //   ((SelectMultiPlayerDialog) newFragment).setDialogInterface(dialogInterface, match_id, ishomeTeam, current_bowler);


        newFragment.show(ft, "dialog");


    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment ff = getSupportFragmentManager().findFragmentByTag("dialog");
        System.out.println("_____PP" + 1);
        if (ff != null)
            ff.onActivityResult(requestCode, resultCode, data);
        else if (getSupportFragmentManager().findFragmentById(R.id.mainFrag) != null)
            getSupportFragmentManager().findFragmentById(R.id.mainFrag).onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void msg(String s) {
        Fragment ff = getSupportFragmentManager().findFragmentById(realmstudy.R.id.mainFrag);
        if (ff instanceof MsgToFragment) {
            ((MsgToFragment) ff).msg(s);
        }
    }

    @Override
    public void messageFromDialog(int dialogType, boolean success, String data,String message) {
        Fragment ff = getSupportFragmentManager().findFragmentById(realmstudy.R.id.mainFrag);
        if (ff instanceof MsgFromDialog) {
            ((MsgFromDialog) ff).messageFromDialog(dialogType, success,data, message);
        }
    }
}
