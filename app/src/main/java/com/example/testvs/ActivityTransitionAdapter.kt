package com.example.testvs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testvs.databinding.ItemActivityTransitionBinding

// RecyclerView Adapter 클래스: 활동 전환 데이터를 리스트 형태로 관리하고 표시하는 역할을 담당
class ActivityTransitionAdapter
    : RecyclerView.Adapter<ActivityTransitionAdapter.ActivityTransitionViewHolder>() {

    // 활동 전환 데이터를 저장하는 리스트
    private val activityTransitionList: MutableList<String> = mutableListOf()

    // ViewHolder를 생성하는 메서드: 뷰 아이템의 레이아웃을 인플레이트하여 ViewHolder로 반환
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActivityTransitionViewHolder = ActivityTransitionViewHolder(
        // 아이템 레이아웃을 인플레이트하여 ViewHolder를 생성
        ItemActivityTransitionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    // ViewHolder에 데이터를 바인딩하는 메서드
    override fun onBindViewHolder(holder: ActivityTransitionViewHolder, position: Int) {
        // 해당 위치의 데이터를 ViewHolder에 바인딩
        holder.bind(activityTransition = activityTransitionList[position])
    }

    // RecyclerView 아이템의 개수를 반환
    override fun getItemCount(): Int = activityTransitionList.size

    // 새로운 활동 전환 데이터를 리스트에 추가하고 RecyclerView에 변경사항 알림
    fun addItem(activityTransition: String) {
        // 리스트에 새로운 항목 추가
        activityTransitionList.add(activityTransition)
        // 리스트에 새로 추가된 항목을 RecyclerView에 알림
        notifyItemInserted(activityTransitionList.lastIndex)
    }

    // ViewHolder 클래스: 각 아이템을 위한 뷰를 관리
    class ActivityTransitionViewHolder(
        private val binding: ItemActivityTransitionBinding
    ): RecyclerView.ViewHolder(binding.root) {

        // 데이터를 뷰에 바인딩하는 함수
        fun bind(activityTransition: String) {
            // TextView에 활동 전환 데이터를 설정
            binding.tvItemActivity.text = activityTransition
        }
    }

    // 리스트를 초기화하는 메서드: 기존 항목을 모두 제거하고 RecyclerView 업데이트
    fun clearItems() {
        // 리스트 초기화
        activityTransitionList.clear()
        // RecyclerView가 데이터가 변경된 것을 알림
        notifyDataSetChanged()
    }
}
