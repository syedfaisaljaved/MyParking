package com.faisaljaved.myparking.Activities;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faisaljaved.myparking.BaseActivity;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.models.Images;
import com.faisaljaved.myparking.models.MyAdData;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
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
import me.shaohui.advancedluban.OnMultiCompressListener;

import static com.faisaljaved.myparking.utils.Constants.LIST_OF_CITIES_INDIA;
import static com.faisaljaved.myparking.utils.RealPathUtil.getRealPathFromURI_API19;

public class PostAdActivity extends BaseActivity {

    private static final String TAG = "PostAdActivity";

    private static final int PICK_IMAGE_REQUEST_CODE = 1;

    private Button mPostAdButton, mImagePicker;
    private TextView mPhotoText;
    private AutoCompleteTextView mLocation;
    private TextInputLayout mTitle, mDescription, mPrice;
    private ImageView mImageView;
    private LinearLayout mImagesLayout;
    private RadioGroup radioGroup;
    private Toolbar toolbar;
    private String mVehicleType;
    private List<Uri> mSelectedImages;
    private List<File> mFileList;
    private List<String> mSelectedImagesURL;
    private iOSDialogBuilder iOSDialogBuilder;

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
        radioGroup = findViewById(R.id.radio_group);
        mTitle = (TextInputLayout) findViewById(R.id.post_ad_title);
        mDescription = (TextInputLayout) findViewById(R.id.post_ad_description);
        mPrice = (TextInputLayout) findViewById(R.id.post_ad_price);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mLocation = findViewById(R.id.choose_location);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, LIST_OF_CITIES_INDIA);
        mLocation.setAdapter(adapter);

        //progressDialog
        progressDialog = new ProgressDialog(this);

        //list initialize
        mSelectedImages = new ArrayList<>();
        mSelectedImagesURL = new ArrayList<>();
        mFileList = new ArrayList<>();

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
                iOSDialogBuilder.build().show();
            }
        });

        //radio listener
        RadioButton radioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        mVehicleType = radioButton.getText().toString();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedid) {
                RadioButton checkRButton = radioGroup.findViewById(checkedid);
                boolean isChecked = checkRButton.isChecked();

                if(isChecked){
                    setmVehicleType(checkRButton.getText().toString());
                }
            }
        });


        mPostAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmInputs();
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
        iOSDialogBuilder = new iOSDialogBuilder(PostAdActivity.this)
                .setTitle("Are you sure?")
                .setSubtitle("All your changes will be lost.")
                .setBoldPositiveLabel(true)
                .setCancelable(false)
                .setPositiveListener(getString(R.string.ok),new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        finish();
                        dialog.dismiss();

                    }
                })
                .setNegativeListener(getString(R.string.dismiss), new iOSDialogClickListener() {
                    @Override
                    public void onClick(iOSDialog dialog) {
                        dialog.dismiss();
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
        mSelectedImages.clear();
        mFileList.clear();
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data !=null) {
            ClipData clipData = data.getClipData();
            if (clipData != null){
                if (clipData.getItemCount() < 5) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri uri = clipData.getItemAt(i).getUri();
                        mSelectedImages.add(uri);
                        File file = new File(getRealPathFromURI_API19(this,uri));
                        mFileList.add(file);
                    }
                }
                else {
                    Toast.makeText(this, "Can't select more than 5 Photos", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Uri uri = data.getData();
                mSelectedImages.add(uri);
                File file = new File(getRealPathFromURI_API19(this,uri));
                mFileList.add(file);
            }
        }
        emptyViews();
        loadImage();
    }

    private void compressImages(List<File> mFileList){
        if (mFileList.isEmpty()){
            return;
        }

        Luban.compress(this, mFileList)
                .putGear(Luban.CUSTOM_GEAR)
                .launch(new OnMultiCompressListener() {
                    @Override
                    public void onStart() {
                        Log.i(TAG, "start");
                    }

                    @Override
                    public void onSuccess(List<File> fileList) {
                        List<Images> imagesList = new ArrayList<>();

                        for (int i = 0; i <fileList.size(); i++) {
                            Log.d(TAG, "onSuccess: "+ fileList.get(i));
                            Uri uri = Uri.fromFile(fileList.get(i));
                            imagesList.add(new Images(getFileNameFromImageUri(uri),uri));
                        }

                        uploadImage(imagesList);
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
                    RequestOptions requestOptions = new RequestOptions();

                    Glide.with(this)
                            .setDefaultRequestOptions(requestOptions)
                            .load(mSelectedImages.get(i))
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

    private void uploadImage(final List<Images> imagesList) {
        progressDialog.setMessage("Uploading");

        mSelectedImagesURL.clear();
        if (imagesList.size() != 0) {
            counter = 0;
            final StorageReference storageReference = firebaseStorage.getReference().child("userData");
            for (int i = 0; i < imagesList.size(); i++) {
                final int finalI = i;
                final StorageReference imageTOBeUploaded = storageReference.child(imagesList.get(i).getImageName());

                imageTOBeUploaded.putFile(imagesList.get(i).getImageUri())
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

                                        if (counter == imagesList.size()){
                                            saveLinkToFirebase();
                                        }
                                    } else {
                                        storageReference.child(imagesList.get(finalI).getImageName()).delete();
                                    }
                                }
                            });
                        } else {
                            counter++;
                            Toast.makeText(PostAdActivity.this, "Couldn't upload " + imagesList.get(finalI).getImageName(), Toast.LENGTH_SHORT).show();
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
        else {
            Toast.makeText(this, "Upload Images First", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveLinkToFirebase(){
        progressDialog.show();
        final String uniqueId = reference.push().getKey();
        String title = mTitle.getEditText().getText().toString().trim();
        String description = mDescription.getEditText().getText().toString().trim();
        String price = mPrice.getEditText().getText().toString().trim();
        String vehicleType = getmVehicleType();
        String location = mLocation.getEditableText().toString().trim();
        Long timestamp = -System.currentTimeMillis();
        String uid = userId;

        final MyAdData myAdData = new MyAdData(uniqueId,uid,title,description,price,vehicleType,location,timestamp,mSelectedImagesURL);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child("data").child(uniqueId).setValue(myAdData);
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
        compressImages(mFileList);
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
        iOSDialogBuilder.build().show();
    }
}
