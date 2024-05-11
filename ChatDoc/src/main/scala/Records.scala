/*
Before running any Scala code, open the project's directory in the terminal and type docker-compose up,  this command builds the Database up.

If the Database wasn't built correctly for any reason, type docker-compose down in a new terminal to stop it and use the up command to re-do it.

To use SQL directly on the Database through terminal for any reason, in project's directory through the terminal, type docker ps and copy the database name, then type docker exec -it database name psql -U postgres

Install the following to build and run the database without issues:
-Docker, Docker Desktop
-PostgreSQL
-Slick library
-Tototoshi Library
*/


import slick.jdbc.PostgresProfile.api.*
import scala.concurrent.Await
import scala.concurrent.duration.*
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import com.github.tototoshi.csv.*
import java.io.File
import slick.jdbc.PositionedParameters

/* case classes here are used to treat a row in a table like an object, so for example, when you construct a symptom case class, you're making a new row for the Symptoms Table*/
sealed trait tables
case class DiseaseDescription(disease: String, description: String)  //disease description table Row definition, the rest of the cases follow similarly ;)

case class Symptoms(disease: String, symptom1: String, symptom2: String, symptom3: String, symptom4: String, symptom5: String, symptom6: String, symptom7: String, symptom8: String, symptom9: String, symptom10: String, symptom11: String, symptom12: String, symptom13: String, symptom14: String, symptom15: String, symptom16: String, symptom17: String, symptom18: String)

case class Precautions(disease: String, precaution1: String, precaution2: String, precaution3: String, precaution4: String)

case class Patient(patient_id: Long, first_name: String, last_name: String, age: String, gender: String)

case class PatientHistory(patient_id: Long, disease: String)

/*These classes are made for the word lexicon file, which helps the bot to understand the context and sentiment of speaking */
case class PositiveNoun(noun: String, x: String)
case class PositiveVerb(verb: String, x: String)
case class PositiveAdjective (adjective: String, x: String)
case class PositiveAdverb (adverb: String, x: String)
case class PositiveWords (word: String, x: String)

case class NegativeNoun(noun: String, x: String)
case class NegativeVerb(verb: String, x: String)
case class NegativeAdjective (adjective: String, x: String)
case class NegativeAdverb (adverb: String, x: String)
case class NegativeWords (word: String, x: String)


/*Disease Description Table objects used to interact with it, you will mainly need to access the lazy val to make queries and the case class to create a new row (entry)*/
object DiseaseDescription {
  def tupled: ((String, String)) => DiseaseDescription = { (disease, description) => DiseaseDescription(disease, description) }
}
class DescriptionTable(tag: Tag) extends Table[DiseaseDescription](tag, Some("hospital"), "DiseaseDescription")   //disease description table itself
{
  def disease = column[String]("disease")
  def description = column[String]("description")

  override def * = (disease, description) <> (DiseaseDescription.tupled, DiseaseDescription.unapply)
}
lazy val descriptionTable = TableQuery[DescriptionTable]      //used for queries on disease description table







/*Symptoms Table objects used to interact with it, you will mainly need to access the lazy val to make queries and the case class to create a new row (entry)*/
object Symptoms {
  def tupled: ((String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String, String)) => Symptoms = { (disease, symptom1, symptom2, symptom3, symptom4, symptom5, symptom6, symptom7, symptom8, symptom9, symptom10, symptom11, symptom12, symptom13, symptom14, symptom15, symptom16, symptom17, symptom18) => Symptoms(
      disease, symptom1, symptom2, symptom3, symptom4, symptom5, symptom6, symptom7, symptom8, symptom9, symptom10, symptom11, symptom12, symptom13, symptom14, symptom15, symptom16, symptom17, symptom18: String
    )}
}
class SymptomsTable(tag: Tag) extends Table[Symptoms](tag, Some("hospital"), "Symptoms")
{
  def disease = column[String]("disease")
  def symptom1 = column[String]("symptom1")
  def symptom2 = column[String]("symptom2")
  def symptom3 = column[String]("symptom3")
  def symptom4 = column[String]("symptom4")
  def symptom5 = column[String]("symptom5")
  def symptom6 = column[String]("symptom6")
  def symptom7 = column[String]("symptom7")
  def symptom8 = column[String]("symptom8")
  def symptom9 = column[String]("symptom9")
  def symptom10 = column[String]("symptom10")
  def symptom11 = column[String]("symptom11")
  def symptom12 = column[String]("symptom12")
  def symptom13 = column[String]("symptom13")
  def symptom14 = column[String]("symptom14")
  def symptom15 = column[String]("symptom15")
  def symptom16 = column[String]("symptom16")
  def symptom17 = column[String]("symptom17")
  def symptom18 = column[String]("symptom18")

