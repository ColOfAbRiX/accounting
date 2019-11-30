package com.colofabrix.scala.accounting.csv

import com.colofabrix.scala.accounting.csv.CsvDefinitions.CsvRow

// Taken from real CSV files of banks
object CsvData {

  val barclaysCsv: List[CsvRow] = List(
    List("Number", "Date", "Account", "Amount", "Subcategory", "Memo"),
    List(" ", "08/11/2019", "20-32-06 13152170", "6.88", "DIRECTDEP,DELLE LUCHE           Food 31/10         BGC"),
    List("	 ", "08/11/2019", "20-32-06 13152170", "-235.00", "FT,BRIAN CUMMING         TUNNEL D4          FT"),
    List("	 ", "08/11/2019", "20-32-06 13152170", "-23.63", "FT,CLAIRE DELLE LUCHE    GROCERY            FT"),
    List("	 ", "08/11/2019", "20-32-06 13152170", "-2.00", "PAYMENT,CRV*BEST FOOD CENT    ON 07 NOV          BCC"),
    List("	 ", "07/11/2019", "20-32-06 13152170", "-5.70", "PAYMENT,CRV*EASY RUSTY BAR    ON 06 NOV          BCC"),
    List("	 ", "07/11/2019", "20-32-06 13152170", "-4.86", "PAYMENT,CRV*BEST FOOD CENT    ON 06 NOV          BCC"),
    List("	 ", "05/11/2019", "20-32-06 13152170", "-430.00", "PAYMENT,HALIFAX CLARITY MA    5253030006544290   BBP"),
    List("	 ", "05/11/2019", "20-32-06 13152170", "-4.95", "PAYMENT,CRV*WEWORK (1219)     ON 04 NOV          BCC"),
    List("	 ", "04/11/2019", "20-32-06 13152170", "-100.00", "FT,TOHUR A"),
    List(),
    List()
  )

  val halifaxCsv: List[CsvRow] = List(

  )

  val starlingCsv: List[CsvRow] = List(

  )

  val amexCsv: List[CsvRow] = List(
    List("21/10/2019", "Reference: AT192960031000011300953", " 1.50", "TFL TRAVEL CHARGE TFL.GOV.UK/CP", " Process Date 22/10/2019"),
    List("23/10/2019", "Reference: AT192970032000011327509", " 3.30", "MARKS & SPENCER SOUT LONDON", "RETAIL GOODS Process Date 24/10/2019  RETAIL GOODS"),
    List("24/10/2019", "Reference: AT192980030000011350877", " 8.10", "MARKS & SPENCER SOUT LONDON", "RETAIL GOODS Process Date 25/10/2019  RETAIL GOODS"),
    List("25/10/2019", "Reference: AT192990024000011384110", " 16.76", "BIERSCHENKE LONDON", " Process Date 25/10/2019"),
    List("25/10/2019", "Reference: AT192990024000011350781", " 6.80", "TFL TRAVEL CHARGE TFL.GOV.UK/CP", " Process Date 25/10/2019"),
    List("26/10/2019", "Reference: AT193000024000011249387", " 7.50", "IZ *UKGP LIMITED SIBSON", " Process Date 26/10/2019"),
    List("26/10/2019", "Reference: AT193000024000011265265", " 2.40", "TFL TRAVEL CHARGE TFL.GOV.UK/CP", " Process Date 26/10/2019"),
    List("26/10/2019", "Reference: AT193010030000011235226", " 35.23", "TRAINLINE.COM LONDON", " Process Date 27/10/2019"),
    List("27/10/2019", "Reference: AT193010030000011250097", " 2.40", "TFL TRAVEL CHARGE TFL.GOV.UK/CP", " Process Date 27/10/2019"),
    List("27/10/2019", "Reference: AT193010030000011238282", " 7.75", "THE LORD TREDEGAR LONDON", " Process Date 27/10/2019"),
    List(),
  )

}
