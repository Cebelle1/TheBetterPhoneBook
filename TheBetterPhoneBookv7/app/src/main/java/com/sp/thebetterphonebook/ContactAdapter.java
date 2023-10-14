package com.sp.thebetterphonebook;

import androidx.cursoradapter.widget.CursorAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private Context mContext;
    private Cursor model;
    private Cursor mCursortest;
    private View.OnClickListener mListener;
    private RecyclerViewClickListener listener;
    private SQLiteHelper helper = null;
    private String sentText = "";
    private boolean mVariableTest=false;
    ArrayList<String> contactArray = new ArrayList<>();

    //private List<ContactModel> contactModelList;

    public ContactAdapter(Context mContext, Cursor model) {
        this.mContext = mContext;
        this.model = model;
        this.listener = listener;
        helper = new SQLiteHelper((mContext));
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
            if (model.moveToNext()) {
                System.out.print(helper.getContactName(model) + "MODELNAMELAFUCK");
                if (helper.getContactName(model) != null && !helper.getContactName(model).equals("")) {
                    System.out.println(helper.getContactName(model) + "your nenek la");
                    contactArray.add(helper.getContactName(model));
                    holder.name_contact.setText(helper.getContactName(model));
                    holder.phone_contact.setText(helper.getContactNo(model));

                    if (helper.getContactPhoto(model) != null) {
                        byte[] contPhoto = helper.getContactPhoto(model);
                        System.out.print(contPhoto + " CONTACTPHOTOBYTEARRAY");
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(contPhoto);

                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        System.out.print(bitmap + "BITMAPLO");
                        //Bitmap bitmap = BitmapFactory.decodeByteArray(contPhoto, 0, contPhoto.length);
                        if (bitmap == null) {
                        /*Bitmap bitmapPhoto = BitmapFactory.decodeFile(helper.getContactPhoto(model).toString());
                        System.out.print(bitmapPhoto+ "BITMAPPHOTO");
                        holder.img_contact.setImageBitmap(bitmapPhoto);*/
                            holder.img_contact.setImageResource(R.drawable.nyankosenseri);
                        } else {
                            holder.img_contact.setImageBitmap(bitmap);
                        }
                    }
                }
            }

        if(mVariableTest==true){
            mCursortest.moveToFirst();
            holder.name_contact.setText(helper.getContactName(mCursortest));
            holder.phone_contact.setText(helper.getContactNo(mCursortest));
            if (helper.getContactPhoto(mCursortest) != null) {
                byte[] contPhoto = helper.getContactPhoto(mCursortest);
                System.out.print(contPhoto + " CONTACTPHOTOBYTEARRAY");
                ByteArrayInputStream inputStream = new ByteArrayInputStream(contPhoto);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                System.out.print(bitmap + "BITMAPLO");
                if (bitmap == null) {
                    holder.img_contact.setImageResource(R.drawable.nyankosenseri);
                } else {
                    holder.img_contact.setImageBitmap(bitmap);
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        int count = helper.countDB() - 1;
        System.out.println(count + "diu lei");
        if(mVariableTest==true){
            count=1;
        }
        return count;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onListClick) {
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }


    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name_contact, phone_contact;
        CircleImageView img_contact;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            System.out.println("hello world");
            name_contact = itemView.findViewById(R.id.name_contact);
            phone_contact = itemView.findViewById(R.id.phone_contact);
            img_contact = itemView.findViewById(R.id.img_contact);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mVariableTest==false) {
                Intent intent = new Intent(mContext, DetailContact.class);
                model.moveToPosition(getAdapterPosition() + 1);
                System.out.println("Position =" + getAdapterPosition());
                String contactID = helper.getID(model);
                System.out.println("Contact ID =" + contactID);
                intent.putExtra("ID", contactID);
                mContext.startActivity(intent);
            }else{
                Intent intent = new Intent(mContext, DetailContact.class);
                mCursortest.moveToPosition(getAdapterPosition());
                System.out.println("Position =" + getAdapterPosition());
                String contactID = helper.getID(mCursortest);
                System.out.println("Contact ID =" + contactID);
                intent.putExtra("ID", contactID);
                mContext.startActivity(intent);

            }
        }
    }

    public void filter(String text) {
        sentText = text.trim();
        for (String contact : contactArray) {
            System.out.println(contact + "CONTACTARRAYLAH");
            if (contact.toLowerCase().contains(sentText.toLowerCase())) {
                if(sentText!=null && !sentText.equals("")) {
                    System.out.println(contact.toLowerCase() + " CONTACT LOWER CASE" + sentText.toLowerCase() + "TEXT LOWER CASE");
                    mCursortest = helper.getContByName(contact);
                    mVariableTest = true;
                }else{
                    mVariableTest=false;
                    helper = new SQLiteHelper(mContext);
                    model = helper.getAllContact();

                }
                notifyDataSetChanged();
            }
        }
    }
}
