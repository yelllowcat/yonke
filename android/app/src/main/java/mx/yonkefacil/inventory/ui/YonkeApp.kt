package mx.yonkefacil.inventory.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.PhonelinkSetup
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import mx.yonkefacil.inventory.ui.theme.ErrorContainer
import mx.yonkefacil.inventory.ui.theme.ErrorRed
import mx.yonkefacil.inventory.ui.theme.InverseOnSurface
import mx.yonkefacil.inventory.ui.theme.InverseSurface
import mx.yonkefacil.inventory.ui.theme.MonoDisplayStyle
import mx.yonkefacil.inventory.ui.theme.MonoLargeStyle
import mx.yonkefacil.inventory.ui.theme.MonoMediumStyle
import mx.yonkefacil.inventory.ui.theme.OnErrorContainer
import mx.yonkefacil.inventory.ui.theme.OnPrimary
import mx.yonkefacil.inventory.ui.theme.OnPrimaryContainer
import mx.yonkefacil.inventory.ui.theme.OnSecondary
import mx.yonkefacil.inventory.ui.theme.OnSecondaryContainer
import mx.yonkefacil.inventory.ui.theme.OnSuccessContainer
import mx.yonkefacil.inventory.ui.theme.OnSurfaceInk
import mx.yonkefacil.inventory.ui.theme.OnSurfaceMuted
import mx.yonkefacil.inventory.ui.theme.OnWarningContainer
import mx.yonkefacil.inventory.ui.theme.OutlineBorder
import mx.yonkefacil.inventory.ui.theme.OutlineSubtle
import mx.yonkefacil.inventory.ui.theme.PrimaryContainer
import mx.yonkefacil.inventory.ui.theme.PrimaryOrange
import mx.yonkefacil.inventory.ui.theme.SecondaryCobalt
import mx.yonkefacil.inventory.ui.theme.SecondaryContainer
import mx.yonkefacil.inventory.ui.theme.SuccessContainer
import mx.yonkefacil.inventory.ui.theme.SuccessGreen
import mx.yonkefacil.inventory.ui.theme.SurfaceContainer
import mx.yonkefacil.inventory.ui.theme.SurfaceLow
import mx.yonkefacil.inventory.ui.theme.SurfaceLowest
import mx.yonkefacil.inventory.ui.theme.SurfaceWarm
import mx.yonkefacil.inventory.ui.theme.WarningAmber
import mx.yonkefacil.inventory.ui.theme.WarningContainer
import java.io.File
import java.io.FileOutputStream
import java.text.Normalizer

private enum class AppRoute { Login, MainTabs, CameraIntake, PartDetail }
private enum class IntakeMode { Part, Vehicle }
private enum class PaymentMethod { Cash, Transfer, Card }

@Composable
fun YonkeApp(repository: InventoryRepository) {
    var languageName by rememberSaveable { mutableStateOf(AppLanguage.Spanish.name) }
    val language = AppLanguage.valueOf(languageName)
    val copy = Copy(language)

    var routeName by rememberSaveable { mutableStateOf(AppRoute.Login.name) }
    val route = AppRoute.valueOf(routeName)
    var currentTabName by rememberSaveable { mutableStateOf(YardTab.Inventory.name) }
    val currentTab = YardTab.valueOf(currentTabName)
    var intakeModeName by rememberSaveable { mutableStateOf(IntakeMode.Part.name) }
    var detailPartId by rememberSaveable { mutableStateOf<String?>(null) }

    var currentUser by remember { mutableStateOf<StaffUser?>(null) }
    val vehicles by repository.vehicles.collectAsState()
    val parts by repository.parts.collectAsState()
    val staff by repository.staff.collectAsState()
    val enrolled by repository.isEnrolled.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    val toggleLanguage = {
        languageName = if (language == AppLanguage.Spanish) AppLanguage.English.name else AppLanguage.Spanish.name
    }

    val openCamera: (IntakeMode) -> Unit = { mode ->
        intakeModeName = mode.name
        routeName = AppRoute.CameraIntake.name
    }

    val openPartDetail: (String) -> Unit = { partId ->
        detailPartId = partId
        routeName = AppRoute.PartDetail.name
    }

    Scaffold(
        containerColor = SurfaceWarm,
        snackbarHost = { SnackbarHost(snackbar) },
    ) { _ ->
        when (route) {
            AppRoute.Login -> PinLoginScreen(
                copy = copy,
                language = language,
                staff = staff,
                enrolled = enrolled,
                onLanguage = toggleLanguage,
                onEnroll = repository::enrollDevice,
                onSignIn = { staffId, pin ->
                    repository.signIn(staffId, pin)?.also { signedUser ->
                        currentUser = signedUser
                        currentTabName = YardTab.Inventory.name
                        routeName = AppRoute.MainTabs.name
                    }
                },
            )

            AppRoute.MainTabs -> {
                val user = currentUser ?: run {
                    routeName = AppRoute.Login.name
                    return@Scaffold
                }
                val shiftSubtitle = buildString {
                    append("${copy.shiftPrefix} ${user.name}")
                    if (user.role == StaffRole.Owner) append(" (${copy.owner})")
                }
                val topTitle = when (currentTab) {
                    YardTab.Home -> copy.appName
                    YardTab.Inventory -> copy.navInventory
                    YardTab.Sales -> copy.navSales
                    YardTab.Team -> copy.ownerTools
                }
                Scaffold(
                    containerColor = SurfaceWarm,
                    topBar = {
                        YardTopBar(
                            title = topTitle,
                            subtitle = shiftSubtitle,
                            syncText = copy.syncedBadge,
                            language = language,
                            onToggleLanguage = toggleLanguage,
                        )
                    },
                    bottomBar = {
                        BottomYardNavBar(
                            currentTab = currentTab,
                            copy = copy,
                            onSelectTab = { currentTabName = it.name },
                            onCameraClick = { openCamera(IntakeMode.Part) },
                        )
                    },
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (currentTab) {
                            YardTab.Home -> HomeTab(
                                copy = copy,
                                user = user,
                                vehicles = vehicles,
                                parts = parts,
                                onReceiveVehicle = { openCamera(IntakeMode.Vehicle) },
                                onAddPart = { openCamera(IntakeMode.Part) },
                                onSearch = { currentTabName = YardTab.Inventory.name },
                                onSell = { currentTabName = YardTab.Sales.name },
                                onTeam = { currentTabName = YardTab.Team.name },
                                onSignOut = {
                                    currentUser = null
                                    routeName = AppRoute.Login.name
                                },
                            )

                            YardTab.Inventory -> InventoryTab(
                                copy = copy,
                                language = language,
                                vehicles = vehicles,
                                parts = parts,
                                onOpenDetail = openPartDetail,
                            )

                            YardTab.Sales -> SalesTab(
                                copy = copy,
                                language = language,
                                user = user,
                                repository = repository,
                                vehicles = vehicles,
                                parts = parts,
                                onOpenDetail = openPartDetail,
                                snackbar = snackbar,
                            )

                            YardTab.Team -> TeamOwnerTab(
                                copy = copy,
                                language = language,
                                user = user,
                                repository = repository,
                                vehicles = vehicles,
                                parts = parts,
                                onOpenDetail = openPartDetail,
                                onSignOut = {
                                    currentUser = null
                                    routeName = AppRoute.Login.name
                                },
                                snackbar = snackbar,
                            )
                        }
                    }
                }
            }

            AppRoute.CameraIntake -> CameraIntakeScreen(
                copy = copy,
                initialMode = IntakeMode.valueOf(intakeModeName),
                repository = repository,
                vehicles = vehicles,
                onBack = { routeName = AppRoute.MainTabs.name },
                snackbar = snackbar,
            )

            AppRoute.PartDetail -> {
                val user = currentUser
                val part = parts.firstOrNull { it.id == detailPartId }
                if (user == null || part == null) {
                    routeName = AppRoute.MainTabs.name
                } else {
                    PartDetailSaleScreen(
                        copy = copy,
                        language = language,
                        user = user,
                        part = part,
                        vehicle = vehicles.firstOrNull { it.id == part.vehicleId },
                        repository = repository,
                        onBack = { routeName = AppRoute.MainTabs.name },
                        snackbar = snackbar,
                    )
                }
            }
        }
    }
}

