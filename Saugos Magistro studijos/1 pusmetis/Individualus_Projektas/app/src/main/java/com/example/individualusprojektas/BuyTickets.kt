package com.example.individualusprojektas

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.lang.Exception
import androidx.appcompat.widget.Toolbar


class BuyTickets : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var monthlyTicket: Button? = null
    var oneHourTicket: Button? = null
    var dailyTicketButton:Button? = null
    var spinner: Spinner? = null
    var NFCContent: TextView? = null
    var creditText: TextView? = null
    var totalPrice: TextView? = null
    var daysToBuy:EditText? = null

    var buyMonthlyTicket = false
    var buyOneHourTicket = false
    var buyDailyTicket = false
    var timer: CountDownTimer = object: CountDownTimer(5000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }
        override fun onFinish() {
            NFCContent!!.text = ""
        }
    }
    var toolbar:androidx.appcompat.widget.Toolbar? = null
    var drawerLayout: DrawerLayout? = null
    var navigationView:NavigationView? = null
    var nfcAdapter: NfcAdapter? = null
    var tag:Tag? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_tickets)

        toolbar = findViewById(R.id.tool_bar) as Toolbar
        drawerLayout = findViewById(R.id.draw_layout) as DrawerLayout
        navigationView = findViewById(R.id.nav_view) as NavigationView
        NFCContent = findViewById(R.id.nfc_contents)
        dailyTicketButton = findViewById(R.id.dailyTicketButton) as Button
        daysToBuy = findViewById(R.id.editTextDays) as EditText
        totalPrice = findViewById(R.id.totalPrice) as TextView


        setSupportActionBar(toolbar)
        navigationView!!.bringToFront()
        var toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
        navigationView!!.setNavigationItemSelectedListener(this)

        monthlyTicket = findViewById(R.id.button2) as Button
        oneHourTicket = findViewById(R.id.button3) as Button

        val headerLayout = navigationView!!.getHeaderView(0);
        spinner = headerLayout.findViewById(R.id.spinner) as Spinner
        creditText = headerLayout.findViewById(R.id.credits) as TextView

        CardData.AddCitieView(spinner!!)
        CardData.AddCreditView(creditText!!)
        CardData.UpdateCities()
        CardData.UpdateCredits()

        NFCContent!!.text = ""

        monthlyTicket!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                buyMonthlyTicket = true
                buyOneHourTicket = false
                val procentage = CardData.GetDiscount().toFloat()/100f
                NFCContent!!.text = "Add NFC Card"
                totalPrice?.text = "Total Price: " + (50f - (50f * procentage))
                timer.cancel()
            }
        })

        oneHourTicket!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                buyMonthlyTicket = false
                buyOneHourTicket = true
                val procentage = CardData.GetDiscount().toFloat()/100f
                NFCContent!!.text = "Add NFC Card"
                totalPrice?.text = "Total Price: " + (1f - (1f * procentage))
                timer.cancel()
            }
        })

        dailyTicketButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                buyMonthlyTicket = false
                buyOneHourTicket = false
                buyDailyTicket = true
                val procentage = CardData.GetDiscount().toFloat()/100f
                NFCContent!!.text = "Add NFC Card"
                totalPrice?.text =
                    "Total Price: " + ((daysToBuy?.text.toString().toFloat() * 2f) - (daysToBuy?.text.toString().toFloat() * ( 2f * procentage)))
                timer.cancel()
            }
        })

        daysToBuy?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if(buyDailyTicket) {
                    if (daysToBuy?.text.toString() != "") {
                        val procentage = CardData.GetDiscount().toFloat()/100f
                        totalPrice?.text =
                            "Total Price: " + ((daysToBuy?.text.toString().toFloat() * 2f) - (daysToBuy?.text.toString().toFloat() * ( 2f * procentage)))
                    } else {
                        totalPrice?.text = "Total Price: "
                    }
                }
            }

        })

        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
            }
        }


        nfcAdapter = CardFunctions.InitNfcAdapter(this)

        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show()
            finish()
        }

    }

    public override fun onNavigationItemSelected(menuItem: MenuItem):Boolean
    {
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
            R.id.nav_tickets -> {
                var intent: Intent = Intent(this, Tickets::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_home -> {
                var intent: Intent = Intent(this, MainActivity::class.java)
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

            try {
                val mfc = MifareClassic.get(raMsgs)
                mfc.connect()

                if (buyMonthlyTicket) {
                    buyMonthlyTicket = false
                    var res = CardFunctions.BuyTicket(mfc, 1, TicketType.Monthly,50f - (CardData.GetDiscount().toFloat()/100f) * 50f, spinner!!, 1);
                    NFCContent!!.text = res.toString()
                    var credits = CardFunctions.readCredits(mfc)
                    creditText!!.text = "Your Credits: " + credits
                    CardData.SetCredits(credits)
                    CardData.UpdateCredits()
                    timer.start()
                }

                if (buyOneHourTicket) {
                    buyOneHourTicket = false
                    var res = CardFunctions.BuyTicket(mfc, 2, TicketType.Hourly, 1f - (CardData.GetDiscount().toFloat()/100f) * 1f, spinner!!, 1);
                    NFCContent!!.text = res.toString()
                    var credits = CardFunctions.readCredits(mfc)
                    creditText!!.text = "Your Credits: " + credits
                    CardData.SetCredits(credits)
                    CardData.UpdateCredits()
                    timer.start()
                }

                if(buyDailyTicket)
                {
                    buyDailyTicket = false
                    var res = CardFunctions.BuyTicket(mfc, 3, TicketType.Daily, 2f - (CardData.GetDiscount().toFloat()/100f) * 2f, spinner!!, daysToBuy?.text.toString().toInt());
                    NFCContent!!.text = res.toString()
                    var credits = CardFunctions.readCredits(mfc)
                    creditText!!.text = "Your Credits: " + credits
                    CardData.SetCredits(credits)
                    CardData.UpdateCredits()
                    timer.start()
                }



            } catch (e:Exception) {
                Toast.makeText(this, "Error exception! Tag was lost", Toast.LENGTH_SHORT).show();
            }

        }
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