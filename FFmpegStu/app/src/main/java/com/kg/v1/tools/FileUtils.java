package com.kg.v1.tools;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.kg.v1.utils.IoUtil;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * file utils
 * Created by gzg on 2016/4/5.
 */
public class FileUtils {
    //下载文件保存目录 rootDir/Ploy
    public static final String ROOT_DIR_NAME = "pv";
    public static final String PHOTO_PATH = File.separator + FileUtils.ROOT_DIR_NAME + File.separator + "temp" + File.separator;
    public static String AVATAR_NAME = "pv_avatar.jpg";

    /**
     * 判断 sdcard 是否可用
     *
     * @return true：可用 or false
     */
    public static boolean ExistSDCard() {

        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getDirectoryDcim(String videoId) {
        String cachePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
        if (!TextUtils.isEmpty(cachePath)) {
            File file = new File(cachePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return cachePath + "/" + videoId + ".mp4";
    }

    public static String getCapturePictureSavePath(String name) {
        String cachePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        if (!TextUtils.isEmpty(cachePath)) {
            File file = new File(cachePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return cachePath + name;
    }

    /**
     * 清空某个目录，或刪除某个文件
     */
    public static void clearDir(File fileToDelete) {

        if (null == fileToDelete) {
            return;
        }

        // 先改名再删除，防止出现EBUSY(Device or resource busy)
        File file;
        final File renamedFile = new File(fileToDelete.getAbsolutePath() + System.currentTimeMillis());
        boolean res = fileToDelete.renameTo(renamedFile);

        if (res) {
            file = renamedFile;
        } else {
            file = fileToDelete;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        clearDir(files[i]);
                    } else if (files[i].isFile()) {
                        files[i].delete();
                    }
                }
            }
            file.delete();

        } else if (file.isFile()) {
            file.delete();
        }
    }

    public static boolean copyFile(String srcFileName, String destFileName) throws IOException {
        File srcFile = new File(srcFileName);
        File destFile = new File(destFileName);
        if (!srcFile.exists() || !srcFile.isFile() || !srcFile.canRead()) {
            return false;
        }
        if (destFile.exists()) {
            destFile.delete();
        }
        try {
            InputStream inStream = new FileInputStream(srcFile);
            FileOutputStream outStream = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int byteRead = 0;
            while ((byteRead = inStream.read(buf)) != -1) {
                outStream.write(buf, 0, byteRead);
            }
            outStream.flush();
            outStream.close();
            inStream.close();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static File makeDirAndCreateFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.isDirectory()) {
            file.mkdirs();
            return file;

        }
        String parent = file.getParent();
        File parentFile = new File(parent);
        if (!(parentFile.exists())) {
            if (parentFile.mkdirs()) {
                file.createNewFile();
            }
        }

        if (!(file.exists())) {
            file.createNewFile();
        }

        return file;
    }

    public static boolean writeAssetsFile(Context context, String path, String name) {
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(name);
            outputStream = new FileOutputStream(path);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 拷贝文件
     *
     * @param path
     * @param inputStream
     */
    public static void writeFile(String path, InputStream inputStream) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(path));
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    * String转换为File。
    * 如果创建失败，会删除文件。
    * @param string 字符内容
    * @param file   文件
    * @return 如果创建成功，返回true；否则返回false
    */
    public static boolean string2File(String string, File file) {
        if (file == null || string == null) return false;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(string);
            bufferedWriter.flush();
            return true;
        } catch (IOException e) {
            deleteFile(file);
            return false;
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            final File copyFile = new File(file.getAbsolutePath() + System.currentTimeMillis());
            if (file.renameTo(copyFile)) {
                copyFile.delete();
                return true;
            } else {
                file.delete();
            }
        }
        return false;
    }

    public static boolean deleteDir(File dirfile) {
        if (dirfile != null && dirfile.exists()) {
            final File copyFile = new File(dirfile.getAbsolutePath() + System.currentTimeMillis());
            if (dirfile.renameTo(copyFile)) {
                copyFile.delete();
                return true;
            } else {
                dirfile.delete();
            }
        }
        return false;
    }

    /**
     * 删除文件(夹)及包含文件
     *
     * @param path
     * @return
     */
//    public static boolean clearDir(final File path) {
//        return clearDirectory(path, true);
//    }


    /**
     * 清空文件夹。
     *
     * @param path
     * @param removeSelf true 删除文件夹及内容，false 仅仅删除文件夹内容
     */
    public static boolean clearDirectory(File path, boolean removeSelf) {
        if (path == null || !path.exists()) {
            return true;
        }
        if (path.isDirectory()) {
            final File[] files = path.listFiles();
            if (files != null) {
                for (File child : files) {
                    clearDirectory(child, true);
                }
            }
        }
        if (removeSelf) {
            return path.delete();
        }
        return true;
    }

    /**
     * File转换为String。
     *
     * @param file 文件
     * @return 如果读取失败，返回null；否则返回字符串形式。
     */
    public static String file2String(File file) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            IoUtil.copy(fis, baos);
            return baos.toString();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            IoUtil.closeSilently(fis);
            IoUtil.closeSilently(baos);
        }
        return null;
    }


    /**
     * @param path 文件路径  eg path/test.mp4 ===> test;path/test===>test
     *             path==>path;path.mp4=> path
     * @return 文件名，去除文件名后缀
     */
    public static String getFileName(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        int startIndex = path.lastIndexOf("/");
        if (startIndex <= -1) {
            startIndex = -1;
        }
        String filename = path.substring(startIndex + 1, path.length());
        int lastIndex = filename.lastIndexOf(".");
        if (lastIndex <= -1) {
            return filename;
        }
        return filename.substring(0, lastIndex);
    }


    public static void makeSureFileDirExist(String downloadPath) {
        if (!TextUtils.isEmpty(downloadPath)) {
            File file = new File(downloadPath);
            file.mkdirs();
        }
    }

    public static void makeSureFileExist(File file) {
        if (!file.exists()) {
            try {
                File parentDirectory = new File(file.getParent());
                if (!parentDirectory.exists()) {
                    parentDirectory.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {

            }
        }
    }

    /**
     * 解压缩文件到指定的目录.
     *
     * @param unZipfileName 需要解压缩的文件
     * @param mDestPath     解压缩后存放的路径
     */
    public static void unZip(String unZipfileName, String mDestPath) {
        if (!mDestPath.endsWith("/")) {
            mDestPath = mDestPath + "/";
        }
        FileOutputStream fileOut = null;
        ZipInputStream zipIn = null;
        ZipEntry zipEntry = null;
        File file = null;
        int readedBytes = 0;
        byte buf[] = new byte[4096];
        try {
            zipIn = new ZipInputStream(new BufferedInputStream(new FileInputStream(unZipfileName)));
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                file = new File(mDestPath + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    // 如果指定文件的目录不存在,则创建之.
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    while ((readedBytes = zipIn.read(buf)) > 0) {
                        fileOut.write(buf, 0, readedBytes);
                    }
                    fileOut.close();
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();

        } finally {

            if (null != zipIn) {
                try {
                    zipIn.closeEntry();
                } catch (IOException e) {

                }
                try {
                    zipIn.close();
                } catch (IOException e) {

                }
            }
        }
    }

    public static Uri getProfileSavaPath(Context ctx) {
        Uri profileSaveUri;

        if (null == ctx) {
            return null;
        }

        if (FileUtils.ExistSDCard()) {
            FileUtils.makeSureFileDirExist(Environment.getExternalStorageDirectory() + PHOTO_PATH);
            profileSaveUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + PHOTO_PATH, AVATAR_NAME));
        } else {
            FileUtils.makeSureFileDirExist(ctx.getCacheDir() + PHOTO_PATH);
            profileSaveUri = Uri.fromFile(new File(ctx.getCacheDir(), AVATAR_NAME));
        }

        if (null == profileSaveUri) {
            Toast.makeText(ctx, "sdcard not exit", Toast.LENGTH_SHORT).show();
        }
        return profileSaveUri;
    }

}
