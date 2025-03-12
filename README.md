# **Tic Tac Toe**

<br>

### README

<h3 align="left">Languages :</h3>
<p align="left"> <a href="https://www.java.com" target="_blank" rel="noreferrer"> <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="java" width="40" height="40"/> </a> </p>

<h3 align="left">How the project works :</h3>
<h4 align="justify">
This Java project is a simple tic-tac-toe game with the standard rules. The first player to have three squares vertically, horizontally or diagonally wins. The squares are placed alternately. The game is played against the computer, and the longer you play against the computer, the better it gets, which increases the difficulty. To continue playing, you log in to your existing account. The account data is stored in a database, whereby the password and security question are hashed. The database runs on PostgreSQL. The individual tables are created with Liquibase as soon as the programme is started. Each individual move in a match and the matches themselves are also stored in the database. There is no game history, as this runs via the question/answer function of the API. The web application that works with the API is coming soon ...

</h4>
<br>

<h3 align="left">the reason for the project :</h3>
<h4 align="justify">
this is a programming learning project, but of course you can also use it to play tiktaktoe

<h3 align="left">how to run:</h3>

<h4 align="justify">
It can be executed in development environments. Playing is currently only possible via cmb commands or Bruno as you have to communicate with the API to be able to play but the user-friendly web application is comingz.

</h4>
<h3 align="left">self notes</h3>

<h4 align="justify">
With the help of the pmd Ruls plugin in Maven, the code was reworked a little. The codestyle
was improved and design and error sources removed. The corresponding rules were
added to the pmd-rules.xml file piece by piece.

To check if everything works as it should, there is unit 5 test. You have to have Docker or something and a container for the database running that is not yet built into the code.

</h4>

<p align="left">
</p>
