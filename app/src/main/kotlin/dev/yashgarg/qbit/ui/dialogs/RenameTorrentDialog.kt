package dev.yashgarg.qbit.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.yashgarg.qbit.R

class RenameTorrentDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        // TODO: Switch to string resources below
        alertDialogBuilder.apply {
            setTitle("Rename torrent")
            setView(R.layout.rename_torrent_dialog)
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            setPositiveButton("Rename", null)
        }

        val title = arguments?.getString(TORRENT_NAME_KEY)
        val dialog = alertDialogBuilder.create()
        dialog.window?.setSoftInputMode(5)

        dialog.setOnShowListener {
            val nameTil = dialog.findViewById<TextInputLayout>(R.id.torrentName_til)
            val nameTiet = dialog.findViewById<TextInputEditText>(R.id.torrentName_tiet)
            nameTiet?.setText(title)
            nameTiet?.setSelection(title?.length ?: 0)

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (!nameTiet?.text.isNullOrEmpty()) {
                    setFragmentResult(
                        RENAME_TORRENT_KEY,
                        bundleOf(RENAME_KEY to nameTiet?.text.toString())
                    )
                    dialog.dismiss()
                } else {
                    nameTil?.error = "Please enter a valid name"
                }
            }
        }

        return dialog
    }

    companion object {
        fun newInstance(): RenameTorrentDialog = RenameTorrentDialog()
        const val TAG = "RenameTorrentDialogFragment"
        const val TORRENT_NAME_KEY = "torrent_name"
        const val RENAME_TORRENT_KEY = "rename_torrent"
        const val RENAME_KEY = "rename_fragment"
    }
}
