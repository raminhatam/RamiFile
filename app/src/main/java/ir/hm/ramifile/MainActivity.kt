package ir.hm.ramifile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import ir.hm.ramifile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    companion object {
        // ourViewType -> Linear = 0
        // ourViewType -> Grid = 1
        var ourViewType = 0

        // count of the column
        var ourSpanCount = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // مسیر ذخیر سازی فایل را در متغیر file میریزیم و با استفاده از path آن را به برنامه میدهیم
       // val file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val file = getExternalFilesDir(null)!!
        val path = file.path

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frame_main_container, FragmentFile(path))
        transaction.commit()

    }
}