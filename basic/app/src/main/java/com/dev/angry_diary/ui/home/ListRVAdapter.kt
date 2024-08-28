package com.dev.angry_diary.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev.angry_diary.data.local.model.Diary
import com.dev.angry_diary.databinding.ItemDiaryHeaderBinding
import com.dev.angry_diary.databinding.ItemDiaryItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class ListRVAdapter : ListAdapter<DateItem, RecyclerView.ViewHolder>(DiaryDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun submitDiaryList(list: List<Diary>?) {
        adapterScope.launch {
            val groupedItems = list?.groupByDate() ?: emptyList()  // 다이어리 리스트를 날짜별로 그룹화
            withContext(Dispatchers.Main) {
                submitList(groupedItems)  // 변환된 리스트를 RecyclerView에 제출
            }
        }
    }

    private fun List<Diary>.groupByDate(): List<DateItem> {
        return this.groupBy { it.date }  // 날짜별로 그룹화
            .flatMap { (date, diaries) ->  // 각 날짜에 대해
                listOf(DateItem.Header(date)) + diaries.map { DateItem.DiaryItem(it) }  // 헤더와 다이어리 아이템으로 변환
            }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is DateItem.DiaryItem -> (holder as ViewHolder).bind(item.diary)
            is DateItem.Header -> (holder as TextViewHolder).bind(item.date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    // 각 아이템의 뷰 타입 반환
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DateItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DateItem.DiaryItem -> ITEM_VIEW_TYPE_ITEM
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    class TextViewHolder private constructor(private val binding: ItemDiaryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String) {  // 데이터 바인딩
            binding.diaryDayTv.text = date
            binding.executePendingBindings()
        }

        companion object {  // 뷰 홀더 인스턴스 생성
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDiaryHeaderBinding.inflate(layoutInflater, parent, false)
                return TextViewHolder(binding)
            }
        }
    }

    class ViewHolder private constructor(private val binding: ItemDiaryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Diary) {
            binding.viewModel = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDiaryItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    companion object {
        const val ITEM_VIEW_TYPE_HEADER = 0
        const val ITEM_VIEW_TYPE_ITEM = 1
    }
}

class DiaryDiffCallback : DiffUtil.ItemCallback<DateItem>() {
    override fun areItemsTheSame(oldItem: DateItem, newItem: DateItem): Boolean {
        return (oldItem is DateItem.DiaryItem && newItem is DateItem.DiaryItem && oldItem.diary.contentId == newItem.diary.contentId) ||
                (oldItem is DateItem.Header && newItem is DateItem.Header && oldItem.date == newItem.date)
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DateItem, newItem: DateItem): Boolean {
        return oldItem == newItem  // 내용 비교
    }
}

sealed class DateItem {
    data class Header(val date: String) : DateItem()
    data class DiaryItem(val diary: Diary) : DateItem()
}
