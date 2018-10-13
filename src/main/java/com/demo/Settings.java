package com.demo;

import java.io.File;

/**
 * 配置
 */
class Settings {
    private File mOutputDir;

    /**
     * 构造函数
     */
    private Settings() {
        mOutputDir = new File(".");
    }

    /**
     * 获取输出文件
     */
    public File getOutputFile(String fileName) {
        return new File(mOutputDir, fileName);
    }

    /**
     * 设置输出路径
     */
    private void setOutputDir(String path) {
        mOutputDir = new File(path);
        if (!mOutputDir.exists()) {
            /**
             * 路径不存在，创建
             */
            mOutputDir.mkdirs();
        }
    }

    /**
     * 创建配置
     */
    public static Settings create(String[] args) {
        Settings settings = new Settings();

        for (String arg : args) {
            if (arg.startsWith("--output=")) {
                settings.setOutputDir(arg.substring(9));
            }
            else {
                /**
                 * ignore
                 */
            }
        }

        return settings;
    }
}
