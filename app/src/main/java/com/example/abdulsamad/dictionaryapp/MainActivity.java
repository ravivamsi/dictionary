package com.example.abdulsamad.dictionaryapp;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.googlecode.tesseract.android.TessBaseAPI;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {
    Bitmap image; //our image
    private TessBaseAPI mTess; //Tess API reference
    String datapath = ""; //path to folder containing language data file
    EditText word;
    private static Uri imageUri;
    SharedPreferences preferences;
    ProgressDialog progressDialog;
    AlertDialog.Builder showDialog;
    String[] numberoOfWords;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Result.object=new ArrayList<>();
        showDialog=new AlertDialog.Builder(this);
        showDialog.setCancelable(true);
        showDialog.setTitle("Error");
        showDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        showDialog.setMessage("Internal Error Occurred ! Please try again later");
        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Searching for Meanings...");
        preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
        word=(EditText)findViewById(R.id.vinumber);
        //init image
        image = BitmapFactory.decodeResource(getResources(), R.drawable.testimage);
        datapath = getFilesDir()+ "/tesseract/";
        //make sure training data has been copied
        checkFile(new File(datapath + "tessdata/"));
        //initialize Tesseract API
        String lang = "eng";
        mTess = new TessBaseAPI();
        mTess.init(datapath, lang);
    }
    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }
    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";
            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getText(Bitmap bitmap){
        String OCRresult = null;
        mTess.setImage(getResizedBitmap(bitmap,100));
        OCRresult = mTess.getUTF8Text();
        word.setText(OCRresult);
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    //////////////////////////////////
    public  void startGalleryActivity(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        } else {
            startGl();
        }
    }
    private  void startGl()
    {
        Intent libraryIntent = new Intent(Intent.ACTION_PICK);
        libraryIntent.setType("image/*");
        ((Activity) this).startActivityForResult(libraryIntent, 0);
    }
    //implement camera methods
    public  void processImage(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},1);
        } else {
            getcamera();
        }
    }
    public void getcamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
        } else {
            oc();
        }
    }
    private void oc() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "myTempImage");
        imageUri =getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Log.d("uri=" + imageUri, "camera: ");
        SharedPreferences.Editor e=preferences.edit();
        e.putString("imageuri",imageUri.toString());
        e.commit();
        e.apply();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, this);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        ((Activity) this).startActivityForResult(intent, 1);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getcamera();
            }
        }
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGl();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(preferences.getString("imageuri", "")));
                    getText(bitmap);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }else if (requestCode == 0) {
                try {
                    Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                    getText(selectedBitmap);
                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void parsejson(JSONObject object){
        Result.object.add(object);
        Toast.makeText(this, "obj length="+Result.object.size(), Toast.LENGTH_SHORT).show();
        if (Result.object.size()==numberoOfWords.length) {
            finish();
            startActivity(new Intent(MainActivity.this, Result.class));
        }else
        {
            Toast.makeText(this, "request="+numberoOfWords[Result.object.size()], Toast.LENGTH_SHORT).show();
            getMeanings(numberoOfWords[Result.object.size()]);
        }
    }
    public void getMeanings(final String word) {
        progressDialog.show();
        //http://urbanscraper.herokuapp.com/define/young
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("urbanscraper.herokuapp.com")
                .appendPath("define")
                .appendPath(word);
        String url=builder.build().toString();
        System.out.print("url="+url);
        JsonObjectRequest R=new JsonObjectRequest(Request.Method.GET, url, "", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("onResponse:", response.toString());
                progressDialog.hide();
                parsejson(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(error.toString(), "onErrorResponse: ");
                        Toast.makeText(MainActivity.this, "Error searching word", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }
        );
        AppController.getInstance().addToRequestQueue(R);
    }
    public void search(View view)
    {
        if (word.getText().length()>0)
        {
            numberoOfWords=word.getText().toString().split(" ");
            Toast.makeText(this, "Length="+numberoOfWords.length, Toast.LENGTH_SHORT).show();
            getMeanings(numberoOfWords[0]);
        }else
        {
            Toast.makeText(this, "Please Enter a Word to search", Toast.LENGTH_SHORT).show();
        }
    }
}