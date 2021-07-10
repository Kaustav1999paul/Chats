package com.example.chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chats.Adapter.ContactsAdapter;
import com.example.chats.Model.Contacts;

import java.util.ArrayList;

import id.voela.actrans.AcTrans;

public class ContactListActivity extends AppCompatActivity {

    ImageView back;
    RecyclerView contactList;
    ArrayList<Contacts> arrayList = new ArrayList<Contacts>();
    ContactsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        back = findViewById(R.id.back);
        contactList = findViewById(R.id.contactList);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                new AcTrans.Builder(ContactListActivity.this).performSlideToTop();
            }
        });

        checkContactsPermission();

    }

    private void checkContactsPermission() {
        if (ContextCompat.checkSelfPermission(ContactListActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
//            If permission not granted, request for it
            ActivityCompat.requestPermissions(ContactListActivity.this, new String[]{
                    Manifest.permission.READ_CONTACTS}, 100);
        }else {
//            If permission is granted, get the contact List
            getContactList();
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC";
        Cursor cursor = getContentResolver().query(
                uri,null,null,null,sort
        );
        if (cursor.getCount()>0){
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";

                Cursor  phoneCursor = getContentResolver().query(
                        uriPhone,null,selection,new String[]{id}, null
                );

                if (phoneCursor.moveToNext()){
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));

                    Contacts model = new Contacts();
                    model.setName(name);
                    model.setNumber(number);
                    arrayList.add(model);
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
        contactList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactsAdapter(this, arrayList);
        contactList.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            Permission Granted
            getContactList();
        }else {
            Toast.makeText(this, "Contact Permission needed", Toast.LENGTH_SHORT).show();
            checkContactsPermission();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        new AcTrans.Builder(ContactListActivity.this).performSlideToTop();
    }
}