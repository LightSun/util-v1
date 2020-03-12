package com.heaven7.core.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class PackageUtil {

    /**
     * activate install apk
     * @param context the context
     * @param file the file
     * @param uri the uri
     * @since 1.1.7
     */
    public static void activateInstallApk(Context context, File file, Uri uri) {
        setPermission(file.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) {
           // Uri apkUri = FileProvider.getUriForFile(context, "com.lxtwsw.weather.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private static void setPermission(String absolutePath) {
        String command = "chmod " + "777" + " " + absolutePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Deprecated
    public static void activateInstallApk(Context context, File file) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");

        context.startActivity(intent);
    }
    @Deprecated
    public static void activateInstallApk(Context context, String uri) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.parse(uri),
                "application/vnd.android.package-archive");

        context.startActivity(intent);
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            @SuppressWarnings("unused")
            PackageInfo packinfo = context.getPackageManager().getPackageInfo(
                    packageName, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static void openApp(Context context, String packageName) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        List<ResolveInfo> apps = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String className = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(ri.activityInfo.packageName,
                    className);

            intent.setComponent(cn);
            context.startActivity(intent);
        } else {
            Logger.w(packageName, "not installed...");
        }
    }

    //open web url , download also can use this too.
    public static void openWebURL(Context context, String inURL) {
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/*it.setClassName("com.android.browser",
				"com.android.browser.BrowserActivity");*/
        context.startActivity(it);
    }

    public static String getPackageName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            // ApplicationInfo appInfo = info.applicationInfo;
            //String appName = pm.getApplicationLabel(appInfo).toString();  
            // String packageName = appInfo.packageName;  //得到安装包名称
            return info.applicationInfo.packageName;
        }
        return null;
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (NameNotFoundException e) {
            return -1;
        }
    }
}
