import slick.jdbc.PostgresProfile.api.*

import scala.util.Random
import scala.io.StdIn
import scala.util.{Failure, Success, Try}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration



//Sets of words that the chatbot will consider, understand, reply to :)
val greetingSet: List[String] =List("hi","hey","hello","yo", "hiya", "greetings", "salutations", "aloha", "sup", "howdy","good morning","good afternoon","good evening")
val appreciatSet: List[String] =List("thank you","thank","thanks","thankful","grateful","appreciate")
val loveSet: Seq[String] =List("love you","love")
val unsatisfactSet: List[String] =List("hate","disappointed","unsatisfied","not satisfied")
val exitSet: List[String]= List("bye","goodbye","see you later","talk to you later","good night","nightie")
val commonWords: List[String] = List(
  "a", "an", "as", "at", "be", "by", "do", "go", "he", "if", "in", "is", "it","me", "my", "no", "of", "on", "or", "so", "to", "up", "us", "we", "ye", "for",
  "and", "the", "but", "not", "you", "all", "any", "are", "can", "had", "has", "her","him", "his", "how", "man", "new", "now", "old", "one", "our", "out", "own", "put",
  "say", "see", "she", "two", "way", "who", "why", "yes", "yet", "you","also", "back", "best", "both", "call", "come", "each", "even", "find", "from",
  "give", "good", "have", "here", "just", "know", "like", "look", "make", "most","must", "name", "need", "only", "over", "part", "same", "show", "such", "take",
  "that", "them", "then", "this", "upon", "want", "well", "when", "will", "with","work", "your","any", "arm", "ask", "bag", "bed", "big", "bug", "buy", "car", "cat", "day",
  "dog", "ear", "egg", "end", "fan", "far", "fat", "fix", "fly", "fun", "get","got", "gum", "guy", "hat", "hit", "hop", "hot", "ice", "jet", "joy", "key",
  "leg", "let", "log", "map", "mix", "mom", "net", "nut", "off", "oil", "out", "pen", "pig", "pop", "red", "run", "am", "i","pain"
)
val phraseSet: List[String]= List("good morning","good afternoon","good evening","thank you","love you","not satisfied","see you")

//set of replies the bot will randomly choose from and use
val appReplies: List[String] = List(
  "You're very welcome! ",
  "It's my pleasure! ",
  "No problem, happy to help! ",
  "You're welcome, anytime! ",
  "It was nothing, glad to assist! ",
  "Don't mention it, I'm here to help! ",
  "Anytime, feel free to ask! ",
  "You bet, always happy to lend a hand! ",
  "No worries, glad I could be of assistance! ",
  "My pleasure, let me know if you need anything else! "
)
val loveReplies: List[String] = List (
  "Me too! ",
  "Love you too! ",
  "Aww, love you! ",
  "You're the best, love you! ",
  "I love you more! ",
  "Love you bunches! ",
)
val unsatReplies: List[String] = List(
  "My apologies if you're not satisfied. ",
  "I apologize for any inconvenience caused. ",
  "I'm sorry things didn't meet your expectations. ",
  "Please accept my apologies for any disappointment. ",
  "I'm sorry to hear you're not completely satisfied. ",
  "I apologize if our service fell short of your expectations. ",
  "I'm sorry for any frustration this may have caused. ",
  "Please forgive us if you're not entirely happy. ",
  "I apologize for any inconvenience this has caused you. "
)
val invalidReply: List[String] = List(
  "I'm sorry, I didn't quite catch that. Could you please provide more context related to your health issues?",
  "Apologies, I'm having trouble understanding your question. Can you try elaborating a bit more your health concerns?",
  "It seems like I'm having difficulty understanding your request. Could you please clarify your health concern?",
  "I'm sorry, I'm not sure I understand. Can you provide more details about your health condition?",
  "Hmm, I'm having trouble processing your question. Can you try giving more information about you health condition?",
  "I'm having difficulty grasping your inquiry. Could you elaborate a bit more your health issues?",
  "I'm sorry, I'm unable to comprehend your question. Could you please clarify your health concern?",
  "It appears I'm not understanding your inquiry properly. Could you please clarify your health concern?"
)
val validReply: List[String] = List(
  "Ready to help with any medical questions you have. ",//Are you experiencing any health issues?",
  "I'm your medical assistant. ",//Do you need assistance with a health concern?",
  "I'm here to provide medical guidance. ",//Have a health question you'd like to discuss?",
  "As ChatDoc, I'm here to support you with any medical inquiries. ",//What health matter can I assist you with today?",
  "As ChatDoc, I'm available to address your medical concerns. "//Are you facing any health issues you'd like to discuss?"
)

