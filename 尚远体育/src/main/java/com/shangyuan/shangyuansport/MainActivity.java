package com.shangyuan.shangyuansport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

;

public class MainActivity extends AppCompatActivity {

    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mInfo;
    private String APPID="1105692541";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTencent = Tencent.createInstance(APPID,MainActivity.this.getApplication());
        findViewById(R.id.tv_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
                 官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
                 第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
                mIUiListener = new BaseUiListener();
                mTencent.login(MainActivity.this,"all", mIUiListener);
            }
        });
    }

    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
            Log.e("tag", "response:" + response);
            JSONObject jo = (JSONObject) response;

            try {
                    String openID = jo.getString("openid");
                    String accessToken = jo.getString("access_token");
                    String expires = jo.getString("expires_in");
                    mTencent.setOpenId(openID);
                    mTencent.setAccessToken(accessToken, expires);
                    QQToken qqToken = mTencent.getQQToken();
                        mInfo = new UserInfo(getApplicationContext(), qqToken);
                        mInfo.getUserInfo(new IUiListener() {
                            @Override
                            public void onComplete(Object response) {
                                Log.e("BaseUiListener", "成功"+response.toString());
                            }

                            @Override
                            public void onError(UiError uiError) {
                                Log.e("BaseUiListener", "失败"+uiError.toString());
                            }

                            @Override
                            public void onCancel() {
                                Log.e("BaseUiListener", "取消");
                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "登录取消", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TAG", "-->onActivityResult " + requestCode  + " resultCode=" + resultCode);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,mIUiListener);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
