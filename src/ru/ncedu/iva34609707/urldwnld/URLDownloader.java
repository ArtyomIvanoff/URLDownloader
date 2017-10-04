/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ncedu.iva34609707.urldwnld;

import java.io.File;

/**
 * The interface establishes main aspects of task about saving the resource of given URL.
 * @author iva34609707
 */
public interface URLDownloader {
    /**
     * The method saves the contents of resource which URL is given.
     * Saved file may be opened. 
     * @param urlStr String representation of URL of some resource
     * @return path to this saved file
     * @throws IllegalArgumentException if url isn't valid
     */
    public String saveFromURL(String urlStr) throws IllegalArgumentException;
    
    /**
     * The method tries to save file on this path.
     * If path hasn't existed yet, it will be created.
     * If path exists and there is a file with such name, it will ask to replace
     * by new file or not. If choice is not to replace, then it offers to 
     * establish new name and this name will be checked.
     * @param pathStr String representation of the path to the file
     * @param nameFile Name of saving file
     * @return approved full path to this file
     * @throws IllegalArgumentException if one of the arguments is null
     */
    public String saveFileTo(String pathStr, String nameFile) throws IllegalArgumentException; 
    
    /**
     * The method opens the file
     * @param file the file which is needed to open
     * @throws IllegalArgumentException if the argument is null
     * @throws IllegalStateException if something goes wrong
     */
    void showFile(File file) throws IllegalArgumentException,IllegalStateException;
}
