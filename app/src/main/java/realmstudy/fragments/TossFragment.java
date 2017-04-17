package realmstudy.fragments;


import android.app.Activity;
import android.content.Context;

import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;


import realmstudy.MyApplication;
import realmstudy.data.RealmObjectData.MatchDetails;
import realmstudy.data.RealmObjectData.Team;
import realmstudy.lib.CustomAnimationDrawable;
import realmstudy.MainActivity;
import realmstudy.MainFragmentActivity;
import realmstudy.R;
import realmstudy.databaseFunctions.RealmDB;
import realmstudy.lib.Animation;
import realmstudy.lib.Coin;

import java.util.EnumMap;

import javax.inject.Inject;

import io.realm.Realm;


public class TossFragment extends Fragment {

    // debugging tag
    private static final String TAG = TossFragment.class.getSimpleName();

    // extra flag indicating we should open the settings activity when the app loads
    public static final String OPEN_SETTINGS_FLAG = "EXTRA_OPEN_SETTINGS";

    // version of the settings schema used by this codebase
    private static final int SCHEMA_VERSION = 6;
    View v;
    EnumMap<Animation.ResultState, Drawable> coinImagesMap;

    private Drawable heads = null;
    private Team homeTeam;
    private Team awayTeam;
    private MatchDetails md;

    private LinearLayout main_layout, choose_to_lay;
    private TextView Noofplayers;
    private Spinner no_of_players;
    private TextView overss;
    private EditText total_overs;
    private TextView venues;
    private EditText venue;
    private TextView toss;
    private TextView team, retoss;
    private RadioGroup like_bat_bowl;
    private ImageView coin_image_view;
    private RadioGroup choose_to_bat_bowl, team_ask_toss_radio, toss_radio_group;
    private android.support.v7.widget.AppCompatButton start_match;
    RadioButton team_ask_home, like_to_bat, choose_to_bat, toss_head;
    private boolean tossWon;
    private int match_id = -1;
    private boolean homeWin;
    LinearLayout before_toss_lay, after_toss_lay;
    private TextView toss_won_detail;
    private RadioButton team_ask_away;
    @Inject
    Realm realm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Bundle b = getArguments();
            if (b != null)
                match_id = b.getInt("match_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.toss_fragment, container, false);
        ((MyApplication) getActivity().getApplication()).getComponent().inject(this);
        try {
            Bundle b = getArguments();
            String s[] = b.getString("teamIDs").split("__");
            homeTeam = realm.where(Team.class).equalTo("team_id", Integer.parseInt(s[0])).findFirst();
            awayTeam = realm.where(Team.class).equalTo("team_id", Integer.parseInt(s[1])).findFirst();
            match_id = b.getInt("match_id");
            System.out.println("TeamName____" + homeTeam.name + "___" + awayTeam.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // initialize the coin image and result text views
        initViews();

        // initialize the sounds
        initSounds();

        // initialize the coin maps
        Animation.init();
        coinImagesMap = new EnumMap<Animation.ResultState, Drawable>(Animation.ResultState.class);


        if (tapper == null) {
            tapper = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!venue.getText().toString().trim().isEmpty())
                        if (!total_overs.getText().toString().trim().isEmpty()) {
                            flipCoin();
                        } else
                            Toast.makeText(getActivity(), "total", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "venue", Toast.LENGTH_SHORT).show();
                }
            };
        }

        start_match = (AppCompatButton) v.findViewById(R.id.start_match);

