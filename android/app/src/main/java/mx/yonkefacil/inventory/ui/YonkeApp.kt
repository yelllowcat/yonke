package mx.yonkefacil.inventory.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import mx.yonkefacil.inventory.LabelPdfExporter
import mx.yonkefacil.inventory.data.AppLanguage
import mx.yonkefacil.inventory.data.InventoryRepository
import mx.yonkefacil.inventory.data.InventoryStatus
import mx.yonkefacil.inventory.data.NewPart
import mx.yonkefacil.inventory.data.NewVehicle
import mx.yonkefacil.inventory.data.Part
import mx.yonkefacil.inventory.data.PartCondition
import mx.yonkefacil.inventory.data.SaveResult
import mx.yonkefacil.inventory.data.StaffRole
import mx.yonkefacil.inventory.data.StaffUser
import mx.yonkefacil.inventory.data.Vehicle
import mx.yonkefacil.inventory.ui.theme.Navy
import mx.yonkefacil.inventory.ui.theme.Paper
import mx.yonkefacil.inventory.ui.theme.SafetyYellow
import mx.yonkefacil.inventory.ui.theme.Sky
import mx.yonkefacil.inventory.ui.theme.Success
import java.text.Normalizer
import java.io.File
import java.io.FileOutputStream
import android.graphics.BitmapFactory

private enum class Screen { Login, Home, ReceiveVehicle, AddPart, Search, Sell, Owner }

@Composable
fun YonkeApp(repository: InventoryRepository) {
    var languageName by rememberSaveable { mutableStateOf(AppLanguage.Spanish.name) }
    val language = AppLanguage.valueOf(languageName)
    val copy = Copy(language)
    var screenName by rememberSaveable { mutableStateOf(Screen.Login.name) }
    val screen = Screen.valueOf(screenName)
    var currentUser by remember { mutableStateOf<StaffUser?>(null) }
    val vehicles by repository.vehicles.collectAsState()
    val parts by repository.parts.collectAsState()
    val staff by repository.staff.collectAsState()
    val enrolled by repository.isEnrolled.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    val toggleLanguage = {
        languageName = if (language == AppLanguage.Spanish) AppLanguage.English.name else AppLanguage.Spanish.name
    }
    val goHome = { screenName = Screen.Home.name }

    Scaffold(
        containerColor = Paper,
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (screen) {
                Screen.Login -> LoginScreen(
                    copy = copy,
                    language = language,
                    staff = staff,
                    enrolled = enrolled,
                    onLanguage = toggleLanguage,
                    onEnroll = repository::enrollDevice,
                    onSignIn = { staffId, pin ->
                        repository.signIn(staffId, pin)?.also {
                            currentUser = it
                            screenName = Screen.Home.name
                        }
                    },
                )
                Screen.Home -> HomeScreen(
                    copy = copy,
                    language = language,
                    user = currentUser ?: return@Scaffold,
                    onLanguage = toggleLanguage,
                    onNavigate = { screenName = it.name },
                    onSignOut = { currentUser = null; screenName = Screen.Login.name },
                )
                Screen.ReceiveVehicle -> ReceiveVehicleScreen(copy, repository, goHome, snackbar)
                Screen.AddPart -> AddPartScreen(copy, repository, vehicles, goHome, snackbar)
                Screen.Search -> SearchScreen(copy, language, vehicles, parts, goHome)
                Screen.Sell -> SellScreen(copy, language, currentUser ?: return@Scaffold, repository, vehicles, parts, goHome, snackbar)
                Screen.Owner -> OwnerScreen(copy, language, repository, vehicles, parts, goHome, snackbar)
            }
        }
    }
}

