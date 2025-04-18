package org.lsposed.lspd.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lsposed.lspd.service.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    public static String toJson(List<ConfigManager.ModuleScope> moduleScopeList) {
        try {
            JSONArray moduleScope = new JSONArray();
            for (ConfigManager.ModuleScope module : moduleScopeList) {

                JSONObject jsonModule = new JSONObject();
                jsonModule.put("moduleId", module.moduleId);
                jsonModule.put("apkPath", module.apkPath);

                JSONArray jsonItemArray = new JSONArray();

                for (ConfigManager.ModuleScopeItem item : module.scopes) {
                    JSONObject jsonItem = new JSONObject();
                    jsonItem.put("packageName", item.packageName);

                    JSONArray jsonProcess = new JSONArray();
                    for (String process : item.process) {
                        jsonProcess.put(process);
                    }

                    jsonItem.put("process", jsonProcess);
                    jsonItemArray.put(jsonItem);
                }

                jsonModule.put("scopes", jsonItemArray);

                moduleScope.put(jsonModule);
            }
            return moduleScope.toString();
        } catch (Exception e) {
            Log.i("LSPoseed", "写入 json 错误：" + e.getMessage());
        }
        return "";
    }

    public static List<ConfigManager.ModuleScope> parse(String json) {
        List<ConfigManager.ModuleScope> moduleScopeList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonModule = jsonArray.getJSONObject(i);

                    ConfigManager.ModuleScope moduleScope = new ConfigManager.ModuleScope();
                    moduleScope.moduleId = jsonModule.getString("moduleId");
                    moduleScope.apkPath = jsonModule.getString("apkPath");

                    JSONArray jsonScopes = jsonModule.getJSONArray("scopes");
                    List<ConfigManager.ModuleScopeItem> scopes = new ArrayList<>();

                    for (int j = 0; j < jsonScopes.length(); j++) {
                        JSONObject jsonItem = jsonScopes.getJSONObject(j);

                        ConfigManager.ModuleScopeItem moduleScopeItem = new ConfigManager.ModuleScopeItem();
                        moduleScopeItem.packageName = jsonItem.getString("packageName");

                        JSONArray jsonProcess = jsonItem.getJSONArray("process");
                        List<String> processList = new ArrayList<>();
                        for (int k = 0; k < jsonProcess.length(); k++) {
                            processList.add(jsonProcess.getString(k));
                        }
                        moduleScopeItem.process = processList;

                        scopes.add(moduleScopeItem);
                    }

                    moduleScope.scopes = scopes;

                    moduleScopeList.add(moduleScope);
                }
            }
        } catch (Exception e) {
            Log.i("LSPoseed", "读取 json 错误：" + e.getMessage());
        }
        return moduleScopeList;
    }


}
