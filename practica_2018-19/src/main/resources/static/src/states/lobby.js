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
		
		
		
		
		game.add.tileSprite(0, 0,1024, 600, "roomsbackground");
		
	

		
	  function loadRoomTableSurface(){
		
		var gamediv = document.getElementById("gameDiv")
		var div = document.createElement("div");
		div.setAttribute("id","deletethis")
		div.style.width = "700px";
		div.style.height = "300px";
		div.style.background = "rgba(182, 54, 236, 0.68)"; 
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
		}
		
	  async function requestRoomTableData(){
		  await(loadRoomTableSurface())
			let requestmsg = {
					event : "REQUEST ALL EXISTING ROOMS"
					
			}
			game.global.socket.send(JSON.stringify(requestmsg))
	  }
	  requestRoomTableData()
	
	
		var newroomname
		
		async function makearoom(rname){
		  console.log(game.global.myPlayer)
		 let myname = String(game.global.myPlayer.name)
			let roommsg = {
			event : "MAKE ROOM",
			roomcreator : myname,
			roomname : rname 
			}
			game.global.socket.send(JSON.stringify(roommsg))
		}
		
		
		
		function makeroomprompt(){
			 let rname = prompt("Name your room");
			 if (rname){
				 makearoom(rname)
			 }
		}
		
		function matchMakingAuto()
		{
			let roommsg = {
					event : "JOIN ROOM"
					}
					game.global.socket.send(JSON.stringify(roommsg))
		}
		
		var makeroombutton = game.add.button(game.world.centerX-75 ,game.world.centerY + 200, "makeroombutton" , makeroomprompt, this);
		var matchmakemebutton = game.add.button(game.world.centerX+75 , game.world.centerY+200,"matchmakemebutton",matchMakingAuto,this);
	
		
	},

	update : function() {
	
		
	

	}
}