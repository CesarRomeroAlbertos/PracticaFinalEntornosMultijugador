var maxRows = 25
var currentRows = 0
var table

function sendChatMsg(){
	var input = document.getElementById("msgField").value
	console.log("you just said " + input)
	let cmessage = {
		room : game.global.myPlayer.room ,
		message : input,
		event : "CHAT MESSAGE",
		player : game.global.myPlayer.name,
		
	}
	game.global.socket.send(JSON.stringify(cmessage))

}

var count = 0 
function sendChatMsgPRUEBA(){
	paintNewestMessage(count)
	count++
}

function paintNewestMessage(latestChatMessage){
	if (!table){
		 table = document.getElementById("chatTable")
	}
	
		var newestRow = table.insertRow(currentRows)
		 var Name = newestRow.insertCell(0);
		 var Message = newestRow.insertCell(1);
		 
		 Name.innerHTML = "Nombre de la persona :"
		Message.innerHTML = latestChatMessage
		currentRows++
		
	
}

