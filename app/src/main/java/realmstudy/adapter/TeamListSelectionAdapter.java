package realmstudy.adapter;

/**
 * Created by developer on 26/12/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import realmstudy.R;
import realmstudy.data.RealmObjectData.Team;
import realmstudy.interfaces.MsgToFragment;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class TeamListSelectionAdapter extends RealmBaseAdapter<Team> implements ListAdapter {

    int home_id = -1, away_id = -1;

    private static class ViewHolder {
        TextView team_name;
        CheckBox home_radio, away_radio;
    }

    public TeamListSelectionAdapter(Context c, OrderedRealmCollection<Team> realmResults) {
        super(c, realmResults);

    }


//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if(position==0)
//            return 0;
//        else
//            return  1;
//    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.team_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.team_name = (TextView) convertView.findViewById(R.id.team_name);
            viewHolder.home_radio = (CheckBox) convertView.findViewById(R.id.home_radio);
            viewHolder.away_radio = (CheckBox) convertView.findViewById(R.id.away_radio);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Team item = adapterData.get(position);
        viewHolder.team_name.setText(item.name);
        if (position == home_id) {
            viewHolder.home_radio.setChecked(true);
        } else {
            viewHolder.home_radio.setChecked(false);
        }
        if (position == away_id) {
            viewHolder.away_radio.setChecked(true);
        } else {
            viewHolder.away_radio.setChecked(false);
        }
        viewHolder.home_radio.setTag(position);
        viewHolder.away_radio.setTag(position);
        viewHolder.home_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox c = (CheckBox) view;
                int id = (int) view.getTag();
                if (!c.isChecked()) {
                    home_id = -1;
                } else {
                    if (id != away_id)
                        home_id = id;
                    else {
                        //  c.setChecked(false);
                        Toast.makeText(context, "no same", Toast.LENGTH_SHORT).show();
                    }
                }
                //  System.out.println("___________PPP"+parent.getClass().getCanonicalName());
                setHeaderText();
                notifyDataSetChanged();
            }
        });
        viewHolder.away_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CheckBox c = (CheckBox) view;
                int id = (int) view.getTag();
                if (!c.isChecked()) {
                    away_id = -1;
                } else {
                    if (id != home_id)
                        away_id = id;
                    else {
                        //  ((CheckBox) view).setChecked(false);
                        Toast.makeText(context, "no same", Toast.LENGTH_SHORT).show();
                    }
                }
                setHeaderText();
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private void setHeaderText() {
        String ss = "";
        //  TextView view = (TextView) lv.findViewById(R.id.header);
        String homeTeam = context.getString(R.string.home_team);
        String awayTeam = context.getString(R.string.away_team);
        //if (view != null) {
        if (home_id != -1)
            homeTeam = adapterData.get(home_id).name;
        if (away_id != -1)
            awayTeam = adapterData.get(away_id).name;
        ss = homeTeam + " vs " + awayTeam;
        if (context instanceof MsgToFragment) {
            ((MsgToFragment) context).msg(ss);
        }
        // }

    }

    public String selectedItem() {
        if (home_id != -1 && away_id != -1)
            return String.valueOf(adapterData.get(home_id).team_id) + "__" + String.valueOf(adapterData.get(away_id).team_id);
        else
            return "";
    }
}
