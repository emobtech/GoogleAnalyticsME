# Google Analytics ME ([[#Donate|Support us!]])

'''Google Analytics ME''' is a compelling and well defined API for Java ME developers who wish to integrate their apps into Google Analytics. With this API, developers will be able to prepare their apps to send out useful data, about how users are interacting with them. Those data will be valuable to identify, e.g., audience and improvement points.

====<span id="tablecont">Table of Contents:</span>====
#[[#Licensing|Licensing]]
#[[#MinReqs|Minimum Requirements]]
#[[#UpdateHistory|Update History]]
#[[#SampleCodes|Sample Codes]]
#[[#Donate|Donation]]
#[[#SeeAlso|See Also]]
#[[#References|References]]
#[[#ExtLinks|External Links]]

= <span id="Licensing">Licensing</span> =
Google Analytics ME is under two licenses: '''[http://en.wikipedia.org/wiki/GNU_General_Public_License GNU General Public License v2.0]''' regarding the source code and '''[http://en.wikipedia.org/wiki/GNU_Lesser_General_Public_License GNU Lesser General Public License v3.0]''' for the binaries. It means that now you can develop proprietary applications with Google Analytics ME' if you merely link them to API's binaries.

[[#tablecont|(back to top)]]

= <span id="MinReqs">Minimum Requirements</span> =

*Java Micro Edition (MIDP 2.0 / CLDC 1.0) or newer
*Internet connection (e.g. GPRS, EDGE, 3G, wi-fi, etc)
*Google Analytics account*

<nowiki>*</nowiki>By creating an account on Google Analytics, make sure you select '''"Web Site"''', when asked what you would like to track. "App" will not work for this API.

[[#tablecont|(back to top)]]

= <span id="UpdateHistory">Update History</span> =
Google Analytics ME is now at its fifth release ('''2.1'''). This last version consists of bug fixes and new features.

{|- border="1"
!Version
!Date
!Contents
|-
|<center>2.1</center>
|<center>06/07/2012</center>
|<ul><li>Fixed issue that was not identifing properly unique visitors.</li><li>Improved session counter and visit duration calculation.</li><li>Assign a custom user agent throught the JAD property "GAME-Custom-UserAgent".</li></ul>
|-
|<center>2.0</center>
|<center>01/02/2012</center>
|<ul><li>Identification of unique and returning visitors.</li><li>Better visitor's features identification.</li><li>Small code refactoring.</li><li>Android support deprecated.</li></ul>
|-
|<center>1.2</center>
|<center>03/11/2011</center>
|<ul><li>Android support</li></ul>
|-
|<center>1.1</center>
|<center>01/13/2011</center>
|<ul><li>A bug fix related to visitor identification.</li></ul>
|-
|<center>1.0</center>
|<center>12/27/2010</center>
|<ul><li>Page View and Event tracking</li><li>Synchronous and Asynchronous communication</li></ul>
|}

[[#tablecont|(back to top)]]

= <span id="SampleCodes">Sample Codes</span> =
In order to help you to quick learn how to work with Google Analytics ME, here it goes some sample codes showing how to perform some common tasks.

'''- Tracking how many times a MIDlet is started up:'''
<pre name="java">
public class MediaPlayerMIDlet extends MIDlet {

  public AppMIDlet() {
    ...
    Tracker tracker = Tracker.getInstance(this, "UA-1736743-0");
    tracker.addToQueue(new PageView("/midlet_started"));
    ...
  }
  ...
}
</pre>

'''- Tracking how many times a screen is displayed:'''
<pre name="java">
public class MediaPlayerScreen extends Form {
  ...
  public void commandAction(Command c, Displayable d) {
    if (c == cmdSongDetail) {
      Tracker tracker = Tracker.getInstance(midlet, "UA-1736743-0");
      tracker.addToQueue(new PageView("/song_detail"));
      //
      display.setCurrent(detailScreen);
    }
    ...
  }
  ...
}
</pre>

'''- Tracking how many times a function of a screen is performed:'''
<pre name="java">
...
public void play() {
  Tracker tracker = Tracker.getInstance(midlet, "UA-1736743-0");
  tracker.addToQueue(new Event("/media_player", "Play", null, null));
  ...
}
...
</pre>

'''- Tracking events synchronously:'''
<pre name="java">
...
public void rewind() {
  Tracker tracker = Tracker.getInstance(midlet, "UA-1736743-0");
  tracker.track(new Event("/media_player", "Rewind", null, null));
  ...
}
...
</pre>

'''- Explicitly *flushing all queued events:'''
<pre name="java">
public class MediaPlayerMIDlet extends MIDlet {
  ...
  public void destroyApp(boolean unconditional) {
    Tracker tracker = Tracker.getInstance(this, "UA-1736743-0");
    tracker.flush(false);
  }
}
</pre>

'''*Make to sure to call this method (synchronously) before the app is destroyed. Otherwise, any queued requests will be lost.'''

'''- Customize User Agent in JAD file:'''
<pre name="java">

MicroEdition-Configuration: CLDC-1.0
MicroEdition-Profile: MIDP-2.0
...
GAME-Custom-UserAgent: NokiaN90-1/3.0545.5.1 Series60/2.8 Profile/MIDP-2.0 Configuration/CLDC-1.1
...
</pre>

[[#tablecont|(back to top)]]

= <span id="Donate">Donation</span> =
In case of Google Analytics ME has brought good benefits for you and/or your company, and because of that you would like to thank us for all our hard work. Please, feel free to donate us any amount, via PayPal, by clicking '''[https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=ernandes%40gmail%2ecom&lc=US&item_name=Google%20Analytics%20ME&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest here]'''. It is easy and quick to do. In addition, this contribution will provide us more resources to keep up improving this API for you.

[[#tablecont|(back to top)]]

= <span id="SeeAlso">See Also</span> =
* [http://code.google.com/mobile/analytics/docs/ Google Analytics for Mobile]

= <span id="References">References</span> =
* [http://code.google.com/mobile/analytics/docs/web/ Google Analytics for Mobile Websites]
* [http://code.google.com/apis/analytics/docs/tracking/gaTrackingTroubleshooting.html Troubleshooting the Tracking Code]

= <span id="ExtLinks">External Links</span> =
* [http://www.google.com/analytics Google Analytics]
* [http://j2megroup.blogspot.com J2ME Group Blog]
* [http://www.emobtech.com eMob Tech]
