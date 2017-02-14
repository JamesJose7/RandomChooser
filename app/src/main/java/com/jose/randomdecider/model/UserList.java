package com.jose.randomdecider.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 2/5/2017.
 */

public class UserList {

    private String mListName;
    private List<String> mList;

    public UserList() {
        mList = new ArrayList<>();
        mListName = "default";
    }

    public UserList(String listName, List<String> list) {
        mList = list;
        mListName = listName;
    }

    public String getListName() {
        return mListName;
    }

    public List<String> getList() {
        return mList;
    }

    public void addItem(String item) {
        mList.add(item);
    }

    public void resetList() {
        mList.removeAll(mList);
    }
}
