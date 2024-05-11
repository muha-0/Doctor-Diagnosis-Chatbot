import slick.jdbc.PostgresProfile.api.*
import scala.io.StdIn.readLine
import scala.annotation.tailrec
import scala.util.Random
import scala.io.StdIn
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.Random

/*
too many symptoms problem
can be fixed by either checking how many symptoms in symptoms list then ask for more
maybe list up to 3 symptoms
or if list of missing symptopms too big DONE

IF LIST OF disseases is 0 not found immedisatly instead of waiting for response
so decide after keepfiltering DONE
and if the user says no or the new list is empty then we need to choose the next five symptoms to show for the user

get answer
tokenize
check if it makes sense
if it does then continue normally
if it doesn;t tell to spcify more


 ###CHECK 4.2 AND 4.3
 */


var handledInput:Map[String,List[String]]= null
var symps:List[String] = null
var diseases:List[List[String]] = null

@main
def doctor(userInput : String): String={

  if(phase == 4){
    phase += 0.1
    return "How can I help today?"
  }
  else if(phase == 4.1){

    handledInput = reply(label(handlephrases2(tokenize(userInput))))
    symps = handledInput("Symptoms").map(symp => " " + symp)
    if(symps.nonEmpty){phase+=0.1}
    diseases = symptomsCheck(symps)
    var str = ""
    handledInput("Prompts").foreach(str+=_+"\n")
    if(str == ""){
      str = "Sorry to hear that, but I am here to help, can you write all the symptoms?"
    }
    return str
  }
  else if(phase == 4.2 || phase == 4.3) {
    return decideDisease(userInput)
  }
  ""

}

//2
def decideDisease(answer:String):String={

  val answer_map: Map[String, List[String]] = reply(label(handlephrases2(tokenize(answer))))
  val symptoms = answer_map("Symptoms").map(symp => " " + symp)
  symps = symps ++ symptoms
  symps = symps.distinct
  println(symps)
  if (symps.length < 3) {
    return "Can you specify more?"
  }
  var botresponse = "Do you have any of these?\n"
  diseases = keepFiltering(symps,diseases).distinct
  val missingSymptoms = missSymptoms(diseases, symps)
  val someSymptoms = subSymptoms(missingSymptoms)
  someSymptoms.foreach(symp => if (symp != "") {
    botresponse += symp + "\n"
  })


  println(diseases.length)
  if (diseases.length == 1) {
    phase += 0.2
    return s"I think you might have ${diseases.head.head}\n" + s"${getRecordsDescription(diseases.head.head)}" + "Here are some precautions\n" + s"${getRecordsPrecautions(diseases.head.head)}"
  }
  else if (diseases.isEmpty) {
    phase += 0.2
    return "I'm sorry I can't find any disease with these symptoms.\nI would advise to go to a doctor asap."
  }
  return botresponse+"If no, write more symptoms please"

}

def subSymptoms(symptoms: List[String]): List[String] = {
  if (symptoms.length > 5) Random.shuffle(symptoms).take(5)
  else symptoms
}


def missSymptoms(diseases: List[List[String]], givenSymptoms: List[String]): List[String]={
  // iterate over all diseases anf get symptoms that weren't given by the user
  // begin after head of each disease list (has the name of the disease so it doesn't matter)
  // diseases -> disease -> get each word that doesn't have the given symptoms
  diseases match
    case Nil => Nil
    case h::t=>
      // filter all words then move on to the next disease
      val missing = h.tail.filter(symp => !givenSymptoms.contains(symp)).distinct
      (missing ++ missSymptoms(t, givenSymptoms)).distinct
}


//1
def symptomsCheck(symptoms: List[String]): List[List[String]]={
  symptoms match
    case Nil => List.empty[List[String]]
    case head::tail =>
      val list = readSymptoms(db.run(symptomsTable.filter(row => checkIfSymptom(head)).distinct.result)).toList
      list.map(sympRow => List(
        sympRow.disease,
        sympRow.symptom1,
        sympRow.symptom2,
        sympRow.symptom3,
        sympRow.symptom4,
        sympRow.symptom5,
        sympRow.symptom6,
        sympRow.symptom7,
        sympRow.symptom8,
        sympRow.symptom9,
        sympRow.symptom10,
        sympRow.symptom11,
        sympRow.symptom12,
        sympRow.symptom13,
        sympRow.symptom14,
        sympRow.symptom15,
        sympRow.symptom16,
        sympRow.symptom17,
      )):::symptomsCheck(tail)


}

@tailrec
def keepFiltering(symptom: List[String], list: List[List[String]]): List[List[String]] = {
  // Nil means there's no other symptoms otherwise it'll keep filtering
  symptom match
    case Nil => list
    case head::tail =>
      // printing to see if it works
      //list.filter(row => row.contains(head)).foreach(println(_))
      //println("\n\n")
      keepFiltering(tail,list.filter(row => row.contains(head)))
}

// get the description of the disease
def getRecordsDescription(disease: String): String={
  try {
    val Description = readDescription(db.run(descriptionTable.filter(_.disease === disease).distinct.result))
    Description.head.description
  }catch {
    case _ => "Disease not found or misspelled"
  }
}

// get a list of precautions for the disease
def getRecordsPrecautions(disease: String): String={
  try {
    val Precautions = readPrecaution(db.run(precautionsTable.filter(_.disease === disease).distinct.result))
    s"- ${Precautions.head.precaution1}\n- ${Precautions.head.precaution2}\n- ${Precautions.head.precaution3}\n- ${Precautions.head.precaution4}"
  }catch {
    case _ => "No precautions found for this disease or misspelled disease name"
  }
}

