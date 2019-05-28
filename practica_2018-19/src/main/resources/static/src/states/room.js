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
		
		var thediv = document.getElementById("deletethis") //borramos la tabla de salas  , ya no nos hace falta
		  thediv.parentNode.removeChild(thediv);
		//Funcion para  enviarle al servidor el mensaje de que ya no queremos jugar en esa sala
		function cancelReady(){
			let cancelmsg = {
					event: "PLAYER HAS CANCELED",
					roomid : game.global.room.id
			}
			game.global.socket.send(JSON.stringify(cancelmsg))

		}
		//Funcion para enviarle al servidor el mensaje de que ya estamos listos para jugar 
		function imReady () {
			
			
			
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