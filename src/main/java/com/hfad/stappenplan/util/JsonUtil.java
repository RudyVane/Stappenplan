package com.hfad.stappenplan.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtil {
    public static ArrayList<String> jsonToStringArraylist(String json){
        ArrayList<String> stringArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArrayStap = jsonObject.getJSONArray("stap");
            for (int i = 0; i < jsonArrayStap.length(); i++) {
                JSONObject jsonObjectStap = jsonArrayStap.getJSONObject(i);
                stringArrayList.add(jsonObjectStap.getString("text"));
            }

        }catch(JSONException ex){
            ex.printStackTrace();
        }
        return stringArrayList;
    }
}
