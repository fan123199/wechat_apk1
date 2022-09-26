package im.fdx.openapk

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import im.fdx.openapk.ui.theme.OpenApkTheme
import java.io.File
import java.io.FileInputStream


const val TAG = "openapk"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.data
        //content://com.tencent.mm.external.fileprovider/external/Android/data/com.tencent.mm/MicroMsg/Download/F-Droid.apk.1
        data?.let { installApk(this, it) }

        setContent {
            OpenApkTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Spacer(modifier = Modifier.size(1.dp))
                }
            }
        }
    }

    fun installApk(context: Context, uri: Uri) {

//        val contentUri = FileProvider.getUriForFile(
//            context,
//            BuildConfig.APPLICATION_ID + ".provider",
//            File(uri.path!!)
//        )
        Log.i(TAG, "installApk: ${uri.path}")
        saveFile(this, uri)
        val uriTmp = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",  File(context.filesDir,"temp.apk"))
        val install = Intent(Intent.ACTION_VIEW)
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
        install.data = uriTmp
        context.startActivity(install)
        finish()
    }

    fun rename(context: Context, uri: Uri, rename: String) {

        //create content values with new name and update
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, rename)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.contentResolver.update(uri, contentValues, null)
        }
    }

    fun saveFile(context: Context, uri: Uri) {
            context.applicationContext.contentResolver.openFileDescriptor(uri, "r")?.use { fd ->
                FileInputStream(fd.fileDescriptor).use { fis ->
                    val f = File( context.filesDir,"temp.apk")
                    val fos = f.outputStream()
//                    val bytes = ByteArray(1024 * 1024)
//                    var readCount: Int
//                    while (true) {
//                        readCount = fis.read(bytes)
//                        if (readCount == -1) break
//                        fos.write(bytes, 0, readCount)
//                    }
                    val readBytes = fis.readBytes()
                    Log.i(TAG, readBytes.size.toString())
                    fos.write(readBytes)

                    fos.flush()
                    fos.close()
                    Log.i(TAG, "Success")
                }
            }
    }

}

