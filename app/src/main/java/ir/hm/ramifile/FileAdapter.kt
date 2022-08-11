package ir.hm.ramifile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.net.URLConnection

class FileAdapter(private val data: ArrayList<File>, private val fileEvents: FileEvents) :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {
    var ourViewType = 0

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt = itemView.findViewById<TextView>(R.id.txtFolderName)
        val img = itemView.findViewById<ImageView>(R.id.imgFolder)
        fun bindViews(file: File) {
            // memeType => http://androidxref.com/4.4.4_r1/xref/frameworks/base/media/java/android/media/MediaFile.java#174

            var typeFile: String = ""

            txt.text = file.name

            if (file.isDirectory) {
                img.setImageResource(R.drawable.ic_folder)
            } else {

                when {
                    isImage(file.path) -> {
                        img.setImageResource(R.drawable.ic_image)
                        typeFile = "image/*"
                    }

                    isVideo(file.path) -> {
                        img.setImageResource(R.drawable.ic_video)
                        typeFile = "video/*"
                    }

                    isZip(file.name) -> {
                        img.setImageResource(R.drawable.ic_zip)
                        typeFile = "application/zip"

                    }

                    else -> {
                        img.setImageResource(R.drawable.ic_file)
                        typeFile = "text/plain"
                    }
                }

            }
            itemView.setOnClickListener {

                if (file.isDirectory) {

                    fileEvents.onFolderClicked(file.path)

                } else {

                    fileEvents.onFileClicked(file, typeFile)

                }

            }

            itemView.setOnLongClickListener {
                fileEvents.onLongClicked(file, adapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view: View
        if (viewType == 0) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_file_linear, parent, false)
        } else {
            view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_file_grid, parent, false)

        }
        return FileViewHolder(view)
    }
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bindViews(data[position])
    }
    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return ourViewType
    }

    private fun isImage(path: String): Boolean {
        val mimeType: String = URLConnection.guessContentTypeFromName(path)
        return mimeType.startsWith("image")
    }
    private fun isVideo(path: String): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType.startsWith("video")
    }
    private fun isZip(name: String): Boolean {
        return name.contains(".zip") || name.contains(".rar")
    }

    fun addNewFile(file: File) {
        data.add(0, file)
        notifyItemInserted(0)
    }
    fun removeFile(oldFile: File, position: Int) {
        data.remove(oldFile)
        notifyItemRemoved(position)

    }
    fun changeViewType(newViewType:Int){
        ourViewType = newViewType
        notifyDataSetChanged()
    }

    interface FileEvents {
        fun onFolderClicked(path: String)
        fun onFileClicked(file: File, type: String)
        fun onLongClicked(file: File, position: Int)
    }

}