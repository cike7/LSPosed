/*
 * This file is part of LSPosed.
 *
 * LSPosed is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LSPosed is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LSPosed.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2022 LSPosed Contributors
 */

package org.lsposed.lspd.core;

import android.os.IBinder;
import android.os.Process;

import org.lsposed.lspd.service.ILSPApplicationService;
import org.lsposed.lspd.util.Utils;

public class Main {

    public static void forkCommon(boolean isSystem, String niceName, String appDir, IBinder binder) {
        // 移除 manager app
//        if (isSystem) {
//            // 将 lsp 模块管理器附加到 shell 程序
//            ParasiticManagerSystemHooker.start();
//        }

        Startup.initXposed(isSystem, niceName, appDir, ILSPApplicationService.Stub.asInterface(binder));

//        try {
//            Utils.Log.muted = serviceClient.isLogMuted();
//        } catch (Throwable t) {
//            Utils.logE("failed to configure logs", t);
//        }
        // 移除 manager app
//        if (niceName.equals(BuildConfig.DEFAULT_MANAGER_PACKAGE_NAME) && ParasiticManagerHooker.start()) {
//            Utils.logI("Loaded manager, skipping next steps");
//            return;
//        }

        Utils.logI("Loading xposed for " + niceName + "/" + Process.myUid());
        // 启动 Xposed 框架
        Startup.bootstrapXposed();
    }
}