  override def * = (disease, symptom1, symptom2, symptom3, symptom4, symptom5, symptom6, symptom7, symptom8, symptom9, symptom10, symptom11, symptom12, symptom13, symptom14, symptom15, symptom16, symptom17, symptom18) <> (Symptoms.tupled, Symptoms.unapply)
}
lazy val symptomsTable = TableQuery[SymptomsTable]







/*Precautions Table objects used to interact with it, you will mainly need to access the lazy val to make queries and the case class to create a new row (entry)*/
object Precautions {
  def tupled: ((String, String, String, String, String)) => Precautions = { (disease, precaution1, precaution2, precaution3, precaution4) => Precautions(disease, precaution1, precaution2, precaution3, precaution4)}
}
class PrecautionsTable(tag: Tag) extends Table[Precautions](tag, Some("hospital"), "Precautions")
{
  def disease = column[String]("disease")
  def precaution1 = column[String]("precaution1")
  def precaution2 = column[String]("precaution2")
  def precaution3 = column[String]("precaution3")
  def precaution4 = column[String]("precaution4")

  override def * = (disease, precaution1, precaution2, precaution3, precaution4) <> (Precautions.tupled, Precautions.unapply)
}
lazy val precautionsTable = TableQuery[PrecautionsTable]







/* Patient Table, you'll be inserting many rows here, patient_id is a unique key for each patient, it is auto incremented, as each new petient will have a an id higher by 1 than the patient before him*/
object Patient {
  def tupled: ((Long, String, String, String, String)) => Patient = { (patient_id, first_name, last_name, age, gender) => Patient(patient_id, first_name, last_name, age, gender)}
}
class PatientTable(tag: Tag) extends Table[Patient](tag, Some("hospital"), "Patient")
{
  def patient_id = column[Long]("patient_id", O.PrimaryKey, O.AutoInc)
  def first_name = column[String]("first_name")
  def last_name = column[String]("last_name")
  def age = column[String]("age")
  def gender = column[String]("gender")

  override def * = (patient_id, first_name, last_name, age, gender) <> (Patient.tupled, Patient.unapply)
}
lazy val patientTable = TableQuery[PatientTable]







/* Patient History Table, as each patient may have a long history of illness, you can find each patient's history here using their unique id */
object PatientHistory {
  def tupled: ((Long, String)) => PatientHistory = { (patient_id, disease) => PatientHistory(patient_id, disease)}
}
class PatientHistoryTable(tag: Tag) extends Table[PatientHistory](tag, Some("hospital"), "PatientHistory")
{
  def patient_id = column[Long]("patient_id")
  def disease = column[String]("disease")

  override def * = (patient_id, disease) <> (PatientHistory.tupled, PatientHistory.unapply)
}
lazy val patientHistoryTable = TableQuery[PatientHistoryTable]





