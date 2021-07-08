package com.e.ft_hangout;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.e.ft_hangout.adapter.EmojiAdapter;
import com.e.ft_hangout.tools.Useful;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChoosePicsActivity extends AppCompatActivity {

    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    private static int RESULT_GALLERY = 1;
    private int[] emoji = {
            R.drawable.anger,
            R.drawable.sad,
            R.drawable.disapointed,
            R.drawable.dead,
            R.drawable.girl,
            R.drawable.happy,
            R.drawable.simplet,
            R.drawable.ninja,
            R.drawable.mad
    };
    private static Uri result = null;
    private static int bResult = 0;
    private static Boolean fromModify;
    private static String fromModifyPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pics);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        MainActivity.setPref(getResources().getString(R.string.image_selection), this, actionBar);
        ImageView current_pics = findViewById(R.id.current_pics);
        fromModify = getIntent().getBooleanExtra("FROM_MODIFY", false);
        if (fromModify)
            actionBar.setDisplayHomeAsUpEnabled(false);
        if (getIntent().hasExtra("PICS")) {
            fromModifyPic = getIntent().getStringExtra("PICS");
            if (fromModifyPic.compareTo("pics") != 0) {
                ImageView backPics = findViewById(R.id.back_pics);
                Useful.fillImageView(fromModifyPic, backPics);
                backPics.setVisibility(View.VISIBLE);
                backPics.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Useful.fillImageView(fromModifyPic, current_pics);
                        result = Uri.parse(fromModifyPic);
                        bResult = 0;
                    }
                });
            }
            TextView cancel = findViewById(R.id.cancel_new_pics);
            cancel.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent result = new Intent();
                    setResult(RESULT_CANCELED, result);
                    finish();
                }
            });
        }
        else
            fromModifyPic = "pics";
        Useful.fillImageView(fromModifyPic, current_pics);
        final TextView valid = findViewById(R.id.valid_new_pics);
        final TextView galerie = findViewById(R.id.gallery);
        final GridView emojiListView = findViewById(R.id.emoji_list_view);
        final ImageView camera = findViewById(R.id.camera);
        final ImageView suggestion = findViewById(R.id.suggestion_pics);
        final Activity activity = this;

        emojiListView.setAdapter(new EmojiAdapter(getApplicationContext(), emoji));
        emojiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ImageView current_emoji = view.findViewById(R.id.current_emoji);
                    Drawable draw = current_emoji.getDrawable();
                    current_pics.setImageDrawable(draw);
                    bResult = getDrawableId(current_emoji);
                    result = null;
                }
            });

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                if (result != null)
                    resultIntent.putExtra("imageUriString", result.toString());
                resultIntent.putExtra("imageUriInt", bResult);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
            galerie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(
                            Intent.ACTION_OPEN_DOCUMENT,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_GALLERY);
                }
            });

            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askCameraPermission(activity);
                }
            });

            suggestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView current = findViewById(R.id.current_pics);
                    current.setImageResource(R.drawable.pics);
                    bResult = R.drawable.pics;
                    result = null;
                }
            });
        }

    private void askCameraPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            dispatchTakePictureIntent(activity);
        }
    }

    private File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }

    public void dispatchTakePictureIntent(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile(activity);
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                    "com.e.ft_hangout.fileprovider",
                    photoFile);
            SharedPreferences sharedpreferences;
            sharedpreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("currentPhotoPath", String.valueOf(photoURI));
            editor.commit();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            activity.startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_GALLERY && resultCode == RESULT_OK && null != data) {
            Uri contentUri = data.getData();
            String ext = Useful.getFileExt(contentUri, getApplicationContext());
            List<String> extList = Arrays.asList("jpeg", "jpg", "gif", "png");
            if (extList.contains(ext)) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "_JPEG" + timeStamp + "." + ext;
                ImageView currentPics = findViewById(R.id.current_pics);
                currentPics.setImageURI(null);
                currentPics.setImageResource(0);
                currentPics.setImageURI(contentUri);
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Check for the freshest data.
                getContentResolver().takePersistableUriPermission(contentUri, takeFlags);
                result = contentUri;
                bResult = 0;
            }
            else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_ext_photo), Toast.LENGTH_LONG);
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && null != data){
            SharedPreferences currentPhotoPath = getSharedPreferences("user_pref", MODE_PRIVATE);
            Uri current = Uri.parse(currentPhotoPath.getString("currentPhotoPath", ""));
            ImageView currentPics = findViewById(R.id.current_pics);
            currentPics.setImageURI(current);
            result = current;
            bResult = 0;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent(this);
            } else {
                Toast.makeText(this, getResources().getString(R.string.camera_permission_toast),  Toast.LENGTH_SHORT);
            }
        }
    }

    private int getDrawableId(ImageView iv) {
        return (Integer)iv.getTag();
    }
}
