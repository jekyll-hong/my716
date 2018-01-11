package com.my716;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

public class Settings {
    private static Settings sInstance = null;

    public static Settings getInstance() {
        if (sInstance == null) {
            sInstance = new Settings();
        }

        return sInstance;
    }

    private String mOutputDirectory = "";

    private boolean mEnableProxy = false;
    private String mProxyIp;
    private int mProxyPort;

    private Settings() {
        //nothing
    }

    public void setOutputDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            /**
             * 不存在，尝试自动创建
             */
            if (!dir.mkdirs()) {
                /**
                 * 创建文件夹失败
                 */
                throw new IllegalStateException("make output directory fail");
            }
        }
        else {
            if (!dir.isDirectory()) {
                /**
                 * 不是文件夹
                 */
                throw new IllegalArgumentException("output is not a directory");
            }
        }

        if (!dir.canWrite()) {
            /**
             * 没有写权限
             */
            throw new IllegalStateException("output directory must be writable");
        }

        try {
            mOutputDirectory = dir.getCanonicalPath();
        }
        catch (IOException e) {
            //ignore
        }
    }

    public String getOutputDirectory() {
        return mOutputDirectory;
    }

    public void setProxy(String proxy) {
        int colonPos = proxy.indexOf(":");
        if (colonPos < 0) {
            throw new IllegalArgumentException("proxy must be ip:port");
        }

        mProxyIp = proxy.substring(0, colonPos);
        mProxyPort = Integer.parseInt(proxy.substring(colonPos + 1));

        mEnableProxy = true;
    }

    public Proxy getProxy() {
        Proxy proxy;

        if (mEnableProxy) {
            SocketAddress address = new InetSocketAddress(mProxyIp, mProxyPort);
            proxy = new Proxy(Proxy.Type.HTTP, address);
        }
        else {
            proxy = Proxy.NO_PROXY;
        }

        return proxy;
    }
}
