package com.example.individualusprojektas

import android.widget.Spinner
import android.widget.TextView

class CardData {
    companion object {
        private var credits:Float = 0f
        private var discount:Int = 0
        private var selectedCity:Int = 0
        private var cities:MutableList<Spinner> = mutableListOf()
        private var creditList:MutableList<TextView> = mutableListOf()

        public fun AddCreditView(creadit:TextView)
        {
            creditList.add(creadit)
        }

        public fun AddCitieView(city:Spinner)
        {
            cities.add(city)
        }

        public fun UpdateCredits()
        {
            for (i in 0..creditList.size-1)
            {
                creditList[i].setText("Credits: " + credits.toString())
            }
        }

        public fun UpdateCities()
        {
            for (i in 0..cities.size-1)
            {
                cities[i].setSelection(selectedCity)
            }
        }

        public fun SetCredits(credit:Float)
        {
            credits = credit
        }

        public fun SetCity(city:Int)
        {
            selectedCity = city
        }
        public fun SetDiscount(discount:Int)
        {
            this.discount = discount
        }

        public fun GetDiscount():Int
        {
            return discount
        }

        public fun GetCredits():Float
        {
            return credits
        }

        public fun GetCity():Int
        {
            return selectedCity
        }

    }
}