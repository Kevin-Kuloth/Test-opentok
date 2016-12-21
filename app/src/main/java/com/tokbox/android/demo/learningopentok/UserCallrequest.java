package com.tokbox.android.demo.learningopentok;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rajaganapathi on 12/1/2016.
 */

public class UserCallrequest {

    private List<String> users = new ArrayList<String>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The users
     */
    public List<String> getUsers() {
        return users;
    }

    /**
     *
     * @param users
     * The users
     */
    public void setUsers(List<String> users) {
        this.users = users;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
