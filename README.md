Basic Elder Scrolls calendar for fabric 1.20.1. Not tested under any other environments, but doesn't really make use of any fabric-specific things outside of event bindings, so it could easily be ported to other platforms. It is intended to entirely run on the server.

Players get a message when they join of the current date, and during the day they are also sent a message with the new day. I'm not entirely sure why, but the actual firing of the message seems to happen randomly during the day.

Basic assumptions made:
* A year is 365 days and has no leap years
* A day is 24000 ticks
* The first day of the year is the first day of spring, not January first (every Season mod starts the game on Spring 1 by default, and it is traditional for Harvest Moon type games as well)
* An era is 1000 years, not a time period as defined by the fantasy government (potential improvement would be track world creations and treat these as eras)
  
