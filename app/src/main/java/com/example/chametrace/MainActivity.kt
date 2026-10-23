package com.example.chametrace

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import org.json.JSONArray

class MainActivity : Activity() {
    private val winners = mutableListOf<String>()
    private lateinit var history: TextView
    private lateinit var stats: TextView
    private lateinit var choices: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "محلل سباق محضون"

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.WHITE)
        }

        fun addText(text: String, size: Float) {
            layout.addView(TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                setPadding(8, 12, 8, 12)
            })
        }

        addText("محلل سباق محضون", 24f)
        addText("سجّل الفائز بكل جولة لمراجعة النتائج السابقة", 16f)

        choices = Spinner(this)
        val options = listOf("السيارة 1", "السيارة 2", "السيارة 3",
            "السيارة 4", "الدراجة النارية", "غير ذلك")
        choices.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, options
        )
        layout.addView(choices)

        val save = Button(this).apply {
            text = "تسجيل الفائز"
            setOnClickListener {
                winners.add(choices.selectedItem.toString())
                refresh()
                getSharedPreferences("race", MODE_PRIVATE).edit()
                    .putString("winners", JSONArray(winners).toString()).apply()
                Toast.makeText(this@MainActivity, "تم تسجيل النتيجة", Toast.LENGTH_SHORT).show()
            }
        }
        layout.addView(save)

        stats = TextView(this).apply {
            textSize = 16f
            setTextColor(Color.BLACK)
            setPadding(8, 16, 8, 16)
        }
        layout.addView(stats)

        history = TextView(this).apply {
            textSize = 14f
            setTextColor(Color.DKGRAY)
        }
        layout.addView(history)

        val clear = Button(this).apply {
            text = "مسح السجل"
            setOnClickListener {
                winners.clear()
                getSharedPreferences("race", MODE_PRIVATE).edit().remove("winners").apply()
                refresh()
            }
        }
        layout.addView(clear)

        val saved = getSharedPreferences("race", MODE_PRIVATE)
            .getString("winners", "[]")
        try {
            val array = JSONArray(saved)
            for (i in 0 until array.length()) winners.add(array.getString(i))
        } catch (_: Exception) { }

        setContentView(ScrollView(this).apply { addView(layout) })
        refresh()
    }

    private fun refresh() {
        if (!::stats.isInitialized || !::history.isInitialized) return
        stats.text = "عدد الجولات المسجلة: ${winners.size}\n" +
            winners.distinct().joinToString("\n") {
                "$it: ${winners.count { w -> w == it }} مرة"
            }
        history.text = "\nآخر النتائج:\n" +
            winners.takeLast(20).reversed().joinToString("\n")
    }
}
