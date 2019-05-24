
var currentRoomRows = 0 

function updateRoomTable(nombrecreador,nombresala){
	
	var table = document.getElementById("roomtable")
	var newestRoomRow = table.insertRow(currentRoomRows)
		 var creator = newestRoomRow.insertCell(0);
		 var roomname = newestRoomRow.insertCell(1);
		 
		 
		creator.innerHTML  = nombrecreador
		roomname.innerHTML = nombresala
		
		currentRoomRows++
		
}


function pruebadefuego(){
updateRoomTable("hola","mundo")
}
