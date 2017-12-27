package com.lab.ffmpegstu;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kg.v1.global.Global;
import com.lab.ugcmodule.media.MediaOperator;
import com.lab.ugcmodule.media.MediaOperatorListener;
import com.lab.ugcmodule.media.MediaOperatorListenerAdapter;
import com.lab.ugcmodule.media.OperatorResult;
import com.lab.ugcmodule.media.ffmpeg.cmd.Overlay;
import com.lab.ugcmodule.media.ffmpeg.cmd.Watermark;
import com.lab.ugcmodule.media.service.MediaOperatorTaskBuilder;

import org.wysaid.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    static String[] TEST_CASE_ITEM = {"trimVideo", "concatVideo",
            "watermark", "addBackgroundMusic",
            "adjustVolume", "fastVideo",
            "slowVideo", "addFilter",
            "multipleTask", "overlay",
            "compress",
            "scaleAndCompress"
    };

    static String sdcardPath = Environment.getExternalStorageDirectory().getPath() + "/abcd";
    static String sdcardPath_output = Environment.getExternalStorageDirectory().getPath() + "/abcd/output";
    private static final String TAG = "FFmpegMediaOperatorImpl";


    private RecyclerView mRecyclerView;
    private TextView status_tx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Global.setGlobalContext(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                MediaOperator.getInstance().cancelAllTask();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new SimpleAdapter());

        status_tx = (TextView) findViewById(R.id.status_tx);

        MediaOperator.getInstance().initRemoteService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MediaOperator.getInstance().cancelAllTask();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        String method = String.valueOf(v.getTag());

