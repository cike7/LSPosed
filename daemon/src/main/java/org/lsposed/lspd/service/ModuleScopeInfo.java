package org.lsposed.lspd.service;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

/**
 * 从apk中获取模块作用范围
 */
public final class ModuleScopeInfo {

    private ApplicationInfo app;
    private List<String> scopeList;
    private Bundle metaData;

    public ModuleScopeInfo(String apkPath) {
        var pkg = getPackageInfo(apkPath);
        if (pkg == null) return;
        app = pkg.applicationInfo;
        if (app == null) return;
        metaData = app.metaData;

        try {
            ZipFile modernModuleApk = new ZipFile(apkPath);
            var scopeEntry = modernModuleApk.getEntry("META-INF/xposed/scope.list");
            if (scopeEntry != null) {
                var reader = new BufferedReader(new InputStreamReader(modernModuleApk.getInputStream(scopeEntry)));
                scopeList = reader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            Log.e("LSPoseed", "解析 lsposed 模块出错：" + e.getMessage());
        }
    }

    private PackageInfo getPackageInfo(String apkPath) {
        if (ConfigManager.getInstance().packageManager == null){
            return null;
        }
        var pkg = ConfigManager.getInstance().packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
        if (pkg != null && pkg.applicationInfo != null) {
            pkg.applicationInfo.sourceDir = apkPath;
            pkg.applicationInfo.publicSourceDir = apkPath;
        }
        return pkg;
    }

    /**
     * 根据 APK 文件路径创建 Resources 对象
     *
     * @param apkPath APK 文件的完整路径
     */
    private Resources getResources(String apkPath) {
        try {
            // 创建新的 AssetManager
            AssetManager assetManager = AssetManager.class.newInstance();
            // 通过反射调用 addAssetPath 方法
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, apkPath);
            // 使用新的 AssetManager 创建 Resources
            return new Resources(assetManager, null, null);
        } catch (Exception e) {
            Log.e("LSPoseed", "获取 Resources 出错 ：" + e.getMessage());
            return null;
        }
    }

    public List<String> getScopeList() {
        if (scopeList != null || metaData == null) return scopeList;
        List<String> list = null;
        int scopeListResourceId = metaData.getInt("xposedscope");
        if (scopeListResourceId != 0) {
            Resources resources = getResources(app.sourceDir);
            if (resources != null) {
                String[] stringArray = resources.getStringArray(scopeListResourceId);
                if (stringArray.length > 0) {
                    list = Arrays.asList(stringArray);
                }
            }
        } else {
            String scopeListString = metaData.getString("xposedscope");
            if (scopeListString != null && !scopeListString.isEmpty()) {
                if (scopeListString.contains(";")) {
                    list = Arrays.asList(scopeListString.split(";"));
                }
            }
        }
        if (list != null) {
            //For historical reasons, legacy modules use the opposite name.
            //https://github.com/rovo89/XposedBridge/commit/6b49688c929a7768f3113b4c65b429c7a7032afa
            list.replaceAll(s ->
                    switch (s) {
                        case "android" -> "system";
                        case "system" -> "android";
                        default -> s;
                    }
            );
            scopeList = list;
        }
        return scopeList;
    }

    public ApplicationInfo getApplicationInfo() {
        return app;
    }

}
