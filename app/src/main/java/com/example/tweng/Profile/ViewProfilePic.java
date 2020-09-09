package com.example.tweng.Profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tweng.FirebaseOps.UploadProfilePic;
import com.example.tweng.R;
import com.example.tweng.local_storage.SaveProfilePic;

import java.io.File;
import java.io.IOException;

public class ViewProfilePic extends AppCompatActivity {
    Uri imageUri;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imageView;
    String filename = "profile__pic.jpg";
    Bitmap bitmap;
    File file;
    File directory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_pic);
        setSupportActionBar((Toolbar) findViewById(R.id.profile_pic_toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        //actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Profile Pic");
        //
        imageView = findViewById(R.id.profile_imageView);
        //
        directory = new File(getApplicationContext().getFilesDir(), filename);
        //file = new File(directory, "profile_pic" + ".jpg");
        if (directory.exists()) {
            bitmap = BitmapFactory.decodeFile(directory.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_pic_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_pic_edit:
                // User chose the "Settings" item, show the app settings UI...

                showDialog(ViewProfilePic.this);

                break;
            case R.id.profile_pic_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                new SaveProfilePic(getApplicationContext(), bitmap);
                new UploadProfilePic(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //imageView.setImageURI(imageUri);

        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            assert extras != null;
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            new SaveProfilePic(getApplicationContext(), imageBitmap);
        }

    }

    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.choose_pic);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        ImageButton gallery = dialog.findViewById(R.id.galleryImageButton);
        ImageButton camera = dialog.findViewById(R.id.cameraImageButton);
        ImageButton delete = dialog.findViewById(R.id.deleteImageButton);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 101);
                dialog.dismiss();

            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo Delete Profile Pic function
                file.delete();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}
