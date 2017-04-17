package realmstudy;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.regex.Pattern;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import realmstudy.di.component.DaggerStorageComponent;
import realmstudy.di.component.StorageComponent;
import realmstudy.di.modules.StorageModule;
import realmstudy.extras.OverrideFont;


/**
 * Created by developer on 9/12/16.
 */

public class MyApplication extends Application {
    StorageComponent storageComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        // Stetho.initializeWithDefaults(this);
        initializeStetho(this);

        OverrideFont.setDefaultFont(this, "DEFAULT", "HelveticaLT35Thin.ttf");
        OverrideFont.setDefaultFont(this, "MONOSPACE", "HelveticaLT35Thin.ttf");
        OverrideFont.setDefaultFont(this, "SANS_SERIF", "HelveticaLT35Thin.ttf");


        storageComponent = DaggerStorageComponent
                .builder()
                .storageModule(new StorageModule(this))
                .build();

    }

    public StorageComponent getComponent() {
        return storageComponent;
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