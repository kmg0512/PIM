package com.example.utility.jsonizer;

import org.json.JSONObject;

/**
 * Created by Hoon on 5/22/2016.
 */
public interface JSONAble {
    JSONObject toJSON();
    boolean fromJSON(JSONObject json);
}
