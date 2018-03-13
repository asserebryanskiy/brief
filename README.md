# Brief

Freelance project.

Site for offline game "Brief" of [MOZ](http://www.zamyshlyaev.com/) (Masterskaya Olega Zamyshlaeva). 

Gradle project on Spring Boot, Spring Security, Thymeleaf, Websocket, JPA with H2 database. 

Link to a project - https://moz-brief.herokuapp.com/ 
To enter as a player use "test" for "Код сессии" and "1" for "Назвнание комманды".
To enter as a moderator use "moderator1" as login and "password" as password. 

#### Spring Security
Makes use of two login pages, additional security filter (see config/authentication/PlayerAuthenticationFilter.java) and sessionRegistry to programmatically logout users. 