//        Toast.makeText(this, method, Toast.LENGTH_SHORT).show();

        switch (method) {
            case "addBackgroundMusic":

                addBackgroundMusic();
                break;
            case "adjustVolume":

                adJustVolume();
                break;
            case "watermark":

                watermark();
                break;
            case "trimVideo":

                trimVideo();
                break;
            case "fastVideo":

                fastOrSlow(2f);
                break;
            case "slowVideo":

                fastOrSlow(0.5f);
                break;
            case "addFilter":

                addFilter();
                break;

            case "concatVideo":

                concatVideo();
                break;

            case "multipleTask":

                multipleTask();
                break;

            case "overlay":

                overlay();
                break;
            case "compress":

                compress();
                break;
            case "scaleAndCompress":

                scaleAndCompress();
                break;
        }
    }

    class SimpleAdapter extends RecyclerView.Adapter<SimpleViewHolder> {
        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.simple_recycler_view_item_ly, parent, false);
            SimpleViewHolder holder = new SimpleViewHolder(view);

            holder.textView.setOnClickListener(MainActivity.this);
            return holder;
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            holder.textView.setText(TEST_CASE_ITEM[position]);

            holder.textView.setTag(TEST_CASE_ITEM[position]);
        }

        @Override
        public int getItemCount() {
            return TEST_CASE_ITEM.length;
        }
    }


    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public SimpleViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView;
        }
    }

    private void concatVideo() {

        String outputMp4 = sdcardPath_output + "/output_concat_video.mp4";

        File file = new File(sdcardPath + "/tmpRecording/");
        File[] files = file.listFiles();

        List<String> array = new ArrayList<>();
        for (File item : files) {
            array.add(item.getAbsolutePath());
        }

        MediaOperator.getInstance().concatVideo(array, outputMp4, createListener("concatVideo"));
    }

    private void addFilter() {
        String inputMp4 = sdcardPath + "/videoplayback_output.mp4";
        String outputMp4 = sdcardPath_output + "/output_add_filter.mp4";
        String filterConfig = Config.effectConfigs[1];

        MediaOperator.getInstance().addFilter(inputMp4, filterConfig, outputMp4, createListener("addFilter"));
    }

    private void addBackgroundMusic() {
        String inputMp4 = sdcardPath + "/videoplayback_output.mp4";
        String inputBgMusic = sdcardPath + "/ten_second_music.mp3";
        String outputMp4 = sdcardPath_output + "/output_add_bg_music.mp4";

        MediaOperator.getInstance().addBackgroundMusic(inputMp4, inputBgMusic, outputMp4, createListener("addBackgroundMusic"));
    }

    private void adJustVolume() {
        String inputMp4 = sdcardPath + "/videoplayback_output.mp4";
        String outputMp4 = sdcardPath_output + "/output_volume_video.mp4";
        float percent = 4;

        MediaOperator.getInstance().adjustVolume(inputMp4, percent, outputMp4, createListener("adJustVolume"));
    }

    private void fastOrSlow(float v) {
        String inputMp4 = sdcardPath + "/videoplayback_output.mp4";
        String outputMp4 = sdcardPath_output + "/output_" + (v > 1 ? "fastVideo" : "slowVideo") + "_video.mp4";

        MediaOperator.getInstance().fastOrSlowVideo(inputMp4, v, outputMp4, createListener((v > 1 ? "fastVideo" : "slowVideo")));
    }

    private void trimVideo() {
        String inputMp4 = sdcardPath + "/videoplayback.mp4";
        String outputMp4 = sdcardPath_output + "/output_trim_video_output.mp4";
        int start = 5;
        int duration = 15;

        MediaOperator.getInstance().trimVideo(inputMp4, start, duration, outputMp4, createListener("trimVideo"));
    }

    private void watermark() {
        String inputMp4 = sdcardPath + "/videoplayback_output.mp4";
        String outputMp4 = sdcardPath_output + "/output_watermark_video_scale.mp4";
        String waterImg;

        List<Watermark> watermarks = new ArrayList<>();
        Watermark watermark;
        for (int i = 2; i < 4; i++) {

            if (i % 2 == 1) {
                waterImg = sdcardPath + "/eye.png";
            } else {
                waterImg = sdcardPath + "/eye.gif";
            }
            watermark = new Watermark(waterImg, i * 50, i * 20);
            watermarks.add(watermark);
        }


        MediaOperator.getInstance().watermark(inputMp4, watermarks, outputMp4, createListener("watermark"));
    }

    private void multipleTask() {
        String inputMp4 = sdcardPath + "/videoplayback_output.mp4";
        String outputMp4 = sdcardPath_output + "/output_multiple_video.mp4";
        String waterImg;

        List<Watermark> watermarks = new ArrayList<>();
        Watermark watermark;
        for (int i = 0; i < 4; i++) {

            if (i % 2 == 1) {
                waterImg = sdcardPath + "/eye.png";
            } else {
                waterImg = sdcardPath + "/eye.gif";
            }
            watermark = new Watermark(waterImg, i * 100, i * 20);
            watermarks.add(watermark);
        }


        MediaOperatorTaskBuilder builder = new MediaOperatorTaskBuilder.TaskBuilder()
                .prepare(inputMp4, outputMp4)
//                .addFilter(Config.effectConfigs[1])
//                .watermark(watermarks)
                .adjustVolume(1.5f)
//                .extractVideo()
                .compress(23)
                .build();

        MediaOperator.getInstance().commandMultipleTask(builder, createListener("multipleTask"));
    }

    private void overlay() {
        String inputMp4 = sdcardPath + "/videoplayback_output.mp4";
        String outputMp4 = sdcardPath_output + "/output_overlay_video.mp4";

        String overlay = sdcardPath + "/input_overlay.mkv";
        List<Overlay> overlays = new ArrayList<>();
        Overlay item;

        for (int i = 0; i < 3; i++) {
            item = new Overlay(overlay, 3 + i * 3, 100, 80);
            overlays.add(item);
        }

        MediaOperator.getInstance().overlay(inputMp4, overlays, outputMp4, createListener("overlay"));
    }

    private void compress() {
        String inputMp4 = sdcardPath + "/input_test.mp4";
        String outputMp4 = sdcardPath_output + "/output_compress_video.mp4";

        MediaOperator.getInstance().compress(inputMp4, 23, outputMp4, createListener("compress"));
    }


    private void scaleAndCompress() {
        String inputMp4 = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Video/test.mp4";
        inputMp4 = sdcardPath+"/input_test.mp4";
        String outputMp4 = sdcardPath_output + "/output_scale_compress_video.mp4";

        MediaOperator.getInstance().scaleAndCompress(inputMp4, 480, 360, outputMp4, createListener("scaleAndCompress"));
    }

    private MediaOperatorListener createListener(final String who) {
        return new MediaOperatorListenerAdapter() {

            long start;

            @Override
            public void onStart(int forWho) {
                start = System.currentTimeMillis();

                Log.d(TAG, who + " onStart");

                status_tx.setText(who + "\nonStart...");
            }

            @Override
            public void onError(int forWho) {
                Log.w(TAG, who + " onError");
                status_tx.setText(who + "\nonError");
            }

            @Override
            public void onComplete(int forWho, OperatorResult result) {
                Log.d(TAG, who + " onComplete " + result);
                status_tx.setText(who + "\nonComplete\nuse time =(" + (System.currentTimeMillis() - start) + "ms)\n" + result);
            }

            @Override
            public void onProgressUpdate(int forWho, int percent) {

                status_tx.setText(who + "\n" + forWho + " : " + percent + "%");
            }
        };
    }
}
