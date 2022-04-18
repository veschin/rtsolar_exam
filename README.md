# Тестовое задание для Ростелеком-Солар  
### Получение статистики с серверов StackOverflow 


## Требования

- Java8  
- Clojure / Clj / RlWrap  
- Make  
- Curl  


## Использование

```sh
make build
make run_server
```

```sh
curl -X GET http://localhost:8080/search?tag=...
``` 

или 

```sh
make test_req
```

Чтобы убить процесс, используйте 
```sh 
make kill_server
```  

## Ожидаемый вывод

```json
{
  "clojure" : {
    "total" : 113,
    "answered" : 137
  },
  "scala" : {
    "total" : 138,
    "answered" : 42
  }
```