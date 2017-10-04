/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ncedu.iva34609707.urldwnld;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * The specific implementation of interface URLDownloader
 *
 * @author iva34609707
 */
public class URLDownloaderImpl implements URLDownloader {

    private String nameOfFile;
    private String pathStr;
    private boolean isToShow;
    private String host;
    private final String RESOURCE_PATTERN = "<(img|link).*?(src|href)=\"(.+?)\".*?>";

    /**
     * The constructor sets path of the saving file and the parameter of opening
     * after saving.
     *
     * @param pathString the path on which the file will be saving
     * @param paramShow the value for the opening parameter See also
     * {@link #saveFromURL(java.lang.String)}
     */
    public URLDownloaderImpl(String pathString, boolean paramShow) {
        pathStr = pathString;
        isToShow = paramShow;
    }

    /**
     * The constructor sets path of the saving file which won't be opened after
     * saving.
     *
     * @param pathString the path on which the file will be saving See also
     * {@link #saveFromURL(java.lang.String)}
     */
    public URLDownloaderImpl(String pathString) {
        this(pathString, false);
    }

    /**
     * The standart constructor which establishes the default path for saved
     * files which won't be opened after loading.
     */
    public URLDownloaderImpl() {
        this("", false);
    }

    /**
     * The method returns the full name of the file which is from the URL See
     * also {@link #saveFromURL(java.lang.String)}
     *
     * @return full name of file, including extension.
     * @throws NullPointerException if name hasn't been set yet.
     */
    public String getNameOfFile() throws NullPointerException {
        if (nameOfFile == null) {
            throw new NullPointerException();
        } else {
            return nameOfFile;
        }
    }

    /**
     * The method returns only part of name of the file which is from the URL
     * See also {@link #saveFromURL(java.lang.String)}
     *
     * @return part name of file, excluding extension.
     * @throws NullPointerException if name hasn't been set yet.
     */
    public String getFirstPartName() throws NullPointerException {
        if (nameOfFile == null) {
            throw new NullPointerException();
        } else {
            return nameOfFile.substring(0, nameOfFile.lastIndexOf("."));
        }
    }

    /**
     * The method returns the path for saving of the file which is from the URL
     * See also {@link #saveFromURL(java.lang.String)}
     *
     * @return path of saving file, excluding its name
     * @throws NullPointerException if path hasn't been set yet.
     */
    public String getPathStr() throws NullPointerException {
        if (pathStr == null) {
            throw new NullPointerException();
        } else {
            return pathStr;
        }
    }

    /**
     * The method gives information about opening parameter of the downloaded
     * file after saving. See also {@link #saveFromURL(java.lang.String)}
     *
     * @return opening parameter
     */
    public boolean isShowFile() {
        return isToShow;
    }

    /**
     * The method sets the new name for the file after calling {@link #setName(java.net.URLConnection)
     * }
     * See also {@link #saveFromURL(java.lang.String)}
     *
     * @throws NullPointerException if argument is null
     */
    private void setNameOfFile(String newName) throws NullPointerException {
        if (newName == null) {
            throw new NullPointerException();
        } else {
            nameOfFile = newName;
        }
    }

    /**
     * The method sets a new value for the opening parameter of the downloaded
     * file after saving.
     *
     * @param showBool the value for the opening parameter See also
     * {@link #saveFromURL(java.lang.String)}
     */
    public void setShowParam(boolean showBool) {
        isToShow = showBool;
    }

    @Override
    public String saveFromURL(String urlStr) throws IllegalArgumentException {
        try {
            URL url = new URL(urlStr);
            URLConnection urlCon = url.openConnection();

            int closShashIndex = urlStr.indexOf("/", urlStr.indexOf("://") + 6);
            if (closShashIndex == -1) {
                closShashIndex = urlStr.length();
            }

            //it may be essential in parsing of HTML later
            host = urlStr.substring(0, closShashIndex);

            //creating name of file
            nameOfFile = setName(urlCon);

            //determine fullpath of saving file
            String fullPath = saveFileTo(pathStr, nameOfFile);
            setNameOfFile(fullPath.substring(fullPath.lastIndexOf("\\") + 1));

            byte[] buf;
            //reading file from gived URL
            if (nameOfFile.endsWith(".html") || nameOfFile.endsWith(".htm")) {
                buf = parseHTML(urlCon);
            } else {
                buf = readToBytes(urlCon);
            }

            writeToFile(buf, fullPath);

            //File could be opened after saving
            if (isToShow) {
                showFile(new File(fullPath));
            }

            return fullPath;

        } catch (MalformedURLException ex) {
            System.out.println("URL exception: " + ex);
            throw new IllegalStateException();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            throw new IllegalStateException();
        } catch (IllegalStateException e) {
            System.out.println("Exception: " + e);
            throw new IllegalStateException();
        }
    }

