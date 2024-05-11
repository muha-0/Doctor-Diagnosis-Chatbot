import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{TextField, Button}
import scalafx.scene.layout.{HBox, BorderPane, VBox}
import scalafx.geometry.{Insets, Pos}
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.{KeyEvent, KeyCode, MouseEvent}
import scalafx.application.Platform
import scalafx.animation.{PauseTransition, FadeTransition, Timeline, KeyFrame}
import scalafx.util.Duration
import scalafx.scene.text.{Text, TextFlow, FontWeight}
import scalafx.scene.layout.{Region, Priority, StackPane}
import scalafx.scene.control.ScrollPane

var phase:Double = 0

object ChatbotGUI extends JFXApp3 {

  override def start(): Unit = {

    val splashScreen = new VBox {
      val splashImage = new ImageView {
        image = new Image(getClass.getResourceAsStream("/Chat_Doc_logo6.png"))
        preserveRatio = true
        fitHeight = 500
      }
      children = splashImage
      style = "-fx-background-color: #f8f8f8; -fx-alignment: center;"
      padding = Insets(20)
      opacity = 0
    }

    val fadeIn = new FadeTransition {
      duration = Duration(2000)
      node = splashScreen
      fromValue = 0
      toValue = 1
      onFinished = _ => {
        val delay = new PauseTransition(Duration(3000))
        delay.onFinished = _ => stage.scene = createMainScene()

        delay.play()
      }
    }

    val splashScene = new Scene(splashScreen,1200,1000)

    stage = new PrimaryStage {

      title = "ChatDoc"
      scene = splashScene

      fadeIn.play()
    }
  }

