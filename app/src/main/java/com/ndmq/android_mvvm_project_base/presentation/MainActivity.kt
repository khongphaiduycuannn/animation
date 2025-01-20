package com.ndmq.android_mvvm_project_base.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.ndmq.android_mvvm_project_base.base.BaseActivity
import com.ndmq.android_mvvm_project_base.custom_view.draw_view.DrawView
import com.ndmq.android_mvvm_project_base.data.source.asset.StickerUtils
import com.ndmq.android_mvvm_project_base.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(
    ActivityMainBinding::inflate
) {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            StickerUtils.loadStickersAssetsBitmap(
                this@MainActivity,
                doOnLoadSuccess = {
                    DrawView.loadStickers(it)
                }
            )
        }
    }
}