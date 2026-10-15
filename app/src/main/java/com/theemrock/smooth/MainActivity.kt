package com.theemrock.smooth

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.theemrock.smooth.ui.theme.SmoothTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmoothTheme {
                SmoothApp()
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Placeholder : Screen("placeholder/{moduleName}") {
        fun createRoute(moduleName: String) = "placeholder/$moduleName"
    }
    object Settings : Screen("settings")
}

data class ModuleItem(val id: String, val titleResId: Int)

fun smoothModules() = ModuleRegistry.modules.map {
    ModuleItem(it.id, it.titleResId)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmoothApp() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var moduleStatuses by remember {
        mutableStateOf<Map<String, ModuleStatus>>(emptyMap())
    }

    DisposableEffect(context, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                moduleStatuses = ModuleRegistry.modules.associate { module ->
                    module.id to ModuleRegistry.getStatus(context, module)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    var currentRoute by rememberSaveable { mutableStateOf(Screen.Home.route) }
    var selectedModuleId by rememberSaveable { mutableStateOf("") }


    val modules = smoothModules()
    val selectedModule = modules.firstOrNull { it.id == selectedModuleId }
    val navigateHome = {
        currentRoute = Screen.Home.route
        selectedModuleId = ""
    }

    BackHandler(enabled = currentRoute != Screen.Home.route, onBack = navigateHome)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            currentRoute == Screen.Home.route -> stringResource(R.string.app_name)
                            currentRoute == Screen.Settings.route -> stringResource(R.string.settings_title)
                            else -> selectedModule?.let { stringResource(it.titleResId) }
                                ?: stringResource(R.string.app_name)
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (currentRoute == Screen.Home.route) {
                        IconButton(
                            onClick = { currentRoute = Screen.Settings.route },
                            modifier = Modifier.testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings_title)
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (currentRoute != Screen.Home.route) {
                        IconButton(onClick = navigateHome) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                currentRoute == Screen.Home.route -> {
                    HomeScreen(
                        modules = modules,
                        statuses = moduleStatuses,
                        onModuleClick = { module ->
                            val definition = ModuleRegistry.modules.firstOrNull { it.id == module.id }
                            val result = if (definition == null) {
                                LaunchResult.NOT_CONFIGURED
                            } else {
                                ModuleLauncher.launch(context, definition)
                            }
                            if (result != LaunchResult.LAUNCHED) {
                                val message = when (result) {
                                    LaunchResult.NOT_INSTALLED -> "Module not installed"
                                    LaunchResult.NOT_CONFIGURED -> "Module not configured"
                                    LaunchResult.NOT_LAUNCHABLE -> "Module cannot be launched"
                                    LaunchResult.FAILED -> "Module launch failed"
                                    LaunchResult.LAUNCHED -> ""
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
                currentRoute == Screen.Settings.route -> {
                    SettingsScreen()
                }
                currentRoute.startsWith("placeholder/") -> {
                    selectedModule?.let { PlaceholderScreen(module = it) }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    modules: List<ModuleItem>,
    statuses: Map<String, ModuleStatus>,
    onModuleClick: (ModuleItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("module_list")
        ) {
            items(modules) { module ->
                ModuleCard(
                    module = module,
                    status = statuses[module.id] ?: ModuleStatus.NOT_CONFIGURED,
                    onClick = { onModuleClick(module) }
                )
            }
        }
    }
}

@Composable
fun ModuleCard(
    module: ModuleItem,
    status: ModuleStatus,
    onClick: () -> Unit
) {
    val statusText = when (status) {
        ModuleStatus.INSTALLED -> "Installed"
        ModuleStatus.NOT_INSTALLED -> "Not installed"
        ModuleStatus.NOT_CONFIGURED -> "Not configured"
        ModuleStatus.NOT_LAUNCHABLE -> "Not launchable"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .testTag("module_${module.id}")
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(module.titleResId),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
fun PlaceholderScreen(module: ModuleItem) {
    val moduleName = stringResource(module.titleResId)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = moduleName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.placeholder_desc, moduleName),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SettingsScreen() {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.app_version_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = BuildConfig.VERSION_NAME,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.check_updates),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.check_updates)) },
            text = { Text(stringResource(R.string.update_not_implemented)) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.dialog_ok))
                }
            }
        )
    }
}
