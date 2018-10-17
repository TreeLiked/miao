package com.example.lqs2.courseapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 网上找来的share持久化工具类
 *
 * @author lqs2
 */
public class SharedPreferenceUtil {

    private static final String FILE_NAME = "share_data";


    /**
     * 保存数据的方法根据类型调用不同的保存方法
     *
     * @param context 上下文
     * @param key     key
     * @param object  value
     */
    public static void put(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法, 调用相对于的方法获取值
     *
     * @param context       上下文
     * @param key           key
     * @param defaultObject 如果不存在此key对应的value，返回的默认值
     * @return value
     */
    public static Object get(Context context, String key, Object defaultObject) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param context 上下文
     * @param key     key
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     *
     * @param context 上下文
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        SharedPreferenceUtil.put(context, "hasGetCourse", false);
        SharedPreferenceUtil.put(context, "hasLoginJW", false);
        SharedPreferenceUtil.put(context, "remember_password_jw", false);
        SharedPreferenceUtil.put(context, "remember_password_jw", false);
        SharedPreferenceUtil.put(context, "courseSourceCode", "");
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context 上下文
     * @param key     key
     * @return 存在/不存在
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context 上下文
     * @return 所有键值对
     */
    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 保存图片到SharedPreferences
     *
     * @param mContext 上下文
     * @param bitmap   图像
     */
    public static void putImage(Context mContext, String key, Bitmap bitmap) {
//        /将Bitmap压缩成字节数组输出流
        ByteArrayOutputStream byStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byStream);
//        利用Base64将字节数组输出流转换成String
        byte[] byteArray = byStream.toByteArray();
        String imgString = Base64.encodeToString(byteArray, Base64.DEFAULT);
//        将String保存shareUtils
        SharedPreferenceUtil.put(mContext, key, imgString);
    }

    /**
     * 从SharedPreferences读取图片
     *
     * @param mContext 上下文
     * @param key      key
     * @return 图像
     */
    public static Bitmap getImage(Context mContext, String key) {
        String imgString = (String) SharedPreferenceUtil.get(mContext, key, "");
        if (!"".equals(imgString)) {
            // 利用Base64将string转换
            byte[] byteArray = Base64.decode(imgString, Base64.DEFAULT);
            ByteArrayInputStream byStream = new ByteArrayInputStream(byteArray);
            // 生成bitmap
            return BitmapFactory.decodeStream(byStream);
        }
        return null;
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private static final Method S_APPLY_METHOD = findApplyMethod();

        /**
         * 反射查找apply方法
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException ignored) {
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        static void apply(SharedPreferences.Editor editor) {
            try {
                if (S_APPLY_METHOD != null) {
                    S_APPLY_METHOD.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException ignored) {
            } catch (IllegalAccessException ignored) {
            } catch (InvocationTargetException ignored) {
            }
            editor.commit();
        }
    }
}
