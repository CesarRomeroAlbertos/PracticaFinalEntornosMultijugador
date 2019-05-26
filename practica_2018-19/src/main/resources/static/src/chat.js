var maxRows = 25
var currentRows = 0
var table

function sendChatMsg(){
	var input = document.getElementById("msgField").value
	console.log("you just said " + input)
	let name  = game.global.myPlayer.name
	let cmessage = {
		room : game.global.room.id ,
		message : input,
		event : "CHAT MESSAGE",
		player : name
		
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


