package realmstudy.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
import realmstudy.MainActivity;
import realmstudy.MainFragmentActivity;
import realmstudy.MyApplication;
import realmstudy.R;
import realmstudy.data.CommanData;
import realmstudy.data.RealmObjectData.BatingProfile;
import realmstudy.data.RealmObjectData.BowlingProfile;
import realmstudy.data.RealmObjectData.InningsData;
import realmstudy.data.RealmObjectData.MatchDetails;
import realmstudy.databaseFunctions.RealmDB;
import realmstudy.fragments.TossFragment;
import realmstudy.interfaces.SlideRecyclerView;

/**
 * Created by developer on 21/2/17.
 */


public class SavedGameListAdapter extends RecyclerView.Adapter implements SlideRecyclerView {
    private Context context;
    private static final int PENDING_REMOVAL_TIMEOUT = 1500; // 3sec

    List<String> items;
    List<String> itemsPendingRemoval;
    int lastInsertedIndex; // so we can add some more items for testing purposes
    boolean undoOn = true; // is undo on, you can turn it on from the toolbar menu

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    RealmResults<MatchDetails> data;
    @Inject
    Realm realm;

    public SavedGameListAdapter(Context context, RealmResults<MatchDetails> data) {
        this.data = data;
        items = new ArrayList<>();
        itemsPendingRemoval = new ArrayList<>();
        lastInsertedIndex = data.size();
        ((MyApplication) ((Activity) context).getApplication()).getComponent().inject(this);
        // let's generate some items
        SavedGameListAdapter.this.context = context;
        for (int i = 0; i < data.size(); i++) {
            items.add(String.valueOf(data.get(i).getMatch_id()));

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TestViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TestViewHolder viewHolder = (TestViewHolder) holder;
        final String item = items.get(position);

        if (itemsPendingRemoval.contains(item)) {
            // we need to show the "undo" state of the row
            viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            viewHolder.titleView.setVisibility(View.GONE);
            viewHolder.undoButton.setVisibility(View.VISIBLE);
            viewHolder.titleView.setTag(position);
            viewHolder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                    pendingRunnables.remove(item);
                    if (pendingRemovalRunnable != null)
                        handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(item);
                    // this will rebind the row in "normal" state
                    notifyItemChanged(items.indexOf(item));
                }
            });
        } else {
            // we need to show the "normal" state
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            viewHolder.titleView.setVisibility(View.VISIBLE);
            // viewHolder.titleView.setText(item);
            viewHolder.venue.setText(data.get(position).getLocation());
            viewHolder.home_team_name.setText(data.get(position).getHomeTeam().nick_name);
            viewHolder.away_team_name.setText(data.get(position).getAwayTeam().nick_name);
            viewHolder.time.setText(CommanData.getDateCurrentTimeZone(data.get(position).getTime()));
            viewHolder.undoButton.setVisibility(View.GONE);
            viewHolder.undoButton.setOnClickListener(null);
            viewHolder.titleView.setTag(position);

            if (data.get(position).getMatchStatus() == CommanData.MATCH_COMPLETED) {
                viewHolder.status.setText(context.getString(R.string.completed));
                viewHolder.status.setTextColor(ContextCompat.getColor(context,R.color.red_M));
            } else if (data.get(position).getMatchStatus() == CommanData.MATCH_NOT_YET_STARTED) {
                viewHolder.status.setText(context.getString(R.string.match_not_started));
                viewHolder.status.setTextColor(ContextCompat.getColor(context,R.color.yellow_M));
            } else {
                viewHolder.status.setText(context.getString(R.string.on_going));
                viewHolder.status.setTextColor(ContextCompat.getColor(context,R.color.green_M));
            }
            viewHolder.titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MatchDetails md = RealmDB.getMatchById(context, realm, data.get(position).getMatch_id());
                    if (md.getToss() == null) {
                        Bundle b = new Bundle();
                        TossFragment fragment = new TossFragment();
                        b.putInt("match_id", md.getMatch_id());
                        b.putString("teamIDs", String.valueOf(md.getHomeTeam().team_id) + "__" + String.valueOf(md.getAwayTeam().team_id));
                        fragment.setArguments(b);
                        ((MainFragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.mainFrag, fragment).commit();

                    } else if (md.getMatchStatus() != CommanData.MATCH_COMPLETED) {
                        Bundle b = new Bundle();
                        MainActivity fragment = new MainActivity();
                        b.putInt("match_id", md.getMatch_id());
                        // Toast.makeText(context,  String.valueOf(md.getMatch_id()), Toast.LENGTH_SHORT).show();
                        fragment.setArguments(b);
                        ((MainFragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.mainFrag, fragment).commit();
                    } else
                        Toast.makeText(context, context.getString(R.string.game_over), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        final String item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(items.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        //  Toast.makeText(context, "dsr", Toast.LENGTH_SHORT).show();
        String item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
            items.remove(position);
            notifyItemRemoved(position);
        }
        System.out.println("_______________II" + item + "___" + position);
        realm.beginTransaction();
        MatchDetails md = realm.where(MatchDetails.class).equalTo("match_id", Integer.parseInt(item)).findFirst();
        RealmResults<InningsData> inningsDatas= realm.where(InningsData.class).equalTo("match_id", Integer.parseInt(item)).findAll();
        RealmResults<BowlingProfile> bowlingProfiles= realm.where(BowlingProfile.class).equalTo("match_id", Integer.parseInt(item)).findAll();
        RealmResults<BatingProfile> battingProfiles= realm.where(BatingProfile.class).equalTo("match_id", Integer.parseInt(item)).findAll();
        if (md != null)
            md.deleteFromRealm();
        if(inningsDatas!=null)
            inningsDatas.deleteAllFromRealm();
        if(bowlingProfiles!=null)
            bowlingProfiles.deleteAllFromRealm();
        if(battingProfiles!=null)
            battingProfiles.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public boolean isPendingRemoval(int position) {
        String item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }
}

/**
 * ViewHolder capable of presenting two states: "normal" and "undo" state.
 */
class TestViewHolder extends RecyclerView.ViewHolder {

    realmstudy.lib.customViews.CircleImageView
            home_team_image, away_team_image;
    realmstudy.lib.customViews.SemiLargeTextView home_team_name;
    realmstudy.lib.customViews.SemiLargeTextView away_team_name;
    TextView venue, status;
    TextView time;
    RelativeLayout titleView;
    Button undoButton;


    public TestViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_list_item, parent, false));
        titleView = (RelativeLayout) itemView.findViewById(R.id.titleView);
        undoButton = (Button) itemView.findViewById(R.id.undo_button);
        status = (TextView) itemView.findViewById(R.id.status);
        home_team_image = (realmstudy.lib.customViews.CircleImageView) itemView.findViewById(R.id.home_team_image);
        home_team_name = (realmstudy.lib.customViews.SemiLargeTextView) itemView.findViewById(R.id.home_team_name);
        away_team_name = (realmstudy.lib.customViews.SemiLargeTextView) itemView.findViewById(R.id.away_team_name);
        venue = (TextView) itemView.findViewById(R.id.venue);
        time = (TextView) itemView.findViewById(R.id.time);
        away_team_image = (realmstudy.lib.customViews.CircleImageView) itemView.findViewById(R.id.away_team_image);
    }

}



