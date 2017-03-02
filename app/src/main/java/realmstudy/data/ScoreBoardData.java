package realmstudy.data;

import io.realm.RealmObject;
import realmstudy.data.RealmObjectData.Wicket;

/**
 * Created by developer on 9/12/16.
 */
public class ScoreBoardData  {
    int totalRuns;
    int totalBalls;
    int StrikerRun;
    int StrikerBalls;
    int NonStrikerRun;
    int NonStrikerBalls;
    int currentBowlerBalls,currentBowlerRuns;
    int nextBowlerBalls,nextBowlerRuns;
    String StrikerName,NonStrikerName;
    int nextBatsman=-1;
    int nextBatsmanRun;
    int nextBatsmanBalls;
    Wicket wicket;
    String CurrentBowlerName,NextBowlerName;


    public int getNextBatsman() {
        return nextBatsman;
    }

    public void setNextBatsman(int nextBatsman) {
        this.nextBatsman = nextBatsman;
    }

    public int getNextBatsmanRun() {
        return nextBatsmanRun;
    }

    public void setNextBatsmanRun(int nextBatsmanRun) {
        this.nextBatsmanRun = nextBatsmanRun;
    }

    public int getNextBatsmanBalls() {
        return nextBatsmanBalls;
    }

    public void setNextBatsmanBalls(int nextBatsmanBalls) {
        this.nextBatsmanBalls = nextBatsmanBalls;
    }



    public Wicket getWicket() {
        return wicket;
    }

    public void setWicket(Wicket wicket) {
        this.wicket = wicket;
    }




    boolean batsmanSwitched;

    public boolean isBowlerSwitched() {
        return bowlerSwitched;
    }

    public void setAskNextBowler(boolean bowlerSwitched) {
        this.bowlerSwitched = bowlerSwitched;
    }

    boolean bowlerSwitched;

    public int getCurrentBowlerBalls() {
        return currentBowlerBalls;
    }

    public void setCurrentBowlerBalls(int currentBowlerBalls) {
        this.currentBowlerBalls = currentBowlerBalls;
    }

    public int getCurrentBowlerRuns() {
        return currentBowlerRuns;
    }

    public void setCurrentBowlerRuns(int currentBowlerRuns) {
        this.currentBowlerRuns = currentBowlerRuns;
    }

    public int getNextBowlerBalls() {
        return nextBowlerBalls;
    }

    public void setNextBowlerBalls(int nextBowlerBalls) {
        this.nextBowlerBalls = nextBowlerBalls;
    }

    public int getNextBowlerRuns() {
        return nextBowlerRuns;
    }

    public void setNextBowlerRuns(int nextBowlerRuns) {
        this.nextBowlerRuns = nextBowlerRuns;
    }



    public String getCurrentBowlerName() {
        return CurrentBowlerName;
    }

    public void setCurrentBowlerName(String currentBowlerName) {
        CurrentBowlerName = currentBowlerName;
    }

    public String getNextBowlerName() {
        return NextBowlerName;
    }

    public void setNextBowlerName(String nextBowlerName) {
        NextBowlerName = nextBowlerName;
    }



    public boolean isBatsmanSwitched() {
        return batsmanSwitched;
    }

    public void setBatsmanSwitched(boolean batsmanSwitched) {
        this.batsmanSwitched = batsmanSwitched;
    }



    public String getStrikerName() {
        return StrikerName;
    }

    public void setStrikerName(String StrikerName) {
        this.StrikerName = StrikerName;
    }

    public String getNonStrikerName() {
        return NonStrikerName;
    }

    public void setNonStrikerName(String NonStrikerName) {
        this.NonStrikerName = NonStrikerName;
    }

    public int getStrikerRun() {
        return StrikerRun;
    }

    public void setStrikerRun(int StrikerRun) {
        this.StrikerRun = StrikerRun;
    }

    public int getStrikerBalls() {
        return StrikerBalls;
    }

    public void setStrikerBalls(int StrikerBalls) {
        this.StrikerBalls = StrikerBalls;
    }

    public int getNonStrikerRun() {
        return NonStrikerRun;
    }

    public void setNonStrikerRun(int NonStrikerRun) {
        this.NonStrikerRun = NonStrikerRun;
    }

    public int getNonStrikerBalls() {
        return NonStrikerBalls;
    }

    public void setNonStrikerBalls(int NonStrikerBalls) {
        this.NonStrikerBalls = NonStrikerBalls;
    }



    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public int getTotalBalls() {
        return totalBalls;
    }

    public void setTotalBalls(int totalBalls) {
        this.totalBalls = totalBalls;
    }

    public String getTotalOver(){
       return ballsToOver(totalBalls);
    }

    public String getCurrentBowlerOver(){
        return ballsToOver(currentBowlerBalls);
    }
    public String getNextBowlerOver(){
        return ballsToOver(nextBowlerBalls);
    }

    private String  ballsToOver(int balls){
        String over="0.0";
        if(balls>=6)
        {
            over= (balls/6)+"."+(balls%6);
        }else{
            over="0."+balls;
        }
        return  over;
    }

}
