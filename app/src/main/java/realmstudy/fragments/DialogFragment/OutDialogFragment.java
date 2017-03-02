package realmstudy.fragments.DialogFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import realmstudy.MainFragmentActivity;
import realmstudy.R;
import realmstudy.data.CommanData;
import realmstudy.data.RealmObjectData.InningsData;
import realmstudy.data.RealmObjectData.MatchDetails;
import realmstudy.data.RealmObjectData.Player;
import realmstudy.data.ScoreBoardData;
import realmstudy.databaseFunctions.RealmDB;
import realmstudy.interfaces.MsgFromDialog;
import realmstudy.mainFunctions.GRadioGroup;

/**
 * Created by developer on 23/2/17.
 */
public class OutDialogFragment extends DialogFragment {

    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 430;
    private static final int PICK_CONTACT = 360;
    private Realm realm;
    private Spinner caught_by;
    private Spinner run_out_by;
    private int current_bowler_id;
    MatchDetails matchDetails;
    private int striker;
    private int non_striker;
    int current_bowler;
    private ScoreBoardData current_score_data;


    public static OutDialogFragment newInstance(int striker, int non_striker, int current_bowler_id, int matchDetails) {
        OutDialogFragment f = new OutDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("striker", striker);
        args.putInt("matchDetails", matchDetails);
        args.putInt("non_striker", non_striker);
        // args.putInt("assignToPlayer", assignToPlayer);
        args.putInt("current_bowler_id", current_bowler_id);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // int match_id = getArguments().getInt("match_id");
        striker = getArguments().getInt("striker");
        int match_id = getArguments().getInt("matchDetails");
        non_striker = getArguments().getInt("non_striker");
        current_bowler_id = getArguments().getInt("current_bowler_id");

        Realm.init(getActivity());
        RealmConfiguration config = new RealmConfiguration.Builder()
                .build();
        realm = Realm.getInstance(config);
        // current_bowler=RealmDB.getPlayer(getActivity(),realm,current_bowler_id);
        matchDetails = RealmDB.getMatchById(getActivity(), realm, match_id);
        current_score_data = CommanData.fromJson(realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).findAll().sort("index").last().getScoreBoardData(), ScoreBoardData.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.out_dialog, null);
        init(v, getString(R.string.wicket));
        return v;
    }

    private void init(View v, String title_txt) {
        RadioButton
                caught, lbw, bowled, runnout, hitwicket;
        final LinearLayout caught_by_lay;

        final LinearLayout run_out_lay;
        Spinner runs_scored_spinner;

        final Spinner wicket_of;
        android.support.v7.widget.AppCompatButton submit;
        final GRadioGroup rg = new GRadioGroup();


//        if (v != null)
//            v.dismiss();
        realm = ((MainFragmentActivity) (getActivity())).getRealm();
        RealmResults<Player> players = realm.where(Player.class).findAll();

//
//        ArrayAdapter<Player> oppPlayer;
//        oppPlayer = new ArrayAdapter<>(
//                getActivity(), R.layout.player_spinner_item, players);


// Create the AlertDialog


        caught = (RadioButton) v.findViewById(realmstudy.R.id.caught);
        lbw = (RadioButton) v.findViewById(realmstudy.R.id.lbw);
        bowled = (RadioButton) v.findViewById(realmstudy.R.id.bowled);
        runnout = (RadioButton) v.findViewById(realmstudy.R.id.runnout);
        hitwicket = (RadioButton) v.findViewById(realmstudy.R.id.hitwicket);
        caught_by_lay = (LinearLayout) v.findViewById(realmstudy.R.id.caught_by_lay);
        caught_by = (Spinner) v.findViewById(realmstudy.R.id.caught_by);
        run_out_lay = (LinearLayout) v.findViewById(realmstudy.R.id.run_out_lay);
        runs_scored_spinner = (Spinner) v.findViewById(realmstudy.R.id.runs_scored_spinner);
        run_out_by = (Spinner) v.findViewById(realmstudy.R.id.run_out_by);
        wicket_of = (Spinner) v.findViewById(realmstudy.R.id.wicket_of);
        submit = (android.support.v7.widget.AppCompatButton) v.findViewById(realmstudy.R.id.submit);

        TextView from_contacts = (TextView) v.findViewById(realmstudy.R.id.from_contacts);
        final TextView name = (TextView) v.findViewById(realmstudy.R.id.name);
        final TextView ph_no = (TextView) v.findViewById(realmstudy.R.id.ph_no);

        AppCompatButton submit_new_player = (android.support.v7.widget.AppCompatButton) v.findViewById(realmstudy.R.id.submit_new_player);

        from_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  assignToPlayer = 5;
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                        ) {
                    // PlayerListFragment
                    requestPermissions(
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_CONTACTS);
                } else {
                    //   selectPlayerDialog.dismiss();

                    pickFromContacts();
                }
            }
        });

        submit_new_player.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if (!name.getText().toString().isEmpty()) {
                    //     assignToPlayer = 5;
                    int id = newPlayerAdded(name.getText().toString(), ph_no.getText().toString());
//                    if (assignToPlayer == 5) {
//                        ArrayList<Player> bowlingTeamPlayers = getBowlingTeamPlayer();
//                        ArrayAdapter<Player> bowling_team_player_adapter = new ArrayAdapter<>(
//                                getActivity(), realmstudy.R.layout.player_spinner_item, bowlingTeamPlayers);
//                        if (caught_by != null && run_out_by != null) {
//                            System.out.println();
//                            caught_by.setAdapter(bowling_team_player_adapter);
//                            run_out_by.setAdapter(bowling_team_player_adapter);
//                            int ids = 0;
//                            for (int i = 0; i < bowlingTeamPlayers.size(); i++) {
//                                if (bowlingTeamPlayers.get(i).getpID() == id)
//                                    ids = i;
//                            }
//                            caught_by.setSelection(ids);
//                            run_out_by.setSelection(ids);
//                        }
//                    }
                }
            }
        });


        ArrayList<Player> bowlingTeamPlayers = null;


