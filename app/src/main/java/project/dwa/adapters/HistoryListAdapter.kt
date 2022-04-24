package project.dwa.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import project.dwa.R
import project.dwa.models.GameHistoryItem

class HistoryListAdapter(
    private val context: Context,
    private val historyItemList: List<GameHistoryItem>
): RecyclerView.Adapter<HistoryListAdapter.RecyclerViewHolder>() {
    class RecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // creating variables for our views.
        val historyIV: LinearLayout = itemView.findViewById(R.id.history_item_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        // Inflate Layout in this method which we have created
        val view: View = LayoutInflater.from(context).inflate(
            R.layout.history_item,
            parent,
            false
        )

        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        // Get the item
        val item = historyItemList[position]

        // Write text to each textview
        (holder.historyIV.getChildAt(0) as TextView).text = item.getFormattedPLayDate()
        (holder.historyIV.getChildAt(2) as TextView).text = item.winner.name
        (holder.historyIV.getChildAt(4) as TextView).text = item.getOtherPlayersString()
    }

    override fun getItemCount(): Int {
        return historyItemList.size
    }
}