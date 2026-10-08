package com.example.myno.msos6

import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class DateCo : AppCompatActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var selectedDateText: TextView
    private lateinit var festivalText: TextView
    private lateinit var historyText: TextView
    
    var app_version = "1.1.1"

    // 节日数据：键为 "月/日" 格式（月、日无前导零），值为节日名称
    private val festivalsMap = mapOf(
        // 庆祝性节日
        "4/1" to "🎭 愚人节",
        "5/9" to "🇷🇺 俄罗斯胜利日",
        "10/1" to "🇨🇳 中国国庆节",
        "5/1" to "⚒️ 国际劳动节",
        "5/4" to "🔨 五四青年节",
        "6/1" to "🤗 国际儿童节"
    )
    
    private val comMap = mapOf(
        // 纪念性节日
        "8/1" to "八一建军节"
    )
    
    private val MournMap = mapOf(
        // 哀悼性节日
        "5/12" to "四川汶川大地震遇难同胞悼念日",
        "9/18" to "九一八事变纪念日",
        "7/7" to "七七事变纪念日",
        "9/11" to "爱国者日与全国服务纪念日"
    )
    
    private val NationaldayMap = mapOf(
        // 公祭日
        "12/13" to "中国南京大屠杀死难者国家公祭日",
        "12/7" to "美国国家珍珠港纪念日"
    )

    // 历史上的今天数据：键为 "月/日" 格式，值为事件列表
    private val historyMap = mapOf(
        "8/15" to listOf("📜 1945年8月15日：日本宣布无条件投降，二战结束。"),
        "4/29" to listOf(
            "📜 2005年4月29日：中国共产党中央总书记胡锦涛与中国国民党主席连战在北京举行会谈。",
            "📜 1946年4月29日：远东国际军事法庭开庭，对东条英机等28名甲级战犯正式起诉"
        ),
        "5/4" to listOf("📜 1919年5月4日：五四运动爆发。"),
        "10/1" to listOf("📜 1949年10月1日：中华人民共和国成立"),
        "7/4" to listOf("📜 1776年7月4日：美利坚合众国成立"),
        "9/11" to listOf("📜 2001年9月11日：伊斯兰教极端恐怖主义分子本·拉登对美国世贸中心以及五角大厦进行了恐怖主义打击"),
        "12/30" to listOf("📜 1922年12月30日：苏维埃社会主义共和国联盟正式成立"),
        "12/26" to listOf("📜 1991年12月26日：苏维埃社会主义共和国联盟停止存在")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.date_co)

        datePicker = findViewById(R.id.datePicker)
        selectedDateText = findViewById(R.id.selectedDateText)
        festivalText = findViewById(R.id.festivalText)
        historyText = findViewById(R.id.historyText)

        // 获取当前日期，并设置到DatePicker
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) // 注意：Calendar的月份是0-11
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // 设置DatePicker的初始值为当前日期
        datePicker.init(currentYear, currentMonth, currentDay) { _, year, month, day ->
            // 用户选择日期时的回调
            updateDisplay(year, month, day)
        }

        // 初始显示当前日期对应的信息
        updateDisplay(currentYear, currentMonth, currentDay)
    }

    /**
     * 更新界面显示：根据年月日更新 日期文本、节日、历史上的今天
     * @param year 年份
     * @param month 月份（DatePicker回调中为0-11）
     * @param day 日期（1-31）
     */
    private fun updateDisplay(year: Int, month: Int, day: Int) {
        // month 来自 DatePicker 的回调，范围是 0-11，需要+1转为1-12显示
        val displayMonth = month + 1
        
        // 更新日期显示框
        selectedDateText.text = "📅 已选日期: ${year}年 ${displayMonth}月 ${day}日"

        // 构建键值 "月/日" 用于查找节日和历史上的今天
        val key = "$displayMonth/$day"

        // 更新节日显示框
        val festival = festivalsMap[key]
        val com = comMap[key]
        val mourn = MournMap[key]
        val nd = NationaldayMap[key]
        festivalText.text = if (festival != null) {
            "🎉 $festival"
        } 
        else if (com != null) {
            "⭐ $com"
        }
        else if (mourn != null) {
            "🕯️ $mourn"
        }
        else if (nd != null) {
            "⚫ $nd"
        }
        else {
            "✨ 无特定节日"
        }

        // 更新历史上的今天显示框（支持多个事件）
        val historyList = historyMap[key]
        historyText.text = if (historyList != null) {
            historyList.joinToString("\n")
        } else {
            "📜 历史上的今天：无记录"
        }
    }
}