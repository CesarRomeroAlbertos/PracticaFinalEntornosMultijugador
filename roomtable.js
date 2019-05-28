
var currentRoomRows = 0 
//Funcion para limpiar la tabla cuando hay una actualizacion

function clearTable(){
	
	var table = document.getElementById("roomtable")
	if(document.getElementById("roomtable") != null)
		{
	if( document.getElementById("roomtable").rows.length > 0){
	
	for(var i = document.getElementById("roomtable").rows.length; i > 0;i--)
{
document.getElementById("roomtable").deleteRow(i -1);
}
	
	currentRoomRows = 0 
	}
		}
}
//Funcion que sirve para esperar el limpiado de la tabla antes de avisar al servidor de que esta limpia y que puede enviar informacion
async function tableisClear(){
	await(clearTable())
	
	let msg = {
		event : "TABLE CLEARED WARNING"
	}
	game.global.socket.send(JSON.stringify(msg))

}
//Funcion para escribir una fila de la tabla cuando el cliente envia un mensaje de actualizacion

function updateRoomTable(nombrecreador,nombresala,idsala ,gentedentro , capacidad){
	
	
	
	var table = document.getElementById("roomtable")
	

	var newestRoomRow = table.insertRow(currentRoomRows)
		 var creator = newestRoomRow.insertCell(0);
		 var roomname = newestRoomRow.insertCell(1);
		 var roomid = newestRoomRow.insertCell(2)
		 var rowbuttonspace = newestRoomRow.insertCell(3);
		 var roomplayers = newestRoomRow.insertCell(4);
	
			creator.innerHTML  = nombrecreador
			roomname.innerHTML = nombresala
			roomid.innerHTML = idsala
			roomplayers.innerHTML = gentedentro +"/" + capacidad
			currentRoomRows++
		 
			let promise = new Promise((ressolve, reject)=>
			{
				
			}
					)
			
		 var rowbutton = document.createElement("button")
		 rowbutton.innerHTML = "join"
		 rowbutton.addEventListener ("click", function() {
				 let myid = roomid.innerHTML
			 let msg = {
					 event : "JOIN EXISTING ROOM",
					 roomid : myid
			 }
			 
			 game.global.socket.send(JSON.stringify(msg))
			 
				});
	
			 rowbuttonspace.appendChild(rowbutton)
}


function pruebadefuego(){
updateRoomTable("hola","mundo")
}
