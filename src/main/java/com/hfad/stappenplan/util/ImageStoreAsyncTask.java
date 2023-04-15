package com.hfad.stappenplan.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImageStoreAsyncTask extends AsyncTask<ArrayList<String>,Void,ArrayList<String>> {
    private AsyncTaskListener delegate;
    public ImageStoreAsyncTask(AsyncTaskListener delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<String> doInBackground(ArrayList<String>... strings) {
        ArrayList<String> surlArrayList = strings[0];
        ArrayList<String> pathArraylist = new ArrayList<>();
        for (String surl:surlArrayList) {
            pathArraylist.add(SaveImage(getBmpFromURL(surl),surl));
        }
        return pathArraylist;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        delegate.imgPath(strings);
    }

    public Bitmap getBmpFromURL(String surl){
        try {
            URL url = new URL(surl);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.connect();
            InputStream in = urlcon.getInputStream();
            Bitmap mIcon = BitmapFactory.decodeStream(in);
            return  mIcon;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String SaveImage(Bitmap finalBitmap, String surl) {
        String name = surl.substring(surl.lastIndexOf("/") + 1).toLowerCase();

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        File file = new File(myDir, name);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getPath();
    }
}
