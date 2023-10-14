package com.sp.thebetterphonebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainContactList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private Cursor model =null;
    private SQLiteHelper helper = null;
    private ImageButton imgBtn;
    private String contactID ="";
    private ImageButton btnHelp;
    private EditText eSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_contact_list);

        recyclerView = findViewById(R.id.contacts_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);

        imgBtn = findViewById(R.id.buttonAddContact);
        imgBtn.setOnClickListener(onAddContact);
        btnHelp=findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(onOpenHelp);

        eSearch = findViewById(R.id.eSearch);
        eSearch.addTextChangedListener(onSearchContact);

        helper = new SQLiteHelper(this);
        model = helper.getAllContact();
        adapter = new ContactAdapter(this,model);
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_CONTACTS).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (response.getPermissionName().equals(Manifest.permission.READ_CONTACTS)){
                    getContactsfromLocalPB();
                }
            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(MainContactList.this,"Permission should be granted",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        recyclerView.setVisibility(View.VISIBLE);

        adapter = new ContactAdapter(this,model);
        adapter.setOnItemClickListener(onListClick);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,outputStream);
        return outputStream.toByteArray();
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper = new SQLiteHelper(this);
        model = helper.getAllContact();
        eSearch.setText("");
        adapter = new ContactAdapter(this,model);
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_CONTACTS).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (response.getPermissionName().equals(Manifest.permission.READ_CONTACTS)){
                    getContactsfromLocalPB();
                }
            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(MainContactList.this,"Permission should be granted",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setVisibility(View.VISIBLE);

        adapter = new ContactAdapter(this,model);
        adapter.setOnItemClickListener(onListClick);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        helper = new SQLiteHelper(this);
        model = helper.getAllContact();
        adapter = new ContactAdapter(this,model);
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_CONTACTS).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (response.getPermissionName().equals(Manifest.permission.READ_CONTACTS)){
                    getContactsfromLocalPB();
                }
            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(MainContactList.this,"Permission should be granted",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        recyclerView.setVisibility(View.VISIBLE);

        adapter = new ContactAdapter(this,model);
        adapter.setOnItemClickListener(onListClick);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getContactsfromLocalPB() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);
        Cursor storedContact = helper.getAllName();
        ArrayList<String> names = new ArrayList<>();
        while(storedContact.moveToNext()){
            System.out.println(storedContact.getString(storedContact.getColumnIndex("contactName"))+"STOREDCONTACT");
            names.add(storedContact.getString(storedContact.getColumnIndex("contactName")));

        }
        while(phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String phoneAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
            byte[] phoneUri = phones.getBlob(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

            System.out.println(name+"NAME CHECK");
            boolean exists = false;
            for (String element :names){
                if(element.equals(name)){
                    exists = true;
                    break;
                    }
                }
            if (exists ==false) {
                if (phoneUri != null) {
                    //byte[] byteArray = phoneUri.getBytes();
                    helper.insertContact(name, phoneNumber, phoneAddr, phoneUri);
                    System.out.print(phoneUri + " CEBELLE'S PHOTO 123 ");
                } else {
                    Drawable image = this.getResources().getDrawable(R.drawable.humaniconcircle);
                    Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    helper.insertContact(name, phoneNumber, phoneAddr, byteArray);
                    System.out.print(phoneUri + " CEBELLE'S PHOTO ");
                }
            }
            adapter.notifyDataSetChanged();
        }
        model = helper.getAllContact();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        model.moveToPosition(position);
        String contactID = helper.getID(model);
        Intent detailIntent;
        detailIntent = new Intent(this,DetailContact.class);
        detailIntent.putExtra("ID",contactID);
        startActivity(detailIntent);
    }

    private View.OnClickListener onAddContact = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainContactList.this,AddContact.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onDestroy(){
        helper.close();
        super.onDestroy();
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainContactList.this,DetailContact.class);
            System.out.println(id +"CHECK THE BLOODY ID");
            model.moveToPosition(position);
            String contactName = helper.getContactName(model);
            String contactID =helper.getID(model);
            intent.putExtra("ID",contactID);
            intent.putExtra("contactName",contactName);
            startActivity(intent);
        }
    };

    View.OnClickListener onOpenHelp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainContactList.this,HelpManualWebView.class);
            startActivity(intent);
        }
    };

    TextWatcher onSearchContact = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            adapter.filter(s.toString());
        }
    };

}
