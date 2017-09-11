package com.lab.ugcmodule.media.ffmpeg;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.kg.v1.tools.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kuaigeng01 on 2017/6/14.
 */

public class OperatorUtils {

    private static final String FFMPEG = "ffmpeg";
    private static final String FFMPEG_tmp = "ffmpeg_tmp";
    private static final String concatFile = "concat.txt";

    public static String getFfmpegCacheTmpDir(Context context) {
        File parentFile = getFfmpegCacheDir(context);
        String filePath = parentFile + File.separator + FFMPEG_tmp;

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        return filePath;
    }

    public static void cleanFfmpegCacheTmpDir(Context context) {
        String path = getFfmpegCacheTmpDir(context);

        FileUtils.clearDirectory(new File(path), false);
    }

    public static File getFfmpegCacheDir(Context context) {

        return context.getExternalFilesDir(FFMPEG);
    }

    public static String createFileForConcat(Context context, List<String> filePathList) {
        File dir = getFfmpegCacheDir(context);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = dir + File.separator + concatFile;

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        StringBuilder builder = new StringBuilder();
        for (String str : filePathList) {
            builder.append("file").append(FFmpegNative.SPLIT);
            builder.append("'").append(str).append("'").append("\r");
        }

        writeContent2File(builder.toString(), file);

        return filePath;
    }

    private static void writeContent2File(String content, File file) {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (null != fileWriter) {
                try {
                    fileWriter.close();
                } catch (Exception e) {

                }
            }
        }
    }


    public static String cmdFormat(String cmd, Object... args) {
        return String.format(Locale.getDefault(), cmd, args);
    }


    /**
     * 获取媒体视频时长
     *
     * @param inputMediaFile 本地媒体文件路径
     * @return 时长，单位：秒
     */
    public static long getMediaFileDuration(String inputMediaFile) {
        if (TextUtils.isEmpty(inputMediaFile)) {
            return -1;
        }

        File file = new File(inputMediaFile);
        if (!file.exists() || !file.isFile()) {
            return -1;
        }

        long duration = -1;
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(inputMediaFile);

            long fileSize = file.length();
            long bitRate = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));

            duration = (fileSize * 8) / (bitRate);//单位，秒

            mediaMetadataRetriever.release();
        } catch (Exception e) {
            //ignore
        }

        return duration;
    }

    public static boolean validSupportFileType(String inputFilePath) {
        if (TextUtils.isEmpty(inputFilePath)) {
            return false;
        }

        String lowCase = inputFilePath.toLowerCase(Locale.US);

        return lowCase.endsWith(".mp4") || lowCase.endsWith(".mp3") || lowCase.endsWith(".acc");
    }

    public static String formatDecimal(double input, int dot) {
        StringBuilder builder = new StringBuilder("0.");
        for (int i = 0; i < dot; i++) {
            builder.append("0");
        }

        DecimalFormat df = new DecimalFormat(builder.toString());

        return df.format(input);
    }

    //"frame= 6036 fps=293 q=29.0 size=   10255kB time=00:03:20.89 bitrate= 418.2kbits/s speed";
    private static final String regex = "\\d{2}:\\d{2}:\\d{2}\\.\\d{2}";
    private static Pattern mPattern;

    public static String extractTimeFromFFmpegLog(String log) {
        String result = null;

        if (!TextUtils.isEmpty(log) && (log.startsWith("frame=") || log.startsWith("size="))) {
            if (null == mPattern) {
                mPattern = Pattern.compile(regex);
            }

            Matcher matcher = mPattern.matcher(log);
            if (matcher.find()) {
                result = matcher.group(0);
            }
        }

        return result;
    }

    public static long convertTimeString2Millisecond(String timeStr) {//timeStr = 00:04:25
        if (TextUtils.isEmpty(timeStr)) {
            return 0;
        }

        int dot = timeStr.indexOf(".");
        String mil = timeStr.substring(dot + 1);
        timeStr = timeStr.substring(0, dot);
        try {
            String[] array = timeStr.split(":");

            int h = Integer.parseInt(array[0]);
            int m = Integer.parseInt(array[1]);
            int s = Integer.parseInt(array[2]);
            int mm = Integer.parseInt(mil);

            return (h * 60 * 60 + m * 60 + s) * 1000L + mm * 10;
        } catch (Exception e) {

        }
        return 0;
    }
}