//new tables addition to the ones above, same rules apply
object PositiveNoun {
  def tupled: ((String, String)) => PositiveNoun = { (noun, x) => PositiveNoun(noun, x)}
}
object PositiveVerb {
  def tupled: ((String, String)) => PositiveVerb = { (verb, x) => PositiveVerb(verb, x)}
}
object PositiveAdjective {
  def tupled: ((String, String)) => PositiveAdjective = { (adjective, x) => PositiveAdjective(adjective, x)}
}
object PositiveAdverb {
  def tupled: ((String, String)) => PositiveAdverb = { (adverb, x) => PositiveAdverb(adverb, x)}
}
object PositiveWords {
  def tupled: ((String, String)) => PositiveWords = { (word, x) => PositiveWords(word, x)}
}
object NegativeNoun {
  def tupled: ((String, String)) => NegativeNoun = { (noun, x) => NegativeNoun(noun, x)}
}
object NegativeVerb {
  def tupled: ((String, String)) => NegativeVerb = { (verb, x) => NegativeVerb(verb, x)}
}
object NegativeAdjective {
  def tupled: ((String, String)) => NegativeAdjective = { (adjective, x) => NegativeAdjective(adjective, x)}
}
object NegativeAdverb {
  def tupled: ((String, String)) => NegativeAdverb = { (adverb, x) => NegativeAdverb(adverb, x)}
}
object NegativeWords {
  def tupled: ((String, String)) => NegativeWords = { (word, x) => NegativeWords(word, x)}
}

class PositiveNounTable(tag: Tag) extends Table[PositiveNoun](tag, Some("hospital"), "PositiveNoun")
{
  def noun = column[String]("noun")
  def x = column[String]("x")
  def * = (noun, x) <> (PositiveNoun.tupled, PositiveNoun.unapply)
}
lazy val positiveNounTable = TableQuery[PositiveNounTable]


class PositiveVerbTable(tag: Tag) extends Table[PositiveVerb](tag, Some("hospital"), "PositiveVerb")
{
  def verb = column[String]("verb")
  def x = column[String]("x")

  def * = (verb, x) <> (PositiveVerb.tupled, PositiveVerb.unapply)
}
lazy val positiveVerbTable = TableQuery[PositiveVerbTable]


class PositiveAdjectiveTable(tag: Tag) extends Table[PositiveAdjective](tag, Some("hospital"), "PositiveAdjective")
{
  def adjective = column[String]("adjective")
  def x = column[String]("x")

  def * = (adjective, x) <> (PositiveAdjective.tupled, PositiveAdjective.unapply)
}
lazy val positiveAdjectiveTable = TableQuery[PositiveAdjectiveTable]


class PositiveAdverbTable(tag: Tag) extends Table[PositiveAdverb](tag, Some("hospital"), "PositiveAdverb")
{
  def adverb = column[String]("adverb")
  def x = column[String]("x")

  def * = (adverb, x) <> (PositiveAdverb.tupled, PositiveAdverb.unapply)
}
lazy val positiveAdverbTable = TableQuery[PositiveAdverbTable]


class PositiveWordsTable(tag: Tag) extends Table[PositiveWords](tag, Some("hospital"), "PositiveWords")
{
  def word = column[String]("word")
  def x = column[String]("x")

  def * = (word, x) <> (PositiveWords.tupled, PositiveWords.unapply)
}
lazy val positiveWordsTable = TableQuery[PositiveWordsTable]





class NegativeNounTable(tag: Tag) extends Table[NegativeNoun](tag, Some("hospital"), "NegativeNoun")
{
  def noun = column[String]("noun")
  def x = column[String]("x")

  def * = (noun, x) <> (NegativeNoun.tupled, NegativeNoun.unapply)
}
lazy val negativeNounTable = TableQuery[NegativeNounTable]


class NegativeVerbTable(tag: Tag) extends Table[NegativeVerb](tag, Some("hospital"), "NegativeVerb")
{
  def verb = column[String]("verb")
  def x = column[String]("x")

  def * = (verb, x) <> (NegativeVerb.tupled, NegativeVerb.unapply)
}
lazy val negativeVerbTable = TableQuery[NegativeVerbTable]


class NegativeAdjectiveTable(tag: Tag) extends Table[NegativeAdjective](tag, Some("hospital"), "NegativeAdjective")
{
  def adjective = column[String]("adjective")
  def x = column[String]("x")

  def * = (adjective, x) <> (NegativeAdjective.tupled, NegativeAdjective.unapply)
}
lazy val negativeAdjectiveTable = TableQuery[NegativeAdjectiveTable]


class NegativeAdverbTable(tag: Tag) extends Table[NegativeAdverb](tag, Some("hospital"), "NegativeAdverb")
{
  def adverb = column[String]("adverb")

