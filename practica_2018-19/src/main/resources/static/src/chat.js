var maxRows = 25
var currentRows = 0

function sendChatMsg(){
	var input = document.getElementById("msgField").value
	console.log("you just said " + input)
	let cmessage = {
		room : game.global.myPlayer.room ,
		message : input,
		event : "CHAT MESSAGE",
		player : game.global.myPlayer.name
		
	}
	game.global.socket.send(JSON.stringify(cmessage))

}


var table = document.getElementById("chatTable")