// ============================================================================
// SCREEN 1: Acceso por PIN (Stitch 01_acceso_pin.html)
// ============================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PinLoginScreen(
    copy: Copy,
    language: AppLanguage,
    staff: List<StaffUser>,
    enrolled: Boolean,
    onLanguage: () -> Unit,
    onEnroll: suspend (String, String) -> SaveResult<Unit>,
    onSignIn: suspend (String, String) -> StaffUser?,
) {
    var selectedStaffId by rememberSaveable { mutableStateOf<String?>(null) }
    var pin by rememberSaveable { mutableStateOf("") }
    var pinError by rememberSaveable { mutableStateOf(false) }
    var showEnrollmentForm by rememberSaveable { mutableStateOf(!enrolled) }
    var enrollmentCode by rememberSaveable { mutableStateOf("") }
    var deviceName by rememberSaveable { mutableStateOf("") }
    var enrollError by rememberSaveable { mutableStateOf(false) }
    var busy by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(staff) {
        if (selectedStaffId == null && staff.isNotEmpty()) {
            selectedStaffId = staff.first().id
        }
    }
    LaunchedEffect(enrolled) {
        if (!enrolled) showEnrollmentForm = true
    }

    val selectedStaff = staff.firstOrNull { it.id == selectedStaffId } ?: staff.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceWarm)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Brand & Device Status Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryOrange)
                            .border(2.dp, PrimaryOrange, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.CarRepair, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(24.dp))
                    }
                    Column {
                        Text(copy.appName, style = MaterialTheme.typography.headlineMedium, color = OnSurfaceInk)
                        Text(copy.yardSubtitle, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    StatusBadge(
                        text = if (enrolled) copy.linkedBadge else copy.notLinkedBadge,
                        containerColor = if (enrolled) SuccessContainer else WarningContainer,
                        contentColor = if (enrolled) OnSuccessContainer else OnWarningContainer,
                        borderColor = if (enrolled) SuccessGreen else WarningAmber,
                        icon = Icons.Default.VerifiedUser,
                    )
                    LanguageChip(language = language, onClick = onLanguage)
                }
            }
        }

        if (showEnrollmentForm || !enrolled) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = copy.enrollPhone,
                        style = MaterialTheme.typography.headlineLarge,
                        color = OnSurfaceInk,
                        modifier = Modifier.semantics { heading() },
                    )
                    HelperBanner(copy.enrollmentStep)
                }
            }
            item {
                YardSectionCard(borderColor = OutlineBorder) {
                    YardTextField(
                        value = enrollmentCode,
                        onValueChange = {
                            if (it.length <= 6 && it.all(Char::isDigit)) {
                                enrollmentCode = it
                                enrollError = false
                            }
                        },
                        label = copy.enrollmentCode,
                        placeholder = "842915",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = enrollError,
                    )
                    YardTextField(
                        value = deviceName,
                        onValueChange = { deviceName = it },
                        label = copy.deviceName,
                        placeholder = copy.deviceExample,
                    )
                    if (enrollError) {
                        ErrorBanner(copy.enrollmentFailed)
                    }
                    PrimaryButton(
                        text = copy.connect,
                        icon = Icons.Default.PhonelinkSetup,
                        enabled = enrollmentCode.length == 6 && deviceName.trim().length >= 2 && !busy,
                        onClick = {
                            busy = true
                            scope.launch {
                                val res = onEnroll(enrollmentCode, deviceName.trim())
                                enrollError = res !is SaveResult.Saved
                                if (res is SaveResult.Saved) {
                                    showEnrollmentForm = false
                                    enrollmentCode = ""
                                }
                                busy = false
                            }
                        },
                    )
                    if (enrolled) {
                        SecondaryButton(
                            text = copy.back,
                            subtleBorder = true,
                            onClick = { showEnrollmentForm = false; enrollError = false },
                        )
                    }
                }
            }
        } else if (staff.isEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp), color = PrimaryOrange)
                    Spacer(Modifier.width(12.dp))
                    Text(copy.loadingStaff, style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            // Staff Selector Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = copy.choosePerson,
                        style = MaterialTheme.typography.headlineLarge,
                        color = OnSurfaceInk,
                        modifier = Modifier.semantics { heading() },
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        staff.forEach { member ->
                            val isSelected = selectedStaff?.id == member.id
                            val chipLabel = if (member.role == StaffRole.Owner) {
                                "${member.name} (${copy.owner})"
                            } else {
                                member.name
                            }
                            YardFilterChip(
                                label = chipLabel,
                                selected = isSelected,
                                icon = if (member.role == StaffRole.Owner) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                minHeightDp = 48,
                                onClick = {
                                    selectedStaffId = member.id
                                    pin = ""
                                    pinError = false
                                },
                            )
                        }
                    }
                }
            }

            // PIN Dots Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SurfaceLowest,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(2.dp, if (pinError) ErrorRed else OutlineBorder),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("${copy.enterPinFor} ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = OnSurfaceInk)) {
                                    append(selectedStaff?.name ?: "")
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceMuted,
                            textAlign = TextAlign.Center,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            repeat(4) { index ->
                                val filled = index < pin.length
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (filled) PrimaryOrange else SurfaceContainer)
                                        .border(
                                            width = 2.dp,
                                            color = if (filled) PrimaryOrange else OutlineBorder,
                                            shape = CircleShape,
                                        ),
                                )
                            }
                        }
                        if (pinError) {
                            Text(
                                text = copy.wrongPin,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ErrorRed,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            // Tactile Numeric Keypad (3x4 grid, 60dp height)
            item {
                val tapDigit: (String) -> Unit = { digit ->
                    if (pin.length < 4) {
                        pin += digit
                        pinError = false
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                    ).forEach { rowDigits ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            rowDigits.forEach { d ->
                                KeypadDigitButton(
                                    label = d,
                                    modifier = Modifier.weight(1f),
                                    onClick = { tapDigit(d) },
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(role = Role.Button) { pin = ""; pinError = false },
                            color = SurfaceLow,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(2.dp, OutlineSubtle),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(copy.clear, style = MaterialTheme.typography.labelLarge, color = OnSurfaceMuted)
                            }
                        }
                        KeypadDigitButton(
                            label = "0",
                            modifier = Modifier.weight(1f),
                            onClick = { tapDigit("0") },
                        )
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(role = Role.Button) {
                                    if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                    pinError = false
                                }
                                .semantics { contentDescription = copy.backspace },
                            color = SurfaceLow,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(2.dp, OutlineSubtle),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = copy.backspace,
                                    tint = OnSurfaceInk,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }
                    }
                }
            }

            // Primary Submit + Link Phone Footer
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PrimaryButton(
                        text = copy.signIn,
                        icon = Icons.AutoMirrored.Filled.Login,
                        enabled = selectedStaff != null && pin.length == 4 && !busy,
                        onClick = {
                            val memberId = selectedStaff?.id ?: return@PrimaryButton
                            busy = true
                            scope.launch {
                                if (onSignIn(memberId, pin) == null) {
                                    pinError = true
                                    pin = ""
                                }
                                busy = false
                            }
                        },
                    )
                    SecondaryButton(
                        text = copy.linkPhoneButton,
                        icon = Icons.Default.PhonelinkSetup,
                        subtleBorder = true,
                        onClick = { showEnrollmentForm = true },
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadDigitButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick),
        color = SurfaceLowest,
        contentColor = OnSurfaceInk,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, OutlineBorder),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = label, style = MonoLargeStyle, color = OnSurfaceInk)
        }
    }
}

