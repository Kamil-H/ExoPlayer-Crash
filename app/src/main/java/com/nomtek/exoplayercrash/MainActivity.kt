package com.nomtek.exoplayercrash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.nomtek.exoplayercrash.custom_views.PlaylistAdapter
import com.nomtek.exoplayercrash.extension.observeNotNull
import com.nomtek.exoplayercrash.models.show
import com.nomtek.exoplayercrash.utils.DividerDecorator
import com.nomtek.exoplayercrash.utils.Injector
import com.nomtek.exoplayercrash.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    lateinit var exoPlayer: ExoPlayer

    private val viewModel: PlayerViewModel by viewModels {
        Injector.providePlayerViewModel(this)
    }

    private val playlistAdapter: PlaylistAdapter by lazy { PlaylistAdapter(viewModel::onClicked) }

    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.inject(this)
        super.onCreate(savedInstanceState)

        setUpView()
        setUpObservables()

        container.setOnClickListener {
            viewModel.loadData()
        }
    }

    private fun setUpView() {
        playerView.player = exoPlayer
        with(recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = playlistAdapter
            addItemDecoration(DividerDecorator())
            ItemTouchHelper(SwipeToDeleteCallback(viewModel::swiped)).attachToRecyclerView(this)
        }
    }

    private fun setUpObservables() {
        observeNotNull(viewModel.items, playlistAdapter::submitList)
        observeNotNull(viewModel.snackbarMessage) { show(it, playerView) }
    }
}