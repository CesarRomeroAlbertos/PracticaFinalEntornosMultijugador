Spacewar.scoresState = function(game) {

}

Spacewar.scoresState.prototype = {



	preload : function() {
	
	},

	create : function() {
		var gamediv = document.getElementById("gameDiv")
		var scorediv = document.createElement("div");
		scorediv.setAttribute("id","deletethis2")
		scorediv.style.width = "700px";
		scorediv.style.height = "300px";
		scorediv.style.background = "yellow";
		scorediv.style.color = "white";
		scorediv.style.position = "absolute";
		scorediv.style.zIndex = "1000";
		scorediv.style.left =  "200px";
		scorediv.style.top =  "150px";
		gamediv.appendChild(scorediv);
		scorediv.style.overflow = "auto"
		var scoretablediv = document.createElement("div");
		scoretablediv.style.overflow = "auto"
		var scoretable = document.createElement("table");
		scoretable.setAttribute('id','scoretable')

		scorediv.appendChild(scoretablediv)
		scoretablediv.appendChild(scoretable)
		
	
	},

	update : function() {
		
	}
}