package com.cookandroid.socket;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;
    ImageView imgView; // Image view
    String imageUrl = "https://klas.khu.ac.kr/"; // URL to connect
    Bitmap bmImg = null; // Bitmap to store loaded image
    CLoadImage task; // AsyncTask class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = (ImageView) findViewById(R.id.imgView);
        task = new CLoadImage();
    }

    // Function to load the image
    public void onClickForLoad(View v) {
        task.execute(imageUrl + "webdata/ko/images/main/img_main_roll_01.jpg");
        Toast.makeText(getApplicationContext(), "Load", Toast.LENGTH_LONG).show();
    }

    // Function to save the image
    /*public void onClickForSave(View v) {
        saveBitmaptoJpeg(bmImg, "DCIM", "image");
        Toast.makeText(getApplicationContext(), "Save", Toast.LENGTH_LONG).show();
    }*/

    public void onClickForSave(View v) {
        if (checkStoragePermission()) {
            saveBitmaptoJpeg(bmImg, "DCIM", "image");
            Toast.makeText(getApplicationContext(), "Save", Toast.LENGTH_LONG).show();
        } else {
            requestStoragePermission();
        }
    }
    private boolean checkStoragePermission() {
        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onClickForSave(null);
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask to perform network operations
    private class CLoadImage extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;
        }

        // Update the ImageView on the main thread
        protected void onPostExecute(Bitmap img) {
            imgView.setImageBitmap(bmImg);
        }
    }

    // Function to save the Bitmap as JPEG
    public static void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name) {
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String foler_name = "/" + folder + "/";
        String file_name = name + ".jpg";
        String string_path = ex_storage + foler_name;
        File file_path;
        file_path = new File(string_path);
        if (!file_path.exists()) {
            file_path.mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(string_path + file_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        } catch (IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
    }
}
