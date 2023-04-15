package com.hfad.stappenplan.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Marc on 26-3-2018.
 */


public class UrlBuilder {

    private static final String URL_STRING = "http://stappenplan.mediade.eu/Pictogram.php";

    public static URL buildUrl(){
        try{
            return new URL(URL_STRING);
        }catch (MalformedURLException ex){
            ex.printStackTrace();
            return null;
        }
    }
}
