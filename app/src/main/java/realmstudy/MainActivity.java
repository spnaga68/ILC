package realmstudy;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import realmstudy.data.RealmObjectData.Wicket;
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


    private CommanData.typeExtraEnum extraType;
    MatchDetails matchDetails;


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

    //initialize views
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
                lastInningsDataItem = thisInningsData.last();
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

                //check undo or redo


//                if (lastInningsDataItem != null && lastInningsDataItem.getIndex() + 1 != totalSize) {
//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//
//                            System.out.println("nnnnnnn" + lastInningsDataItem.getIndex() + "___" + totalSize);
//                            if (checkPlayerNotNull()) {
//                                if (matchDetails.getMatchStatus() == CommanData.MATCH_NOT_YET_STARTED)
//                                    matchDetails.setMatchStatus(CommanData.MATCH_STARTED_FI);
//                                RealmResults<InningsData> result = realm.where(InningsData.class).between("index", lastInningsDataItem.getIndex() + 1, totalSize - 1).findAll();
//                                result.deleteAllFromRealm();
//                                System.out.println("secCheck" + totalSize + "-____" + (undoCount));
//                                lastInningsDataItem = realm.where(InningsData.class).findAll().get(realm.where(InningsData.class).findAll().size() - 1);
//                                current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
//                                striker = lastInningsDataItem.getStriker();
//                                non_striker = lastInningsDataItem.getNonStriker();
//                                current_bowler = lastInningsDataItem.getCurrentBowler();
//                                next_bowler = lastInningsDataItem.getNextBowler();
//                                RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).setBallFaced(current_score_data.getStrikerBalls());
//                                striker.setName(current_score_data.getStrikerName());
//                                RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).setRuns(current_score_data.getStrikerRun());
//                                RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id()).setBallFaced(current_score_data.getNonStrikerBalls());
//                                non_striker.setName(current_score_data.getNonStrikerName());
//                                RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id()).setRuns(current_score_data.getNonStrikerRun());
//                                if (current_bowler != null) {
//                                    RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(current_score_data.getCurrentBowlerRuns());
//                                    RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setBallsBowled(current_score_data.getCurrentBowlerBalls());
//                                }
//                                if (next_bowler != null) {
//                                    RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(current_score_data.getNextBowlerRuns());
//                                    RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id()).setBallsBowled(current_score_data.getNextBowlerBalls());
//                                }
//
//                                if (current_score_data.isBowlerSwitched()) {
//                                    assignToPlayer = 2;
//                                    boolean ishome;
////                                    if(!matchDetails.isHomeTeamBatting())
////                                        ishome=true;
//                                    //    selectPlayerDialog(getString(realmstudy.R.string.next_bowler));
//                                    ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), !matchDetails.isHomeTeamBatting(), current_bowler, getString(R.string.next_bowler));
//                                }
//                            }
//
//                        }
//                    });
//
//                }
                redoCount = 0;
                undoCount = 0;

                addScore();
            } else {
                Toast.makeText(getActivity(), "Game Over", Toast.LENGTH_SHORT).show();
               switchInnings();
            }
    }

    private void switchInnings() {
        striker = null;
        non_striker = null;
        current_bowler = null;
        next_bowler = null;
        realm.beginTransaction();
        matchDetails.setFirstInningsCompleted(true);
        matchDetails.setHomeTeamBatting(!matchDetails.isHomeTeamBatting());
        current_score_data=null;
        realm.commitTransaction();
        checkPlayerNotNull();
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
        } else if (lastInningsDataItem != null && lastInningsDataItem.getWicket() != null && current_score_data.getNextBatsman() == -1) {
            assignToPlayer = 5;
            ishome = false;
            if (matchDetails.isHomeTeamBatting())
                ishome = true;
            // selectPlayerDialog(getString(realmstudy.R.string.striker));
            System.out.println("______DDcallinit1" + ishome);
            ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(realmstudy.R.string.next_batsman));
            return false;
        } else if (current_score_data == null) {
            System.out.println("______DDcallinit4" + ishome);
//            current_score_data = new ScoreBoardData();
//            current_score_data.setTotalRuns(0);
//            current_score_data.setTotalBalls(0);
            initialData();
            return false;
        }

        return true;
    }

    @Override
    public void onStop() {
//        if (matchDetails.getMatchStatus() != CommanData.MATCH_NOT_YET_STARTED)
        if (striker != null && RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).getBallFaced() == 0) {
            BatingProfile bf = RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id());
            realm.beginTransaction();
            bf.setCurrentStatus(CommanData.StatusFree);
            realm.commitTransaction();
        }
        if (non_striker != null && RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id()).getBallFaced() == 0) {
            BatingProfile bf = RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id());
            realm.beginTransaction();
            bf.setCurrentStatus(CommanData.StatusFree);
            realm.commitTransaction();
        }
        if (current_bowler != null && RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled() == 0) {
            BowlingProfile bf = RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id());
            realm.beginTransaction();
            bf.setCurrentBowlerStatus(CommanData.StatusFree);
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
                extraType = CommanData.typeExtraEnum.WIDE;
                clearLegalSelection();
                break;
            case realmstudy.R.id.no_ball_txt:
                legal = false;
                extraType = CommanData.typeExtraEnum.NO_BALL;
                clearLegalSelection();
                break;
            case realmstudy.R.id.byes_txt:
                legal = false;
                extraType = CommanData.typeExtraEnum.L_BYES;
                clearLegalSelection();
                break;
            case realmstudy.R.id.leg_byes_txt:
                legal = false;
                extraType = CommanData.typeExtraEnum.LEG_BYES;
                clearLegalSelection();
                break;
            case realmstudy.R.id.granted_txt:
                legal = false;
                extraType = CommanData.typeExtraEnum.GRANTED;
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
        InningsData inningsData = RealmDB.getInningsData(getActivity(), realm,
                lastInningsDataItem != null ? (lastInningsDataItem.getIndex() + 1) : 0,
                matchDetails.getMatch_id(), matchDetails.isFirstInningsCompleted());

        realm.beginTransaction();
        if (matchDetails.getMatchStatus() == CommanData.MATCH_NOT_YET_STARTED)
            matchDetails.setMatchStatus(CommanData.MATCH_STARTED_FI);
        ScoreBoardData score_data = setScoreDataForDB();
        inningsData.setLegal(legal);
        if (legal)
            inningsData.setBallType(CommanData.BALL_LEGAL);
        else
            inningsData.setBallType(extraType);
        inningsData.setMatch_id(matchDetails.getMatch_id());
        inningsData.setRun(runs);
        inningsData.setScoreBoardData(CommanData.toString(score_data));
        inningsData.setStriker(striker);
        inningsData.setFirstInnings(!matchDetails.isFirstInningsCompleted());
        inningsData.setNonStriker(non_striker);
        inningsData.setCurrentBowler(current_bowler);
        inningsData.setNextBowler(next_bowler);
