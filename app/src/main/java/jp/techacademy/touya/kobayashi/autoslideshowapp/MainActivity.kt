package jp.techacademy.touya.kobayashi.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Handler
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null

    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
        // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        )

        if (cursor!!.moveToFirst()) {

            // indexからIDを取得し、そのIDから画像のURLを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }


        next_button.setOnClickListener {
            if (mTimer == null) {

                if (cursor!!.moveToNext()) {

                    // indexからIDを取得し、そのIDから画像のURLを取得する
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    imageView.setImageURI(imageUri)
                } else {
                    if (cursor!!.moveToFirst()) {
                        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        val id = cursor.getLong(fieldIndex)
                        val imageUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )

                        imageView.setImageURI(imageUri)
                    }
                }
            }
        }

        back_button.setOnClickListener {
            if (mTimer == null) {

                if (cursor!!.moveToPrevious()) {

                    // indexからIDを取得し、そのIDから画像のURLを取得する
                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(fieldIndex)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    imageView.setImageURI(imageUri)
                } else {
                    if (cursor!!.moveToLast()) {
                        // indexからIDを取得し、そのIDから画像のURLを取得する
                        val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        val id = cursor.getLong(fieldIndex)
                        val imageUri =
                            ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )

                        imageView.setImageURI(imageUri)
                    }
                }
            }

        }

        auto_button.setOnClickListener {
            if (mTimer == null) {
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        if (cursor!!.moveToNext()) {
                            //取得
                            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                            val id = cursor.getLong(fieldIndex)
                            val imageUri =
                                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            mHandler.post {
                                imageView.setImageURI(imageUri)
                            }
                        } else {
                            if (cursor!!.moveToFirst()) {
                                //取得
                                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                val id = cursor.getLong(fieldIndex)
                                val imageUri =
                                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                                mHandler.post {
                                    imageView.setImageURI(imageUri)
                                }
                            }
                        }
                    }
                }, 2000, 2000)
            } else {
                if (mTimer != null) {
                    mTimer!!.cancel()
                    mTimer = null
                }
            }
        }

    }
}