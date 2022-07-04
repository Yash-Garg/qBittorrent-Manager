package dev.yashgarg.qbit.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.yashgarg.qbit.R

class AddTorrentDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Magnet Link")
            .setView(R.layout.edit_text)
            .setNeutralButton("Upload File") { _, _ -> }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Add") { _, _ ->
                // Respond to positive button press
            }
            .show()
    }

    companion object {
        fun newInstance(): AddTorrentDialog = AddTorrentDialog()
    }
}
