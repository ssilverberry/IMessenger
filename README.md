# IMessenger
Client-server application for real-time messaging

## Description

### General:
  *	Authorization;
  *	Registration of new users;
  *	All registered users have "General chat" by default;
  *	Exchange of messages between users in chats;
  *	Create a group chat;
  *	Create a private dialog with the selected user;
  *	Add users to the group chat;
  * View additional information about users;
  *	Exit from the chat;
  * Exit from the account;

### Additionally:
#### Server:
  * The database stores:
      * User credentials;
      * User chat list;
      * User messages; 
  * Use encryption for passwords and all kind of messages.
  * Check for existing user login at registration step.
  * Also user is not able to sign in again from the same device, if he is already online.
  
#### Protocol:
  *	Data transmission over the network in Json;
  *	Encryption of transmitted data;

#### Client:
  *	User interface was made with material design;
  *	Processing of user input when registering, authorizing and creating chats;
  *	Notification of a new message in the chat, adding to the group or a private dialogue;
  *	Local storage of the chat list and history of private user chats;
  
## Build Client
  ### Maven
  To build IMessenger, run the following file:
  
    build.cmd
    
  To run IMessenger, run the following file:
  
    run.cmd
    
## Build Server
   ### Maven:
   ### Create required database tables.
   To generate all needed db tables you have to run the following file
   
    init.sql
   ### Fill the properties file with your own database credentials.
    Path to file ./resources/db.properties
    
    For example:
    user = Paul
    password = someSuperSecretCode
    dburl = jdbc:oracle:thin:@//localhost:1521/XE
  ### Build server with .bat file which located here:
    ./server/buildServer.bat
  ### Then, simply start <i>server</i> with file which located here:
    ./server/runServer.bat
   **NOTE** : You need to set JAVA_HOME environment variable to point to Java 1.8 directory.
    
   **NOTE** : IMessenger requires Java 1.8u60 and above.
## UI:
#### Main window

![main](https://github.com/YuraLampak/IMessenger/blob/master/client/src/main/resources/icons/preview/Main.PNG)

#### Authorization window

![main](https://github.com/YuraLampak/IMessenger/blob/master/client/src/main/resources/icons/preview/authorization.jpg)

#### Registration window

![main](https://github.com/YuraLampak/IMessenger/blob/master/client/src/main/resources/icons/preview/registration.jpg)
