package com.colofabrix.scala.accounting.csv

import java.time.LocalDate
import com.colofabrix.scala.accounting.csv.CsvDefinitions.CsvRow
import com.colofabrix.scala.accounting.model.{
  AmexTransaction,
  BarclaysTransaction,
  HalifaxTransaction,
  StarlingTransaction
}

// Taken from real CSV files of banks (data anonymized)

// format: off
object CsvData {

  def date(year: Int, month: Int, day: Int): LocalDate = LocalDate.of(year, month, day)

  // Barclay's

  val barclaysCsv: List[CsvRow] = List(
    List("Number", "Date", "Account", "Amount", "Subcategory", "Memo"),
    List(" ", "08/11/2019", "20-32-06 13152170", "6.88", "DIRECTDEP", "DELLELLE           Food 31/10         BGC"),
    List("	 ", "08/11/2019", "20-32-06 13152170", "-235.00", "FT", "ANDREW CUMMING         TUNNEL D4          FT"),
    List("	 ", "08/11/2019", "20-32-06 13152170", "-23.63", "FT", "C DELLELLE    GROCERY            FT"),
    List("	 ", "08/11/2019", "20-32-06 13152170", "-2.00", "PAYMENT", "CRV*BEST FOOD CENT    ON 07 NOV          BCC"),
    List("	 ", "07/11/2019", "20-32-06 13152170", "-5.70", "PAYMENT", "CRV*EASY BIKE BAR    ON 06 NOV          BCC"),
    List("	 ", "07/11/2019", "20-32-06 13152170", "-4.86", "PAYMENT", "CRV*BEST FOOD CENT    ON 06 NOV          BCC"),
    List("	 ", "05/11/2019", "20-32-06 13152170", "-430.00", "PAYMENT", "HALIFAX CLARITY MA    5353130107545290   BBP"),
    List("	 ", "05/11/2019", "20-32-06 13152170", "-4.95", "PAYMENT", "CRV*YOUWORK (1219)     ON 04 NOV          BCC"),
    List("	 ", "04/11/2019", "20-32-06 13152170", "-100.00", "FT", "THOR A"),
    List(),
    List()
  )

