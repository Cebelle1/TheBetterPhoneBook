package com.sp.thebetterphonebook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AddContact extends AppCompatActivity implements ImageCaptureFragment.SingleChoiceListener {
    private ImageView eContPhoto;
    private EditText eContName;
    private EditText eContNo;
    private EditText eContAddr;
    private ImageButton btnTakePhoto;
    private ImageButton btnSave;
    private ImageButton btnCancel;
    private SQLiteHelper helper;
    private ContactAdapter adapter;
    private Cursor model =null;


    private String contactID = "";
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_CODE_GALLERY = 999;
    private AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        eContName = findViewById(R.id.detailContName);
        eContNo = findViewById(R.id.detailContNo);
        eContAddr = findViewById(R.id.detailContAddr);
        eContPhoto = findViewById(R.id.detailContactPhoto);

        btnTakePhoto = findViewById(R.id.detailBtnTakePhoto);
        btnTakePhoto.setOnClickListener(onTakePhoto);

        btnSave = findViewById(R.id.btnContactUpdate);
        btnSave.setOnClickListener(onSaveNewContact);

        btnCancel = findViewById(R.id.btnDetailCancel);
        btnCancel.setOnClickListener(onCancel);

        helper = new SQLiteHelper(getApplicationContext());
        model = helper.getAllContact();
        adapter = new ContactAdapter(this,model);
    }

    View.OnClickListener onSaveNewContact = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = eContName.getText().toString().trim();
            String phone = eContNo.getText().toString().trim();
            String addr = eContAddr.getText().toString().trim();
            byte[] photo = imageViewToByte(eContPhoto);
            helper.insertContact(name, phone, addr, photo);
            Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            finish();
        }
    };

    //Converting image to byte
    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
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
            ActivityCompat.requestPermissions(AddContact.this,
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //Take image from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                eContPhoto.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap image = (Bitmap) extras.get("data");
            eContPhoto.setImageBitmap(image);


                Uri uri = data.getData();
                System.out.println(uri);
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                eContPhoto.setImageBitmap(imageBitmap);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    View.OnClickListener onCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            intent = new Intent(AddContact.this, MainContactList.class);
            startActivity(intent);
            finish();
        }
    };

}