// ============================================================================
// TAB 1: Inicio (Quick Yard Actions Hub)
// ============================================================================
@Composable
private fun HomeTab(
    copy: Copy,
    user: StaffUser,
    vehicles: List<Vehicle>,
    parts: List<Part>,
    onReceiveVehicle: () -> Unit,
    onAddPart: () -> Unit,
    onSearch: () -> Unit,
    onSell: () -> Unit,
    onTeam: () -> Unit,
    onSignOut: () -> Unit,
) {
    val activePartsCount = parts.count { it.status == InventoryStatus.Available }
    val publishedCount = parts.count { it.published && it.status == InventoryStatus.Available }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("${copy.hello}, ${user.name}", style = MaterialTheme.typography.titleMedium, color = SecondaryCobalt)
                Text(
                    text = copy.whatDo,
                    style = MaterialTheme.typography.headlineLarge,
                    color = OnSurfaceInk,
                    modifier = Modifier.semantics { heading() },
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricKpiCard(
                    label = copy.activePartsKpi,
                    value = "$activePartsCount",
                    subtitle = copy.readyForSale,
                    subtitleColor = SuccessGreen,
                    icon = Icons.Default.Inventory2,
                    iconTint = PrimaryOrange,
                    modifier = Modifier.weight(1f),
                )
                MetricKpiCard(
                    label = copy.yardCarsKpi,
                    value = "${vehicles.size}",
                    subtitle = "$publishedCount ${copy.pendingPhotoSuffix}",
                    subtitleColor = WarningAmber,
                    icon = Icons.Default.DirectionsCar,
                    iconTint = SecondaryCobalt,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            ActionCard(
                title = copy.receiveVehicle,
                help = copy.receiveHelp,
                icon = Icons.Default.PhotoCamera,
                highlighted = true,
                onClick = onReceiveVehicle,
            )
        }
        item {
            ActionCard(
                title = copy.addPart,
                help = copy.addPartHelp,
                icon = Icons.Default.Build,
                onClick = onAddPart,
            )
        }
        item {
            ActionCard(
                title = copy.search,
                help = copy.searchHelp,
                icon = Icons.Default.Search,
                onClick = onSearch,
            )
        }
        item {
            ActionCard(
                title = copy.sell,
                help = copy.sellHelp,
                icon = Icons.Default.PointOfSale,
                onClick = onSell,
            )
        }
        if (user.role == StaffRole.Owner) {
            item {
                SecondaryButton(
                    text = copy.ownerTools,
                    icon = Icons.Default.AdminPanelSettings,
                    onClick = onTeam,
                )
            }
        }
        item {
            TextButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = OnSurfaceMuted)
                Spacer(Modifier.width(8.dp))
                Text(copy.signOut, style = MaterialTheme.typography.labelLarge, color = OnSurfaceMuted)
            }
        }
    }
}

