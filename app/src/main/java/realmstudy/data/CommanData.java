package realmstudy.data;

import com.google.gson.Gson;

/**
 * Created by developer on 9/12/16.
 */
public class CommanData {

    public static String API_BASE_URL = "http://192.168.1.169:1009/mobileapi117/index/";

    public static final String AddNewTeam = "AddNewTeam";

    public static final int StatusFree = 0;
    public static final int StatusBatting = 1;
    public static final int StatusInMatch = 2;
    public static final int StatusOut = 3;
    public static final int StatusBowling=4;
    public static final int StatusBowled=5;

    public static final int W_CAUGHT = 0;
    public static final int W_BOWLED = 1;
    public static final int W_HITOUT = 2;
    public static final int W_RUNOUT = 3;
    public static final int W_LBW =4;

//Match Status
    public static final int MATCH_NOT_YET_STARTED = 0;
    public static final int MATCH_STARTED_FI = 1;
    public static final int MATCH_STARTED_SI = 2;
    public static final int MATCH_BREAK_FI=3;
    public static final int MATCH_ABORT=4;
    public static final int MATCH_DELAYED=5;
    public static final int MATCH_STOP=6;
    public static final int MATCH_COMPLETED=7;



    //Dialog Type
    public static final int DIALOG_OUT = 0;
    public static final int DIALOG_NEW_PLAYER = 1;
    public static final int DIALOG_SELECT_PLAYER = 2;
    public static final int DIALOG_SELECT_MULTI_PLAYER=3;
    public static final int DIALOG_NEW_TEAM=4;


    //Ball Type
    public static final int BALL_LEGAL = 0;
    public static final int BALL_WIDE = 1;
    public static final int BALL_NO_BALL = 2;
    public static final int BALL_NO_OVER_STEP=3;
    public static final int BALL_LEGAL_BYES=4;
    public static final int BALL_WIDE_BYES=5;
    public static final int BALL_N0_BALL_BYES=5;
    public static final int BALL_LB=6;

//
public static enum typeExtraEnum {WIDE, NO_BALL, W_BYES,NB_BYES,L_BYES, LEG_BYES, STEP_NO_BALL, GRANTED}
    public static <T> T fromJson(String data, Class<T> classn) {

        return new Gson().fromJson(data, classn);
    }

    public static String toString(Object s) {
        return new Gson().toJson(s);
    }
}
