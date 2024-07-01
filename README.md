# Doctor-Diagnosis-ChatBot
This project is a ChatBot made for Advanced Programming course for year 2, Semester 4. 
The Main gains from this project is learning and practicing the usage of a Database, GUI, and Functional Programming Methodology


# Main concepts, tools, and components used for this project
-*Long Short Term Memory Neural Network*
-*PostgreSQL (Database)*
-*Docker*
-*Functional Programming Paradigm (Pattern Matching, Higher Order functions, Currying, etc...)*



## HOW TO RUN CHAT DOC ##

1- Install docker  https://www.docker.com/products/docker-desktop/  
2- Open docker desktop and keep it open  
3- Open terminal in the project directory  
4- Type docker-compose up  
5- Open another terminal in the same directory  
6- Type *docker ps*  
7- There will be a table with information about the database and copy the name
under *NAMES*  
8- Type in terminal **docker exec -it *thedatabasename* psql -U postgres**  
9- If it's the first time using the chatbot, you need to open the main function and call fillMainTables()  
10- This should fill the database and might take a few minutes  
11- After it finishes, remove the fillMainTables() function  
12- The chatbot is ready to use!  
**If you want to use it again another time and you closed docker, you'll need to go through steps from 2 to 8.**
**If you haven't closed Docker yet, you can run the chatbot directly without any steps before *12***


# Team #
Shady Ali (Database Layer, LSTM Deep Learning Model for Sentiment Analysis)  
Nour Hany  
Ahmed Sameh  
Laila Khaled  

# Head to the Report PDF for more information about the project, contributions, and technologies used
