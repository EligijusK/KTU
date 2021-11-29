package com.example.individualusprojektas

import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.tech.MifareClassic
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.*

public enum class TicketType
{
    Monthly,
    Daily,
    Hourly
}

class CardFunctions {
    companion object{

        public fun InitNfcAdapter(activity: AppCompatActivity):NfcAdapter {
            val nfcManager = activity.getSystemService(Context.NFC_SERVICE) as NfcManager
            return nfcManager.defaultAdapter
        }

        public fun WriteData(mfc: MifareClassic, sector:Int, block:Int, bytes: ByteArray){
            var bIndex = 0
            var authA = mfc.authenticateSectorWithKeyA(sector, MifareClassic.KEY_NFC_FORUM)
            if(authA) {
                bIndex = mfc.sectorToBlock(sector);
                var realIndex = bIndex + block
                mfc.writeBlock(realIndex, bytes)
            }
        }

        public fun ReadDataFromBlock(mfc: MifareClassic, sector: Int, blockIndex:Int):ByteArray {

            var data: ByteArray = ByteArray(1){0};
            var realBlockIndex = mfc.sectorToBlock(sector) + blockIndex
            var authA = mfc.authenticateSectorWithKeyA(sector, MifareClassic.KEY_NFC_FORUM)
            if(authA) {

                // 6.3) Read the block
                try {
                    data = mfc.readBlock(realBlockIndex);
                    // 7) Convert the data into a string from Hex format.

                }catch (ioe: IOException) {
                    ioe.printStackTrace();
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            return data;

        }

        public fun BuyTicket(mfc: MifareClassic, sector: Int, ticketType: TicketType, price:Float, spinner:Spinner, amount:Int):String
        {
            val credits = readCredits(mfc)
            var authA = mfc.authenticateSectorWithKeyA(sector, MifareClassic.KEY_NFC_FORUM)
            if (authA) {
                var first = ReadDataFromBlock(mfc, sector, spinner!!.selectedItemId.toInt())
                var data: Long = DataConversion.BytesToLong(first, 0)
                if (credits >= (price*amount) && Date().time >= data) {
                    var dt = Date()
                    val c = Calendar.getInstance()
                    c.time = dt
                    if(TicketType.Monthly == ticketType)
                    {
                        c.add(Calendar.MONTH, 1)
                    }
                    else if(TicketType.Hourly == ticketType){
                        c.add(Calendar.HOUR, 1)
                    }
                    else if(TicketType.Daily == ticketType){
                        c.add(Calendar.DAY_OF_YEAR, amount)
                    }
                    dt = c.time
                    var byteRes: ByteArray = ByteArray(16)
                    var byteBuffer = ByteBuffer.wrap(byteRes)
                    DataConversion.LongToBytes(byteRes, dt.time, 0)
                    byteBuffer.putFloat(12, price*amount)
                    WriteData(mfc, sector, spinner!!.selectedItemId.toInt(), byteRes)
                    decreaseCredits(mfc, (price*amount))
                    return "Ticket was bought successfully"
                } else if (Date().time < data) {
                    if(credits >= price) {
                        return "Your ticket is still valid"
                    }
                    else
                    {
                        return "You don't have enough credits to buy ticket,\n but your ticket is still valid"
                    }
                } else {
                    return "You don't have enough credits"
                }
            } else {
                return "Can't Access Ticket Data"
            }
        }

        public fun refundTicket(mfc:MifareClassic, city:Int, ticketType: TicketType, price:Float)
        {
            var bytes = ByteArray(16)
            if(TicketType.Monthly == ticketType)
            {
                var cityData = ReadDataFromBlock(mfc, 1, city)
                var byteBuffer = ByteBuffer.wrap(cityData)
                var price = byteBuffer.getFloat(12)
                CardFunctions.addCredits(mfc, price)
                WriteData(mfc, 1, city, bytes)

            }else if(TicketType.Hourly == ticketType)
            {

                var cityData = ReadDataFromBlock(mfc, 2, city)
                var byteBuffer = ByteBuffer.wrap(cityData)
                var price = byteBuffer.getFloat(12)
                CardFunctions.addCredits(mfc, price)
                WriteData(mfc, 2, city, bytes)

            }
            else if(TicketType.Daily == ticketType)
            {
                var cityData = ReadDataFromBlock(mfc, 3, city)
                var byteBuffer = ByteBuffer.wrap(cityData)
                var price = byteBuffer.getFloat(12)
                CardFunctions.addCredits(mfc, price)
                WriteData(mfc, 3, city, bytes)
            }
        }


        public fun saveDiscount(mfc:MifareClassic, discounts:Int)
        {
            var authA = mfc.authenticateSectorWithKeyA(5, MifareClassic.KEY_NFC_FORUM)
            var bytes = ByteArray(16)
            if(authA) {

                DataConversion.IntToBytes(bytes, discounts, 0)
                WriteData(mfc, 5, 0, bytes)
            }
        }

        public fun readDiscount(mfc:MifareClassic):Int
        {
            var authA = mfc.authenticateSectorWithKeyA(5, MifareClassic.KEY_NFC_FORUM)

            if(authA) {
                var bytes = ReadDataFromBlock(mfc, 5, 0)
                var discount = DataConversion.BytesToInt(bytes, 0)
                return discount
            }

            return 0
        }


        public fun readCredits(mfc:MifareClassic):Float
        {
            var authA = mfc.authenticateSectorWithKeyA(4, MifareClassic.KEY_NFC_FORUM)

            if(authA) {

                var bytes = CardFunctions.ReadDataFromBlock(mfc, 4, 0)
                val buffer = ByteBuffer.wrap(bytes)
                var intRes = buffer.getFloat(0)
                return intRes
            }
            return  0f
        }

        public fun addCredits(mfc:MifareClassic, creditsToAdd:Float):Float
        {
            var authA = mfc.authenticateSectorWithKeyA(4, MifareClassic.KEY_NFC_FORUM)

            if(authA) {
                var bytes = CardFunctions.ReadDataFromBlock(mfc, 4, 0)
                val buffer = ByteBuffer.wrap(bytes)
                var intRes = buffer.getFloat(0)
                intRes += creditsToAdd
                buffer.putFloat(0,intRes)
                CardFunctions.WriteData(mfc, 4, 0, bytes)
               return intRes
            }
            return 0f
        }

        public fun decreaseCredits(mfc:MifareClassic, creditsToDecrease:Float):String
        {
            var authA = mfc.authenticateSectorWithKeyA(4, MifareClassic.KEY_NFC_FORUM)

            if(authA) {
                var bytes = CardFunctions.ReadDataFromBlock(mfc, 4, 0)
                val buffer = ByteBuffer.wrap(bytes)
                var intRes = buffer.getFloat(0)
                intRes -= creditsToDecrease
                if (intRes < 0) {
                    intRes = 0f
                }
                buffer.putFloat(0, intRes)
                CardFunctions.WriteData(mfc, 4, 0, bytes)
                return "Your Credits: " + intRes
            }
            return "Your Credits: 0"
        }

        public fun checkSelectedCity(mfc:MifareClassic, spinner:Spinner):Boolean
        {
            var authA = mfc.authenticateSectorWithKeyA(1, MifareClassic.KEY_NFC_FORUM)
            var authb = mfc.authenticateSectorWithKeyA(2, MifareClassic.KEY_NFC_FORUM)
            var authc = mfc.authenticateSectorWithKeyA(3, MifareClassic.KEY_NFC_FORUM)
            if(authA && authb && authc) {
                var bytesMonthly = CardFunctions.ReadDataFromBlock(mfc, 1, spinner!!.selectedItemId.toInt())
                var bytesOneTime = CardFunctions.ReadDataFromBlock(mfc, 2, spinner!!.selectedItemId.toInt())
                var bytesDaily = CardFunctions.ReadDataFromBlock(mfc, 3, spinner!!.selectedItemId.toInt())

                var longOneTime = DataConversion.BytesToLong(bytesOneTime, 0)
                var longMonthly = DataConversion.BytesToLong(bytesMonthly, 0)
                var longDaily = DataConversion.BytesToLong(bytesDaily, 0)

                if (Date().time <= longMonthly){
                    DataConversion.IntToBytes(bytesMonthly, 1, 8)
                    WriteData(mfc, 1, spinner!!.selectedItemId.toInt(), bytesMonthly)
                    return true;
                }
                if( Date().time <= longOneTime) {
                    DataConversion.IntToBytes(bytesOneTime, 1, 8)
                    WriteData(mfc, 2, spinner!!.selectedItemId.toInt(), bytesOneTime)
                    return true;
                }
                if( Date().time <= longDaily) {
                    DataConversion.IntToBytes(bytesOneTime, 1, 8)
                    WriteData(mfc, 3, spinner!!.selectedItemId.toInt(), bytesOneTime)
                    return true;
                }
            }
            return false
        }

        public fun cardData(mfc: MifareClassic):MutableList<MutableList<String>>
        {
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
            var mutableList:MutableList<MutableList<String>> = mutableListOf(mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf(),mutableListOf())

            var index = 0;
            var realIndex:Int = 0
            var realI:Int = 0
            for (i in 0..2){
                index = 0
                realIndex = 0
                if(i == 0)
                {
                    mutableList[realI].add(index," Menesiniai biletai: ")
                    mutableList[realI+1].add(index,"1")
                    mutableList[realI+2].add(index, "0")
                    index++
                }
                else if(i == 1)
                {
                    realI += 3
                    mutableList[realI].add(index," Vienkartiniai biletai: ")
                    mutableList[realI+1].add(index,"1")
                    mutableList[realI+2].add(index, "0")
                    index++
                }
                else
                {
                    realI += 3
                    mutableList[realI].add(index," Dieniniai biletai: ")
                    mutableList[realI+1].add(index,"1")
                    mutableList[realI+2].add(index, "0")
                    index++
                }
                var authA = mfc.authenticateSectorWithKeyA(i+1, MifareClassic.KEY_NFC_FORUM)
                if(authA) {
                    var first = CardFunctions.ReadDataFromBlock(mfc, i+1, 0)
                    var data: Long = DataConversion.BytesToLong(first, 0)
                    var checked:Int = DataConversion.BytesToInt(first, 8)

                    if(data.compareTo(dtBefore.time) >= 0 && data.compareTo(dtAfter.time) == -1) {
                        mutableList[realI].add(index,"Kauno bileto galiojimo laikas: " + Date(data).toString())
                        mutableList[realI+1].add(index,checked.toString())
                        mutableList[realI+2].add(index, realIndex.toString())
                        index++
                    }
                    realIndex++

                    var second = CardFunctions.ReadDataFromBlock(mfc, i+1, 1)
                    data = DataConversion.BytesToLong(second, 0)
                    checked = DataConversion.BytesToInt(second, 8)
                    if(data.compareTo(dtBefore.time) >= 0 && data.compareTo(dtAfter.time) == -1) {
                        mutableList[realI].add(index,"Klaipedos bileto galiojimo laikas: " + Date(data).toString())
                        mutableList[realI+1].add(index,checked.toString())
                        mutableList[realI+2].add(index, realIndex.toString())
                        index++
                    }
                    realIndex++

                    var third = CardFunctions.ReadDataFromBlock(mfc, i+1, 2)
                    data = DataConversion.BytesToLong(third, 0)
                    checked = DataConversion.BytesToInt(third, 8)
                    if(data.compareTo(dtBefore.time) >= 0 && data.compareTo(dtAfter.time) == -1) {
                        mutableList[realI].add(index,"Vilniaus bileto galiojimo laikas: " + Date(data).toString())
                        mutableList[realI+1].add(index,checked.toString())
                        mutableList[realI+2].add(index, realIndex.toString())
                        index++
                    }
                    realIndex++

                }
            }
            return mutableList
        }
    }

}