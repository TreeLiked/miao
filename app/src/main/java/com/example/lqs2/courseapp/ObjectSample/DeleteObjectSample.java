package com.example.lqs2.courseapp.ObjectSample;

import com.example.lqs2.courseapp.common.QServiceCfg;
import com.example.lqs2.courseapp.fragment.FileFragment;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;


/**
 * Created by bradyxiao on 2017/5/31.
 * author bradyxiao
 *
 * Delete Object 接口请求可以在 COS 的 Bucket 中将一个文件（Object）删除。该操作需要请求者对 Bucket 有 WRITE 权限。
 *
 */
public class DeleteObjectSample {
    private QServiceCfg qServiceCfg;
    private FileFragment fileFragment;
    public DeleteObjectSample(QServiceCfg qServiceCfg, FileFragment fileFragment){
        this.qServiceCfg = qServiceCfg;
        this.fileFragment = fileFragment;
    }
    /**
     *
     * 采用异步回调操作
     *
     */
    public void startAsync(){
        String bucket = qServiceCfg.getBucketForObjectAPITest();
        String cosPath = qServiceCfg.getUploadCosPath();
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, cosPath);
        deleteObjectRequest.setSign(3600,null,null);
        qServiceCfg.cosXmlService.deleteObjectAsync(deleteObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                flushFiles();
                showToast("删除成功，喵～", 0);
            }


            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException qcloudException, CosXmlServiceException qcloudServiceException) {
                showToast("删除失败，呜～", 0);
            }
        });
    }

    private void showToast(String c, int t) {
        fileFragment.showToast(c, t);
    }

    private void flushFiles() {
        fileFragment.showCloudFile(true, false);
    }
}
