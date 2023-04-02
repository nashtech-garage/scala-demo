```bash
openssl req -x509 -nodes -newkey rsa:1024 -days 1\
  -keyout '$path/privkey.pem' \
  -out '$path/fullchain.pem' \
  -subj '/CN=$domain'" certbot
```
