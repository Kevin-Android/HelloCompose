package com.kevin.hellocompose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kevin.hellocompose.ui.theme.HelloComposeTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloComposeTheme() {
                ComposeView(
                    onNsdClicked = {
                        startActivity(Intent(this@MainActivity, NsdActivity::class.java))
                    }
                )
            }
        }
    }

}

//  导航label数组
val labels = arrayOf("首页", "发现", "我的")

//  导航选中索引
var selectIndex by mutableStateOf(0)


@Preview
@Composable
fun PreviewComposeView() {
    ComposeView {}
}


@VisibleForTesting
@Composable
fun ComposeView(
    onNsdClicked: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(labels[selectIndex]) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            Log.i(TAG, "是否展开$drawerState.isOpen")
                            if (drawerState.isOpen) {
                                scope.launch { drawerState.close() }
                            } else {
                                scope.launch { drawerState.open() }
                            }
                        },
                    ) {
                        Icon(Icons.Rounded.Menu, contentDescription = null)
                    }
                },
                actions = {
                    // RowScope 在这里，所以这些图标会被水平放置
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Rounded.Search, contentDescription = "搜索")
                    }
                    // RowScope 在这里，所以这些图标会被水平放置
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(Icons.Rounded.MoreVert, contentDescription = "搜索")
                    }

                }
            )
        },
        bottomBar = {
            BuildBottomBar(labels = labels, navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNsdClicked
            ) {
                Icon(Icons.Filled.Favorite, contentDescription = "Localized description")
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = if (drawerState.isClosed) ">>> Swipe >>>" else "<<< Swipe <<<")
                Spacer(Modifier.height(20.dp))
                Divider()
                LazyColumn {
                    // Add a single item
                    item (key = 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 15.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Send,
                                contentDescription = null
                            )
                            Text("帮助",
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )


                        }
                    }
                    item (key = 2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 15.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Send,
                                contentDescription = null
                            )
                            Text("帮助",
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )


                        }
                    }
                }

            }
//            ModalDrawer(
//                drawerState = drawerState,
//                drawerContent = {
//                    Button(
//                        onClick = { scope.launch { drawerState.close() } },
//                        content = { Text("Close Drawer") }
//                    )
//                },
//                content = {

//                }
//            )
        },
        scaffoldState = rememberScaffoldState(drawerState = drawerState),


        ) {
        NavHost(
            navController = navController,
            startDestination = labels[0]
        ) {
            composable(labels[0]) {
                FirstScreen()
            }
            composable(labels[1]) {
                SecondScreen()
            }
            composable(labels[2]) {
                ThirdScreen()
            }
        }

    }
}

@Preview
@Composable
fun PreviewBuildBottomBar() {
    val navController = rememberNavController()
    BuildBottomBar(labels = labels, navController)
}

@Composable
fun BuildBottomBar(
    labels: Array<String>,
    navController: NavHostController
) {
    BottomAppBar(
        cutoutShape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50))
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = null) },
            label = { Text(labels[0]) },
            selected = selectIndex == 0,
            onClick = {
                selectIndex = 0
                navController.navigate(labels[selectIndex])
            }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Rounded.Person, contentDescription = null) },
            label = { Text(labels[2]) },
            selected = selectIndex == 2,
            onClick = {
                selectIndex = 2
                navController.navigate(labels[selectIndex])
            }
        )

    }
}


@Composable
fun FirstScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = labels[0],
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxHeight()
                .padding(24.dp)
                .clickable(onClick = {
                })
        )
    }
}


@Composable
fun SecondScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = labels[1],
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxHeight()
                .padding(24.dp)
                .clickable(onClick = {
                })
        )
    }
}


@Composable
fun ThirdScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = labels[2],
            style = TextStyle(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxHeight()
                .padding(24.dp)
                .clickable(onClick = {
                })
        )
    }
}













