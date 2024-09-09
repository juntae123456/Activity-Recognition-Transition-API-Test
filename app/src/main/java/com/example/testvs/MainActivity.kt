package com.example.testvs

import android.Manifest//
import android.app.PendingIntent//
import android.content.Intent//
import android.content.IntentFilter//
import android.content.pm.PackageManager//
import android.os.Build//
import android.os.Bundle//
import android.util.Log//
import androidx.appcompat.app.AppCompatActivity//
import androidx.core.app.ActivityCompat//
import androidx.recyclerview.widget.LinearLayoutManager//
import com.example.testvs.ActivityTransitionData.TRANSITIONS_EXTRA//
import com.example.testvs.ActivityTransitionData.TRANSITIONS_RECEIVER_ACTION//
import com.example.testvs.databinding.ActivityMainBinding
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransitionRequest
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var adapter: ActivityTransitionAdapter

    private val activityTransitionReceiver by lazy { ActivityTransitionsReceiver() }

    private lateinit var request: ActivityTransitionRequest
    private lateinit var pendingIntent: PendingIntent

    private lateinit var activityClient: ActivityRecognitionClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        activityClient = ActivityRecognition.getClient(this)

        // 활동 인식 권한을 확인하는 메소드
        if (!checkRecognitionPermissionIfLaterVersionQ()) {
            requestRecognitionPermission()
        }
        //버튼 초기화
        initViews()


        // PedingIntent 설정 및 활동전한 요청
        initPendingIntent()

        if (checkRecognitionPermissionIfLaterVersionQ()) {
            registerActivityTransitionUpdates()
        }

    }

    // 안드로이드 10 이상인 경우, RecognitionPermission 확인
    private fun checkRecognitionPermissionIfLaterVersionQ(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        } else true
    }
    //권한이 없을시 사용
    private fun requestRecognitionPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
            0,
        )
    }
    //버튼 초기화 사용
    private fun initViews() {
        val linearLayoutManager = LinearLayoutManager(this@MainActivity)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true

        adapter = ActivityTransitionAdapter()
        binding.rvActivityTransition.adapter = adapter
        binding.rvActivityTransition.layoutManager = linearLayoutManager

        binding.btRegisterRequest.setOnClickListener {
            registerActivityTransitionUpdates()
        }
    }
    //브로드캐스트 사용
    private fun initPendingIntent() {
        val intent = Intent(TRANSITIONS_RECEIVER_ACTION)
        intent.setPackage(this.packageName)

        pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    //활동 전한 감지
    private fun registerActivityTransitionUpdates() {
        request = ActivityTransitionRequest(ActivityTransitionData.getActivityTransitionList())
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        activityClient
            .requestActivityTransitionUpdates(request, pendingIntent).
            addOnSuccessListener {
                adapter.addItem("활동 감지가 시작되었습니다. " + getCurrentTime())
            }
            .addOnFailureListener { exception ->
                adapter.addItem(exception.localizedMessage ?: "예외가 발생하였습니다.")
            }
    }
    //브로드 캐스터 리시버 등록
    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(TRANSITIONS_RECEIVER_ACTION)

        registerReceiver(
            activityTransitionReceiver,
            intentFilter,
            RECEIVER_NOT_EXPORTED
        )
    }

    // From ActivityTransitionReceiver
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("Activity Recognition", "onNewIntent")
        checkIntentData(intent)
    }

    private fun checkIntentData(intent: Intent) {
        val activityTransition = intent.getStringExtra(TRANSITIONS_EXTRA)

        if (activityTransition != null) {
            adapter.clearItems() // 기존 기록 제거
            adapter.addItem(activityTransition) // 새로운 기록 추가
        }
    }
    //앱이 중지 될때 감지
    override fun onStop() {
        if (checkRecognitionPermissionIfLaterVersionQ()) {
           unregisterActivityTransitionUpdates()
        }

        unregisterReceiver(activityTransitionReceiver)
        super.onStop()
    }
    //활동 감지 종료 업데이트
    private fun unregisterActivityTransitionUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        activityClient
            .removeActivityTransitionUpdates(pendingIntent)
            .addOnSuccessListener {
                adapter.addItem("활동 감지가 종료되었습니다. " + getCurrentTime())
            }
            .addOnFailureListener { exception ->
                adapter.addItem(exception.localizedMessage ?: "예외가 발생하였습니다.")
            }
    }
    //시간 출력
    private fun getCurrentTime(): String {
        val timeZoneId = ZoneId.of("Asia/Seoul")
        val dateTimeFormatter = DateTimeFormatter.ofPattern("a KK:mm:ss")
        return LocalDateTime.now(timeZoneId).format(dateTimeFormatter)
    }
}
