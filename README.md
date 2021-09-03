# project-service

The project-service service is used to create/update/search/delete and publish the project. On publish, it will push the
project record to kafka. It will be consumed by project-search-service

# Prerequisites

kakfa should up and run on 9092
mariaDB should up and run on 3306

Please use the ENV variable to change the other configurations. Please refer application.yml Please use spring profiles
to use ENV specific configurations

# To build the project

Use eclipse/intellij or manually build the project using maven -> "**mvn clean compile package**" from the project
directory

# To start the project manually

java -jar -Dspring.profiles.active=dev ./target/app.jar

# To start the entire setup in "docker" ENV

Please refer **project-setup** repository

# To know more about APIs

use swagger API -> http://localhost:4202/swagger-ui.html

Also, can download image (santhoshas1990/project-service:latest) from dokcer hub
