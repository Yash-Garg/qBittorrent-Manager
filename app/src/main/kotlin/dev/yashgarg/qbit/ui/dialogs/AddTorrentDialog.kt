package dev.yashgarg.qbit.ui.dialogs

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.utils.ClipboardUtil
import dev.yashgarg.qbit.utils.PermissionUtil
import dev.yashgarg.qbit.validation.LinkValidator

class AddTorrentDialog : DialogFragment() {
    private val linkValidator by lazy { LinkValidator() }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (!uris.isNullOrEmpty()) {
                setFragmentResult(ADD_TORRENT_FILE_KEY, bundleOf(TORRENT_KEY to uris))
                dismiss()
            } else {
                Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        // TODO: Switch to string resources below
        alertDialogBuilder.apply {
            setTitle("Add Magnet Link")
            setView(R.layout.edit_text)
            setNeutralButton("Upload File", null)
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            setPositiveButton("Add", null)
        }

        val dialog = alertDialogBuilder.create()

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog.setOnShowListener {
            val magnetTil = dialog.findViewById<TextInputLayout>(R.id.magnet_til)
            val magnetTiet = dialog.findViewById<TextInputEditText>(R.id.magnet_tiet)

            magnetTil?.setEndIconOnClickListener {
                val clipText = ClipboardUtil.getClipboardText(requireContext())
                magnetTiet?.setText(clipText)
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val magnetUri = magnetTiet?.text.toString()
                if (!magnetTiet?.text.isNullOrEmpty() && linkValidator.isValid(magnetUri)) {
                    setFragmentResult(ADD_TORRENT_KEY, bundleOf(TORRENT_KEY to magnetUri))
                    dialog.dismiss()
                } else {
                    magnetTil?.error = "Please enter a valid link!"
                }

                magnetTiet?.doAfterTextChanged { magnetTil?.error = null }
            }

            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                if (PermissionUtil.canReadStorage(requireContext())) {
                    filePickerLauncher.launch(TORRENT_MIMETYPE)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }

        return dialog
    }

    companion object {
        fun newInstance(): AddTorrentDialog = AddTorrentDialog()

        const val TAG = "AddTorrentDialogFragment"
        const val ADD_TORRENT_KEY = "add_torrent"
        const val ADD_TORRENT_FILE_KEY = "add_torrent_file"
        const val TORRENT_KEY = "torrent"
        const val TORRENT_MIMETYPE = "application/x-bittorrent"
    }
}
