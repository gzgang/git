package com.lab.ffmpeg;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.lab.ugcmodule.media.ffmpeg.FFmpegNative;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kuaigeng01 on 2017/6/8.
 */

public class TestCase {

    private static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0)
            return "00:00";
        else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = (int) (seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static void test(Context context) {

        String inputFile = "/sdcard/videoplayback.mp4";
        String outputFile = "/sdcard";
        trimVideo(context, inputFile, outputFile, 5000, 20000);
    }

    public static void trimVideo(Context context, String inputFile, String outputFile, long startMs, long endMs) {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final String outputName = "trimmedVideo_" + timeStamp + ".mp4";

        String start = convertSecondsToTime(startMs / 1000);
        String duration = convertSecondsToTime((endMs - startMs) / 1000);

        /**ffmpeg -ss START -t DURATION -i INPUT -vcodec copy -acodec copy OUTPUT
         -ss 开始时间，如： 00:00:20，表示从20秒开始；
         -t 时长，如： 00:00:10，表示截取10秒长的视频；
         -i 输入，后面是空格，紧跟着就是输入视频文件；
         -vcodec copy 和 -acodec copy 表示所要使用的视频和音频的编码格式，这里指定为copy表示原样拷贝；
         INPUT，输入视频文件；
         OUTPUT，输出视频文件*/
        String cmd = "ffmpeg -y " + " -ss " + start + " -t " + duration + " -i " + inputFile + " -vcodec copy -acodec copy " + outputFile + "/" + outputName;

        Log.e("FFmpeg", cmd);

        String[] command = cmd.split(" ");

        long startTime = System.currentTimeMillis();
        Log.e("FFmpeg", "start --------------------");

        FFmpegNative.run(command);

        Log.e("FFmpeg", "finish use time = " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void concatVideo() {
//        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        final String outputName = "trimmedVideo_" + timeStamp + ".mp4";
//
//        String start = convertSecondsToTime(startMs / 1000);
//        String duration = convertSecondsToTime((endMs - startMs) / 1000);

        /**ffmpeg -ss START -t DURATION -i INPUT -vcodec copy -acodec copy OUTPUT
         -ss 开始时间，如： 00:00:20，表示从20秒开始；
         -t 时长，如： 00:00:10，表示截取10秒长的视频；
         -i 输入，后面是空格，紧跟着就是输入视频文件；
         -vcodec copy 和 -acodec copy 表示所要使用的视频和音频的编码格式，这里指定为copy表示原样拷贝；
         INPUT，输入视频文件；
         OUTPUT，输出视频文件*/
//        String cmd = "ffmpeg" + " -ss " + start + " -t " + duration + " -i " + inputFile + " -vcodec copy -acodec copy " + outputFile + "/" + outputName;

        String sdcardPath = Environment.getExternalStorageDirectory().getPath();

        String cmd = "ffmpeg -y -f concat -safe 0 -i " + sdcardPath + "/ffflist.txt -c copy " + sdcardPath + "/concat_result.mp4";
        Log.e("FFmpeg", cmd);

        String[] command = cmd.split(" ");

        long startTime = System.currentTimeMillis();
        Log.e("FFmpeg", "start --------------------");

        FFmpegNative.run(command);

        Log.e("FFmpeg", "finish use time = " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void watermark() {

        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        String cmd = "ffmpeg -i " + sdcardPath + "/videoplayback_output.mp4 -vf \"movie=" + sdcardPath + "/eye.png [watermark]; [in][watermark] overlay=10:10\" " + sdcardPath + "/watermark.mp4";

        cmd = "ffmpeg -i " + sdcardPath + "/videoplayback_output.mp4 -i " + sdcardPath + "/eye.png -filter_complex overlay=5:5 " + sdcardPath + "/watermark_out.mp4";

        cmd = "ffmpeg -y -i " + sdcardPath + "/videoplayback_output.mp4 -i " + sdcardPath + "/eye.png -filter_complex overlay=5:5 -c:v libx264 -preset ultrafast -crf 0 " + sdcardPath + "/watermark_out.mp4";

        Log.e("FFmpeg", cmd);

        String[] command = cmd.split(" ");


//        command = addwaterMark(sdcardPath + "/eye.png", sdcardPath + "/videoplayback_output.mp4", sdcardPath + "/videoplayback_output_water.mp4");
        long startTime = System.currentTimeMillis();
        Log.e("FFmpeg", "start --------------------");

        FFmpegNative.run(command);

        Log.e("FFmpeg", "finish use time = " + (System.currentTimeMillis() - startTime) + "ms");
    }

//    public static String[] addwaterMark(String imageUrl, String videoUrl, String outputUrl) {
//        String[] commands = new String[9];
//        commands[0] = "ffmpeg";
//        //输入
//        commands[1] = "-i";
//        commands[2] = videoUrl;
//        //水印
//        commands[3] = "-i";
//        commands[4] = imageUrl;//此处的图片地址换成带透明通道的视频就可以合成动态视频遮罩。
//        commands[5] = "-filter_complex";
//        commands[6] = "overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2";
//        //覆盖输出
//        commands[7] = "-y";//直接覆盖输出文件
//        //输出文件
//        commands[8] = outputUrl;
//        return commands;
//    }

    /**
     * 调节视频音量
     *
     * @param videoFilePath 视频文件地址
     * @param percent       音量大小[0f~2f]
     */
    public static void adjustVolume() {
        String volume = format(2, 1);

        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        String cmd = "ffmpeg -i " + sdcardPath + "/videoplayback_output.mp4 -af 'volume=2' " + sdcardPath + "/volume_output.mp4";

        cmd = "ffmpeg -y -i " + sdcardPath + "/videoplayback_output.mp4 -vol 50 -strict -2 -vcodec copy " + sdcardPath + "/volume_output2.mp4";
        Log.e("FFmpeg", cmd);

        String[] command = cmd.split(" ");

        long startTime = System.currentTimeMillis();
        Log.e("FFmpeg", "start --------------------");

        FFmpegNative.run(command);

        Log.e("FFmpeg", "finish use time = " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void separateVideo() {
        String volume = format(2, 1);

        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        String cmd = "ffmpeg -y -i " + sdcardPath + "/videoplayback_output.mp4 -vcodec copy -an " + sdcardPath + "/separate_video_output.mp4";

        Log.e("FFmpeg", cmd);

        String[] command = cmd.split(" ");

        long startTime = System.currentTimeMillis();
        Log.e("FFmpeg", "start --------------------");

        FFmpegNative.run(command);

        Log.e("FFmpeg", "finish use time = " + (System.currentTimeMillis() - startTime) + "ms");
    }


    //ffmpeg -y -i input.mp4 -i ainiyiwannian.wav -filter_complex "[0:a] pan=stereo|c0=1*c0|c1=1*c1 [a1], [1:a] pan=stereo|c0=1*c0|c1=1*c1 [a2],[a1][a2]amix=duration=first,pan=stereo|c0<c0+c1|c1<c2+c3,pan=mono|c0=c0+c1[a]" -map "[a]" -map 0:v -c:v libx264 -c:a aac -strict -2 -ac 2 output.mp4

    public static String format(float input, int precision) {
        StringBuilder builder = new StringBuilder("#.0");
        for (int i = 1; i < precision; i++) {
            builder.append("0");
        }

        DecimalFormat df = new DecimalFormat(builder.toString());
        return df.format(input);
    }
}
