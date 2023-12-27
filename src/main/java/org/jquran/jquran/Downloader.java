package org.jquran.jquran;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Downloader implements Runnable {
    private final String url;
    private final String toPath;

    public Downloader(String url, String toPath) {
        this.url = url;
        this.toPath = toPath;
    }

    @Override
    public void run() {
        // surround with try-catch if downloadFile() throws something
        downloadFile(url, toPath);
    }

    public void downloadFile(String url, String toPath) {
        try {
            File file = new File(toPath);
            if (file.isFile()) {
                return;
            }
            file.getParentFile().mkdirs();
            file.createNewFile();
            URL downloadUrl = URI.create(url).toURL();
            ReadableByteChannel readableByteChannel = Channels.newChannel(downloadUrl.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(toPath, false);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
