package realmstudy.mainFunctions;

/**
 * Created by developer on 14/12/16.
 */
public class AddScore  {






    /**
     * Add score to database
     */
    private void addScore() {

//        int total_run = current_score_data.getTotalRuns() + runs;
//        int total_balls = current_score_data.getTotalBalls();
//
//
//        realm.beginTransaction();
//        InningsData inningsData = realm.createObject(InningsData.class);
//        ScoreBoardData score_data = new ScoreBoardData();
//        score_data.setStrikerRun(striker.getRuns() + runs);
//        score_data.setNonStrikerRun(non_striker.getRuns());
//        score_data.setTotalRuns(total_run);
//        score_data.setNonStrikerRun(non_striker.getRuns());
//        score_data.setNonStrikerBalls(non_striker.getBallFaced());
//        score_data.setStrikerName(striker.getName());
//        score_data.setNonStrikerName(non_striker.getName());
//        if (legal) {
//            score_data.setTotalRuns(total_run);
//            score_data.setStrikerBalls(current_score_data.getStrikerRun() + 1);
//
//            striker.setBallFaced(striker.getBallFaced() + 1);
//            total_balls = total_balls + 1;
//        } else {
//            score_data.setTotalRuns(total_run + legalRun);
//            striker.setBallFaced(striker.getBallFaced());
//            score_data.setStrikerBalls(current_score_data.getStrikerBalls());
//        }
//        score_data.setTotalBalls(total_balls);
//        score_data.setStrikerBalls(striker.getBallFaced());
//        score_data.setNonStrikerBalls(non_striker.getBallFaced());
//
//
//        if (redoCount == 0 && undoCount == 0) {
//            if ((runs % 2 == 1 && current_score_data.getTotalBalls() == 1)) {
//                score_data.setBatsmanSwitched(true);
//                // switchStriker(true);
//
//                Toast.makeText(MainActivity.this, "switching1", Toast.LENGTH_SHORT).show();
//            } else if ((runs % 2 == 1 && current_score_data.getTotalBalls() % 6 != 0)
//                    || (runs % 2 == 0 && current_score_data.getTotalBalls() % 6 == 0)) {
//
////                switchStriker(true);
////                Toast.makeText(MainActivity.this, "switching", Toast.LENGTH_SHORT).show();
//                score_data.setBatsmanSwitched(true);
//
//            }
//        } else {
//            // striker=l
//            score_data.setBatsmanSwitched(false);
//        }
//
//
//        //    score_data.setTotalRuns(total_run);
//        //striker.setBallFaced(legal ? striker.getBallFaced() + 1 : striker.getBallFaced());
//        inningsData.setNormalDelivery(legal);
//        inningsData.setRun(runs);
//        inningsData.setScoreBoardData(CommanData.toString(score_data));
//        inningsData.setStriker(striker);
//        inningsData.setNonStriker(non_striker);
//        striker.setRuns(striker.getRuns() + runs);
//        if (lastInningsDataItem != null)
//            inningsData.setIndex(lastInningsDataItem.getIndex() + 1);
//        else
//            inningsData.setIndex(0);
//
//
//        realm.commitTransaction();
//        lastInningsDataItem = realm.where(InningsData.class).findAll().get(realm.where(InningsData.class).findAll().size() - 1);
//        current_score_data = CommanData.fromJson(lastInningsDataItem.getScoreBoardData(), ScoreBoardData.class);
//        updateUI();

    }


}
