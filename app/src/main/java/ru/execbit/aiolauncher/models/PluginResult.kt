package ru.execbit.aiolauncher.models

import android.content.ComponentName
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import android.view.ViewGroup
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

object PluginIntentActions {
    const val PLUGIN_SETTINGS = "ru.execbit.aiolauncher.PLUGIN_SETTINGS"
    const val PLUGIN_GET_DATA = "ru.execbit.aiolauncher.PLUGIN_GET_DATA"
    const val PLUGIN_SEND_ACTION = "ru.execbit.aiolauncher.PLUGIN_SEND_ACTION"
    const val AIO_UPDATE = "ru.execbit.aiolauncher.AIO_UPDATE"
}

@Parcelize
class PluginResult(
    // ComponentName of the plugin
    val from: ComponentName? = null,

    // One of:
    // * PluginLines
    // * PluginTable
    // * PluginButtons
    // * PluginProgressBars
    // * PluginChart
    // * PluginDialog
    // * PluginMenu
    // * PluginMessage
    // * PluginError
    // * PendingIntent
    // * PluginActivity
    val data: Parcelable? = null
) : Parcelable

// Data types

@Parcelize
data class SearchPluginLines(
    val lines: List<PluginLine>,
    val foldable: Boolean = true,
): Parcelable

@Parcelize
data class SearchPluginButtons(
    val buttons: List<PluginButton>,
    val foldable: Boolean = true,
): Parcelable

@Parcelize
data class PluginLines(
    val lines: List<PluginLine>,
    val maxLines: Int = 3,
    val foldable: Boolean = true,
    val privateModeSupport: Boolean = false,
    // Not used
    val showDots: Boolean = false
) : Parcelable

@Parcelize
data class PluginLinesFoldable(
    val lines: List<PluginLine>,
    val maxLines: Int = 3,
    val foldable: Boolean = true,
    val privateModeSupport: Boolean = false,
    var foldedString: String = "",
    // Not used
    val showDots: Boolean = false
) : Parcelable

@Parcelize
data class PluginTable(
    var table: List<List<PluginLine>>,
    val mainColumn: Int = -1,
    val centering: Boolean = false,
    val foldable: Boolean = true,
    var foldedString: String = "",
    val foldedTable: List<PluginLine> = emptyList(),
    val privateModeSupport: Boolean = false,
) : Parcelable

@Parcelize
data class PluginButtons(
    val buttons: List<PluginButton>,
    val maxLines: Int = 3,
    val foldable: Boolean = true,
    val privateModeSupport: Boolean = false
) : Parcelable

@Parcelize
data class PluginProgressBars(
    val bars: List<PluginProgressBar>,
    val maxLines: Int = 3,
    val foldable: Boolean = true,
    val privateModeSupport: Boolean = false
) : Parcelable

@Parcelize
data class PluginChart(
    val title: String,
    val points: List<PluginPoint>,
    val format: String = "",
    val foldedString: String = "",
    val copyright: String = "",
    val showGrid: Boolean = false,
) : Parcelable

@Parcelize
data class PluginPoint(
    val x: Float,
    val y: Float,
) : Parcelable

@Parcelize
data class PluginMenu(
    val buttons: List<PluginButton>
) : Parcelable

@Parcelize
data class PluginLine(
    var body: String = "",
    val from: String = "",
    // Not implemented yet
    val textColor: Int = 0,
    // Not implemented yet
    val backgroundColor: Int = 0,
    // Not implemented yet
    val dotColor: Int = 0,
    // "nohtml" - disable HTML formatting
    val extra: String = "",
    val clickable: Boolean = false,
    val id: Int
) : Parcelable

@Parcelize
data class PluginButton(
    // If text starts with "fa:" it will be interpreted as fontawesome symbol name
    var text: String = "",
    // Used only by menu buttons
    val icon: Bitmap? = null,
    // Not implemented yet
    val textColor: Int = 0,
    var backgroundColor: Int = 0,
    // Not implemented yet, used only by regular buttons
    val badge: Int = 0,
    // "hidemenu=true|false" - when used in context menu this flag indicating that menu should
    // or should not be hidden after click
    val extra: String = "",
    val clickAnimation: Boolean = false,
    val id: Int
) : Parcelable

@Parcelize
data class PluginProgressBar(
    var text: String = "",
    val maxValue: Float = 0f,
    val currentValue: Float = 0f,
    // Not implemented yet
    val textColor: Int = 0,
    val color: Int = 0,
    val extra: String = "",
    val id: Int
) : Parcelable

@Parcelize
data class PluginEmbeds(
    val commands: List<String>
) : Parcelable

// Only for scripts
@Parcelize
data class PluginRawView(
    val view: @RawValue ViewGroup
) : Parcelable

@Parcelize
data class PluginDialog(
    // If there are two lines here, then the second will be a subtitle
    val title: String,
    val text: String = "",
    val radioButtons: List<PluginRadioButton>? = null,
    val checkBoxes: List<PluginCheckBox>? = null,
    val bottomButtons: List<PluginButton>? = null
) : Parcelable

@Parcelize
data class PluginEditDialog(
    // If there are two lines here, then the second will be a subtitle
    val title: String,
    val description: String = "",
    val text: String
) : Parcelable

@Parcelize
data class PluginListDialog(
    // If there are two lines here, then the second will be a subtitle
    val title: String,
    val lines: List<String>,
    // The sequence number of the word (from zero),
    // after which the line will be divided into left and right parts
    val splitSymbol: String = "",
    val needSearch: Boolean = true,
    val needZebra: Boolean = true,
) : Parcelable

@Parcelize
data class PluginRadioButton(
    val text: String,
    var checked: Boolean = false,
    val id: Int
) : Parcelable

@Parcelize
data class PluginCheckBox(
    val text: String,
    var checked: Boolean = false,
    val id: Int
) : Parcelable

@Parcelize
data class PluginCallDialog(
    val number: String,
    val contactId: Int
) : Parcelable

// Added in AIO Launcher 5.4.2
@Parcelize
data class PluginCardProgress(
    val percent: Float,
) : Parcelable

@Parcelize
data class PluginError(
    // 0 - ok
    // 1 - no permission
    // 2 - exception
    // 4 - invalid action
    // 5 - need to update AIO
    // >= 100 - other
    val errorCode: Int,
    val errorText: String
) : Parcelable

@Parcelize
data class PluginMessage(
    val text: String
) : Parcelable

// This is just a hack for chinese phones that blocks PendingIntents
@Parcelize
data class PluginActivity(
    val action: String,
    val data: Uri? = null,
    val component: ComponentName? = null
) : Parcelable

@Parcelize
class PluginHideCard : Parcelable

@Parcelize
class PluginShowCard : Parcelable

/*** From launcher to plugin ***/

@Parcelize
open class PluginAction(
    // tap - user clicked item
    // longtap - user long clicked item
    // dialog - user clicked dialog element
    // menu - user clicked menu element
    val context: String = "tap",
    val selectedIds: List<Int>
) : Parcelable

@Parcelize
class PluginExtAction(
    val cxt: String,
    val ids: List<Int>,
    val extra: @RawValue Any,
) : PluginAction(cxt, ids)
