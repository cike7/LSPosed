package org.lsposed.lspd.service;

import org.lsposed.lspd.models.Module;

interface ILSPApplicationService {
//    boolean isLogMuted();

    List<Module> getLegacyModulesList();

    List<Module> getModulesList();

//    String getPrefsPath(String packageName);

//    ParcelFileDescriptor requestInjectedManagerBinder(out List<IBinder> binder);
}