  def x = column[String]("x")

  def * = (adverb, x) <> (NegativeAdverb.tupled, NegativeAdverb.unapply)
}
lazy val negativeAdverbTable = TableQuery[NegativeAdverbTable]


class NegativeWordsTable(tag: Tag) extends Table[NegativeWords](tag, Some("hospital"), "NegativeWords")
{
  def word = column[String]("word")
  def x = column[String]("x")

  def * = (word, x) <> (NegativeWords.tupled, NegativeWords.unapply)
}
lazy val negativeWordsTable = TableQuery[NegativeWordsTable]








val db = Database.forConfig("postgres")   // DataBase object by which you can run queries on a selected Table


def csvFile(path: String): List[List[String]] = { //CSV File reader, given a file path
  val reader = CSVReader.open(new File(path))
  reader.all()
}


/* This function is used to insert the contents of the datasets used in this project into their respective tables in the DataBase.
 ***Remember to change all file paths to suit your device (absolute path)***
 The original plan was to import the CSV Files' content directly using SQL, but for some reason Postgres and Psql both could not see the files
*/
def fillMainTables(): Unit =
{
  val SymptomsPath = "db\\disease_symptoms.csv"
  csvFile(SymptomsPath).foreach((row: List[String]) =>
    insertSymptom(Symptoms(row.head, row(1), row(2), row(3), row(4), row(5), row(6), row(7), row(8), row(9), row(10), row(11), row(12), row(13), row(14), row(15), row(16), row(17), row(18)))
  )

  val DescriptionsPath = "db\\disease_description.csv"
  csvFile(DescriptionsPath).foreach((row: List[String]) => insertDescription(DiseaseDescription(row.head, row.tail.head)))

  val PrecautionsPath = "db\\precautions.csv"
  csvFile(PrecautionsPath).foreach((row: List[String]) => insertPrecaution(Precautions(row.head, row(1), row(2), row(3), row(4))))

  val PositiveNounPath = "db\\words_lexicon\\PositiveNoun.csv"
  csvFile(PositiveNounPath).foreach((row: List[String]) => {
    val query = positiveNounTable += PositiveNoun(row.head, "")
    val future = db.run(query)

    future.onComplete {
      case Success(posnoun) => println(s"done, new positive noun $posnoun")
      case Failure(ex) => println(s"failed, reason is $ex")
    }

    Thread.sleep(20)
  })

  val PositiveVerbPath = "db\\words_lexicon\\PositiveVerb.csv"
  csvFile(PositiveVerbPath).foreach((row: List[String]) => {
    val query = positiveVerbTable += PositiveVerb(row.head, "")
    val future = db.run(query)

    future.onComplete {
      case Success(posnoun) => println(s"done, new positive verb $posnoun")
      case Failure(ex) => println(s"failed, reason is $ex")
    }

    Thread.sleep(20)
  })

  val PositiveAdjectivePath = "db\\words_lexicon\\PositiveAdjective.csv"
  csvFile(PositiveAdjectivePath).foreach((row: List[String]) => {
    val query = positiveAdjectiveTable += PositiveAdjective(row.head, "")
    val future = db.run(query)

    future.onComplete {
      case Success(posnoun) => println(s"done, new positive adjective $posnoun")
      case Failure(ex) => println(s"failed, reason is $ex")
    }

    Thread.sleep(20)
  })

  val PositiveAdverbPath = "db\\words_lexicon\\PositiveAdverb.csv"
  csvFile(PositiveAdverbPath).foreach((row: List[String]) => {
    val query = positiveAdverbTable += PositiveAdverb(row.head, "")
    val future = db.run(query)

    future.onComplete {
      case Success(posnoun) => println(s"done, new positive adverb $posnoun")
      case Failure(ex) => println(s"failed, reason is $ex")
    }

    Thread.sleep(20)
  })

  val PositiveWordsPath = "db\\words_lexicon\\PositiveWords.csv"
  csvFile(PositiveWordsPath).foreach((row: List[String]) => {
    val query = positiveWordsTable += PositiveWords(row.head, "")
    val future = db.run(query)

    future.onComplete {
      case Success(posnoun) => println(s"done, new positive word $posnoun")
      case Failure(ex) => println(s"failed, reason is $ex")
    }

    Thread.sleep(20)
  })

  val NegativeNounPath = "db\\words_lexicon\\NegativeNoun.csv"
  csvFile(NegativeNounPath).foreach((row: List[String]) => {
    val query = negativeNounTable += NegativeNoun(row.head, "")
    val future = db.run(query)

    future.onComplete {
      case Success(posnoun) => println(s"done, new negative noun $posnoun")
      case Failure(ex) => println(s"failed, reason is $ex")
    }

    Thread.sleep(20)
  })

  val NegativeVerbPath = "db\\words_lexicon\\NegativeVerb.csv"
  csvFile(NegativeVerbPath).foreach((row: List[String]) => {
    val query = negativeVerbTable += NegativeVerb(row.head, "")
    val future = db.run(query)

    future.onComplete {
      case Success(posnoun) => println(s"done, new negative verb $posnoun")
      case Failure(ex) => println(s"failed, reason is $ex")
    }

    Thread.sleep(20)
  })

  val NegativeAdjectivePath = "db\\words_lexicon\\NegativeAdjective.csv"
  csvFile(NegativeAdjectivePath).foreach((row: List[String]) => {
    val query = negativeAdjectiveTable += NegativeAdjective(row.head, "")
    val future = db.run(query)

    future.onComplete {
      case Success(posnoun) => println(s"done, new negative adjective $posnoun")
      case Failure(ex) => println(s"failed, reason is $ex")
    }

    Thread.sleep(20)
  })

  val NegativeAdverbPath = "db\\words_lexicon\\NegativeAdverb.csv"
  csvFile(NegativeAdverbPath).foreach((row: List[String]) => {
    val query = negativeAdverbTable += NegativeAdverb(row.head, "")
    val future = db.run(query)

    future.onComplete {
      case Success(posnoun) => println(s"done, new negative adverb $posnoun")
      case Failure(ex) => println(s"failed, reason is $ex")
    }

    Thread.sleep(20)
  })

  val NegativeWordsPath = "db\\words_lexicon\\NegativeWords.csv"
  csvFile(NegativeWordsPath).foreach((row: List[String]) => {
    val query = negativeWordsTable += NegativeWords(row.head, "")
    val future = db.run(query)

    future.onComplete {
      case Success(posnoun) => println(s"done, new negative word $posnoun")
      case Failure(ex) => println(s"failed, reason is $ex")
    }

    Thread.sleep(20)
  })

}


