
var currentRoomRows = 0 

function updateRoomTable(nombrecreador,nombresala,idsala){
	
	var table = document.getElementById("roomtable")
	var newestRoomRow = table.insertRow(currentRoomRows)
		 var creator = newestRoomRow.insertCell(0);
		 var roomname = newestRoomRow.insertCell(1);
		 var roomid = newestRoomRow.insertCell(2)
		 var rowbuttonspace = newestRoomRow.insertCell(3);
		
		 
		 var rowbutton = document.createElement("button")
		 rowbutton.innerHTML = "join"
		 rowbutton.addEventListener ("click", function() {
				 
			 let msg = {
					 event : "JOIN EXISTING ROOM",
					 roomid : roomid.innerHTML
			 }
			 
			 game.global.socket.send(JSON.stringify(msg))
			 startnext()
				});
		 rowbuttonspace.appendChild(rowbutton)
		creator.innerHTML  = nombrecreador
		roomname.innerHTML = nombresala
		roomid.innerHTML = idsala
		
		currentRoomRows++
		
}


function pruebadefuego(){
updateRoomTable("hola","mundo")
}