//        if (!matchDetails.getBowlingTeamPlayer().trim().isEmpty()) {
//            bowlingTeamPlayers = new ArrayList<>();
//            String s[] = matchDetails.getBowlingTeamPlayer().split(",");
//            for (int i = 0; i < s.length; i++) {
//                bowlingTeamPlayers.add(RealmDB.getPlayer(getActivity(), realm, Integer.parseInt(s[i])));
//
//            }
//        }
        bowlingTeamPlayers = getBowlingTeamPlayer();
        ArrayList<Player> bat = new ArrayList<>();
        bat.add(RealmDB.getPlayer(getActivity(), realm, striker));
        bat.add(RealmDB.getPlayer(getActivity(), realm, non_striker));
        ArrayAdapter<Player> batters;
        batters = new ArrayAdapter<>(getActivity(), realmstudy.R.layout.player_spinner_item, bat);
        wicket_of.setAdapter(batters);
        if (bowlingTeamPlayers != null) {
            ArrayAdapter<Player> bowling_team_player_adapter = new ArrayAdapter<>(
                    getActivity(), realmstudy.R.layout.player_spinner_item, bowlingTeamPlayers);
            if (caught_by != null) {
                caught_by.setAdapter(bowling_team_player_adapter);
                run_out_by.setAdapter(bowling_team_player_adapter);
            } else
                System.out.println("___________null");

        }

        RadioButton[] rb = {caught, lbw, bowled, runnout, hitwicket};

        rg.createRadioGroup(rb);

        caught.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    caught_by_lay.setVisibility(View.VISIBLE);
                    run_out_lay.setVisibility(View.GONE);
                }
            }
        });
        caught.setChecked(true);

        lbw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    caught_by_lay.setVisibility(View.GONE);
                    run_out_lay.setVisibility(View.GONE);
                }
            }
        });

        bowled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    caught_by_lay.setVisibility(View.GONE);
                    run_out_lay.setVisibility(View.GONE);
                }
            }
        });
        runnout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    caught_by_lay.setVisibility(View.GONE);
                    run_out_lay.setVisibility(View.VISIBLE);
                }
            }
        });
        hitwicket.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    caught_by_lay.setVisibility(View.GONE);
                    run_out_lay.setVisibility(View.GONE);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                RadioButton s = rg.getCheckedItem();

                String w = null;


                switch (s.getTag().toString()) {
                    case "caught":
                        w = RealmDB.wicketCaught(getActivity(), realm, striker, current_bowler_id, CommanData.W_CAUGHT,
                                (int) caught_by.getSelectedItem(), current_score_data.getTotalOver(), matchDetails.getMatch_id());

                        break;
                    case "lbw":
                        w = RealmDB.wicketOther(getActivity(), realm, striker, current_bowler_id, CommanData.W_LBW,
                                current_score_data.getTotalOver(), matchDetails.getMatch_id());

                        break;
                    case "bowled":
                        w = RealmDB.wicketOther(getActivity(), realm, striker, current_bowler_id, CommanData.W_BOWLED,
                                current_score_data.getTotalOver(), matchDetails.getMatch_id());

                        break;
                    case "runout":
                        w = RealmDB.wicketRunout(getActivity(), realm, (int) wicket_of.getSelectedItem(), current_bowler_id, CommanData.W_RUNOUT, (int) run_out_by.getSelectedItem(),
                                current_score_data.getTotalOver(), matchDetails.getMatch_id());

                        break;
                    case "hitout":
                        w = RealmDB.wicketOther(getActivity(), realm, striker, current_bowler, CommanData.W_HITOUT,
                                current_score_data.getTotalOver(), matchDetails.getMatch_id());

                        break;

                }
                if (w != null)
                    dismiss();
               // System.out.println("_____Out"+CommanData.DIALOG_OUT+"__"+w+"__");
                ((MsgFromDialog) getActivity()).messageFromDialog(CommanData.DIALOG_OUT, w != null, w, "");
            }


        });


    }

    private void pickFromContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    int newPlayerAdded(String name, String ph_no) {
        Player dummy = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
        ((MainFragmentActivity) getActivity()).messageFromDialog(CommanData.DIALOG_OUT, true, String.valueOf(dummy.getpID()), "Success");

//        if (assignToPlayer == 0) {
//            BatingProfile bf = RealmDB.createBattingProfile(getActivity(), realm);
//            striker = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
//            realm.beginTransaction();
//            if (matchDetails.isHomeTeamBatting())
//                matchDetails.addHomePlayer(striker);
//            else
//                matchDetails.addAwayPlayer(striker);
//            bf.setCurrentStatus(CommanData.StatusBatting);
//            striker.setRecentBatingProfile(bf);
//            realm.commitTransaction();
//        } else if (assignToPlayer == 1) {
//            BatingProfile bf = RealmDB.createBattingProfile(getActivity(), realm);
//            non_striker = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
//            realm.beginTransaction();
//            if (matchDetails.isHomeTeamBatting())
//                matchDetails.addHomePlayer(non_striker);
//            else
//                matchDetails.addAwayPlayer(non_striker);
//            bf.setCurrentStatus(CommanData.StatusBatting);
//            non_striker.setRecentBatingProfile(bf);
//            realm.commitTransaction();
//        } else if (assignToPlayer == 2) {
//            current_bowler = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
//            BowlingProfile bf = RealmDB.createBowlingProfile(getActivity(), realm);
//            realm.beginTransaction();
//            if (matchDetails.isHomeTeamBatting())
//                current_bowler = matchDetails.addAwayPlayer(current_bowler);
//            else
//                current_bowler = matchDetails.addHomePlayer(current_bowler);
//            current_bowler.setRecentBowlingProfile(bf);
//            realm.commitTransaction();
//        } else if (assignToPlayer == 3) {
//            next_bowler = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
//            BowlingProfile bf = RealmDB.createBowlingProfile(getActivity(), realm);
//            realm.beginTransaction();
//            if (matchDetails.isHomeTeamBatting())
//                current_bowler = matchDetails.addAwayPlayer(next_bowler);
//            else
//                current_bowler = matchDetails.addHomePlayer(next_bowler);
//            next_bowler.setRecentBowlingProfile(bf);
//            realm.commitTransaction();
//        } else if (assignToPlayer == 4) {
//            next_bowler = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
//            BowlingProfile bf = RealmDB.createBowlingProfile(getActivity(), realm);
//            realm.beginTransaction();
//            if (matchDetails.isHomeTeamBatting())
//                current_bowler = matchDetails.addAwayPlayer(next_bowler);
//            else
//                current_bowler = matchDetails.addHomePlayer(next_bowler);
//            next_bowler.setRecentBowlingProfile(bf);
//            realm.commitTransaction();
//        } else if (assignToPlayer == 5) {
//            dummy = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
//            //  BowlingProfile bf = RealmDB.createBowlingProfile(getActivity(), realm);
//            realm.beginTransaction();
//            matchDetails.addAwayPlayer(dummy);
//            //  next_bowler.setRecentBowlingProfile(bf);
//            realm.commitTransaction();
//        }

        //   updateUI();
        if (dummy != null)
            return dummy.getpID();
        else
            return -1;
    }

    ArrayList<Player> getBowlingTeamPlayer() {
        ArrayList<Player> bowlingTeamPlayers = new ArrayList<>();
        if (matchDetails.getBowlingTeamPlayer() != null) {

            //    String s[] = matchDetails.getBowlingTeamPlayer().split(",");
            for (int i = 0; i < matchDetails.getBowlingTeamPlayer().size(); i++) {
                bowlingTeamPlayers.add(RealmDB.getPlayer(getActivity(), realm, (matchDetails.getBowlingTeamPlayer().get(i).getpID())));

            }
            return bowlingTeamPlayers;
        } else
            return null;

    }

}
