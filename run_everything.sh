cd assina-server
mvn clean install
java -jar assina-app/target/assina-app-0.0.1-SNAPSHOT.jar
java -jar assina-sa/target/assina-sa-0.0.1-SNAPSHOT.jar

cd ../assina-client

npm i --save
npm start