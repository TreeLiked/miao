package com.example.lqs2.courseapp.ObjectSample;

import android.content.Context;
import android.util.Log;

import com.tencent.cos.xml.CosXml;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.model.bucket.PutBucketRequest;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

/**
 * Created by bradyxiao on 2018/3/6.
 */

public class CreateBucketTool {

    private static final String TAG = CreateBucketTool.class.getName();


    /** 腾讯云 cos 服务的 appid */
    private final String appid = "1253931949";

    /** appid 对应的 秘钥 */
    private final String secretId = "AKIDFKRetDGBkLzXu4iVt67zoTwpjw8OcQ9g";

    /** appid 对应的 秘钥 */
    private final String secretKey = "TtAIF0Y7YM1UXcdXJtKdppdFf2DF4hEx";

    /** bucketForObjectAPITest 所处在的地域 */
    private String region = "ap-shanghai";

    private Context context;

    CosXml cosXml;

    public CreateBucketTool(Context context){
        System.out.println("=============");
        this.context = context;
        System.out.println(context == null ? "true":"false") ;
    }

    public void createBucket(String bucket){

        System.out.println(TAG + "----------");
        if(cosXml == null){
            CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                    .isHttps(false)
                    .setAppidAndRegion(appid, region)
                    .setDebuggable(true)
                    .builder();
            cosXml = new CosXmlService(context, cosXmlServiceConfig,
                    new ShortTimeCredentialProvider(secretId,secretKey,600) );
        }

        PutBucketRequest putBucketRequest = new PutBucketRequest(bucket);
        try {
            cosXml.putBucket(putBucketRequest);
            System.out.println("success");
            Log.d(TAG, bucket + " crate success !");

        } catch (CosXmlClientException e) {
            System.out.println("f1");

            Log.d(TAG, bucket + " crate failed caused by " + e.getMessage());
        } catch (CosXmlServiceException e) {
            System.out.println("f2");

            Log.d(TAG, bucket + " crate failed caused by " + e.getMessage());
        }
    }
}
