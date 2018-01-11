package com.my716.ebook;

import com.my716.Settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Txt {
    private FileWriter mWriter;

    public Txt(String name) throws IOException {
        File file = new File(Settings.getInstance().getOutputDirectory() + "/" + name + ".txt");
        if (file.exists()) {
            file.delete();
        }

        mWriter = new FileWriter(file);
    }

    public void writeChapter(String title, String content) throws IOException {
        mWriter.write(title);
        mWriter.write("\r\n");
        mWriter.write(content);
        mWriter.write("\r\n");
    }

    public void close() throws IOException {
        mWriter.close();
    }
}
