


var currentResultsRows = 0 

function clearScoreTable(){
	
	var stable = document.getElementById("scoretable")
	
	for(var j = document.getElementById("scoretable").rows.length; j > 0;j--)
{
document.getElementById("scoretable").deleteRow(j -1);
}
	
	
	currentResultsRows = 0 
}


 function updateScoreTable(nombrejugador , posicion){
	
	
	
	var stable = document.getElementById("scoretable")
	

	var newestScoreRow = stable.insertRow(currentResultsRows)
		 var nombrej = newestScoreRow.insertCell(0);
		 var posicionj = newestScoreRow.insertCell(1);
		
			nombrej.innerHTML  = nombrejugador
			posicionj.innerHTML = posicion
		
			currentResultsRows++
}