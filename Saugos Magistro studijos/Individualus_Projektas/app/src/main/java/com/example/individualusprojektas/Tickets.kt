package com.example.individualusprojektas

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.lang.Exception
import android.view.View
import android.widget.*


class Tickets : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var toolbar:androidx.appcompat.widget.Toolbar? = null
    var drawerLayout: DrawerLayout? = null
    var navigationView:NavigationView? = null
    var NFCContent: TextView? = null

    var textViewArray:MutableList<MutableList<TextView>> = mutableListOf(mutableListOf(),mutableListOf(),mutableListOf())
    var buttonArray:MutableList<MutableList<Button?>?> = mutableListOf(mutableListOf(),mutableListOf(),mutableListOf())
    var viewArray:MutableList<MutableList<LinearLayout>> = mutableListOf(mutableListOf(),mutableListOf(),mutableListOf())
    var row2:LinearLayout? = null

    var refundTicketIndex:Int = -1
    var refundTicketType:Int = -1
    var cityIndex:Int = -1

    var nfcAdapter: NfcAdapter? = null
    var tag:Tag? = null
    var timer: CountDownTimer = object: CountDownTimer(5000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }
        override fun onFinish() {
            NFCContent!!.text = ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tickets)

        NFCContent = findViewById(R.id.nfc_contents)

        toolbar = findViewById(R.id.tool_bar) as Toolbar
        drawerLayout = findViewById(R.id.draw_layout) as DrawerLayout
        navigationView = findViewById(R.id.nav_view) as NavigationView

        setSupportActionBar(toolbar)
        navigationView!!.bringToFront()
        var toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
        navigationView!!.setNavigationItemSelectedListener(this)

        nfcAdapter = CardFunctions.InitNfcAdapter(this)

        val headerLayout = navigationView!!.getHeaderView(0);
        val spinner = headerLayout.findViewById(R.id.spinner) as Spinner
        val creditText = headerLayout.findViewById(R.id.credits) as TextView

        CardData.AddCitieView(spinner)
        CardData.AddCreditView(creditText)

        CardData.UpdateCities()
        CardData.UpdateCredits()

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                CardData.SetCity(id.toInt())
                CardData.UpdateCities()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }

        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show()
            finish()
        }


    }

    public override fun onNavigationItemSelected(menuItem: MenuItem):Boolean
    {
        Log.e("BBz", navigationView.toString())
        when (menuItem.itemId) {
            R.id.nav_discount -> {
                // handle click
                var intent: Intent = Intent(this, Discounts::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_ticket_check -> {
                var intent: Intent = Intent(this, CheckTickets::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_credits -> {
                var intent: Intent = Intent(this, Credits::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_home -> {
                var intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_buy_tickets -> {
                var intent: Intent = Intent(this, BuyTickets::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
        return false
    }

    private fun readFromIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val raMsgs: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag?
            val mfc = MifareClassic.get(raMsgs)
            mfc.connect()
            var infoText = CardFunctions.cardData(mfc)
//            NFCContent!!.text = infoText
            try {

                if (refundTicketIndex == -1) {

                    var row1 = findViewById<View>(R.id.main_linear_ticket_layout) as LinearLayout
                    if (row2 != null) {
                        row1.removeView(row2)
                        textViewArray =
                            mutableListOf(mutableListOf(), mutableListOf(), mutableListOf())
                        buttonArray =
                            mutableListOf(mutableListOf(), mutableListOf(), mutableListOf())
                        viewArray = mutableListOf(mutableListOf(), mutableListOf(), mutableListOf())

                    }


                    row2 = LinearLayout(this)
                    row2!!.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    )
                    row2!!.weightSum = 1f
                    row2!!.orientation = LinearLayout.VERTICAL
                    row1.addView(row2)

                    var i: Int = 0
                    for (a in 0..2) {
                        if (a > 0) {
                            i += 3
                        }

                        var data = infoText[i]
                        var secData = infoText[i + 1]
                        var thirdData = infoText[i + 2]
                        for (b in 0..data.size - 1) {

                            var row3 = LinearLayout(this)
                            row3.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                            )
                            row3.weightSum = 1f
                            row3.orientation = LinearLayout.HORIZONTAL
                            row2!!.addView(row3)

                            viewArray[a].add(b, row3)

                            var layoutParams = LinearLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                            )
                            var textView = TextView(this)
                            textViewArray[a].add(b, textView)
                            textView.setText(data[b])
                            textView.layoutParams = layoutParams
                            row3.addView(textView)

                            var checked: Int = secData[b].toInt()
                            if (b > 0 && checked == 0) {
                                var layoutParamsTextView = LinearLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT
                                )
                                var layoutParamsTextButton = LinearLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT
                                )
                                layoutParamsTextView.weight = 0.4f
                                textView.layoutParams = layoutParamsTextView
                                layoutParamsTextButton.weight = 0.6f
                                var ivBowl = Button(this)
                                ivBowl.setText("Refund Ticket");
                                //layoutParams.setMargins(0, 0, 0, 0) // left, top, right, bottom
                                ivBowl.layoutParams = layoutParamsTextButton

                                buttonArray[a]!!.add(b - 1, ivBowl)

                                row3.addView(ivBowl)

                                ivBowl!!.setOnClickListener(object : View.OnClickListener {
                                    override fun onClick(v: View?) {
                                        cityIndex = thirdData[b].toInt()
                                        refundTicketIndex = b - 1
                                        refundTicketType = a + 1
                                        NFCContent!!.text = "Add NFC Card"
                                    }
                                })
                            } else if (b > 0 && checked == 1) {
                                buttonArray[a]!!.add(b - 1, null)
                            }

                        }


                    }
                    timer.start()
                }

                if (refundTicketIndex != -1) {
                    if (refundTicketType == 1) {
                        CardFunctions.refundTicket(mfc, cityIndex, TicketType.Monthly, 50f)
                        row2!!.removeView(viewArray[0][refundTicketIndex + 1]!!)
                        viewArray[0].removeAt(refundTicketIndex + 1)
                        buttonArray[0]!!.removeAt(refundTicketIndex)
                        textViewArray[0].removeAt(refundTicketIndex + 1)
                    } else if (refundTicketType == 2) {
                        CardFunctions.refundTicket(mfc, cityIndex, TicketType.Hourly, 1f)
                        row2!!.removeView(viewArray[1][refundTicketIndex + 1]!!)
                        viewArray[1].removeAt(refundTicketIndex + 1)
                        buttonArray[1]!!.removeAt(refundTicketIndex)
                        textViewArray[1].removeAt(refundTicketIndex + 1)
                    } else if (refundTicketType == 3) {
                        CardFunctions.refundTicket(mfc, cityIndex, TicketType.Daily, 2f)
                        row2!!.removeView(viewArray[2][refundTicketIndex + 1]!!)
                        viewArray[2].removeAt(refundTicketIndex + 1)
                        buttonArray[2]!!.removeAt(refundTicketIndex)
                        textViewArray[2].removeAt(refundTicketIndex + 1)
                    }

                    var updatedCredits = CardFunctions.readCredits(mfc)

                    CardData.SetCredits(updatedCredits)
                    CardData.UpdateCredits()
                    timer.start()
                    refundTicketIndex = -1
                }


            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Error exception! Tag was lost: " + e.toString(),
                    Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.action)) {
            readFromIntent(intent)
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