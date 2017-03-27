## [我的博客](http://blog.csdn.net/sw5131899/article/details/61429679)

一个项目，访问网络那是必须的。现在开源的网络框架很多。比如最开始的HeepClient，Volley，xUtils，
最近很火的okhttp，还有例如retrofit，okGo这些都是很不错 的框架。但是毕竟是别人写的。出了什么问题都不好查找。
这里自己封装了一个网络框架。基于okhttp的高并发网络加载，同时也对下载文件进行了封装，使用数据库做下载记录缓存，
暴露给调用层下载进度显示回调。如果有单独需要下载进度显示的，可以把这部分逻辑抠出去，耦合很小，是按功能分包的。
这里简单介绍一下封装逻辑。

### 设计框架逻辑图

![](https://github.com/SingleShu/OkHttpLemonDemo/raw/master/logo/aaa.png)


### UML

![](https://github.com/SingleShu/OkHttpLemonDemo/raw/master/logo/bbb.png)

使用导入：
```Java
compile 'com.singleshu88:OkHttpLemon:1.0.2'
```

采用链式调度，使用builder的设计模式，对参数进行缓存。所有调用都是OkHttpLemon静态链式调度。最后必须调用execute。
## 具体API ##
 
### .init()
OkHttpLemon的初始化。

### .initOptions()
初始化全局变量，也就是设置响应时间之类的参数。

### .url(String url)
设置url不论是下载文件，上传，还是get,post都是在这里设置url

### .get(Class<T> responseClazz)
使用get请求获取参数，同时设置响应类型。

### .get()
使用get，默认返回String类型

### .postString(String key,String value)
使用post上传参数，可以多次调用。

### .postFile(String name,String fileName,File imgFile)
使用post上传文件，图片之类。

### .postResponseClazz(Class<T> responseClazz)
使用post请求，设置响应类型

### .executes(@NonNull IDataListener<String> dataListener)
执行网络请求，返回String类型

### .execute(@NonNull IDataListener<T> dataListener)
执行网络请求，返回设置类型

这些都是常用的网络请求方式，还单独封装了文件下载。

### .downPriority(HttpPriority priority)
设置下载文件的优先级，一个枚举类型，分别有low，middle，high

### .downFilePath(String filePath)
下载文件保存的地址，sdcard/0/wps.apk  包括全名

### .pause(String url)
通过下载地址来暂停下载进度

### .pause(String url,String fileName)
通过下载地址和保存文件地址来暂停下载进度 

### .start(String url)
通过下载地址来重启一个文件下载

### .start(String url,String filePath)
通过下载地址和文件保存地址来准确的重启一个被暂停的文件下载

### .getOptions()
设置网络链接超时等参数


### .executeDown(@NonNull IDownloadCallback dataCallback)
执行文件下载

## 具体使用

```Java
/**
 * Created by ShuWen on 2017/3/11.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpLemon.init().initOptions();
    }
}
```

```Java
package com.example.administrator.okhttplemondemo;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.lemonlibrary.db.util.PermissionUtils;
import com.example.lemonokhttp.enums.DownloadStatus;
import com.example.lemonokhttp.http.OkHttpLemon;
import com.example.lemonokhttp.interfaces.IDataListener;
import com.example.lemonokhttp.interfaces.IDownloadCallback;

public class MainActivity extends AppCompatActivity {

    public String tabaoUrl = "http://download.apk8.com/soft/2015/%E6%B7%98%E5%AE%9D.apk";
    public String wpsUrl = "http://gdown.baidu.com/data/wisegame/8be18d2c0dc8a9c9/WPSOffice_177.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionUtils.getInstance().requestPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance().onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    public void click(View view){
        switch (view.getId()){
            case R.id.btn1:
                Log.i("tag00","下载wps：");
                OkHttpLemon.init()
                        .url(wpsUrl)
                        .downFilePath(Environment.getExternalStorageDirectory() + "/wps.apk")
                        .executeDown(new IDownloadCallback() {
                            @Override
                            public void onDownTotalLength(long totalLen) {

                            }

                            @Override
                            public void onDownCurrentLenChange(long alreadyDownLen, double downPercent, long speed) {
                                Log.i("tag00","-----已下载  "+ alreadyDownLen/1024/1024+"M  下载长度  "+downPercent*100 +"%   "+"下载速度："+ speed/1000 +"k/s");
                            }

                            @Override
                            public void onFinish(long alreadyDownLen, long totalLen, String stratTime, String finishTime) {
                                Log.i("tag00","下载成功。" + stratTime + "  "+finishTime);
                            }


                            @Override
                            public void onEorror(int errorCode, String ts) {
                                Log.i("tag00","下载异常："+"  errorCode：  " + errorCode +" errorMsg " +ts);

                            }

                            @Override
                            public void onDownStatusChange(DownloadStatus downStatus) {
                                Log.i("tag00","状态变更："+downStatus);
                            }
                        });

                break;
            case R.id.btn2:
                //默认下载文件地址    sdcard/okLemonDown/downFiles
                Log.i("tag00","下载game：");
                OkHttpLemon.init()
                        .url(tabaoUrl)
                        .executeDown(new IDownloadCallback() {
                            @Override
                            public void onDownTotalLength(long totalLen) {

                            }

                            @Override
                            public void onDownCurrentLenChange(long alreadyDownLen, double downPercent, long speed) {
                                Log.i("tag00","-----已下载  "+ alreadyDownLen/1024/1024+"M  下载长度  "+downPercent*100 +"%   "+"下载速度："+ speed/1000 +"k/s");
                            }

                            @Override
                            public void onFinish(long alreadyDownLen, long totalLen, String stratTime, String finishTime) {
                                Log.i("tag00","下载成功。");

                            }

                            @Override
                            public void onEorror(int errorCode, String ts) {
                                Log.i("tag00","下载异常："+"  errorCode：  " + errorCode +" errorMsg " +ts);

                            }

                            @Override
                            public void onDownStatusChange(DownloadStatus downStatus) {
                                Log.i("tag00","状态变更："+downStatus);
                            }
                        });
                break;
            case R.id.btn3:
                OkHttpLemon.init().pause(wpsUrl);
                break;
            case R.id.btn5:
                OkHttpLemon.init().start(wpsUrl);
                break;
            case R.id.btn4:
                OkHttpLemon.init().pause(tabaoUrl);
                break;
            case R.id.btn6:
                OkHttpLemon.init().start(tabaoUrl);
                break;
            case R.id.btn7:
                OkHttpLemon.init().url("http://v3.wufazhuce.com:8000/api/hp/idlist/0")
                        .get(MainPageBean.class)
                        .execute(new IDataListener<MainPageBean>() {
                            @Override
                            public void onSuccess(MainPageBean s) {
                                Log.i("tag00",s.getRes()+"");
                                for (String s1:s.getData()) {
                                    Log.i("tag00",s1);
                                }
                            }

                            @Override
                            public void onError(int code,String ts) {

                            }
                        });
                break;
            case R.id.btn8:
                OkHttpLemon.init().url("http://v3.wufazhuce.com:8000/api/praise/add")
                        .postString("itemid","1644")
                        .postString("type","hpcontent")
                        .postString("deviceid","ffffffff-b821-e83f-45c3-423b5c7ea996")
                        .postString("version","3.5.0")
                        .postString("devicetype","android")
                        .postString("platform","android")
                        .executes(new IDataListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                Log.i("tag00",s);
                            }

                            @Override
                            public void onError(int code, String ts) {
                                Log.i("tag00","code:"+code + "提示："+ts);
                            }
                        });
                break;
        }

    }
}
```

```Java
package com.example.administrator.okhttplemondemo;

import java.util.List;

/**
 * Created by ShuWen on 2016/10/26.
 */

public class MainPageBean {

    /**
     * res : 0
     * data : ["1509","1505","1506","1504","1497","1503","1498","1502","1501","1500"]
     */

    private int res;
    private List<String> data;

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MainPageBean{" +
                "res=" + res +
                ", data=" + data +
                '}';
    }
}
```
这个网络加载框架的文件下载的设计还是可以提供参考的，使用了我之前设计的LemonDao的数据库框架扩展性很强。
觉得学到东西了的朋友请给个star。谢谢！

