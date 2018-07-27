package com.alcatraz.admin.project_alcatraz.Data;

import android.content.Context;

import com.alcatraz.admin.project_alcatraz.MyObjectBox;
import com.alcatraz.admin.project_alcatraz.Session.MenuGroup;
import com.alcatraz.admin.project_alcatraz.Session.MenuItem;
import com.alcatraz.admin.project_alcatraz.Social.Chat;
import com.alcatraz.admin.project_alcatraz.Social.Message;
import com.alcatraz.admin.project_alcatraz.User.User;

import javax.inject.Singleton;

import io.objectbox.Box;
import io.objectbox.BoxStore;

@Singleton
public class AppDatabase {
    private static BoxStore mBoxStore;

    public static BoxStore getBoxStore(final Context context) {
        if (mBoxStore == null) {
            synchronized (AppDatabase.class) {
                if (mBoxStore == null) {
                    mBoxStore = MyObjectBox.builder().androidContext(context).buildDefault();
                }
            }
        }
        return mBoxStore;
    }

    public static Box<MenuItem> getMenuItemModel(final Context context) {
        return getBoxStore(context).boxFor(MenuItem.class);
    }

    public static Box<MenuGroup> getMenuGroupModel(final Context context) {
        return getBoxStore(context).boxFor(MenuGroup.class);
    }

    public static Box<User> getUserModel(final Context context) {
        return getBoxStore(context).boxFor(User.class);
    }

    public static Box<Message> getMessageModel(final Context context) {
        return getBoxStore(context).boxFor(Message.class);
    }

    public static Box<Chat> getChatModel(final Context context) {
        return getBoxStore(context).boxFor(Chat.class);
    }
}