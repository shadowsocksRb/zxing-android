## 条码扫描器

基于 [zxing](https://github.com/zxing/zxing)，核心库未修改，仅处理 Android 部分。

- 缩小权限
  * 移除网络权限及相关功能
  * 移除通讯录权限及相关功能
  * 移除存储卡权限并添加相关功能
- 支持无相机使用，从文件扫描和保存图片到文件
- 完全保留通过 intent 编码及请求解码功能
- 移除帮助

### 权限说明

- 相机。非必需，用于从摄像头采集条形码
- 振动。成功扫描后可选的振动提醒
- 控制和查看 WLAN 网络。直接连接扫描到的 WLAN 二维码

### 构建

* OpenJDK 1.8+
* Android SDK
  - Android SDK Build-Tools 30.0.0
  - Android SDK Platform 30
  - Android SDK Tools

```bash
git clone https://github.com/shadowsocksRb/zxing-android.git
cd zxing-android
# 建议编辑 app/build.gradle ，修改 applicationId 以避免冲突
./gradlew aR
adb install app/build/outputs/apk/release/app-release.apk
```
