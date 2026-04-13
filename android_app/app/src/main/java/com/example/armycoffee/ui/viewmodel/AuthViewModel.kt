package com.example.armycoffee.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.armycoffee.data.api.RetrofitClient
import com.example.armycoffee.data.model.User
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    var currentUser by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)

    fun login(username: String, password: String, onLoginSuccess: (User) -> Unit) {
        isLoading = true
        loginError = null
        if (username.isBlank() || password.isBlank()) {
            loginError = "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu"
            isLoading = false
            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.login(
                    mapOf("username" to username, "password" to password)
                )

                // Đăng nhập thành công
                val user = response.user
                if (user != null) {
                    currentUser = user
                    onLoginSuccess(user)
                } else {
                    loginError = "Lỗi: Không tìm thấy thông tin người dùng"
                }
            } catch (e: Exception) {
                loginError = "Đăng nhập thất bại: Sai tài khoản hoặc Server chưa chạy"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        currentUser = null
    }

    fun register(fullName: String, username: String, password: String, onRegisterSuccess: () -> Unit) {
        isLoading = true
        loginError = null
        viewModelScope.launch {
            try {
                // Trong thực tế sẽ gọi API:
                // RetrofitClient.instance.register(mapOf(
                //     "full_name" to fullName,
                //     "username" to username,
                //     "password" to password
                // ))

                // Giả lập
                kotlinx.coroutines.delay(1000)
                onRegisterSuccess()
            } catch (e: Exception) {
                loginError = "Đăng ký thất bại: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
