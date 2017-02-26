package realmstudy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import realmstudy.data.SessionSave;

/**
 * Created by developer on 26/12/16.
 */
public class SplashAct extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SessionSave.getBooleanSession(SessionSave.CURRENTLY_MATCH_GOING, SplashAct.this)) {
            Intent i=new Intent(SplashAct.this, MainFragmentActivity.class);
            i.putExtra("fragmentToLoad","AddNewTeam");
            startActivity(i);
            finish();
        }else{
            Intent i=new Intent(SplashAct.this, MainFragmentActivity.class);
            i.putExtra("fragmentToLoad","AddNewTeam");
            startActivity(i);
            finish();
        }
    }
}
