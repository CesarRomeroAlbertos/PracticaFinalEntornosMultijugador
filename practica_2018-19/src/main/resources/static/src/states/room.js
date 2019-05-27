Spacewar.roomState = function(game) {

}

Spacewar.roomState.prototype = {

	init : function() {
		if (game.global.DEBUG_MODE) {
			console.log("[DEBUG] Entering **ROOM** state");
		}
	},

	preload : function() {

	},

	create : function() {
		game.add.tileSprite(0, 0,1024, 600, "readybackground");

		function loadTextvariables(){
			
			game.global.myPlayer.playersWithMe = game.add.text(250, 16, '', { fill: '#ffffff' });
			game.global.myPlayer.playersWithMeReady = game.add.text(250, 24, '', { fill: '#42f4c5' });
		}
		
		async function requestRS(){
			
			await loadTextvariables()

			let requestroomstatus = {
					event : "REQUEST ROOM STATUS",
					roomid : game.global.room.id
					
					
			}
			
			game.global.socket.send(JSON.stringify(requestroomstatus))
			
		}
		
		requestRS()

		
		var thediv = document.getElementById("deletethis")
		  thediv.parentNode.removeChild(thediv);
		
		function cancelReady(){
			let cancelmsg = {
					event: "PLAYER HAS CANCELED",
					roomid : game.global.room.id
			}
			game.global.socket.send(JSON.stringify(cancelmsg))

		}
		
		function imReady () {
			console.log("IM READY")
			
			
			
			let readymsg = {
				event : "PLAYER IS READY",
				roomid : game.global.room.id
			}
			
			game.global.socket.send(JSON.stringify(readymsg))

		}
		
		var readybutton = game.add.button(game.world.centerX ,game.world.centerY, "readybutton" , imReady, this);
		readybutton.anchor.setTo(0.5)
		var cancelbutton = game.add.button(game.world.centerX ,game.world.centerY+75, "cancelbutton" , cancelReady, this);
		cancelbutton.anchor.setTo(0.5)


	},

	update : function() {
	}
}