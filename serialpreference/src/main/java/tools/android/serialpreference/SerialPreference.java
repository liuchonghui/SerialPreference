package tools.android.serialpreference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

public class SerialPreference {

    @SuppressLint("WrongConstant")
    private static SharedPreferences getSharedPreference(Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("serializtable",
                Build.VERSION.SDK_INT < 11 ? Context.MODE_PRIVATE : Context.MODE_PRIVATE | 0x0004);
        return sp;
    }

    public synchronized boolean write(Context context, String key, Hashtable<String, ? extends Serializable> table) {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(table);
            objectOutputStream.flush();

            byte[] data = byteArrayOutputStream.toByteArray();
            String list = Base64.encodeToString(data, Base64.DEFAULT);

            getSharedPreference(context).edit().putString(key, list).commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public synchronized Hashtable<String, ? extends Serializable> read(Context context, String key) {
        String list = getSharedPreference(context).getString(key, "");
        Serializable object = null;
        if (!TextUtils.isEmpty(list)) {
            ByteArrayInputStream byteArrayInputStream = null;
            ObjectInputStream objectInputStream = null;
            try {
                byteArrayInputStream = new ByteArrayInputStream(Base64.decode(list, Base64.DEFAULT));
                objectInputStream = new ObjectInputStream(byteArrayInputStream);
                object = (Serializable) objectInputStream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (byteArrayInputStream != null) {
                    try {
                        byteArrayInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return (Hashtable<String, ? extends Serializable>) object;
    }
}
