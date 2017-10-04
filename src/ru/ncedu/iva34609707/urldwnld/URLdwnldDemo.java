/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ncedu.iva34609707.urldwnld;

/**
 * This class was created only for demonstration of working
 * ru.ncedu.iva34609707.urldwnld.URLDownliaderImpl. It's assumpted to use the
 * arguments of command line: first - for the URL, it's always required; second
 * - for the path to which the file of some URL will be saved; third - just
 * marked that the file will be open after loading.
 *
 * @author iva34609707
 */
public class URLdwnldDemo {

    public static void main(String[] args) {
        URLDownloader urlDwn;

        if (args.length > 0) {
            if (args.length == 2) {
                urlDwn = new URLDownloaderImpl(args[1]);
            } else if (args.length >= 3) {
                urlDwn = new URLDownloaderImpl(args[1], true);
            } else {
                urlDwn = new URLDownloaderImpl();
            }

            System.out.println("File was saved to: " + urlDwn.saveFromURL(args[0]));
        } else {
            System.out.println("Arguments of the command line aren't given, "
                    + "so the URL will be default.");
            
            urlDwn = new URLDownloaderImpl();
            System.out.println("File was saved to: " 
                    + urlDwn.saveFromURL("http://tvoyavorkuta.ru/index.php?newsid=1336"));
        }
    }
}
