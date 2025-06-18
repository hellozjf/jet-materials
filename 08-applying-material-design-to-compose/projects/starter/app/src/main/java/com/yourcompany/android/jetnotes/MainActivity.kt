/*
 * Copyright (c) 2021 Kodeco Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.yourcompany.android.jetnotes

import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.LiveData
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.yourcompany.android.jetnotes.domain.model.ColorModel
import com.yourcompany.android.jetnotes.routing.Screen
import com.yourcompany.android.jetnotes.theme.JetNotesTheme
import com.yourcompany.android.jetnotes.ui.components.AppDrawer
import com.yourcompany.android.jetnotes.ui.screens.NotesScreen
import com.yourcompany.android.jetnotes.ui.screens.SaveNoteScreen
import com.yourcompany.android.jetnotes.ui.screens.TrashScreen
import com.yourcompany.android.jetnotes.viewmodel.MainViewModel
import com.yourcompany.android.jetnotes.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

/**
 * Main activity for the app.
 */
class MainActivity : AppCompatActivity() {

  private val viewModel: MainViewModel by viewModels(factoryProducer = {
    MainViewModelFactory(
      this,
      (application as JetNotesApplication).dependencyInjector.repository
    )
  })

  @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.Theme_JetNotes)

    super.onCreate(savedInstanceState)

    setContent {
      JetNotesTheme {
        val coroutineScope = rememberCoroutineScope()
        val scaffoldState: ScaffoldState = rememberScaffoldState()
        val navController = rememberNavController()
        val navBackStateEntry by navController.currentBackStackEntryAsState()

        Scaffold(
          scaffoldState = scaffoldState,
          drawerContent = {
            AppDrawer(
              currentScreen = Screen.fromRoute(
                navBackStateEntry?.destination?.route
              ),
              onScreenSelected = { screen ->
                navController.navigate(screen.route) {
                  // 弹出窗口以直接跳转至目标界面，从而避免每次选择屏幕时都重新构建层级结构
                  // 假如起始页面是 A
                  // 当前栈是：A
                  // 跳转到 B 之后，当前栈是：A -> B
                  // 跳转到 C 之后，当前栈是：A -> C，因为 popUpTo 只保留了 A
                  popUpTo(
                    navController.graph.findStartDestination().id
                  ) {
                    // 这个参数的意思是，如果页面被弹出了，把该页面的状态保存下来
                    saveState = true
                  }

                  // 防止同一目的地的重复复制以及同一屏幕的重复复制
                  // 假如当前栈是：A -> B
                  // 跳转到 A 之后，当前栈是：A，不会变成 A -> A
                  launchSingleTop = true

                  // 在选择之前选中的屏幕时恢复其状态
                  // 这个参数的意思是，如果页面被弹出前记录过状态，那么就恢复状态
                  restoreState = true
                }
                coroutineScope.launch {
                  scaffoldState.drawerState.close()
                }
              }
            )
          },
          content = {
            MainActivityScreen(
              navController = navController,
              viewModel = viewModel,
              openNavigationDrawer = {
                coroutineScope.launch {
                  scaffoldState.drawerState.open()
                }
              }
            )
          }
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainActivityScreen(
  navController: NavHostController,
  viewModel: MainViewModel,
  openNavigationDrawer: () -> Unit
) {
  NavHost(
    navController = navController,
    startDestination = Screen.Notes.route
  ) {
    composable(Screen.Notes.route) {
      NotesScreen(
        viewModel = viewModel,
        onOpenNavigationDrawer = openNavigationDrawer,
        onNavigateToSaveNote = {
          navController.navigate(Screen.SaveNote.route)
        }
      )
    }
    composable(Screen.SaveNote.route) {
      SaveNoteScreen(
        viewModel = viewModel,
        onNavigateBack = {
          navController.popBackStack()
        }
      )
    }
    composable(Screen.Trash.route) {
      TrashScreen(
        viewModel = viewModel,
        onTopBarNavigationIconClicked = openNavigationDrawer
      )
    }
  }
}