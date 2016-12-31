package com.marcodc.videopause;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.marcodc.videopause.utils.CountDownTimerWithPause;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "zoom camera";
    private Camera mCamera;
    private CameraRegister mPreview;
    private boolean isRecording = false;
    private MediaRecorder mMediaRecorder;
    private String pahtToFile;
    private CameraActivity mContext;
    private CountDownTimerWithPause countDown;
    private TextView textViewCountDown;
    private static int which = -1;
    private FrameLayout preview;
    private List<String> files = new ArrayList<>();
    private List<Integer> listCameraRecord = new ArrayList<>();
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private FloatingActionButton floatingActionButtonContinue;
    private FloatingActionButton floatingActionButtonPause;
    private long millisUntilFinished = 30;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        // Create an instance of Camera
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(which == -1){
            which = intent.getIntExtra("cameraId", 0);
        }



        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1122);
        } else {

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            1122);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                mCamera = getCameraInstance();
                // Create our Preview view and set it as the content of our activity.
                mPreview = new CameraRegister(this, mCamera);

                preview.addView(mPreview);
            }


        }


        floatingActionButtonPause = (FloatingActionButton) findViewById(R.id.floatingActionButtonPause);
        floatingActionButtonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                releaseMediaRecorder();       // if you are using MediaRecorder, release it first
                //releaseCamera();
                countDown.pause();
                isRecording = false;
                floatingActionButtonPause.setVisibility(View.GONE);
                floatingActionButtonContinue.setVisibility(View.VISIBLE);
                MediaScannerConnection.scanFile(CameraActivity.this, new String[]{pahtToFile}, null, null);
                //prepareVideoRecorder();

            }
        });


        floatingActionButtonContinue = (FloatingActionButton) findViewById(R.id.floatingActionButtonContinue);
        floatingActionButtonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //mCamera = getCameraInstance();
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is prepared,
                    // now you can start recording
                    mMediaRecorder.start();

                    isRecording = true;
                    countDown.resume();
                    floatingActionButtonContinue.setVisibility(View.GONE);
                    floatingActionButtonPause.setVisibility(View.VISIBLE);
                }



            }
        });
        final FloatingActionButton floatingActionButtonFlip = (FloatingActionButton) findViewById(R.id.floatingActionButtonFlip);
        floatingActionButtonFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaScannerConnection.scanFile(CameraActivity.this, new String[]{pahtToFile}, null, null);
                switch (which) {
                    case 0:
                        which = 1;
                        break;
                    case 1:
                        which = 0;
                        break;
                }
                //CameraActivity.this.recreate();
            if (isRecording) {
                mMediaRecorder.stop();
                releaseMediaRecorder();       // if you are using MediaRecorder, release it first
                releaseCamera();

                mCamera = getCameraInstance();
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is prepared,
                    // now you can start recording
                    mMediaRecorder.start();

                    isRecording = true;
                    //countDown.start();
                }
            } else {
                CameraActivity.this.recreate();

            }

            }
        });






        textViewCountDown = (TextView) findViewById(R.id.textViewCountDown);
        Typeface face= Typeface.createFromAsset(this.getAssets(), "Peace Sans.otf");
        textViewCountDown.setTypeface(face);
        mContext = this;

        countDown = new CountDownTimerWithPause(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                millisUntilFinished = millisUntilFinished;
                textViewCountDown.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {

            }
        };


        final FloatingActionButton floatingActionButtonStop = (FloatingActionButton) findViewById(R.id.floatingActionButtonStop);

        floatingActionButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(mMediaRecorder != null) {
                        // stop recording and release camera
                        //mMediaRecorder.stop();  // stop the recording
                        releaseMediaRecorder(); // release the MediaRecorder object
                        mCamera.lock();         // take camera access back from MediaRecorder

                        // inform the user that recording has stopped
                        floatingActionButtonStop.setVisibility(View.GONE);

                        MediaScannerConnection.scanFile(CameraActivity.this, new String[]{pahtToFile}, null, null);
                                       /* Intent returnIntent = new Intent();
                                        returnIntent.putExtra("result",pahtToFile);
                                        setResult(Activity.RESULT_OK,returnIntent);*/
                        countDown.cancel();
                        isRecording = false;
                    }
                    new MergeVideo().execute();

            }
        });
        final FloatingActionButton floatingActionButtonRecord = (FloatingActionButton) findViewById(R.id.floatingActionButtonRecord);
        floatingActionButtonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    // stop recording and release camera
                    mMediaRecorder.stop();  // stop the recording
                    releaseMediaRecorder(); // release the MediaRecorder object
                    mCamera.lock();         // take camera access back from MediaRecorder

                    // inform the user that recording has stopped
                    //floatingActionButtonRecord.setBackgroundColor(Color.RED);
                    MediaScannerConnection.scanFile(CameraActivity.this, new String[]{pahtToFile}, null, null);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",pahtToFile);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                    countDown.cancel();
                    isRecording = false;
                } else {
                    // initialize video camera
                    releaseCamera();
                    mCamera = getCameraInstance();
                    if (prepareVideoRecorder()) {
                        // Camera is available and unlocked, MediaRecorder is prepared,
                        // now you can start recording
                        mMediaRecorder.start();

                        // inform the user that recording has started
                        floatingActionButtonStop.setVisibility(View.VISIBLE);
                        //floatingActionButtonRecord.setBackgroundColor(Color.GREEN);;
                        isRecording = true;
                        countDown.start();
                        floatingActionButtonContinue.setVisibility(View.GONE);
                        floatingActionButtonFlip.setVisibility(View.GONE);
                        floatingActionButtonPause.setVisibility(View.VISIBLE);
                    } else {
                        // prepare didn't work, release the camera
                        releaseMediaRecorder();
                        releaseCamera();
                        // inform user
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1122: {
                // If request is cancelled, the result arrays are empty.
                if(hasPermissions(this, PERMISSIONS)){

                    mCamera = getCameraInstance();
                    // Create our Preview view and set it as the content of our activity.
                    mPreview = new CameraRegister(this, mCamera);

                    preview.addView(mPreview);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void toast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }


    private boolean prepareVideoRecorder(){
        try {
       // mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();

        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
       // mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
       // mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        pahtToFile = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
        mMediaRecorder.setOutputFile(pahtToFile);
        files.add(pahtToFile);
        listCameraRecord.add(which);
        //mMediaRecorder.setOutputFile("/sdcard/videocapture_example.mp4");
        //mMediaRecorder.setMaxDuration(50000); // 50 seconds
        //mMediaRecorder.setMaxFileSize(5000000); // Approximately 5 megabytes
        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

       // mMediaRecorder.setOrientationHint(0);
        if(which == 0){
            mMediaRecorder.setOrientationHint(270);
        } else {
            mMediaRecorder.setOrientationHint(90);
        }
        // Step 6: Prepare configured MediaRecorder

            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    /** Check if this device has a camera */
    private boolean checkCameraHardware() {
        // this device has a camera
// no camera on this device
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    protected void setDisplayOrientation(Camera camera, int angle){
        Method downPolymorphic;
        try
        {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", int.class);
            if (downPolymorphic != null)
                downPolymorphic.invoke(camera, angle);
        }
        catch (Exception e1)
        {
        }
    }
    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance(){
        Camera c = null;
        try {
            switch (which) {
                case 0:
                    c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    setDisplayOrientation(c, 90);
                    break;
                case 1:
                    c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    Camera.Parameters parameters = c.getParameters();
                    setDisplayOrientation(c, 90);
                    //setDisplayOrientation(c, 0);
                    break;
            }

            //c = Camera.open(); // attempt to get a Camera instance
           // c.setDisplayOrientation(90);
        }
        catch (Exception e){
            e.printStackTrace();
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "cameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "idProdotto_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public class MergeVideo extends AsyncTask<Void, Void, Void> {

        private String pathf;
        private ProgressDialog progressDialog;
        private String filename = null;

        private String mFileName;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CameraActivity.this,
                    "Preparing for upload", "Please wait...", true);
            // do initialization of required objects objects here
        }

        ;

        @Override
        protected Void doInBackground(Void... voids) {

           /* FFmpeg ffmpeg = FFmpeg.getInstance(CameraActivity.this);
            try {
                ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                    @Override
                    public void onStart() {}

                    @Override
                    public void onFailure() {}

                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onFinish() {

                    }
                });
            } catch (FFmpegNotSupportedException e) {
                e.printStackTrace();
            }


            try {
                File tmpFile = getOutputMediaFile(MEDIA_TYPE_VIDEO);
               *//* try {
                    tmpFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }*//*
                // to execute "ffmpeg -version" command you just need to pass "-version"
                String[] command = "".split(" ");
                String[] errorSoon = {"-i", files.get(0), "-vf", "transpose=3", tmpFile.getPath()};

                ffmpeg.execute(errorSoon, new ExecuteBinaryResponseHandler() {

                    @Override
                    public void onStart() {}

                    @Override
                    public void onProgress(String message) {
                        Log.e("ffmpeg", message);
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.e("ffmpeg", message);
                    }

                    @Override
                    public void onSuccess(String message) {
                        Log.e("ffmpeg", message);
                    }

                    @Override
                    public void onFinish() {
                        Log.e("ffmpeg", "finish");*/
                        try {
                            String paths[] = new String[files.size()];
                            Movie[] inMovies = new Movie[files.size()];

                            for (int i = 0; i < files.size(); i++) {
                                paths[i] = files.get(i);
                                inMovies[i] = MovieCreator.build(files.get(i));
                                /*if(listCameraRecord.get(i) == 1){
                                    inMovies[i].setMatrix(Matrix.ROTATE_90);
                                }*/
                            }

                            List<Track> videoTracks = new LinkedList<Track>();
                            List<Track> audioTracks = new LinkedList<Track>();
                            for (Movie m : inMovies) {
                                for (Track t : m.getTracks()) {
                                    if (t.getHandler().equals("soun")) {
                                        audioTracks.add(t);
                                    }
                                    if (t.getHandler().equals("vide")) {
                                        videoTracks.add(t);
                                    }
                                }
                            }

                            Movie result = new Movie();

                            if (audioTracks.size() > 0) {
                                result.addTrack(new AppendTrack(audioTracks
                                        .toArray(new Track[audioTracks.size()])));
                            }
                            if (videoTracks.size() > 0) {
                                result.addTrack(new AppendTrack(videoTracks
                                        .toArray(new Track[videoTracks.size()])));
                            }

                            BasicContainer out = (BasicContainer) new DefaultMp4Builder()
                                    .build(result);

                            File tmp = getOutputMediaFile(MEDIA_TYPE_VIDEO);
                            @SuppressWarnings("resource")
                            FileChannel fc = new RandomAccessFile(tmp, "rw").getChannel();
                            //File tmp = getOutputMediaFile(MEDIA_TYPE_VIDEO);
                            out.writeContainer(fc);
                            fc.close();
                            MediaScannerConnection.scanFile(CameraActivity.this, new String[]{tmp.getPath()}, null, null);
                        } catch (Exception e) {
                            releaseCamera();
                            e.printStackTrace();
                        } finally {
                            for(String fileTmp : files){
                                new File(fileTmp).delete();
                            }
                        }
                   /* }
                });
            } catch (FFmpegCommandAlreadyRunningException e) {
                e.printStackTrace();
            }*/

            return null;
        }


        @Override
        protected void onPostExecute(Void x) {
            progressDialog.dismiss();
            Intent i = new Intent(CameraActivity.this, RegisterVideoPreviewActivity.class);
            startActivity(i);
            finish();
        }
    }


}