  def createMainScene(): Scene = new Scene(1200,1000) {

    val chatFlow = new VBox {
      prefWidth = 600
      prefHeight = 700
      spacing = 10
      style = "-fx-background-color: #f8f8f8; -fx-padding: 10; -fx-border-color: #888888; -fx-border-radius: 5;"
    }
    val scrollPane = new ScrollPane {
      fitToWidth = true // Ensures the content width matches the scroll pane width
      content = chatFlow
      style = "-fx-background-color: #f8f8f8;"
    }
    lazy val textField: TextField = new TextField {
      promptText = "Ask Chat Doc..."
      prefWidth = 300
      style = "-fx-font-size: 18px; -fx-background-color: #f8f8f8; -fx-text-fill: #444444; -fx-prompt-text-fill: #565656;"
      onKeyPressed = (ke: KeyEvent) => if (ke.code == KeyCode.Enter) sendButton.fire()
    }

    val sendIcon = new ImageView(new Image(getClass.getResourceAsStream("/button.png")))
    sendIcon.fitWidth = 30
    sendIcon.fitHeight = 30
    val loadingIcon = new ImageView(new Image(getClass.getResourceAsStream("/loading_icon.png")))
    sendIcon.fitWidth = 30
    sendIcon.fitHeight = 30
    loadingIcon.fitWidth = 30
    loadingIcon.fitHeight = 30

    lazy val sendButton: Button = new Button {
      margin = Insets(0, 5, 0, 0)
      graphic = sendIcon
      style = "-fx-background-color: #f8f8f8; -fx-background-radius: 5px; -fx-border-radius: 5px;"
      minWidth = 20
      maxWidth = 20
      minHeight = 20
      maxHeight = 20
      var response = ""

      onAction = (_: ActionEvent) => {
        phase = f"$phase%.1f".toDouble
        if (textField.text.value.toLowerCase == "bye") {
          response = "Bye! Glad to help you"
        }
        else if(phase<4){
          response = Greet(textField.text.value)
        }
        else{
          if(phase >= 4.4){
            phase = 4.1
          }
          response = doctor(textField.text.value)
        }
        phase = f"$phase%.1f".toDouble
        println(phase)
        if (textField.text.value.toLowerCase == "bye") {
          scheduleShutdown()
        }
        simulateBotResponse(response)

      }
      
    }

    def simulateBotResponse(response: String): Unit = {
      // Immediate UI updates
      Platform.runLater {
        textField.editable = false
        sendButton.disable = true
        sendButton.graphic = loadingIcon
        textField.clear()
        chatPane.requestFocus()
        scrollPane.vvalue = scrollPane.vmax.value
      }
      val chatBotIcon = new ImageView(new Image(getClass.getResourceAsStream("/Chat_Doc_logo7.png")))
      chatBotIcon.fitWidth = 40
      chatBotIcon.fitHeight = 40
      val userIcon = new ImageView(new Image(getClass.getResourceAsStream("/user_icon.png"))) // Adjust the path to your actual user icon image
      userIcon.fitWidth = 40
      userIcon.fitHeight = 40

      val userTextFlow = new TextFlow {
        style = "-fx-font-weight: normal; -fx-font-size: 22px; -fx-background-color: #d1ecf1; -fx-padding: 5; -fx-background-radius: 10;"
        children = Seq(new Text(s"${textField.text.value}\n\n"))
      }
      val userTextPane = new HBox {
        spacing = 5
        alignment = Pos.CenterRight // Keeps content aligned to the right
        children = Seq(new TextFlow {
          style = "-fx-font-weight: normal; -fx-font-size: 22px; -fx-background-color: #d1ecf1; -fx-padding: 5; -fx-background-radius: 10;"
          children = Seq(new Text(s"${textField.text.value}\n\n"))
        }, userIcon) // Adding the icon after the text
      }

      chatFlow.children.add(userTextPane)

      val botTextFlow = new TextFlow {
        style = "-fx-font-weight: normal; -fx-font-size: 22px; -fx-background-color: #d1ecf1; -fx-padding: 5; -fx-background-radius: 10;"
      }

      val botTextPane = new HBox {
        spacing = 5
        alignment = Pos.CenterLeft
        children = Seq(chatBotIcon, botTextFlow)
      }

      // Add bot response pane to chat flow initially empty
      chatFlow.children.add(botTextPane)
      val typingDelay = if (response.length > 300) 10 else 50
      var currentIndex = 0
      val timeline = new Timeline {
        cycleCount = Timeline.Indefinite
        keyFrames = KeyFrame(Duration(typingDelay), onFinished = _ => {
          if (currentIndex < response.length) {
            Platform.runLater(() => {
              botTextFlow.children.add(new Text(response.charAt(currentIndex).toString))
              currentIndex += 1
            })
          } else {
            Platform.runLater {
              textField.editable = true
              sendButton.disable = false
              sendButton.graphic = sendIcon
              textField.requestFocus() // Focus back to the text field for the next input
              textField.clear()
            }
            stop() // Stop the timeline
          }
        })
      }
      timeline.play()
      scrollPane.vvalue = scrollPane.vmax.value
    }

    def scheduleShutdown(): Unit = {
      val shutdownDelay = new PauseTransition(Duration(3500)) // Delay for 3.5 seconds
      shutdownDelay.onFinished = _ => {
        Platform.exit()
        System.exit(0)
      }
      shutdownDelay.play()
    }
    val spacer = new Region()
    HBox.setHgrow(spacer, Priority.Always)

    val bottomPanel = new HBox(10) {
      alignment = Pos.Center
      margin = Insets(10, 0, 5, 0)
      padding = Insets(10)
      style = "-fx-background-color: #f8f8f8; -fx-border-color: #888888; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;"
      children = Seq(textField, spacer, sendButton)
    }
    textField.focused.onChange { (_, _, isFocused) =>
      updateBottomPanelStyle(isFocused || sendButton.focused.value)
    }
    sendButton.focused.onChange { (_, _, isFocused) =>
      updateBottomPanelStyle(textField.focused.value || isFocused)
    }

    def updateBottomPanelStyle(isFocused: Boolean): Unit = {
      bottomPanel.style = if (isFocused) {
        "-fx-background-color: #f8f8f8; -fx-border-color: #00BFFF; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;"
      } else {
        "-fx-background-color: #f8f8f8; -fx-border-color: #444444; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;"
      }
    }

    val chatPane = new BorderPane {
      padding = Insets(20)
      style = "-fx-background-color: #f8f8f8;"
      center = scrollPane
      bottom = bottomPanel
    }

    root = chatPane
    stylesheets += getClass.getResource("/styles.css").toExternalForm
    chatPane.onMouseClicked = (event: MouseEvent) => {
      if (!event.target.isInstanceOf[TextField]) {
        chatPane.requestFocus() // Requests focus on the chatPane to defocus the TextField
      }
    }
  }
}
