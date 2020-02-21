package com.meewii.rateconverter.common

import com.meewii.rateconverter.R

/**
 * Returns the flag's drawable ID of matching currency code
 */
fun flagMapper(currencyCode: String): Int {
  // TODO add all the other flags. For demo purposes only 4 are added here
  val flags = mutableMapOf(
    "AED" to R.drawable.ic_flag_aed,
    "AMD" to R.drawable.ic_flag_amd,
    "AUD" to R.drawable.ic_flag_aud,
    "AWG" to R.drawable.ic_flag_awg,
    "AZN" to R.drawable.ic_flag_azn,
    "BAM" to R.drawable.ic_flag_bam,
    "BDT" to R.drawable.ic_flag_bdt,
    "BGN" to R.drawable.ic_flag_bgn,
    "BHD" to R.drawable.ic_flag_bhd,
    "BMD" to R.drawable.ic_flag_bmd,
    "BOB" to R.drawable.ic_flag_bob,
    "BRL" to R.drawable.ic_flag_brl,
    "BYN" to R.drawable.ic_flag_byn,
    "BZD" to R.drawable.ic_flag_bzd,
    "CAD" to R.drawable.ic_flag_cad,
    "CHF" to R.drawable.ic_flag_chf,
    "CLP" to R.drawable.ic_flag_clp,
    "CNY" to R.drawable.ic_flag_cny,
    "CUC" to R.drawable.ic_flag_cuc,
    "CZK" to R.drawable.ic_flag_czk,
    "DKK" to R.drawable.ic_flag_dkk,
    "EGP" to R.drawable.ic_flag_egp,
    "EUR" to R.drawable.ic_flag_eur,
    "GBP" to R.drawable.ic_flag_gbp,
    "GEL" to R.drawable.ic_flag_gel,
    "HKD" to R.drawable.ic_flag_hkd,
    "HRK" to R.drawable.ic_flag_hrk,
    "HTG" to R.drawable.ic_flag_htg,
    "HUF" to R.drawable.ic_flag_huf,
    "IDR" to R.drawable.ic_flag_idr,
    "ILS" to R.drawable.ic_flag_ils,
    "INR" to R.drawable.ic_flag_inr,
    "IQD" to R.drawable.ic_flag_iqd,
    "IRR" to R.drawable.ic_flag_irr,
    "ISK" to R.drawable.ic_flag_isk,
    "JMD" to R.drawable.ic_flag_jmd,
    "JPY" to R.drawable.ic_flag_jpy,
    "KGS" to R.drawable.ic_flag_kgs,
    "KHR" to R.drawable.ic_flag_khr,
    "KRW" to R.drawable.ic_flag_krw,
    "KZT" to R.drawable.ic_flag_kzt,
    "KWD" to R.drawable.ic_flag_kwd,
    "LAK" to R.drawable.ic_flag_lak,
    "LBP" to R.drawable.ic_flag_lbp,
    "LKR" to R.drawable.ic_flag_lkr,
    "MAD" to R.drawable.ic_flag_mad,
    "MDL" to R.drawable.ic_flag_mdl,
    "MKK" to R.drawable.ic_flag_mkk,
    "MOP" to R.drawable.ic_flag_mop,
    "MXN" to R.drawable.ic_flag_mxn,
    "MYR" to R.drawable.ic_flag_myr,
    "NOK" to R.drawable.ic_flag_nok,
    "NPR" to R.drawable.ic_flag_npr,
    "NZD" to R.drawable.ic_flag_nzd,
    "OMR" to R.drawable.ic_flag_omr,
    "PHP" to R.drawable.ic_flag_php,
    "PKR" to R.drawable.ic_flag_pkr,
    "PLN" to R.drawable.ic_flag_pln,
    "PGK" to R.drawable.ic_flag_pgk,
    "PYG" to R.drawable.ic_flag_pyg,
    "QAR" to R.drawable.ic_flag_qar,
    "RON" to R.drawable.ic_flag_ron,
    "RSD" to R.drawable.ic_flag_rsd,
    "RUB" to R.drawable.ic_flag_rub,
    "SAR" to R.drawable.ic_flag_sar,
    "SEK" to R.drawable.ic_flag_sek,
    "SGD" to R.drawable.ic_flag_sgd,
    "THB" to R.drawable.ic_flag_thb,
    "TJS" to R.drawable.ic_flag_tjs,
    "TMT" to R.drawable.ic_flag_tmt,
    "TND" to R.drawable.ic_flag_tnd,
    "TRY" to R.drawable.ic_flag_try,
    "TWD" to R.drawable.ic_flag_twd,
    "UAH" to R.drawable.ic_flag_uah,
    "USD" to R.drawable.ic_flag_usd,
    "UZS" to R.drawable.ic_flag_uzs,
    "VND" to R.drawable.ic_flag_vnd,
    "ZAR" to R.drawable.ic_flag_zar
  )
  return flags[currencyCode] ?: R.drawable.ic_broken_image_24
}

