#AliveLine
Brought to you by CAJAC  

<h2> Introduction </h2>
Aliveline is an Android productivity app that appeals to those who like to calculate the required number of hours he or she must do per day to complete an assignment by a certain deadline. The app currently calculates the number of hours needed to work per day in a linear fashion for those who want to procrastinate, get a head start and work less later, or have a balanced workload everyday. Split into four tabs (home, calendar, timer, and settings), the app allows users to quickly view different tasks (AKA Todos) and keep track of how much time they spent and need to spend to complete an assignment.


<h4>Home</h4>
<p>
The Home tab contains two Fragments, one contains a graph which displays the number of needed hours per day and the other contains a RecyclerView that displays the Todos of the day and the remaining needed time for today.
</p>

<img src="http://i1045.photobucket.com/albums/b452/Chungyuk_Takahashi/screenshot_zpsqa7wmgmc.png" height="500" />

<p>
In this tab is also the '+' button which opens up a dialog that requests the necessary information to create a new Todo.
</p>

<img src="http://i1045.photobucket.com/albums/b452/Chungyuk_Takahashi/screenshot_zpsgasm3h46.png?t=1443499179" height="500" />

<h4>Calendar</h4>
<p>
In the Calendar tab there are also two Fragments, one with a calendar view and the other with another RecyclerView that shows the information about the Todos of any selected day (selected through the calendar view on the top Fragment).
</p>
<p>
The calendar view actually consists of two Fragments, however, there is always only one showing and one hiding. One Fragment is what can be referred to as the "<b>Month View</b>", which uses the API provided by an open-source project called Material CalendarView. There are a few changes we made to its source code, however, adding features such as different size tiles (allowing for a rectangular calendar) and an OnClickListener on the TextView that contains the month of the calendar. The other Fragment is the "<b>Day View</b>" which is a RecyclerView and a custom EndlessScrollListener class. The Fragment allows for infinite scrolling both to the left and the right. 
</p>
![Demo of Aliveline Calendar Tab](http://cdn.makeagif.com/media/9-30-2015/GocbZ-.gif)

<h4>Timer</h4>
<p>
The Timer tab is more of an optional feature that helps track the amount of work one does in a day. 
</p>

![Demo of Aliveline Timer](http://cdn.makeagif.com/media/9-30-2015/ZCDAC7.gif)

<h4>Settings</h4>
<p>
The Settings allow users to change certain parts of the application.
</p>
<p>
Currently we do not have any settings, but there are ideas on possible settings such as maximum hours of work per day, the app color theme, or which calendar view will be default when the app is opened.
</>

<h3>Special Thanks<h3>
Icons by [Icons8](https://icons8.com/)
Material CalendarView (https://github.com/prolificinteractive/material-calendarview)
HoloCircularProgressBar (https://github.com/passsy/android-HoloCircularProgressBar)
MPAndroidChart (https://github.com/PhilJay/MPAndroidChart)
Stack Overflow 
