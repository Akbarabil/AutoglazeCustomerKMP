package com.example.autoglazecustomer.data.model.register

data class RegisterState(
    val nama: String = "",
    val email: String = "",
    val tglLahir: String = "",
    val selectedCountry: Country = allCountries.find { it.isoCode == "ID" } ?: allCountries[0],
    val phone: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isEmailAvailable: Boolean = false,
    val errorField: String? = null
)

data class Country(
    val name: String,
    val isoCode: String,
    val phoneCode: String,
    val flag: String,
    val minDigit: Int,
    val maxDigit: Int
)

val allCountries = listOf(
    Country("Indonesia", "ID", "62", "🇮🇩", 9, 13),
    Country("Singapore", "SG", "65", "🇸🇬", 8, 8),
    Country("Malaysia", "MY", "60", "🇲🇾", 7, 10),
    Country("Thailand", "TH", "66", "🇹🇭", 9, 10),
    Country("Vietnam", "VN", "84", "🇻🇳", 9, 10),
    Country("Philippines", "PH", "63", "🇵🇭", 10, 10),
    Country("Brunei", "BN", "673", "🇧🇳", 7, 7),
    Country("Cambodia", "KH", "855", "🇰🇭", 8, 9),
    Country("Laos", "LA", "856", "🇱🇦", 8, 9),
    Country("Myanmar", "MM", "95", "🇲🇲", 8, 10),
    Country("Australia", "AU", "61", "🇦🇺", 9, 9),
    Country("Japan", "JP", "81", "🇯🇵", 10, 10),
    Country("South Korea", "KR", "82", "🇰🇷", 9, 10),
    Country("Other", "XX", "", "🌐", 5, 15) // Opsi darurat
).sortedBy { it.name }
