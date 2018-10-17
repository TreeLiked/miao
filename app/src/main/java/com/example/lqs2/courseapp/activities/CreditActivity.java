package com.example.lqs2.courseapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.global.ThreadPoolExecutorFactory;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.StatusBarUtils;

import java.util.Map;

/**
 * 查询学分活动
 *
 * @author lqs2
 */
public class CreditActivity extends ActivityCollector {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
        StatusBarUtils.setStatusTransparent(this);

        Intent intent = getIntent();
        String html = intent.getStringExtra("html");

        showCredit(html);
    }

    /**
     * 显示学分
     */
    @SuppressLint("SetTextI18n")
    private void showCredit(String html) {
        ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> {
//            异步解析html
            Map<String, String> info = HtmlCodeExtractUtil.parseHtmlForCredit(html);
            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.credit_xthk_text_view)).setText(info.get("xueHao"));
                ((TextView) findViewById(R.id.credit_xymy_text_view)).setText(info.get("xingMing"));
                ((TextView) findViewById(R.id.credit_xtyr_text_view)).setText(info.get("xueYuan"));
                ((TextView) findViewById(R.id.credit_vrye_text_view)).setText("专业：\t" + info.get("zhuanYe"));
                ((TextView) findViewById(R.id.credit_bjji_text_view)).setText(info.get("xingZhengBan"));

                ((TextView) findViewById(R.id.credit_soxtxtff_text_view)).setText("所选学分：\t" + info.get("choose_credit"));
                ((TextView) findViewById(R.id.credit_hodextff_text_view)).setText("获得学分：\t" + info.get("achieve_credit"));
                ((TextView) findViewById(R.id.credit_isxqxtff_text_view)).setText("重修学分：\t" + info.get("rehearsal_credit"));
                ((TextView) findViewById(R.id.credit_vgkkwztsgoxtff_text_view)).setText("正考未通过学分：\t" + info.get("failed_credit"));

                ((TextView) findViewById(R.id.t_000_text_view)).setText(info.get("000"));
                ((TextView) findViewById(R.id.t_001_text_view)).setText(info.get("001"));
                ((TextView) findViewById(R.id.t_002_text_view)).setText(info.get("002"));
                ((TextView) findViewById(R.id.t_010_text_view)).setText(info.get("010"));
                ((TextView) findViewById(R.id.t_011_text_view)).setText(info.get("011"));
                ((TextView) findViewById(R.id.t_012_text_view)).setText(info.get("012"));
                ((TextView) findViewById(R.id.t_020_text_view)).setText(info.get("020"));
                ((TextView) findViewById(R.id.t_021_text_view)).setText(info.get("021"));
                ((TextView) findViewById(R.id.t_022_text_view)).setText(info.get("022"));
                ((TextView) findViewById(R.id.t_030_text_view)).setText(info.get("030"));
                ((TextView) findViewById(R.id.t_031_text_view)).setText(info.get("031"));
                ((TextView) findViewById(R.id.t_032_text_view)).setText(info.get("032"));
                ((TextView) findViewById(R.id.t_040_text_view)).setText(info.get("040"));
                ((TextView) findViewById(R.id.t_041_text_view)).setText(info.get("041"));
                ((TextView) findViewById(R.id.t_042_text_view)).setText(info.get("042"));
                ((TextView) findViewById(R.id.t_050_text_view)).setText(info.get("050"));
                ((TextView) findViewById(R.id.t_051_text_view)).setText(info.get("051"));
                ((TextView) findViewById(R.id.t_052_text_view)).setText(info.get("052"));
                ((TextView) findViewById(R.id.t_060_text_view)).setText(info.get("060"));
                ((TextView) findViewById(R.id.t_061_text_view)).setText(info.get("061"));
                ((TextView) findViewById(R.id.t_062_text_view)).setText(info.get("062"));
                ((TextView) findViewById(R.id.t_070_text_view)).setText(info.get("070"));
                ((TextView) findViewById(R.id.t_071_text_view)).setText(info.get("071"));
                ((TextView) findViewById(R.id.t_072_text_view)).setText(info.get("072"));

                ((TextView) findViewById(R.id.t_100_text_view)).setText(info.get("100"));
                ((TextView) findViewById(R.id.t_101_text_view)).setText(info.get("101"));
                ((TextView) findViewById(R.id.t_102_text_view)).setText(info.get("102"));
                ((TextView) findViewById(R.id.t_103_text_view)).setText(info.get("103"));
                ((TextView) findViewById(R.id.t_104_text_view)).setText(info.get("104"));
                ((TextView) findViewById(R.id.t_110_text_view)).setText(info.get("110"));
                ((TextView) findViewById(R.id.t_111_text_view)).setText(info.get("111"));
                ((TextView) findViewById(R.id.t_112_text_view)).setText(info.get("112"));
                ((TextView) findViewById(R.id.t_113_text_view)).setText(info.get("113"));
                ((TextView) findViewById(R.id.t_114_text_view)).setText(info.get("114"));
                ((TextView) findViewById(R.id.t_120_text_view)).setText(info.get("120"));
                ((TextView) findViewById(R.id.t_121_text_view)).setText(info.get("121"));
                ((TextView) findViewById(R.id.t_122_text_view)).setText(info.get("122"));
                ((TextView) findViewById(R.id.t_123_text_view)).setText(info.get("123"));
                ((TextView) findViewById(R.id.t_124_text_view)).setText(info.get("124"));
                ((TextView) findViewById(R.id.t_130_text_view)).setText(info.get("130"));
                ((TextView) findViewById(R.id.t_131_text_view)).setText(info.get("131"));
                ((TextView) findViewById(R.id.t_132_text_view)).setText(info.get("132"));
                ((TextView) findViewById(R.id.t_133_text_view)).setText(info.get("133"));
                ((TextView) findViewById(R.id.t_134_text_view)).setText(info.get("134"));
                ((TextView) findViewById(R.id.t_140_text_view)).setText(info.get("140"));
                ((TextView) findViewById(R.id.t_141_text_view)).setText(info.get("141"));
                ((TextView) findViewById(R.id.t_142_text_view)).setText(info.get("142"));
                ((TextView) findViewById(R.id.t_143_text_view)).setText(info.get("143"));
                ((TextView) findViewById(R.id.t_144_text_view)).setText(info.get("144"));
                ((TextView) findViewById(R.id.t_150_text_view)).setText(info.get("150"));
                ((TextView) findViewById(R.id.t_151_text_view)).setText(info.get("151"));
                ((TextView) findViewById(R.id.t_152_text_view)).setText(info.get("152"));
                ((TextView) findViewById(R.id.t_153_text_view)).setText(info.get("153"));
                ((TextView) findViewById(R.id.t_154_text_view)).setText(info.get("154"));

                ((TextView) findViewById(R.id.t_200_text_view)).setText(info.get("200"));
                ((TextView) findViewById(R.id.t_201_text_view)).setText(info.get("201"));
                ((TextView) findViewById(R.id.t_202_text_view)).setText(info.get("202"));
                ((TextView) findViewById(R.id.t_203_text_view)).setText(info.get("203"));
                ((TextView) findViewById(R.id.t_204_text_view)).setText(info.get("204"));
                ((TextView) findViewById(R.id.t_205_text_view)).setText(info.get("205"));
                ((TextView) findViewById(R.id.t_210_text_view)).setText(info.get("210"));
                ((TextView) findViewById(R.id.t_211_text_view)).setText(info.get("211"));
                ((TextView) findViewById(R.id.t_212_text_view)).setText(info.get("212"));
                ((TextView) findViewById(R.id.t_213_text_view)).setText(info.get("213"));
                ((TextView) findViewById(R.id.t_214_text_view)).setText(info.get("214"));
                ((TextView) findViewById(R.id.t_215_text_view)).setText(info.get("215"));
            });
        });
    }
}
