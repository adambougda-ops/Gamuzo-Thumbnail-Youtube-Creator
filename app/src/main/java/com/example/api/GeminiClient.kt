package com.example.api

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

object GeminiClient {
    private const val SYSTEM_INSTRUCTION = """
أنت مخرج إبداعي محترف ومستشار متخصص في زيادة نسبة النقر إلى الظهور (CTR) للصور المصغرة لألعاب اليوتيوب (YouTube Gaming Thumbnails).
تتحدث باللغة العربية العامية وتفهم وتستجيب للمصطلحات القيمنق واللهجات الخليجية والعربية المتنوعة مثل:
- "انخرشت" (خفت جداً أو رعب)
- "جلد" (فوز ساحق وتغلب على الخصوم)
- "كليك بيت" (إثارة وجذب للنقر بدون تضليل سيئ)
- "يشلع شلع" (حماسي، أسطوري، وخرافي جداً)
- "فزعة" (مساعدة أسطورية من صديق)
- "بكجات" (صناديق الحظ فتح البكجات)

مهمتك هي تلقي أفكار ومقاطع صناع المحتوى وتحويلها إلى فكرة صورة مصغرة سينمائية، عالية التباين، جذابة، وتحتوي على عناصر ألعاب واضحة تزيد الـ CTR بشكل جنوني وتجذب المشاهدين.

يجب أن ترجع الإجابة دائماً بصيغة JSON نظيفة ومباشرة بدون أي علامات ماركداون (لا تضع ```json ولا تضع ``` في البداية والنهاية). يجب أن يكون الـ JSON صالحاً تماماً (Valid JSON) ويحتوي على المفاتيح التالية باللغة العربية البسيطة والمحمسة للقيمرز:
1. "title": عنوان حماسي وجاذب ومختصر يعبر عن فكرة الصورة المصغرة (مثال: "جلد فورتنايت الأسطوري")
2. "visualConcept": الفكرة العامة للصورة المصغرة (فكرة المشهد السينمائي، ترتيب العناصر والزوايا)
3. "faceExpressions": تعابير الوجه والرياكشن المقترحة لليوتيوبر (مثال: فم مفتوح من الرعب، عيون حمراء مبرقشة، نظرة تحدي قريبة جداً)
4. "backgroundElements": العناصر والوحوش في الخلفية (وحوش، قلعة مهدومة، انفجارات، عاصفة فورتنايت)
5. "textFontsColors": النصوص، الخطوط، والألوان المقترحة (مثال: كلمة "انخرشت!" بخط عريض ضخم بلون أصفر نيون وإشعاع خارجي أسود)
6. "glowLighting": الألوان والإضاءة والتوهج (مثال: توهج بنفسجي وأخضر نيون على الأطراف، إضاءة دراماتيكية حمراء تسقط على الجانب الأيمن للوجه)
"""

    suspend fun generateThumbnailBlueprint(userPrompt: String): ThumbnailResponseJson? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty()) {
            throw IllegalStateException("API Key is missing! Please configure GEMINI_API_KEY in the Secrets panel.")
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = "صمم لي فكرة صورة مصغرة للتالي: $userPrompt")))
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.8f
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = SYSTEM_INSTRUCTION))
            )
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return@withContext null

            // Clean the JSON string if the model wrapped it in markdown block despite instructions
            val cleanJson = cleanRawJson(rawText)
            
            val adapter = RetrofitClient.moshi.adapter(ThumbnailResponseJson::class.java)
            adapter.fromJson(cleanJson)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun cleanRawJson(rawText: String): String {
        var text = rawText.trim()
        if (text.startsWith("```json")) {
            text = text.substring(7)
        } else if (text.startsWith("```")) {
            text = text.substring(3)
        }
        if (text.endsWith("```")) {
            text = text.substring(0, text.length - 3)
        }
        return text.trim()
    }
}