        start_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flipCounter >= 1)
                    if (!venue.getText().toString().trim().isEmpty())
                        if (!total_overs.getText().toString().trim().isEmpty()) {


                            String chooseTo = "bowl";
                            if (choose_to_bat_bowl.getCheckedRadioButtonId() == choose_to_bat.getId())
                                chooseTo = "bat";


                            md = RealmDB.UpdateorCreateMatchDetail(getActivity(), realm, homeTeam, awayTeam, chooseTo, homeWin ? homeTeam : awayTeam, Integer.parseInt(total_overs.getText().toString()), venue.getText().toString(), no_of_players.getSelectedItemPosition() + 2, 0, match_id);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Bundle b = new Bundle();
                                    b.putInt("match_id", md.getMatch_id());
                                    MainActivity mf = new MainActivity();
                                    mf.setArguments(b);
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mainFrag, mf).commit();
                                }
                            }, 100);

                        } else
                            Toast.makeText(getActivity(), "total", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "venue", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "coin", Toast.LENGTH_SHORT).show();

            }
        });

        /*statsResetButton.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                resetStatistics();
            }
        })*/
        ;
        return v;
    }

    private Drawable tails = null;

    private Drawable edge = null;

    private Drawable background = null;

    private final Coin theCoin = Coin.getInstance();

    //  private ShakeListener shaker;

    private OnClickListener tapper;

    private Boolean currentResult = true;

    private Boolean previousResult = true;

    private ImageView coinImage;

    private LinearLayout mainLayout;

    private CustomAnimationDrawable coinAnimation;


    private int flipCounter = 0;

    private int headsCounter = 0;

    private int tailsCounter = 0;


    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");

        resetCoin();
        resetInstructions();
        loadResources();
        resumeListeners();


        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");

        pauseListeners();

        if (coinAnimation != null) {

            coinAnimation.removeCallbacks();
        }


        super.onPause();
    }


    private void flipCoin() {
        Log.d(TAG, "flipCoin()");

        flipCounter++;
        Log.d(TAG, "flipCounter=" + flipCounter);

        final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        // we're in the process of flipping the coin
        Animation.ResultState resultState = Animation.ResultState.UNKNOWN;

        // pause the shake listener until the result is rendered
        pauseListeners();


        // flip the coin and update the state with the result
        boolean flipResult = theCoin.flip();
        if (flipResult) {
            headsCounter++;
        } else {
            tailsCounter++;
        }
        Log.d(TAG, "headsCounter=" + headsCounter + " | tailsCounter=" + tailsCounter);
        resultState = updateState(flipResult);

        // update the screen with the result of the flip
        renderResult(resultState);

    }

    private void resetCoin() {
        Log.d(TAG, "resetCoin()");

        // hide the animation and draw the reset image
        displayCoinAnimation(false);
        displayCoinImage(true);
        coinImage.setImageDrawable(getResources().getDrawable(R.drawable.tap_me));
//        resultText.setText(" ");
        Animation.clearAnimations();
        coinImagesMap.clear();
    }

    private void resetInstructions() {
        Log.d(TAG, "resetInstructions()");

//
    }

    private Animation.ResultState updateState(final boolean flipResult) {
        // Analyze the current coin state and the new coin state and determine
        // the proper transition between the two.
        // true = HEADS | false = TAILS

        Log.d(TAG, "updateState()");

        Animation.ResultState resultState = Animation.ResultState.UNKNOWN;
        currentResult = flipResult;

        // this is easier to read than the old code
        if (previousResult == true && currentResult == true) {
            resultState = Animation.ResultState.HEADS_HEADS;
        }
        if (previousResult == true && currentResult == false) {
            resultState = Animation.ResultState.HEADS_TAILS;
        }
        if (previousResult == false && currentResult == true) {
            resultState = Animation.ResultState.TAILS_HEADS;
        }
        if (previousResult == false && currentResult == false) {
            resultState = Animation.ResultState.TAILS_TAILS;
        }

        // update the previousResult for the next flip
        previousResult = currentResult;

        return resultState;
    }

    // check the coin preference and determine how to load its resources
    private void loadResources() {
        Log.d(TAG, "loadResources()");


        loadInternalResources();
//
    }

    // load resources internal to the CoinFlip package
    private void loadInternalResources() {
        Log.d(TAG, "loadInternalResources()");

        // load the images
        heads = getResources().getDrawable(R.drawable.heads);
        tails = getResources().getDrawable(R.drawable.tails);
        edge = getResources().getDrawable(R.drawable.edge);
        background = getResources().getDrawable(R.drawable.background);

        // only do all the CPU-intensive animation rendering if animations are enabled
        //  if (Settings.getAnimationPref(this)) {
        // render the animation for each result state and store it in the
        // animations map
        Animation.generateAnimations(heads, tails, edge, background);

        //  }

        // add the appropriate image for each result state to the images map
        // WTF? There's some kind of rendering bug if you use the "heads" or
        // "tails" variables here...
        coinImagesMap.put(Animation.ResultState.HEADS_HEADS, heads);
        coinImagesMap.put(Animation.ResultState.HEADS_TAILS, tails);
        coinImagesMap.put(Animation.ResultState.TAILS_HEADS, heads);
        coinImagesMap.put(Animation.ResultState.TAILS_TAILS, tails);
    }


    private void renderResult(final Animation.ResultState resultState) {
        Log.d(TAG, "renderResult()");

        Drawable coinImageDrawable;

        // hide the static image and clear the text
        displayCoinImage(false);
        displayCoinAnimation(false);
//        resultText.setText("");

        // display the result
        // if (Settings.getAnimationPref(this)) {
        // load the appropriate coin animation based on the state
        coinAnimation = Animation.getAnimation(resultState);
        coinAnimation.setAnimationCallback(new CustomAnimationDrawable.AnimationCallback() {
            @Override
            public void onAnimationFinish() {
//                    playCoinSound();
                updateResultText(resultState);
                resumeListeners();
            }
        });

        // hide the static image and render the animation
        displayCoinImage(false);
        displayCoinAnimation(true);
        coinImage.setBackgroundDrawable(coinAnimation);
        coinAnimation.stop();
        coinAnimation.start();
        updateResultText(resultState);

    }

    private void initSounds() {
        // MediaPlayer was causing ANR issues on some devices.
        // SoundPool should be more efficient.

        Log.d(TAG, "initSounds()");
//        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
//        soundCoin = soundPool.load(this, R.raw.coin, 1);
//        soundOneUp = soundPool.load(this, R.raw.oneup, 1);

    }


    private void updateResultText(final Animation.ResultState resultState) {
        Log.d(TAG, "updateResultText()");

        // if (Settings.getTextPref(this)) {
        boolean homeTeamAskTask = team_ask_toss_radio.getCheckedRadioButtonId() == team_ask_home.getId();
        boolean tossedHead = false;

        switch (resultState) {
            case HEADS_HEADS:
            case TAILS_HEADS:
//                    resultText.setText("head");
                //resultText.setTextColor(getResources().getColor(R.color.lime));
                tossedHead = true;
                System.out.println("nagaaaaa____head");
                break;
            case HEADS_TAILS:
            case TAILS_TAILS:
                System.out.println("nagaaaaa____tails");
//                    resultText.setText("tail");
                //  resultText.setTextColor(getResources().getColor(R.color.red));
                break;
            default:
//                    resultText.setText("tap_me");
                //resultText.setTextColor(getResources().getColor(R.color.yellow));
                break;
        }


        tossWon = (toss_radio_group.getCheckedRadioButtonId() == toss_head.getId() && tossedHead) || (toss_radio_group.getCheckedRadioButtonId() != toss_head.getId() && !tossedHead);

        homeWin = false;
        if (team_ask_toss_radio.getCheckedRadioButtonId() == team_ask_home.getId() && tossWon || team_ask_toss_radio.getCheckedRadioButtonId() != team_ask_home.getId() && !tossWon)
            homeWin = true;
        System.out.println("______________" + tossWon);
        if (getActivity() != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (toss_won_detail != null) {
                        toss_won_detail.setText((homeWin ? homeTeam.nick_name : awayTeam.nick_name).trim() + "   " + getString(R.string.won_toss));
                        resetCoin();
                        before_toss_lay.setVisibility(View.GONE);
                        after_toss_lay.setVisibility(View.VISIBLE);

                        resetInstructions();
                        loadResources();
                        resumeListeners();
                    }
                }
            }, 2000);
        }


    }


    private void displayCoinAnimation(final boolean flag) {
        Log.d(TAG, "displayCoinAnimation()");

        // safety first!
        if (coinAnimation != null) {
            if (flag) {
                coinAnimation.setAlpha(255);
            } else {
                coinAnimation.setAlpha(0);
            }
        }
    }

    private void displayCoinImage(final boolean flag) {
        Log.d(TAG, "displayCoinImage()");

        // safety first!
        if (coinImage != null) {
            if (flag) {
                // get rid of the animation background
                coinImage.setBackgroundDrawable(null);
                coinImage.setAlpha(255);
            } else {
                coinImage.setAlpha(0);
            }
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews()");

        coinImage = (ImageView) v.findViewById(R.id.coin_image_view);
//        resultText = (TextView)view.findViewById(R.id.result_text_view);
//        instructionsText = (TextView)view .findViewById(R.id.instructions_text_view);
        mainLayout = (LinearLayout) v.findViewById(R.id.main_layout);
//        headsStatText = (TextView)view .findViewById(R.id.heads_stat_text_view);
//        tailsStatText = (TextView)view .findViewById(R.id.tails_stat_text_view);
//        statsResetButton = (Button)view.findViewById(R.id.stats_reset_button);
//        statsLayout = (LinearLayout)view. findViewById(R.id.statistics_layout);

        main_layout = (LinearLayout) v.findViewById(R.id.main_layout);
        Noofplayers = (TextView) v.findViewById(R.id.Noofplayers);
        no_of_players = (Spinner) v.findViewById(R.id.no_of_players);

        overss = (TextView) v.findViewById(R.id.overss);
        total_overs = (EditText) v.findViewById(R.id.total_overs);
        venues = (TextView) v.findViewById(R.id.venues);
        venue = (EditText) v.findViewById(R.id.venue);
        toss = (TextView) v.findViewById(R.id.toss);
        team = (TextView) v.findViewById(R.id.team);
        toss_won_detail = (TextView) v.findViewById(R.id.toss_won_detail);
        like_bat_bowl = (RadioGroup) v.findViewById(R.id.like_bat_bowl);
        coin_image_view = (ImageView) v.findViewById(R.id.coin_image_view);
        choose_to_bat_bowl = (RadioGroup) v.findViewById(R.id.choose_to_bat_bowl);
        team_ask_toss_radio = (RadioGroup) v.findViewById(R.id.team_ask_toss_radio);
        toss_radio_group = (RadioGroup) v.findViewById(R.id.toss_radio_group);
        start_match = (android.support.v7.widget.AppCompatButton) v.findViewById(R.id.start_match);
        team_ask_home = (RadioButton) v.findViewById(R.id.team_ask_home);
        team_ask_away = (RadioButton) v.findViewById(R.id.team_ask_away);
        like_to_bat = (RadioButton) v.findViewById(R.id.like_to_bat);
        choose_to_bat = (RadioButton) v.findViewById(R.id.choose_to_bat);
        //  toss_head_tail = (RadioGroup) v.findViewById(R.id.toss_head_tail);
        toss_head = (RadioButton) v.findViewById(R.id.toss_head);
        choose_to_lay = (LinearLayout) v.findViewById(R.id.choose_to_lay);
        retoss = (TextView) v.findViewById(R.id.retoss);
        before_toss_lay = (LinearLayout) v.findViewById(R.id.before_toss_lay);
        after_toss_lay = (LinearLayout) v.findViewById(R.id.after_toss_lay);


        retoss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                before_toss_lay.setVisibility(View.VISIBLE);
                after_toss_lay.setVisibility(View.GONE);
            }
        });

        SpinnerAdapter adap = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text_item, getResources().getStringArray(R.array.no_of_players_in_match));
        no_of_players.setAdapter(adap);
        team_ask_home.setText(homeTeam.nick_name);
        team_ask_away.setText(awayTeam.nick_name);
    }

    private void pauseListeners() {
        Log.d(TAG, "pauseListeners()");
//        if (shaker != null) {
//            shaker.pause();
//        }
        if (tapper != null) {
            coinImage.setOnClickListener(null);
        }
    }

    private void resumeListeners() {
        Log.d(TAG, "resumeListeners()");


//        }
        if (tapper != null) {
            coinImage.setOnClickListener(tapper);
        }
    }

}
