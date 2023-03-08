package com.example.imagesfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String[] items={"PALM DESIGN","BACKSIDE DESIGN","BRIDAL DESIGN","FOOT DESIGN","KIDS DESIGN","LATEST DESIGN"};
    AutoCompleteTextView autoCompleteTextView;
    private RecyclerView rvUnipic;
    private Intent imagesData;

    CustomAdapter customAdapter;
    private FirebaseStorage storage;

    DatabaseReference uniRef,status;
    private FirebaseAuth auth;
    String userId;


    List<String> imageUni1 = new ArrayList<>();

    ArrayAdapter<String> arrayAdapter;
    private static final int PICK_IMAGE_REQUEST = 4843;

    private ImageView upLoadAllImages;
    private StorageReference storageReference;
    List<ImageModel> imageModelList;
    private String saveCurrentDate, saveCurrentTime, postRandomName;
    String downlooadPicUrl;

    Button saveDataButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoCompleteTextView=findViewById(R.id.autoCompleteListener);
        upLoadAllImages=findViewById(R.id.uploadImages);
        arrayAdapter=new ArrayAdapter<>(this,R.layout.list_item,items);

        imageModelList=new ArrayList<>();

        saveDataButton = findViewById(R.id.saveData);
        rvUnipic=findViewById(R.id.showAllImages);
        rvUnipic.setHasFixedSize(true);
        rvUnipic.setLayoutManager(new GridLayoutManager(this, 2));


        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item=adapterView.getItemAtPosition(i).toString();
                Toast.makeText(MainActivity.this, item, Toast.LENGTH_SHORT).show();
            }
        });
        uniRef = FirebaseDatabase.getInstance().getReference().child("All Universities");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        upLoadAllImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //AddImages
                for (int i = 0; i < imageModelList.size(); i++) {
                    ImageModel imageModel = imageModelList.get(i);
                    Calendar calFordate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    saveCurrentDate = currentDate.format(calFordate.getTime());

                    Calendar calForTime = Calendar.getInstance();
                    SimpleDateFormat currenTime = new SimpleDateFormat("HH:mm:ss");
                    saveCurrentTime = currenTime.format(calForTime.getTime());

                    postRandomName = saveCurrentDate + saveCurrentTime;
                    StorageReference imagestorageReference = FirebaseStorage.getInstance().getReference().child("uniImages");

                    StorageReference filePath = imagestorageReference.child(imageModel.getImage().getLastPathSegment() + postRandomName + ".jpg");


                    int finalI = i;
                    StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = filePath.putFile(imageModel.getImage()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downlooadPicUrl = uri.toString();
                                        imageUni1.add(downlooadPicUrl);


                                    }


                                });

                            }

                            AllModels allModels=new AllModels(imageUni1);
                            uniRef.child("All Mehndi Design").setValue(allModels).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(MainActivity.this, "dfghsfkg", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();


                        }
                    });

                }


            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            imagesData = data;

            if (data.getClipData() != null) {

                int totalitem = data.getClipData().getItemCount();


                for (int i = 0; i < totalitem; i++) {

                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    String imagename = getFileName(imageUri);
                    Bitmap bitmap = uriToBitmap(imageUri);
                    if (bitmap != null) {
                        ImageModel imageModel = new ImageModel(imagename, imageUri, bitmap);
                        imageModelList.add(imageModel);
                        Log.d("YOMO", "onActivityResult: " + bitmap);
                    }
                }
                customAdapter = new CustomAdapter(MainActivity.this, imageModelList);
                rvUnipic.setAdapter(customAdapter);

            } else if (data.getData() != null) {
                upLoadAllImages.setImageURI(data.getData());
            }

        } else if (requestCode == 100 && resultCode == RESULT_OK) {


        }
    }


    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);

            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
