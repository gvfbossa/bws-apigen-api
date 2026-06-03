# BWS APIGEN - Backend Application API

## Configuração da Aplicação

1- Criar o Dockerfile na raíz do projeto
2- Criar o docker-compose.yaml -> Não esquecer de configurar o .env
3- Copiar o .env da raíz do projeto para a pasta do deploy do projeto no servidor

## Deploy da Aplicação

1- Gerar a imagem:
`mvn clean install -DskipTests && docker build -t bossaws2024/bwsapigen:latest .`

2- Publicar a imagem no dockerhub:
`docker push bossaws2024/bwsapigen:latest`

#### ATENÇÃO -> CUIDADO COM O `-v`
3- Baixar e subir a imagem no servidor para executá-la:
No servidor, na pasta do docker-compose, rode: 
`docker-compose down && docker rmi bossaws2024/bwsapigen:latest && docker-compose up -d`

## Configurar Certificado e NGINX

### 1 - Criar o subdomínio
Criar um novo subdomínio no `Registro.br`

### 2 - Lembre-se de expor a porta desejada da API no Docker
Exemplo:
```yaml
services:
  api:
    ports:
      - "8445:8080"
```

### 3 - Criar o arquivo `.conf` do NGINX
Navegue para:
```bash
cd /home/workspace/nginx/conf.d
```
Crie:
```bash
nano bwsapigen.conf
```
Exemplo:
```nginx
server {
    listen 80;
    listen [::]:80;
    server_name api.bwsapigen.bossawebsolutions.com.br;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 301 https://$host$request_uri;
    }
}

server {
    listen 443 ssl;
    listen [::]:443 ssl;
    server_name api.bwsapigen.bossawebsolutions.com.br;

    ssl_certificate     /etc/letsencrypt/live/api.bwsapigen.bossawebsolutions.com.br/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.bwsapigen.bossawebsolutions.com.br/privkey.pem;

    location / {
        proxy_pass http://127.0.0.1:8445;

        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Substitua de acordo:
* Domínio
* Caminho do Certificado
* Porta da API

### 4 - Gerar o Certificado SSL
Rode o comando:
```bash
docker exec -it certbot-global certbot certonly \
  --webroot \
  -w /var/www/certbot \
  -d api.bwsapigen.bossawebsolutions.com.br
```

---

### 5 - Valide a configuração do NGINX
Rode:
```bash
docker exec nginx-global nginx -t
```

### 6 - Reload no NGINX
Rode:
```bash
docker exec nginx-global nginx -s reload
```
---

### 7 - Teste a Aplicação
Exemplo:
```bash
curl https://api.bwsapigen.bossawebsolutions.com.br
```