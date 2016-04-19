# Introduction #

With GWT version 1.7 is now very easy to record/store user events (in GWT 1.7 were GWT handlers introduced) and then identify this events and replay user actions step by step. What is this good for?
  * You can much more easier reproduce user steps without any performance side effects
    * Mouse events - click, move, over, out, ...
    * Keyboard events - keydown, keypress, keyup
    * HTML events - blur, focus, scroll, context menu
  * You can see what exactly (and how long) users are doing on your GWT site or GWT application
  * You can record specific events and then play this events to the users
  * Great base for testing platform for your application - like selenium testing framework

This all features are included in acris-recorder projects.

# User event catching #

You can easily catch every user action represented as browser event using NativePreviewHandler like this:
```
NativePreviewHandler nativePreviewHandler = new NativePreviewHandler(){
   public void onPreviewNativeEvent(NativePreviewEvent event) {
        Event gwtevent = Event.as(event.getNativeEvent());
        if (MouseEvent.isCorrectEvent(gwtevent)) {
       fireListeners(new MouseEvent(gwtevent));
        } else if (KeyboardEvent.isCorrectEvent(gwtevent)) {
        fireListeners(new KeyboardEvent(gwtevent));
        } else if (HtmlEvent.isCorrectEvent(gwtevent)) {
        fireListeners(new HtmlEvent(gwtevent));
        }
   }
};
Event.addNativePreviewHandler(nativePreviewHandler);
```

# Encrypting/decrypting user events #

HTML event
  * HTML event (represents most simpliest browser event) consists from:
> > o TargetID - jednoznacny identifikator HTML elementu, ktoreho sa dany event tyka
> > o Type - typ HTML eventu:
> > > + ONBLUR (type = 0)
> > > + ONCHANGE (type = 1)
> > > + ONCONTEXTMENU (type = 2)
> > > + ONERROR (type = 3)
> > > + ONFOCUS (type = 4)
> > > + ONLOAD (type = 5)
> > > + ONSCROLL (type = 6)

![http://acris.googlecode.com/svn/wiki/images/recorder_html_event.png](http://acris.googlecode.com/svn/wiki/images/recorder_html_event.png)
![http://acris.googlecode.com/svn/wiki/images/recorder_keyboard_event.png](http://acris.googlecode.com/svn/wiki/images/recorder_keyboard_event.png)
![http://acris.googlecode.com/svn/wiki/images/recorder_mouse_event.png](http://acris.googlecode.com/svn/wiki/images/recorder_mouse_event.png)


# Generovating element ID #