
docker network create adm_videos_services
docker network create elastic

sudo chmod go-w app/filebeat/filebeat.docker.yml
mkdir -m 777 .docker
mkdir -m 777 .docker/es01
mkdir -m 777 .docker/keycloak
mkdir -m 777 .docker/filebeat

docker-compose -f services/docker-compose.yaml up -d
docker-compose -f elk/docker-compose.yaml up -d

#echo "Inicializando os containers..."
#sleep 20
