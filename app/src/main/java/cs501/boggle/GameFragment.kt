package cs501.boggle


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.TreeSet
import kotlin.random.Random


/**
 * A simple [Fragment] subclass.
 * Use the [GameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameFragment : Fragment() {

    lateinit var buttons: Array<Array<Button>>
    var chars = Array(4) { Array(4) { '*' } }
    var consonants = "bcdfghjklmnpqrstvwxyz".toCharArray()
    var vowels = "aeiou".toCharArray()
    lateinit var locationMapping: HashMap<Int, Pair<Int, Int>>

    lateinit var words: TreeSet<String>
    lateinit var wordsArray: ArrayList<String>

    var lastInput: Pair<Int, Int> = Pair<Int, Int>(999, 999);

    lateinit var vibrator: Vibrator

    val userInputButtons: ArrayList<Int> = ArrayList()
    lateinit var assetManager: AssetManager

    lateinit var userInputView: TextView

    lateinit var commitScoreChange: (Int, Boolean) -> Unit
    lateinit var clearButton: Button
    lateinit var submitButton: Button
    lateinit var originalButtonColor: Drawable

    val hasSubmitWords: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assetManager = requireActivity().assets

        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        userInputView = view.findViewById(R.id.u_entered_words)
        userInputView.text = ""

        val grid_layout = view.findViewById<GridLayout>(R.id.u_word_buttons);

        locationMapping = HashMap<Int, Pair<Int, Int>>()

        buttons = Array(4) { i ->
            Array(4) { j ->
                Button(requireContext()).apply {
                    text = "(${i}, ${j})"
                }
            }
        }

        originalButtonColor = buttons[0][0].background

        clearButton = view.findViewById(R.id.u_clear_button)
        submitButton = view.findViewById(R.id.u_submit_button)

        clearButton.setOnClickListener {
            clearInput()
        }

        submitButton.setOnClickListener {
            submitInput()
        }

        for (i in 0..3) {
            for (j in 0..3) {
                val button = buttons[i][j]
                locationMapping[System.identityHashCode(button)] = Pair(i, j)
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(i, 1, 1f)
                params.columnSpec = GridLayout.spec(j, 1, 1f)
                button.layoutParams = params
                grid_layout.addView(button)
                button.setOnClickListener {
                    onTap(System.identityHashCode(it))
                }
            }
        }


        return view
    }

    override fun onStart() {
        super.onStart()

        loadWords()
        val preloadLetters = requireArguments().getString("preloadLetters", "")
        startNewGame(preloadLetters)

    }

    fun countVowels(word: String): Int {
        val pattern = "aeiou".toPattern()
        val matcher = pattern.matcher(word)
        var count = 0

        while(matcher.find()) {
            count++
        }

        return count
    }

    fun loadWords() {
        words = TreeSet()
        var text = ""
        try {
            val inputStream = assetManager.open("words.txt")

            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
                stringBuilder.append("\n")
            }
            text = stringBuilder.toString()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val pattern = "^[a-zA-Z]{4,6}$".toPattern()

        for(line in text.split("\n")) {
            //if(pattern.matcher(line).find()) {
            //    if(countVowels(line) >= 2) {
                    words.add(line.lowercase())
            //    }
            //}
        }

        wordsArray = ArrayList(words)

    }

    fun startNewGame(argumentPreLoadLetters: String) {
        var preLoadLetters = argumentPreLoadLetters
        var vowelString = "aeiou"
        var consonantString = ""

        val orderX = arrayOf(0, 1, 2, 3, 3, 3, 3, 2, 1, 0, 0, 0, 1, 2, 2, 1)
        val orderY = arrayOf(0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 2, 1, 1, 1, 2, 2)

        var pickedWords = ""

        if(preLoadLetters.isEmpty()) {
            while(preLoadLetters.length < 16) {
                val choice = Random.nextLong(0, words.size.toLong())
                val word = wordsArray[Math.abs(choice.toInt())]
                if(word.length > 4) {
                    continue
                }
                preLoadLetters += word
                pickedWords += word
                pickedWords += ", "
            }

            preLoadLetters = preLoadLetters.substring(0, 16)

            Toast.makeText(requireContext(), "Pick words ${pickedWords}", Toast.LENGTH_LONG).show()
        }



        vowelString += vowels.random()
        vowelString += vowels.random()
        for (i in 0 ..8) { consonantString += consonants.random() }

        var allChars = vowelString + consonantString

        if(!preLoadLetters.isEmpty()) {
            allChars = preLoadLetters
        }

        var charArray = allChars.toCharArray()
        if(preLoadLetters.isEmpty()) {
            charArray.shuffle()
        }

        var n = 0
        for (i in 0..3) {
            for (j in 0..3) {
                chars[i][j] = charArray[n]
                buttons[i][j].text = charArray[n].uppercase()
                n++
            }
        }
        clearInput()
        commitScoreChange(0, true)
        hasSubmitWords.clear()
    }

    fun startGame() {
        startNewGame("")
    }

    fun onTap(clickSource: Int) {
        val source = locationMapping[clickSource]
        if (source != null) {
            if(lastInput.first == 999) {
                commitInput(clickSource)
            }
            else {
                var xRange = source.first - lastInput.first
                var yRange = source.second - lastInput.second

                if(Math.abs(xRange) > 1 || Math.abs(yRange) > 1) {
                    shouldNotTap("You may only select connected letters")
                }
                else if(userInputButtons.contains(clickSource)) {
                    shouldNotTap("You can not select one letter twice")
                }
                else {
                    commitInput(clickSource)
                }
            }
        }
    }

    fun commitInput(clickSource: Int) {
        val source = locationMapping[clickSource]
        userInputButtons.add(clickSource)
        val button = buttons[source!!.first][source!!.second]
        button.background = ColorDrawable(0x379fefff)
        var text = ""
        for(buttonId in userInputButtons) {
            val location = locationMapping[buttonId]
            val button = buttons[location!!.first][location.second]
            text += button.text
        }

        userInputView.text = text
        if (source != null) {
            lastInput = source
        }
        
    }

    fun shouldNotTap(reason: String) {
        Toast.makeText(requireContext(), reason, Toast.LENGTH_SHORT).show()
        vibrator.vibrate(500)
    }

    fun clearInput() {
        lastInput = Pair(999, 999)
        userInputButtons.clear()
        userInputView.text = ""
        for (i in 0..3) {
            for (j in 0..3) {
                buttons[i][j].background = originalButtonColor
            }
        }
    }

    fun submitInput() {
        var scoreThisTime = 0
        var text = ""
        for(buttonId in userInputButtons) {
            val location = locationMapping[buttonId]
            val button = buttons[location!!.first][location.second]
            text += button.text
        }

        if(text.isEmpty()) {
            return
        }

        text = text.lowercase()

        if(text.length < 4) {
            onUserSubmitError()
            return
        }

        if(!words.contains(text)) {
            onUserSubmitError()
            return
        }

        var vowelCount = 0
        val matcher = "[aeiou]".toPattern().matcher(text)
        while(matcher.find()) {
            vowelCount++
        }

        var consonantCount = text.length - vowelCount



        var doubleTimeCount = 0
        val doubleMatcher = "[szpxq]".toPattern().matcher(text)
        while(doubleMatcher.find()) {
            doubleTimeCount++
        }

        scoreThisTime = consonantCount * 1 + vowelCount * 5

        if(doubleTimeCount > 0) {
            scoreThisTime *= 2
        }


        if(vowelCount < 2) {
            onUserSubmitError()
            return
        }

        if(hasSubmitWords.contains(text)) {
            onUserSubmitError()
            return
        }

        hasSubmitWords.add(text)
        onUserSubmitCorrect(scoreThisTime)
    }

    fun onUserSubmitError() {
        commitScoreChange(-10, false)
        Toast.makeText(requireContext(), "That's incorrect, -10", Toast.LENGTH_SHORT).show()

        clearInput()
    }

    fun onUserSubmitCorrect(scoreThisTime: Int) {
        commitScoreChange(scoreThisTime, false)
        Toast.makeText(requireContext(), "That's incorrect, +${scoreThisTime}", Toast.LENGTH_SHORT).show()

        clearInput()
    }

}