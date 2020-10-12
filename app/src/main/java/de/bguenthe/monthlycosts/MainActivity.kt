package de.bguenthe.monthlycosts

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import de.bguenthe.monthlycosts.database.AppDatabase
import de.bguenthe.monthlycosts.database.Costs
import de.bguenthe.monthlycosts.repository.CostsRepository
import de.bguenthe.monthlycosts.repository.IncomeRepository
import java.time.LocalDateTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class Constants {
    data class Consts(var label: String, var color: Int)

    var liste: LinkedHashMap<String, Consts> = linkedMapOf()

    init {
        liste["lbm"] = Consts("lbm", Color.GREEN)
        liste["kaffee"] = Consts("kaffee", Color.BLACK)
        liste["rest"] = Consts("rest", Color.RED)
        liste["beauty"] = Consts("beauty", Color.YELLOW)
        liste["allo"] = Consts("allo", Color.DKGRAY)
        liste["haushalt"] = Consts("haushalt", Color.GRAY)
        liste["drinks"] = Consts("drinks", Color.BLUE)
        liste["sonst"] = Consts("sonst", Color.MAGENTA)
        liste["sum"] = Consts("sum", Color.LTGRAY)
        liste["inc"] = Consts("sum", Color.LTGRAY)
        liste["fix"] = Consts("sum", Color.LTGRAY)
    }
}

class MainActivity : AppCompatActivity() {
    internal lateinit var database: AppDatabase

    private val constants = Constants()
    var numberOfMonthsToShow = 0

    private lateinit var mViewPager: ViewPager
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var costsRepository: CostsRepository
    private lateinit var incomeRepository: IncomeRepository

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        auth = Firebase.auth
        auth.signInWithEmailAndPassword("monthlycostsuser@gmail.com", "8AyzqxAp42Pm9KHZ8PlC")
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        Log.d("Firestore AUTH", "$user signInWithEmail:success")
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e("Firestore AUTH", "signInWithEmail:failure", task.exception)
                    }
                }

        costsRepository = CostsRepository(applicationContext)
        //costsRepository.syncCosts()
        //costsRepository.saveAllCostsToServer()
        costsRepository.saveAllCostsToFirestore()
        numberOfMonthsToShow = costsRepository.getNumberOfMonthsToShow()

        incomeRepository = IncomeRepository(applicationContext)

        mViewPager = findViewById(R.id.viewpager)
        pagerAdapter = BarchartPagerAdapter(supportFragmentManager, numberOfMonthsToShow + 1) // + 1 für den durchschnittsblock
        mViewPager.adapter = pagerAdapter
        mViewPager.currentItem = numberOfMonthsToShow - 1

        val btnLbm = findViewById<Button>(R.id.lbm)
        btnLbm.text = constants.liste.getValue("lbm").label
        btnLbm.setOnClickListener {
            saveCosts(constants.liste.getValue("lbm").label)
        }

        val btnKaffee = findViewById<Button>(R.id.kaffee)
        btnKaffee.text = constants.liste.getValue("kaffee").label
        btnKaffee.setOnClickListener {
            saveCosts(constants.liste.getValue("kaffee").label)
        }

        val btnRest = findViewById<Button>(R.id.rest)
        btnRest.text = constants.liste.getValue("rest").label
        btnRest.setOnClickListener {
            saveCosts(constants.liste.getValue("rest").label)
        }

        val btnDrinks = findViewById<Button>(R.id.drinks)
        btnDrinks.text = constants.liste.getValue("drinks").label
        btnDrinks.setOnClickListener {
            saveCosts(constants.liste.getValue("drinks").label)
        }

        val btnAllo = findViewById<Button>(R.id.allo)
        btnAllo.text = constants.liste.getValue("allo").label
        btnAllo.setOnClickListener {
            saveCosts(constants.liste.getValue("allo").label)
        }

        val btnHaushalt = findViewById<Button>(R.id.haushalt)
        btnHaushalt.text = constants.liste.getValue("haushalt").label
        btnHaushalt.setOnClickListener {
            saveCosts(constants.liste.getValue("haushalt").label)
        }

        val btnSonst = findViewById<Button>(R.id.sonst)
        btnSonst.text = constants.liste.getValue("sonst").label
        btnSonst.setOnClickListener {
            saveCosts(constants.liste.getValue("sonst").label)
        }

        val btnBeauty = findViewById(R.id.beauty) as Button
        btnBeauty.text = constants.liste.getValue("beauty").label
        btnBeauty.setOnClickListener {
            saveCosts(constants.liste.getValue("beauty").label)
        }

        val btnUndo = findViewById(R.id.undo) as Button
        btnUndo.setOnClickListener {
            undo()
            // hier auch am server den letzten eintrag löschen
            refreshView()
        }

        val btnIncome = findViewById(R.id.income) as Button
        btnIncome.setOnClickListener {
            saveIncome()
            // hier auch am server den letzten eintrag löschen
            refreshView()
        }
    }

    fun refreshView() {
        pagerAdapter.notifyDataSetChanged()
        mViewPager.adapter = null
        mViewPager.adapter = pagerAdapter
        mViewPager.currentItem = numberOfMonthsToShow - 1
    }

    fun undo() {
        costsRepository.undo()
    }

    fun saveCosts(type: String) {
        val costs: Costs
        val betrag = findViewById<EditText>(R.id.betrag)
        val amount = betrag.text.toString()
        val editTextComment = findViewById<EditText>(R.id.kommentar)
        val comment = editTextComment.text.toString()
        if (amount != "") {
            costs = Costs(type, LocalDateTime.now(), amount.toDouble(), comment)
            costsRepository.saveCosts("NEW", costs)
            betrag.setText("")
            refreshView()
        }
        editTextComment.setText("")
        betrag.requestFocus()
    }

    fun saveIncome() {
        val betrag = findViewById<EditText>(R.id.betrag)
        val amount = betrag.text.toString()
        val editTextComment = findViewById<EditText>(R.id.kommentar)
        if (amount != "") {
            incomeRepository.saveIncome(amount.toDouble())
            betrag.setText("")
            refreshView()
        }
        editTextComment.setText("")
        betrag.requestFocus()
    }
}