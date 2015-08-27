# Spring Security OAuth2 Fiware Connector

---
## What do I do?

I am simple Spring MVC application which secured by Spring Security. Instead of using simple form based security, I am
secured by Spring Security OAuth2 and the OAuth provider is Fiware.

## Get it up and runnning
The project is built with Maven so you can run the build using the following maven command
`mvn clean package`

To start up the application using the following command (Since it is a Spring boot application)
`mvn spring-boot:run`

The application.properties (in src/main/resources) contains the details of the Fiware application which it uses to authenticate details.
Change the values of the following attributes to the values for your application
client.id
client.secret

###To register a Fiware App perform the following steps
* Go to https://account.lab.fiware.org/idm/myApplications and login with your Fiware account
* If you don't have an application create a new one and then click into the app.
* In the menu on the right side select "OAuth2 Credentials" and the api keys will appear.

 When you have a the Fiware App configured and the Spring boot application and you navigate to http://localhost:9000. It will redirect
 you to a Fiware login page. Upong login it will ask you to authorize your application for access to your account to get email and profile
 data. On successful login it will render a page showing you fiware profile information.

This project is based on https://github.com/skate056/spring-security-oauth2-google