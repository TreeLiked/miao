package com.example.lqs2.courseapp.ObjectSample;

import android.util.Log;

import com.example.lqs2.courseapp.common.QServiceCfg;
import com.example.lqs2.courseapp.fragment.FileFragment;
import com.example.lqs2.courseapp.utils.FileUtils;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.GetObjectRequest;

import java.io.File;


/**
 * Created by bradyxiao on 2017/6/7.
 * author bradyxiao
 * <p>
 * Get Object 接口请求可以在 COS 的 Bucket 中将一个文件（Object）下载至本地。该操作需要请求者对目标 Object 具有读权限或目标 Object 对所有人都开放了读权限（公有读）。
 */
public class GetObjectSample {
    private QServiceCfg qServiceCfg;
    private FileFragment fileFragment;

    public GetObjectSample(QServiceCfg qServiceCfg, FileFragment fileFragment) {
        this.qServiceCfg = qServiceCfg;
        this.fileFragment = fileFragment;
    }

    /**
     * 采用异步回调操作
     */
    public void startAsync(String bucket_id, String filename) {
        String bucket = qServiceCfg.getBucketForObjectAPITest();
        String cosPath = qServiceCfg.getGetCosPath();
        String downloadDir = qServiceCfg.getDownloadDir();

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, cosPath, downloadDir);

        getObjectRequest.setSign(3600, null, null);
        getObjectRequest.setRange(1);
        startProgress("即将开始下载", filename, 1);
        getObjectRequest.setProgressListener((progress, max) -> {
            Log.w("XIAO", "progress = " + progress + " max = " + max);
            float per = ((float) progress / max);
            float p = (float) (Math.round(per * 100));
            showProgress((int) (p), "正在下载：" + filename);
        });
        qServiceCfg.cosXmlService.getObjectAsync(getObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                byeProgress(filename + "下载成功", "文件存储在： " + qServiceCfg.getDownloadDir() + File.separator + filename, 1);
                boolean r = FileUtils.renameFile(bucket_id, filename, downloadDir);
                if (!r) {
                    byeProgress("重命名失败", "现文件名: " + bucket_id + "存储在：" + qServiceCfg.getDownloadDir() + File.separator + filename, 1);
                }
            }
            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException qcloudException, CosXmlServiceException qcloudServiceException) {
                byeProgress("下载失败", "请稍候重试", 1);
            }
        });
    }

    private void showProgress(int per, String t) {
        fileFragment.publishProgress(per, t);
    }

    private void startProgress(String title, String content, int t) {
        fileFragment.startProgress(title, content, t);
    }

    private void byeProgress(String t, String c, int type) {
        fileFragment.sayGoodbyeToProgress(t, c, type);
    }
}
