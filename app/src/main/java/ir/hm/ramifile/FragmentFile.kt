package ir.hm.ramifile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ir.hm.ramifile.databinding.DialogRemoveItemBinding
import ir.hm.ramifile.databinding.FragmentAddFileBinding
import ir.hm.ramifile.databinding.FragmentAddFolderBinding
import ir.hm.ramifile.databinding.FragmentFileBinding
import java.io.File

class FragmentFile(val  path: String) : Fragment(), FileAdapter.FileEvents {

    lateinit var binding: FragmentFileBinding
    lateinit var myAdapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (MainActivity.ourViewType == 0){
            binding.btnShowType.setImageResource(R.drawable.ic_list)
        }else{
            binding.btnShowType.setImageResource(R.drawable.ic_grid)
        }

        // path مسیر فایل ما را به برنامه میدهد از طریق مسیر تعیین شده در MainActivity
        val ourFile = File(path)
        binding.txtPath.text = ourFile.name + ">"

        // با استفاده از ourFile.isDirectory برنامه میفهمد که فایل داریم یا پوشه
        if (ourFile.isDirectory) {

            val listOfFiles = arrayListOf<File>()
            listOfFiles.addAll(ourFile.listFiles()!!)
            listOfFiles.sort()

            myAdapter = FileAdapter(listOfFiles, this)
            binding.recyclerMain.adapter = myAdapter
            binding.recyclerMain.layoutManager = GridLayoutManager(context, MainActivity.ourSpanCount, LinearLayoutManager.VERTICAL, false)
            myAdapter.changeViewType(MainActivity.ourViewType)
            // این شرط تعیین می کند که آیا فایل یا پوشه ای هست یا نه
            if (listOfFiles.size > 0) {

                binding.recyclerMain.visibility = View.VISIBLE
                binding.imgNoData.visibility = View.GONE

            } else {

                binding.recyclerMain.visibility = View.GONE
                binding.imgNoData.visibility = View.VISIBLE

            }
        }

        binding.addNewFolder.setOnClickListener {
            createNewFolder()
        }

        binding.addNewFile.setOnClickListener {
            createNewFile()
        }

        binding.btnShowType.setOnClickListener {

            if (MainActivity.ourViewType == 0){

                MainActivity.ourViewType =1
                MainActivity.ourSpanCount = 3
                myAdapter.changeViewType(MainActivity.ourViewType)
                binding.recyclerMain.layoutManager = GridLayoutManager(context, MainActivity.ourSpanCount)
                binding.btnShowType.setImageResource(R.drawable.ic_grid)

            }else {
                MainActivity.ourViewType = 0
                MainActivity.ourSpanCount = 1
                myAdapter.changeViewType(MainActivity.ourViewType)
                binding.recyclerMain.layoutManager = GridLayoutManager(context, MainActivity.ourSpanCount)
                binding.btnShowType.setImageResource(R.drawable.ic_list)
            }

        }

    }

    private fun createNewFolder() {

        val dialog = AlertDialog.Builder(context).create()
        val addFolderBinding = FragmentAddFolderBinding.inflate(layoutInflater)
        dialog.setView(addFolderBinding.root)

        dialog.show()

        addFolderBinding.btnCreate.setOnClickListener {

            val nameOfNewFolder = addFolderBinding.edtFolderName.text.toString()
            val newFile = File(path + File.separator + nameOfNewFolder)
            if (!newFile.exists()) {
                if (newFile.mkdir()) {
                    myAdapter.addNewFile(newFile)
                    binding.recyclerMain.visibility = View.VISIBLE
                    binding.recyclerMain.scrollToPosition(0)
                    binding.imgNoData.visibility = View.GONE
                }
            }
            dialog.dismiss()
        }

        addFolderBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun createNewFile() {

        val dialog = AlertDialog.Builder(context).create()
        val addFileBinding = FragmentAddFileBinding.inflate(layoutInflater)
        dialog.setView(addFileBinding.root)

        dialog.show()

        addFileBinding.btnCreate.setOnClickListener {

            val nameOfNewFolder = addFileBinding.edtFileName.text.toString()
            val newFile = File(path + File.separator + nameOfNewFolder)
            if (!newFile.exists()) {
                if (newFile.createNewFile()) {
                    myAdapter.addNewFile(newFile)
                    binding.recyclerMain.visibility = View.VISIBLE
                    binding.recyclerMain.scrollToPosition(0)
                    binding.imgNoData.visibility = View.GONE
                }
            }
            dialog.dismiss()
        }

        addFileBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }


    }

    override fun onFolderClicked(path: String) {

        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main_container, FragmentFile(path))
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun onFileClicked(file: File, type: String) {

        //چک کردن برای ورژن اندروید 7 به بالا و پایین
        val intent = Intent(Intent.ACTION_VIEW)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            val fileProvider = FileProvider.getUriForFile(
                requireContext(),
                requireActivity().packageName + ".provider",
                file
            )

            intent.setDataAndType(fileProvider, type)

        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
        }

        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivity(intent)

    }

    override fun onLongClicked(file: File, position: Int) {
        val dialog =AlertDialog.Builder(context).create()
        val dialogRemoveItemBinding = DialogRemoveItemBinding.inflate(layoutInflater)

        dialog.setView(dialogRemoveItemBinding.root)
        dialog.show()

        dialogRemoveItemBinding.btnCreate.setOnClickListener {
            if (file.exists()){ // در اینجا بررسی می کنیم توسط exists که اصلا همچین فایلی وجود دارد یا نه
                if (file.deleteRecursively()){ // از طریق متد deleteRecursively وقتی فولدری را پاک می کنیم محتویات داخلش هم از حافظه پاک می شود
                    myAdapter.removeFile(file, position)
                }
            }
            dialog.dismiss()
        }

        dialogRemoveItemBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }




    }
}