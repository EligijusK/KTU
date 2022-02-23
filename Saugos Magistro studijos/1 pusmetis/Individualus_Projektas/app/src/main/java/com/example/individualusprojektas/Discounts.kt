package com.example.individualusprojektas

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.lang.Exception

class Discounts : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var toolbar:androidx.appcompat.widget.Toolbar? = null
    var drawerLayout: DrawerLayout? = null
    var navigationView:NavigationView? = null
    var saveDiscount:Boolean = false
    var discountSelection: RadioGroup? = null
    var NFCContent: TextView? = null

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
        setContentView(R.layout.activity_discounts)

        toolbar = findViewById(R.id.tool_bar) as Toolbar
        drawerLayout = findViewById(R.id.draw_layout) as DrawerLayout
        navigationView = findViewById(R.id.nav_view) as NavigationView
        NFCContent = findViewById(R.id.nfc_contents)

        var saveButton:Button = findViewById(R.id.saveButton) as Button
        discountSelection = findViewById(R.id.radioGroup)

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

        var discount = CardData.GetDiscount()
        if(discount == 0)
        {
            discountSelection!!.check(R.id.discount_0)
        }
        else if(discount == 50)
        {
            discountSelection!!.check(R.id.discount_50)
        }
        else if(discount == 80)
        {
            discountSelection!!.check(R.id.discount_80)
        }


        saveButton!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                saveDiscount = true
                NFCContent!!.text = "Add NFC Card"
                timer.cancel()
            }
        })

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
//        Log.e("BBz", navigationView.toString())
        when (menuItem.itemId) {
            R.id.nav_home -> {
                // handle click
                var intent: Intent = Intent(this, MainActivity::class.java)
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
            try {
                val mfc = MifareClassic.get(raMsgs)
                mfc.connect()

                if (saveDiscount) {
                    saveDiscount = false
                    if (R.id.discount_0 == discountSelection!!.checkedRadioButtonId) {
                        CardFunctions.saveDiscount(mfc, 0)
                    } else if (R.id.discount_50 == discountSelection!!.checkedRadioButtonId) {
                        CardFunctions.saveDiscount(mfc, 50)
                    } else if (R.id.discount_80 == discountSelection!!.checkedRadioButtonId) {
                        CardFunctions.saveDiscount(mfc, 80)
                    }

                }

                var discount = CardFunctions.readDiscount(mfc)
                CardData.SetDiscount(discount)
                if(discount == 0)
                {
                    discountSelection!!.check(R.id.discount_0)
                }
                else if(discount == 50)
                {
                    discountSelection!!.check(R.id.discount_50)
                }
                else if(discount == 80)
                {
                    discountSelection!!.check(R.id.discount_80)
                }

                timer.start()

            }catch (e:Exception) {
                Toast.makeText(this, "Error exception! Tag was lost", Toast.LENGTH_SHORT).show();
            }

        }
    }

    override fun onNewIntent(intent: Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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