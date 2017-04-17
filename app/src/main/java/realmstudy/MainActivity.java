package realmstudy;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
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
import realmstudy.extras.CanvasStudy;
import realmstudy.fragments.ScoreBoardFragment;
import realmstudy.interfaces.DialogInterface;
import realmstudy.interfaces.MsgFromDialog;
import realmstudy.interfaces.MsgToFragment;
import realmstudy.service.CoreClient;
import realmstudy.service.ServiceGenerator;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends ScoreBoardFragment implements DialogInterface, MsgToFragment, MsgFromDialog {

    private static final int COLOR_SELECT = Color.RED;
    private static final int COLOR_UNSELECT = Color.BLACK;
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 420;
    private static final int PICK_CONTACT = 421;
    private static final int SUBMIT_DELAY = 0;
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
    private boolean normal_delivery = true;
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
    private CanvasStudy groundFragment;


    @Override
    public void msg(String s) {

    }


    private CommanData.typeExtraEnum extraType;
    MatchDetails matchDetails;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_main, container, false);


        realm = ((MainFragmentActivity) (getActivity())).getRealm();


        try {
            Bundle b = getArguments();
            int match_id = b.getInt("match_id");
            matchDetails = RealmDB.getMatchById(getActivity(), realm, match_id);
            resumeMatch(matchDetails);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initialize(v);
        init(v);
        checkAndUpdateUI();

        return v;
    }

    //initialize views
    public void init(View v) {
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
        undo = (ImageButton) getActivity().findViewById(realmstudy.R.id.undo);
        redo = (ImageButton) getActivity().findViewById(realmstudy.R.id.redo);
        submit = (AppCompatButton) v.findViewById(realmstudy.R.id.submit);
        out = (AppCompatButton) v.findViewById(realmstudy.R.id.out);
        groundFragment = (CanvasStudy) getChildFragmentManager().findFragmentById(R.id.ground_frag);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // callData();
                //submitbuttonClicked(view);


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
//                checkAndUpdateUI();
                checkUnOrRedo();
                updateUI(current_score_data);

            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redoCount = 1;

                //   checkAndUpdateUI();
                checkUnOrRedo();
                updateUI(current_score_data);

            }
        });
    }

    private void checkAndUpdateUI() {
        if (checkPlayerNotNull()) {
            checkUnOrRedo();
            updateUI(current_score_data);


            normal_delivery = true;
            runs = 0;
            clearLegalSelection();
            clearRunSelection();
            legal_ball_txt.setTextColor(COLOR_SELECT);
            checkInningsGoingOn();
            // dot_txt.setTextColor(COLOR_SELECT);
        }
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
                striker = RealmDB.getPlayer(realm, lastInningsDataItem.getStriker());
                non_striker = RealmDB.getPlayer(realm, lastInningsDataItem.getNonStriker());
                current_bowler = RealmDB.getPlayer(realm, lastInningsDataItem.getCurrentBowler());
                next_bowler = RealmDB.getPlayer(realm, lastInningsDataItem.getNextBowler());

            }
        }
    }

    private void submitbuttonClicked(int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                submitbuttonClicked();
            }
        }, delay);
    }

    private void submitbuttonClicked() {

        if (checkPlayerNotNull()) {
//            if ((matchDetails.getOvers() * 6) > current_score_data.getTotalBalls() &&
//                    RealmDB.noOfWicket(getActivity(), realm, matchDetails.getMatch_id(), !matchDetails.isFirstInningsCompleted()
//                    ) < matchDetails.getTotalPlayers()) {
            final int totalSize = realm.where(InningsData.class).equalTo("match_id",matchDetails.getMatch_id())
                    .equalTo("firstInnings",!matchDetails.isFirstInningsCompleted()).findAll().size();

            //check undo or redo
                if (lastInningsDataItem != null && lastInningsDataItem.getDelivery()  != totalSize) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            System.out.println("nnnnnnn" + lastInningsDataItem.getDelivery() + "___" + totalSize);
                           // if (checkPlayerNotNull()) {
                                if (matchDetails.getMatchStatus() == CommanData.MATCH_NOT_YET_STARTED)
                                    matchDetails.setMatchStatus(CommanData.MATCH_STARTED_FI);
                                RealmResults<InningsData> result = realm.where(InningsData.class).between("delivery", lastInningsDataItem.getDelivery()+1, totalSize ).findAll();

                            result.deleteAllFromRealm();
                                System.out.println("secCheck" + totalSize + "-____" + (undoCount));
                                lastInningsDataItem = realm.where(InningsData.class).findAll().get(realm.where(InningsData.class).findAll().size() - 1);
                                current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
                                striker = RealmDB.getPlayer(realm,lastInningsDataItem.getStriker());
                                non_striker = RealmDB.getPlayer(realm,lastInningsDataItem.getNonStriker());
                                current_bowler = RealmDB.getPlayer(realm,lastInningsDataItem.getCurrentBowler());
                                next_bowler = RealmDB.getPlayer(realm,lastInningsDataItem.getNextBowler());
                                RealmDB.updateBattingBowlingProfile(realm,matchDetails);
                               // RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).setBallFaced(current_score_data.getStrikerBalls());
                                striker.setName(current_score_data.getStrikerName());
                               // RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).setRuns(current_score_data.getStrikerRun());
                                //RealmDB.getBattingProfile( realm, non_striker.getpID(), matchDetails.getMatch_id()).setBallFaced(current_score_data.getNonStrikerBalls());
                                non_striker.setName(current_score_data.getNonStrikerName());
                                RealmDB.getBattingProfile( realm, non_striker.getpID(), matchDetails.getMatch_id()).setRuns(current_score_data.getNonStrikerRun());
                                if (current_bowler != null) {
                                    RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(current_score_data.getCurrentBowlerRuns());
                                    RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).setBallsBowled(current_score_data.getCurrentBowlerBalls());
                                }
                                if (next_bowler != null) {
                                    RealmDB.getBowlingProfile( realm, next_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(current_score_data.getNextBowlerRuns());
                                    RealmDB.getBowlingProfile( realm, next_bowler.getpID(), matchDetails.getMatch_id()).setBallsBowled(current_score_data.getNextBowlerBalls());
                                }

//                                if (current_score_data.isBowlerSwitched()) {
//                                    assignToPlayer = 2;
//                                    boolean ishome;
////                                    if(!matchDetails.isHomeTeamBatting())
////                                        ishome=true;
//                                    //    selectPlayerDialog(getString(realmstudy.R.string.next_bowler));
//                                    ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), !matchDetails.isHomeTeamBatting(), current_bowler, getString(R.string.next_bowler),assignToPlayer);
//                                }
                            //}




                        }
                    });

                }
            redoCount = 0;
            undoCount = 0;
            addScore(null);
        }
    }

    private void switchInnings() {
        striker = null;
        non_striker = null;
        current_bowler = null;
        next_bowler = null;
        //  Toast.makeText(getActivity(),"ssssss",Toast.LENGTH_LONG).show();
        realm.beginTransaction();
        matchDetails.setFirstInningsCompleted(true);
        matchDetails.setMatchStatus(CommanData.MATCH_BREAK_FI);
        matchDetails.setHomeTeamBatting(!matchDetails.isHomeTeamBatting());
        current_score_data = null;
        realm.commitTransaction();
        checkPlayerNotNull();
    }

    public int getNextKey(RealmObject t, String key) {
        try {
            return realm.where(t.getClass()).equalTo("match_id", matchDetails.getMatch_id()).max(key).intValue() + 1;
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    private boolean checkInningsGoingOn() {
        System.out.println("_________WW" + RealmDB.noOfWicket(getActivity(), realm, matchDetails.getMatch_id(), !matchDetails.isFirstInningsCompleted()
        ) + "___" + matchDetails.getTotalPlayers() + "___" + ((current_score_data != null) ? current_score_data.getTotalBalls() : "null"));

        if ((current_score_data != null && (lastInningsDataItem.getOver() >= (matchDetails.getOvers()))) ||
                (RealmDB.noOfWicket(getActivity(), realm, matchDetails.getMatch_id(), !matchDetails.isFirstInningsCompleted()) >= (matchDetails.getTotalPlayers() - 1))) {
            System.out.println("_________WWs");
            if (matchDetails.getMatchStatus() == CommanData.MATCH_STARTED_SI) {
                realm.beginTransaction();
                matchDetails.setMatchStatus(CommanData.MATCH_COMPLETED);
                realm.commitTransaction();
                Toast.makeText(getActivity(), getString(R.string.game_over), Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("_________WWd");
                if (matchDetails.getMatchStatus() == CommanData.MATCH_BREAK_FI)
                    return true;
                else {

                    switchInnings();
                    return false;
                }
            }
            return false;
        } else
            return true;
    }

    private boolean checkPlayerNotNull() {
        boolean ishome = false;
        if (true) {
            if (striker == null) {
                assignToPlayer = 0;
                if (matchDetails.isHomeTeamBatting())
                    ishome = true;
                // selectPlayerDialog(getString(realmstudy.R.string.striker));
                System.out.println("______DDcallinit1" + ishome);
                ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(realmstudy.R.string.striker), assignToPlayer);
                return false;
            } else if (non_striker == null) {
                assignToPlayer = 1;
                if (matchDetails.isHomeTeamBatting())
                    ishome = true;
                System.out.println("______DDcallinit2" + ishome);
                ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(R.string.non_striker), assignToPlayer);
                // selectPlayerDialog(getString(realmstudy.R.string.non_striker));
                return false;
            } else if (current_bowler == null) {
                assignToPlayer = 2;
                if (!matchDetails.isHomeTeamBatting())
                    ishome = true;
                System.out.println("______DDcallinit3" + ishome);
                ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(R.string.current_bowler), assignToPlayer);
                // selectPlayerDialog(getString(realmstudy.R.string.current_bowler));
                return false;
            } else if (lastInningsDataItem != null && lastInningsDataItem.getWicket() != null && current_score_data != null && current_score_data.getNextBatsmanName() == null) {
                assignToPlayer = 5;
                ishome = false;
                if (matchDetails.isHomeTeamBatting())
                    ishome = true;
                // selectPlayerDialog(getString(realmstudy.R.string.striker));
                System.out.println("______DDcallinit1" + ishome);
                ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(realmstudy.R.string.next_batsman), assignToPlayer);
                return false;
            } else if (current_score_data == null) {
                System.out.println("______DDcallinit4" + ishome);
//            current_score_data = new ScoreBoardData();
//            current_score_data.setTotalRuns(0);
//            current_score_data.setTotalBalls(0);
                initialData();
                return false;
            } else
                return true;
        } else
            return false;
        //  return true;
    }

    @Override
    public void onStop() {
//        if (matchDetails.getMatchStatus() != CommanData.MATCH_NOT_YET_STARTED)
        if (striker != null && RealmDB.getBattingProfile(realm, striker.getpID(), matchDetails.getMatch_id()).getBallFaced() == 0) {
            BatingProfile bf = RealmDB.getBattingProfile(realm, striker.getpID(), matchDetails.getMatch_id());
            realm.beginTransaction();
            bf.setCurrentStatus(CommanData.StatusFree);
            realm.commitTransaction();
        }
        if (non_striker != null && RealmDB.getBattingProfile(realm, non_striker.getpID(), matchDetails.getMatch_id()).getBallFaced() == 0) {
            BatingProfile bf = RealmDB.getBattingProfile(realm, non_striker.getpID(), matchDetails.getMatch_id());
            realm.beginTransaction();
            bf.setCurrentStatus(CommanData.StatusFree);
            realm.commitTransaction();
        }
        if (current_bowler != null && RealmDB.getBowlingProfile(realm, current_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled() == 0) {
            BowlingProfile bf = RealmDB.getBowlingProfile(realm, current_bowler.getpID(), matchDetails.getMatch_id());
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
                submitbuttonClicked(SUBMIT_DELAY);
                break;
            case realmstudy.R.id.one_run_txt:
                runs = 1;
                clearRunSelection();
                submitbuttonClicked(SUBMIT_DELAY);
                break;
            case realmstudy.R.id.two_run_txt:
                runs = 2;
                clearRunSelection();
                submitbuttonClicked(SUBMIT_DELAY);
                break;
            case realmstudy.R.id.three_run_txt:
                runs = 3;
                clearRunSelection();
                submitbuttonClicked(SUBMIT_DELAY);
                break;
            case realmstudy.R.id.four_run_txt:
                runs = 4;
                clearRunSelection();
                submitbuttonClicked(SUBMIT_DELAY);
                break;
            case realmstudy.R.id.bfour_txt:
                runs = 4;
                clearRunSelection();
                submitbuttonClicked(SUBMIT_DELAY);
                break;
            case realmstudy.R.id.bSix_txt:
                runs = 6;
                clearRunSelection();
                submitbuttonClicked(SUBMIT_DELAY);
                break;
            case realmstudy.R.id.wide_txt:
                normal_delivery = false;
                extraType = CommanData.typeExtraEnum.WIDE;
                clearLegalSelection();
                break;
            case realmstudy.R.id.no_ball_txt:
                normal_delivery = false;
                extraType = CommanData.typeExtraEnum.NO_BALL;
                clearLegalSelection();
                break;
            case realmstudy.R.id.byes_txt:
                normal_delivery = false;
                extraType = CommanData.typeExtraEnum.L_BYES;
                clearLegalSelection();
                break;
            case realmstudy.R.id.leg_byes_txt:
                normal_delivery = false;
                extraType = CommanData.typeExtraEnum.LEG_BYES;
                clearLegalSelection();
                break;
            case realmstudy.R.id.granted_txt:
                normal_delivery = false;
                extraType = CommanData.typeExtraEnum.GRANTED;
                clearLegalSelection();
                break;
            case realmstudy.R.id.legal_ball_txt:
                normal_delivery = true;
                clearLegalSelection();
                break;

        }

        ((TextView) v).setTextColor(COLOR_SELECT);
    }

    /**
     * Set all text color of normal_delivery layout to unselect
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
    private void addScore(Wicket wicket) {
        InningsData inningsData = RealmDB.getInningsData(getActivity(), realm,
                lastInningsDataItem != null ? (lastInningsDataItem.getIndex() + 1) : 0,
                matchDetails.getMatch_id(), matchDetails.isFirstInningsCompleted());
        ScoreBoardData score_data = null;
        realm.beginTransaction();
        if (matchDetails.getMatchStatus() == CommanData.MATCH_NOT_YET_STARTED)
            matchDetails.setMatchStatus(CommanData.MATCH_STARTED_FI);
        else if (matchDetails.getMatchStatus() == CommanData.MATCH_BREAK_FI)
            matchDetails.setMatchStatus(CommanData.MATCH_STARTED_SI);

        inningsData.setNormalDelivery(normal_delivery);
        if (normal_delivery)
            inningsData.setBallType(CommanData.BALL_LEGAL);
        else
            inningsData.setBallType(extraType);
        inningsData.setMatch_id(matchDetails.getMatch_id());
        inningsData.setRun(runs);
        // inningsData.setScoreBoardData(CommanData.toString(score_data));
        inningsData.setStriker(striker.getpID());
        inningsData.setFirstInnings(!matchDetails.isFirstInningsCompleted());
        inningsData.setNonStriker(non_striker.getpID());
        inningsData.setCurrentBowler(current_bowler.getpID());
        inningsData.setNextBowler(next_bowler == null ? -1 : next_bowler.getpID());
        inningsData.setWicket(wicket);
        inningsData.setDelivery(getNextKey(inningsData, "delivery"));
        if (!normal_delivery && (extraType == CommanData.typeExtraEnum.NO_BALL || extraType == CommanData.typeExtraEnum.WIDE)) {
            System.out.println("_______OVer" + lastInningsDataItem.getOver() + "__" + Math.floor(lastInningsDataItem.getOver()) + "__" + (lastInningsDataItem.getOver() - Math.floor(lastInningsDataItem.getOver())));
            inningsData.setOver(lastInningsDataItem.getOver());
        } else {

            float over =RealmDB.BallinWhichOver(realm,matchDetails);
            inningsData.setOver(over);
        }
        BowlingProfile current_bowler_bf=RealmDB.getBowlingProfile(realm, current_bowler.getpID(), matchDetails.getMatch_id());
        //setting status of players
        RealmDB.getBattingProfile(realm, striker.getpID(), matchDetails.getMatch_id()).setCurrentStatus(CommanData.StatusBatting);
        RealmDB.getBattingProfile(realm, non_striker.getpID(), matchDetails.getMatch_id()).setCurrentStatus(CommanData.StatusBatting);
        current_bowler_bf.setCurrentBowlerStatus(CommanData.StatusInMatch);


//        if (lastInningsDataItem != null)
//            inningsData.setIndex(lastInningsDataItem.getIndex() + 1);
//        else
//            inningsData.setIndex(0);

        realm.commitTransaction();


        RealmDB.updateBowlingProfile(realm, matchDetails, current_bowler.getpID());
        RealmDB.updateBattingProfile(realm, matchDetails, striker.getpID());
        if(non_striker!=null)
        RealmDB.updateBattingProfile(realm, matchDetails, non_striker.getpID());
        realm.beginTransaction();
        score_data = setScoreDataForDB(wicket,inningsData.getOver());
        inningsData.setScoreBoardData(CommanData.toString(score_data));
        realm.commitTransaction();
        lastInningsDataItem = realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).findAll().last();
        current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
        System.out.println("____________AAAA" + lastInningsDataItem.getWicket());
        checkAndUpdateUI();
        System.out.println("____________AAAA2" + lastInningsDataItem.getWicket());

    }




    private ScoreBoardData setScoreDataForDB(Wicket wicket,float over) {
        int total_run = current_score_data.getTotalRuns() + runs;
        int total_balls = current_score_data.getTotalBalls();
        ScoreBoardData score_data = new ScoreBoardData();
        BatingProfile strikerProfile = RealmDB.getBattingProfile(realm, striker.getpID(), matchDetails.getMatch_id());
        BowlingProfile current_bowler_bf=RealmDB.getBowlingProfile(realm, current_bowler.getpID(), matchDetails.getMatch_id());
        BatingProfile non_strikerProfile = RealmDB.getBattingProfile(realm, non_striker.getpID(), matchDetails.getMatch_id());;
        boolean legal = true;
        if (!normal_delivery && (extraType == CommanData.typeExtraEnum.WIDE || extraType == CommanData.typeExtraEnum.NO_BALL))
            legal = false;
        if (normal_delivery) {

            //STRIKER
            if (striker != null) {
//                RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).setRuns(RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).getRuns() + runs);
                score_data.setStrikerName(striker.getName());
                score_data.setStrikerRun(strikerProfile.getRuns());
            }

            //NON_STRIKER
            if (non_striker != null) {
                score_data.setNonStrikerName(non_striker.getName());
                score_data.setNonStrikerRun(non_strikerProfile.getRuns());
                score_data.setNonStrikerBalls(non_strikerProfile.getBallFaced());
            }
            //CURRENT BOWLER
            if (current_bowler != null) {
//                RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).setBallsBowled(RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled() + 1);
//                RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted() + runs);

                score_data.setCurrentBowlerName(current_bowler.getName());
                score_data.setCurrentBowlerRuns(current_bowler_bf.getRunsGranted());
                int extra_ball=current_bowler_bf.getNoBall()+current_bowler_bf.getWide();
                score_data.setCurrentBowlerLegalBalls(current_bowler_bf.getBallsBowled()-extra_ball);
            }

            //NEXT BOWLER
            if (next_bowler != null) {
//                score_data.setNextBowlerName(next_bowler.getName());
//                next_bowler.getRecentBowlingProfile().setRunsGranted(next_bowler.getRecentBowlingProfile().getRunsGranted());
//                score_data.setNextBowlerRuns(next_bowler.getRecentBowlingProfile().getRunsGranted());
//                score_data.setNextBowlerBalls(next_bowler.getRecentBowlingProfile().getBallsBowled());
                score_data.setNextBowlerName(next_bowler.getName());
                //  RealmDB.getBowlingProfile( realm, next_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile( realm, next_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
                score_data.setNextBowlerRuns(RealmDB.getBowlingProfile(realm, next_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
                score_data.setNextBowlerBalls(RealmDB.getBowlingProfile(realm, next_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled());
            }
            score_data.setTotalRuns(total_run);
            score_data.setStrikerBalls(strikerProfile.getBallFaced());

            // RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).setBallFaced(RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).getBallFaced() + 1);

            total_balls = total_balls + 1;
        } else {

            //  RealmDB.updateBowlingProfile(realm,matchDetails,current_bowler.getpID());
            if (legal) {
                if (current_bowler != null) {
//                    RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).setBallsBowled(RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled() + 1);
//                    RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted() + runs);
                    score_data.setCurrentBowlerName(current_bowler.getName());
                    score_data.setCurrentBowlerRuns(current_bowler_bf.getRunsGranted());
                    score_data.setCurrentBowlerLegalBalls(current_bowler_bf.getBallsBowled());
                }
            }


           // BowlingProfile current_bowler_bowlingProfile = RealmDB.getBowlingProfile(realm, current_bowler.getpID(), matchDetails.getMatch_id());


            //STRIKER
//            if (extraType == CommanData.typeExtraEnum.L_BYES || extraType == CommanData.typeExtraEnum.LEG_BYES || extraType == CommanData.typeExtraEnum.WIDE) {
//                RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).setRuns(RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).getRuns());
//
//            } else
//                RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).setRuns(RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).getRuns() + runs);
            score_data.setNonStrikerRun(non_strikerProfile.getRuns());
            score_data.setStrikerName(striker.getName());
            score_data.setStrikerRun(strikerProfile.getRuns());
            //  RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).setBallFaced(RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).getBallFaced());
            // if (extraType == CommanData.typeExtraEnum.NO_BALL)
            score_data.setStrikerBalls(strikerProfile.getBallFaced());
//            else
//                score_data.setStrikerBalls(current_score_data.getStrikerBalls());
            score_data.setStrikerRun(strikerProfile.getRuns());

            //NON_STRIKER
            score_data.setNonStrikerName(non_striker.getName());
            score_data.setNonStrikerBalls(non_strikerProfile.getBallFaced());
            score_data.setNonStrikerRun(non_strikerProfile.getRuns());

            //CURRENT BOWLER
//            if (extraType == CommanData.typeExtraEnum.L_BYES || extraType == CommanData.typeExtraEnum.LEG_BYES
//                    || extraType == CommanData.typeExtraEnum.W_BYES || extraType == CommanData.typeExtraEnum.NB_BYES)
//                RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
//            else {
//                RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted() + runs + legalRun);
//            }
            score_data.setCurrentBowlerName(current_bowler.getName());
            score_data.setCurrentBowlerName(current_bowler.getName());
            // RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).setBallsBowled(RealmDB.getBowlingProfile( realm, current_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled());
            score_data.setCurrentBowlerLegalBalls(current_bowler_bf.getBallsBowled());
            score_data.setCurrentBowlerRuns(current_bowler_bf.getRunsGranted());

            switch (extraType) {

                case WIDE:
                    current_bowler_bf.setWide(current_bowler_bf.getWide() + 1);
                    break;
                case NO_BALL:
                    current_bowler_bf.setNoBall(current_bowler_bf.getNoBall() + 1);
                    break;
                case L_BYES:
                    current_bowler_bf.setByes(current_bowler_bf.getByes() + 1);
                    break;
                case LEG_BYES:
                    current_bowler_bf.setByes(current_bowler_bf.getByes() + 1);
                    break;
            }


            //NEXT BOWLER
            if (next_bowler != null) {
                score_data.setNextBowlerName(next_bowler.getName());
                //  RealmDB.getBowlingProfile( realm, next_bowler.getpID(), matchDetails.getMatch_id()).setRunsGranted(RealmDB.getBowlingProfile( realm, next_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
                score_data.setNextBowlerRuns(RealmDB.getBowlingProfile(realm, next_bowler.getpID(), matchDetails.getMatch_id()).getRunsGranted());
                score_data.setNextBowlerBalls(RealmDB.getBowlingProfile(realm, next_bowler.getpID(), matchDetails.getMatch_id()).getBallsBowled());
            }
//Totals

            //CURRENT BOWLER
            if (extraType == CommanData.typeExtraEnum.L_BYES || extraType == CommanData.typeExtraEnum.LEG_BYES) {
                score_data.setTotalRuns(total_run + 0);
                total_balls = total_balls + 1;
            } else {
                score_data.setTotalRuns(total_run + legalRun);
            }
//            }if (extraType == CommanData.typeExtraEnum.L_BYES || extraType == CommanData.typeExtraEnum.LEG_BYES)
//                total_balls = total_balls + 1;


        }
        boolean current_overCompleted = false;
        if (legal)
            current_overCompleted = (over%1==0);
        score_data.setTotalBalls(total_balls);
        //  score_data.setStrikerBalls(RealmDB.getBattingProfile( realm, striker.getpID(), matchDetails.getMatch_id()).getBallFaced());
        score_data.setNonStrikerBalls(non_strikerProfile.getBallFaced());
        score_data.setTotal_wicket(RealmDB.noOfWicket(getActivity(), realm, matchDetails.getMatch_id(), !matchDetails.isFirstInningsCompleted()));
        if (redoCount == 0 && undoCount == 0) {
            if (current_score_data.getTotalBalls() == 0) {
                if (runs % 2 == 1) {
                    score_data.setBatsmanSwitched(true);
                    score_data.setAskNextBowler(false);

                    //   Toast.makeText(getActivity(), "switching12", Toast.LENGTH_SHORT).show();
                } else score_data.setBatsmanSwitched(false);
            } else {
               // System.out.println("_____________aaaaaa" + nextBallinWhichOver() + "__" + Math.floor(lastInningsDataItem.getOver()) + "__" + matchDetails.getOvers());
//                if ((current_score_data.getTotalBalls() + (legal ? 1 : 0)) % 6 == 0
//                        && (current_score_data.getTotalBalls() + (legal ? 1 : 0)) / 6 < matchDetails.getOvers()) {
                if (!normal_delivery && (extraType == CommanData.typeExtraEnum.NO_BALL || extraType == CommanData.typeExtraEnum.WIDE)) {
//                    System.out.println("_______OVer"+ lastInningsDataItem.getOver() +"__"+Math.floor(lastInningsDataItem.getOver())+"__"+(lastInningsDataItem.getOver() -Math.floor(lastInningsDataItem.getOver())));
//                    inningsData.setOver(lastInningsDataItem.getOver());
                    score_data.setAskNextBowler(false);
                } else {
                    if (current_overCompleted && over < matchDetails.getOvers()) {
                        Toast.makeText(getActivity(), "asssssss" + matchDetails.getMatchStatus(), Toast.LENGTH_SHORT).show();

                        next_bowler = current_bowler;
                        assignToPlayer = 2;
                        boolean ishome = false;
                        if (!matchDetails.isHomeTeamBatting())
                            ishome = true;
                        ((MainFragmentActivity) getActivity()).showSelectplayer(matchDetails.getMatch_id(), ishome, current_bowler, getString(R.string.next_bowler), assignToPlayer);
                        score_data.setAskNextBowler(true);


                    } else
                        score_data.setAskNextBowler(false);
                }

                if (((runs % 2 == 1 && (current_score_data.getTotalBalls() + (legal ? 1 : 0)) % 6 != 0)
                        || (runs % 2 == 0 && (current_score_data.getTotalBalls() + (legal ? 1 : 0)) % 6 == 0)) && checkInningsGoingOn()) {
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

        score_data.setHomeTeam(matchDetails.getHomeTeam().nick_name);
        score_data.setAwayTeam(matchDetails.getAwayTeam().nick_name);
        score_data.setHomeTeamBatting(matchDetails.isHomeTeamBatting());
        if (matchDetails.isFirstInningsCompleted()) {
            score_data.setFirstInningsTotal(RealmDB.getFirstInningsTotal(realm, matchDetails));
            score_data.setfirstInningsWicket(RealmDB.noOfWicket(getActivity(), realm, matchDetails.getMatch_id(), true));

            score_data.setFirstIinningsOver(RealmDB.getFirstInningsOver(realm, matchDetails));
        } else {
            score_data.setMatchQuote(matchDetails.getToss().nick_name + " " + getString(R.string.won_and_elect) + " " + matchDetails.getChooseTo());
        }
        score_data.setShotAt(groundFragment.getCordinate());
        ArrayList<String> ss = current_score_data.getLastThreeOvers();
        if (ss == null)
            ss = new ArrayList<>();
        if (ss.size() > 23) {
            ss.remove(0);
        }
        ss.add(get_delivery_result(runs, wicket, normal_delivery, extraType));
        if (current_overCompleted)
            ss.add("|");
        score_data.setLastThreeOvers(ss);
        score_data.setTotalOver(String.valueOf(over));

        return score_data;
    }

    private String get_delivery_result(int runs, Wicket wicket, boolean legal, CommanData.typeExtraEnum extraType) {
        String type = "";
        type = runs + (wicket != null ? "o" : "") + (legal ? "" : extraType.toString().charAt(0));

        return type;
    }

    private void swapStriker(boolean withText) {
        Player dummy = striker;
        striker = non_striker;
        non_striker = dummy;
        swapStrikerText(current_score_data, withText);
    }

//

    private void checkUnOrRedo() {
        if (undoCount > 0) {
            if (lastInningsDataItem.getIndex() != 0) {
                InningsData id = realm.where(InningsData.class).equalTo("index", RealmDB.getInningsDataIndex((lastInningsDataItem.getIndex() - 1), matchDetails)).findFirst();
                if (id != null) {
                    //   System.out.println("__________UUU"+realm.where(InningsData.class).equalTo("match_id",matchDetails.getMatch_id()).findAllSorted("index").last().getScoreBoardData());
                    lastInningsDataItem = id;
                    current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);

                    undoCount = 0;
                }
            } else {
                //  Toast.makeText(MainActivity.this, "rajinimurugan2", Toast.LENGTH_SHORT).show();
            }
        }
        if (redoCount > 0) {
            InningsData id = realm.where(InningsData.class).equalTo("index", RealmDB.getInningsDataIndex((lastInningsDataItem.getIndex() + 1), matchDetails)).findFirst();
            if (id != null && id.getIndex() <= realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).equalTo("firstInnings", !matchDetails.isFirstInningsCompleted()).findAll().size()) {
                lastInningsDataItem = id;
                current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
                redoCount = 0;
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
        inningsData.setNormalDelivery(normal_delivery);
        inningsData.setRun(runs);
        inningsData.setScoreBoardData(CommanData.toString(score_data));
        inningsData.setStriker(striker.getpID());
        inningsData.setNonStriker(non_striker.getpID());
        inningsData.setCurrentBowler(current_bowler.getpID());
        inningsData.setOver(0);
        // inningsData.setNextBowler(null);

//        inningsData.setIndex(0);


        realm.commitTransaction();
        lastInningsDataItem = realm.where(InningsData.class).equalTo("match_id", matchDetails.getMatch_id()).findAll().last();
        current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);

        checkAndUpdateUI();
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
            ((MainFragmentActivity) getActivity()).closePrevSelectPlayer();
            if (dialogType == CommanData.DIALOG_OUT) {

                Wicket wicket = RealmDB.getWicket(getActivity(), realm, data);
                System.out.println("_____Out" + dialogType + "__" + data + "__" + wicket.getType());
                addScore(wicket);
                // System.out.println("_____Outsdf1" + dialogType + "__" + data + "__" + wicket.getType());
                if (wicket.getType() != CommanData.W_RUNOUT) {
                    realm.beginTransaction();
                    // System.out.println("_____Outsdf2" + dialogType + "__" + data + "__" + wicket.getType()+"__"+CommanData.toString( realm.copyFromRealm(wicket)));
                    current_score_data.setWicket(CommanData.toString(realm.copyFromRealm(wicket)));
                    //System.out.println("_____Outsdf3" + dialogType + "__" + data + "__" + wicket.getType());
                    lastInningsDataItem.setScoreBoardData(CommanData.toString(current_score_data));
                    realm.commitTransaction();

                    striker = null;

                } else if (wicket.getType() == CommanData.W_RUNOUT) {
                    Player p = RealmDB.getPlayer(realm, wicket.getBatsman());
                    if (p.getpID() == striker.getpID())
                        striker = null;
                    else
                        non_striker = null;

                }

            }


        } else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void messageFromDialog(int dialogType, boolean success, String data, String message, int assignTo) {
        System.out.println("_____Out" + dialogType + "__" + data + "__" + success);
        if (success) {
            ((MainFragmentActivity) getActivity()).closePrevSelectPlayer();
//            if (dialogType == CommanData.DIALOG_OUT) {
//
//                Wicket wicket = RealmDB.getWicket(getActivity(), realm, data);
//                System.out.println("_____Out" + dialogType + "__" + data + "__" + wicket.getType());
//                addScore(wicket);
//                // System.out.println("_____Outsdf1" + dialogType + "__" + data + "__" + wicket.getType());
//                if (wicket.getType() != CommanData.W_RUNOUT) {
//                    realm.beginTransaction();
//                    // System.out.println("_____Outsdf2" + dialogType + "__" + data + "__" + wicket.getType()+"__"+CommanData.toString( realm.copyFromRealm(wicket)));
//                    current_score_data.setWicket(CommanData.toString(realm.copyFromRealm(wicket)));
//                    //System.out.println("_____Outsdf3" + dialogType + "__" + data + "__" + wicket.getType());
//                    lastInningsDataItem.setScoreBoardData(CommanData.toString(current_score_data));
//                    realm.commitTransaction();
//
//                    striker = null;
//
//                } else if (wicket.getType() == CommanData.W_RUNOUT) {
//                    Player p = RealmDB.getPlayer( realm, wicket.getBatsman());
//                    if (p.getpID() == striker.getpID())
//                        striker = null;
//                    else
//                        non_striker = null;
//
//                }
//
//            } else {
            Player bb;

            bb = RealmDB.getPlayer(realm, Integer.parseInt(data));
            boolean ss = success;
            System.out.println("checkkkk" + ss);
//

            if (assignTo == 0) {
                striker = RealmDB.getPlayer(realm, bb.getpID());
                BatingProfile bf = RealmDB.getBattingProfile(realm, striker.getpID(), matchDetails.getMatch_id());
                realm.beginTransaction();
                if (matchDetails.isHomeTeamBatting())
                    matchDetails.addHomePlayer(striker);
                else
                    matchDetails.addAwayPlayer(striker);
                realm.commitTransaction();

            } else if (assignTo == 1) {
                if (striker.getpID() != bb.getpID()) {
                    non_striker = RealmDB.getPlayer(realm, bb.getpID());
                    BatingProfile bf = RealmDB.getBattingProfile(realm, non_striker.getpID(), matchDetails.getMatch_id());
                    if (bf == null)
                        bf = RealmDB.createBattingProfile(realm, non_striker.getpID(), matchDetails.getMatch_id());
                    realm.beginTransaction();
                    if (matchDetails.isHomeTeamBatting())
                        matchDetails.addHomePlayer(non_striker);
                    else
                        matchDetails.addAwayPlayer(non_striker);
                    realm.commitTransaction();
                } else
                    Toast.makeText(getContext(), getString(R.string.player_in_striker_end), Toast.LENGTH_SHORT).show();
            } else if (assignTo == 2) {
                current_bowler = RealmDB.getPlayer(realm, bb.getpID());
                BowlingProfile bf = RealmDB.getBowlingProfile(realm, current_bowler.getpID(), matchDetails.getMatch_id());
                if (bf == null)
                    bf = RealmDB.getBowlingProfile(realm, current_bowler.getpID(), matchDetails.getMatch_id());
                realm.beginTransaction();
                if (matchDetails.isHomeTeamBatting())
                    matchDetails.addAwayPlayer(current_bowler);
                else
                    matchDetails.addHomePlayer(current_bowler);
                realm.commitTransaction();
            } else if (assignTo == 3) {
                next_bowler = RealmDB.getPlayer(realm, bb.getpID());
                BowlingProfile bf = RealmDB.getBowlingProfile(realm, next_bowler.getpID(), matchDetails.getMatch_id());
                if (bf == null)
                    bf = RealmDB.getBowlingProfile(realm, next_bowler.getpID(), matchDetails.getMatch_id());
                realm.beginTransaction();
                if (matchDetails.isHomeTeamBatting())
                    matchDetails.addAwayPlayer(next_bowler);
                else
                    matchDetails.addHomePlayer(next_bowler);
                realm.commitTransaction();
            } else if (assignTo == 5) {
                Player dummy = RealmDB.getPlayer(realm, bb.getpID());
                BatingProfile bf = RealmDB.getBattingProfile(realm, dummy.getpID(), matchDetails.getMatch_id());
                if (bf == null)
                    bf = RealmDB.createBattingProfile(realm, dummy.getpID(), matchDetails.getMatch_id());
                realm.beginTransaction();
                if (matchDetails.isHomeTeamBatting())
                    matchDetails.addAwayPlayer(dummy);
                else
                    matchDetails.addHomePlayer(dummy);
                //bf.setCurrentStatus(CommanData.StatusBatting);
                // next_bowler.setRecentBowlingProfile(bf);

                current_score_data.setNextBatsmanName(dummy.getName());
                current_score_data.setNextBatsmanBalls(bf.getBallFaced());
                current_score_data.setNextBatsmanRun(bf.getRuns());
                lastInningsDataItem.setScoreBoardData(CommanData.toString(current_score_data));
                realm.commitTransaction();
                //   System.out.println("___________www"+lastInningsDataItem.getWicket().getBatsman());
                if (striker == null) {
                    striker = null;
                    striker = dummy;
                } else {
                    non_striker = null;
                    non_striker = dummy;
                }
            }
            //  selectPlayerDialog.dismiss();}
            checkAndUpdateUI();

        } else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void messageFromDialog(int dialogType, boolean success, ArrayList<Integer> data, String message) {

    }
}





