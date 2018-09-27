package com.example.lqs2.courseapp.ObjectSample;

import com.example.lqs2.courseapp.common.QServiceCfg;
import com.example.lqs2.courseapp.fragment.FileFragment;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;


/**
 * Created by bradyxiao on 2017/6/1.
 * author bradyxiao
 * <p>
 * Put Object 接口请求可以将本地的文件（Object）上传至指定 Bucket 中。该操作需要请求者对 Bucket 有 WRITE 权限。
 */
public class PutObjectSample {
    private QServiceCfg qServiceCfg;
    private FileFragment fileFragment;

    public PutObjectSample(QServiceCfg qServiceCfg, FileFragment fileFragment) {
        this.qServiceCfg = qServiceCfg;
        this.fileFragment = fileFragment;
    }

    /**
     * 采用异步回调操作
     */
    public void startAsync(String filename) {
        String bucket = qServiceCfg.getBucketForObjectAPITest();
        String cosPath = qServiceCfg.getUploadCosPath();
        String srcPath = qServiceCfg.getUploadFileUrl();

        startProgress("即将开始上传", filename, -1);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath,
                srcPath);
        putObjectRequest.setProgressListener((progress, max) -> {
            float result = (float) (progress * 100.0 / max);
            showProgress((int) result , "正在上传：" + filename);
        });
//        putObjectRequest.setSign(3600, null, null);
        qServiceCfg.cosXmlService.putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {

                byeProgress(filename + "上传成功", "" , -1);
            }
            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException qcloudException, CosXmlServiceException qcloudServiceException) {
                byeProgress("上传失败", "请稍后重试", -1);
            }
        });
    }

    private void showProgress(int per, String  t) {
        fileFragment.publishProgress(per, t);
    }
    private void startProgress(String title, String content, int t) {
        fileFragment.startProgress(title, content, t);
    }

    private void byeProgress(String t, String c, int type) {
        fileFragment.sayGoodbyeToProgress(t, c, type);
    }
}
