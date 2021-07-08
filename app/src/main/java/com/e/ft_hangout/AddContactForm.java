package com.e.ft_hangout;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.e.ft_hangout.models.Contact;
import com.e.ft_hangout.models.DBLocalAccess;
import com.e.ft_hangout.tools.Useful;


public class AddContactForm extends AppCompatActivity {

    final private static int PICS_RESULT = 2;
    final private static int MODIFY_CANCEL = 4;
    private static String url = null;
    private static int urlInt = 0;
    private static String id;
    private static String name;
    private static String firstname;
    private static String email;
    private static String phone;
    private static String pics;
    private static String identity;
    private static Boolean fromModify;
    private static String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_form);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        if (getIntent().hasExtra("ID"))
            fromModify = true;
        else
            fromModify = false;
        if (fromModify){
            MainActivity.setPref(getResources().getString(R.string.modify), this, actionBar);

            setModifyContact();

            TextView modifyTitle = findViewById(R.id.new_contact);
            modifyTitle.setText(identity);

            ImageView image = findViewById(R.id.profil_pics);
            Useful.fillImageView(pics, image);
        } else {
            MainActivity.setPref(getString(R.string.title_add_contact), this, actionBar);
        }
        TextView change_picture = findViewById(R.id.add_picture);
        change_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent choosePicsActivity = new Intent(getApplicationContext(), ChoosePicsActivity.class);
                if (fromModify) {
                    choosePicsActivity.putExtra("PICS", pics);
                    choosePicsActivity.putExtra("FROM_MODIFY", fromModify);
                    startActivityForResult(choosePicsActivity, MODIFY_CANCEL);
                }else
                    startActivityForResult(choosePicsActivity, PICS_RESULT);
            }
        });

        if (ContactFileActivity.focus == ContactFileActivity.Focus.ADDRESS_FOCUS){
            EditText addr = findViewById(R.id.address);
            addr.setFocusedByDefault(true);
            addr.requestFocus();
        }

        if (ContactFileActivity.focus == ContactFileActivity.Focus.EMAIL_FOCUS){
            EditText mai = findViewById(R.id.email);
            mai.setFocusedByDefault(true);
            mai.requestFocus();
        }

        if (ContactFileActivity.focus == ContactFileActivity.Focus.PHONE_FOCUS){
            EditText pn = findViewById(R.id.phoneNumber);
            pn.setFocusedByDefault(true);
            pn.requestFocus();
        }

        if (ContactFileActivity.focus == ContactFileActivity.Focus.NONE){
            EditText fn = findViewById(R.id.firstname);
            fn.setFocusedByDefault(true);
            fn.requestFocus();
        }
    }

    public void setModifyContact(){
        name = getIntent().getStringExtra("NAME");
        id = getIntent().getStringExtra("ID");
        firstname = getIntent().getStringExtra("FIRSTNAME");
        identity = firstname + " " + name;
        phone = getIntent().getStringExtra("PHONE");
        email = getIntent().getStringExtra("EMAIL");
        pics = getIntent().getStringExtra("PICS");
        address = getIntent().getStringExtra("ADDRESS");

        EditText firstna = findViewById(R.id.firstname);
        EditText nam = findViewById(R.id.name);
        EditText mai = findViewById(R.id.email);
        EditText phoneNumber = findViewById(R.id.phoneNumber);
        EditText addr = findViewById(R.id.address);
        if (!firstname.isEmpty() )
            firstna.setText(firstname);
        if (!name.isEmpty() )
            nam.setText(name);
        if (!email.isEmpty() )
            mai.setText(email);
        if (!phone.isEmpty() )
            phoneNumber.setText(phone);
        if (address != null && !address.isEmpty())
            addr.setText(address);
    }
    public void addContact(View v){
            EditText firstna = findViewById(R.id.firstname);
            String fn = firstna.getText().toString();
            fn = fn.trim();
            EditText nam = findViewById(R.id.name);
            String n = nam.getText().toString();
            n = n.trim();
            EditText phoneNumber = findViewById(R.id.phoneNumber);
            String pn = phoneNumber.getText().toString();
            EditText emai = findViewById(R.id.email);
            String mail = emai.getText().toString();
            String pic = getPic(url, urlInt);
            EditText address = findViewById(R.id.address);
            String addr = address.getText().toString();
            if (
                    checkIfPhoneOrName(pn, n, fn, nam, firstna)
                    && isEmail(mail, emai)
                    && isPhone(pn, phoneNumber)
                    && isaddress(addr, address)) {

                String[] nameTab = {n, fn};
                DBLocalAccess db = new DBLocalAccess(this);

                if (fromModify) {
                    if (n.compareTo(name) != 0 || fn.compareTo(firstname) != 0) {
                        nameTab = checkDoubleFirstname(nameTab, this);
                    }
                    Contact contact = new Contact(id, nameTab[0], nameTab[1], pn, mail, pic, addr);
                    db.updateContact(contact);
                }
                else {
                    nameTab = checkDoubleFirstname(nameTab, this);
                    Contact contact = new Contact("", nameTab[0], nameTab[1], pn, mail, pic, addr);
                    db.AddContact(contact);
                }
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
    }

    public Boolean checkIfPhoneOrName(String tel, String name, String firstname, EditText n, EditText fn){
        if (tel.isEmpty() && name.isEmpty() && firstname.isEmpty()){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty_contact_field), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!name.matches("^[A-Za-z- ]+$") && !name.isEmpty()) {
            n.setError(getResources().getString(R.string.name_field_validation));
            return false;
        }
        if (name.length() > 30){
            n.setError(getResources().getString(R.string.error_lenght_name));
            return false;
        }
        if (!firstname.matches("^[A-Za-z- ]+$") && !firstname.isEmpty()){
            fn.setError(getResources().getString(R.string.firstname_field_validation));
            return false;
        }
        if (firstname.length() > 20){
            fn.setError(getResources().getString(R.string.firstanme_length_validation));
            return false;
        }
        return true;
    }

    public String[] checkDoubleFirstname(String[] name, Context context){
        DBLocalAccess db = new DBLocalAccess(context);
        int res = db.getContact2("name", "firstname", name[0], name[1]);
        if (res > 0){
            if (name[0].isEmpty())
                name[1] += " (" + res + ")";
            else if (name[1].isEmpty() || (!name[0].isEmpty() && !name[1].isEmpty()))
                name[0] += " (" + res + ")";
        }
        return name;
    }

    public Boolean isEmail(String email, EditText mail){
        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return true;
        if (email.isEmpty())
            return true;
        mail.setError(getResources().getString(R.string.error_email_validation));
        return false;
    }

    public Boolean isaddress(String addr, EditText address){
        if (!addr.matches("^[A-Za-z0-9, ]+$") && !addr.isEmpty()) {
            address.setError(getResources().getString(R.string.address_field_validation));
            return false;
        }
        return true;
    }
    public Boolean isPhone(String phoneNum, EditText phone){
        if (!phoneNum.isEmpty() && Patterns.PHONE.matcher(phoneNum).matches())
            return true;
        if (phoneNum.isEmpty())
            return true;
        phone.setError(getResources().getString(R.string.phone_error_validation));
        return false;
    }

    public String getPic(String url, int urlInt){
        String pic;
        pic = "pics";
        if (url != null)
            pic = url;
        else if (urlInt != 0){
            pic = String.valueOf(urlInt);
        }
        return pic;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICS_RESULT || requestCode == MODIFY_CANCEL) && resultCode == RESULT_OK && null != data) {
            ImageView current = findViewById(R.id.profil_pics);
            url = data.getStringExtra("imageUriString");
            urlInt = data.getIntExtra("imageUriInt", 0);
            if (url != null) {
                urlInt = 0;
                current.setImageURI(Uri.parse(url));
            }
            else if (urlInt != 0) {
                url = null;
                current.setImageResource(urlInt);
            }
        }
        else if (requestCode == MODIFY_CANCEL && resultCode == RESULT_CANCELED){}
    }
}