//Use this to insert new row of symptoms into the Symptoms Table, it takes a case class object of the table disired. All Insert Functions follow accordingly for the rest of the Tables
def insertSymptom(symptom: Symptoms): Unit = {
  val query = symptomsTable += symptom
  val future = db.run(query)
  future.onComplete {
    case Success(symp) => println(s"done, new symptom $symp")
    case Failure(ex) => println(s"failed, reason is $ex")
  }

  Thread.sleep(20)
}

def insertDescription(description: DiseaseDescription): Unit = {
  val query = descriptionTable += description
  val future = db.run(query)
  future.onComplete {
    case Success(newDrug) => println(s"done, new drug description $newDrug")
    case Failure(ex) => println(s"failed, reason is $ex")
  }

  Thread.sleep(20)
}

def insertPrecaution(precaution: Precautions): Unit = {
  val query = precautionsTable += precaution
  val future = db.run(query)
  future.onComplete {
    case Success(precau) => println(s"done, new disease precaution $precau")
    case Failure(ex) => println(s"failed, reason is $ex")
  }

  Thread.sleep(20)
}






//all Read Functions takes a query as an argument and return the result of the query as a sequence of rows of type TableRow(refer to case classes above)

def readDescription(future: Future[Seq[TableQuery.Extract[DescriptionTable]]]): Seq[DiseaseDescription] = {
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }
  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readSymptoms(future: Future[Seq[TableQuery.Extract[SymptomsTable]]]): Seq[Symptoms] = {
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }
  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readPrecaution(future: Future[Seq[TableQuery.Extract[PrecautionsTable]]]): Seq[Precautions] = {
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }
  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}




