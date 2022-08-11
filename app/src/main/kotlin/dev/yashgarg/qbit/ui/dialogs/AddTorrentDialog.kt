package dev.yashgarg.qbit.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.utils.TextUtils

class AddTorrentDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        alertDialogBuilder.setTitle("Add Magnet Link")
        alertDialogBuilder.setView(R.layout.edit_text)
        alertDialogBuilder.setNeutralButton("Upload File", null)
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        alertDialogBuilder.setPositiveButton("Add", null)

        val dialog = alertDialogBuilder.create()

        dialog.window?.setSoftInputMode(5)
        dialog.setOnShowListener {
            val magnetTil = dialog.findViewById<TextInputLayout>(R.id.magnet_til)
            val magnetTiet = dialog.findViewById<TextInputEditText>(R.id.magnet_tiet)

            magnetTil?.setEndIconOnClickListener {
                val clipText = TextUtils.getClipboardText(requireContext())
                magnetTiet?.setText(clipText)
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (!magnetTiet?.text.isNullOrEmpty()) {
                    setFragmentResult(
                        ADD_TORRENT_KEY,
                        bundleOf(TORRENT_KEY to magnetTiet?.text.toString())
                    )
                    dialog.dismiss()
                } else {
                    magnetTil?.error = "Please enter a url"
                }

                magnetTiet?.doAfterTextChanged { magnetTil?.error = null }
            }
        }

        return dialog
    }

    companion object {
        fun newInstance(): AddTorrentDialog = AddTorrentDialog()
        const val TAG = "AddTorrentDialogFragment"
        const val ADD_TORRENT_KEY = "add_torrent"
        const val TORRENT_KEY = "torrent"
    }
}
