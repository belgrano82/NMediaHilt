package ru.netology.nmedia.util

import android.app.AlertDialog
import android.content.Context
import androidx.navigation.NavController
import ru.netology.nmedia.R

class LoginDialog(private val navController: NavController) {

    fun showLoginDialog(context: Context) {
        val loginDialog = AlertDialog.Builder(context)

        with(loginDialog) {
            setTitle("Необходимо авторизироваться")
            setMessage("Ввести логин и пароль?")
            setNegativeButton("Отмена", null)
            setPositiveButton("ОК") { dialog, which ->
                // Вызов navigate при нажатии на "PositiveButton"
                navController.navigate(R.id.action_feedFragment_to_loginFragment)
            }
        }

        loginDialog.show()
    }
}