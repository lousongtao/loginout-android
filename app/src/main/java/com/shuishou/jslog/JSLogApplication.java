package com.shuishou.jslog;

import android.app.Application;

import com.shuishou.jslog.io.CrashHandler;

import java.io.File;

import pl.brightinventions.slf4android.FileLogHandlerConfiguration;
import pl.brightinventions.slf4android.LoggerConfiguration;

/**
 * Created by Administrator on 2017/10/5.
 */

public class JSLogApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FileLogHandlerConfiguration fileHandler = LoggerConfiguration.fileLogHandler(this);
        File dir = new File(ConstantValue.ERRORLOGPATH);
        if (!dir.exists())
            dir.mkdir();
        fileHandler.setFullFilePathPattern(ConstantValue.ERRORLOGPATH + "/" + ConstantValue.ERRORLOGFILENAME + ".%g.%u.log");

        LoggerConfiguration.configuration().addHandlerToRootLogger(fileHandler);
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }
}
