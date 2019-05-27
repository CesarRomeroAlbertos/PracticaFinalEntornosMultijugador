Spacewar.scoresState = function(game) {

}

Spacewar.scoresState.prototype = {



	preload : function() {
	
	},

	create : function() {
		function loadtablesurface(){
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
		}
		async function requestInfo(){
			await(loadtablesurface())
		
			let scoremsg = {
					event : "PLAYER WANTS RESULTS",
					roomid : game.global.room.id
			}
			game.global.socket.send(JSON.stringify(scoremsg))
		}
		requestInfo()
		
		function deleteScoreTable(){
			var a = document.getElementById("deletethis2")
			a.parentNode.removeChild(a);
		}
		async function backtolobby(){
			await(deleteScoreTable())
			


			let btlmsg = {
					event : "SEND BACK TO LOBBY"
					
			}
			game.global.socket.send(JSON.stringify(btlmsg))
			
		}
		async function actuallyquit(){
			await(deleteScoreTable())
			let btmmsg = {
			event:"SEND BACK TO MENU"
			}
			game.global.socket.send(JSON.stringify(btmmsg))
		}
		
		
		var backtolobbybutton = game.add.button(game.world.centerX-75 ,game.world.centerY + 200, "backtolobbybutton" , backtolobby, this);
		var actualquitbutton = game.add.button(game.world.centerX+75 ,game.world.centerY + 200, "actualquitbutton" , actuallyquit, this);
		
		
	
	},

	update : function() {
		
	}
}