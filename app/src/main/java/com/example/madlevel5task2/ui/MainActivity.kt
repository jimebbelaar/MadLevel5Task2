package com.example.madlevel5task2.ui

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.madlevel5task2.R
import com.example.madlevel5task2.adapter.GameAdapter
import com.example.madlevel5task2.model.Game

const val ADD_GAME_REQUEST_CODE = 100

class MainActivity : AppCompatActivity() {

    private val games = arrayListOf<Game>()
    private val gameAdapter = GameAdapter(games)
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initViews()
        initViewModel()
    }

    private fun initViews() {
        rvGames.layoutManager =
            LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        rvGames.adapter = gameAdapter
        createItemTouchHelper().attachToRecyclerView(rvGames)

        fab.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        viewModel.games.observe(this, Observer { reminders ->
            this@MainActivity.games.clear()
            this@MainActivity.games.addAll(reminders)
            gameAdapter.notifyDataSetChanged()
        })
    }

    private fun showUndoSnackbar(game: Game) {
        val snackbar = Snackbar.make(
            findViewById(R.id.root),
            getString(R.string.success_game), Snackbar.LENGTH_SHORT
        )
        snackbar.setAction(getString(R.string.undo)) {
            viewModel.insertGame(game)
        }
        snackbar.show()
    }

    private fun showUndoSnackbar(games: List<Game>) {
        val snackbar = Snackbar.make(
            findViewById(R.id.root),
            getString(R.string.success_backlog), Snackbar.LENGTH_SHORT
        )
        snackbar.setAction(getString(R.string.undo)) {
            games.forEach { game ->
                viewModel.insertGame(game)
            }

        }
        snackbar.show()
    }

    private fun createItemTouchHelper(): ItemTouchHelper {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val game = games[viewHolder.adapterPosition]
                viewModel.deleteGame(games[viewHolder.adapterPosition])
                showUndoSnackbar(game)
            }
        }

        return ItemTouchHelper(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                val games = games.toList()
                viewModel.deleteAllGames()
                showUndoSnackbar(games)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}