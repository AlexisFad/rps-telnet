# Камень-ножницы-бумага telnet (NETTY)
TCP сервер реализованный с помощью Netty и Spring-boot

## Запуск
    $ mvn spring-boot:run

* Соединение с сервером осуществляется через telnet.
```
    $ telnet localhost 8090
    Trying ::1...
    Connected to localhost.
    Escape character is '^]'.
    Welcome to the game "rock, paper, scissors"
    Trying find opponent, please wait
```
* После того как оппонент будет найден, начнется игра
```
Opponent was find, start game
Available commands: rock, paper, scissors
Your move -> 
```


