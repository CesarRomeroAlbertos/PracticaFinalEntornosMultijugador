var currentRows = 0
var table
//funcion para enviar un mensaje por el chat 
function sendChatMsg(){
	var input = document.getElementById("msgField").value
	let name  = game.global.myPlayer.name
	let cmessage = {
		room : game.global.room.id ,
		message : input,
		event : "CHAT MESSAGE",
		player : name
		
	}
	game.global.socket.send(JSON.stringify(cmessage))

}


//Funcion para escribir un mensaje que llega desde el servidor 
function paintNewestMessage(latestChatMessage){
	if (!table){
		 table = document.getElementById("chatTable")
	}
	
		var newestRow = table.insertRow(currentRows)
		 var Name = newestRow.insertCell(0);
		 var Message = newestRow.insertCell(1);
		 
		
		Message.innerHTML = latestChatMessage
		currentRows++
		
	
}


