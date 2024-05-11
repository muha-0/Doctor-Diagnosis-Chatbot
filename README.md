# Doctor-Diagnosis-ChatBot
This project is a ChatBot made for Advanced Programming course for year 2, Semester 4. 
The Main gains from this project is learning and practicing the usage of a Database, GUI, and Functional Programming Methodology


# Main concepts, tools, and components used for this project
-PostgreSQL  
-Docker  
-Functional Programming Paradigm (Pattern Matching, Higher Order functions, Currying, etc...)  



## HOW TO RUN CHAT DOC ##

1- install docker  https://www.docker.com/products/docker-desktop/  
2- open docker desktop and keep it open  
3- open terminal in the project directory  
4- type docker-compose up  
5- open another terminal in the same directory  
6- type docker ps  
7- there will be a table with information about the database and copy the name
under NAMES  
8- type in terminal docker exec -it thedatabasename psql -U postgres  
9- If it's the first time using the chatbot, you need to open the main function and call fillMainTables()  
10- this should fill the database and might take a few minutes  
11- After it finishes, remove the fillMainTables() function  
12- the chatbot is ready to use!  
if you want to use it again another time, you'll need to go through steps from 2 to 8  


# Team #
Shady Ali (Database, ...)  
Nour Hany  
Ahmed Sameh  
Laila Khaled  

# Head to the Report PDF for more information about the project, contributions, and technologies used
