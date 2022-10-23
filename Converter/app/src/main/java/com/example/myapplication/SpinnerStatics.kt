package com.example.myapplication

class Metric(var name: String, var number: Double){
    override fun toString(): String {
        return name
    }
}

var info = arrayOf(
    Metric("Bit", 1.0),
    Metric("Byte", 8.0),
    Metric("Kilobyte", 8000.0),
    Metric("Megabyte", 8000000.0)
)

var volume = arrayOf(
    Metric("Cm^3", 1.0),
    Metric("Liter", 1000.0),
    Metric("Dcm^3", 1000.0),
    Metric("M^3", 1000000.0),
)

var speed = arrayOf(
    Metric("m/h", 1.0),
    Metric("km/s", 3600000.0),
    Metric("km/h", 1000.0),
    Metric("m/s", 3600.0),
)




