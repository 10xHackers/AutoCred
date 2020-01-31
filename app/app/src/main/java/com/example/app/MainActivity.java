package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

     ImageButton mSelectimage;
     Button msubmitButton;

     EditText mPostTitle;
     EditText mPostDesc;

     ProgressDialog mprogress;

     DatabaseReference mDatabase;

     Uri mImageUri =null;

     private static final int GALLERY_REQUEST=1;
     private static final int CAMERA_REQUEST_CODE=1;

     StorageReference mStorage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");

        mSelectimage=findViewById(R.id.imageButton);
        msubmitButton=findViewById(R.id.mSubmitbtn);
        mPostTitle=findViewById(R.id.titlefield);
        mPostDesc=findViewById(R.id.descField);

        mprogress=new ProgressDialog(this);

        mSelectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager())!=null)
                {
                    startActivityForResult(intent,CAMERA_REQUEST_CODE);
                }
            }
        });
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

        mImageUri = data.getData();
            mSelectimage.setImageURI(mImageUri);

            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)

                    .start(this);



            Bitmap mImageUri = (Bitmap) data.getExtras().get("data");
            mSelectImage.setImageBitmap(mImageUri);
        }


        msubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });


    }

    private void startPosting()
    {
        mprogress.setMessage("Posting to Blog...");

        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();


        if(!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val) && mImageUri != null){

            mprogress.show();
            StorageReference filepath = mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri downloadUrl =taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = mDatabase.push();
                    newPost.child("title").setValue(title_val);
                    newPost.child("desc").setValue(desc_val);
                   // newPost.child("image").setValue(downloadUrl.toString());


                    mprogress.dismiss();
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                }
            });
        }


    }
}