// ============================================================================
// SCREEN 2 (TAB 2): Inventario y Patio (Stitch 02_inventario_patio.html)
// ============================================================================
@Composable
private fun InventoryTab(
    copy: Copy,
    language: AppLanguage,
    vehicles: List<Vehicle>,
    parts: List<Part>,
    onOpenDetail: (String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("") }

    val availableParts = remember(parts) { parts.filter { it.status == InventoryStatus.Available } }
    val categories = remember(availableParts, language) {
        availableParts.map { it.category(language) }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val publishedCount = remember(availableParts) { availableParts.count { it.published } }

    val filteredParts = remember(query, selectedCategory, parts, vehicles, language) {
        val needle = normalize(query)
        parts.filter { part ->
            val vehicle = vehicles.firstOrNull { it.id == part.vehicleId }
            val matchesQuery = needle.isBlank() || normalize(
                listOf(
                    part.reference,
                    part.nameEs,
                    part.nameEn,
                    part.categoryEs,
                    part.categoryEn,
                    part.locationNote,
                    vehicle?.year,
                    vehicle?.make,
                    vehicle?.model,
                ).joinToString(" "),
            ).contains(needle)
            val matchesCategory = selectedCategory.isBlank() || part.category(language).equals(selectedCategory, ignoreCase = true)
            matchesQuery && matchesCategory
        }.sortedBy { if (it.status == InventoryStatus.Available) 0 else 1 }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // KPI Strip (2 columns)
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricKpiCard(
                    label = copy.activePartsKpi,
                    value = "${availableParts.size}",
                    subtitle = copy.readyForSale,
                    subtitleColor = SuccessGreen,
                    icon = Icons.Default.Inventory2,
                    iconTint = PrimaryOrange,
                    modifier = Modifier.weight(1f),
                )
                MetricKpiCard(
                    label = copy.yardCarsKpi,
                    value = "${vehicles.size}",
                    subtitle = "$publishedCount ${copy.pendingPhotoSuffix}",
                    subtitleColor = WarningAmber,
                    icon = Icons.Default.DirectionsCar,
                    iconTint = SecondaryCobalt,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Search Input + Horizontal Category Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                YardTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = "",
                    placeholder = copy.searchPlaceholder,
                    leadingIcon = Icons.Default.Search,
                    trailing = if (query.isNotBlank()) {
                        {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Default.Close, contentDescription = copy.clear, tint = OutlineBorder)
                            }
                        }
                    } else {
                        null
                    },
                )
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    YardFilterChip(
                        label = "${copy.allFilter} (${parts.size})",
                        selected = selectedCategory.isBlank(),
                        onClick = { selectedCategory = "" },
                    )
                    categories.forEach { category ->
                        YardFilterChip(
                            label = category,
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = if (selectedCategory == category) "" else category },
                        )
                    }
                }
            }
        }

        // Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(copy.recentParts, style = MaterialTheme.typography.titleLarge, color = OnSurfaceInk)
                Text(copy.tapToSell, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
            }
        }

        if (filteredParts.isEmpty()) {
            item { EmptyMessage(copy.noResults) }
        }

        items(filteredParts, key = { it.id }) { part ->
            PartActionCard(
                part = part,
                vehicle = vehicles.firstOrNull { it.id == part.vehicleId },
                language = language,
                copy = copy,
                onOpenDetail = { onOpenDetail(part.id) },
                onQuickAction = if (part.status == InventoryStatus.Available) ({ onOpenDetail(part.id) }) else null,
                quickActionLabel = if (part.status == InventoryStatus.Available) copy.sell else null,
            )
        }
    }
}

