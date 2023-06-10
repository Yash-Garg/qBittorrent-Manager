package dev.yashgarg.qbit.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.yashgarg.qbit.R

class RemoveTorrentDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        // TODO: Switch to string resources below
        alertDialogBuilder.apply {
            setTitle("Are you sure you want to delete the torrent(s)?")
            setView(R.layout.delete_files_dialog)
            setPositiveButton("Yes", null)
            setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        }

        val dialog = alertDialogBuilder.create()

        dialog.setOnShowListener {
            val deleteFilesLL = dialog.findViewById<LinearLayout>(R.id.deleteFiles_ll)
            val deleteFilesCheckBox = dialog.findViewById<CheckBox>(R.id.deleteFiles_box)

            deleteFilesLL?.setOnClickListener {
                deleteFilesCheckBox?.isChecked = !deleteFilesCheckBox!!.isChecked
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                setFragmentResult(
                    REMOVE_TORRENT_KEY,
                    bundleOf(TORRENT_KEY to deleteFilesCheckBox?.isChecked)
                )
                dialog.dismiss()
            }
        }

        return dialog
    }

    companion object {
        fun newInstance(): RemoveTorrentDialog = RemoveTorrentDialog()

        const val TAG = "RemoveTorrentDialogFragment"
        const val REMOVE_TORRENT_KEY = "remove_torrent"
        const val TORRENT_KEY = "torrent"
    }
}
