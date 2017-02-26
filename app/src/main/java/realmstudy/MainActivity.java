package realmstudy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import realmstudy.data.CommanData;
import realmstudy.data.RealmObjectData.BatingProfile;
import realmstudy.data.RealmObjectData.BowlingProfile;
import realmstudy.data.RealmObjectData.InningsData;
import realmstudy.data.RealmObjectData.MatchDetails;
import realmstudy.data.RealmObjectData.Player;
import realmstudy.data.ScoreBoardData;
import realmstudy.databaseFunctions.RealmDB;
import realmstudy.interfaces.DialogInterface;
import realmstudy.interfaces.MsgFromDialog;
import realmstudy.interfaces.MsgToFragment;
import realmstudy.service.CoreClient;
import realmstudy.service.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends Fragment implements DialogInterface, MsgToFragment, MsgFromDialog {

    private static final int COLOR_SELECT = Color.RED;
    private static final int COLOR_UNSELECT = Color.BLACK;
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 420;
    private static final int PICK_CONTACT = 421;
    private boolean IS_FIRST_INNINGS = true;
    private Realm realm;
    private ScoreBoardData current_score_data;
    private Player striker, non_striker;
    private Player current_bowler, next_bowler;
    private TextView num_balls_txt, total_runs_txt;
    private TextView dot_txt, one_run_txt,
            two_run_txt, three_run_txt, four_run_txt, bfour_txt, bSix_txt;
    private TextView wide_txt, no_ball_txt, byes_txt, leg_byes_txt, granted_txt, legal_ball_txt;

    LinearLayout next_bowler_lay, current_bowler_lay;
    private int runs = 0;
    private boolean legal = true;
    private AppCompatButton submit, out;
    private ScoreBoardData r1;
    private InningsData lastInningsDataItem;
    private TextView striker_score, non_striker_score, striker_balls, non_striker_balls;
    private TextView striker_name, non_striker_name;
    public static int legalRun = 1;
    ImageButton undo, redo;
    private int undoCount, redoCount;
    private TextView current_bowler_name, current_bowler_overs, current_bowler_runs, next_bowler_name, next_bowler_overs, next_bowler_runs;
    //temp variable holds address of player(i.e striker,bowler)
    /**
     * 0-->striker
     * 1-->non_striker
     * 2-->current_bowler
     * 3-->next_bowler
     */
    private int assignToPlayer;
    private Dialog selectPlayerDialog;
    private AlertDialog outDialog;

    @Override
    public void msg(String s) {

    }


    private enum typeExtraEnum {WIDE, NO_BALL, BYES, LEG_BYES, STEP_NO_BALL, GRANTED}

    private typeExtraEnum extraType;
    MatchDetails matchDetails;
    Spinner caught_by;
    Spinner run_out_by;
    // ArrayAdapter<Player> bowling_team_player_adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(realmstudy.R.layout.homepage_drawer, container, false);


        realm = ((MainFragmentActivity) (getActivity())).getRealm();


        try {
            Bundle b = getArguments();
            int match_id = b.getInt("match_id");

            matchDetails = RealmDB.getMatchById(getActivity(), realm, match_id);

            resumeMatch(matchDetails);
            System.out.println("TeamName____" + matchDetails.getHomeTeam().name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initialize(v);


        return v;
    }

    private void initialize(View v) {
        undo = (ImageButton) v.findViewById(realmstudy.R.id.undo);
        redo = (ImageButton) v.findViewById(realmstudy.R.id.redo);
        submit = (AppCompatButton) v.findViewById(realmstudy.R.id.submit);
        out = (AppCompatButton) v.findViewById(realmstudy.R.id.out);
        num_balls_txt = (TextView) v.findViewById(realmstudy.R.id.num_balls_txt);
        total_runs_txt = (TextView) v.findViewById(realmstudy.R.id.total_runs_txt);
        dot_txt = (TextView) v.findViewById(realmstudy.R.id.dot_txt);
        one_run_txt = (TextView) v.findViewById(realmstudy.R.id.one_run_txt);
        two_run_txt = (TextView) v.findViewById(realmstudy.R.id.two_run_txt);
        three_run_txt = (TextView) v.findViewById(realmstudy.R.id.three_run_txt);
        four_run_txt = (TextView) v.findViewById(realmstudy.R.id.four_run_txt);
        bfour_txt = (TextView) v.findViewById(realmstudy.R.id.bfour_txt);
        bSix_txt = (TextView) v.findViewById(realmstudy.R.id.bSix_txt);

        wide_txt = (TextView) v.findViewById(realmstudy.R.id.wide_txt);
        no_ball_txt = (TextView) v.findViewById(realmstudy.R.id.no_ball_txt);
        byes_txt = (TextView) v.findViewById(realmstudy.R.id.byes_txt);
        leg_byes_txt = (TextView) v.findViewById(realmstudy.R.id.leg_byes_txt);
        granted_txt = (TextView) v.findViewById(realmstudy.R.id.granted_txt);
        legal_ball_txt = (TextView) v.findViewById(realmstudy.R.id.legal_ball_txt);


        striker_score = (TextView) v.findViewById(realmstudy.R.id.striker_score);
        non_striker_score = (TextView) v.findViewById(realmstudy.R.id.non_striker_score);
        striker_balls = (TextView) v.findViewById(realmstudy.R.id.striker_balls);
        non_striker_balls = (TextView) v.findViewById(realmstudy.R.id.non_striker_balls);
        striker_name = (TextView) v.findViewById(realmstudy.R.id.striker_name);
        non_striker_name = (TextView) v.findViewById(realmstudy.R.id.non_striker_name);

        current_bowler_name = (TextView) v.findViewById(realmstudy.R.id.current_bowler_name);
        current_bowler_overs = (TextView) v.findViewById(realmstudy.R.id.current_bowler_overs);
        current_bowler_runs = (TextView) v.findViewById(realmstudy.R.id.current_bowler_runs);
        next_bowler_name = (TextView) v.findViewById(realmstudy.R.id.next_bowler_name);
        next_bowler_overs = (TextView) v.findViewById(realmstudy.R.id.next_bowler_overs);
        next_bowler_runs = (TextView) v.findViewById(realmstudy.R.id.next_bowler_runs);

        next_bowler_lay = (LinearLayout) v.findViewById(realmstudy.R.id.next_bowler_lay);
        current_bowler_lay = (LinearLayout) v.findViewById(realmstudy.R.id.current_bowler_lay);


        if (current_score_data == null) {

            checkPlayerNotNull();
        } else {
            updateUI();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // callData();
                submitbuttonClicked(view);


            }
        });

        out.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //outDialog(getString(realmstudy.R.string.wicket));
                if (checkPlayerNotNull())
                    ((MainFragmentActivity) getActivity()).showOutDialog(striker.getpID(), non_striker.getpID(), assignToPlayer, matchDetails.getMatch_id());
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoCount = 1;
                updateUI();
            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redoCount = 1;

                updateUI();
            }
        });
    }

    private void resumeMatch(MatchDetails matchDetails) {
        if (matchDetails.getMatchStatus() == CommanData.MATCH_NOT_YET_STARTED) {


        } else {

            //to get last updated item from db
            RealmResults<InningsData> thisInningsData = realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).findAll();
            // if (thisInningsData.size() > 0 && realm.where(InningsData.class).findAll().get(realm.where(InningsData.class).findAll().size() - 1) != null) {
            if (thisInningsData.size() > 0) {
                // lastInningsDataItem = realm.where(InningsData.class).findAll().get(realm.where(InningsData.class).findAll().size() - 1);
                lastInningsDataItem = thisInningsData.sort("index").last();
                current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
                striker = lastInningsDataItem.getStriker();
                non_striker = lastInningsDataItem.getNonStriker();
                current_bowler = lastInningsDataItem.getCurrentBowler();
                next_bowler = lastInningsDataItem.getNextBowler();

            }
        }
    }

    private void submitbuttonClicked(View view) {
        if (checkPlayerNotNull())
            if ((matchDetails.getOvers() * 6) > current_score_data.getTotalBalls()) {
                final int totalSize = realm.where(InningsData.class).findAll().size();
                if (lastInningsDataItem != null && lastInningsDataItem.getIndex() + 1 != totalSize) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            System.out.println("nnnnnnn" + lastInningsDataItem.getIndex() + "___" + totalSize);
                            if (checkPlayerNotNull()) {
                                if (matchDetails.getMatchStatus() == CommanData.MATCH_NOT_YET_STARTED)
                                    matchDetails.setMatchStatus(CommanData.MATCH_STARTED_FI);
                                RealmResults<InningsData> result = realm.where(InningsData.class).between("index", lastInningsDataItem.getIndex() + 1, totalSize - 1).findAll();
                                result.deleteAllFromRealm();
                                System.out.println("secCheck" + totalSize + "-____" + (undoCount));
                                lastInningsDataItem = realm.where(InningsData.class).findAll().get(realm.where(InningsData.class).findAll().size() - 1);
                                current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
                                striker = lastInningsDataItem.getStriker();
                                non_striker = lastInningsDataItem.getNonStriker();
                                current_bowler = lastInningsDataItem.getCurrentBowler();
                                next_bowler = lastInningsDataItem.getNextBowler();

                                striker.getRecentBatingProfile().setBallFaced(current_score_data.getStrikerBalls());
                                striker.setName(current_score_data.getStrikerName());
                                striker.getRecentBatingProfile().setRuns(current_score_data.getStrikerRun());
                                non_striker.getRecentBatingProfile().setBallFaced(current_score_data.getNonStrikerBalls());
                                non_striker.setName(current_score_data.getNonStrikerName());
                                non_striker.getRecentBatingProfile().setRuns(current_score_data.getNonStrikerRun());
                                if (current_bowler != null) {
                                    current_bowler.getRecentBowlingProfile().setRunsGranted(current_score_data.getCurrentBowlerRuns());
                                    current_bowler.getRecentBowlingProfile().setBallsBowled(current_score_data.getCurrentBowlerBalls());
                                }
                                if (next_bowler != null) {
                                    next_bowler.getRecentBowlingProfile().setRunsGranted(current_score_data.getNextBowlerRuns());
                                    next_bowler.getRecentBowlingProfile().setBallsBowled(current_score_data.getNextBowlerBalls());
                                }

                                if (current_score_data.isBowlerSwitched()) {
                                    assignToPlayer = 2;
                                    boolean ishome;
//                                    if(!matchDetails.isHomeTeamBatting())
//                                        ishome=true;
                                    //    selectPlayerDialog(getString(realmstudy.R.string.next_bowler));
                                    ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), !matchDetails.isHomeTeamBatting(), current_bowler, getString(R.string.next_bowler));
                                }
                            }

                        }
                    });

                }
                redoCount = 0;
                undoCount = 0;

                addScore();
            } else
                Toast.makeText(getActivity(), "Game Over", Toast.LENGTH_SHORT).show();
    }

    private boolean checkPlayerNotNull() {
        boolean ishome = false;
        if (striker == null) {
            assignToPlayer = 0;
            if (matchDetails.isHomeTeamBatting())
                ishome = true;
            // selectPlayerDialog(getString(realmstudy.R.string.striker));
            System.out.println("______DDcallinit1" + ishome);
            ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(realmstudy.R.string.striker));
            return false;
        } else if (non_striker == null) {
            assignToPlayer = 1;
            if (matchDetails.isHomeTeamBatting())
                ishome = true;
            System.out.println("______DDcallinit2" + ishome);
            ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(R.string.non_striker));
            // selectPlayerDialog(getString(realmstudy.R.string.non_striker));
            return false;
        } else if (current_bowler == null) {
            assignToPlayer = 2;
            if (!matchDetails.isHomeTeamBatting())
                ishome = true;
            System.out.println("______DDcallinit3" + ishome);
            ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(R.string.current_bowler));
            // selectPlayerDialog(getString(realmstudy.R.string.current_bowler));
            return false;
        } else {
            if (current_score_data == null) {
                System.out.println("______DDcallinit4" + ishome);
                current_score_data = new ScoreBoardData();
                current_score_data.setTotalRuns(0);
                current_score_data.setTotalBalls(0);
                initialData();
            }
            return true;
        }

    }


    @Override
    public void onStop() {
//        if (matchDetails.getMatchStatus() != CommanData.MATCH_NOT_YET_STARTED)
        if (striker != null && striker.getRecentBatingProfile().getBallFaced() == 0) {
            BatingProfile bf = striker.getRecentBatingProfile();
            realm.beginTransaction();
            bf.setCurrentStatus(CommanData.StatusFree);
            realm.commitTransaction();
        }
        if (non_striker != null && non_striker.getRecentBatingProfile().getBallFaced() == 0) {
            BatingProfile bf = non_striker.getRecentBatingProfile();
            realm.beginTransaction();
            bf.setCurrentStatus(CommanData.StatusFree);
            realm.commitTransaction();
        }
        if (current_bowler != null && current_bowler.getRecentBowlingProfile().getBallsBowled() == 0) {
            BatingProfile bf = current_bowler.getRecentBatingProfile();
            realm.beginTransaction();
            bf.setCurrentStatus(CommanData.StatusFree);
            realm.commitTransaction();
        }

        super.onStop();

    }

    @Override
    public void onDestroy() {
        if (selectPlayerDialog != null)
            selectPlayerDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        String cNumber = "";
        String name = "";
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getActivity().getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                            System.out.println("number is:" + cNumber);
                        }
                        name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                    }

                    if (!name.trim().isEmpty()) {
//                        realm.beginTransaction();
//                        Player playerObj = realm.createObject(Player.class);
//                        playerObj.setpID(realm.where(Player.class).findAll().size());
//                        playerObj.setName(name);
//                        playerObj.setPh_no(cNumber);
//                        realm.commitTransaction();
                        // RealmDB.AddPlayer(getActivity(), realm, name, cNumber);

                        if (!name.isEmpty()) {
                            int id = newPlayerAdded(name, cNumber, null);
//                            if (assignToPlayer == 5) {
//                                ArrayList<Player> bowlingTeamPlayers = getBowlingTeamPlayer();
//                                ArrayAdapter<Player> bowling_team_player_adapter = new ArrayAdapter<>(
//                                        getActivity(), realmstudy.R.layout.player_spinner_item, bowlingTeamPlayers);
//                                if (caught_by != null && run_out_by != null) {
//                                    System.out.println();
//                                    caught_by.setAdapter(bowling_team_player_adapter);
//                                    run_out_by.setAdapter(bowling_team_player_adapter);
//                                    int ids = 0;
//                                    for (int i = 0; i < bowlingTeamPlayers.size(); i++) {
//                                        if (bowlingTeamPlayers.get(i).getpID() == id)
//                                            ids = i;
//                                    }
//                                    caught_by.setSelection(ids);
//                                    run_out_by.setSelection(ids);
//                                }
//                            }
                        }


                    }
                    break;
                }
        }
    }


    @Override
    public void onSuccess(String result, boolean success) {

    }


    /**
     * Set onClick listner for views
     *
     * @param v --> view that get clicked
     */
    public void onClick(View v) {


        switch (v.getId()) {
            case realmstudy.R.id.dot_txt:
                runs = 0;
                clearRunSelection();
                break;
            case realmstudy.R.id.one_run_txt:
                runs = 1;
                clearRunSelection();
                break;
            case realmstudy.R.id.two_run_txt:
                runs = 2;
                clearRunSelection();
                break;
            case realmstudy.R.id.three_run_txt:
                runs = 3;
                clearRunSelection();
                break;
            case realmstudy.R.id.four_run_txt:
                runs = 4;
                clearRunSelection();
                break;
            case realmstudy.R.id.bfour_txt:
                runs = 4;
                clearRunSelection();
                break;
            case realmstudy.R.id.bSix_txt:
                runs = 6;
                clearRunSelection();
                break;
            case realmstudy.R.id.wide_txt:
                legal = false;
                extraType = typeExtraEnum.WIDE;
                clearLegalSelection();
                break;
            case realmstudy.R.id.no_ball_txt:
                legal = false;
                extraType = typeExtraEnum.NO_BALL;
                clearLegalSelection();
                break;
            case realmstudy.R.id.byes_txt:
                legal = false;
                extraType = typeExtraEnum.BYES;
                clearLegalSelection();
                break;
            case realmstudy.R.id.leg_byes_txt:
                legal = false;
                extraType = typeExtraEnum.LEG_BYES;
                clearLegalSelection();
                break;
            case realmstudy.R.id.granted_txt:
                legal = false;
                extraType = typeExtraEnum.GRANTED;
                clearLegalSelection();
                break;
            case realmstudy.R.id.legal_ball_txt:
                legal = true;
                clearLegalSelection();
                break;

        }

        ((TextView) v).setTextColor(COLOR_SELECT);
    }


    /**
     * Set all text color of legal layout to unselect
     */

    private void clearLegalSelection() {
        wide_txt.setTextColor(COLOR_UNSELECT);
        no_ball_txt.setTextColor(COLOR_UNSELECT);
        byes_txt.setTextColor(COLOR_UNSELECT);
        leg_byes_txt.setTextColor(COLOR_UNSELECT);
        granted_txt.setTextColor(COLOR_UNSELECT);
        legal_ball_txt.setTextColor(COLOR_UNSELECT);

    }

    /**
     * Set all text color of Run layout to unselect
     */
    private void clearRunSelection() {
        one_run_txt.setTextColor(COLOR_UNSELECT);
        two_run_txt.setTextColor(COLOR_UNSELECT);
        three_run_txt.setTextColor(COLOR_UNSELECT);
        four_run_txt.setTextColor(COLOR_UNSELECT);
        bfour_txt.setTextColor(COLOR_UNSELECT);
        bSix_txt.setTextColor(COLOR_UNSELECT);
        dot_txt.setTextColor(COLOR_UNSELECT);


    }

    /**
     * Add score to database
     */
    private void addScore() {
        realm.beginTransaction();
        InningsData inningsData = realm.createObject(InningsData.class);
        ScoreBoardData score_data = setScoreDataForDB();
        inningsData.setLegal(legal);
        inningsData.setMatch_id(matchDetails.getMatch_id());
        inningsData.setRun(runs);
        inningsData.setScoreBoardData(CommanData.toString(score_data));
        inningsData.setStriker(striker);
        inningsData.setFirstInnings(IS_FIRST_INNINGS);
        inningsData.setNonStriker(non_striker);
        inningsData.setCurrentBowler(current_bowler);
        inningsData.setNextBowler(next_bowler);
        if (lastInningsDataItem != null)
            inningsData.setIndex(lastInningsDataItem.getIndex() + 1);
        else
            inningsData.setIndex(0);

        realm.commitTransaction();
        lastInningsDataItem = realm.where(InningsData.class).findAll().get(realm.where(InningsData.class).findAll().size() - 1);
        current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
        updateUI();

    }

    private ScoreBoardData setScoreDataForDB() {
        int total_run = current_score_data.getTotalRuns() + runs;
        int total_balls = current_score_data.getTotalBalls();
        ScoreBoardData score_data = new ScoreBoardData();

        if (legal) {

            //STRIKER
            striker.getRecentBatingProfile().setRuns(striker.getRecentBatingProfile().getRuns() + runs);
            score_data.setNonStrikerRun(non_striker.getRecentBatingProfile().getRuns());
            score_data.setStrikerName(striker.getName());
            score_data.setStrikerRun(striker.getRecentBatingProfile().getRuns());


            //NON_STRIKER
            score_data.setNonStrikerName(non_striker.getName());
            score_data.setNonStrikerBalls(non_striker.getRecentBatingProfile().getBallFaced());

            //CURRENT BOWLER
            current_bowler.getRecentBowlingProfile().setBallsBowled(current_bowler.getRecentBowlingProfile().getBallsBowled() + 1);
            current_bowler.getRecentBowlingProfile().setRunsGranted(current_bowler.getRecentBowlingProfile().getRunsGranted() + runs);
            score_data.setCurrentBowlerName(current_bowler.getName());
            score_data.setCurrentBowlerRuns(current_bowler.getRecentBowlingProfile().getRunsGranted());
            score_data.setCurrentBowlerBalls(current_bowler.getRecentBowlingProfile().getBallsBowled());


            //NEXT BOWLER
            if (next_bowler != null) {
                score_data.setNextBowlerName(next_bowler.getName());
                next_bowler.getRecentBowlingProfile().setRunsGranted(next_bowler.getRecentBowlingProfile().getRunsGranted());
                score_data.setNextBowlerRuns(next_bowler.getRecentBowlingProfile().getRunsGranted());
                score_data.setNextBowlerBalls(next_bowler.getRecentBowlingProfile().getBallsBowled());
            }


            score_data.setTotalRuns(total_run);
            score_data.setStrikerBalls(current_score_data.getStrikerRun() + 1);

            striker.getRecentBatingProfile().setBallFaced(striker.getRecentBatingProfile().getBallFaced() + 1);

            total_balls = total_balls + 1;
        } else {
            //STRIKER
            if (extraType == typeExtraEnum.BYES || extraType == typeExtraEnum.LEG_BYES ||extraType == typeExtraEnum.WIDE)
                striker.getRecentBatingProfile().setRuns(striker.getRecentBatingProfile().getRuns());
            else
                striker.getRecentBatingProfile().setRuns(striker.getRecentBatingProfile().getRuns() + runs);
            score_data.setNonStrikerRun(non_striker.getRecentBatingProfile().getRuns());
            score_data.setStrikerName(striker.getName());
            score_data.setStrikerRun(striker.getRecentBatingProfile().getRuns());
            striker.getRecentBatingProfile().setBallFaced(striker.getRecentBatingProfile().getBallFaced());
            score_data.setStrikerBalls(current_score_data.getStrikerBalls());
            score_data.setStrikerRun(striker.getRecentBatingProfile().getRuns());

            //NON_STRIKER
            score_data.setNonStrikerName(non_striker.getName());
            score_data.setNonStrikerBalls(non_striker.getRecentBatingProfile().getBallFaced());
            score_data.setNonStrikerRun(non_striker.getRecentBatingProfile().getRuns());

            //CURRENT BOWLER
            if (extraType == typeExtraEnum.BYES || extraType == typeExtraEnum.LEG_BYES)
                current_bowler.getRecentBowlingProfile().setRunsGranted(current_bowler.getRecentBowlingProfile().getRunsGranted());
            else {
                current_bowler.getRecentBowlingProfile().setRunsGranted(current_bowler.getRecentBowlingProfile().getRunsGranted() + runs + legalRun);
            }
            score_data.setCurrentBowlerName(current_bowler.getName());
            score_data.setCurrentBowlerName(current_bowler.getName());
            current_bowler.getRecentBowlingProfile().setBallsBowled(current_bowler.getRecentBowlingProfile().getBallsBowled());
            score_data.setCurrentBowlerBalls(current_bowler.getRecentBowlingProfile().getBallsBowled());
            score_data.setCurrentBowlerRuns(current_bowler.getRecentBowlingProfile().getRunsGranted());
            switch (extraType) {
                case WIDE:
                    current_bowler.getRecentBowlingProfile().setWide(current_bowler.getRecentBowlingProfile().getWide() + 1);
                    break;
                case NO_BALL:
                    current_bowler.getRecentBowlingProfile().setWide(current_bowler.getRecentBowlingProfile().getNoBall() + 1);
                    break;
                case BYES:
                    current_bowler.getRecentBowlingProfile().setByes(current_bowler.getRecentBowlingProfile().getByes() + 1);
                    break;
                case LEG_BYES:
                    current_bowler.getRecentBowlingProfile().setByes(current_bowler.getRecentBowlingProfile().getByes() + 1);
                    break;
            }


            //NEXT BOWLER
            if (next_bowler != null) {
                score_data.setNextBowlerName(next_bowler.getName());
                next_bowler.getRecentBowlingProfile().setRunsGranted(next_bowler.getRecentBowlingProfile().getRunsGranted());
                score_data.setNextBowlerRuns(next_bowler.getRecentBowlingProfile().getRunsGranted());
                score_data.setNextBowlerBalls(next_bowler.getRecentBowlingProfile().getBallsBowled());
            }
//Totals

            //CURRENT BOWLER
            if (extraType == typeExtraEnum.BYES || extraType == typeExtraEnum.LEG_BYES)
                score_data.setTotalRuns(total_run + 0);
            else
                score_data.setTotalRuns(total_run + legalRun);


        }


        score_data.setTotalBalls(total_balls);
        score_data.setStrikerBalls(striker.getRecentBatingProfile().getBallFaced());
        score_data.setNonStrikerBalls(non_striker.getRecentBatingProfile().getBallFaced());


        if (redoCount == 0 && undoCount == 0) {
            if (current_score_data.getTotalBalls() == 0) {
                if (runs % 2 == 1) {
                    score_data.setBatsmanSwitched(true);
                    score_data.setAskNextBowler(false);

                    //   Toast.makeText(getActivity(), "switching12", Toast.LENGTH_SHORT).show();
                } else score_data.setBatsmanSwitched(false);
            } else if ((runs % 2 == 1 && (current_score_data.getTotalBalls() + (legal?1:0)) % 6 != 0)
                    || (runs % 2 == 0 && (current_score_data.getTotalBalls() + (legal?1:0)) % 6 == 0)) {
                // Toast.makeText(getActivity(), "switching_____", Toast.LENGTH_SHORT).show();
                score_data.setBatsmanSwitched(true);
                if ((current_score_data.getTotalBalls() + (legal?1:0)) % 6 == 0) {
                    next_bowler = current_bowler;

                    assignToPlayer = 2;
                    boolean ishome = false;
                    if (!matchDetails.isHomeTeamBatting())
                        ishome = true;
                    //  selectPlayerDialog(getString(realmstudy.R.string.next_bowler));
                    ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(R.string.next_bowler));
                    score_data.setAskNextBowler(true);
                } else
                    score_data.setAskNextBowler(false);

            } else {
                score_data.setBatsmanSwitched(false);
                score_data.setAskNextBowler(false);
            }
        }

        if (score_data.isBatsmanSwitched())
            swapStriker(true);

        return score_data;
    }


    private void swapStriker(boolean withText) {
        Player dummy = striker;
        striker = non_striker;
        non_striker = dummy;
        if (withText) {
            striker_score.setText(String.valueOf(current_score_data.getNonStrikerRun()));
            striker_balls.setText(String.valueOf(current_score_data.getNonStrikerBalls()));
            non_striker_score.setText(String.valueOf(current_score_data.getStrikerRun()));
            non_striker_balls.setText(String.valueOf(current_score_data.getStrikerBalls()));
            striker_name.setText(current_score_data.getNonStrikerName() + "*");
            non_striker_name.setText(current_score_data.getStrikerName());
        }

    }


    /**
     * update UI to the current database using current_score_data
     * current_score_data-> object of ScoreboardData
     */
    private void updateUI() {
        System.out.println("___________updateUI");
        if (checkPlayerNotNull()) {
            checkUnOrRedo();
            System.out.println("nagacheckkk" + runs % 2 + "____" + current_score_data.getTotalBalls() + current_score_data.getCurrentBowlerName());
            striker_score.setText(String.valueOf(current_score_data.getStrikerRun()));
            striker_balls.setText(String.valueOf(current_score_data.getStrikerBalls()));
            non_striker_score.setText(String.valueOf(current_score_data.getNonStrikerRun()));
            non_striker_balls.setText(String.valueOf(current_score_data.getNonStrikerBalls()));
            striker_name.setText(current_score_data.getStrikerName() + "*");
            non_striker_name.setText(current_score_data.getNonStrikerName());

            if (current_score_data.getCurrentBowlerName() != null) {
                current_bowler_name.setText(current_score_data.getCurrentBowlerName());
                current_bowler_overs.setText(current_score_data.getCurrentBowlerOver());
                current_bowler_runs.setText(String.valueOf(current_score_data.getCurrentBowlerRuns()));
                current_bowler_lay.setVisibility(View.VISIBLE);
            } else {
                current_bowler_lay.setVisibility(View.GONE);
            }
            if (current_score_data.getNextBowlerName() != null) {
                next_bowler_name.setText(current_score_data.getNextBowlerName());
                next_bowler_overs.setText(current_score_data.getNextBowlerOver());
                next_bowler_runs.setText(String.valueOf(current_score_data.getNextBowlerRuns()));
                next_bowler_lay.setVisibility(View.VISIBLE);
            } else {
                next_bowler_lay.setVisibility(View.GONE);
            }

            if (current_score_data.isBatsmanSwitched()) {
                //SwaponlyText

                striker_score.setText(String.valueOf(current_score_data.getNonStrikerRun()));
                striker_balls.setText(String.valueOf(current_score_data.getNonStrikerBalls()));
                non_striker_score.setText(String.valueOf(current_score_data.getStrikerRun()));
                non_striker_balls.setText(String.valueOf(current_score_data.getStrikerBalls()));
                striker_name.setText(current_score_data.getNonStrikerName() + "*");
                non_striker_name.setText(current_score_data.getStrikerName());
                // Toast.makeText(getActivity(), "switching1", Toast.LENGTH_SHORT).show();
            }
//            if (current_score_data.isBowlerSwitched()) {
//                switchBowler(true);
//            }


            clearLegalSelection();
            clearRunSelection();
            legal_ball_txt.setTextColor(COLOR_SELECT);
            dot_txt.setTextColor(COLOR_SELECT);
            legal = true;
            runs = 0;
            num_balls_txt.setText(current_score_data.getTotalOver());
            total_runs_txt.setText(String.valueOf(current_score_data.getTotalRuns()));
        }

    }

    private void checkUnOrRedo() {
        if (undoCount > 0) {
            if (lastInningsDataItem.getIndex() != 0) {
                if (realm.where(InningsData.class).findAll().get(lastInningsDataItem.getIndex() - 1) != null) {
                    lastInningsDataItem = realm.where(InningsData.class).findAll().get(lastInningsDataItem.getIndex() - 1);
                    current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);

                    undoCount = 0;
                }
            } else {
                //  Toast.makeText(MainActivity.this, "rajinimurugan2", Toast.LENGTH_SHORT).show();
            }
        }
        if (redoCount > 0) {
            if ((lastInningsDataItem.getIndex() + 1) < realm.where(InningsData.class).findAll().size()) {
                if (realm.where(InningsData.class).findAll().get(lastInningsDataItem.getIndex() + 1) != null) {
                    lastInningsDataItem = realm.where(InningsData.class).findAll().get(lastInningsDataItem.getIndex() + 1);
                    current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
                    redoCount = 0;
                }
            } else {
                //  Toast.makeText(MainActivity.this, "rajinimurugan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initialData() {
        System.out.println("______DDinit");
        realm.beginTransaction();
        InningsData inningsData = realm.createObject(InningsData.class);
        ScoreBoardData score_data = new ScoreBoardData();
        score_data.setStrikerName(striker.getName());
        score_data.setNonStrikerName(non_striker.getName());
        score_data.setCurrentBowlerName(current_bowler.getName());
        inningsData.setMatch_id(matchDetails.getMatch_id());
        // score_data.setNextBowlerName("Bowler2");
        score_data.setBatsmanSwitched(false);
        inningsData.setLegal(legal);
        inningsData.setRun(runs);
        inningsData.setScoreBoardData(CommanData.toString(score_data));
        inningsData.setStriker(striker);
        inningsData.setNonStriker(non_striker);
        inningsData.setCurrentBowler(current_bowler);
        // inningsData.setNextBowler(null);

        inningsData.setIndex(0);


        realm.commitTransaction();
        lastInningsDataItem = realm.where(InningsData.class).findAll().get(realm.where(InningsData.class).findAll().size() - 1);
        current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
        updateUI();
    }

    private void callData() {
        CoreClient client = new ServiceGenerator().createService(CoreClient.class);

        Call<ResponseBody> LoginResponse = client.coreDetails();
        LoginResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (MainActivity.this != null) {
                    // closeDialog();
                    try {
                        String data = response.body().string();
                        System.out.println("ressssss____" + data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
//                closeDialog();
//                Toast.makeText(getActivity(), getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();

                // (getActivity() != null)
                //((MainActivity) getActivity()).closeProgressDialog();
            }
        });
    }

//select player


//    private void selectPlayerDialog(String title_txt) {
//        TextView
//                title, submit_new_player, submit_from_db;
//        LinearLayout database_lay;
//        final Spinner player_db_spinner;
//        final EditText name;
//        final EditText ph_no;
//
//        if (selectPlayerDialog != null)
//            selectPlayerDialog.dismiss();
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(realmstudy.R.layout.select_player);
//
//        RealmResults<Player> players = realm.where(Player.class).findAll();
//        ArrayAdapter<Player> adapter;
//        adapter = new ArrayAdapter<>(
//                getActivity(), realmstudy.R.layout.player_spinner_item, players);
//
//// Set other dialog properties
//
//// Create the AlertDialog
//        selectPlayerDialog = builder.create();
//        selectPlayerDialog.setCancelable(false);
//        selectPlayerDialog.setCanceledOnTouchOutside(false);
//        selectPlayerDialog.show();
//        title = (TextView) selectPlayerDialog.findViewById(realmstudy.R.id.title);
//        database_lay = (LinearLayout) selectPlayerDialog.findViewById(realmstudy.R.id.database_lay);
//        player_db_spinner = (Spinner) selectPlayerDialog.findViewById(realmstudy.R.id.player_db_spinner);
//        name = (EditText) selectPlayerDialog.findViewById(realmstudy.R.id.name);
//        ph_no = (EditText) selectPlayerDialog.findViewById(realmstudy.R.id.ph_no);
//        submit_new_player = (AppCompatButton) selectPlayerDialog.findViewById(realmstudy.R.id.submit_new_player);
//        submit_from_db = (AppCompatButton) selectPlayerDialog.findViewById(realmstudy.R.id.submit_from_db);
//        TextView from_contacts = (TextView) selectPlayerDialog.findViewById(realmstudy.R.id.from_contacts);
//        //set value
//        title.setText(title_txt);
//        player_db_spinner.setAdapter(adapter);
//        if (players.size() <= 0)
//            database_lay.setVisibility(View.GONE);
//        from_contacts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
//                        ) {
//                    // PlayerListFragment
//                    requestPermissions(
//                            new String[]{Manifest.permission.READ_CONTACTS},
//                            MY_PERMISSIONS_REQUEST_CONTACTS);
//                } else {
//                    selectPlayerDialog.dismiss();
//                    pickFromContacts();
//                }
//            }
//        });
//
//        submit_new_player.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//
//                if (!name.getText().toString().isEmpty()) {
//                    newPlayerAdded(name.getText().toString(), ph_no.getText().toString(), selectPlayerDialog);
//
//                }
//            }
//        });
//        submit_from_db.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Player bb;
//                bb = (Player) player_db_spinner.getSelectedItem();
//                boolean ss = isEligible(bb.getpID(), assignToPlayer);
//                System.out.println("_________________dd5" + matchDetails.getBattingTeamPlayer());
//                System.out.println("checkkkk" + ss);
//
//                if (ss) {
//
//                    if (assignToPlayer == 0) {
//                        striker = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
//                        BatingProfile bf = RealmDB.createBattingProfile(getActivity(), realm);
//                        realm.beginTransaction();
//                        if (matchDetails.isHomeTeamBatting())
//                            matchDetails.addHomePlayer(striker);
//                        else
//                            matchDetails.addAwayPlayer(striker);
//                        bf.setCurrentStatus(CommanData.StatusBatting);
//                        striker.setRecentBatingProfile(bf);
//                        realm.commitTransaction();
//
//                    } else if (assignToPlayer == 1) {
//                        non_striker = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
//                        BatingProfile bf = RealmDB.createBattingProfile(getActivity(), realm);
//                        realm.beginTransaction();
//                        if (matchDetails.isHomeTeamBatting())
//                            matchDetails.addHomePlayer(non_striker);
//                        else
//                            matchDetails.addAwayPlayer(non_striker);
//                        bf.setCurrentStatus(CommanData.StatusBatting);
//                        non_striker.setRecentBatingProfile(bf);
//                        realm.commitTransaction();
//                    } else if (assignToPlayer == 2) {
//                        current_bowler = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
//                        BowlingProfile bf = RealmDB.createBowlingProfile(getActivity(), realm);
//                        realm.beginTransaction();
//                        if (matchDetails.isHomeTeamBatting())
//                            matchDetails.addAwayPlayer(current_bowler);
//                        else
//                            matchDetails.addHomePlayer(current_bowler);
//                        current_bowler.setRecentBowlingProfile(bf);
//                        realm.commitTransaction();
//                    } else if (assignToPlayer == 3) {
//                        next_bowler = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
//                        BowlingProfile bf = RealmDB.createBowlingProfile(getActivity(), realm);
//                        realm.beginTransaction();
//                        if (matchDetails.isHomeTeamBatting())
//                            matchDetails.addAwayPlayer(next_bowler);
//                        else
//                            matchDetails.addHomePlayer(next_bowler);
//                        next_bowler.setRecentBowlingProfile(bf);
//                        realm.commitTransaction();
//                    }
//                    selectPlayerDialog.dismiss();
//                    updateUI();
//                }
//            }
//        });
//
//
//    }

    int newPlayerAdded(String name, String ph_no, Dialog dialog) {
        Player dummy = null;
        if (assignToPlayer == 0) {

            striker = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
            BatingProfile bf = RealmDB.createBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id());
            realm.beginTransaction();
            if (matchDetails.isHomeTeamBatting())
                matchDetails.addHomePlayer(striker);
            else
                matchDetails.addAwayPlayer(striker);
            bf.setCurrentStatus(CommanData.StatusBatting);
            striker.setRecentBatingProfile(bf);
            realm.commitTransaction();
        } else if (assignToPlayer == 1) {

            non_striker = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
            BatingProfile bf = RealmDB.createBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id());
            realm.beginTransaction();
            if (matchDetails.isHomeTeamBatting())
                matchDetails.addHomePlayer(non_striker);
            else
                matchDetails.addAwayPlayer(non_striker);
            bf.setCurrentStatus(CommanData.StatusBatting);
            non_striker.setRecentBatingProfile(bf);
            realm.commitTransaction();
        } else if (assignToPlayer == 2) {
            current_bowler = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
            BowlingProfile bf = RealmDB.createBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id());
            realm.beginTransaction();
            if (matchDetails.isHomeTeamBatting())
                current_bowler = matchDetails.addAwayPlayer(current_bowler);
            else
                current_bowler = matchDetails.addHomePlayer(current_bowler);
            current_bowler.setRecentBowlingProfile(bf);
            realm.commitTransaction();
        } else if (assignToPlayer == 3) {
            next_bowler = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
            BowlingProfile bf = RealmDB.createBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id());
            realm.beginTransaction();
            if (matchDetails.isHomeTeamBatting())
                current_bowler = matchDetails.addAwayPlayer(next_bowler);
            else
                current_bowler = matchDetails.addHomePlayer(next_bowler);
            next_bowler.setRecentBowlingProfile(bf);
            realm.commitTransaction();
        } else if (assignToPlayer == 4) {
            next_bowler = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
            BowlingProfile bf = RealmDB.createBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id());
            realm.beginTransaction();
            if (matchDetails.isHomeTeamBatting())
                current_bowler = matchDetails.addAwayPlayer(next_bowler);
            else
                current_bowler = matchDetails.addHomePlayer(next_bowler);
            next_bowler.setRecentBowlingProfile(bf);
            realm.commitTransaction();
        } else if (assignToPlayer == 5) {
            dummy = RealmDB.AddPlayer(getActivity(), realm, name, ph_no);
            //  BowlingProfile bf = RealmDB.createBowlingProfile(getActivity(), realm);
            realm.beginTransaction();
            matchDetails.addAwayPlayer(dummy);
            //  next_bowler.setRecentBowlingProfile(bf);
            realm.commitTransaction();
        }
        if (dialog != null)
            dialog.dismiss();
        updateUI();
        if (dummy != null)
            return dummy.getpID();
        else
            return -1;
    }

    private void pickFromContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }


    boolean isEligible(int id, int assignToPlayer) {
        boolean eligible = true;
//        if (matchDetails.getHomeTeam() != null) {
//            System.out.println("_________________ss" + matchDetails.getHomeTeam().name);
//            System.out.println("_________________dd" + matchDetails.getBattingTeamPlayer() + "000");
//            String battingTeamPlayers = matchDetails.getBattingTeamPlayer();
//            String bowlingTeamPlayers = matchDetails.getBowlingTeamPlayer();
//
//            // battingTeamPlayers = "";
//
//            System.out.println("_________________dd1" + bowlingTeamPlayers);
//            // battingTeamPlayers = "";
//            // bowlingTeamPlayers = "";
//            if (assignToPlayer <= 1) {
//                System.out.println("_________________dd2" + bowlingTeamPlayers);
//                if (!battingTeamPlayers.equals("")) {
//
//                    String battingTeamPlayer[] = matchDetails.getBattingTeamPlayer().split(",");
//                    String PlayerWhoLossWicketArray[]=matchDetails.PlayerWhoLossWicketArray();
//                    if(PlayerWhoLossWicketArray!=null)
//                        for(int i=0;i<PlayerWhoLossWicketArray.length;i++){
//                            if(id==Integer.parseInt(PlayerWhoLossWicketArray[i])){
//                                eligible = false;
//                                Toast.makeText(getActivity(), getString(R.string.player_already_batted), Toast.LENGTH_SHORT).show();
//                            }
//                        }
////                    for (int i = 0; i < battingTeamPlayer.length; i++) {
////                       boolean isPlayerAlreadyBatted=false;
////                        if(matchDetails.PlayerWhoLossWicketArray()!=null)
////
////                        if (id == Integer.parseInt(battingTeamPlayer[i]) && realm.where(Player.class).equalTo("pID", id).findFirst().getRecentBatingProfile().getCurrentStatus() != CommanData.StatusFree) {
////                            eligible = false;
////                            Toast.makeText(getActivity(), "Player already batted/batting", Toast.LENGTH_SHORT).show();
////                        }
////                    }
//                    if (!bowlingTeamPlayers.equals("")) {
//                        String bowlingTeamPlayer[] = matchDetails.getBowlingTeamPlayer().split(",");
//                        for (int i = 0; i < bowlingTeamPlayer.length; i++) {
//                            if (id == Integer.parseInt(bowlingTeamPlayer[i])) {
//                                eligible = false;
//                                Toast.makeText(getActivity(), "Player in oponent team", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                }
//            } else {
//                if (!battingTeamPlayers.equals("")) {
//                    System.out.println("_________________dd3" + matchDetails.getBattingTeamPlayer());
//                    String battingTeamPlayer[] = matchDetails.getBattingTeamPlayer().split(",");
//                    for (int i = 0; i < battingTeamPlayer.length; i++) {
//                        if (id == Integer.parseInt(battingTeamPlayer[i])) {
//                            eligible = false;
//                            Toast.makeText(getActivity(), "Player in oponent team", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//                System.out.println("_________________dd0d" + bowlingTeamPlayers);
//                if (!bowlingTeamPlayers.isEmpty()) {
//                    System.out.println("_________________dd4" + matchDetails.getBowlingTeamPlayer());
//                    String bowlingTeamPlayer[] = matchDetails.getBowlingTeamPlayer().split(",");
//                    if(bowlingTeamPlayer!=null)
//                    for (int i = 0; i < bowlingTeamPlayer.length; i++) {
//                        if (id == Integer.parseInt(bowlingTeamPlayer[i]) && id == current_bowler.getpID()) {
//                            eligible = false;
//                            Toast.makeText(getActivity(), "Player can't bowl continous spell", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//        } else {
//            System.out.println("_____________elsee");
//        }


        return eligible;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CONTACTS) {

            // If request is cancelled, the result arrays are empty.
            System.out.println("___" + grantResults.length + "____" + grantResults[0]);
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                pickFromContacts();

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.

            }
            //  return;
        }
        //  break;

    }