def readPatient(future: Future[Seq[TableQuery.Extract[PatientTable]]]): Seq[Patient] = {
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }
  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readPatientHistory(future: Future[Seq[TableQuery.Extract[PatientHistoryTable]]]): Seq[PatientHistory] = {
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }
  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}



def readPNouns(future: Future[Seq[TableQuery.Extract[PositiveNounTable]]]): Seq[PositiveNoun] =
{
  future.onComplete {
  case Failure(ex) => println(s"failed cause of dis: $ex")
  case _ => Nil
  }

  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readPVerbs(future: Future[Seq[TableQuery.Extract[PositiveVerbTable]]]): Seq[PositiveVerb] =
{
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }

  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readPAdjectives(future: Future[Seq[TableQuery.Extract[PositiveAdjectiveTable]]]): Seq[PositiveAdjective] =
{
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }

  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readPAdverbs(future: Future[Seq[TableQuery.Extract[PositiveAdverbTable]]]): Seq[PositiveAdverb] =
{
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }

  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readPWords(future: Future[Seq[TableQuery.Extract[PositiveWordsTable]]]): Seq[PositiveWords] =
{
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }

  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}



def readNNouns(future: Future[Seq[TableQuery.Extract[NegativeNounTable]]]): Seq[NegativeNoun] =
{
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }

  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readNVerbs(future: Future[Seq[TableQuery.Extract[NegativeVerbTable]]]): Seq[NegativeVerb] =
{
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }

  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readNAdjectives(future: Future[Seq[TableQuery.Extract[NegativeAdjectiveTable]]]): Seq[NegativeAdjective] =
{
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }

  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readNAdverbs(future: Future[Seq[TableQuery.Extract[NegativeAdverbTable]]]): Seq[NegativeAdverb] =
{
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }

  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}

def readNWords(future: Future[Seq[TableQuery.Extract[NegativeWordsTable]]]): Seq[NegativeWords] =
{
  future.onComplete {
    case Failure(ex) => println(s"failed cause of dis: $ex")
    case _ => Nil
  }

  Thread.sleep(50)
  Await.result(future, Duration.Inf)
}


/*Since we won't need to manipulate any Table's further except the Patients table, this function creates a new patient record (Row) in the Patients Table.*/
def newPatient(patient: Patient): Patient = {
  val query = patientTable += patient
  val future = db.run(query)
  future.onComplete {
    case Success(row) => println(s"done, new Patient: \n $row")
    case Failure(ex) => println(s"failed, reason is $ex")
  }

  val newOne = readPatient(db.run(patientTable.sortBy(_.patient_id.desc).result)).head

  Thread.sleep(20)

  newOne
}

//you can make a new patient history record regardless of entering a new patient with it, as the patient can give you a brief about his history after a while of saving his personal Data.
def newPatientHistoryEntry(patientHistory: PatientHistory): Unit = {
  val query = patientHistoryTable += patientHistory
  val future = db.run(query)
  future.onComplete {
    case Success(row) => println(s"done, new Patient Disease History: \n $row")
    case Failure(ex) => println(s"failed, reason is $ex")
  }
  Thread.sleep(20)
}


//You can delete a Patient with this function, it takes a Patient Object and look it up, then delete it from patients table along with deleting his history from the History Table
def deletePatient(patient: Patient): Unit = {

  val future = db.run(patientTable.filter(_.patient_id === patient.patient_id).delete)
  future.onComplete {
    case Success(row) => println(s"done, Patient Data Deleted: \n $row")
    case Failure(ex) => println(s"failed, reason is $ex")
  }

  val futureHistory = db.run(patientHistoryTable.filter(_.patient_id === patient.patient_id).delete)
  futureHistory.onComplete {
    case Success(row) => println(s"done, Patient History Deleted: \n $row")
    case Failure(ex) => println(s"failed, reason is $ex")
  }


  Thread.sleep(20)
}
