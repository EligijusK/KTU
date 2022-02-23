package com.example.nfclaboratorinis

import android.app.PendingIntent
import android.content.Context
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.nfc.*
import android.util.Log
import android.widget.*
import android.nfc.NfcAdapter
import android.nfc.tech.MifareUltralight
import android.nfc.tech.MifareClassic
import android.os.CountDownTimer

import kotlin.experimental.and

import java.lang.Exception
import java.lang.StringBuilder
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    var nfcAdapter: NfcAdapter? = null
    var pendingIntent:PendingIntent? = null
    var intentFilter = arrayOf<IntentFilter>()
    var tag:Tag? = null
    var NFCContent: TextView? = null
    var defaultValueNFCText = ""
    var spinner:Spinner? = null
    var credits:TextView? = null
    var addCredits:Button? = null
    var monthlyTicket:Button? = null
    var oneHourTicket:Button? = null
    var checkTicket:Button? = null
    var creditText:EditText? = null

    var updateCredits = false
    var buyMonthlyTicket = false
    var buyOneHourTicket = false
    var checkTicketTime = false
    var timer:CountDownTimer = object: CountDownTimer(5000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {NFCContent!!.text = "Add NFC Card to read ticket data"}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //context = this;

        NFCContent = findViewById(R.id.nfc_contents) as TextView
        spinner = findViewById(R.id.spinner) as Spinner
        credits = findViewById(R.id.text_view) as TextView
        addCredits = findViewById(R.id.button5) as Button
        monthlyTicket = findViewById(R.id.button2) as Button
        oneHourTicket = findViewById(R.id.button3) as Button
        checkTicket = findViewById(R.id.button4) as Button
        creditText = findViewById(R.id.editTextCreditCount) as EditText
        defaultValueNFCText = NFCContent!!.text.toString()



        addCredits!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                updateCredits = true
                buyMonthlyTicket = false
                buyOneHourTicket = false
                checkTicketTime = false
                NFCContent!!.text = defaultValueNFCText
                timer.cancel();
            }
        }) // write to nfc

        monthlyTicket!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                updateCredits = false
                buyMonthlyTicket = true
                buyOneHourTicket = false
                checkTicketTime = false
                NFCContent!!.text = defaultValueNFCText
                timer.cancel();
            }
        })

        oneHourTicket!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                updateCredits = false
                buyMonthlyTicket = false
                buyOneHourTicket = true
                checkTicketTime = false
                NFCContent!!.text = defaultValueNFCText
                timer.cancel();
            }
        })

        checkTicket!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                updateCredits = false
                buyMonthlyTicket = false
                buyOneHourTicket = false
                checkTicketTime = true
                NFCContent!!.text = defaultValueNFCText
                timer.cancel();
            }
        })

        initNfcAdapter()

        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show()
            finish()
        }
        readFromIntent(intent)

        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            0
        )
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
        intentFilter = arrayOf<IntentFilter>(tagDetected)
    }

    private fun initNfcAdapter() {
        val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
        nfcAdapter = nfcManager.defaultAdapter
    }

    private fun readFromIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val raMsgs:Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag?

            try {
                val mfc = MifareClassic.get(raMsgs)
                mfc.connect()
                cardData(mfc)

                if (updateCredits) {
                    updateCredits = false
                    addCredits(mfc, creditText!!.text.toString().toInt())
                    NFCContent!!.text = defaultValueNFCText
                }

                if (buyMonthlyTicket) {
                    buyMonthlyTicket = false
                    BuyTicket(mfc, 1, true,30);
                    timer.start()
                }

                if (buyOneHourTicket) {
                    buyOneHourTicket = false
                    BuyTicket(mfc, 2, false,1);
                    timer.start()
                }

                if (checkTicketTime) {
                    checkTicketTime = false
                    var result = checkSelectedCity(mfc)
                    if (result) {
                        NFCContent!!.text = "Your ticket is valid"
                    } else {
                        NFCContent!!.text = "You don't have a ticket or it is expired"
                    }
                    timer.start()

                }
            } catch (e:Exception) {
                Toast.makeText(this, "Error exception! Tag was lost", Toast.LENGTH_SHORT).show();
            }

        }
    }

    fun BuyTicket(mfc: MifareClassic, sector: Int, monthlyTicket:Boolean, price:Int)
    {
        val credits = readCredits(mfc)
        var authA = mfc.authenticateSectorWithKeyA(sector, MifareClassic.KEY_NFC_FORUM)
        if (authA) {
            var first = readDataFromBlock(mfc, sector, spinner!!.selectedItemId.toInt())
            var data: Long = bytesToLong(first, 0)

            if (credits >= price && Date().time >= data) {
                var dt = Date()
                val c = Calendar.getInstance()
                c.time = dt
                if(monthlyTicket)
                {
                    c.add(Calendar.YEAR, 1)
                }
                else{
                    c.add(Calendar.HOUR, 1)
                }
                dt = c.time
                var byteRes: ByteArray = ByteArray(16)
                longToBytes(byteRes, dt.time, 0)
                writeData(mfc, sector, spinner!!.selectedItemId.toInt(), byteRes)
                decreaseCredits(mfc, price)
            } else if (Date().time < data) {
                if(credits >= price) {
                    NFCContent!!.text = "Your ticket is still valid"
                }
                else
                {
                    NFCContent!!.text = "You don't have enough credits to buy ticket,\n but your ticket is still valid"
                }
            } else {
                NFCContent!!.text = "You don't have enough credits"
            }
        } else {
            NFCContent!!.text = "Cant Access Ticket Data"
        }

    }

    private fun cardData(mfc: MifareClassic)
    {
        NFCContent!!.text = ""
        var dtBefore = Date()
        var dtAfter = Date()
        val a = Calendar.getInstance()
        a.time = dtAfter
        val b = Calendar.getInstance()
        b.time = dtBefore
        a.add(Calendar.MONTH, -120);
        b.add(Calendar.YEAR, 2)
        dtBefore = a.time
        dtAfter = b.time

        for (i in 1..2){
            var text:String = NFCContent!!.text.toString()
            if(i == 1)
            {
                NFCContent!!.text = text + " Menesiniai biletai: \n"
            }
            else
            {
                NFCContent!!.text = text + " Vienkartiniai biletai: \n"
            }
            var authA = mfc.authenticateSectorWithKeyA(i, MifareClassic.KEY_NFC_FORUM)
            if(authA) {
                var first = readDataFromBlock(mfc, i, 0)
                var data: Long = bytesToLong(first, 0)

                if(data.compareTo(dtBefore.time) >= 0 && data.compareTo(dtAfter.time) == -1) {
                    text = NFCContent!!.text.toString()
                    NFCContent!!.text =
                        text + "Kauno bileto galiojimo laikas: " + Date(data).toString() + "\n"
                }

                var second = readDataFromBlock(mfc, i, 1)
                data = bytesToLong(second, 0)
                if(data.compareTo(dtBefore.time) >= 0 && data.compareTo(dtAfter.time) == -1) {
                    text = NFCContent!!.text.toString()
                    NFCContent!!.text =
                        text + "Klaipeda bileto galiojimo laikas: " + Date(data).toString() + "\n"
                }

                var third = readDataFromBlock(mfc, i, 2)
                data = bytesToLong(third, 0)
                if(data.compareTo(dtBefore.time) >= 0 && data.compareTo(dtAfter.time) == -1) {
                    text = NFCContent!!.text.toString()
                    NFCContent!!.text =
                        text + "Vilnius bileto galiojimo laikas: " + Date(data).toString() + "\n"
                }

                var fourth = readDataFromBlock(mfc, i, 3)
                data = bytesToLong(fourth, 0)
                if(data.compareTo(dtBefore.time) >= 0 && data.compareTo(dtAfter.time) == -1) {
                    text = NFCContent!!.text.toString()
                    NFCContent!!.text =
                        text + "Siauliai bileto galiojimo laikas: " + Date(data).toString() + "\n"
                }
            }
            readCredits(mfc)
        }
    }



    private fun readCredits(mfc:MifareClassic):Int
    {
        var authA = mfc.authenticateSectorWithKeyA(3, MifareClassic.KEY_NFC_FORUM)

        if(authA) {

            var bytes = readDataFromBlock(mfc, 3, 0)
            var intRes = bytesToInt(bytes, 0)
            credits?.text = "Your Credits: " + intRes
            return intRes
        }
        return  0
    }



    private fun addCredits(mfc:MifareClassic, creditsToAdd:Int)
    {
        var authA = mfc.authenticateSectorWithKeyA(3, MifareClassic.KEY_NFC_FORUM)

        if(authA) {
            var bytes = readDataFromBlock(mfc, 3, 0)
            var intRes = bytesToInt(bytes, 0)
            intRes += creditsToAdd
            var byteRes: ByteArray = ByteArray(16)
            intToBytes(byteRes, intRes, 0)
            writeData(mfc, 3, 0, byteRes)
            credits?.text = "Your Credits: " + intRes
        }
    }

    private fun decreaseCredits(mfc:MifareClassic, creditsToDecrease:Int)
    {
        var authA = mfc.authenticateSectorWithKeyA(3, MifareClassic.KEY_NFC_FORUM)

        if(authA) {
            var bytes = readDataFromBlock(mfc, 3, 0)
            var intRes = bytesToInt(bytes, 0)
            intRes -= creditsToDecrease
            if (intRes < 0) {
                intRes = 0
            }
            var byteRes: ByteArray = ByteArray(16)
            intToBytes(byteRes, intRes, 0)
            writeData(mfc, 3, 0, byteRes)
            credits?.text = "Your Credits: " + intRes
        }
    }

    private fun checkSelectedCity(mfc:MifareClassic):Boolean
    {
        var authA = mfc.authenticateSectorWithKeyA(1, MifareClassic.KEY_NFC_FORUM)
        var authb = mfc.authenticateSectorWithKeyA(2, MifareClassic.KEY_NFC_FORUM)
        if(authA && authb) {
            var bytesMonthly = readDataFromBlock(mfc, 1, spinner!!.selectedItemId.toInt())
            var bytesOneTime = readDataFromBlock(mfc, 2, spinner!!.selectedItemId.toInt())

            var longOneTime = bytesToLong(bytesOneTime, 0)
            var longMonthly = bytesToLong(bytesMonthly, 0)

            if (Date().time <= longMonthly || Date().time <= longOneTime) {
                return true;
            }
        }
        return false
    }

    fun longToBytes(buffer: ByteArray, lng: Long, offset: Int) {

        buffer[offset + 0] = (lng shr 0).toByte()
        buffer[offset + 1] = (lng shr 8).toByte()
        buffer[offset + 2] = (lng shr 16).toByte()
        buffer[offset + 3] = (lng shr 24).toByte()
        buffer[offset + 4] = (lng shr 32).toByte()
        buffer[offset + 5] = (lng shr 40).toByte()
        buffer[offset + 6] = (lng shr 48).toByte()
        buffer[offset + 7] = (lng shr 56).toByte()

    }

    fun bytesToLong(bytes: ByteArray, offset: Int): Long {
        return (
                (bytes[offset + 7].toLong() shl 56) or
                (bytes[offset + 6].toLong() and 0xff shl 48) or
                (bytes[offset + 5].toLong() and 0xff shl 40) or
                (bytes[offset + 4].toLong() and 0xff shl 32) or
                (bytes[offset + 3].toLong() and 0xff shl 24) or
                (bytes[offset + 2].toLong() and 0xff shl 16) or
                (bytes[offset + 1].toLong() and 0xff shl 8) or
                (bytes[offset + 0].toLong() and 0xff)
                )
    }

    private fun intToBytes(buffer: ByteArray, data: Int, offset: Int) {
        buffer[offset + 0] = (data shr 0).toByte()
        buffer[offset + 1] = (data shr 8).toByte()
        buffer[offset + 2] = (data shr 16).toByte()
        buffer[offset + 3] = (data shr 24).toByte()
    }

    private fun bytesToInt(buffer: ByteArray, offset: Int): Int {
        return (buffer[offset + 3].toInt() shl 24) or
                (buffer[offset + 2].toInt() and 0xff shl 16) or
                (buffer[offset + 1].toInt() and 0xff shl 8) or
                (buffer[offset + 0].toInt() and 0xff)
    }

    private fun writeData(mfc:MifareClassic, sector:Int, block:Int, bytes: ByteArray){
        var bIndex = 0
        var authA = mfc.authenticateSectorWithKeyA(sector, MifareClassic.KEY_NFC_FORUM)
        if(authA) {
            bIndex = mfc.sectorToBlock(sector);
            var realIndex = bIndex + block
            mfc.writeBlock(realIndex, bytes)
        }
    }

    private fun readDataFromBlock(mfc:MifareClassic, sector: Int, blockIndex:Int):ByteArray {

        var data: ByteArray = ByteArray(1){0};
        var realBlockIndex = mfc.sectorToBlock(sector) + blockIndex
        var authA = mfc.authenticateSectorWithKeyA(sector, MifareClassic.KEY_NFC_FORUM)
        if(authA) {

            // 6.3) Read the block
            try {
                data = mfc.readBlock(realBlockIndex);
                // 7) Convert the data into a string from Hex format.

            }catch (ioe:IOException) {
                ioe.printStackTrace();
            } catch (e:Exception) {
                e.printStackTrace()
            }

        }
        return data;

    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        readFromIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }
        super.onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this.applicationContext, this.javaClass)
        val pendingIntent = PendingIntent.getActivity(this.applicationContext, 0, intent, 0)
        nfcAdapter!!.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        nfcAdapter!!.disableForegroundDispatch(this)
        super.onPause()
    }

}