//    private void outDialog(String title_txt) {
//        RadioButton
//                caught, lbw, bowled, runnout, hitwicket;
//        final LinearLayout caught_by_lay;
//
//        final LinearLayout run_out_lay;
//        Spinner runs_scored_spinner;
//
//        final Spinner wicket_of;
//        android.support.v7.widget.AppCompatButton submit;
//        final GRadioGroup rg = new GRadioGroup();
//
//
//        if (outDialog != null)
//            outDialog.dismiss();
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(realmstudy.R.layout.out_dialog);
//
//        RealmResults<Player> players = realm.where(Player.class).findAll();
//
////
////        ArrayAdapter<Player> oppPlayer;
////        oppPlayer = new ArrayAdapter<>(
////                getActivity(), R.layout.player_spinner_item, players);
//
//
//// Create the AlertDialog
//        outDialog = builder.create();
//        outDialog.setCancelable(false);
//        outDialog.setCanceledOnTouchOutside(false);
//        outDialog.show();
//
//
//        caught = (RadioButton) outDialog.findViewById(realmstudy.R.id.caught);
//        lbw = (RadioButton) outDialog.findViewById(realmstudy.R.id.lbw);
//        bowled = (RadioButton) outDialog.findViewById(realmstudy.R.id.bowled);
//        runnout = (RadioButton) outDialog.findViewById(realmstudy.R.id.runnout);
//        hitwicket = (RadioButton) outDialog.findViewById(realmstudy.R.id.hitwicket);
//        caught_by_lay = (LinearLayout) outDialog.findViewById(realmstudy.R.id.caught_by_lay);
//        caught_by = (Spinner) outDialog.findViewById(realmstudy.R.id.caught_by);
//        run_out_lay = (LinearLayout) outDialog.findViewById(realmstudy.R.id.run_out_lay);
//        runs_scored_spinner = (Spinner) outDialog.findViewById(realmstudy.R.id.runs_scored_spinner);
//        run_out_by = (Spinner) outDialog.findViewById(realmstudy.R.id.run_out_by);
//        wicket_of = (Spinner) outDialog.findViewById(realmstudy.R.id.wicket_of);
//        submit = (android.support.v7.widget.AppCompatButton) outDialog.findViewById(realmstudy.R.id.submit);
//
//        TextView from_contacts = (TextView) outDialog.findViewById(realmstudy.R.id.from_contacts);
//        final TextView name = (TextView) outDialog.findViewById(realmstudy.R.id.name);
//        final TextView ph_no = (TextView) outDialog.findViewById(realmstudy.R.id.ph_no);
//
//        AppCompatButton submit_new_player = (android.support.v7.widget.AppCompatButton) outDialog.findViewById(realmstudy.R.id.submit_new_player);
//
//        from_contacts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                assignToPlayer = 5;
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
//                        ) {
//                    // PlayerListFragment
//                    requestPermissions(
//                            new String[]{Manifest.permission.READ_CONTACTS},
//                            MY_PERMISSIONS_REQUEST_CONTACTS);
//                } else {
//                    selectPlayerDialog.dismiss();
//
//                    pickFromContacts();
//                }
//            }
//        });
//
//        submit_new_player.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//
//                if (!name.getText().toString().isEmpty()) {
//                    assignToPlayer = 5;
//                    int id = newPlayerAdded(name.getText().toString(), ph_no.getText().toString(), selectPlayerDialog);
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
//                }
//            }
//        });
//
//
//        ArrayList<Player> bowlingTeamPlayers = null;
//
//
////        if (!matchDetails.getBowlingTeamPlayer().trim().isEmpty()) {
////            bowlingTeamPlayers = new ArrayList<>();
////            String s[] = matchDetails.getBowlingTeamPlayer().split(",");
////            for (int i = 0; i < s.length; i++) {
////                bowlingTeamPlayers.add(RealmDB.getPlayer(getActivity(), realm, Integer.parseInt(s[i])));
////
////            }
////        }
//        bowlingTeamPlayers = getBowlingTeamPlayer();
//        ArrayList<Player> bat = new ArrayList<>();
//        bat.add(striker);
//        bat.add(non_striker);
//        ArrayAdapter<Player> batters;
//        batters = new ArrayAdapter<>(getActivity(), realmstudy.R.layout.player_spinner_item, bat);
//        wicket_of.setAdapter(batters);
//        if (bowlingTeamPlayers != null) {
//            ArrayAdapter<Player> bowling_team_player_adapter = new ArrayAdapter<>(
//                    getActivity(), realmstudy.R.layout.player_spinner_item, bowlingTeamPlayers);
//            if (caught_by != null) {
//                caught_by.setAdapter(bowling_team_player_adapter);
//                run_out_by.setAdapter(bowling_team_player_adapter);
//            } else
//                System.out.println("___________null");
//
//        }
//
//        RadioButton[] rb = {caught, lbw, bowled, runnout, hitwicket};
//
//        rg.createRadioGroup(rb);
//
//        caught.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    caught_by_lay.setVisibility(View.VISIBLE);
//                    run_out_lay.setVisibility(View.GONE);
//                }
//            }
//        });
//        caught.setChecked(true);
//
//        lbw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    caught_by_lay.setVisibility(View.GONE);
//                    run_out_lay.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        bowled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    caught_by_lay.setVisibility(View.GONE);
//                    run_out_lay.setVisibility(View.GONE);
//                }
//            }
//        });
//        runnout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    caught_by_lay.setVisibility(View.GONE);
//                    run_out_lay.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        hitwicket.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    caught_by_lay.setVisibility(View.GONE);
//                    run_out_lay.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        submit.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                RadioButton s = rg.getCheckedItem();
//                switch (s.getTag().toString()) {
//                    case "caught":
//                        RealmDB.wicketCaught(getActivity(), realm, striker, current_bowler, CommanData.CAUGHT,
//                                (Player) caught_by.getSelectedItem(), current_score_data.getTotalOver(), matchDetails.getMatch_id());
//                        striker = null;
//                        assignToPlayer = 0;
//                        ((MainFragmentActivity)getActivity()).showSelectplayer(matchDetails.getMatch_id(),false,current_bowler,getString(realmstudy.R.string.striker));
//                        outDialog.dismiss();
//                        break;
//                    case "lbw":
//                        RealmDB.wicketOther(getActivity(), realm, striker, current_bowler, CommanData.LBW,
//                                current_score_data.getTotalOver(), matchDetails.getMatch_id());
//                        striker = null;
//                        assignToPlayer = 0;
//                        ((MainFragmentActivity)getActivity()).showSelectplayer(matchDetails.getMatch_id(),false,current_bowler,getString(realmstudy.R.string.striker));
//
//                    //    selectPlayerDialog(getString(realmstudy.R.string.striker));
//                        outDialog.dismiss();
//                        break;
//                    case "bowled":
//                        RealmDB.wicketOther(getActivity(), realm, striker, current_bowler, CommanData.BOWLED,
//                                current_score_data.getTotalOver(), matchDetails.getMatch_id());
//                        striker = null;
//                        assignToPlayer = 0;
//                        ((MainFragmentActivity)getActivity()).showSelectplayer(matchDetails.getMatch_id(),false,current_bowler,getString(realmstudy.R.string.striker));
//
//                       // selectPlayerDialog(getString(realmstudy.R.string.striker));
//                        outDialog.dismiss();
//                        break;
//                    case "runout":
//                        RealmDB.wicketRunout(getActivity(), realm, (Player) wicket_of.getSelectedItem(), current_bowler, CommanData.RUNOUT, (Player) run_out_by.getSelectedItem(),
//                                current_score_data.getTotalOver(), matchDetails.getMatch_id());
//                        if (((Player) wicket_of.getSelectedItem()).getpID() == striker.getpID()) {
//                            striker = null;
//                            assignToPlayer = 0;
//                        } else {
//                            non_striker = null;
//                            assignToPlayer = 1;
//                        }
//
//                        ((MainFragmentActivity)getActivity()).showSelectplayer(matchDetails.getMatch_id(),false,current_bowler,getString(realmstudy.R.string.striker));
//                        //selectPlayerDialog(getString(realmstudy.R.string.striker));
//                        outDialog.dismiss();
//                        break;
//                    case "hitout":
//                        RealmDB.wicketOther(getActivity(), realm, striker, current_bowler, CommanData.HITOUT,
//                                current_score_data.getTotalOver(), matchDetails.getMatch_id());
//                        striker = null;
//                        assignToPlayer = 0;
//                        ((MainFragmentActivity)getActivity()).showSelectplayer(matchDetails.getMatch_id(),false,current_bowler,getString(realmstudy.R.string.striker));
//                        //selectPlayerDialog(getString(realmstudy.R.string.striker));
//                        outDialog.dismiss();
//                        break;
//
//                }
//            }
//        });
//
//
//    }
//
//    ArrayList<Player> getBowlingTeamPlayer() {
//        ArrayList<Player> bowlingTeamPlayers = new ArrayList<>();
//        if (!matchDetails.getBowlingTeamPlayer().trim().isEmpty()) {
//
//            String s[] = matchDetails.getBowlingTeamPlayer().split(",");
//            for (int i = 0; i < s.length; i++) {
//                bowlingTeamPlayers.add(RealmDB.getPlayer(getActivity(), realm, Integer.parseInt(s[i])));
//
//            }
//            return bowlingTeamPlayers;
//        } else
//            return null;
//
//    }

    @Override
    public void messageFromDialog(int dialogType, boolean success, String data, String message) {
        Player bb;
        bb = RealmDB.getPlayer(getActivity(), realm, Integer.parseInt(data));
        boolean ss = isEligible(bb.getpID(), assignToPlayer);
        System.out.println("_________________dd6" + matchDetails.getBattingTeamPlayer());
        System.out.println("checkkkk" + ss);
        if(ss)
            ((MainFragmentActivity) getActivity()).closePrevSelectPlayer();
        if (ss) {

            if (assignToPlayer == 0) {
                striker = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
                BatingProfile bf = RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id());
                realm.beginTransaction();
                if (matchDetails.isHomeTeamBatting())
                    matchDetails.addHomePlayer(striker);
                else
                    matchDetails.addAwayPlayer(striker);
                bf.setCurrentStatus(CommanData.StatusBatting);
                striker.setRecentBatingProfile(bf);
                realm.commitTransaction();

            } else if (assignToPlayer == 1) {
                non_striker = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
                BatingProfile bf = RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id());
                realm.beginTransaction();
                if (matchDetails.isHomeTeamBatting())
                    matchDetails.addHomePlayer(non_striker);
                else
                    matchDetails.addAwayPlayer(non_striker);
                bf.setCurrentStatus(CommanData.StatusBatting);
                non_striker.setRecentBatingProfile(bf);
                realm.commitTransaction();
            } else if (assignToPlayer == 2) {
                current_bowler = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
                BowlingProfile bf = RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id());
                realm.beginTransaction();
                if (matchDetails.isHomeTeamBatting())
                    matchDetails.addAwayPlayer(current_bowler);
                else
                    matchDetails.addHomePlayer(current_bowler);
                bf.setCurrentBowlerStatus(CommanData.StatusBowling);
                current_bowler.setRecentBowlingProfile(bf);
                realm.commitTransaction();
            } else if (assignToPlayer == 3) {
                next_bowler = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
                BowlingProfile bf = RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id());
                realm.beginTransaction();
                if (matchDetails.isHomeTeamBatting())
                    matchDetails.addAwayPlayer(next_bowler);
                else
                    matchDetails.addHomePlayer(next_bowler);
                bf.setCurrentBowlerStatus(CommanData.StatusInMatch);
                next_bowler.setRecentBowlingProfile(bf);
                realm.commitTransaction();
            }
            //  selectPlayerDialog.dismiss();
            updateUI();
        } else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }


        //outDialog
