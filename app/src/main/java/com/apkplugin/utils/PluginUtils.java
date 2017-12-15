package com.apkplugin.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.apkplugin.R;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by Administrator on 2017-12-15.
 */

public class PluginUtils {

    /**
     * 获取未安装apk的信息
     *
     * @param context
     * @param archiveFilePath apk文件的path
     * @return
     */
    private String[] getUninstallApkInfo(Context context, String archiveFilePath) {
        String[] info = new String[2];
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            String versionName = pkgInfo.versionName;//版本号
            Drawable icon = pm.getApplicationIcon(appInfo);//图标
            String appName = pm.getApplicationLabel(appInfo).toString();//app名称
            String pkgName = appInfo.packageName;//包名
            info[0] = appName;
            info[1] = pkgName;

        }
        return info;
    }

    /**
     * @param apkName
     * @return 得到对应插件的Resource对象
     */
    private Resources getPluginResources(Context context, String apkName) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);//反射调用方法addAssetPath(String path)
            //第二个参数是apk的路径：Environment.getExternalStorageDirectory().getPath()+File.separator+"plugin"+File.separator+"apkplugin.apk"
            addAssetPath.invoke(assetManager, "");//将未安装的Apk文件的添加进AssetManager中，第二个参数为apk文件的路径带apk名
            Resources superRes = context.getResources();
            Resources mResources = new Resources(assetManager, superRes.getDisplayMetrics(),
                    superRes.getConfiguration());
            return mResources;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 加载apk获得内部资源
     *
     * @param apkDir  apk目录
     * @param apkName apk名字,带.apk
     * @throws Exception
     */
    private void dynamicLoadApk(Context context, String apkDir, String apkName, String apkPackageName) throws Exception {
        File optimizedDirectoryFile = context.getDir("dex", Context.MODE_PRIVATE);//在应用安装目录下创建一个名为app_dex文件夹目录,如果已经存在则不创建
        Log.v("zxy", optimizedDirectoryFile.getPath().toString());// /data/data/com.example.dynamicloadapk/app_dex
        //参数：1、包含dex的apk文件或jar文件的路径，2、apk、jar解压缩生成dex存储的目录，3、本地library库目录，一般为null，4、父ClassLoader
        DexClassLoader dexClassLoader = new DexClassLoader(apkDir + File.separator + apkName, optimizedDirectoryFile.getPath(), null, ClassLoader.getSystemClassLoader());
        Class<?> clazz = dexClassLoader.loadClass(apkPackageName + ".R$mipmap");//通过使用apk自己的类加载器，反射出R类中相应的内部类进而获取我们需要的资源id
        Field field = clazz.getDeclaredField("one");//得到名为one的这张图片字段
        int resId = field.getInt(R.id.class);//得到图片id
        Resources mResources = getPluginResources(context, apkName);//得到插件apk中的Resource
        if (mResources != null) {
            //通过插件apk中的Resource得到resId对应的资源
            // findViewById(R.id.background).setBackgroundDrawable(mResources.getDrawable(resId));
        }
    }

}