sealed trait Word
case class Symptom(txt: String) extends Word
case class Disease(txt: String) extends Word
case class Appreciat(txt: String) extends Word
case class Love(txt: String) extends Word
case class Unsatisfact(txt: String) extends Word
case class Greeting(txt: String) extends Word
//case class Exit(txt: String) extends Word

//handling punctuation (not all of them tho, but the most common) and splitting
def tokenize(txt :String):List[String]=
{
  val txt2=txt.toLowerCase.replaceAll("n't", " not").replaceAll("'m", " am").replaceAll("'s", " is").replaceAll("'re'", "are")
  txt2.replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+").toList
}

def checkIfSymptom(symtom: String): Boolean = {
  def symptomDetection(p: SymptomsTable)(symptom: String): Rep[Boolean] = {
    p.symptom1.like(s"%$symptom%") ||
      p.symptom2.like(s"%$symptom%") ||
      p.symptom3.like(s"%$symptom%") ||
      p.symptom4.like(s"%$symptom%") ||
      p.symptom5.like(s"%$symptom%") ||
      p.symptom6.like(s"%$symptom%") ||
      p.symptom7.like(s"%$symptom%") ||
      p.symptom8.like(s"%$symptom%") ||
      p.symptom9.like(s"%$symptom%") ||
      p.symptom10.like(s"%$symptom%") ||
      p.symptom11.like(s"%$symptom%") ||
      p.symptom12.like(s"%$symptom%") ||
      p.symptom13.like(s"%$symptom%") ||
      p.symptom14.like(s"%$symptom%") ||
      p.symptom15.like(s"%$symptom%") ||
      p.symptom16.like(s"%$symptom%") ||
      p.symptom17.like(s"%$symptom%") ||
      p.symptom18.like(s"%$symptom%")
  }

  val future = db.run(symptomsTable.filter(symptomDetection(_)(symtom)).exists.result)
  val bool: Boolean = Await.result(future, Duration.Inf)
  bool
}

def handlephrases(phrases: List[String])(tokens: List[String]):List[String]=
{
  tokens match{
    case Nil => Nil
    case h::Nil => h::Nil
    case h1::h2::t if(phrases.contains(h1+" "+h2))=> h1+" "+h2::handlephrases(phrases)(t)
    case h1::h2::t => h1::handlephrases(phrases)(h2::t)
  }
}
val handlephrases2=handlephrases(phraseSet)
//labeling words and dropping the ones that are not labeled
//vulnerabilities: some phrases hold their meaning in being a phrase (got solved by handlephrases :))))
//i.e: love,hate it can be pointed to any one/thing but this code ain't enough
def label(lst :List[String], words: List[Word]=Nil): List[Word] = {
  lst match{
    case Nil=>words
    case h::t if commonWords.contains(h) => label(t,words)
    //case h::t if exitSet.contains(h) => label(t,words:+Exit(h))
    case h::t if greetingSet.contains(h) => label(t,words:+Greeting(h))
    case h::t if loveSet.contains(h) => label(t,words:+Love(h))
    case h::t if appreciatSet.contains(h) => label(t, words :+ Appreciat(h))
    case h::t if unsatisfactSet.contains(h) => label(t, words :+ Unsatisfact(h))
    case h::t if checkIfSymptom(h) => label(t,words:+Symptom(h))
    case _::t => label(t,words)
  }
}

def reply(lst: List[Word], sympt:List[String]=Nil, prompt: List[String]=Nil): Map[String, List[String]] = {
  lst match{
    case Nil if (sympt==Nil && prompt == Nil)=> Map("Prompts"->(prompt:+invalidReply(Random.nextInt(invalidReply.length))),"Symptoms"->sympt)//invalid
    case Nil if (sympt==Nil)=>Map("Prompts"->(prompt:+validReply(Random.nextInt(validReply.length))),"Symptoms"->sympt)//ask for his inquiry
    case Nil => Map("Prompts"->prompt,"Symptoms"->sympt)
    case h::t =>
      h match {
        case Symptom(txt)=>
          reply(t,sympt:+txt,prompt)
        case Appreciat(txt)=>
          reply(t,sympt,prompt:+appReplies(Random.nextInt(appReplies.length)))
        case Love(txt)=>
          reply(t, sympt, prompt :+ loveReplies(Random.nextInt(loveReplies.length)))
        case Unsatisfact(txt)=>
          reply(t, sympt, prompt :+ unsatReplies(Random.nextInt(unsatReplies.length)))
        case Greeting(txt)=>
          reply(t,sympt,prompt :+(txt+", "+firstname))
      }
  }
}

@main def hi(): Unit={

  println(reply(label(handlephrases2(tokenize("headache")))))
}