    @Override
    public String saveFileTo(String pathStr, String nameFile) throws IllegalArgumentException {
        if (pathStr == null || nameFile == null) {
            throw new IllegalArgumentException();
        }

        //case when path contains non-existed folder
        Path pathToFolder = Paths.get(pathStr);
        File fileFolder = pathToFolder.toFile();

        if (pathStr.length() > 1 && !fileFolder.exists()) {
            System.out.println("Path contains non-existed folder(s), so this path will be created.");
            try {
                Files.createDirectories(pathToFolder);
            } catch (IOException ex) {
                System.out.println("Cannot create new folders!");
                throw new IllegalStateException();
            }
        } else {
            //case when path exists
            Path pathFile = Paths.get(pathStr, nameFile);
            File file = pathFile.toFile();
            System.out.println("Trying to safe file as " + pathFile.toString());

            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String ans;

                while (true) {
                    System.out.println("Warning! File with this name is already exists here. Replace it by new? (y|n)");
                    try {
                        ans = reader.readLine();
                    } catch (IOException ex) {
                        throw new IllegalStateException();
                    }

                    //if user decides to replace old file with such name
                    if (ans.startsWith("y")) {
                        break;
                    }

                    if (ans.startsWith("n")) {
                        System.out.println("Change name for this file:");
                        try {
                            String newNameFile = reader.readLine();
                            newNameFile += nameFile.substring(nameFile.lastIndexOf("."), nameFile.length());
                            nameFile = newNameFile;
                        } catch (IOException ex) {
                            throw new IllegalStateException();
                        }

                        //trying to check this name
                        return saveFileTo(pathStr, nameFile);
                    }
                }
            }
        }

        //add slash to the path if it's necessary
        if (pathStr.length() > 1 && !pathStr.endsWith("\\") && !pathStr.endsWith("/")) {
            pathStr += "\\";
        }

