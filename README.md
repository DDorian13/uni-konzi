# UNI-KONZI
University consultation API with java spring, mongodb

`under development, will be finished for the end of May 2021`
## application.properties file is MISSING
You can configure your own mongoDB Atlas cluster with URI and database.\
`spring.data.mongodb.uri={your_cluster_uri}`\
`spring.data.mongodb.database={database_name}`\
In this file you must set `unikonzi.app.jwtSecret` which will be used for encoding password and `unikonzi.app.jwtExpirationMs` to set the length of time until the token will expire 
