package cs501.boggle

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.TreeSet


class MainActivity : AppCompatActivity() {

    lateinit var gameFragment: GameFragment
    lateinit var scoreFragment: ScoreFragment

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val arguments = Bundle()
        gameFragment = GameFragment()
        if(intent.extras != null) {
            if(intent.extras!!.getBoolean("flagPreload", false)) {
                val preloadLetters = "appl" +
                        "ccee" +
                        "zetp" +
                        "roxz"
                arguments.putString("preloadLetters", preloadLetters)
            }
        }

        gameFragment.arguments = arguments
        gameFragment.commitScoreChange =  { change, clear -> onScoreChange(change, clear) }

        scoreFragment = ScoreFragment()
        scoreFragment.emitNewGame = { startNewGame() }

        fragmentTransaction.add(R.id.u_game_fragment_container, gameFragment)
        fragmentTransaction.add(R.id.u_score_fragment_container, scoreFragment)

        fragmentTransaction.commit()

    }

    fun onScoreChange(change: Int, clear: Boolean) {
        scoreFragment.onScoreChange(change, clear)
    }

    fun startNewGame() {
        gameFragment.startNewGame("")
    }

}