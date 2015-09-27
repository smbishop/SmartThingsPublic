definition(
    name: "Basic Auto Re-Lock Door",
    namespace: "Lock Auto Super Enhanced",
    author: "Arnaud",
    description: "Automatically locks a specific door after X minutes.",
    category: "Safety & Security",
    iconUrl: "http://www.gharexpert.com/mid/4142010105208.jpg",
    iconX2Url: "http://www.gharexpert.com/mid/4142010105208.jpg"
)

preferences{
    section("Select the door lock:") {
        input "lock1", "capability.lock", required: true
    }
    section("Automatically lock the door when closed...") {
        input "minutesLater", "number", title: "Delay (in minutes):", required: false
    }
    section( "Notifications" ) {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes", "No"]], required: false
			}
}

def installed(){
    initialize()
}

def updated(){
    unsubscribe()
    unschedule()
    initialize()
}

def initialize(){
    log.debug "Settings: ${settings}"
    subscribe(lock1, "lock", doorHandler, [filterEvents: false])
    subscribe(lock1, "unlock", doorHandler, [filterEvents: false])  
}

def lockDoor(){
    log.debug "Locking the door."
    lock1.lock()
    log.debug ( "Sending Push Notification..." ) 
    if ( sendPushMessage != "No" ) sendPush( "${lock1} automatically re-locked after ${minutesLater} minutes!" )
}

def doorHandler(evt){
    if (evt.value == "unlocked") { // If the door is closed and a person unlocks it then...
        def delay = (minutesLater * 60) // runIn uses seconds
        runIn( delay, lockDoor ) // ...schedule (in minutes) to lock.
    }
}
