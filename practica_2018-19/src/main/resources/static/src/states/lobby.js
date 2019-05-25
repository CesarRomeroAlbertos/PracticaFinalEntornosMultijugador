Spacewar.lobbyState = function(game) {

}

function startnext(){
	game.state.start('roomState')

}


Spacewar.lobbyState.prototype = {

	init : function() {
		if (game.global.DEBUG_MODE) {
			console.log("[DEBUG] Entering **LOBBY** state");
			
		}
	},

	preload : function() {

	},

	create : function() {
		
		let requestmsg = {
				event : "REQUEST ALL EXISTING ROOMS"
				
		}
		game.global.socket.send(JSON.stringify(requestmsg))

		
		var gamediv = document.getElementById("gameDiv")
		var div = document.createElement("div");
		div.setAttribute("id","deletethis")
		div.style.width = "700px";
		div.style.height = "300px";
		div.style.background = "red";
		div.style.color = "white";
		div.style.position = "absolute";
		div.style.zIndex = "1000";
		div.style.left =  "200px";
		div.style.top =  "150px";
		gamediv.appendChild(div);
		div.style.overflow = "auto"
		var roomtablediv = document.createElement("div");
		roomtablediv.style.overflow = "auto"
		var roomtable = document.createElement("table");
		roomtable.setAttribute('id','roomtable')

		div.appendChild(roomtablediv)
		roomtablediv.appendChild(roomtable)
		
	
	
		var newroomname
		
		function makearoom(rname){
			let roommsg = {
			event : "MAKE ROOM",
			roomcreator : game.global.myPlayer.name,
			roomname : rname 
			}
			game.global.socket.send(JSON.stringify(roommsg))
		}
		
		function joinaroom(){
			let message = {
					event : 'JOIN ROOM',
					id : game.global.myPlayer.id
					
				}
				game.global.socket.send(JSON.stringify(message))
		}
		
		function makeroomprompt(){
			 let rname = prompt("Name your room");
			 if (rname){
				 makearoom(rname)
			 }
		}
		
		var makeroombutton = game.add.button(game.world.centerX ,game.world.centerY + 200, "makeroombutton" , makeroomprompt, this);
		
	
		
	},

	update : function() {
	
		
	

	}
}