@Composable
private fun LoginScreen(
    copy: Copy,
    language: AppLanguage,
    staff: List<StaffUser>,
    enrolled: Boolean,
    onLanguage: () -> Unit,
    onEnroll: suspend (String, String) -> SaveResult<Unit>,
    onSignIn: suspend (String, String) -> StaffUser?,
) {
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    var pin by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    var enrollmentCode by rememberSaveable { mutableStateOf("") }
    var deviceName by rememberSaveable { mutableStateOf("") }
    var busy by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(copy.appName, trailing = { LanguageButton(language, onLanguage) })
        LazyColumn(
            modifier = Modifier.fillMaxSize().imePadding(),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Navy, modifier = Modifier.size(56.dp))
                Spacer(Modifier.height(14.dp))
                Text(
                    if (!enrolled) copy.enrollPhone else if (selected == null) copy.choosePerson else copy.enterPin,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(7.dp))
                if (!enrolled) HelperBanner(copy.enrollmentStep)
                else if (selected == null) Text(copy.choosePersonHelp, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                else Text(copy.pinHelp, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!enrolled) {
                item {
                    SectionCard(copy.enrollPhone) {
                        OutlinedTextField(
                            enrollmentCode,
                            { if (it.length <= 6 && it.all(Char::isDigit)) { enrollmentCode = it; error = false } },
                            Modifier.fillMaxWidth(),
                            label = { Text(copy.enrollmentCode) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            isError = error,
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(deviceName, { deviceName = it }, Modifier.fillMaxWidth(), label = { Text(copy.deviceName) }, placeholder = { Text(copy.deviceExample) }, singleLine = true)
                        if (error) ErrorBanner(copy.enrollmentFailed)
                        Spacer(Modifier.height(16.dp))
                        PrimaryButton(copy.connect, enabled = enrollmentCode.length == 6 && deviceName.length >= 2 && !busy, onClick = {
                            busy = true
                            scope.launch {
                                error = onEnroll(enrollmentCode, deviceName) !is SaveResult.Saved
                                busy = false
                            }
                        })
                    }
                }
            } else if (staff.isEmpty()) {
                item {
                    Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp), color = Sky)
                        Spacer(Modifier.width(12.dp))
                        Text(copy.loadingStaff)
                    }
                }
            } else if (selected == null) {
                items(staff) { member ->
                    Card(
                        modifier = Modifier.fillMaxWidth().height(98.dp).clickable(role = Role.Button) { selected = member.id },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    ) {
                        Row(Modifier.fillMaxSize().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = SafetyYellow, shape = RoundedCornerShape(40.dp), modifier = Modifier.size(58.dp)) {
                                Box(contentAlignment = Alignment.Center) { Text(member.name.take(1), fontWeight = FontWeight.ExtraBold) }
                            }
                            Spacer(Modifier.width(17.dp))
                            Column(Modifier.weight(1f)) {
                                Text(member.name, style = MaterialTheme.typography.titleLarge)
                                if (member.role == StaffRole.Owner) {
                                    Spacer(Modifier.height(3.dp))
                                    StatusPill(copy.owner, positive = true)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Sky, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            } else {
                item {
                    val member = staff.first { it.id == selected }
                    Text(member.name, style = MaterialTheme.typography.titleLarge, color = Sky)
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { value -> if (value.length <= 4 && value.all(Char::isDigit)) { pin = value; error = false } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("PIN") },
                        singleLine = true,
                        isError = error,
                        supportingText = if (error) ({ Text(copy.wrongPin) }) else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                    )
                    Spacer(Modifier.height(18.dp))
                    PrimaryButton(copy.signIn, enabled = pin.length == 4, onClick = {
                        busy = true
                        scope.launch {
                            if (onSignIn(selected!!, pin) == null) { error = true; pin = "" }
                            busy = false
                        }
                    })
                    Spacer(Modifier.height(8.dp))
                    SecondaryButton(copy.back, onClick = { selected = null; pin = ""; error = false })
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(
    copy: Copy,
    language: AppLanguage,
    user: StaffUser,
    onLanguage: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onSignOut: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(copy.appName, trailing = { LanguageButton(language, onLanguage) })
        SavedBanner(copy)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text("${copy.hello}, ${user.name}", color = Sky, style = MaterialTheme.typography.titleMedium)
                Text(copy.whatDo, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(top = 3.dp, bottom = 10.dp).semantics { heading() })
                HelperBanner(copy.homeTip)
            }
            item { ActionCard(copy.receiveVehicle, copy.receiveHelp, actionIcon(0), SafetyYellow) { onNavigate(Screen.ReceiveVehicle) } }
            item { ActionCard(copy.addPart, copy.addPartHelp, actionIcon(1), Color(0xFFBFD9EE)) { onNavigate(Screen.AddPart) } }
            item { ActionCard(copy.search, copy.searchHelp, actionIcon(2), Color(0xFFD6EBDD)) { onNavigate(Screen.Search) } }
            item { ActionCard(copy.sell, copy.sellHelp, actionIcon(3), Color(0xFFFFD8D4)) { onNavigate(Screen.Sell) } }
            if (user.role == StaffRole.Owner) {
                item {
                    SecondaryButton(copy.ownerTools, onClick = { onNavigate(Screen.Owner) }, icon = Icons.Default.AdminPanelSettings)
                }
            }
            item {
                TextButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth().height(54.dp)) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(copy.signOut)
                }
            }
        }
    }
}

@Composable
private fun ReceiveVehicleScreen(
    copy: Copy,
    repository: InventoryRepository,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    var year by rememberSaveable { mutableStateOf("") }
    var make by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var vin by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(copy.receiveVehicle, onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().imePadding(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text(copy.vehicleDetails, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
                Spacer(Modifier.height(4.dp))
                HelperBanner(copy.vehicleTip)
            }
            item {
                SectionCard(copy.vehicleDetails) {
                    OutlinedTextField(year, { if (it.length <= 4 && it.all(Char::isDigit)) { year = it; error = false } }, Modifier.fillMaxWidth(), label = { Text(copy.year) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, isError = error && year.isBlank())
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(make, { make = it; error = false }, Modifier.fillMaxWidth(), label = { Text(copy.make) }, singleLine = true, isError = error && make.isBlank())
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(model, { model = it; error = false }, Modifier.fillMaxWidth(), label = { Text(copy.model) }, singleLine = true, isError = error && model.isBlank())
                    HorizontalDivider(Modifier.padding(vertical = 15.dp))
                    OutlinedTextField(color, { color = it }, Modifier.fillMaxWidth(), label = { Text(copy.color) }, singleLine = true)
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(vin, { vin = it.uppercase() }, Modifier.fillMaxWidth(), label = { Text(copy.vin) }, singleLine = true)
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(location, { location = it }, Modifier.fillMaxWidth(), label = { Text(copy.location) }, placeholder = { Text(copy.locationExample) }, minLines = 2)
                }
            }
            if (error) item { ErrorBanner(copy.requiredVehicle) }
            item {
                PrimaryButton(copy.save, icon = Icons.Default.DirectionsCar, onClick = {
                    val numericYear = year.toIntOrNull()
                    if (numericYear == null || make.isBlank() || model.isBlank()) error = true
                    else scope.launch {
                        when (repository.addVehicle(NewVehicle(numericYear, make, model, color, vin, location))) {
                            is SaveResult.Saved -> { snackbar.showSnackbar(copy.saved); onBack() }
                            SaveResult.Queued -> { snackbar.showSnackbar(copy.queued); onBack() }
                            is SaveResult.Failed -> { error = true; snackbar.showSnackbar(copy.somethingWrong) }
                        }
                    }
                })
            }
        }
    }
}

@Composable
private fun AddPartScreen(
    copy: Copy,
    repository: InventoryRepository,
    vehicles: List<Vehicle>,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    var vehicleId by rememberSaveable { mutableStateOf(vehicles.firstOrNull()?.id ?: "") }
    var nameEs by rememberSaveable { mutableStateOf("") }
    var nameEn by rememberSaveable { mutableStateOf("") }
    var categoryEs by rememberSaveable { mutableStateOf("") }
    var categoryEn by rememberSaveable { mutableStateOf("") }
    var conditionName by rememberSaveable { mutableStateOf(PartCondition.Untested.name) }
    val condition = PartCondition.valueOf(conditionName)
    var location by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var published by rememberSaveable { mutableStateOf(false) }
    var hasPhoto by rememberSaveable { mutableStateOf(false) }
    var photoPath by rememberSaveable { mutableStateOf<String?>(null) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val directory = File(context.cacheDir, "part-photos").apply { mkdirs() }
            val file = File(directory, "${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 86, it) }
            photoPath = file.absolutePath
            hasPhoto = true
        }
    }
    val photoBitmap = remember(photoPath) { photoPath?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() } }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(copy.addPart, onBack)
        if (vehicles.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(58.dp), tint = Sky)
                    Spacer(Modifier.height(12.dp))
                    Text(copy.noVehicles, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(20.dp))
                    PrimaryButton(copy.back, onBack)
                }
            }
            return
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().imePadding(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                SectionCard(copy.chooseVehicle) {
                    Text(copy.chooseVehicleTip, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        vehicles.take(8).forEach { vehicle ->
                            FilterChip(
                                selected = vehicleId == vehicle.id,
                                onClick = { vehicleId = vehicle.id },
                                label = { Text("${vehicle.reference} · ${vehicle.year} ${vehicle.make} ${vehicle.model}", style = MaterialTheme.typography.bodyLarge) },
                                modifier = Modifier.fillMaxWidth().height(58.dp),
                            )
                        }
                    }
                }
            }
            item {
                SectionCard(copy.partNameEs + " / " + copy.partNameEn) {
                    OutlinedTextField(nameEs, { nameEs = it; error = null }, Modifier.fillMaxWidth(), label = { Text(copy.partNameEs) }, singleLine = true)
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(nameEn, { nameEn = it; error = null }, Modifier.fillMaxWidth(), label = { Text(copy.partNameEn) }, singleLine = true)
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(categoryEs, { categoryEs = it; error = null }, Modifier.fillMaxWidth(), label = { Text(copy.categoryEs) }, singleLine = true)
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(categoryEn, { categoryEn = it; error = null }, Modifier.fillMaxWidth(), label = { Text(copy.categoryEn) }, singleLine = true)
                }
            }
            item {
                SectionCard(copy.condition) {
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PartCondition.entries.forEach { item ->
                            FilterChip(selected = condition == item, onClick = { conditionName = item.name }, label = { Text(copy.condition(item)) })
                        }
                    }
                    Spacer(Modifier.height(13.dp))
                    OutlinedTextField(location, { location = it }, Modifier.fillMaxWidth(), label = { Text(copy.location) }, placeholder = { Text(copy.locationExample) }, minLines = 2)
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(price, { if (it.all(Char::isDigit)) price = it }, Modifier.fillMaxWidth(), label = { Text(copy.price) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                }
            }
            item {
                SectionCard(copy.catalogVisibility) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(copy.publicCatalog, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(3.dp))
                            Text(copy.photoNeeded, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = published, onCheckedChange = { published = it })
                    }
                    Spacer(Modifier.height(12.dp))
                    if (photoBitmap != null) {
                        Image(photoBitmap!!, contentDescription = copy.photoAdded, modifier = Modifier.fillMaxWidth().height(160.dp), contentScale = ContentScale.Crop)
                        Spacer(Modifier.height(10.dp))
                    }
                    SecondaryButton(if (hasPhoto) copy.changePhoto else copy.addPhoto, onClick = { camera.launch(null) }, icon = if (hasPhoto) Icons.Default.CheckCircle else Icons.Default.CameraAlt)
                    if (published && !hasPhoto) {
                        Spacer(Modifier.height(10.dp))
                        ErrorBanner(copy.invalidPhoto)
                    }
                }
            }
            error?.let { message -> item { ErrorBanner(message) } }
            item {
                PrimaryButton(copy.save, icon = Icons.Default.CheckCircle, enabled = !published || hasPhoto, onClick = {
                    if (nameEs.isBlank() || nameEn.isBlank() || categoryEs.isBlank() || categoryEn.isBlank()) {
                        error = copy.somethingWrong
                    } else scope.launch {
                        when (repository.addPart(NewPart(vehicleId, nameEs, nameEn, categoryEs, categoryEn, condition, location, price.toIntOrNull(), published, hasPhoto, photoPath))) {
                            is SaveResult.Saved -> { snackbar.showSnackbar(copy.saved); onBack() }
                            SaveResult.Queued -> { snackbar.showSnackbar(copy.queued); onBack() }
                            is SaveResult.Failed -> { error = copy.somethingWrong }
                        }
                    }
                })
            }
        }
    }
}

