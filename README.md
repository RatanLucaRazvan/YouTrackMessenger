YouTrack-Telegram Integration
=================================
The program fetches notifications periodically from YouTrack and uses a connection with a Telegram bot to represent those notifications as real time text messages
    

Prerequisites
-------------

To build and run this project, the followings are needed:

*   **Java Development Kit (JDK):** Version 11 or newer.
    
*   **Apache Maven:** To manage dependencies and build the project.
    

Configuration
----------------

Before running the application, you must provide six credentials. All configuration is done in the src/main/java/com/jetbrains/intern/Main.java file.

### Finding Your Credentials:

1.  **YOUTRACK\_URL**: The full URL of your YouTrack instance (e.g., https://my-team.youtrack.cloud).
    
2.  **YOUTRACK\_TOKEN**:
    
    *   In YouTrack, click your profile picture → **Profile** → **Account Security**
        
    *   Click **New token** and give it a name and the "YouTrack" scope.
        
    *   Copy the entire token. **Important:** The token string must start with perm:.
        
3.  **YOUTRACK\_PROJECT\_ID**:
    
    *   This is the **internal system ID**.
        
    *   To find this, I created a function to query the API at the endpoint /api/admin/projects.
        
4.  **BOT\_TOKEN**:
    
    *   In Telegram, start a chat with the user **@BotFather**.
        
    *   Send the /newbot command and follow the instructions.
        
    *   BotFather will give a unique token to access the API.
        
5.  **BOT\_USERNAME**: The chosen username for the bot when creating it with BotFather
    
6.  **TARGET\_CHAT\_ID**:
    
    *   This is the chat where the bot will send notifications from Part 1.
        
    *   To find the personal Chat ID, start a chat with the user **@userinfobot** and it will reply with the ID.
        

How to Run
-------------

1.  Close the repository
    
2.  Open the project in IntelliJ

3. Build and Run the project  
    

Usage
--------

### Receiving Notifications

Once the application is running, simply perform actions in your YouTrack project (create issues, change their state, add comments). Within 30 seconds, a formatted notification will automatically appear in the Telegram chat specified by TARGET\_CHAT\_ID.

### Creating an Issue

In any chat with your Telegram bot, send the following command: \create [ISSUE_NAME]

The bot will reply with a confirmation message, including the new issue's ID
