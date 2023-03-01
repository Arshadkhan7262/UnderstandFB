package com.example.understandfb;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    AppCompatButton btn1,btn2,btn3;
    Uri imageuri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    DatabaseReference reference;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imageView);
        btn1=findViewById(R.id.btn1);
        btn2=findViewById(R.id.btn2);
        btn3=findViewById(R.id.btn3);
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        reference=FirebaseDatabase.getInstance().getReference().child("imagep");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Galleryintent=new Intent();
                Galleryintent.setType("image/*");
                Galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Galleryintent,101);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MainActivity2.class));
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageuri!=null)
                {

                String key=reference.push().getKey();
            StorageReference storageReference=storage.getReference().child("imagep").child(key);

            storageReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Model model = new Model(uri.toString());
                            database.getReference().child("imagep").child(key).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "Pic Uploaded to Firebase Successfully", Toast.LENGTH_SHORT).show();
                                    imageView.setImageURI(null);
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    Toast.makeText(MainActivity.this, "Pic is uploading now ", Toast.LENGTH_SHORT).show();

                }

            });

            }

        }});



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(imageView==null)
        {
            Toast.makeText(this, "Please Select Image From Gallery", Toast.LENGTH_SHORT).show();
        }
        else if(imageView!=null)
        {

            imageView.setImageURI(data.getData());
            imageuri=data.getData();
            Toast.makeText(this, "pic has been set on imageView", Toast.LENGTH_SHORT).show();

        }
    }
}