package com.gpn.constants

// List of fuel type names for dropdown menus
val FuelTypes = listOf("Regular", "Midgrade", "Premium", "Diesel", "E85", "UNL88")

// Map of fuel type name → GasBuddy API ID or internal ID
val FuelTypeMap = mapOf(
    "Regular" to 1,
    "Midgrade" to 2,
    "Premium" to 3,
    "Diesel" to 4,
    "E85" to 5,
    "UNL88" to 12
)

// Reverse map: ID → Name (useful when loading alert.fuelType from DB or API)
val ReverseFuelTypeMap = FuelTypeMap.entries.associate { (name, id) -> id to name }