  val barclaysTransactions: List[BarclaysTransaction] = List(
    BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", 6.88, "directdep", "dellelle food 31/10 bgc"),
    BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -235.0, "ft", "andrew cumming tunnel d4 ft"),
    BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -23.63, "ft", "c dellelle grocery ft"),
    BarclaysTransaction(None, date(2019, 11, 8), "20-32-06 13152170", -2.0, "payment", "crv*best food cent on 07 nov bcc"),
    BarclaysTransaction(None, date(2019, 11, 7), "20-32-06 13152170", -5.7, "payment", "crv*easy bike bar on 06 nov bcc"),
    BarclaysTransaction(None, date(2019, 11, 7), "20-32-06 13152170", -4.86, "payment", "crv*best food cent on 06 nov bcc"),
    BarclaysTransaction(None, date(2019, 11, 5), "20-32-06 13152170", -430.0, "payment", "halifax clarity ma 5353130107545290 bbp"),
    BarclaysTransaction(None, date(2019, 11, 5), "20-32-06 13152170", -4.95, "payment", "crv*youwork (1219) on 04 nov bcc"),
    BarclaysTransaction(None, date(2019, 11, 4), "20-32-06 13152170", -100.0, "ft", "thor a")
  )

  // Halifax

  val halifaxCsv: List[CsvRow] = List(
    List("Date", "Date entered", "Reference", "Description", "Amount"),
    List("07/11/2019", "07/11/2019", "99630930", "INTEREST ", "0.65"),
    List("04/11/2019", "05/11/2019", "68125600", "PAYMENT RECEIVED - THAN ", "-430.00"),
    List("01/11/2019", "01/11/2019", "99691550", "DIRECT DEBIT PAYMENT -  ", "-26.32"),
    List("21/10/2019", "22/10/2019", "10256095", "SELF SERVICE NE  ", "17.06"),
    List("19/10/2019", "22/10/2019", "10210344", "IRISH DRUID            ", "12.55"),
    List("19/10/2019", "22/10/2019", "10209649", "COLLINA RISTORANTE        ", "21.64"),
    List("19/10/2019", "22/10/2019", "10209091", "BOOKLET LIBRERIE        ", "12.12"),
    List("19/10/2019", "22/10/2019", "10209050", "PARKING GEST     ", "2.60"),
    List("19/10/2019", "21/10/2019", "10224975", "IPER CONAD              ", "31.17"),
    List()
  )

  val halifaxTransactions: List[HalifaxTransaction] = List(
    HalifaxTransaction(date(2019, 11,  7), date(2019, 11,  7), "99630930", "interest", -0.65),
    HalifaxTransaction(date(2019, 11,  4), date(2019, 11,  5), "68125600", "payment received - than", 430.0),
    HalifaxTransaction(date(2019, 11,  1), date(2019, 11,  1), "99691550", "direct debit payment -", 26.32),
    HalifaxTransaction(date(2019, 10, 21), date(2019, 10, 22), "10256095", "self service ne", -17.06),
    HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 22), "10210344", "irish druid", -12.55),
    HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 22), "10209649", "collina ristorante", -21.64),
    HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 22), "10209091", "booklet librerie", -12.12),
    HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 22), "10209050", "parking gest", -2.60),
    HalifaxTransaction(date(2019, 10, 19), date(2019, 10, 21), "10224975", "iper conad", -31.17)
  )

  // Starling

  val starlingCsv: List[CsvRow] = List(
    List("Date", "Counter Party", "Reference", "Type", "Amount (GBP)", "Balance (GBP)"),
    List("", "Opening Balance", "", "", "", "0.00"),
    List("01/03/2019", "COLUMN F", "TOP UP STARLING", "FASTER PAYMENT", "100.00", "100.00"),
    List("04/03/2019", "Butler Brewery C Chelmsford", "IZ *BUTLER BREWERY C Chelmsford    GBR", "CONTACTLESS", "-8.00", "92.00"),
    List("04/03/2019", "Sainsbury's", "SAINSBURYS SACAT 0768  CHELMSFORD    GBR", "CONTACTLESS", "-3.70", "88.30"),
    List()
  )

  val starlingTransactions: List[StarlingTransaction] = List(
    StarlingTransaction(date(2019, 3, 1), "column f", "top up starling", "faster payment", 100.0, 100.0),
    StarlingTransaction(date(2019, 3, 4), "butler brewery c chelmsford", "iz *butler brewery c chelmsford gbr", "contactless", -8.0, 92.0),
    StarlingTransaction(date(2019, 3, 4), "sainsbury's", "sainsburys sacat 0768 chelmsford gbr", "contactless", -3.7, 88.3)
  )

  // American Express

  val amexCsv: List[CsvRow] = List(
    List("21/10/2019", "Reference: AT192160041000011301953", " 1.50", "YGA TRAVEL CHARGE YGA.GOV.NL/CP", " Process Date 22/10/2019"),
    List("23/10/2019", "Reference: AT192170042000011328509", " 3.30", "MARKS & SPENCER SOUT", "RETAIL GOODS Process Date 24/10/2019  RETAIL GOODS"),
    List("24/10/2019", "Reference: AT192180040000011351877", " 8.10", "MARKS & SPENCER SOUT", "RETAIL GOODS Process Date 25/10/2019  RETAIL GOODS"),
    List("25/10/2019", "Reference: AT192190034000011385110", " 16.76", "BEERGERMANHALL", " Process Date 25/10/2019"),
    List("25/10/2019", "Reference: AT192190034000011350881", " 6.80", "YGA TRAVEL CHARGE YGA.GOV.NL/CP", " Process Date 25/10/2019"),
    List("26/10/2019", "Reference: AT193100034000011249397", " 7.50", "IZ *PGKU LIMITED BEWDLEY", " Process Date 26/10/2019"),
    List("26/10/2019", "Reference: AT193100034000011266265", " 2.40", "YGA TRAVEL CHARGE YGA.GOV.NL/CP", " Process Date 26/10/2019"),
    List("26/10/2019", "Reference: AT193110050000011236226", " 35.23", "TRAINLINE.COM", " Process Date 27/10/2019"),
    List("27/10/2019", "Reference: AT193110050000011250107", " 2.40", "YGA TRAVEL CHARGE YGA.GOV.NL/CP", " Process Date 27/10/2019"),
    List("27/10/2019", "Reference: AT193110060000011238382", " 7.75", "THE LORD MORRIS", " Process Date 27/10/2019"),
    List()
  )

  val amexTransactions: List[AmexTransaction] = List(
    AmexTransaction(date(2019, 10, 21), "reference: at192160041000011301953", -1.5, "yga travel charge yga.gov.nl/cp", "process date 22/10/2019"),
    AmexTransaction(date(2019, 10, 23), "reference: at192170042000011328509", -3.3, "marks & spencer sout", "retail goods process date 24/10/2019 retail goods"),
    AmexTransaction(date(2019, 10, 24), "reference: at192180040000011351877", -8.1, "marks & spencer sout", "retail goods process date 25/10/2019 retail goods"),
    AmexTransaction(date(2019, 10, 25), "reference: at192190034000011385110", -16.76, "beergermanhall", "process date 25/10/2019"),
    AmexTransaction(date(2019, 10, 25), "reference: at192190034000011350881", -6.8, "yga travel charge yga.gov.nl/cp", "process date 25/10/2019"),
    AmexTransaction(date(2019, 10, 26), "reference: at193100034000011249397", -7.5, "iz *pgku limited bewdley", "process date 26/10/2019"),
    AmexTransaction(date(2019, 10, 26), "reference: at193100034000011266265", -2.4, "yga travel charge yga.gov.nl/cp", "process date 26/10/2019"),
    AmexTransaction(date(2019, 10, 26), "reference: at193110050000011236226", -35.23, "trainline.com", "process date 27/10/2019"),
    AmexTransaction(date(2019, 10, 27), "reference: at193110050000011250107", -2.4, "yga travel charge yga.gov.nl/cp", "process date 27/10/2019"),
    AmexTransaction(date(2019, 10, 27), "reference: at193110060000011238382", -7.75,"the lord morris", "process date 27/10/2019")
  )

}
// format: on
