package de.bguenthe.monthlycosts.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.bguenthe.monthlycosts.costswebservice.CostsDTO
import de.bguenthe.monthlycosts.costswebservice.CostsResult
import de.bguenthe.monthlycosts.costswebservice.CostsWebservice
import de.bguenthe.monthlycosts.database.AppDatabase
import de.bguenthe.monthlycosts.database.Costs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.MonthDay
import java.time.Year
import java.time.format.DateTimeFormatter

// TODO: Wie schaffe ich es im Emulator KEINE Daten zu senden. Die TEST/DEBUG Möglichkeit fand ich scheiße
class CostsRepository(val context: Context) {
    lateinit var uuidsRemote: List<CostsDTO>

    // https://www.c-sharpcorner.com/article/how-to-use-retrofit-2-with-android-using-kotlin/

    val database: AppDatabase = AppDatabase.getDatabase(context)

    // synchronize local with remote
    // IDEE hole all uuid local und remote,
    // 1.) mache uuid diff local - remote, es bleiben alle remote schlüssel, die holen und in die db einfügen
    // 2) mache uuids diff remote - local, es bleiben alle local schlüssel, diese senden
    // IST DIE PERFORMANT GENUG?
    // WANN RUFE ICH DIES AUF? NUR BEI WLAN?

    fun syncCosts() {
        val retrofitcosts = Retrofit.Builder()
                .baseUrl("http://192.168.178.20:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val servicecosts = retrofitcosts.create(CostsWebservice::class.java)

        val callcosts = servicecosts.getAllCosts()
        callcosts.enqueue(object : Callback<CostsResult> {
            override fun onResponse(call: Call<CostsResult>, response: Response<CostsResult>) {
                if (response.code() == 200) {
                    val res = response.body()!!
//                    uuidsRemote = res
                    val uuidsLocal: List<String> = database.costsDao().getAllUUIds()
                    //val tobeinserted = uuidsRemote - uuidsLocal
                    //val tobesend = uuidsLocal - uuidsRemote
                }
            }

            override fun onFailure(call: Call<CostsResult>, t: Throwable) {
                t.printStackTrace()
                return
            }
        })
    }

    fun stringToDateTime(datetime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // // Format: 2018-03-22 19:02:12.337909
        return datetime.format(formatter)
    }

    var stringToDateTimelamda = { datetime: LocalDateTime ->
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // // Format: 2018-03-22 19:02:12.337909
        datetime.format(formatter)
    }

    fun saveAllCostsToServer() {
        val retrofitcosts = Retrofit.Builder()
                .baseUrl("http://192.168.178.20:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val servicecosts = retrofitcosts.create(CostsWebservice::class.java)
        val allCosts = database.costsDao().getAll
        for (cost in allCosts) {
            val costDTO = CostsDTO(cost.id, cost.type!!, stringToDateTimelamda(cost.recordDateTime!!), cost.costs, cost.uniqueID!!, cost.comment!!, cost.deleted)
            val callcosts = servicecosts.saveCosts(costDTO)
            callcosts.enqueue(object : Callback<CostsDTO> {
                override fun onResponse(call: Call<CostsDTO>, response: Response<CostsDTO>) {
                    if (response.code() == 200) {
                        val res = response.body()!!
                    }
                }

                override fun onFailure(call: Call<CostsDTO>, t: Throwable) {
                    t.printStackTrace()
                    return
                }
            })
        }
    }

    fun saveAllCostsToFirestore() {
        val db = Firebase.firestore
        val allCosts = database.costsDao().getAll
        for (cost in allCosts) {
            val firestorecost = hashMapOf(
                    "id" to cost.id,
                    "comments" to cost.comment,
                    "costs" to cost.costs,
                    "recordDateTime" to cost.recordDateTime.toString(),
                    "mqttsend" to cost.mqttsend,
                    "uniqueID" to cost.uniqueID,
                    "type" to cost.type,
                    "deleted" to cost.deleted
            )

            // Add a new document with a generated ID
            db.collection("costs")
                    .document(cost.uniqueID!!)
                    .set(firestorecost)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Firestore", "DocumentSnapshot added with ID: $cost.uniqueID!!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error adding document", e)
                    }

        }
    }

    fun saveCostToFirestore(costs: Costs) {
        val db = Firebase.firestore
        val firestorecost = hashMapOf(
                "id" to costs.id,
                "comments" to costs.comment,
                "costs" to costs.costs,
                "recordDateTime" to costs.recordDateTime,
                "mqttsend" to costs.mqttsend,
                "uniqueID" to costs.uniqueID,
                "type" to costs.type,
                "deleted" to costs.deleted
        )

        // Add a new document with a generated ID
        db.collection("costs")
                .document(costs.uniqueID!!)
                .set(firestorecost)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "DocumentSnapshot added with ID: $costs.uniqueID!!")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding document", e)
                }

    }

    fun getNumberOfMonthsToShow(): Int {
        return database.costsDao().numberOfMonthsToShow
    }

    fun saveCosts(type: String, costs: Costs): Boolean {
        try {
            if (type == "NEW") {
                costs.mqttsend = false
                database.costsDao().add(costs)
            } else if (type == "DELETE)") {
                database.costsDao().update(costs)
            }
        } catch (e: Exception) {
            return false // nix, wird später gespeichert
        }

        saveCostToFirestore(costs)

        return true
    }

    fun undo() {
        val costs = database.costsDao().getLast(Year.now().value, MonthDay.now().month.value)
        if (costs == null) {
            return // kein Eintrag zum Löschen
        }
        costs.deleted = true
        database.costsDao().update(costs)
        if (saveCosts("DELETE", costs)) {
            costs.mqttsend = true
            database.costsDao().update(costs)
        }
    }
}