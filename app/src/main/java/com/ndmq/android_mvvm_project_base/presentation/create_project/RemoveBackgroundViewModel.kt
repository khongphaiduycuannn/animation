package com.ndmq.android_mvvm_project_base.presentation.create_project

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import com.ndmq.android_mvvm_project_base.base.BaseViewModel
import com.ndmq.android_mvvm_project_base.data.source.asset.StickerUtils
import com.ndmq.android_mvvm_project_base.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RemoveBackgroundViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val fileUtils: FileUtils
) : BaseViewModel() {

    var projectFolderName = ""

    fun saveImage(bitmap: Bitmap, onSaved: (filePngPath: String?) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                isLoading.postValue(true)
                val file = bitmap.let { bitmap ->
                    fileUtils.saveBitmapToPng("$projectFolderName/sticker_images", bitmap)
                }?.also {
                    StickerUtils.loadStickerBitmap(context, it)
                }

                withContext(Dispatchers.Main) {
                    onSaved(file)
                }
//                isLoading.postValue(false)
            } catch (ex: Exception) {
//                isLoading.postValue(false)
                ex.printStackTrace()
            }
        }
    }
}