// ============================================================================
// SCREEN 3: Cámara e Ingreso (Stitch 03_camara_ingreso.html)
// ============================================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CameraIntakeScreen(
    copy: Copy,
    initialMode: IntakeMode,
    repository: InventoryRepository,
    vehicles: List<Vehicle>,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    var modeName by rememberSaveable { mutableStateOf(initialMode.name) }
    val mode = IntakeMode.valueOf(modeName)
    var flashOn by rememberSaveable { mutableStateOf(false) }

    // Shared photo capture state (up to 4 photos in thumbnail strip)
    var photoPaths by rememberSaveable { mutableStateOf(listOf<String>()) }

    // Part fields
    var vehicleId by rememberSaveable { mutableStateOf(vehicles.firstOrNull()?.id ?: "") }
    var nameEs by rememberSaveable { mutableStateOf("") }
    var nameEn by rememberSaveable { mutableStateOf("") }
    var categoryEs by rememberSaveable { mutableStateOf("") }
    var categoryEn by rememberSaveable { mutableStateOf("") }
    var conditionName by rememberSaveable { mutableStateOf(PartCondition.Untested.name) }
    val condition = PartCondition.valueOf(conditionName)
    var partLocation by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var published by rememberSaveable { mutableStateOf(false) }

    // Vehicle fields
    var year by rememberSaveable { mutableStateOf("") }
    var make by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var color by rememberSaveable { mutableStateOf("") }
    var vin by rememberSaveable { mutableStateOf("") }
    var vehicleLocation by rememberSaveable { mutableStateOf("") }

    var error by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(vehicles) {
        if (vehicleId.isBlank() && vehicles.isNotEmpty()) {
            vehicleId = vehicles.first().id
        }
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val directory = File(context.cacheDir, "part-photos").apply { mkdirs() }
            val file = File(directory, "${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 86, it) }
            photoPaths = (photoPaths + file.absolutePath).takeLast(4)
            error = null
        }
    }

    val latestBitmap = remember(photoPaths) {
        photoPaths.lastOrNull()?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
    }
    val hasPhoto = photoPaths.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InverseSurface),
    ) {
        // Dark Inverse Top Camera Bar (Stitch 03_camara_ingreso.html)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(InverseSurface)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconSquareButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = copy.back,
                onClick = onBack,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (mode == IntakeMode.Vehicle) copy.vehicleIntakeTitle else copy.partIntakeTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = InverseOnSurface,
                )
                Text(
                    text = copy.offlineQueueReady,
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryContainer,
                )
            }
            IconSquareButton(
                icon = if (flashOn) Icons.Default.FlashOn else Icons.Default.FlashAuto,
                contentDescription = "Flash",
                onClick = { flashOn = !flashOn },
            )
        }
        HorizontalDivider(thickness = 1.dp, color = OutlineBorder)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SurfaceWarm)
                .imePadding(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // Live / Preview Camera Viewfinder Frame
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(InverseSurface)
                        .clickable(role = Role.Button) { camera.launch(null) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (latestBitmap != null) {
                        Image(
                            bitmap = latestBitmap,
                            contentDescription = copy.photoAdded,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = PrimaryContainer,
                                modifier = Modifier.size(44.dp),
                            )
                            Text(
                                text = copy.addPhoto,
                                style = MaterialTheme.typography.labelLarge,
                                color = InverseOnSurface,
                            )
                        }
                    }

                    // Framing Corner Guides
                    ViewfinderCorners(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                    )

                    // Scrimmed Hint Pill
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.dp),
                        color = InverseSurface.copy(alpha = 0.90f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, OutlineBorder),
                    ) {
                        Text(
                            text = copy.viewfinderHint,
                            style = MaterialTheme.typography.labelSmall,
                            color = InverseOnSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        )
                    }
                }
                HorizontalDivider(thickness = 2.dp, color = OutlineBorder)
            }

            // Thumbnail Strip + Mode Switch + Quick Tags
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // Thumbnail Strip & Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            photoPaths.forEachIndexed { idx, path ->
                                val thumb = remember(path) { BitmapFactory.decodeFile(path)?.asImageBitmap() }
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SurfaceContainer)
                                        .border(
                                            width = 2.dp,
                                            color = if (idx == photoPaths.lastIndex) PrimaryOrange else OutlineSubtle,
                                            shape = RoundedCornerShape(4.dp),
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (thumb != null) {
                                        Image(thumb, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    }
                                }
                            }
                            if (photoPaths.size < 4) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SurfaceLow)
                                        .border(2.dp, OutlineBorder, RoundedCornerShape(4.dp))
                                        .clickable(role = Role.Button) { camera.launch(null) },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = copy.addPhoto, tint = OutlineBorder, modifier = Modifier.size(22.dp))
                                }
                            }
                        }
                        Text(
                            text = "${photoPaths.size} / 4 ${copy.photosCounterSuffix}",
                            style = MonoMediumStyle,
                            color = OnSurfaceInk,
                        )
                    }

                    // Mode selector (Part vs Vehicle)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        YardFilterChip(
                            label = copy.modePart,
                            selected = mode == IntakeMode.Part,
                            icon = Icons.Default.Build,
                            modifier = Modifier.weight(1f),
                            onClick = { modeName = IntakeMode.Part.name; error = null },
                        )
                        YardFilterChip(
                            label = copy.modeVehicle,
                            selected = mode == IntakeMode.Vehicle,
                            icon = Icons.Default.DirectionsCar,
                            modifier = Modifier.weight(1f),
                            onClick = { modeName = IntakeMode.Vehicle.name; error = null },
                        )
                    }

                    // Quick Shutter + Save Row (Stitch 2-column action bar)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        PrimaryButton(
                            text = copy.addPhoto,
                            icon = Icons.Default.PhotoCamera,
                            modifier = Modifier.weight(1f),
                            onClick = { camera.launch(null) },
                        )
                        val saveEnabled = if (mode == IntakeMode.Part) (!published || hasPhoto) && vehicles.isNotEmpty() else true
                        SecondaryButton(
                            text = if (hasPhoto) "${copy.save} (${photoPaths.size})" else copy.save,
                            icon = Icons.Default.CloudUpload,
                            enabled = saveEnabled,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (mode == IntakeMode.Vehicle) {
                                    val numericYear = year.toIntOrNull()
                                    if (numericYear == null || make.isBlank() || model.isBlank()) {
                                        error = copy.requiredVehicle
                                    } else {
                                        scope.launch {
                                            when (repository.addVehicle(NewVehicle(numericYear, make, model, color, vin, vehicleLocation))) {
                                                is SaveResult.Saved -> { snackbar.showSnackbar(copy.saved); onBack() }
                                                SaveResult.Queued -> { snackbar.showSnackbar(copy.queued); onBack() }
                                                is SaveResult.Failed -> { error = copy.somethingWrong }
                                            }
                                        }
                                    }
                                } else {
                                    if (nameEs.isBlank() || nameEn.isBlank() || categoryEs.isBlank() || categoryEn.isBlank()) {
                                        error = copy.somethingWrong
                                    } else {
                                        scope.launch {
                                            val input = NewPart(
                                                vehicleId = vehicleId,
                                                nameEs = nameEs,
                                                nameEn = nameEn,
                                                categoryEs = categoryEs,
                                                categoryEn = categoryEn,
                                                condition = condition,
                                                locationNote = partLocation,
                                                priceMxn = price.toIntOrNull(),
                                                published = published,
                                                hasPhoto = hasPhoto,
                                                photoPath = photoPaths.lastOrNull(),
                                            )
                                            when (repository.addPart(input)) {
                                                is SaveResult.Saved -> { snackbar.showSnackbar(copy.saved); onBack() }
                                                SaveResult.Queued -> { snackbar.showSnackbar(copy.queued); onBack() }
                                                is SaveResult.Failed -> { error = copy.somethingWrong }
                                            }
                                        }
                                    }
                                }
                            },
                        )
                    }

                    error?.let { ErrorBanner(it) }
                }
            }

            if (mode == IntakeMode.Part) {
                if (vehicles.isEmpty()) {
                    item {
                        Box(Modifier.padding(horizontal = 16.dp)) {
                            EmptyMessage(copy.noVehicles)
                        }
                    }
                } else {
                    // Quick 1-Tap Tags (Zero Typing Helpers)
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(copy.quickTagsLabel, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                vehicles.take(4).forEach { v ->
                                    YardFilterChip(
                                        label = "${v.make} ${v.model} ${v.year}",
                                        selected = vehicleId == v.id,
                                        minHeightDp = 40,
                                        onClick = { vehicleId = v.id },
                                    )
                                }
                                listOf(
                                    "Motor" to "Engine",
                                    "Sistema eléctrico" to "Electrical",
                                    "Carrocería" to "Body",
                                ).forEach { (catEs, catEn) ->
                                    YardFilterChip(
                                        label = catEs,
                                        selected = categoryEs.equals(catEs, ignoreCase = true),
                                        minHeightDp = 40,
                                        onClick = {
                                            categoryEs = catEs
                                            categoryEn = catEn
                                            error = null
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // Bilingual Part Details Card
                    item {
                        Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            YardSectionCard(title = copy.chooseVehicle) {
                                YardTextField(nameEs, { nameEs = it; error = null }, copy.partNameEs, placeholder = "Ej. Alternador Bosch 110A")
                                YardTextField(nameEn, { nameEn = it; error = null }, copy.partNameEn, placeholder = "Ex. Bosch 110A Alternator")
                                YardTextField(categoryEs, { categoryEs = it; error = null }, copy.categoryEs)
                                YardTextField(categoryEn, { categoryEn = it; error = null }, copy.categoryEn)
                            }
                        }
                    }

                    // Condition, Location & Price
                    item {
                        Box(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            YardSectionCard(title = copy.condition) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    PartCondition.entries.forEach { item ->
                                        YardFilterChip(
                                            label = copy.condition(item),
                                            selected = condition == item,
                                            minHeightDp = 40,
                                            onClick = { conditionName = item.name },
                                        )
                                    }
                                }
                                YardTextField(partLocation, { partLocation = it }, copy.location, placeholder = copy.locationExample)
                                YardTextField(
                                    value = price,
                                    onValueChange = { if (it.all(Char::isDigit)) price = it },
                                    label = copy.price,
                                    placeholder = "1450",
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                )
                            }
                        }
                    }

                    // Catalog Visibility
                    item {
                        Box(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                            YardSectionCard(title = copy.catalogVisibility) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(copy.publicCatalog, style = MaterialTheme.typography.titleMedium, color = OnSurfaceInk)
                                        Text(copy.photoNeeded, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
                                    }
                                    Switch(
                                        checked = published,
                                        onCheckedChange = { published = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = OnPrimary,
                                            checkedTrackColor = PrimaryOrange,
                                        ),
                                    )
                                }
                                if (published && !hasPhoto) {
                                    ErrorBanner(copy.invalidPhoto)
                                }
                            }
                        }
                    }
                }
            } else {
                // Vehicle Intake Form
                item {
                    Box(Modifier.padding(horizontal = 16.dp)) {
                        YardSectionCard(title = copy.vehicleDetails) {
                            HelperBanner(copy.vehicleTip)
                            YardTextField(
                                value = year,
                                onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) { year = it; error = null } },
                                label = copy.year,
                                placeholder = "2014",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = error != null && year.isBlank(),
                            )
                            YardTextField(make, { make = it; error = null }, copy.make, placeholder = "Nissan", isError = error != null && make.isBlank())
                            YardTextField(model, { model = it; error = null }, copy.model, placeholder = "Tsuru", isError = error != null && model.isBlank())
                            HorizontalDivider(color = OutlineSubtle)
                            YardTextField(color, { color = it }, copy.color, placeholder = "Blanco")
                            YardTextField(vin, { vin = it.uppercase() }, copy.vin)
                            YardTextField(vehicleLocation, { vehicleLocation = it }, copy.location, placeholder = copy.locationExample, singleLine = false, minLines = 2)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewfinderCorners(modifier: Modifier = Modifier) {
    val cornerSize = 28.dp
    val stroke = 4.dp
    Box(modifier = modifier) {
        // Top-left
        Box(Modifier.align(Alignment.TopStart).width(cornerSize).height(stroke).background(PrimaryContainer))
        Box(Modifier.align(Alignment.TopStart).width(stroke).height(cornerSize).background(PrimaryContainer))
        // Top-right
        Box(Modifier.align(Alignment.TopEnd).width(cornerSize).height(stroke).background(PrimaryContainer))
        Box(Modifier.align(Alignment.TopEnd).width(stroke).height(cornerSize).background(PrimaryContainer))
        // Bottom-left
        Box(Modifier.align(Alignment.BottomStart).width(cornerSize).height(stroke).background(PrimaryContainer))
        Box(Modifier.align(Alignment.BottomStart).width(stroke).height(cornerSize).background(PrimaryContainer))
        // Bottom-right
        Box(Modifier.align(Alignment.BottomEnd).width(cornerSize).height(stroke).background(PrimaryContainer))
        Box(Modifier.align(Alignment.BottomEnd).width(stroke).height(cornerSize).background(PrimaryContainer))
    }
}

// ============================================================================
// SCREEN 4: Detalle y Venta Rápida (Stitch 04_detalle_venta.html)
// ============================================================================
@Composable
private fun PartDetailSaleScreen(
    copy: Copy,
    language: AppLanguage,
    user: StaffUser,
    part: Part,
    vehicle: Vehicle?,
    repository: InventoryRepository,
    onBack: () -> Unit,
    snackbar: SnackbarHostState,
) {
    var paymentName by rememberSaveable { mutableStateOf(PaymentMethod.Cash.name) }
    val payment = PaymentMethod.valueOf(paymentName)
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var capturedPhotoPath by rememberSaveable(part.id) { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val directory = File(context.cacheDir, "part-photos").apply { mkdirs() }
            val file = File(directory, "detail-${part.id}-${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 86, it) }
            capturedPhotoPath = file.absolutePath
        }
    }
    val photoBitmap = remember(capturedPhotoPath) {
        capturedPhotoPath?.let { BitmapFactory.decodeFile(it)?.asImageBitmap() }
    }

    val isAvailable = part.status == InventoryStatus.Available
    val priceDisplay = formatMxn(part.priceMxn, copy.askPrice)

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = SurfaceLowest,
            shape = RoundedCornerShape(8.dp),
            title = { Text(copy.confirmSell, style = MaterialTheme.typography.headlineMedium, color = OnSurfaceInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${part.reference} · ${part.name(language)}", style = MaterialTheme.typography.titleMedium, color = OnSurfaceInk)
                    if (part.locationNote.isNotBlank()) {
                        Text(part.locationNote, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
                    }
                    Text(copy.confirmSellHelp, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        scope.launch {
                            when (repository.sellPart(part.id, user.id)) {
                                is SaveResult.Saved -> {
                                    snackbar.showSnackbar(copy.soldDone, duration = SnackbarDuration.Short)
                                    onBack()
                                }
                                SaveResult.Queued -> {
                                    snackbar.showSnackbar(copy.waitingInternet)
                                    onBack()
                                }
                                is SaveResult.Failed -> snackbar.showSnackbar(copy.somethingWrong)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange, contentColor = OnPrimary),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(copy.confirm, style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(copy.cancel, style = MaterialTheme.typography.labelLarge, color = OnSurfaceMuted)
                }
            },
        )
    }

    Scaffold(
        containerColor = SurfaceWarm,
        topBar = {
            ContextualBackHeader(
                title = "${copy.partHeaderPrefix}${part.reference}",
                subtitle = part.locationNote.ifBlank { vehicle?.let { "${it.make} ${it.model} ${it.year}" } },
                onBack = onBack,
                trailing = {
                    StatusBadge(
                        text = if (isAvailable) copy.available else copy.sold,
                        containerColor = if (isAvailable) SuccessContainer else ErrorContainer,
                        contentColor = if (isAvailable) OnSuccessContainer else OnErrorContainer,
                        borderColor = if (isAvailable) SuccessGreen else ErrorRed,
                    )
                },
            )
        },
        bottomBar = {
            Surface(
                color = SurfaceWarm,
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            ) {
                Column {
                    HorizontalDivider(thickness = 2.dp, color = OutlineSubtle)
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                        if (isAvailable) {
                            val ctaLabel = if (part.priceMxn != null) {
                                "${copy.confirmSalePrefix} · $priceDisplay"
                            } else {
                                copy.confirmSalePrefix
                            }
                            PrimaryButton(
                                text = ctaLabel,
                                icon = Icons.Default.PointOfSale,
                                onClick = { showConfirmDialog = true },
                            )
                        } else if (user.role == StaffRole.Owner) {
                            SecondaryButton(
                                text = copy.restore,
                                icon = Icons.Default.Restore,
                                onClick = {
                                    scope.launch {
                                        when (repository.restorePart(part.id)) {
                                            is SaveResult.Saved -> snackbar.showSnackbar(copy.saved)
                                            SaveResult.Queued -> snackbar.showSnackbar(copy.waitingInternet)
                                            is SaveResult.Failed -> snackbar.showSnackbar(copy.somethingWrong)
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 4:3 Part Photo Frame with floating Add Photo button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceContainer)
                        .border(2.dp, OutlineBorder, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (photoBitmap != null) {
                        Image(
                            bitmap = photoBitmap,
                            contentDescription = part.name(language),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = OutlineBorder, modifier = Modifier.size(48.dp))
                            Text(part.reference, style = MonoLargeStyle, color = OnSurfaceMuted)
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .defaultMinSize(minHeight = 44.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(role = Role.Button) { camera.launch(null) },
                        color = SurfaceLowest,
                        contentColor = OnSurfaceInk,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(2.dp, OutlineBorder),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(copy.addAnotherPhoto, style = MaterialTheme.typography.labelSmall, color = OnSurfaceInk)
                        }
                    }
                }
            }

            // Title & Vehicle Metadata Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = part.name(language),
                        style = MaterialTheme.typography.headlineLarge,
                        color = OnSurfaceInk,
                        modifier = Modifier.semantics { heading() },
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceLowest,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(2.dp, OutlineSubtle),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                MetadataCell(
                                    label = copy.vehicleLabel,
                                    value = vehicle?.let { "${it.make} ${it.model} ${it.year}" } ?: "—",
                                    modifier = Modifier.weight(1f),
                                )
                                MetadataCell(
                                    label = copy.categoryLabel,
                                    value = part.category(language).ifBlank { "—" },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            HorizontalDivider(color = OutlineSubtle)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                MetadataCell(
                                    label = copy.condition,
                                    value = copy.condition(part.condition),
                                    modifier = Modifier.weight(1f),
                                )
                                MetadataCell(
                                    label = copy.location,
                                    value = part.locationNote.ifBlank { "—" },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }

            // Price & Payment Method Selector Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SurfaceLowest,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(2.dp, OutlineBorder),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            Text(copy.totalToCharge, style = MaterialTheme.typography.labelLarge, color = OnSurfaceMuted)
                            Text(priceDisplay, style = MonoDisplayStyle, color = OnSurfaceInk)
                        }

                        if (isAvailable) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(copy.paymentMethod, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    listOf(
                                        PaymentMethod.Cash to copy.payCash,
                                        PaymentMethod.Transfer to copy.payTransfer,
                                        PaymentMethod.Card to copy.payCard,
                                    ).forEach { (method, label) ->
                                        val selected = payment == method
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .defaultMinSize(minHeight = 48.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable(role = Role.Button) { paymentName = method.name },
                                            color = if (selected) SecondaryCobalt else SurfaceLowest,
                                            contentColor = if (selected) OnSecondary else OnSurfaceInk,
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(2.dp, if (selected) SecondaryCobalt else OutlineSubtle),
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (selected) OnSecondary else OnSurfaceInk,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceMuted)
        Text(value, style = MaterialTheme.typography.titleMedium, color = OnSurfaceInk, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

// ============================================================================
// TAB 4: Ventas (QR Fast Sale + Sold Parts History)
// ============================================================================
@Composable
private fun SalesTab(
    copy: Copy,
    language: AppLanguage,
    user: StaffUser,
    repository: InventoryRepository,
    vehicles: List<Vehicle>,
    parts: List<Part>,
    onOpenDetail: (String) -> Unit,
    snackbar: SnackbarHostState,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val available = remember(query, parts) {
        val needle = normalize(query)
        parts.filter {
            it.status == InventoryStatus.Available &&
                (needle.isBlank() || normalize(listOf(it.reference, it.nameEs, it.nameEn, it.locationNote).joinToString(" ")).contains(needle))
        }
    }
    val sold = remember(parts) { parts.filter { it.status == InventoryStatus.Sold } }

    val scanner = rememberLauncherForActivityResult(ScanContract()) { result ->
        val raw = result.contents ?: return@rememberLauncherForActivityResult
        val id = raw.substringAfterLast('/')
        val match = parts.firstOrNull { it.id == id || it.reference.equals(raw, ignoreCase = true) }
        if (match != null) {
            onOpenDetail(match.id)
        } else {
            scope.launch { snackbar.showSnackbar(copy.noResults) }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            YardSectionCard(title = copy.sellPart, borderColor = OutlineBorder) {
                PrimaryButton(
                    text = copy.scanLabel,
                    icon = Icons.Default.QrCodeScanner,
                    onClick = {
                        scanner.launch(
                            ScanOptions()
                                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                                .setPrompt(copy.searchExample)
                                .setBeepEnabled(false)
                                .setOrientationLocked(false),
                        )
                    },
                )
                YardTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = copy.manualReference,
                    placeholder = copy.searchPlaceholder,
                    leadingIcon = Icons.Default.Search,
                )
            }
        }

        if (available.isEmpty()) {
            item { EmptyMessage(copy.noResults) }
        }
        items(available, key = { it.id }) { part ->
            PartActionCard(
                part = part,
                vehicle = vehicles.firstOrNull { it.id == part.vehicleId },
                language = language,
                copy = copy,
                onOpenDetail = { onOpenDetail(part.id) },
                onQuickAction = { onOpenDetail(part.id) },
                quickActionLabel = copy.sell,
            )
        }

        if (sold.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(copy.soldHistory, style = MaterialTheme.typography.titleLarge, color = OnSurfaceInk)
                    if (user.role == StaffRole.Owner) {
                        Text(copy.restoreHelp, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
                    }
                }
            }
            items(sold, key = { "sold-${it.id}" }) { part ->
                PartActionCard(
                    part = part,
                    vehicle = vehicles.firstOrNull { it.id == part.vehicleId },
                    language = language,
                    copy = copy,
                    onOpenDetail = { onOpenDetail(part.id) },
                    onQuickAction = if (user.role == StaffRole.Owner) {
                        {
                            scope.launch {
                                when (repository.restorePart(part.id)) {
                                    is SaveResult.Saved -> snackbar.showSnackbar(copy.saved)
                                    SaveResult.Queued -> snackbar.showSnackbar(copy.waitingInternet)
                                    is SaveResult.Failed -> snackbar.showSnackbar(copy.somethingWrong)
                                }
                            }
                        }
                    } else {
                        null
                    },
                    quickActionLabel = if (user.role == StaffRole.Owner) copy.restore else null,
                    isRestoreAction = true,
                )
            }
        }
    }
}

// ============================================================================
// SCREEN 5 (TAB 5): Equipo y Dueño (Stitch 05_equipo_dueno.html)
// ============================================================================
@Composable
private fun TeamOwnerTab(
    copy: Copy,
    language: AppLanguage,
    user: StaffUser,
    repository: InventoryRepository,
    vehicles: List<Vehicle>,
    parts: List<Part>,
    onOpenDetail: (String) -> Unit,
    onSignOut: () -> Unit,
    snackbar: SnackbarHostState,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val staff by repository.staff.collectAsState()
    val sold = remember(parts) { parts.filter { it.status == InventoryStatus.Sold } }
    val isOwner = user.role == StaffRole.Owner

    var enrollmentCode by rememberSaveable { mutableStateOf<String?>(null) }
    var showAddStaff by rememberSaveable { mutableStateOf(false) }
    var staffName by rememberSaveable { mutableStateOf("") }
    var staffPin by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(isOwner) {
        if (isOwner && enrollmentCode == null) {
            when (val res = repository.createEnrollmentCode()) {
                is SaveResult.Saved -> enrollmentCode = res.value
                else -> Unit
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().imePadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // 6-Digit Phone Enrollment Code Card (Owner Only — Stitch 05_equipo_dueno.html)
        if (isOwner) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimaryContainer,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(2.dp, PrimaryOrange),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(copy.linkNewPhoneTitle, style = MaterialTheme.typography.titleLarge, color = OnPrimaryContainer)
                                Text(copy.linkNewPhoneHelp, style = MaterialTheme.typography.bodyMedium, color = OnPrimaryContainer)
                            }
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.PhonelinkLock, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(28.dp))
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = SurfaceLowest,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(2.dp, PrimaryOrange),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = enrollmentCode?.let(::formatSixDigitCode) ?: "— — —",
                                    style = MonoDisplayStyle.copy(letterSpacing = 3.sp),
                                    color = OnSurfaceInk,
                                )
                                StatusBadge(
                                    text = copy.expires10m,
                                    containerColor = WarningContainer,
                                    contentColor = OnWarningContainer,
                                )
                            }
                        }

                        PrimaryButton(
                            text = copy.generateNewCode,
                            icon = Icons.Default.Refresh,
                            onClick = {
                                scope.launch {
                                    when (val result = repository.createEnrollmentCode()) {
                                        is SaveResult.Saved -> enrollmentCode = result.value
                                        else -> snackbar.showSnackbar(copy.somethingWrong)
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }

        // Staff List Header + Inline Add Button
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${copy.staffWithPin} (${staff.size})",
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurfaceInk,
                        modifier = Modifier.semantics { heading() },
                    )
                    if (isOwner) {
                        OutlinedButton(
                            onClick = { showAddStaff = !showAddStaff },
                            modifier = Modifier.defaultMinSize(minHeight = 44.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = SurfaceLowest,
                                contentColor = SecondaryCobalt,
                            ),
                            border = BorderStroke(2.dp, SecondaryCobalt),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(copy.addShort, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                if (isOwner && showAddStaff) {
                    YardSectionCard(title = copy.addStaff, borderColor = SecondaryCobalt) {
                        YardTextField(
                            value = staffName,
                            onValueChange = { staffName = it },
                            label = copy.staffName,
                            placeholder = "Ej. Carlos Ruiz",
                            hint = copy.staffNameHint,
                        )
                        YardTextField(
                            value = staffPin,
                            onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) staffPin = it },
                            label = "PIN",
                            placeholder = "4 dígitos",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            visualTransformation = PasswordVisualTransformation(),
                        )
                        PrimaryButton(
                            text = copy.addStaff,
                            icon = Icons.Default.PersonAdd,
                            enabled = staffName.trim().length >= 2 && staffPin.length == 4,
                            onClick = {
                                scope.launch {
                                    when (repository.addStaff(staffName.trim(), staffPin)) {
                                        is SaveResult.Saved -> {
                                            staffName = ""
                                            staffPin = ""
                                            showAddStaff = false
                                            snackbar.showSnackbar(copy.saved)
                                        }
                                        else -> snackbar.showSnackbar(copy.somethingWrong)
                                    }
                                }
                            },
                        )
                    }
                }

                // Staff Cards (Stitch 05_equipo_dueno.html)
                staff.forEachIndexed { index, member ->
                    val memberIsOwner = member.role == StaffRole.Owner
                    val avatarBg = when {
                        memberIsOwner -> PrimaryContainer
                        index % 2 == 0 -> SecondaryContainer
                        else -> SurfaceContainer
                    }
                    val avatarFg = when {
                        memberIsOwner -> OnPrimaryContainer
                        index % 2 == 0 -> OnSecondaryContainer
                        else -> OnSurfaceInk
                    }
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceLowest,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(2.dp, OutlineSubtle),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(avatarBg),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = member.name.take(1).uppercase(),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = avatarFg,
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(member.name, style = MaterialTheme.typography.titleMedium, color = OnSurfaceInk)
                                    Text(
                                        text = if (memberIsOwner) copy.ownerRoleLabel else copy.staffRoleLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnSurfaceMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            StatusBadge(
                                text = if (memberIsOwner) copy.owner else copy.active,
                                containerColor = if (memberIsOwner) SecondaryContainer else SuccessContainer,
                                contentColor = if (memberIsOwner) OnSecondaryContainer else OnSuccessContainer,
                            )
                        }
                    }
                }
            }
        }

        // QR Labels Export (Owner Only)
        if (isOwner) {
            item {
                YardSectionCard(title = copy.generateLabels) {
                    Text(copy.labelsHelp, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
                    SecondaryButton(
                        text = copy.generateLabels,
                        icon = Icons.Default.PictureAsPdf,
                        onClick = {
                            LabelPdfExporter.share(context, parts.filter { it.status == InventoryStatus.Available })
                        },
                    )
                }
            }
        }

        // Sold Parts Recovery (Owner Only)
        if (isOwner && sold.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(copy.soldHistory, style = MaterialTheme.typography.titleLarge, color = OnSurfaceInk)
                    Text(copy.restoreHelp, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
                }
            }
            items(sold, key = { "owner-sold-${it.id}" }) { part ->
                PartActionCard(
                    part = part,
                    vehicle = vehicles.firstOrNull { it.id == part.vehicleId },
                    language = language,
                    copy = copy,
                    onOpenDetail = { onOpenDetail(part.id) },
                    onQuickAction = {
                        scope.launch {
                            when (repository.restorePart(part.id)) {
                                is SaveResult.Saved -> snackbar.showSnackbar(copy.saved)
                                SaveResult.Queued -> snackbar.showSnackbar(copy.waitingInternet)
                                is SaveResult.Failed -> snackbar.showSnackbar(copy.somethingWrong)
                            }
                        }
                    },
                    quickActionLabel = copy.restore,
                    isRestoreAction = true,
                )
            }
        }

        // Sign Out Action
        item {
            SecondaryButton(
                text = copy.signOut,
                icon = Icons.AutoMirrored.Filled.Logout,
                subtleBorder = true,
                onClick = onSignOut,
            )
        }
    }
}

private fun normalize(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFD)
    .replace(Regex("\\p{Mn}+"), "")
    .lowercase()
