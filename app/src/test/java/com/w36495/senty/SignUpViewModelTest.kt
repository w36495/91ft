package com.w36495.senty

import com.w36495.senty.domain.repository.AuthRepository
import com.w36495.senty.view.screen.signup.SignUpViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeAuthRepository : AuthRepository {
    override suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        return if (email.contains("success")) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("가입 실패"))
        }
    }
}

class SignUpViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var vm: SignUpViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vm = SignUpViewModel(FakeAuthRepository())
    }

    // 이메일 테스트
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `올바른 이메일은 유효하다`(): Unit = runTest {
        vm.updateEmail("test@example.com")
        advanceUntilIdle()
        assertTrue(vm.isEmailValid.value)
    }

    @Test
    fun `공백으로 이루어진 이메일은 유효하지 않다`(): Unit = runTest {
        vm.updateEmail("")
        advanceUntilIdle()
        assertFalse(vm.isEmailValid.value)
    }

    @Test
    fun `골뱅이가 빠져있다면 이메일은 유효하지 않다`(): Unit = runTest {
        vm.updateEmail("testexample.com")
        advanceUntilIdle()
        assertFalse(vm.isEmailValid.value)
    }

    // 비밀번호 테스트
    @Test
    fun `영소문자와 숫자로 이루어진 8자 이상의 비밀번호는 유효하다`(): Unit = runTest {
        vm.updatePassword("sid3kf82l")
        advanceUntilIdle()
        assertTrue(vm.isPasswordValid.value)
    }

    @Test
    fun `영대문자와 숫자로 이루어진 8자 이상의 비밀번호는 유효하다`(): Unit = runTest {
        vm.updatePassword("A9B438DK")
        advanceUntilIdle()
        assertTrue(vm.isPasswordValid.value)
    }

    @Test
    fun `영문자와 숫자로 이루어진 8자 이상의 비밀번호는 유효하다`(): Unit = runTest {
        vm.updatePassword("as83FdP32")
        advanceUntilIdle()
        assertTrue(vm.isPasswordValid.value)
    }

    @Test
    fun `8자 미만의 비밀번호는 유효하지 않다`(): Unit = runTest {
        vm.updatePassword("cd123")
        advanceUntilIdle()
        assertFalse(vm.isPasswordValid.value)
    }

    @Test
    fun `영문자로만 이루어진 비밀번호는 유효하지 않다`(): Unit = runTest {
        vm.updatePassword("abcdasd")
        advanceUntilIdle()
        assertFalse(vm.isPasswordValid.value)
    }

    @Test
    fun `숫자로만 이루어진 비밀번호는 유효하지 않다`(): Unit = runTest {
        vm.updatePassword("23838452")
        advanceUntilIdle()
        assertFalse(vm.isPasswordValid.value)
    }

    @Test
    fun `공백으로 이루어진 비밀번호는 유효하지 않다`(): Unit = runTest {
        vm.updatePassword("")
        advanceUntilIdle()
        assertFalse(vm.isPasswordValid.value)
    }

    @Test
    fun `특수문자가 존재하는 비밀번호는 유효하지 않다`(): Unit = runTest {
        vm.updatePassword("D08s#@F")
        advanceUntilIdle()
        assertFalse(vm.isPasswordValid.value)
    }

    @Test
    fun `한글이 존재하는 비밀번호는 유효하지 않다`(): Unit = runTest {
        vm.updatePassword("비밀번호1234")
        advanceUntilIdle()
        assertFalse(vm.isPasswordValid.value)
    }

    // 비밀번호 확인 테스트
    @Test
    fun `비밀번호와 비밀번호 확인이 같다면 일치한다`(): Unit = runTest {
        vm.updatePassword("b8sd30fAS")
        vm.updatePasswordConfirm("b8sd30fAS")
        advanceUntilIdle()
        assertTrue(vm.isPasswordMatch.value)
    }

    @Test
    fun `비밀번호와 비밀번호 확인이 다르다면 일치하지 않는다`(): Unit = runTest {
        vm.updatePassword("b8sd30fAS")
        vm.updatePasswordConfirm("b8sd30f")
        advanceUntilIdle()
        assertFalse(vm.isPasswordMatch.value)
    }

    @Test
    fun `비밀번호 확인이 공백이라면 일치하지 않는다`(): Unit = runTest {
        vm.updatePassword("sD23F231")
        vm.updatePasswordConfirm("")
        advanceUntilIdle()
        assertFalse(vm.isPasswordMatch.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}