//        if (lastInningsDataItem != null)
//            inningsData.setIndex(lastInningsDataItem.getIndex() + 1);
//        else
//            inningsData.setIndex(0);

        realm.commitTransaction();
        lastInningsDataItem = realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).findAll().last();
        current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
        updateUI();

    }

    private ScoreBoardData setScoreDataForDB() {
        int total_run = current_score_data.getTotalRuns() + runs;
        int total_balls = current_score_data.getTotalBalls();
        ScoreBoardData score_data = new ScoreBoardData();

        if (legal) {

            //STRIKER
            if (striker != null) {
                RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).setRuns(RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).getRuns() + runs);
                score_data.setStrikerName(striker.getName());
                score_data.setStrikerRun(RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).getRuns());
            }

            //NON_STRIKER
            if (non_striker != null) {
                score_data.setNonStrikerName(non_striker.getName());
                score_data.setNonStrikerRun(RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id()).getRuns());
                score_data.setNonStrikerBalls(RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id()).getBallFaced());
            }
            //CURRENT BOWLER
            if (current_bowler != null) {
                RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setBallsBowled(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled() + 1);
                RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted() + runs);
                score_data.setCurrentBowlerName(current_bowler.getName());
                score_data.setCurrentBowlerRuns(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
                score_data.setCurrentBowlerBalls(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled());
            }

            //NEXT BOWLER
            if (next_bowler != null) {
//                score_data.setNextBowlerName(next_bowler.getName());
//                next_bowler.getRecentBowlingProfile().setRunsGranted(next_bowler.getRecentBowlingProfile().getRunsGranted());
//                score_data.setNextBowlerRuns(next_bowler.getRecentBowlingProfile().getRunsGranted());
//                score_data.setNextBowlerBalls(next_bowler.getRecentBowlingProfile().getBallsBowled());

                score_data.setNextBowlerName(next_bowler.getName());
                RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
                score_data.setNextBowlerRuns(RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
                score_data.setNextBowlerBalls(RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled());

            }


            score_data.setTotalRuns(total_run);
            score_data.setStrikerBalls(current_score_data.getStrikerRun() + 1);

            RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).setBallFaced(RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).getBallFaced() + 1);

            total_balls = total_balls + 1;
        } else {
            //STRIKER
            if (extraType == CommanData.typeExtraEnum.L_BYES || extraType == CommanData.typeExtraEnum.LEG_BYES || extraType == CommanData.typeExtraEnum.WIDE)
                RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).setRuns(RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).getRuns());
            else
                RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).setRuns(RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).getRuns() + runs);
            score_data.setNonStrikerRun(RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id()).getRuns());
            score_data.setStrikerName(striker.getName());
            score_data.setStrikerRun(RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).getRuns());
            RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).setBallFaced(RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).getBallFaced());
            score_data.setStrikerBalls(current_score_data.getStrikerBalls());
            score_data.setStrikerRun(RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).getRuns());

            //NON_STRIKER
            score_data.setNonStrikerName(non_striker.getName());
            score_data.setNonStrikerBalls(RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id()).getBallFaced());
            score_data.setNonStrikerRun(RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id()).getRuns());

            //CURRENT BOWLER
            if (extraType == CommanData.typeExtraEnum.L_BYES || extraType == CommanData.typeExtraEnum.LEG_BYES
                    || extraType == CommanData.typeExtraEnum.W_BYES || extraType == CommanData.typeExtraEnum.NB_BYES)
                RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
            else {
                RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted() + runs + legalRun);
            }
            score_data.setCurrentBowlerName(current_bowler.getName());
            score_data.setCurrentBowlerName(current_bowler.getName());
            RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setBallsBowled(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled());
            score_data.setCurrentBowlerBalls(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled());
            score_data.setCurrentBowlerRuns(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
            switch (extraType) {
                case WIDE:
                    RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setWide(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getWide() + 1);
                    break;
                case NO_BALL:
                    RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setWide(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getNoBall() + 1);
                    break;
                case L_BYES:
                    RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setByes(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getByes() + 1);
                    break;
                case LEG_BYES:
                    RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).setByes(RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id()).getByes() + 1);
                    break;
            }


            //NEXT BOWLER
            if (next_bowler != null) {
                score_data.setNextBowlerName(next_bowler.getName());
                RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
                score_data.setNextBowlerRuns(RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
                score_data.setNextBowlerBalls(RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled());
            }
//Totals

            //CURRENT BOWLER
            if (extraType == CommanData.typeExtraEnum.L_BYES || extraType == CommanData.typeExtraEnum.LEG_BYES)
                score_data.setTotalRuns(total_run + 0);
            else
                score_data.setTotalRuns(total_run + legalRun);


        }


        score_data.setTotalBalls(total_balls);
        score_data.setStrikerBalls(RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id()).getBallFaced());
        score_data.setNonStrikerBalls(RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id()).getBallFaced());


        if (redoCount == 0 && undoCount == 0) {
            if (current_score_data.getTotalBalls() == 0) {
                if (runs % 2 == 1) {
                    score_data.setBatsmanSwitched(true);
                    score_data.setAskNextBowler(false);

                    //   Toast.makeText(getActivity(), "switching12", Toast.LENGTH_SHORT).show();
                } else score_data.setBatsmanSwitched(false);
            } else {

                if ((current_score_data.getTotalBalls() + (legal ? 1 : 0)) % 6 == 0) {

                    next_bowler = current_bowler;
                    assignToPlayer = 2;
                    boolean ishome = false;
                    if (!matchDetails.isHomeTeamBatting())
                        ishome = true;
                    ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(R.string.next_bowler));
                    score_data.setAskNextBowler(true);


                } else
                    score_data.setAskNextBowler(false);

                if ((runs % 2 == 1 && (current_score_data.getTotalBalls() + (legal ? 1 : 0)) % 6 != 0)
                        || (runs % 2 == 0 && (current_score_data.getTotalBalls() + (legal ? 1 : 0)) % 6 == 0)) {
                    // Toast.makeText(getActivity(), "switching_____", Toast.LENGTH_SHORT).show();
                    score_data.setBatsmanSwitched(true);
                } else
                    score_data.setBatsmanSwitched(false);
//            else {
//                score_data.setBatsmanSwitched(false);
//                score_data.setAskNextBowler(false);
//            }
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
        } else {
            System.out.println("_________________falssss");
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
        InningsData inningsData = RealmDB.getInningsData(getActivity(), realm, 0, matchDetails.getMatch_id(), matchDetails.isFirstInningsCompleted());
        realm.beginTransaction();

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

//        inningsData.setIndex(0);


        realm.commitTransaction();
        lastInningsDataItem = realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).findAll().last();
        current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
        System.out.println("____________AAAA" + current_score_data);
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


    @Override
    public void messageFromDialog(int dialogType, boolean success, String data, String message) {
        System.out.println("_____Out" + dialogType + "__" + data + "__" + success);
        if (success) {

            if (dialogType == CommanData.DIALOG_OUT) {

                Wicket wicket = RealmDB.getWicket(getActivity(), realm, data);
                System.out.println("_____Out" + dialogType + "__" + data + "__" + wicket.getType());
                addScore();
                if (wicket.getType() != CommanData.W_RUNOUT) {
                    striker = null;
//                    assignToPlayer=5;
//                    boolean ishome=false;
//                    if (matchDetails.isHomeTeamBatting())
//                        ishome = true;
//                    // selectPlayerDialog(getString(realmstudy.R.string.striker));
//                    System.out.println("______DDcallinit1" + ishome);
//                    ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(realmstudy.R.string.next_batsman));

                } else if (wicket.getType() == CommanData.W_RUNOUT) {
                    Player p = wicket.getBatsman();
                    if (p.getpID() == striker.getpID())
                        striker = null;
                    else
                        non_striker = null;

                }

            } else {
                Player bb;

                bb = RealmDB.getPlayer(getActivity(), realm, Integer.parseInt(data));
                boolean ss = success;
                System.out.println("_________________dd6" + matchDetails.getBattingTeamPlayer());
                System.out.println("checkkkk" + ss);
//

                if (assignToPlayer == 0) {
                    striker = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
                    BatingProfile bf = RealmDB.getBattingProfile(getActivity(), realm, striker.getpID(), matchDetails.getMatch_id());
                    realm.beginTransaction();
                    if (matchDetails.isHomeTeamBatting())
                        matchDetails.addHomePlayer(striker);
                    else
                        matchDetails.addAwayPlayer(striker);
                    bf.setCurrentStatus(CommanData.StatusBatting);
                    //striker.setRecentBatingProfile(bf);
                    realm.commitTransaction();

                } else if (assignToPlayer == 1) {
                    non_striker = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
                    BatingProfile bf = RealmDB.getBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id());
                    if (bf == null)
                        bf = RealmDB.createBattingProfile(getActivity(), realm, non_striker.getpID(), matchDetails.getMatch_id());
                    realm.beginTransaction();
                    if (matchDetails.isHomeTeamBatting())
                        matchDetails.addHomePlayer(non_striker);
                    else
                        matchDetails.addAwayPlayer(non_striker);
                    bf.setCurrentStatus(CommanData.StatusBatting);
                    //  non_striker.setRecentBatingProfile(bf);
                    realm.commitTransaction();
                } else if (assignToPlayer == 2) {
                    current_bowler = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
                    BowlingProfile bf = RealmDB.getBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id());
                    if (bf == null)
                        bf = RealmDB.createBowlingProfile(getActivity(), realm, current_bowler.getpID(), matchDetails.getMatch_id());
                    realm.beginTransaction();
                    if (matchDetails.isHomeTeamBatting())
                        matchDetails.addAwayPlayer(current_bowler);
                    else
                        matchDetails.addHomePlayer(current_bowler);
                    bf.setCurrentBowlerStatus(CommanData.StatusBowling);
                    // current_bowler.setRecentBowlingProfile(bf);
                    realm.commitTransaction();
                } else if (assignToPlayer == 3) {
                    next_bowler = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
                    BowlingProfile bf = RealmDB.getBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id());
                    if (bf == null)
                        bf = RealmDB.createBowlingProfile(getActivity(), realm, next_bowler.getpID(), matchDetails.getMatch_id());
                    realm.beginTransaction();
                    if (matchDetails.isHomeTeamBatting())
                        matchDetails.addAwayPlayer(next_bowler);
                    else
                        matchDetails.addHomePlayer(next_bowler);
                    bf.setCurrentBowlerStatus(CommanData.StatusInMatch);
                    // next_bowler.setRecentBowlingProfile(bf);
                    realm.commitTransaction();
                } else if (assignToPlayer == 5) {


                    Player dummy = RealmDB.getPlayer(getActivity(), realm, bb.getpID());
                    BatingProfile bf = RealmDB.getBattingProfile(getActivity(), realm, dummy.getpID(), matchDetails.getMatch_id());
                    if (bf == null)
                        bf = RealmDB.createBattingProfile(getActivity(), realm, dummy.getpID(), matchDetails.getMatch_id());
                    realm.beginTransaction();
                    if (matchDetails.isHomeTeamBatting())
                        matchDetails.addAwayPlayer(dummy);
                    else
                        matchDetails.addHomePlayer(dummy);
                    bf.setCurrentStatus(CommanData.StatusBatting);
                    // next_bowler.setRecentBowlingProfile(bf);

                    current_score_data.setNextBatsman(dummy.getpID());
                    current_score_data.setNextBatsmanBalls(bf.getBallFaced());
                    current_score_data.setNextBatsmanRun(bf.getRuns());
                    lastInningsDataItem.setScoreBoardData(CommanData.toString(current_score_data));
                    realm.commitTransaction();
                    if (striker.getpID() == current_score_data.getWicket().getBatsman().getpID()) {
                        striker = null;
                        striker = dummy;
                    } else {
                        non_striker = null;
                        non_striker = dummy;
                    }


                }


                //  selectPlayerDialog.dismiss();}
                updateUI();
            }

        } else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }


    }
}


















