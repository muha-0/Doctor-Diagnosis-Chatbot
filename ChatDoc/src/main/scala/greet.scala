import scalafx.util.Duration
import slick.jdbc.PostgresProfile.api.*

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.util.Random
import scala.io.StdIn
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

def areDoublesEqual(a: Double, b: Double, epsilon: Double = 1e-12): Boolean = {
  (a - b).abs < epsilon
}

var firstname: String = ""
var lastname:String = ""
var Age = ""
var Gender = ""
var patient:Patient = null
val random: Random = new scala.util.Random
var rowNum:Int = -1
var rowTry2:Int = -1

def Greet(userInput:String): String = {



  //func1->no modifications
  def parseInput(input: String): List[String] = {
    def tokenize(input: String): List[String] = {
      input.toLowerCase.split(Array(',', ' ', ';', ':', '\'', '/', '"', '.', '#', '$', '%', '-', '_', '@', '!', '?', '(', ')')).toList.filter(_ != "")
    }


    //println(s"this is the tokens: ${tokenize(input)}")
    tokenize(input)
  }

  /* Naive Shady do some sort of classification (sentiment analysis) to the responses to know if they are positive, negative, neutral.
    In this context it's used to know if the user is affirming or negating the bot's responses */
  //func2->no modifications
  def naiveShady(response: String): String =
  {
    val tokens: Seq[String] = parseInput(response)

    def positiveWord(word: String): Int = {
      val query = db
      val qList: List[Boolean] = List(Await.result(query.run(positiveNounTable.filter(_.noun === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(positiveVerbTable.filter(_.verb === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(positiveAdjectiveTable.filter(_.adjective === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(positiveAdverbTable.filter(_.adverb === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(positiveWordsTable.filter(_.word === word).exists.result), concurrent.duration.Duration.Inf))

      val bool: Boolean = qList.foldLeft(false)(_ || _)

      bool match
        case false => 0
        case true => 1
    }

    def negativeWord(word: String): Int = {
      val query = db
      val qList: List[Boolean] = List(Await.result(query.run(negativeNounTable.filter(_.noun === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(negativeVerbTable.filter(_.verb === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(negativeAdjectiveTable.filter(_.adjective === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(negativeAdverbTable.filter(_.adverb === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(negativeWordsTable.filter(_.word === word).exists.result), concurrent.duration.Duration.Inf))


      val bool: Boolean = qList.foldLeft(false)(_ || _)

      bool match
        case false => 0
        case true => 1
    }

    val testp = tokens.map((word: String) => positiveWord(word))
    //println(s"positives: $testp")

    val positives: Int = testp.sum
    //println(s"positive words $positives")


    val testn = tokens.map((word: String) => negativeWord(word))
    //println(s"negatives: $testn")
    val negatives: Int = testn.sum
    //println(s"negative words $negatives")

    positives - negatives match
      case difference if (difference < 0) => "negative word"
      case difference if (difference > 0) => "positive word"
      case difference if (difference == 0) => "neutral word"
  }
  //func3
  def startPhrasePicker(listNeeded: String): String = {
    val hellos = List("Hellooooo, this a 75 IQ Chat bot, Glad to be here with you today!!", "Hey, it's me, DocBot, how can I help you today?",
      "Hello, I am a chat bot made to help you: \nIdentify your illness \nDescribe such illness if exists \nTell you how to handle it until you can get a real doctor diagnosis and prescription",
      "Salutations, esteemed interlocutor! It is with the utmost pleasure that I proffer my assistance in whatever inquiry or task may occupy your esteemed mind today.",
      "Across the shimmering expanse of the digital realm, greetings! May our conversation today blossom with knowledge and fruitful exchange.")


    val whatAreWe = List("......Btw, have we talked before??", "Firsttt... Do I, by any chance, know you?",
      "For starters, would you mind telling me if I ever had the pleasure to talk to you before this right instant?:)",
      "To best serve you, may I inquire whether we have engaged in a conversation on a prior occasion?",
      "I apologize if this seems redundant, but have we had the pleasure of interacting before?",
      "Is this our first encounter, or are you a returning friend?")

    listNeeded match
      case "say hi" => {
        phase+=1
        hellos(random.between(0, 5))
      }

      case "what are we??" => {
        phase+=1
        whatAreWe(random.between(0, 6))
      }
  } //this is where the chat starts




  //func4 -> phase 1
  def whatAreWe(): String = // asks the user if they interacted previously with the bot
  {//phase here equal 2
    val whatAreWeResponse: String = userInput
    naiveShady(whatAreWeResponse) match
      case "positive word" => {

        themOrNotGame()
      }
      case "negative word" => {
        phase = 3
        askDetails()
      }
      case "neutral word" =>
        "Well.... Why don't we try again, shall we?" //Increment 0

  }


  /*
  * Query all patient, check if exists any in the first place, if yes tell him "are you x" if yes return his patient cass class.
  * Else, try once more, if didn't work, ask him to idenitify himself to recognize him and return his patient case class
  */

  //func5->phase 2
  def themOrNotGame(): String =
  {
    //phase = 2
    val result = readPatient(db.run(patientTable.result))
    result match {
      case seq if (seq.isEmpty) =>
      {
        phase += 1 //Go to askDetails()
        "Look, I know lying is funny sometimes, but I might crash, which won't be funny this way :( \nThis is my first time interacting with a human :3\nOne thing before I ask anything serious, I won't crash btw, I was lying too ;)"
      }

      case seq => //if there's a result, then we can play a little bit before being serious
        if(rowNum == -1){rowNum = random.between(0, seq.size)}

        if (phase == 2) {
          phase+=0.1
          s"Would you, by any chance, be ${seq(rowNum).first_name} ${seq(rowNum).last_name}??"
        }
        else { //phase = 2.1 or 2.2

          val response = userInput
          naiveShady(response) match
            case "positive word"=> { //if i get them right, i now know them, i'll return their patient row
              phase=4
              if(rowTry2 != -1){
                return s"yayyy, I knew I know you from somewhere, my MEMORY is just a bit fuzzy (literally) \nWelcome back ${seq(rowTry2).first_name}!"
              }
              s"Wowwww, it's great talking to you again, ${seq(rowNum).first_name}!"
              //seq(rowNum)->Patient to be returned, increment by 0.9
            }

            case "negative word" => //if I got it wrong, i'll try once more to identify them, if wrong again, i'll straight up ask them (call askDetails())
              if (phase == 2.1) {
                phase += 0.1
                if(seq.size<2){
                  phase = 3
                  return "OK, I GIVE UP."
                }
                rowTry2 = random.between(0, seq.size)
                while(rowTry2 == rowNum){
                  rowTry2 = random.between(0, seq.size)
                }
                return s"Oops! Ok....${seq(rowNum).first_name} :p, just kidding, lemme try again\nPerhaps you're ${seq(rowTry2).first_name} ${seq(rowTry2).last_name}?? I hope I got that one right :)"

              }

              val responseTry2 = userInput
              naiveShady(responseTry2) match
                case "positive word" => {
                  phase = 4
                  s"yayyy, I knew I know you from somewhere, my MEMORY is just a bit fuzzy (literally) \nWelcome back ${seq(rowTry2).first_name}!"
                  // return that patient seq(rowTry2)
                }
                case _ =>{
                  phase =3
                  "OK, I GIVE UP."
                }


            case "neutral word" => //if I don't get a clear response, i'll need them to answer me once again to decide my next step
              "Hold up, I'm so sorry, I didn't get what you have just said \nWould you mind answering my question once again"

        }
    }
  }

  /*
  * askDetails starts questioning the user to gather his personal info for future purposes
  */

  //func6
  def askDetails(): String = //this function asks for the user details: first/last names, age, gender
  {
    if(phase == 3){
      phase += 0.1
      return "So, I would really love to continue chatting with you, but we need to get down to business. \nWould you mind answering some questions regarding your personal information?"
    }



    def filter(response: String, name: Boolean = false): List[String] = {
      val responseL = parseInput(response)

      //This function checks if word exists in any word table we have in the database, in order to filter it out
      def usefulWord(word: String): Boolean = {
        def positiveWord(word: String): Boolean = {
          val query = db
          val qList: List[Boolean] = List(Await.result(query.run(positiveNounTable.filter(_.noun === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(positiveVerbTable.filter(_.verb === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(positiveAdjectiveTable.filter(_.adjective === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(positiveAdverbTable.filter(_.adverb === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(positiveWordsTable.filter(_.word === word).exists.result), concurrent.duration.Duration.Inf))

          val bool: Boolean = qList.foldLeft(false)(_ || _)

          bool
        }

        def negativeWord(word: String): Boolean = {
          val query = db
          val qList: List[Boolean] = List(Await.result(query.run(negativeNounTable.filter(_.noun === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(negativeVerbTable.filter(_.verb === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(negativeAdjectiveTable.filter(_.adjective === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(negativeAdverbTable.filter(_.adverb === word).exists.result), concurrent.duration.Duration.Inf), Await.result(query.run(negativeWordsTable.filter(_.word === word).exists.result), concurrent.duration.Duration.Inf))


          val bool: Boolean = qList.foldLeft(false)(_ || _)
          bool
        }

        positiveWord(word) || negativeWord(word)
      }

      val usefulfiltered = responseL.filter(usefulWord)

      val listMiscallunuous: List[String] = List("i", "am", "is", "are", "his", "her", "they", "he", "she", "it", "name", "age", "year", "years", "old", "iam", "my", "this", "their", "those")

      val filtered = usefulfiltered.filter((word: String) => !listMiscallunuous.contains(word)) //if the list contains anything like "i am, he, she, etc.." it's useless


      name match  //if a name is needed, no name is less than 3 letters
        case true => filtered.filter(_.length >= 3)
        case _ => filtered
    }


    //phase 3.1->3.2
    def firstName(): String = {
      if(phase == 3.1){
        phase += 0.1
        return "May I ask first about your first name??"
      }

      val nameResponse = userInput

      val filtered = filter(nameResponse, name = true)
      filtered match
        case Nil =>
          "I guess we need to try again, could you rephrase what you just said?"


        case head :: tail if(tail.isEmpty) => {
          firstname = head
          phase+=0.1
          s"Great nice to meet you! $head"
        }

        case _ =>
          "I guess we need to try again, could you rephrase what you just said?"

    }
    //func8
    //phase 3.3->3.4
    def lastName(): String = {
      if(phase == 3.3){
        phase+=0.1
        return "And your last name??"
      }

      val nameResponse = userInput

      val filtered = filter(nameResponse, name = true)
      filtered match
        case Nil =>
          "I guess we need to try again, could you rephrase what you just said?"


        case head :: tail if (tail.isEmpty) => {
          lastname = head
          phase+=0.1
          return s"Great nice to meet you! $firstname $head"
        }
        case _ =>
          "I guess we need to try again, could you rephrase what you just said?"

    }
    //func8 phase 3.5->3.6

    def age(): String = {
      if(phase == 3.5){
        phase+=0.1
        return "What about your age??"
      }

      val nameResponse = userInput

      val filtered = filter(nameResponse)
      filtered match
        case Nil =>
          "I guess we need to try again, could you rephrase what you just said?"


        case head :: tail if (tail.isEmpty) =>{
          Age = head
          phase+=0.1
          s"Great"

        }


        case _ =>
          "I guess we need to try again, could you rephrase what you just said?"

    }
    //func9 phase 3.7->3.8

    def gender(): String = {
      if(phase == 3.7){
        phase += 0.1
        return "Finally, what's your gender?"
      }

      val nameResponse = userInput

      val filtered = filter(nameResponse)
      filtered match
        case Nil =>
          "I guess we need to try again, could you rephrase what you just said?"


        case head :: tail if (tail.isEmpty) => {
          Gender = head
          phase+=0.1
          s"Great!! that's all for your personal details!"

        }


        case _ =>
          "I guess we need to try again, could you rephrase what you just said?"

    }
    if(phase == 3.1 || phase == 3.2){
      firstName()
    }
    else if(phase == 3.3 || phase== 3.4){
      lastName()
    }
    else if(phase == 3.5 || phase == 3.6){
      age()
    }
    else if(phase == 3.7 || phase == 3.8){
      gender()
    }
    else{
      patient = Patient(patient_id = 1L, firstname, lastname, Age, Gender)
      val existingPatient: Seq[Patient] = readPatient(db.run(patientTable.filter(_.first_name === firstname).filter(_.last_name === lastname).filter(_.age === Age).result))
      phase+=0.1
      existingPatient match
        case result if (result.nonEmpty) =>

          s"Welcome back! ${result.head.first_name} ${result.head.last_name}, ${validReply(Random.nextInt(validReply.length))}"
        //return patient -> result.head

        case _ =>
          s"Welcome! $firstname $lastname, ${validReply(Random.nextInt(validReply.length))}"
      //return patient -> newPatient(patient) //A new patient will be added to the table
    }


  }


  if (phase == 0) {
    startPhrasePicker("say hi")
  }
  else if (phase== 1) {
    startPhrasePicker("what are we??")
  }
  else if (phase == 2) {
    whatAreWe()
  }
  else if (phase == 2.1 || phase == 2.2) {
    themOrNotGame()
  }
  else if (phase.toInt == 3) {
    askDetails()
  }
  else{
    "error"
  }

}