@Composable
private fun SearchScreen(copy: Copy, language: AppLanguage, vehicles: List<Vehicle>, parts: List<Part>, onBack: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var makeFilter by rememberSaveable { mutableStateOf("") }
    val makes = remember(vehicles) { vehicles.map { it.make }.distinct().sorted() }
    val results = remember(query, makeFilter, parts, vehicles) {
        val needle = normalize(query)
        parts.filter { part ->
            val vehicle = vehicles.firstOrNull { it.id == part.vehicleId }
            val matchesText = needle.isBlank() || normalize(listOf(part.reference, part.nameEs, part.nameEn, part.categoryEs, part.categoryEn, vehicle?.year, vehicle?.make, vehicle?.model).joinToString(" ")).contains(needle)
            matchesText && (makeFilter.isBlank() || vehicle?.make == makeFilter)
        }
    }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(copy.searchInventory, onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text(copy.search, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
                Spacer(Modifier.height(4.dp))
                Text(copy.searchExample, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    query,
                    { query = it },
                    Modifier.fillMaxWidth(),
                    label = { Text(copy.search) },
                    placeholder = { Text(copy.searchExample) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Sky) },
                    trailingIcon = if (query.isNotBlank()) ({ TextButton(onClick = { query = "" }) { Text("×", fontSize = 24.sp) } }) else null,
                    singleLine = true,
                )
                Spacer(Modifier.height(13.dp))
                HelperBanner("${results.size} ${copy.results}", positive = results.isNotEmpty())
                if (makes.size > 1) {
                    Spacer(Modifier.height(12.dp))
                    Text(copy.quickMakes, style = MaterialTheme.typography.labelLarge, color = Navy)
                    Spacer(Modifier.height(7.dp))
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = makeFilter.isBlank(), onClick = { makeFilter = "" }, label = { Text(copy.make) })
                        makes.forEach { make ->
                            FilterChip(selected = makeFilter == make, onClick = { makeFilter = if (makeFilter == make) "" else make }, label = { Text(make) })
                        }
                    }
                }
            }
            if (results.isEmpty()) item { EmptyMessage(copy.noResults) }
            items(results, key = { it.id }) { part -> PartRow(part, vehicles.firstOrNull { it.id == part.vehicleId }, language, copy) }
        }
    }
}

