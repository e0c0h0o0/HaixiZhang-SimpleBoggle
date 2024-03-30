package cs501.boggle

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


/**
 * A simple [Fragment] subclass.
 * Use the [ScoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScoreFragment : Fragment() {

    lateinit var emitNewGame: () -> Unit
    var score = 0
    lateinit var scoreView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_score, container, false)
        scoreView = view.findViewById(R.id.u_score)

        view.findViewById<Button>(R.id.u_new_game_button).setOnClickListener {
            emitNewGame()
        }

        return view
    }

    @SuppressLint("SetTextI18n")
    fun onScoreChange(change: Int, clear: Boolean) {

        if(clear) {
            score = 0
        }
        else {
            score += change
        }

        if(score < 0) { score = 0 }

        scoreView.text = "Score: ${score}"

    }

}