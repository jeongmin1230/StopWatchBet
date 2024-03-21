package com.example.stopwatchbet

import android.content.Context
import android.graphics.fonts.FontStyle
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stopwatchbet.ui.theme.StopWatchBetTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StopWatchBetTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Whole()
                }
            }
        }
    }
}

@Composable
fun Whole() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val players = remember { mutableIntStateOf(2) }
    NavHost(navController = navController, startDestination = context.getString(R.string.nav_1)) {
        composable(context.getString(R.string.nav_1)){
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally) {
                StartScreen(players, context, navController)
            }
        }
        composable(context.getString(R.string.nav_2)) {
            Column(modifier = Modifier.background(Color.White),) {
                GameScreen(players, navController)
            }
        }
    }
}

@Composable
fun StartScreen(players: MutableIntState, context: Context, navController: NavHostController) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.select_players_num),
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center, color = Color.Black),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .border(BorderStroke(1.dp, Color.LightGray))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .height(48.dp)
            ) {
                (2..10).forEach {
                    item {
                        Text(
                            text = it.toString(),
                            style = if(players.intValue == it) MaterialTheme.typography.titleLarge.copy(Color.Black) else MaterialTheme.typography.bodyLarge.copy(Color.LightGray),
                            modifier = Modifier.clickable { players.intValue = it }
                        )
                    }
                }
            }
            Text(text = "${stringResource(id = R.string.players)} ${players.intValue}")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.background(Color.LightGray)) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clickable { navController.navigate(context.getString(R.string.nav_2)) }
            )
        }
    }
}

@Composable
fun GameScreen(players: MutableIntState, navController: NavHostController) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedTime by remember { mutableLongStateOf(0L) }
    var startTime by remember { mutableLongStateOf(0L) }
    var after by remember { mutableIntStateOf(0) }
    val record = remember { mutableMapOf<Int, MutableList<Int>>() }
    var order by remember { mutableIntStateOf(1) }
    val style = if (isRunning) MaterialTheme.typography.bodyLarge.copy(
        textAlign = TextAlign.Center,
        color = Color.White
    )
    else MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center, color = Color.Black)
    LaunchedEffect(isRunning) {
        while (isRunning) {
            val currentTime = System.currentTimeMillis()
            elapsedTime += currentTime - startTime
            startTime = currentTime
            delay(100)
        }
    }
    for (i in 1..players.intValue) {
        record.computeIfAbsent(i) { mutableListOf() }
    }
    Column(Modifier.padding(all = 10.dp)) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Color.DarkGray))
            .weight(0.2f)) {
            Box(modifier = Modifier.background(if (!isRunning) Color.Green else Color.Red)) {
                Text(
                    text = if (!isRunning) stringResource(id = R.string.start_timer) else stringResource(id = R.string.stop_timer),
                    style = style,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                        .clickable {
                            if (!isRunning) {
                                after++
                                startTime = System.currentTimeMillis()
                                isRunning = true
                            } else {
                                isRunning = false
                            }
                        }
                )
                if (after != 0 && !isRunning) {
                    if (record[order]?.size == 2) {
                        after = 0
                        ++order
                    }
                    record[order]?.add(formatTime(elapsedTime).last().digitToInt())
                }
            }
            Text(
                text = formatTime(elapsedTime),
                style = MaterialTheme.typography.headlineLarge.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 60.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 10.dp)
            )
        }
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .weight(0.8f)) {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                record.forEach { (index, ints) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Order(index, Modifier.padding(horizontal = 4.dp))
                        if (ints.isNotEmpty()) {
                            RandomNum(ints[0], Modifier.weight(0.2f))
                            Spacer(modifier = Modifier.width(8.dp))
                            if (ints.size == 2) {
                                RandomNum(ints[1], Modifier.weight(0.2f))
                                Text(
                                    text = "${ints[0] * ints[1]}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center,
                                        color = Color.Red
                                    ),
                                    modifier = Modifier.weight(0.2f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            if(order == players.intValue && record[order]?.size!! >= 2) {
                val sortedEntries = record.entries.sortedByDescending { it.value[0] * it.value[1] }
                Rank(sortedMap = sortedEntries) { navController.popBackStack() }
            }
        }
    }
}


@Composable
fun Order(order: Int, modifier: Modifier) {
    Row(horizontalArrangement = Arrangement.Center) {
        Text(
            text = "${stringResource(id = R.string.player)} $order",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal, fontSize = 20.sp, textAlign = TextAlign.Center, color = Color.DarkGray),
            modifier = modifier
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun RandomNum(num: Int, modifier: Modifier) {
    Row (
        modifier = modifier,
        horizontalArrangement = Arrangement.Center){
        Text(
            text = num.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp,textAlign = TextAlign.Center, color = Color.Black),
        )
    }
}

@Composable
fun Rank(sortedMap: List<MutableMap.MutableEntry<Int, MutableList<Int>>>, onClickBack: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Divider()
        Text(text = stringResource(id = R.string.result))
        sortedMap.forEachIndexed { index, mutableEntry ->
            val color = if(index == 0) Color.Blue else if(index == sortedMap.lastIndex) Color.Red else Color.DarkGray
            Row {
                Text(
                    text = "${index+1} ${stringResource(id = R.string.rank)}",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp, color = color)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${stringResource(id = R.string.player)} ${mutableEntry.key}",
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp, color = color)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier
            .background(Color.LightGray)
            .clickable { onClickBack() }) {
            Text(
                text = stringResource(id = R.string.retry),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Black),
                modifier = Modifier.padding(all = 8.dp)
            )
        }
    }
}

@Composable
fun formatTime(milliseconds: Long): String {
    val minutes = milliseconds / 60000
    val seconds = milliseconds / 1000 % 60
    val millis = milliseconds % 100

    return String.format("%02d:%02d.%02d", minutes, seconds, millis)
}