package com.langoor.app.blueshak;

/**
 * Created by iiro on 7.6.2016.
 */

import com.divrt.co.R;
public class TabMessage {
    public static String get(int menuItemId, boolean isReselection) {
        String message = "Content for ";

        switch (menuItemId) {
            case R.id.home:
                message += "Home";
                break;
            case R.id.search:
                message += "search";
                break;
            case R.id.list:
                message += "CreateSaleActivity";
                break;
            case R.id.message:
                message += "Messages";
                break;
            case R.id.profile:
                message += "Profile";
                break;
        }

        if (isReselection) {
            message += " WAS RESELECTED! YAY!";
        }

        return message;
    }
}
