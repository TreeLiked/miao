package com.example.lqs2.courseapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.example.lqs2.courseapp.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片工具类
 *
 * @author lqs2
 */
public class ImageTools {


    /**
     * 将bit存储到相册
     *
     * @param bmp     位图
     * @param picName 图像名称
     * @param context 上下文
     */
    public static void saveBmp2Gallery(Bitmap bmp, String picName, Context context) {
        String fileName = null;
        //系统相册目录
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;
        File file = null;
        FileOutputStream outStream = null;

        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(galleryPath, picName + ".jpg");
            // 获得文件相对路径
            fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(),
                bmp, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
        Toast.makeText(context, "图片保存在相册了QAQ", Toast.LENGTH_LONG).show();
    }

    /**
     * 裁剪图片
     *
     * @param uri             图片uri
     * @param context         上下文
     * @param activity        当前活动
     * @param needCircleImage 是否需要圆角图片
     */
    public static void cropRawPhoto(Uri uri, Context context, Activity activity, boolean needCircleImage) {

        int scaleX = 9;
        int scaleY = 16;
        // 修改配置参数（列出了部分配置，并不是全部）
        UCrop.Options options = new UCrop.Options();
        //设置裁剪图片可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        // 修改标题栏颜色
        options.setToolbarColor(context.getResources().getColor(R.color.r4));
        // 修改状态栏颜色
        options.setStatusBarColor(context.getResources().getColor(R.color.r4));
        // 隐藏底部工具
        options.setHideBottomControls(false);
        // 图片格式
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        // 设置图片压缩质量
        options.setCompressionQuality(100);
        // 是否让用户调整范围(默认false)，如果开启，可能会造成剪切的图片的长宽比不是设定的
        // 如果不开启，用户不能拖动选框，只能缩放图片
        options.setFreeStyleCropEnabled(true);
        // 框框内加个圈圈,好看点而已
        if (needCircleImage) {
            options.setCircleDimmedLayer(true);
            options.setShowCropGrid(false);
            scaleX = 1;
            scaleY = 1;

        }
        //是否隐藏底部容器，默认显示
        options.setHideBottomControls(true);
        options.setToolbarTitle("裁剪");
        // 设置图片在切换比例时的动画
        options.setImageToCropBoundsAnimDuration(666);
        // 设置源uri及目标uri
        UCrop.of(uri, Uri.fromFile(new File(context.getCacheDir(), "SampleCropImage.jpeg")))
                // 长宽比
                .withAspectRatio(scaleX, scaleY)
                // 图片大小
//                .withMaxResultSize(250, 250)
                // 配置参数
                .withOptions(options)
                .start(activity);
    }

    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri 图片uri
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        assert input != null;
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1)) {
            return null;
        }
        //图片分辨率以480x800为标准
        float hh = 800f;
        float ww = 480f;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        //be=1表示不缩放
        int be = 1;
        if (originalWidth > originalHeight && originalWidth > ww) {
            //如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {
            //如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        //设置缩放比例
        bitmapOptions.inSampleSize = be;
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        assert input != null;
        input.close();
        //再进行质量压缩
        assert bitmap != null;
        return compressImage(bitmap);
    }

    /**
     * 质量压缩
     *
     * @param image 图像位图
     * @return 压缩后的位图
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            if (options == 0) {
                break;
            }
            //重置baos即清空baos
            baos.reset();
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            //这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //把ByteArrayInputStream数据生成图片
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * 不进行质量压缩，进行分辨率压缩
     *
     * @param sourceBm     原图像
     * @param targetWidth  目标宽度
     * @param targetHeight 目标高度
     * @return 压缩后的图像
     */
    private static Bitmap zipPicWithoutCompress(Bitmap sourceBm, float targetWidth, float targetHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        // 可删除
        newOpts.inPurgeable = true;
        // 可共享
        newOpts.inInputShareable = true;
        // 转成数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        sourceBm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] temp = baos.toByteArray();
        // 此时返回bm为空
        Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        // be=1表示不缩放
        int be = 1;
        if (w > h && w > targetWidth) {
            // 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / targetWidth);
        } else if (w < h && h > targetHeight) {
            // 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / targetHeight);
        }
        if (be <= 0) {
            be = 1;
        }
        // 设置缩放比例
        newOpts.inSampleSize = be;
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length, newOpts);
        // 压缩好比例大小后再进行质量压缩
        return bitmap;

    }


    /**
     * 压缩图片到当前屏幕的大小
     *
     * @param activity 当前活动
     * @param bitmap   要压缩的图像
     * @return 压缩后的图像
     */
    public static Bitmap zipToScreenSize(Activity activity, Bitmap bitmap) {

        DisplayMetrics metrics = new DisplayMetrics();
        /*
          getRealMetrics - 屏幕的原始尺寸，即包含状态栏,获得的尺寸单位为px，即像素，而不是屏幕的绝对尺寸
          version >= 4.2.2
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        } else {
            return zipPicWithoutCompress(bitmap, 600, 800);
        }
        return zipPicWithoutCompress(bitmap, metrics.widthPixels, metrics.heightPixels);

    }

    /**
     * resource中的图像到位图
     *
     * @param r          资源
     * @param resId      资源id
     * @param isCompress 是否开启压缩
     * @return 结果位图
     */
    public static Bitmap srcPicToBitmap(Resources r, int resId, boolean isCompress) {
        @SuppressLint("ResourceType") InputStream is = r.openRawResource(resId);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        Bitmap bmp = bmpDraw.getBitmap();
        if (isCompress) {
            return ImageTools.compressImage(bmp);
        } else {
            return bmp;
        }
    }


    /**
     * 存储图像到相册
     *
     * @param context  上下文
     * @param activity 活动上下文
     * @param imgBit   图像
     */
    public static void saveImgToGallery(Context context, Activity activity, Bitmap imgBit) {
        boolean flag = true;
        if (PermissionUtils.checkWriteExtraStoragePermission(context)) {
            String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            File appDir = new File(storePath);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(appDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                //通过io流的方式来压缩保存图片
                imgBit.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                //把文件插入到系统图库
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
                //保存图片后发送广播通知更新数据库
                Uri uri = Uri.fromFile(file);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            } catch (IOException e) {
                e.printStackTrace();
                flag = false;
            }
            if (flag) {
                ToastUtils.showToast(context, "保存成功", Toast.LENGTH_LONG);
            } else {
                ToastUtils.showToast(context, "保存失败", Toast.LENGTH_SHORT);
            }
        } else {
            PermissionUtils.requestWritePermission(context, activity, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE);
        }
    }


    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     *
     * @param filePath   图片路径
     * @param profilePic 是否是头像
     * @return 压缩后的图像
     */
    public static Bitmap getSmallBitmap(String filePath, boolean profilePic) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        if (profilePic) {
            options.inSampleSize = calculateInSampleSize(options, 100, 100);
        }

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算图片的缩放值
     *
     * @param options   不知道
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     * @return 不知道
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    /**
     * 从assets文件夹中获取图像
     *
     * @param context  上下文
     * @param subDir   子目录
     * @param fileName 文件名
     * @return 图像
     */
    public static Bitmap getImageFromAssetsFile(Context context, String subDir, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(subDir + java.io.File.separator + fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
//            e.printStackTrace();
            try {
                return BitmapFactory.decodeStream(am.open(subDir + File.separator + "default.png"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return image;
    }

}
