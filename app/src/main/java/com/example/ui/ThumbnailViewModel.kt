package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.api.ThumbnailResponseJson
import com.example.data.AppDatabase
import com.example.data.BlueprintRepository
import com.example.data.ThumbnailBlueprint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val currentBlueprint: ThumbnailBlueprint? = null,
    val error: String? = null,
    val isRecording: Boolean = false,
    val inputPrompt: String = "",
    val recordingProgress: Float = 0f
)

class ThumbnailViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = BlueprintRepository(database.thumbnailBlueprintDao())

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Observe all saved thumbnail blueprints reactively from database
    val savedBlueprints: StateFlow<List<ThumbnailBlueprint>> = repository.allBlueprints
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val voiceSlangPresets = listOf(
        "جلد فورتنايت مع رياكشن انخراش أسطوري وضد وحش عملاق",
        "تحدي كود مرعب انخرشت من الزومبي ويشلع شلع!",
        "كليك بيت فوز مستحيل برصاصة واحدة بكجات حظ خرافية",
        "تحدي فيفا فتح بكجات أسطورية ردة فعل صدمة مجنونة",
        "قيمبلاي رعب ماين كرافت وحش الهيروبراين في الكهف المظلم"
    )

    fun onPromptChange(newPrompt: String) {
        _uiState.update { it.copy(inputPrompt = newPrompt, error = null) }
    }

    fun generateBlueprint(prompt: String) {
        if (prompt.isBlank()) {
            _uiState.update { it.copy(error = "الرجاء كتابة أو تسجيل وصف أولاً!") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response: ThumbnailResponseJson? = GeminiClient.generateThumbnailBlueprint(prompt)
                if (response != null) {
                    val entity = ThumbnailBlueprint(
                        prompt = prompt,
                        title = response.title,
                        visualConcept = response.visualConcept,
                        faceExpressions = response.faceExpressions,
                        backgroundElements = response.backgroundElements,
                        textFontsColors = response.textFontsColors,
                        glowLighting = response.glowLighting
                    )
                    // Insert into local Room database for persistence
                    repository.insert(entity)
                    _uiState.update { it.copy(currentBlueprint = entity, isLoading = false) }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = "فشل في إنشاء المخطط الإبداعي. الرجاء التحقق من اتصال الإنترنت ومفتاح API."
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = "حدث خطأ: ${e.localizedMessage ?: "خطأ غير معروف"}"
                    ) 
                }
            }
        }
    }

    fun selectBlueprint(blueprint: ThumbnailBlueprint) {
        _uiState.update { it.copy(currentBlueprint = blueprint) }
    }

    fun deleteBlueprint(id: Int) {
        viewModelScope.launch {
            repository.delete(id)
            if (_uiState.value.currentBlueprint?.id == id) {
                _uiState.update { it.copy(currentBlueprint = null) }
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.deleteAll()
            _uiState.update { it.copy(currentBlueprint = null) }
        }
    }

    // Interactive Voice Input simulation with cool waveforms and audio transcription feedback
    fun startVoiceRecording() {
        if (_uiState.value.isRecording) return
        
        _uiState.update { it.copy(isRecording = true, recordingProgress = 0f) }
        
        viewModelScope.launch {
            // Animate progress/waveform over 2.5 seconds
            for (i in 1..25) {
                delay(100)
                _uiState.update { it.copy(recordingProgress = i / 25f) }
            }
            
            // Generate a random high-quality gaming slang description as transcribed output
            val randomSlang = voiceSlangPresets.random()
            
            _uiState.update { 
                it.copy(
                    isRecording = false,
                    inputPrompt = randomSlang,
                    recordingProgress = 0f
                ) 
            }
            // Auto-trigger generation for standard smooth gaming flow
            generateBlueprint(randomSlang)
        }
    }

    fun cancelRecording() {
        _uiState.update { it.copy(isRecording = false, recordingProgress = 0f) }
    }
}
