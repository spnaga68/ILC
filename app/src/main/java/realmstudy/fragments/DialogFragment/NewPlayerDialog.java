package realmstudy.fragments.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import realmstudy.MainFragmentActivity;
import realmstudy.R;
import realmstudy.data.RealmObjectData.Player;
import realmstudy.interfaces.DialogInterface;
import realmstudy.databaseFunctions.RealmDB;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by developer on 15/2/17.
 */
public class NewPlayerDialog extends DialogFragment {
    int mNum;
    private static final int PICK_CONTACT = 420;
    private EditText name, ph_no;
    DialogInterface dialogInterface;

    NewPlayerDialog setDialogInterface(DialogInterface dialogInterface) {
        this.dialogInterface = dialogInterface;
        return NewPlayerDialog.newInstance();
    }


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static NewPlayerDialog newInstance() {
        NewPlayerDialog f = new NewPlayerDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", 1);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_new_player, container, false);
        name = (EditText) v.findViewById(R.id.name);
        ph_no = (EditText) v.findViewById(R.id.time);
        ph_no.setHint(R.string.ph_no);

        v.findViewById(R.id.from_contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                        ) {

                    ((MainFragmentActivity) getActivity()).startInstalledAppDetailsActivity(getActivity());
                } else {
                  //  dismiss();
                    pickFromContacts();
                }
            }
        });
        v.findViewById(R.id.submit_new_player).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameS = name.getText().toString();
                final String phno = ph_no.getText().toString();
                if (!nameS.trim().isEmpty()) {
                    Realm.init(getActivity());
                    RealmConfiguration config = new RealmConfiguration.Builder()
                            .build();
                    Realm realm = Realm.getInstance(config);

//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
                    RealmDB.AddPlayer(getActivity(), realm, nameS, phno);
                    if (dialogInterface != null)
                        dialogInterface.onSuccess(name + "____" + phno, true);

//                        }
//                    });
                    ph_no.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    dismiss();
                }
            }
        });

        return v;

    }

    private void pickFromContacts() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        String cNumber = "";
        String name = "";
        System.out.println("_____PP"+3);
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getActivity().getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                            System.out.println("number is:" + cNumber);
                        }
                        name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


                    }

                    if (!name.trim().isEmpty()) {
//                        realm.beginTransaction();
//                        Player playerObj = realm.createObject(Player.class);
//                        playerObj.setpID(realm.where(Player.class).findAll().size());
//                        playerObj.setName(name);
//                        playerObj.setPh_no(cNumber);
//                        realm.commitTransaction();
                        // RealmDB.AddPlayer(getActivity(), realm, name, cNumber);

                        if (!name.isEmpty()) {
                            Realm.init(getActivity());
                            RealmConfiguration config = new RealmConfiguration.Builder()
                                    .build();

                            Realm.getInstance(config);
                            Player id = RealmDB.AddPlayer(getActivity(), Realm.getInstance(config), name, cNumber);


                        }
                        break;
                    }
                }
        }
        dismiss();
    }
}