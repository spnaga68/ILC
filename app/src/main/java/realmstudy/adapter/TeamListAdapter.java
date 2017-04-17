package realmstudy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import realmstudy.R;
import realmstudy.data.RealmObjectData.Team;

/**
 * Created by developer on 21/2/17.
 */

public class TeamListAdapter extends RealmRecyclerViewAdapter<Team, TeamListAdapter.MyViewHolder> {

    private Context context;


    public TeamListAdapter(Context activity, OrderedRealmCollection<Team> realmResults) {
        super(activity,realmResults,true);
        this.context = activity;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Team obj = getData().get(position);
        holder.data = obj;
        holder.title.setText(obj.name);
        holder.ph_no.setText(obj.nick_name);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView title,ph_no;
        public Team data;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.playername);
            ph_no= (TextView) view.findViewById(R.id.time);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            // context.deleteItem(data);
            return true;
        }
    }
}