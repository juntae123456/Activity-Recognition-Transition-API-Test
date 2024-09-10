package com.example.testvs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.testvs.ActivityTransitionData.TRANSITIONS_EXTRA
import com.example.testvs.ActivityTransitionData.TRANSITIONS_RECEIVER_ACTION
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// 브로드캐스트 리시버 클래스: 활동 전환 이벤트를 수신하고 처리하는 역할
class ActivityTransitionsReceiver: BroadcastReceiver() {

    // 브로드캐스트 수신 시 호출되는 메서드
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TRANSITIONS_RECEIVER_ACTION, "ON_RECEIVE $intent")

        // 인텐트의 액션이 활동 전환과 관련된 경우 처리
        when (intent.action) {
            TRANSITIONS_RECEIVER_ACTION -> {
                // 인텐트에서 활동 전환 결과를 추출
                if (ActivityTransitionResult.hasResult(intent)) {
                    val result: ActivityTransitionResult = ActivityTransitionResult.extractResult(intent) ?: return

                    // 결과에 있는 각 활동 전환 이벤트를 처리
                    for (event in result.transitionEvents) {
                        // 전환 정보를 가져와서 처리
                        val transitionInfo = getTransitionInfo(event)
                        // 전환 정보를 메인 액티비티로 전달
                        sendTransitionInfo(transitionInfo, context)
                    }
                }
            }
        }
    }

    // 이벤트에서 활동 유형과 전환 유형을 문자열로 변환하여 전환 정보를 생성하는 메서드
    private fun getTransitionInfo(event: ActivityTransitionEvent): String {
        val activityType = activityType(event.activityType) // 활동 유형 변환
        val transitionType = transitionType(event.transitionType) // 전환 유형 변환
        Log.d("ActivityRecognition", "Activity Type: $activityType, Transition Type: $transitionType") // 로그 추가
        // 활동 유형, 전환 유형, 현재 시간을 문자열로 결합
        return "($activityType) ($transitionType) ${getCurrentTime()}"
    }

    // 전환 정보를 메인 액티비티로 전달하는 메서드
    private fun sendTransitionInfo(transitionInfo: String, context: Context) {
        // 메인 액티비티로 인텐트를 생성하고 전환 정보를 전달
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(TRANSITIONS_EXTRA, transitionInfo)
        // 기존 액티비티를 다시 열지 않고 같은 액티비티로 인텐트를 전달
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        context.startActivity(intent) // 메인 액티비티 시작
    }

    // 전환 유형을 문자열로 변환하는 메서드 (ENTER, EXIT)
    private fun transitionType(transition: Int): String? {
        return when (transition) {
            ActivityTransition.ACTIVITY_TRANSITION_ENTER -> "시작"
            ActivityTransition.ACTIVITY_TRANSITION_EXIT -> "종료"
            else -> "알 수 없음"
        }
    }

    // 활동 유형을 문자열로 변환하는 메서드 (걷기, 뛰기, 차량 이동 등)
    private fun activityType(activity: Int): String? {
        return when (activity) {
            DetectedActivity.IN_VEHICLE -> "차량 이동"
            DetectedActivity.STILL -> "정지"
            DetectedActivity.WALKING -> "걷기"
            DetectedActivity.ON_BICYCLE -> "자전거 이동"
            DetectedActivity.RUNNING -> "뛰기"
            else -> "알 수 없음"
        }
    }

    // 현재 시간을 "오전/오후 HH:MM:SS" 형식으로 반환하는 메서드
    private fun getCurrentTime(): String {
        val timeZoneId = ZoneId.of("Asia/Seoul") // 서울 시간대 설정
        val dateTimeFormatter = DateTimeFormatter.ofPattern("a KK:mm:ss") // 시간 형식 지정
        return LocalDateTime.now(timeZoneId).format(dateTimeFormatter) // 현재 시간 반환
    }


}