        return pathStr + nameFile;
    }

    /**
     * The method establish name: if the URL contains only main domain, so it's
     * "index.html" as default. In other cases tne name is the last part of the
     * URL, before character '?' If it's necessary, add extension
     *
     * @param urlCon the URLConnnection object
     * @return name of file from given URL
     */
    protected String setName(URLConnection urlCon) {
        //if only high-level domain
        String nameFile = "index.html";
        String urlStr = urlCon.toString();

        //if URL contents not only domain, so name will be last part of url, before '?'
        int lastIndexSlash = urlStr.lastIndexOf("/", urlStr.length() - 2);

        if (lastIndexSlash > urlStr.indexOf("://") + 2 && urlStr.indexOf("?") != lastIndexSlash + 1) {
            int end = (urlStr.contains("?") == true ? urlStr.indexOf("?") : urlStr.length());
            nameFile = urlStr.substring(lastIndexSlash + 1, end);

            //remove slashes 
            if (nameFile.contains("/")) {
                nameFile = nameFile.replaceAll("/", "");
            }

            //if extension of file wasn't in URL
            String extens = urlCon.getContentType();

            if (extens != null) {
                int beginExt = (extens.contains("+") ? extens.indexOf("+") + 1 : extens.indexOf("/") + 1);
                int endExt = (extens.contains(";") ? extens.indexOf(";") : extens.length());
                extens = extens.substring(beginExt, endExt);
            } else {
                //assumption
                extens = "html";
            }

            if (!nameFile.endsWith("." + extens)) {
                nameFile += ("." + extens);
            }
        }

        return nameFile;
    }

    @Override
    public void showFile(File file) throws IllegalArgumentException, IllegalStateException {
        if (file == null) {
            System.out.println("showFile: Argument is null!");
            throw new IllegalArgumentException();
        } else {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(file);
            } catch (IOException ex) {
                System.out.println("Error while opening file!");
                throw new IllegalStateException();
            }
        }
    }

    /**
     * The method reads and saves elements of the HTML document from given URL
     * Firstly, the HTML document are read to the String, using the necessary
     * charset(default - "utf-8"). All files are saved to the folder with name
     * "{@link #nameOfFile}_files" If an exception occurs in the proccess of
     * reading from the InputStream, the method continue to work with next URLs
     * from the HTML document. Note: the URLs in the document will be replaced
     * by local paths and the {@link #HashSet} is needed to prevent a loading of
     * same files. It's worth to say that due to
     * {@link #setName(java.net.URLConnection)} some files may get same
     * names(i.e. "index.html"), so you need to decide: replace the old one or
     * give another name for new.
     *
     * @param urlCon the URLConnection object which is associated with some HTML
     * file.
     * @return bytes representing of the HTML page
     * @throws IllegalArgumentException if the argument is null or the type
     * isn't html
     * @throws IllegalStateException if the error occurs in the proccess of
     * reading
     */
    private byte[] parseHTML(URLConnection urlCon)
            throws IllegalArgumentException, IllegalStateException {
        if (urlCon == null) {
            System.out.println("Argument is null.");
            throw new IllegalArgumentException();
        }

        String contentType = urlCon.getContentType();
        if (!contentType.contains("html")) {
            System.out.println("It isn't HTML!");
            throw new IllegalArgumentException();
        }

        String charset = contentType.substring(contentType.indexOf("=") + 1);
        if (charset == null || !contentType.contains("charset")) {
            charset = "utf-8";
        }

        String str = new String();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), charset))) {
            String tmp;
            while ((tmp = br.readLine()) != null) {
                str += tmp;
            }
            tmp = null;
        } catch (IOException ex) {
            System.out.println("IOException while reading HTML!");
            throw new IllegalStateException(ex);
        }

        String nameOfFolder = this.getFirstPartName() + "_files/";
        String pathToFolder = "";

        //try to create a folder for elements of HTML document
        try {
            Path pathFld = Paths.get(pathStr, nameOfFolder);
            File fileOfFld = pathFld.toFile();

            if (!fileOfFld.exists()) {
                pathToFolder = Files.createDirectory(pathFld).toString();
            } else {
                pathToFolder = pathFld.toString();
            }
        } catch (IOException ex) {
            System.out.println("Error when creating folder!");
            throw new IllegalStateException(ex);
        }

        HashSet<String> urlSet = new HashSet<>();
        //try to save elements
        Pattern rsrcPat = Pattern.compile(RESOURCE_PATTERN);
        Matcher matcher = rsrcPat.matcher(str);

        while (matcher.find()) {
            try {
                String urlRes = matcher.group(3);

                //skip if url has already been
                if (urlSet.contains(urlRes)) {
                    continue;
                }

                URLConnection urlConRes;
                String origUrlRes = urlRes;

                //if URL without protocol, add http
                if (urlRes.startsWith("//")) {
                    urlRes = ("http:" + urlRes);
                } //if URL is local, so add the host part
                else if (urlRes.startsWith("/")) {
                    urlRes = host + urlRes;
                }

                //allowed only common protocols!
                if (urlRes.contains("tp://") || urlRes.contains("tps://")) {
                    try {
                        urlConRes = new URL(urlRes).openConnection();
                    } catch (MalformedURLException ex) {
                        System.out.println("MalformedURLException! Let's proceed...");
                        continue;
                    } catch (IOException ex) {
                        System.out.println("IOException while reading resource! "
                                + "Let's proceed...");
                        continue;
                    }

                    byte[] buf;

                    try {
                        buf = readToBytes(urlConRes);
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        System.out.println("Some exception occurs while writing "
                                + "to the file, let's proceed...");
                        continue;
                    }

                    String nameOfRes = setName(urlConRes);

                    String fullPathRes = saveFileTo(pathToFolder, nameOfRes);
                    nameOfRes = fullPathRes.substring(pathToFolder.length() + 1);
                    try {
                        writeToFile(buf, fullPathRes);
                    } catch (IllegalStateException | IllegalArgumentException e) {
                        System.out.println("Error while writing the file, proceed...");
                        continue;
                    }

                    /*replace the URL by the local path and add this URL to prevent
                     loading of the same file*/
                    str = str.replace(origUrlRes, nameOfFolder + nameOfRes);
                    urlSet.add(origUrlRes);
                }
            } catch (Exception e) {
                System.out.println("Error of some resource, proceed...");
            }
        }

        return str.getBytes();
    }

    /**
     * The method reads from InputStream of given URL to the byte array
     *
     * @param urlCon represrnation of URL, from which is read the resource
     * @return byte array which contains the resource from URL
     * @throws IllegalArgumentException if the argument is null
     * @throws IllegalStateException if something get wrong
     */
    private byte[] readToBytes(URLConnection urlCon)
            throws IllegalArgumentException, IllegalStateException {
        if (urlCon == null) {
            throw new IllegalArgumentException();
        }

        byte[] buf;
        int sizeContent = urlCon.getContentLength();

        if (sizeContent != -1) {
            buf = new byte[sizeContent];
        } else {
            buf = new byte[300000000];
        }

        try (InputStream isr = urlCon.getInputStream()) {
            byte[] temp = new byte[2048];
            int n;
            int begin = 0;

            while ((n = isr.read(temp)) != -1) {
                System.arraycopy(temp, 0, buf, begin, n);
                begin += n;
            }

            if (sizeContent == -1) {
                temp = new byte[begin];
                System.arraycopy(buf, 0, temp, 0, begin);
                buf = temp;
            }
        } catch (IOException ex) {
            System.out.println("Exception while reading from InputStream!");
            throw new IllegalStateException(ex);
        }

        return buf;
    }

    /**
     * The method write the byte array to the file with given path as secondary
     * argument.
     *
     * @param buf the byte array which contains something
     * @param fullPath the full path, including also name of saving file
     * @throws IllegalArgumentException if one of the arguments is null
     * @throws IllegalStateException if the error occurs in process of writing
     */
    private void writeToFile(byte[] buf, String fullPath)
            throws IllegalArgumentException, IllegalStateException {
        if (buf == null || fullPath == null) {
            throw new IllegalArgumentException();
        }

        try (FileOutputStream fos = new FileOutputStream(fullPath)) {
            fos.write(buf);
        } catch (IOException e) {
            System.out.println("Exception while writing to file:" + e);
            throw new IllegalStateException();
        }
    }

}
