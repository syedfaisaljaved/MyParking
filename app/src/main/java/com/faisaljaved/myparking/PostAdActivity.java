package com.faisaljaved.myparking;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faisaljaved.myparking.models.Images;
import com.faisaljaved.myparking.models.MyAdData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;
import me.shaohui.advancedluban.OnMultiCompressListener;

import static com.faisaljaved.myparking.utils.Constants.LIST_OF_CITIES_INDIA;

public class PostAdActivity extends BaseActivity {

    private static final String TAG = "PostAdActivity";

    private static final int PICK_IMAGE_REQUEST_CODE = 1;

    private Button mPostAdButton, mImagePicker;
    private TextView mPhotoText;
    private AutoCompleteTextView mLocation;
    private TextInputLayout mTitle, mDescription, mPrice;
    private ImageView mImageView;
    private LinearLayout mImagesLayout;
    private Spinner mSpinner;
    private Toolbar toolbar;
    private String mVehicleType = "Two Wheeler";
    private List<Images> mSelectedImages;
    private List<String> mSelectedImagesURL;
    private AlertDialog.Builder dialogBuilder;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private String userId;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ProgressDialog progressDialog;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);
        mPhotoText = findViewById(R.id.photo_text);
        mImagesLayout = findViewById(R.id.post_ad_images_layout);
        mImagePicker = findViewById(R.id.post_ad_choose_image);
        mImageView = findViewById(R.id.image1);
        mPostAdButton = findViewById(R.id.post_ad_button);
        mTitle = (TextInputLayout) findViewById(R.id.post_ad_title);
        mDescription = (TextInputLayout) findViewById(R.id.post_ad_description);
        mPrice = (TextInputLayout) findViewById(R.id.post_ad_price);
        mSpinner = (Spinner) findViewById(R.id.post_ad_spinner);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mLocation = findViewById(R.id.choose_location);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, LIST_OF_CITIES_INDIA);
        mLocation.setAdapter(adapter);

        //progressDialog
        progressDialog = new ProgressDialog(this);

        //list initialize
        mSelectedImages = new ArrayList<>();
        mSelectedImagesURL = new ArrayList<>();

        //firebase
        initFirebase();

        //Alert Dialog
        initAlertDialogBox();

        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.create().show();
            }
        });

        //image selection array
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,R.array.vehicle_type, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String vehicle = adapterView.getItemAtPosition(i).toString();
                setmVehicleType(vehicle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mPostAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmInputs();
                uploadImage();
            }
        });

        mImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
    }


    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    private void initAlertDialogBox() {
        dialogBuilder = new AlertDialog.Builder(PostAdActivity.this)
                .setTitle("Are you sure?")
                .setMessage("All changes will be lost")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent,PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data !=null) {
            ClipData clipData = data.getClipData();
            mSelectedImages.clear();
            if (clipData != null){
                List<File> fileList = new ArrayList<>();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    fileList.add(new File(uri.getPath()));
                }
                compressMultipleImage(fileList);

            }else {
                Uri uri = data.getData();
                compressImage(uri);
            }
        }

        emptyViews();
        loadImage();
    }

    private void compressMultipleImage(List<File> fileList) {
        Luban.compress(this, fileList)
                .setMaxSize(200)
                .putGear(Luban.CUSTOM_GEAR)
                .launch(new OnMultiCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(List<File> fileList) {
                        for (int i = 0; i < fileList.size(); i++) {
                            Uri uri = Uri.fromFile(fileList.get(i));
                            mSelectedImages.add(new Images(getFileNameFromImageUri(uri),uri));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void compressImage(Uri resultUri) {
        File file = new File(resultUri.getPath());
        Luban.compress(file, getFilesDir())
                .setMaxSize(200)
                .putGear(Luban.CUSTOM_GEAR)
                .launch(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        Uri uri = Uri.fromFile(file);
                        mSelectedImages.add(new Images(getFileNameFromImageUri(uri),uri));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    public String getFileNameFromImageUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
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

    public void emptyViews() {
        for (int i = 0; i < 5; i++) {
            View view = mImagesLayout.getChildAt(i+1);
            if (view instanceof ImageView) {
                mImageView.setImageDrawable(null);
                view.setVisibility(View.GONE);
            }
        }
    }

    private void loadImage() {
        if (!mSelectedImages.isEmpty()) {
            for (int i = 0; i < mSelectedImages.size(); i++) {
                View view = mImagesLayout.getChildAt(i+1);
                if (view instanceof ImageView) {
                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.ic_launcher_background);

                    Glide.with(this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(mSelectedImages.get(i).getImageUri())
                            .into((ImageView) view);
                    view.setVisibility(View.VISIBLE);
                }
            }

            String text = getString(R.string.photo_added, mSelectedImages.size());
            mPhotoText.setText(text);

        } else {
            mPhotoText.setText(R.string.photo);
        }
    }

    private void uploadImage() {
        progressDialog.setMessage("Uploading");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setProgress(0);

        mSelectedImagesURL.clear();
        if (mSelectedImages.size() != 0) {
            counter = 0;
            final StorageReference storageReference = firebaseStorage.getReference().child("userData");
            for (int i = 0; i < mSelectedImages.size(); i++) {
                final int finalI = i;
                final StorageReference imageTOBeUploaded = storageReference.child(mSelectedImages.get(i).getImageName());

                imageTOBeUploaded.putFile(mSelectedImages.get(i).getImageUri())
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            imageTOBeUploaded.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        counter++;
                                        mSelectedImagesURL.add(task.getResult().toString());
                                        Log.d(TAG, "onComplete: image received" +mSelectedImagesURL);

                                        if (counter == mSelectedImages.size()){
                                            saveLinkToFirebase();
                                        }
                                    } else {
                                        storageReference.child(mSelectedImages.get(finalI).getImageName()).delete();
                                    }
                                }
                            });
                        } else {
                            counter++;
                            Toast.makeText(PostAdActivity.this, "Couldn't upload " + mSelectedImages.get(finalI).getImageName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.show();
                    }
                });
            }
        }
    }

    private void saveLinkToFirebase(){
        progressDialog.show();
        final String uniqueId = reference.push().getKey();
        String title = mTitle.getEditText().getText().toString().trim();
        String description = mDescription.getEditText().getText().toString().trim();
        String price = mPrice.getEditText().getText().toString().trim();
        String vehicleType = getmVehicleType();
        String location = mLocation.getEditableText().toString();
        Long timestamp = System.currentTimeMillis();
        String uid = userId;

        final MyAdData myAdData = new MyAdData(uniqueId,uid,title,description,price,vehicleType,location,timestamp,mSelectedImagesURL);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child("data").child(userId).child("MyAds").child(uniqueId).setValue(myAdData);
                progressDialog.cancel();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void confirmInputs(){
        if (!validate()){
            return;
        }
    }

    private boolean validate(){

        boolean valid = true;
        String title = mTitle.getEditText().getText().toString().trim();
        String description = mDescription.getEditText().getText().toString().trim();
        String price = mPrice.getEditText().getText().toString().trim();
        String location = mLocation.getEditableText().toString();


        if (title.isEmpty()){
            mTitle.setError("Fields can't be empty");
            valid = false;
        }else {
            mTitle.setError(null);
        }

        if (description.isEmpty()){
            mDescription.setError("Fields can't be empty");
            valid = false;
        }else {
            mDescription.setError(null);
        }

        if (price.isEmpty()){
            mPrice.setError("Fields can't be empty");
            valid = false;
        }else {
            mPrice.setError(null);
        }

        if (location.isEmpty()){
            Toast.makeText(this, "choose location", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    public String getmVehicleType() {
        return mVehicleType;
    }

    public void setmVehicleType(String mVehicleType) {
        this.mVehicleType = mVehicleType;
    }

    @Override
    public void onBackPressed() {
        dialogBuilder.create().show();
    }
}
