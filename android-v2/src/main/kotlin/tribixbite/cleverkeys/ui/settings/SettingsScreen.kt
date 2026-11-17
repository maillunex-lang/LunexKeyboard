package tribixbite.cleverkeys.ui.settings

import android.content.Intent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tribixbite.cleverkeys.R
import tribixbite.cleverkeys.SettingsActivity
import tribixbite.cleverkeys.ThemeSettingsActivity
import tribixbite.cleverkeys.ui.settings.sections.AccessibilitySection
import tribixbite.cleverkeys.ui.settings.sections.ActivitiesSection
import tribixbite.cleverkeys.ui.settings.sections.AdvancedSection
import tribixbite.cleverkeys.ui.settings.sections.VoiceInputSection
import tribixbite.cleverkeys.ui.settings.sections.AppearanceSection
import tribixbite.cleverkeys.ui.settings.sections.AutoCorrectionSection
import tribixbite.cleverkeys.ui.settings.sections.BackupRestoreSection
import tribixbite.cleverkeys.ui.settings.sections.ClipboardSection
import tribixbite.cleverkeys.ui.settings.sections.GestureTuningSection
import tribixbite.cleverkeys.ui.settings.sections.GifPanelSection
import tribixbite.cleverkeys.ui.settings.sections.HelpSection
import tribixbite.cleverkeys.ui.settings.sections.InputBehaviorSection
import tribixbite.cleverkeys.ui.settings.sections.MultiLanguageSection
import tribixbite.cleverkeys.ui.settings.sections.SwipeTypingSection
import tribixbite.cleverkeys.ui.settings.sections.PrivacySection
import tribixbite.cleverkeys.ui.settings.sections.SwipeTrailSection
import tribixbite.cleverkeys.ui.settings.sections.TestKeyboardSection
import tribixbite.cleverkeys.ui.settings.sections.VersionActionsSection

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun SettingsActivity.SettingsScreen() {
        var showAdvancedTools by rememberSaveable { mutableStateOf(false) }
        val lunexContext = LocalContext.current
        val scrollState = rememberScrollState()
        // Store references for scroll-to-setting functionality.
        // composeScope has MonotonicFrameClock needed for animateScrollTo.
        //
        // Published from SideEffect, not from the composition body (ARC-048): a body write
        // also runs for compositions that are later ABANDONED, and the scope from an
        // abandoned composition is cancelled — so the Activity kept a dead CoroutineScope
        // and every later scrollToSetting() silently no-opped. SideEffect runs only after a
        // composition is successfully applied, and still before this frame's layout pass, so
        // the onGloballyPositioned readers in SettingsControls see the same values as before.
        val scope = rememberCoroutineScope()
        SideEffect {
            mainScrollState = scrollState
            composeScope = scope
        }

        // Collected Data Viewer Dialog
        if (showCollectedDataViewer) {
            CollectedDataViewerDialog(
                dataList = collectedDataList,
                stats = collectedDataStats,
                onDismiss = { showCollectedDataViewer = false }
            )
        }

        // Performance Stats Viewer Dialog
        if (showPerfStatsViewer) {
            PerfStatsViewerDialog(
                summary = perfStatsSummary,
                onDismiss = { showPerfStatsViewer = false }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
                // The viewport's top in root space: positions recorded for search scrolling are
                // made relative to it (contentYOf), or every scroll overshoots by this offset.
                .onGloballyPositioned { scrollViewportTop = it.positionInRoot().y }
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = stringResource(R.string.settings_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = stringResource(R.string.settings_description),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            // Settings Search Bar
            val filteredSettings = getFilteredSettings(settingsSearchQuery)
            val showResults = settingsSearchQuery.isNotBlank() && filteredSettings.isNotEmpty()

            OutlinedTextField(
                value = settingsSearchQuery,
                onValueChange = { query ->
                    settingsSearchQuery = query
                },
                label = { Text(stringResource(R.string.settings_search_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.common_search)
                    )
                },
                trailingIcon = {
                    if (settingsSearchQuery.isNotBlank()) {
                        IconButton(onClick = {
                            settingsSearchQuery = ""
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.common_clear)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Search Results - always below search field, scrollable
            // Uses nestedScroll barrier to prevent scroll propagation to parent
            AnimatedVisibility(
                visible = showResults,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .nestedScroll(searchResultsNestedScrollConnection),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(4.dp)
                    ) {
                        filteredSettings.forEach { setting ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        executeSearchAction(setting)
                                        settingsSearchQuery = ""
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = setting.title,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = stringResource(R.string.settings_search_in_section, setting.sectionName),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Default settings are deliberately concise; all original options are preserved.
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    lunexContext.startActivity(Intent(lunexContext, ThemeSettingsActivity::class.java))
                },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("✦ Liquid Glass", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text(
                        stringResource(R.string.lunex_glass_themes_desc),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            TestKeyboardSection()
            AppearanceSection()
            SwipeTypingSection()
            InputBehaviorSection()
            AutoCorrectionSection()
            ClipboardSection()
            MultiLanguageSection()

            OutlinedButton(
                onClick = { showAdvancedTools = !showAdvancedTools },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    if (showAdvancedTools) stringResource(R.string.lunex_hide_advanced)
                    else stringResource(R.string.lunex_show_advanced)
                )
            }
            if (showAdvancedTools) {
                ActivitiesSection()
                SwipeTrailSection()
                GestureTuningSection()
                AccessibilitySection()
                GifPanelSection()
                VoiceInputSection()
                BackupRestoreSection()
                PrivacySection()
                AdvancedSection()
                VersionActionsSection()
                HelpSection()
            }
        }
    }
