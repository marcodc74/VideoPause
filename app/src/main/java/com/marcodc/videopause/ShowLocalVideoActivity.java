package com.marcodc.videopause;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ShowLocalVideoActivity extends AppCompatActivity {

    private VideoView videoViewVideoLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_local_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


      final String selectedPath = getIntent().getStringExtra("pathToVideo");


        videoViewVideoLocal = (VideoView) findViewById(R.id.videoViewVideoLocal);
        videoViewVideoLocal.setVideoPath(selectedPath);

        videoViewVideoLocal.seekTo(100);
        final FloatingActionButton floatingActionButtonPlay = (FloatingActionButton) findViewById(R.id.floatingActionButtonPlay);
        floatingActionButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if(videoViewVideoLocal.isPlaying()){
                    videoViewVideoLocal.stopPlayback();
                    floatingActionButtonPlay.setImageDrawable(ContextCompat.getDrawable(ShowLocalVideoActivity.this, R.drawable.ic_media_play));

                } else {*/
                    //videoViewVideoLocal.start();

                MediaController mediaController = new
                        MediaController(ShowLocalVideoActivity.this);
                mediaController.setAnchorView(videoViewVideoLocal);
                videoViewVideoLocal.setMediaController(mediaController);

                videoViewVideoLocal.start();
                floatingActionButtonPlay.setVisibility(View.GONE);
                //floatingActionButtonPlay.setImageDrawable(ContextCompat.getDrawable(ShowLocalVideoActivity.this, R.drawable.ic_media_pause));
                //}

            }
        });
        FloatingActionButton floatingActionButtonUpload = (FloatingActionButton) findViewById(R.id.floatingActionButtonUpload);
        floatingActionButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not yet implemented", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        FloatingActionButton floatingActionButtonShare = (FloatingActionButton) findViewById(R.id.floatingActionButtonShare);
        floatingActionButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackBar = Snackbar.make(view, "Share your video", Snackbar.LENGTH_LONG);
                snackBar.setAction("Click Upload", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        shareIntent.setType("video/mp4");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "nope");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "nope");

                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" +selectedPath));
                        startActivityForResult(Intent.createChooser(shareIntent, "Share Your Video"),2233);
                    }
                });
                snackBar.getView().bringToFront();
                snackBar.show();
            }
        });
        FloatingActionButton floatingActionButtonDelete = (FloatingActionButton) findViewById(R.id.floatingActionButtonDelete);
        floatingActionButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackBar = Snackbar.make(view, "Delete this video", Snackbar.LENGTH_LONG);
                snackBar.setAction("Click to delete", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new File(selectedPath).delete();
                        ShowLocalVideoActivity.this.finish();
                    }
                });
                snackBar.show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public byte[] readBytes(String dataPath) throws IOException {

        InputStream inputStream = new FileInputStream(dataPath);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

}
