package realmstudy.fragments.DialogFragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import realmstudy.R;
import realmstudy.data.RealmObjectData.Team;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by developer on 26/12/16.
 */
public class NewTeamDialog extends DialogFragment {
    int mNum;
    private EditText home_team_name, nick_name_txt;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static NewTeamDialog newInstance(int num) {
        NewTeamDialog f = new NewTeamDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;

        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.team_popup, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

        home_team_name = (EditText) v.findViewById(R.id.home_team_name);
        nick_name_txt = (EditText) v.findViewById(R.id.nick_name_txt);
        v.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = home_team_name.getText().toString();
                final String location = nick_name_txt.getText().toString();
                if (!name.trim().isEmpty() ) {
                    if(!location.trim().isEmpty()){
                    Realm.init(getActivity());
                    RealmConfiguration config = new RealmConfiguration.Builder()
                            .build();
                    Realm realm = Realm.getInstance(config);

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Team teamObj = realm.createObject(Team.class);
                            teamObj.team_id = realm.where(Team.class).findAll().size();
                            teamObj.name = name;
                            teamObj.nick_name = location;
                        }
                    });
                    nick_name_txt.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    dismiss();
                }else{
                        Toast.makeText(getActivity(), getString(R.string.enter_team_name), Toast.LENGTH_SHORT).show();
                }}else{
                    Toast.makeText(getActivity(), getString(R.string.enter_team_nick_name), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;

    }
}
