Name: Ido Porat
CSE user name: iporat08
id: 307905166

I pledge the highest level of ethical principles in support of academic excellence.  I ensure that all of my work reflects my own abilities and not those of someone else.

Answer for the question:
We would need to put in the pendingIntent, as its' extras, the ID of the notification and the new text we want for it.
In addition, we would need to use set the action of the pendingIntent to be something like "sms_sent".
Finally we would want to add to LocalSendSmsBroadcastReceiver another case in it's onReceive, a case of (action == "sms_sent".
In this case, the LocalSendSmsBroadcastReceiver would change the text of the notification with the ID from the pendingIntent to the text in the pendingIntent.