# Activity Recognition Transition API Test

Activity Recognition Transition API를 활용한 간단한 테스트 앱입니다

## 프로젝트 설명

[//]: # 이 프로젝트는 Activity Recognition Transition API를 이용한 자동차나 자전거,걷기,뛰기,정지를 알려주는 앱입니다.
## 설치 및 설정

### 필수 사항

- kotlin 1.9.0 이상

### 설치 방법

1. build.gradle.kts(app)

   ```sh
   implementation ("com.google.android.gms:play-services-location:21.3.0")
   ```

2. AndroidManifest.xml

   ```sh
   //활동기반 
    <!-- Required for 28 and below. -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
    <!-- Required for 29+. -->
    <uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
    //포그라운드
     <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    //위치기반
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
   ```



## 사용 예시

`app` 파일을 실제로 실행시켰을때의 화면.

![Screenshot_20240911_091607_testvs 1](https://github.com/user-attachments/assets/e4daf0ab-e5d5-4b1f-9670-6ec5ff71c158)

![Screenshot_20240911_091518_testvs 1](https://github.com/user-attachments/assets/48240ee6-627c-4b14-aec8-53a34fb1c4a5)

