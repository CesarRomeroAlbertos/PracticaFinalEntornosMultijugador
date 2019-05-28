window.onload = function() {

	game = new Phaser.Game(1024, 600, Phaser.AUTO, 'gameDiv')

	// GLOBAL VARIABLES
	game.global = {
		FPS : 30,
		DEBUG_MODE : false,
		socket : null,
		myPlayer : new Object(),
		otherPlayers : [],
		projectiles : [],
		room : new Object(),
		
		
		
	}
	var otherstyle = { font: "bold 32px Arial", fill: "#ffffff", boundsAlignH: "center", boundsAlignV: "middle" }
	var style = { font: "bold 28px Arial", fill: "#ffffff", boundsAlignH: "center", boundsAlignV: "middle" };
	

	
	// WEBSOCKET CONFIGURATOR
	game.global.socket = new WebSocket("ws://127.0.0.1:8080/spacewar")
	
	game.global.socket.onopen = () => {
		if (game.global.DEBUG_MODE) {
			console.log('[DEBUG] WebSocket connection opened.')
		}
	}

	game.global.socket.onclose = () => {
		if (game.global.DEBUG_MODE) {
			console.log('[DEBUG] WebSocket connection closed.')
		}
	}
	
	game.global.socket.onmessage = (message) => {
		var msg = JSON.parse(message.data)
		// console.log(msg.event)
		switch (msg.event) {
		
		//Case para cuando la sala a la que quiere unirse el jugador esta llena
		case "ROOM DENIED":
			alert("Esta sala esta llena , no puedes conectarte")
			break
		//Case para limpiar la tabla de salas antes de escribir en ella
		case "CLEAR TABLE":
			tableisClear()
			break
		//Case que sirve para recibir permiso del servidor para ver las puntuaciones
		case "SCORE PERMISSION":
			game.global.myPlayer.isResults = true
			game.state.start("scoresState")
			break
		
		case "START GAME" :
			game.state.start('gameState')
			break
		//Case para ir a la pantalla de puntuaciones de forma obligatoria al terminar el juego	
		case "FORCE SCORES" :
			game.global.myPlayer.isResults = true
			game.state.start("scoresState")
			break
		//Case para recibir el aviso del servidor de que ha cambiado nuestra municion en cualquier sentido	
		case "AMMO UPDATE":
			console.log("ammo =" + msg.ammo)
			game.global.myPlayer.ammo = msg.ammo
			game.global.myPlayer.myAmmoCounter.text =  game.global.myPlayer.ammo;

			
			break
		//Case para recibir un mensaje de actualizacion de resultados y llamar a la funcion de insercion de fila de esta
		case "UPDATE SCORE TABLE":
			if(document.getElementById("scoretable")){
			if(game.global.myPlayer.isResults){
			updateScoreTable(msg.playername,msg.position)
			}
			}
		
			break
		//Case para gestionar cuando el servidor nos asigne una sala y pasar al estado de espera
		case "ROOM ASIGNED":
			game.global.room.name= msg.roomname
			game.global.room.id = msg.roomid
			game.global.myPlayer.isWaiting = true

			
			let requestroomstatus = {
				event : "REQUEST ROOM STATUS",
				roomid : game.global.room.id
			}

			game.global.socket.send(JSON.stringify(requestroomstatus))
			
			startnext()
		
			break
		//Case para cuando el servidor ha cancelado nuestra participacion en la partida de una sala para volver a lobby	
		case "CANCELED UPDATE":
			function cWait(){
			game.global.myPlayer.isWaiting = false
			game.global.myPlayer.isResults= false

			}
			async function gTL(){
				await(cWait())
				goToLobby()

			}
			gTL()
			break;
		//Case para llamar a la funcion de insercion de fila de la tabla de salas cuando el servidor manda una actualizacion
		case "UPDATE ROOM TABLE":
			if (!game.global.myPlayer.isWaiting){
			updateRoomTable(msg.roomcreator,msg.roomname,msg.roomid , msg.playersinside , msg.totalcapacity)
			}
			break
		//Case para gestionar cuando el servidor nos manda un mensaje de chat  , el mensaje se inserta en la tabla de chat
		case "chatMessageReception":
			console.log("tienes un mensaje")
			let latestChatMessage = msg.messageText
			paintNewestMessage(latestChatMessage)
			break
			
		//Case para cuando el servidor nos manda nuestro nombre de jugador 
		case "SET NAME" :
			if(msg.id == game.global.myPlayer.id){
			game.global.myPlayer.name = msg.name
			goToLobby();
			}
			else{
				game.global.otherPlayers[msg.id].name = msg.name
			}
			break
		
		case 'JOIN':
			if (game.global.DEBUG_MODE) {
				console.log('[DEBUG] JOIN message recieved')
				console.dir(msg)
			}
			game.global.myPlayer.id = msg.id
			game.global.myPlayer.shipType = msg.shipType
			game.global.myPlayer.health = msg.health
			game.global.myPlayer.playerIsGhost = msg.ghost
			game.global.myPlayer.isWaiting = false

			if (game.global.DEBUG_MODE) {
				console.log('[DEBUG] ID assigned to player: ' + game.global.myPlayer.id)
			}
			break

		//Modificada para que no se actualicen los jugadores fantasmas 
		case 'GAME STATE UPDATE' :

			if (game.global.DEBUG_MODE) {
				console.log('[DEBUG] GAME STATE UPDATE message recieved')
				console.dir(msg)
			}
			
			if (typeof game.global.myPlayer.image !== 'undefined') {
				for (var player of msg.players) {
					

					if (game.global.myPlayer.id == player.id) {
						game.global.myPlayer.image.x = player.posX
						game.global.myPlayer.image.y = player.posY
						game.global.myPlayer.image.angle = player.facingAngle
						
					} else {
						if (typeof game.global.otherPlayers[player.id] == 'undefined' ) {
							game.global.otherPlayers[player.id] = {
									image : game.add.sprite(player.posX, player.posY, 'spacewar', player.shipType),
									playerIsGhost : player.isGhost,
									name : game.add.text(player.posX, player.posY - 40, player.name ,otherstyle)
							}
							console.log("NAME IS " + player.name)
							game.global.otherPlayers[player.id].image.anchor.setTo(0.5, 0.5)
							
						} else  {
							if (!game.global.otherPlayers[player.id].playerIsGhost){
							game.global.otherPlayers[player.id].image.x = player.posX
							game.global.otherPlayers[player.id].image.y = player.posY
							game.global.otherPlayers[player.id].image.angle = player.facingAngle
							game.global.otherPlayers[player.id].name.x = player.posX-40;
							game.global.otherPlayers[player.id].name.y = player.posY-40;
							game.global.otherPlayers[player.id].name.fontSize = 20
							
							}
							else{
								game.global.otherPlayers[player.id].image.alpha = 0
							}
						
						}
					
					}
				}
				
				for (var projectile of msg.projectiles) {
					if (projectile.isAlive) {
						game.global.projectiles[projectile.id].image.x = projectile.posX
						game.global.projectiles[projectile.id].image.y = projectile.posY
						if (game.global.projectiles[projectile.id].image.visible === false) {
							game.global.projectiles[projectile.id].image.angle = projectile.facingAngle
							game.global.projectiles[projectile.id].image.visible = true
						}
					} else {
						if (projectile.isHit) {
							// we load explosion
							
							let explosion = game.add.sprite(projectile.posX, projectile.posY, 'explosion')
							explosion.animations.add('explosion')
							explosion.anchor.setTo(0.5, 0.5)
							explosion.scale.setTo(2, 2)
							explosion.animations.play('explosion', 15, false, true)
						}
						game.global.projectiles[projectile.id].image.visible = false
					}
				}
			}
			break
			
			//Case para cuando el servidor le indica al jugador que ha perdido vida al jugador
		case 'UPDATE HEALTH':
			game.global.myPlayer.health -=1
			game.global.myPlayer.myHCounter.text =  game.global.myPlayer.health;
			
			
		break
		//Case para cuando el servidor le avisa al cliente de que borre la tabla pues tiene una actualizacion para el
		case "CLEAR RESULTS TABLE":
		async function waitforclear(){
			await(clearScoreTable())
			
			let wmsg = {
				event : "CLEAR RESULTS WARNING"
			}
			game.global.socket.send(JSON.stringify(wmsg))
		}
		if(game.global.myPlayer.isResults){
		waitforclear()
		}
		break
		//Case para pasar al menu cuanddo el jugador decide marcharse del todo tras perder y volver al menu en lugar de a lobby
		case "END OF GAME":
			game.state.start("menuState")
			break
		//Case para cuando un jugador muere y ha de transformarse en fantasma , si el jugador especificado en el  mensaje no es el cliente , este lo elimina
		//Si es el cliente , este se pone en estado fantasma 
		case "PLAYER GHOST":
			if(msg.id == game.global.myPlayer.id){
			game.global.myPlayer.playerIsGhost = true
			activateGhost()
			}
			else{
				game.global.otherPlayers[msg.id].playerIsGhost = true
			}
	
		break;

		case 'REMOVE PLAYER' :
			if (game.global.DEBUG_MODE) {
				console.log('[DEBUG] REMOVE PLAYER message recieved')
				console.dir(msg.players)
			}
			if(msg.id == game.global.myPlayer.id ){
				 game.global.myPlayer.id = "undefined"
					
			}
			game.global.otherPlayers[msg.id].image.destroy()
			delete game.global.otherPlayers[msg.id]
		default :
			console.dir(msg)
			break
		}
	}

	// PHASER SCENE CONFIGURATOR
	game.state.add('bootState', Spacewar.bootState)
	game.state.add('preloadState', Spacewar.preloadState)
	game.state.add('menuState', Spacewar.menuState)
	game.state.add('lobbyState', Spacewar.lobbyState)
	game.state.add('matchmakingState', Spacewar.matchmakingState)
	game.state.add('roomState', Spacewar.roomState)
	game.state.add('gameState', Spacewar.gameState)
	game.state.add('scoresState', Spacewar.scoresState)


	game.state.start('bootState')

}