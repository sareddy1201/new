package perfectride;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import perfectride.Model.User;
import perfectride.database.DatabaseHandler;

public class PaymentFragment extends Fragment {
    private DatabaseHandler db;
    private View rootView;
    private EditText card_numberEt;
    private EditText expiryEt;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_payment, container, false);

        card_numberEt = rootView.findViewById(R.id.card_number_et);
        expiryEt = rootView.findViewById(R.id.expiry_et);
        Button save_cardButton = rootView.findViewById(R.id.save_card_button);

        db = new DatabaseHandler(getActivity());
        user = db.getUser(DatabaseHandler.SINGLE_USER_ID);
        card_numberEt.setText(user.get_cardNum());
        expiryEt.setText(user.get_expiry());
        save_cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNumber = card_numberEt.getText().toString();
                String expiry = expiryEt.getText().toString();
                User user1 = new User(DatabaseHandler.SINGLE_USER_ID, user.get_fname(), user.get_lname(), user.get_photo(), cardNumber, expiry);
                db.updateContact(user1);
                Toast.makeText(getActivity(), "Card details saved successfully", Toast.LENGTH_SHORT).show();
            }
        });

        expiryEt.addTextChangedListener(new TextWatcher() {
            public boolean flag;
            public String a;
            public int keyDel;
            public int mAfter;
            public boolean mFormatting;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mAfter = i2;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {

                if (flag) {
                    expiryEt.setOnKeyListener(new View.OnKeyListener() {

                        public boolean onKey(View v, int keyCode, KeyEvent event) {

                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                keyDel = 1;
                            return false;
                        }
                    });

                    flag = false;

                    if (keyDel == 1) {
                        keyDel = 0;
                        expiryEt.setText(charSequence);
                        expiryEt.setSelection(charSequence.length());
                    } else {

                        String s = expiryEt.getText().toString();
                        String str = s.replaceAll("\\D+", "");

                        if (str.length() > 2) {
                            s = str.substring(0, 2) + "/" + str.substring(2, str.length());
                        }
                        expiryEt.setText(s);
                        expiryEt.setSelection(s.length());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                flag = true;
            }
        });

        return rootView;

    }
}
