package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Member
import com.example.data.MemberRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MemberViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MemberRepository

    val uiState: StateFlow<List<Member>>

    // Event system for UI callbacks (like success/failure alerts)
    private val _eventFlow = MutableSharedFlow<RegistrationEvent>()
    val eventFlow: SharedFlow<RegistrationEvent> = _eventFlow.asSharedFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = MemberRepository(database.memberDao())
        uiState = repository.allMembers.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun registerMember(
        name: String,
        fatherOrHusbandName: String,
        ageStr: String,
        mobileNumber: String,
        address: String,
        district: String,
        occupation: String
    ) {
        viewModelScope.launch {
            if (name.isBlank() || fatherOrHusbandName.isBlank() || mobileNumber.isBlank() ||
                address.isBlank() || district.isBlank() || occupation.isBlank()) {
                _eventFlow.emit(RegistrationEvent.Error("அனைத்து விவரங்களையும் பூர்த்தி செய்க!\n(Please fill all fields!)"))
                return@launch
            }

            val age = ageStr.toIntOrNull()
            if (age == null || age <= 0 || age > 120) {
                _eventFlow.emit(RegistrationEvent.Error("சரியான வயதை உள்ளிடவும்!\n(Please enter a valid age!)"))
                return@launch
            }

            if (mobileNumber.trim().length < 8) {
                _eventFlow.emit(RegistrationEvent.Error("சரியான கைபேசி எண்ணை உள்ளிடவும்!\n(Please enter a valid mobile number!)"))
                return@launch
            }

            val newMember = Member(
                name = name.trim(),
                fatherOrHusbandName = fatherOrHusbandName.trim(),
                age = age,
                mobileNumber = mobileNumber.trim(),
                address = address.trim(),
                district = district.trim(),
                occupation = occupation.trim()
            )

            try {
                repository.insert(newMember)
                _eventFlow.emit(RegistrationEvent.Success)
            } catch (e: Exception) {
                _eventFlow.emit(RegistrationEvent.Error("சேமிப்பதில் பிழை: ${e.localizedMessage}"))
            }
        }
    }

    fun deleteMember(member: Member) {
        viewModelScope.launch {
            repository.delete(member)
        }
    }
}

sealed interface RegistrationEvent {
    object Success : RegistrationEvent
    data class Error(val message: String) : RegistrationEvent
}
