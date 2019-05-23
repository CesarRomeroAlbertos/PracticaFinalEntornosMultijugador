window.onload = function() {

	game = new Phaser.Game(1024, 600, Phaser.AUTO, 'gameDiv')

	// GLOBAL VARIABLES
	game.global = {
		FPS : 30,
		DEBUG_MODE : false,
		DEBUG_MODE_CARMEN : true,
		socket : null,
		myPlayer : new Object(),
		otherPlayers : [],
		projectiles : [],
		
		
	}
	var otherstyle = { font: "bold 32px Arial", fill: "#ffffff", boundsAlignH: "center", boundsAlignV: "middle" }

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
		
		switch (msg.event) {
		case "SET NAME" :
			if(msg.id == game.global.myPlayer.id){
			game.global.myPlayer.name = msg.name
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
			if (game.global.DEBUG_MODE) {
				console.log('[DEBUG] ID assigned to player: ' + game.global.myPlayer.id)
			}
			break
		case 'NEW ROOM' :
			if (game.global.DEBUG_MODE) {
				console.log('[DEBUG] NEW ROOM message recieved')
				console.dir(msg)
			}
			game.global.myPlayer.room = {
					name : msg.room
			}
			break
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
									playerIsGhost : player.isGhost
							}
							console.log("NAME IS " + player.name)
							game.global.otherPlayers[player.id].image.anchor.setTo(0.5, 0.5)
							
						} else  {
							if (!game.global.otherPlayers[player.id].playerIsGhost){
							game.global.otherPlayers[player.id].image.x = player.posX
							game.global.otherPlayers[player.id].image.y = player.posY
							game.global.otherPlayers[player.id].image.angle = player.facingAngle
							
							}
							else{
								game.global.otherPlayers[player.id].image.alpha = 0
							}
							if ((game.global.otherPlayers[player.id].image) && player.name != null && player.id != game.global.myPlayer.id) {
								game.global.otherPlayers[player.id] = {
								name :  game.add.text(player.posX, player.posY + 30, player.name ,otherstyle)
								}
								game.global.otherPlayers[player.id].name.fontSize = 20;
								game.global.otherPlayers[player.id].image.addChild(game.global.otherPlayers[player.id].name)
							
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
		case 'UPDATE HEALTH':
			game.global.myPlayer.health -=1
			game.global.myPlayer.myHCounter.text =  game.global.myPlayer.health;
			if (game.global.myPlayer.health <= 0 ){
			
				let msg = {
						event : "PLAYER DEAD"		
				}
				game.global.socket.send(JSON.stringify(msg))
			}
		break
		case "PLAYER GHOST":
			if(msg.id == game.global.myPlayer.id){
			game.global.myPlayer.playerIsGhost = true
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

	game.state.start('bootState')

}