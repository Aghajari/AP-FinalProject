# AP-FinalProject
 A messaging social platform based on Discord for Desktop and Android.<br>
This is my final project for Advanced-Programming (AP) in Amirkabir University of Technology (AUT).<br>

![](./images/1.png)

**Technologies Used:**<br>
- **JavaFX** for Desktop GUI
- **Java** for Android
- **Java** for Socket And ServerSocket
- **Node.js** for Rest API
- **PHP** for Sample Bots
- **MongoDB** And **MySQL** for Database

**Libraries Used:**<br>
- [XmlByPass](https://github.com/Aghajari/XmlByPass)
- [AXAnimation](https://github.com/Aghajari/AXAnimation)
- [AXrLottie](https://github.com/Aghajari/AXrLottie)
- [Retrofit](https://github.com/square/retrofit)
- [Gson](https://github.com/google/gson)
- [ZXing](https://github.com/zxing/zxing)
- [MaterialFX](https://github.com/palexdev/MaterialFX)
- [Mongoose](https://github.com/Automattic/mongoose)
- [jsonwebtoken](https://github.com/auth0/node-jsonwebtoken)

## Chat Page
These are some screen shots of PrivateMessage and Server's GroupChat.<br>
Reactions are lottie animations from telegram emoji set.

![](./images/2.png)<br>
![](./images/6.png)

## Voice Call
Voice call over socket tcp connection (i'll replace it with VoIP soon...)

![](./images/8.png)

## Friendship
Send friendship request, block your enemies :D, Chat with your online friends.

![](./images/3.png)

## Servers
Join a server if you have an invite code, <br>
Create a server and design channels & groups then invite your friends to start a party!<br>
Manage permissions of each member in your server.

![](./images/4.png)

## Login By QRCode
You can login or signup in normal way in both desktop and android <br>
You can also link your authorized android account to the desktop by scanning a QRCode!

![](./images/5.png)

## Bot
Connect your account to a bot. (Only for developers) <br>
A bot is only an HTTP api link, You can develop the api in all programming languages.

![](./images/7.png)

![](./images/9.png)

This one is a "Hello World!" bot code written in PHP.<br>
```PHP
<?php
header("Content-Type: application/json");

$data = json_decode(file_get_contents('php://input'));

echo json_encode([
    "to"=>$data->chatId, 
    "text"=>"Hello World!"
]);

?>
```

[See more...](./Bot)

<details><summary><b>Bot Api Document</b></summary>
<p>

|Field|Type|Description|
| :---------------- | :----------------: | :---------------- |
| chatId | String | Unique identifier for this chat |
| from | User | Sender of the message |
| to | User | Receiver of the message, if receiver is a user |
| server | Server |  Server details, if receiver is channel of a server |
| channel | Server.Channel | Receiver of the message if is channel of a server |
| isPrivateMessage | Boolean | True, if receiver is a user |
| message | Message | Information about the message |

<b>User</b>
|Field|Type|Description|
| :---------------- | :----------------: | :---------------- |
| avatar | String | User's avatar link |
| username | String | User's user name |
| nickname | String | User's nick name |
| email | String | User's email address |
| isOnline | Boolean | True, if user is online |
 
<b>Server</b>
|Field|Type|Description|
| :---------------- | :----------------: | :---------------- |
| id | String | Server's unique id |
| name | String | Server's name |
| avatar | String | Server's avatar link |
| channels | Array of Server.Channel | List of all channel and groups in this server |
 
<b>Server.Channel</b>
|Field|Type|Description|
| :---------------- | :----------------: | :---------------- |
| id | String | Channel's unique id |
| name | String | Channel's name |
| type | Integer | 0 if is a channel, 1 if is a group |
 
<b>Message</b>
|Field|Type|Description|
| :---------------- | :----------------: | :---------------- |
| text | String | Message's text |
| time | Integer | Date the message was sent in Unix time |
| fromId | String | Id of sender of this message |
| toId | String | Id of receiver of this message |
 
</p></details>