@Composable
private fun SellScreen(
    copy: Copy,
    language: AppLanguage,
    user: StaffUser,
    repository: InventoryRepository,
    vehicles: List<Vehicle>,
    parts: List<Part>,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selected by remember { mutableStateOf<Part?>(null) }
    val scope = rememberCoroutineScope()
    val available = parts.filter { it.status == InventoryStatus.Available && (query.isBlank() || normalize(listOf(it.reference, it.nameEs, it.nameEn).joinToString(" ")).contains(normalize(query))) }
    val scanner = rememberLauncherForActivityResult(ScanContract()) { result ->
        val id = result.contents?.substringAfterLast('/')
        selected = parts.firstOrNull { it.id == id || it.reference.equals(result.contents, true) }
    }
    if (selected != null) {
        val part = selected!!
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(copy.confirmSell) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InventoryTag(part.reference)
                    Text(part.name(language), style = MaterialTheme.typography.titleLarge)
                    Text(part.locationNote, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(copy.confirmSellHelp)
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        when (repository.sellPart(part.id, user.id)) {
                            is SaveResult.Saved -> { snackbar.showSnackbar(copy.soldDone, duration = SnackbarDuration.Short); selected = null }
                            SaveResult.Queued -> { snackbar.showSnackbar(copy.waitingInternet); selected = null }
                            is SaveResult.Failed -> { snackbar.showSnackbar(copy.somethingWrong); selected = null }
                        }
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text(copy.confirm) }
            },
            dismissButton = { TextButton(onClick = { selected = null }) { Text(copy.cancel) } },
        )
    }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(copy.sell, onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text(copy.sellPart, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
                Spacer(Modifier.height(4.dp))
                Text(copy.confirmSellHelp, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                SectionCard(copy.sell) {
                    PrimaryButton(copy.scanLabel, icon = Icons.Default.QrCodeScanner, onClick = {
                        scanner.launch(
                            ScanOptions().setDesiredBarcodeFormats(ScanOptions.QR_CODE).setPrompt(copy.searchExample).setBeepEnabled(false).setOrientationLocked(false),
                        )
                    })
                    Spacer(Modifier.height(10.dp))
                    Text(copy.manualReference, style = MaterialTheme.typography.labelLarge, color = Navy)
                    Spacer(Modifier.height(7.dp))
                    OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), label = { Text(copy.search) }, placeholder = { Text(copy.searchExample) }, leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Sky) }, singleLine = true)
                }
            }
            if (available.isEmpty()) item { EmptyMessage(copy.noResults) }
            items(available, key = { it.id }) { part ->
                PartRow(part, vehicles.firstOrNull { it.id == part.vehicleId }, language, copy) { selected = part }
            }
        }
    }
}

