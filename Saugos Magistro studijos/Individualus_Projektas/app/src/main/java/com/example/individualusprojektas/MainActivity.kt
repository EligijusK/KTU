package com.example.individualusprojektas

import android.app.PendingIntent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.nfc.*
import android.widget.*
import android.nfc.NfcAdapter
import android.nfc.tech.MifareClassic
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.android.material.navigation.NavigationView.TEXT_ALIGNMENT_INHERIT
import java.lang.Exception
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener


class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    var nfcAdapter: NfcAdapter? = null
    var pendingIntent:PendingIntent? = null
    var intentFilter = arrayOf<IntentFilter>()
    var tag:Tag? = null
    var NFCContent: TextView? = null
    var defaultValueNFCText = ""
    var row2:LinearLayout? = null

    var toolbar:androidx.appcompat.widget.Toolbar? = null
    var drawerLayout:DrawerLayout? = null
    var navigationView:NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NFCContent = findViewById(R.id.nfc_contents) as TextView
        defaultValueNFCText = NFCContent!!.text.toString()

        toolbar = findViewById(R.id.tool_bar) as androidx.appcompat.widget.Toolbar
        drawerLayout = findViewById(R.id.draw_layout) as DrawerLayout
        navigationView = findViewById(R.id.nav_view) as NavigationView

        setSupportActionBar(toolbar)
        navigationView!!.bringToFront()
        var toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout!!.addDrawerListener(toggle)
        toggle.syncState()
        navigationView!!.setNavigationItemSelectedListener(this)

        val headerLayout = navigationView!!.getHeaderView(0);
        val spinner = headerLayout.findViewById(R.id.spinner) as Spinner
        val creditText = headerLayout.findViewById(R.id.credits) as TextView

        CardData.AddCitieView(spinner)
        CardData.AddCreditView(creditText)
        CardData.UpdateCities()
        CardData.UpdateCredits()

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
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

        nfcAdapter = CardFunctions.InitNfcAdapter(this)
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

    public override fun onNavigationItemSelected(menuItem: MenuItem):Boolean
    {
        when (menuItem.itemId) {
            R.id.nav_discount -> {
                // handle click
                var intent:Intent = Intent(this, Discounts::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_ticket_check -> {
                var intent:Intent = Intent(this, CheckTickets::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_credits -> {
                var intent:Intent = Intent(this, Credits::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_tickets -> {
                var intent:Intent = Intent(this, Tickets::class.java)
                startActivity(intent)
                true
            }
            R.id.nav_buy_tickets -> {
                var intent:Intent = Intent(this, BuyTickets::class.java)
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
            val raMsgs:Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag?

            try {
                val mfc = MifareClassic.get(raMsgs)
                mfc.connect()

                var infoText = CardFunctions.cardData(mfc)

                var row1 = findViewById<View>(R.id.main_linear_layout) as LinearLayout
                row1.removeView(NFCContent!!)
                if (row2 != null) {
                    row1.removeView(row2)
                }


                row2 = LinearLayout(this)
                row2!!.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                row2!!.weightSum = 1f
                row2!!.orientation = LinearLayout.VERTICAL
                row1.addView(row2)


                var creditsView = LinearLayout(this)
                creditsView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                creditsView.weightSum = 1f
                creditsView.orientation = LinearLayout.HORIZONTAL
                row2!!.addView(creditsView)

                var credits = TextView(this)
                credits.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                credits.gravity = Gravity.CENTER
                credits.textAlignment = TEXT_ALIGNMENT_INHERIT
                credits.textSize = 30f
                creditsView.addView(credits)

                var discountView = LinearLayout(this)
                discountView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                discountView.weightSum = 1f
                discountView.orientation = LinearLayout.HORIZONTAL
                row2!!.addView(discountView)


                var discount = TextView(this)
                discount.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                discount.gravity = Gravity.CENTER
                discount.textAlignment = TEXT_ALIGNMENT_INHERIT
                discount.textSize = 30f
                discountView.addView(discount)

                var discountValue = CardFunctions.readDiscount(mfc)
                var creditValue = CardFunctions.readCredits(mfc)

                credits.text = "Your Credits: " + creditValue.toString()
                discount.text = "Your Discount: " + discountValue.toString()

                CardData.SetCredits(creditValue)
                CardData.SetDiscount(discountValue)
                CardData.UpdateCredits()

                var i: Int = 0
                for (a in 0..2) {
                    if (a > 0) {
                        i += 3
                    }

                    var data = infoText[i]
                    for (b in 0..data.size - 1) {

                        var row3 = LinearLayout(this)
                        row3.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        row3.weightSum = 1f
                        row3.orientation = LinearLayout.HORIZONTAL
                        row2!!.addView(row3)

                        var layoutParams = LinearLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        )
                        var textView = TextView(this)
                        textView.setText(data[b])
                        textView.layoutParams = layoutParams
                        row3.addView(textView)

                    }

                }


            } catch (e:Exception) {
                Toast.makeText(this, "Error exception! Tag was lost: " + e.toString(), Toast.LENGTH_SHORT).show();
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