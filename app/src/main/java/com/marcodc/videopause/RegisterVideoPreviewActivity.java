package com.marcodc.videopause;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RegisterVideoPreviewActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButtonRec;
    private GridView gridviewVideoLocalContainer;
    private ImageView imageViewVideonotFound;
    private ProgressDialog progressDialog;
    private ArrayList<String> filesPath = new ArrayList<>();
    private VideoLocalAdapter ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_register_video_preview);

        imageViewVideonotFound = (ImageView) findViewById(R.id.imageViewVideonotFound);
        floatingActionButtonRec = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButtonRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterVideoPreviewActivity.this, CameraActivity.class);

                startActivity(intent);

            }
        });
        progressDialog.show();

        gridviewVideoLocalContainer = (GridView) findViewById(R.id.gridviewVideoLocal);
        ad = new VideoLocalAdapter(RegisterVideoPreviewActivity.this, filesPath);
        gridviewVideoLocalContainer.setAdapter(ad);
        gridviewVideoLocalContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedPath = (String)adapterView.getItemAtPosition(i);
                Intent intent = new Intent(RegisterVideoPreviewActivity.this, ShowLocalVideoActivity.class);
                intent.putExtra("pathToVideo", selectedPath);
                startActivity(intent);
            }
        });
       // new LongOperation().execute("");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
       // new LongOperation().execute("");


    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.show();
        new LongOperation().execute("");

    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        private ArrayList<String> filesPath = new ArrayList<>();
        private List<File> files = new ArrayList<>();

        @Override
        protected String doInBackground(String... params) {

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES), "cameraApp");
            if(mediaStorageDir.listFiles() != null) {
           /* String path = Environment.getExternalStorageDirectory().toString()+"/video";
            Log.d("Files", "Path: " + path);*/
           /* File directory = new File(path);*/
                for (int i = 0; i < mediaStorageDir.listFiles().length; i++) {
                    File tmp = mediaStorageDir.listFiles()[i];
                    files.add(tmp);
                }
                Collections.sort(files, new Comparator()
                {
                    public int compare(Object o1, Object o2) {

                        if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                            return -1;
                        } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                            return +1;
                        } else {
                            return 0;
                        }
                    }

                });
                //files  = Arrays.asList(mediaStorageDir.listFiles());

            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            for (int i = 0; i < files.size(); i++){
                Log.d("Files", "FileName:" + files.get(i).getName());
                try {
                    filesPath.add(files.get(i).getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if(filesPath.isEmpty()){
                imageViewVideonotFound.setVisibility(View.VISIBLE);
            } else {
                imageViewVideonotFound.setVisibility(View.GONE);
            }
           // Toast.makeText(RegisterVideoPreviewActivity.this, "FOUND " + filesPath.size(), Toast.LENGTH_SHORT).show();
           /* VideoLocalAdapter ad = new VideoLocalAdapter(RegisterVideoPreviewActivity.this, filesPath);
            gridviewVideoLocalContainer.setAdapter(ad);*/
            ad = new VideoLocalAdapter(RegisterVideoPreviewActivity.this, filesPath);
            gridviewVideoLocalContainer.setAdapter(ad);


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, 1000);

        }


        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