@Composable
private fun OwnerScreen(
    copy: Copy,
    language: AppLanguage,
    repository: InventoryRepository,
    vehicles: List<Vehicle>,
    parts: List<Part>,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sold = parts.filter { it.status == InventoryStatus.Sold }
    val staff by repository.staff.collectAsState()
    var enrollmentCode by remember { mutableStateOf<String?>(null) }
    var staffName by rememberSaveable { mutableStateOf("") }
    var staffPin by rememberSaveable { mutableStateOf("") }
    if (enrollmentCode != null) {
        AlertDialog(
            onDismissRequest = { enrollmentCode = null },
            title = { Text(copy.shareCode) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(enrollmentCode!!, style = MaterialTheme.typography.displaySmall, color = Navy, fontWeight = FontWeight.ExtraBold)
                    Text(copy.codeExpires)
                }
            },
            confirmButton = { Button(onClick = { enrollmentCode = null }) { Text("OK") } },
        )
    }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(copy.ownerTools, onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 18.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Text(copy.ownerTools, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
                Spacer(Modifier.height(4.dp))
                HelperBanner(copy.restoreHelp)
            }
            item {
                SectionCard(copy.generateLabels) {
                    Text(copy.labelsHelp, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    SecondaryButton(copy.generateLabels, onClick = { LabelPdfExporter.share(context, parts.filter { it.status == InventoryStatus.Available }) }, icon = Icons.Default.PictureAsPdf)
                }
            }
            item {
                SectionCard(copy.staffDevices) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = Sky, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(copy.staffDevices, style = MaterialTheme.typography.titleMedium)
                            Text(copy.staffDevicesHelp, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                        }
                        StatusPill("${staff.size}", positive = true)
                    }
                    Spacer(Modifier.height(12.dp))
                    SecondaryButton(copy.createPhoneCode, onClick = {
                        scope.launch {
                            when (val result = repository.createEnrollmentCode()) {
                                is SaveResult.Saved -> enrollmentCode = result.value
                                else -> snackbar.showSnackbar(copy.somethingWrong)
                            }
                        }
                    }, icon = Icons.Default.QrCodeScanner)
                }
            }
            item {
                SectionCard(copy.addStaff) {
                    OutlinedTextField(staffName, { staffName = it }, Modifier.fillMaxWidth(), label = { Text(copy.staffName) }, singleLine = true)
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(
                        staffPin,
                        { if (it.length <= 4 && it.all(Char::isDigit)) staffPin = it },
                        Modifier.fillMaxWidth(),
                        label = { Text("PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                    )
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(copy.addStaff, enabled = staffName.length >= 2 && staffPin.length == 4, onClick = {
                        scope.launch {
                            when (repository.addStaff(staffName, staffPin)) {
                                is SaveResult.Saved -> { staffName = ""; staffPin = ""; snackbar.showSnackbar(copy.saved) }
                                else -> snackbar.showSnackbar(copy.somethingWrong)
                            }
                        }
                    })
                }
            }
            item {
                Text(copy.soldHistory, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 10.dp).semantics { heading() })
                Text(copy.restoreHelp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (sold.isEmpty()) item { EmptyMessage(copy.noResults) }
            items(sold, key = { it.id }) { part ->
                Column {
                    PartRow(part, vehicles.firstOrNull { it.id == part.vehicleId }, language, copy)
                    Spacer(Modifier.height(8.dp))
                    SecondaryButton(copy.restore, onClick = {
                        scope.launch {
                            when (repository.restorePart(part.id)) {
                                is SaveResult.Saved -> snackbar.showSnackbar(copy.saved)
                                SaveResult.Queued -> snackbar.showSnackbar(copy.waitingInternet)
                                is SaveResult.Failed -> snackbar.showSnackbar(copy.somethingWrong)
                            }
                        }
                    })
                }
            }
        }
    }
}

@Composable
private fun EmptyMessage(message: String) {
    Surface(color = Color.White, border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant), shape = RoundedCornerShape(8.dp)) {
        Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Inventory2, contentDescription = null, tint = Sky, modifier = Modifier.size(50.dp))
            Spacer(Modifier.height(10.dp))
            Text(message, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        }
    }
}

private fun normalize(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
    .replace(Regex("\\p{Mn}+"), "")
    .lowercase()
