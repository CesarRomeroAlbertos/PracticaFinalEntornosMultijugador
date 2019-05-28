
var currentResultsRows = 0 
//Funcion para limpiar la tabla cuando hay una actualizacion
function clearScoreTable(){
	
	var stable = document.getElementById("scoretable")
		if( document.getElementById("scoretable").rows.length > 0){
	
	for(var j = document.getElementById("scoretable").rows.length; j > 0 ;  j--)
{
document.getElementById("scoretable").deleteRow(j -1);
}
		}
	
	currentResultsRows = 0 
}
//Funcion para escribir una fila de la tabla cuando el cliente envia un mensaje de actualizacion

 function updateScoreTable(nombrejugador , posicion){
	
	
	
	var stable = document.getElementById("scoretable")
	

	var newestScoreRow = stable.insertRow(currentResultsRows)
		 var nombrej = newestScoreRow.insertCell(0);
		 var posicionj = newestScoreRow.insertCell(1);
		
			nombrej.innerHTML  = nombrejugador
			posicionj.innerHTML = posicion
		
			currentResultsRows++
}