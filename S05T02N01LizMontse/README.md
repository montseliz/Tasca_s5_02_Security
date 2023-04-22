# Dice Game API amb autenticació JWT

## Swagger



## Postman
### Environment:

![environtment.PNG](captures%2Fenvirontment.PNG)

## Fase 1 (MySQL)

### Request POST: 

- #### /api/auth/register: registra un jugador/a

![register.PNG](captures%2Fregister.PNG)
![register4.PNG](captures%2Fregister4.PNG)
![register2.PNG](captures%2Fregister2.PNG)
![register3.PNG](captures%2Fregister3.PNG)

- #### /api/auth/login: inici de sessió d'un jugador/a

![login.PNG](captures%2Flogin.PNG)
![login2.PNG](captures%2Flogin2.PNG)
![login3.PNG](captures%2Flogin3.PNG)

- #### /players/{id}/game: un jugador/a específic realitza una tirada de daus

![newgame.PNG](captures%2Fnewgame.PNG)
![newgame2.PNG](captures%2Fnewgame2.PNG)

### Request PUT:

- #### /players/update/{id}: modifica el nom del jugador/a

![update.PNG](captures%2Fupdate.PNG)
![update2.PNG](captures%2Fupdate2.PNG)
![update3.PNG](captures%2Fupdate3.PNG)
![update4.PNG](captures%2Fupdate4.PNG)

### Request DELETE: 

- #### /players/{id}/games: elimina les tirades del jugador/a

![delete.PNG](captures%2Fdelete.PNG)
![delete2.PNG](captures%2Fdelete2.PNG)
![delete3.PNG](captures%2Fdelete3.PNG)

### Request GET: 

- #### /players/: retorna el llistat de tots els jugadors/es del sistema amb el seu percentatge mitjà d’èxits

![getall.PNG](captures%2Fgetall.PNG)

- #### /players/{id}/games: retorna el llistat de jugades per un jugador/a

![getallgames.PNG](captures%2Fgetallgames.PNG)
![getallgames2.PNG](captures%2Fgetallgames2.PNG)
![getallgames3.PNG](captures%2Fgetallgames3.PNG)

- #### /players/ranking: retorna el ranking mig de tots els jugadors/es del sistema. És a dir, el percentatge mitjà d’èxits

![ranking.PNG](captures%2Franking.PNG)
![ranking2.PNG](captures%2Franking2.PNG)

- #### /players/ranking/loser: retorna el jugador/a amb pitjor percentatge d’èxit

![loser.PNG](captures%2Floser.PNG)
![loser2.PNG](captures%2Floser2.PNG)

- #### /players/ranking/winner: retorna el jugador amb pitjor percentatge d’èxit

![winner.PNG](captures%2Fwinner.PNG)
![winner2.PNG](captures%2Fwinner2.PNG)

### MySQL

![mysqlplayers.PNG](captures%2Fmysqlplayers.PNG)
![mysqlroles.PNG](captures%2Fmysqlroles.PNG)
![mysqlgames.PNG](captures%2Fmysqlgames.PNG)

## Fase 2 (MongoDB)

### Request POST:

- #### /players/add: crea un jugador/a



- #### /players/{id}/game: un jugador/a específic realitza una tirada de daus



### Request PUT:

- #### /players/update/{id}: modifica el nom del jugador/a



### Request DELETE:

- #### /players/{id}/games: elimina les tirades del jugador/a


### Request GET:

- #### /players/: retorna el llistat de tots els jugadors/es del sistema amb el seu percentatge mitjà d’èxits



- #### /players/{id}/games: retorna el llistat de jugades per un jugador/a



- #### /players/ranking: retorna el ranking mig de tots els jugadors/es del sistema. És a dir, el percentatge mitjà d’èxits



- #### /players/ranking/loser: retorna el jugador/a amb pitjor percentatge d’èxit



- #### /players/ranking/winner: retorna el jugador amb pitjor percentatge d’èxit



### MongoDB


