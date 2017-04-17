package realmstudy;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import realmstudy.data.SessionSave;

/**
 * Created by developer on 26/12/16.
 */
public class SplashAct extends AppCompatActivity {

   // public static RealmConfiguration config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        Realm.init(this);
//
//
//
//         config = new RealmConfiguration.Builder()
//                .schemaVersion(1) // Must be bumped when the schema changes
//                .migration(migration) // Migration to run instead of throwing an exception
//                .build();


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

    RealmMigration migration = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            // DynamicRealm exposes an editable schema
            RealmSchema schema = realm.getSchema();
            System.out.println("______sssss"+oldVersion+"__"+newVersion);
            // Migrate to version 1: Add a new class.
            // Example:
            // public Person extends RealmObject {
            //     private String name;
            //     private int age;
            //     // getters and setters left out for brevity
            // }
//            if (oldVersion == 0) {
//                schema.create("Person")
//                        .addField("name", String.class)
//                        .addField("age", int.class);
//                oldVersion++;
//            }

            // Migrate to version 2: Add a primary key + object references
            // Example:
            // public Person extends RealmObject {
            //     private String name;
            //     @PrimaryKey
            //     private int age;
            //     private Dog favoriteDog;
            //     private RealmList<Dog> dogs;
            //     // getters and setters left out for brevity
            // }
            if (oldVersion == 0) {
                schema.get("MatchDetails").addPrimaryKey("match_id");

                oldVersion++;
            }
        }
    };
}
