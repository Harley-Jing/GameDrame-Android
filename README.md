
@[toc]
#  封装OkHttps
1. 支持Get请求
2. 支持Post请求
3. 支持上传文件
4. 支持文件下载和断点续传（自动获取文件名）
5. 支持自定义Callback（包括上传/下载的进度回调）
6. 支持在线缓存（只针对Get）
7. 支持离线缓存（只针对Get）
8. 支持断线重连（可自定义次数）
9. 支持log输出（默认输出Log）
 
自己封装okhttp只是加深理解，网上已经有很优秀的封装，本库借鉴了以下项目：
[EasyOk](https://github.com/lihangleo2/EasyOk)
[okhttputils](https://github.com/hongyangAndroid/okhttputils)
### Get请求
```java
        OkHttpUtils.get().url("url")
                .addParams("secretId", "6e8a9dd05c15329")
                .addParams("type", "2")
                .tag("Get")
                //离线缓存  单位: SECONDS
                //.offlineCacheTime(HttpActivity.this, 10)
                //在线缓存  单位: SECONDS
                //.onlineCacheTime(10)
                .build()
                .execute(new OkStringCallback() {
                    public void onBefore(){
                    }
                    
                    public void onAfter(){
                    }
                    
                    @Override
                    public void onError(Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                    }
                });
```
### Post请求
```java
		//Post String
        OkHttpUtils.postString()
                .url("url")
                .content("{"userCode":"123456"}")
                .tag("json")
                //默认JSON
                //.mediaType("application/json;charset=utf-8")
                .build()
                .execute(new OkStringCallback() {
                    public void onBefore(){
                    }
                    
                    public void onAfter(){
                    }
                    
                    @Override
                    public void onError(Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                    }
                });
```

```java
		//Post Form
        OkHttpUtils.postForm()
                .url("url")
                .addParams("key", "val")
                .tag("form")
                .build()
                .execute(new OkStringCallback() {
                    public void onBefore(){
                    }
                    
                    public void onAfter(){
                    }
                    
                    @Override
                    public void onError(Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                    }
                });
```
###  下载文件
下载文件时，必须指定tag。不同的下载文件，必须指定不同的tag，避免混淆断点续传。
```java
  OkHttpUtils.download()
                .url("https://imtt.dd.qq.com/16891/apk/06AB1F5B0A51BEFD859B2B0D6B9ED9D9.apk?fsname=com.tencent.mobileqq_8.1.0_1232.apk&csr=1bbd")
                .path(HttpActivity.this.getCacheDir().getPath())
                //支持自动获取文件名
                //.fileName("qq.exe")
                .tag("download")
                .build()
                .execute(new OkDownloadCallback() {
                    @Override
                    public boolean isRange() {
                    	//支持断点续传
                        return true;
                    }

                    @Override
                    public void pause(float progress, long total) {
                    }

                    @Override
                    public void inProgress(float progress, long total, long speed) {
                    }

                    @Override
                    public void onError(Exception e) {
                    }

                    @Override
                    public void onResponse(File response) {
                    }
                });
```
###  上传文件
```java
        OkHttpUtils.upload().url("url")
                .tag("upload")
                .addFile("key", "filename", new File("path"))
                .build()
                .execute(new OkUploadCallback() {
                    @Override
                    public void inProgress(float progress, long total, long speed) {
                    }

                    @Override
                    public void onError(Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                    }
                });
```
###  取消请求（可暂停下载）

```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //根据
        OkHttpUtils.getInstance().cancelOkhttpTag(TAG);
    }
```
### 其他公共设置
这里以Get为例

```java
     OkHttpUtils.get().url("url")
                .addParams("secretId", "6e8a9dd05c15329")
                .addParams("type", "2")
                .tag("Get")
                //默认10s
                //.connTimeOut(10000L)
                //默认10s
                //.readTimeOut(10000L)
                //默认10s
                //.writeTimeOut(10000L)
                //默认不重连
                //.tryAgainCount(0)
                //默认输出log
                //.isDebug(true)
                .build()
                .execute(new OkStringCallback() {
                    public void onBefore(){
                    }
                    
                    public void onAfter(){
                    }
                    
                    @Override
                    public void onError(Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                    }
                });
```

### 混淆
由于底层使用的是 okhttp,它不能混淆,所以只需要添加以下混淆代码就可以了

```java
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}
```
