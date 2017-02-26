package realmstudy.adapter;

/**
 * Created by developer on 27/12/16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import realmstudy.MainFragmentActivity;
import realmstudy.R;
import realmstudy.data.RealmObjectData.Player;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class PlayerListAdapter extends RealmRecyclerViewAdapter<Player, PlayerListAdapter.MyViewHolder> {

    private Context context;


    public PlayerListAdapter(Context activity, OrderedRealmCollection<Player> data) {
        super(activity,data,true);
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
        Player obj = getData().get(position);
        holder.data = obj;
        holder.title.setText(obj.getName());
        holder.ph_no.setText(obj.getPh_no());
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView title,ph_no;
        public Player data;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.playername);
            ph_no= (TextView) view.findViewById(R.id.ph_no);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
           // context.deleteItem(data);
            return true;
        }
    }
}