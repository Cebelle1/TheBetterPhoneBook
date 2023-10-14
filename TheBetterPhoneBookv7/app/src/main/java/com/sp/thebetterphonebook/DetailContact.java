package com.sp.thebetterphonebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DetailContact extends AppCompatActivity implements ImageCaptureFragment.SingleChoiceListener {
    private String contactName = "";
    private String contactID = "";
    private int contID;
    private SQLiteHelper helper = null;
    private EditText detailContName;
    private EditText detailContNo;
    private EditText detailContAddr;
    private TextView detailContNameTop;
    private ImageView detailContPhoto;
    private ImageButton detailTakePhoto;
    private ImageButton showInMap;
    private ImageButton btnDetailCancel;
    private ImageButton btnContactUpdate;
    private ImageButton editContact;
    private ImageButton btnCall;
    private GPSTracker gpsTracker;
    private ContactAdapter adapter;
    private Cursor model = null;

    private final static int REQUEST_CODE_GALLERY = 999;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_CALL =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_contact);
        detailContName = findViewById(R.id.detailContName);
        detailContName.setEnabled(false);
        detailContNo = findViewById(R.id.detailContNo);
        detailContNo.setEnabled(false);
        detailContAddr = findViewById(R.id.detailContAddr);
        detailContAddr.setEnabled(false);
        detailContPhoto = findViewById(R.id.detailContactPhoto);
        detailContNameTop = findViewById(R.id.detailContNameTop);
        editContact = findViewById(R.id.editContact);
        editContact.setOnClickListener(onEditContact);

        detailTakePhoto = findViewById(R.id.detailBtnTakePhoto);
        detailTakePhoto.setOnClickListener(onTakePhoto);

        showInMap = findViewById(R.id.showinMap);
        showInMap.setOnClickListener(onOpenMap);
        btnDetailCancel = findViewById(R.id.btnDetailCancel);
        btnDetailCancel.setOnClickListener(onDetailCancel);
        btnContactUpdate = findViewById(R.id.btnContactUpdate);
        btnContactUpdate.setOnClickListener(onContactUpdate);
        btnCall = findViewById(R.id.btnCall);
        btnCall.setOnClickListener(onCallContact);

        helper = new SQLiteHelper(this);
        gpsTracker = new GPSTracker(DetailContact.this);

        helper = new SQLiteHelper(this);
        model = helper.getAllContact();
        adapter = new ContactAdapter(this, model);

        //contactName= getIntent().getStringExtra("contactName");
        // System.out.println("CONTACTNAMELA"+contactName);
        contactID = getIntent().getStringExtra("ID");
        load();
    }

    private void load() {
        Cursor c = helper.getContById(contactID);
        c.moveToFirst();
        detailContNameTop.setText(helper.getContactName(c));
        detailContName.setText(helper.getContactName(c));
        detailContNo.setText(helper.getContactNo(c));
        detailContAddr.setText(helper.getContactAddr(c));
        Bitmap bitmap = BitmapFactory.decodeByteArray(helper.getContactPhoto(c), 0, helper.getContactPhoto(c).length);
        if (bitmap == null) {
            detailContPhoto.setImageResource(R.drawable.nyankosenseri);
        } else {
            detailContPhoto.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
        gpsTracker.stopUsingGPS();
    }

    View.OnClickListener onTakePhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogFragment imageOptionFragment = new ImageCaptureFragment();
            imageOptionFragment.setCancelable(false);
            imageOptionFragment.show(getSupportFragmentManager(), "Image Option Dialog");
        }
    };

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        if (position == 0) {
            ActivityCompat.requestPermissions(DetailContact.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
        } else if (position == 1) {
            Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (imageTakeIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onNegativeButtonClicked() {
    }

    //Permission to access gallery
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "You do not have permissions to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (requestCode== REQUEST_CALL){
            if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
            } else {
                Toast.makeText(this,"Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Setting Image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                detailContPhoto.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            detailContPhoto.setImageBitmap(image);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    View.OnClickListener onOpenMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = detailContName.getText().toString().trim();
            String addr = detailContAddr.getText().toString().trim();
            System.out.println(addr + "CHECKADDRESS");
            if (addr != null && !addr.equals("")) {
                Intent intent = new Intent(DetailContact.this, Map.class);
                intent.putExtra("addr", addr);
                intent.putExtra("name", name);
                startActivity(intent);
            } else {
                Toast.makeText(getApplication(), "Address is empty!", Toast.LENGTH_LONG).show();
            }

        }
    };

    View.OnClickListener onEditContact = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            detailContName.setEnabled(true);
            detailContAddr.setEnabled(true);
            detailContNo.setEnabled(true);
        }
    };

    View.OnClickListener onDetailCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DetailContact.this, MainContactList.class);
            startActivity(intent);
            finish();
        }
    };

    View.OnClickListener onContactUpdate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String nameStr = detailContName.getText().toString();
            String numStr = detailContNo.getText().toString();
            String addrStr = detailContAddr.getText().toString();
            byte[] photo = imageViewToByte(detailContPhoto);
            helper.updateContact(contactID, nameStr, numStr, addrStr, photo);
            Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            Intent intent = new Intent(DetailContact.this, MainContactList.class);
            startActivity(intent);
            finish();
        }
    };

    View.OnClickListener onCallContact = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Toast.makeText(DetailContact.this,"PRESSED",Toast.LENGTH_SHORT).show();
            makePhoneCall();

        }
    };


    public void makePhoneCall(){
        System.out.print("MAKINGPHONECALLIAO");
        String contactCallNo = detailContNo.getText().toString();
        System.out.println(contactCallNo+ "WORK LA FUCK CONTACT NO");
        if (contactCallNo.trim().length()>0) {
            if (ContextCompat.checkSelfPermission(DetailContact.this,
                    Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(DetailContact.this,
                        new String[] {Manifest.permission.CALL_PHONE},REQUEST_CALL);
            } else {
                String dial = "tel:" + contactCallNo;
                startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse(dial)));
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Enter Number!", Toast.LENGTH_SHORT).show();
        }
    }
    //Converting image to byte
    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;

    }
}
