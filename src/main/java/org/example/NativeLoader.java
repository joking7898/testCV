package org.example;

import java.io.*;

public class NativeLoader {

    public void loadLibrary(String library) {
        try {
            System.load(saveLibrary(library));

        } catch (IOException e) {
            System.out.println("Could not find library " + library +
                    " as resource, trying fallback lookup through System.loadLibrary");
            System.loadLibrary(library);
        }
    }

    private String saveLibrary(String library) throws IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            String libraryName = getOSSpecificLibraryName(library, true);
            in = this.getClass().getClassLoader().getResourceAsStream("lib/" + libraryName);
            String tmpDirName = System.getProperty("java.io.tmpdir");
            File tmpDir = new File(tmpDirName);
            if (!tmpDir.exists()) {
                tmpDir.mkdir();
            }
            File file = File.createTempFile(library + "-", ".tmp", tmpDir);

            file.deleteOnExit();
            out = new FileOutputStream(file);

            int cnt;
            byte buf[] = new byte[16 * 1024];

            while ((cnt = in.read(buf)) >= 1) {
                out.write(buf, 0, cnt);
            }
            System.out.println("Saved libfile: " + file.getAbsoluteFile());
            return file.getAbsolutePath();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private String getOSSpecificLibraryName(String library, boolean includePath) {
        String osArch = System.getProperty("os.arch");
        String osName = System.getProperty("os.name").toLowerCase();
        String name;
        String path;

        if (osName.startsWith("win")) {
            if (osArch.equalsIgnoreCase("amd64")) {
                name = library + ".dll";
                path = "win-x64/";
            } else {
                throw new UnsupportedOperationException("Platform " + osName + ":" + osArch + " not supported");
            }
        } else if (osName.startsWith("linux")) {
            if (osArch.equalsIgnoreCase("amd64")) {
                name = library + ".so";
                path = "linux-amd64/";
            } else {
                throw new UnsupportedOperationException("Platform " + osName + ":" + osArch + " not supported");
            }
        } else if (osName.startsWith("mac")) {
                name = library +".dylib";
                path = "osx-x86_64";
        } else {
            throw new UnsupportedOperationException("Platform " + osName + ":" + osArch + " not supported");
        }

        return includePath ? path +"/" + name : name;
    }
}
