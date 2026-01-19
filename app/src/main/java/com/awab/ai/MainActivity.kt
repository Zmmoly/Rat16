package com.awab.ai

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var chatContainer: LinearLayout
    private lateinit var inputField: EditText
    private lateinit var scrollView: ScrollView
    private lateinit var rootLayout: LinearLayout
    private lateinit var commandHandler: CommandHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize command handler
        commandHandler = CommandHandler(this)
        
        // Hide action bar
        supportActionBar?.hide()
        
        // Root layout
        rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0xFFF0F2F5.toInt())
            fitsSystemWindows = true
        }

        // Chat area (scrollable)
        scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            isScrollbarFadingEnabled = false
        }

        chatContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 48, 16, 16)
        }

        scrollView.addView(chatContainer)
        rootLayout.addView(scrollView)

        // Input area
        val inputArea = createInputArea()
        rootLayout.addView(inputArea)

        setContentView(rootLayout)
        
        // Handle keyboard insets
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            view.setPadding(
                systemInsets.left,
                systemInsets.top,
                systemInsets.right,
                imeInsets.bottom
            )
            
            // Scroll to bottom when keyboard appears
            if (imeInsets.bottom > 0) {
                rootLayout.post {
                    scrollToBottom()
                }
            }
            
            WindowInsetsCompat.CONSUMED
        }

        // Welcome message
        addBotMessage("مرحباً! أنا أواب AI 🤖\n\nكيف يمكني مساعدتك اليوم؟")
    }

    private fun createInputArea(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundColor(0xFFFFFFFF.toInt())
            setPadding(16, 16, 16, 16)
            gravity = Gravity.CENTER_VERTICAL

            inputField = EditText(this@MainActivity).apply {
                hint = "اكتب رسالتك هنا..."
                textSize = 16f
                setPadding(20, 16, 20, 16)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                background = createRoundedBackground(0xFFF0F2F5.toInt(), 24f)
            }
            addView(inputField)

            val sendText = TextView(this@MainActivity).apply {
                text = "➤"
                textSize = 28f
                setTextColor(0xFF075E54.toInt())
                setPadding(16, 0, 0, 0)
                setOnClickListener {
                    sendMessage()
                }
            }
            addView(sendText)
            
            val settingsIcon = TextView(this@MainActivity).apply {
                text = "⚙️"
                textSize = 24f
                setTextColor(0xFF075E54.toInt())
                setPadding(16, 0, 0, 0)
                setOnClickListener {
                    openSettings()
                }
            }
            addView(settingsIcon)
        }
    }

    private fun sendMessage() {
        val message = inputField.text.toString().trim()
        if (message.isEmpty()) return

        addUserMessage(message)
        inputField.text.clear()

        android.os.Handler(mainLooper).postDelayed({
            handleBotResponse(message)
        }, 500)
    }

    private fun handleBotResponse(userMessage: String) {
        // أولاً: جرب الأوامر
        val commandResponse = commandHandler.handleCommand(userMessage)
        
        // إذا كان الأمر معروف، أرسل الرد
        if (commandResponse != null && !commandResponse.contains("لم أفهم الأمر")) {
            addBotMessage(commandResponse)
            return
        }
        
        // ثانياً: الردود العادية
        val response = when {
            userMessage.contains("مرحبا", ignoreCase = true) || 
            userMessage.contains("السلام", ignoreCase = true) ||
            userMessage.contains("هلا", ignoreCase = true) -> {
                "مرحباً بك! 👋\n\nأنا مساعدك الذكي. يمكنني:\n\n📱 فتح أي تطبيق:\n• افتح [اسم أي تطبيق]\n• اعرض التطبيقات (لرؤية القائمة)\n\n📞 الاتصال بجهات الاتصال:\n• اتصل أحمد\n• اتصل بأحمد\n• اضرب لأحمد\n• اتصل 0501234567\n\n⚙️ التحكم الكامل:\n• شغل الواي فاي\n• سكرين شوت\n• أقفل التطبيق\n\nجرب أي أمر!"
            }
            userMessage.contains("أذونات", ignoreCase = true) || 
            userMessage.contains("صلاحيات", ignoreCase = true) ||
            userMessage.contains("permission", ignoreCase = true) -> {
                "لإدارة الأذونات، اضغط على زر الإعدادات ⚙️ في الأسفل.\n\nهناك يمكنك:\n✓ طلب الأذونات العادية\n✓ الأذونات الخاصة\n✓ إمكانية الوصول"
            }
            userMessage.contains("كيف", ignoreCase = true) || 
            userMessage.contains("ساعد", ignoreCase = true) ||
            userMessage.contains("help", ignoreCase = true) ||
            userMessage.contains("أوامر", ignoreCase = true) -> {
                "📋 الأوامر المتاحة:\n\n📱 التطبيقات:\n• افتح [اسم أي تطبيق]\n• أقفل [اسم التطبيق] ⭐\n• اعرض التطبيقات\n\n📞 الاتصال:\n• اتصل [اسم أو رقم]\n• اتصل ب[اسم]\n• اضرب ل[اسم]\n• كلم [اسم]\n\n⚙️ الإعدادات:\n• شغل الواي فاي ⭐\n• شغل البلوتوث ⭐\n• رجوع / هوم ⭐\n\n🔊 الصوت:\n• على الصوت\n• خفض الصوت\n\n📸 أخرى:\n• سكرين شوت ⭐\n• اقرا الشاشة ⭐\n• اضغط على \"نص\" ⭐\n\n⭐ = يحتاج Accessibility"
            }
            userMessage.contains("إعدادات", ignoreCase = true) || 
            userMessage.contains("settings", ignoreCase = true) -> {
                openSettings()
                "سأفتح لك صفحة الإعدادات..."
            }
            else -> {
                "لم أفهم 🤔\n\nجرب:\n• \"أوامر\" - لرؤية كل الأوامر\n• \"افتح واتساب\"\n• \"شغل الواي فاي\"\n• \"على الصوت\""
            }
        }

        addBotMessage(response)
    }

    private fun addUserMessage(message: String) {
        val messageView = createMessageBubble(message, isUser = true)
        chatContainer.addView(messageView)
        scrollToBottom()
    }

    private fun addBotMessage(message: String) {
        val messageView = createMessageBubble(message, isUser = false)
        chatContainer.addView(messageView)
        scrollToBottom()
    }

    private fun createMessageBubble(message: String, isUser: Boolean): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
            gravity = if (isUser) Gravity.END else Gravity.START

            val bubble = TextView(this@MainActivity).apply {
                text = message
                textSize = 16f
                setPadding(20, 16, 20, 16)
                setTextColor(if (isUser) 0xFFFFFFFF.toInt() else 0xFF000000.toInt())
                background = createRoundedBackground(
                    if (isUser) 0xFF075E54.toInt() else 0xFFFFFFFF.toInt(),
                    16f
                )
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                maxWidth = (resources.displayMetrics.widthPixels * 0.75).toInt()
            }
            addView(bubble)
        }
    }

    private fun createRoundedBackground(color: Int, radius: Float): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius
        }
    }

    private fun scrollToBottom() {
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}