val flagMap = mutableMapOf(
  "AED" to R.string.currency_name_AED,
  "AMD" to R.string.currency_name_AMD,
  "AUD" to R.string.currency_name_AUD,
  "AWG" to R.string.currency_name_AWG,
  "AZN" to R.string.currency_name_AZN,
  "BAM" to R.string.currency_name_BAM,
  "BDT" to R.string.currency_name_BDT,
  "BGN" to R.string.currency_name_BGN,
  "BHD" to R.string.currency_name_BHD,
  "BMD" to R.string.currency_name_BMD,
  "BOB" to R.string.currency_name_BOB,
  "BRL" to R.string.currency_name_BRL,
  "BYN" to R.string.currency_name_BYN,
  "BZD" to R.string.currency_name_BZD,
  "CAD" to R.string.currency_name_CAD,
  "CHF" to R.string.currency_name_CHF,
  "CLP" to R.string.currency_name_CLP,
  "CNY" to R.string.currency_name_CNY,
  "CUC" to R.string.currency_name_CUC,
  "CZK" to R.string.currency_name_CZK,
  "DKK" to R.string.currency_name_DKK,
  "EGP" to R.string.currency_name_EGP,
  "EUR" to R.string.currency_name_EUR,
  "GBP" to R.string.currency_name_GBP,
  "GEL" to R.string.currency_name_GEL,
  "HKD" to R.string.currency_name_HKD,
  "HRK" to R.string.currency_name_HRK,
  "HTG" to R.string.currency_name_HTG,
  "HUF" to R.string.currency_name_HUF,
  "IDR" to R.string.currency_name_IDR,
  "ILS" to R.string.currency_name_ILS,
  "INR" to R.string.currency_name_INR,
  "IQD" to R.string.currency_name_IQD,
  "IRR" to R.string.currency_name_IRR,
  "ISK" to R.string.currency_name_ISK,
  "JMD" to R.string.currency_name_JMD,
  "JPY" to R.string.currency_name_JPY,
  "KGS" to R.string.currency_name_KGS,
  "KHR" to R.string.currency_name_KHR,
  "KRW" to R.string.currency_name_KRW,
  "KZT" to R.string.currency_name_KZT,
  "KWD" to R.string.currency_name_KWD,
  "LAK" to R.string.currency_name_LAK,
  "LBP" to R.string.currency_name_LBP,
  "LKR" to R.string.currency_name_LKR,
  "MAD" to R.string.currency_name_MAD,
  "MDL" to R.string.currency_name_MDL,
  "MKK" to R.string.currency_name_MKK,
  "MOP" to R.string.currency_name_MOP,
  "MXN" to R.string.currency_name_MXN,
  "MYR" to R.string.currency_name_MYR,
  "NOK" to R.string.currency_name_NOK,
  "NPR" to R.string.currency_name_NPR,
  "NZD" to R.string.currency_name_NZD,
  "OMR" to R.string.currency_name_OMR,
  "PHP" to R.string.currency_name_PHP,
  "PKR" to R.string.currency_name_PKR,
  "PLN" to R.string.currency_name_PLN,
  "PGK" to R.string.currency_name_PGK,
  "PYG" to R.string.currency_name_PYG,
  "QAR" to R.string.currency_name_QAR,
  "RON" to R.string.currency_name_RON,
  "RSD" to R.string.currency_name_RSD,
  "RUB" to R.string.currency_name_RUB,
  "SAR" to R.string.currency_name_SAR,
  "SEK" to R.string.currency_name_SEK,
  "SGD" to R.string.currency_name_SGD,
  "THB" to R.string.currency_name_THB,
  "TJS" to R.string.currency_name_TJS,
  "TMT" to R.string.currency_name_TMT,
  "TND" to R.string.currency_name_TND,
  "TRY" to R.string.currency_name_TRY,
  "TWD" to R.string.currency_name_TWD,
  "UAH" to R.string.currency_name_UAH,
  "USD" to R.string.currency_name_USD,
  "UZS" to R.string.currency_name_UZS,
  "VND" to R.string.currency_name_VND,
  "ZAR" to R.string.currency_name_ZAR
)

/**
 * Returns the name's string ID of matching currency code
 */
fun nameMapper(currencyCode: String): Int {
  return flagMap[currencyCode] ?: R.string.currency_name_not_found
}