package realmstudy;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.regex.Pattern;


/**
 * Created by developer on 9/12/16.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
       // Stetho.initializeWithDefaults(this);
        initializeStetho(this);
    }



    private void initializeStetho(final Context context) {
        // See also: Stetho.initializeWithDefaults(Context)
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
//        RealmInspectorModulesProvider.builder(this)
//                .withFolder(getCacheDir())
//                .withMetaTables()
//                .withDescendingOrder()
//                .withLimit(1000)
//                .databaseNamePattern(Pattern.compile(".+\\.realm"))
//                .build();

        RealmInspectorModulesProvider.builder(this)
                .withFolder(getCacheDir())
                .withMetaTables()
                .withDescendingOrder()
                .withLimit(1000)
                .databaseNamePattern(Pattern.compile(".+\\.realm"))
                .build();

    }





}