//        String s = "";
//        int wicket_of = -1;
//        switch (s.toString()) {
//            case "caught":
//                RealmDB.wicketCaught(getActivity(), realm, striker, current_bowler, CommanData.CAUGHT,
//                        (Player) caught_by.getSelectedItem(), current_score_data.getTotalOver(), matchDetails.getMatch_id());
//                striker = null;
//                assignToPlayer = 0;
//
//                ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), !matchDetails.isHomeTeamBatting(), current_bowler, getString(realmstudy.R.string.striker));
//
//                break;
//            case "lbw":
//                RealmDB.wicketOther(getActivity(), realm, striker, current_bowler, CommanData.LBW,
//                        current_score_data.getTotalOver(), matchDetails.getMatch_id());
//                striker = null;
//                assignToPlayer = 0;
//
//                ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), !matchDetails.isHomeTeamBatting(), current_bowler, getString(realmstudy.R.string.striker));
//
//                //    selectPlayerDialog(getString(realmstudy.R.string.striker));
//
//                break;
//            case "bowled":
//                RealmDB.wicketOther(getActivity(), realm, striker, current_bowler, CommanData.BOWLED,
//                        current_score_data.getTotalOver(), matchDetails.getMatch_id());
//                striker = null;
//                assignToPlayer = 0;
//
//                ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), !matchDetails.isHomeTeamBatting(), current_bowler, getString(realmstudy.R.string.striker));
//                // selectPlayerDialog(getString(realmstudy.R.string.striker));
//                break;
//            case "runout":
//
//
//                RealmDB.wicketRunout(getActivity(), realm, RealmDB.getPlayer(getActivity(), realm, wicket_of), current_bowler, CommanData.RUNOUT, (Player) run_out_by.getSelectedItem(),
//                        current_score_data.getTotalOver(), matchDetails.getMatch_id());
//                if (RealmDB.getPlayer(getActivity(), realm, wicket_of).getpID() == striker.getpID()) {
//                    striker = null;
//                    assignToPlayer = 0;
//                } else {
//                    non_striker = null;
//                    assignToPlayer = 1;
//                }
//
//                ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), !matchDetails.isHomeTeamBatting(), current_bowler, getString(realmstudy.R.string.striker));
//                //selectPlayerDialog(getString(realmstudy.R.string.striker));
//
//                break;
//            case "hitout":
//                RealmDB.wicketOther(getActivity(), realm, striker, current_bowler, CommanData.HITOUT,
//                        current_score_data.getTotalOver(), matchDetails.getMatch_id());
//                striker = null;
//                assignToPlayer = 0;
////
//                ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), !matchDetails.isHomeTeamBatting(), current_bowler, getString(realmstudy.R.string.striker));
//                //selectPlayerDialog(getString(realmstudy.R.string.striker));
//
//                break;
//
//        }


    }


}















