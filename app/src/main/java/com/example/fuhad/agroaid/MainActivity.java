package com.example.fuhad.agroaid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String image_name;
    String selectImage;
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddmmss");
    Date date = new Date();
    Date x ;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2;
    private Uri file_uri;
    private File file;
    ImageView image;
    Upload up;

    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;

    final int CAMERA_REQUEST = 13323;
    final int GALLERY_REQUEST = 22131;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.social_floating_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.floating_camera);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.floating_gallery);

        cameraPhoto = new CameraPhoto(getApplicationContext());
        galleryPhoto = new GalleryPhoto(getApplicationContext());

        image = (ImageView) findViewById(R.id.image);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                try {
                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                    cameraPhoto.addToGallery();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Something Wrong while taking photos "+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                try{
                    startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQUEST);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),
                            "Something Wrong while selecting photos "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });
        up = new Upload();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST){
            if (resultCode == RESULT_OK){
                String photoPath = cameraPhoto.getPhotoPath();
                selectImage = photoPath;

                try {
                    Toast.makeText(this,"selected and uploading",Toast.LENGTH_LONG).show();
                    Bitmap bitmap = ImageLoader.init().from(selectImage).requestSize(512, 512).getBitmap();
                    image.setImageBitmap(getRotatedBitmap(bitmap, 90));
                    upload(selectImage);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something Wrong while loading photos", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,"not selected",Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == GALLERY_REQUEST){
            if(resultCode == RESULT_OK){
                Uri uri = data.getData();

                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                selectImage = photoPath;
                try {
                    Toast.makeText(this,"selected and uploading",Toast.LENGTH_LONG).show();
                    Bitmap bitmap1 = ImageLoader.init().from(selectImage).requestSize(512, 512).getBitmap();
                    image.setImageBitmap(bitmap1);
                    upload(selectImage);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something Wrong while choosing photos", Toast.LENGTH_SHORT).show();
                }


            }
            else{
                Toast.makeText(this,"not selected",Toast.LENGTH_LONG).show();
            }
        }
    }

    private Bitmap getRotatedBitmap(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap bitmap1 = Bitmap.createBitmap(source,
                0, 0, source.getWidth(), source.getHeight(), matrix, true);
        return bitmap1;
    }

    public void upload(String x){

        final String infile = x;
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;
            //String file = infile;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(MainActivity.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                if(s != null && s.equals("done")){
                    Toast.makeText(MainActivity.this, Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"),Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this,"can't submitted problem",Toast.LENGTH_SHORT).show();
                }

            /*textViewResponse.setText());
            textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());*/
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.upLoad2Server(infile);
                return msg;
            }
        }
        new UploadVideo().execute();
    }
}
