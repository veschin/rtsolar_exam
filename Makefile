test_req:
	curl -X GET 'http://localhost:8080/search?tag=clojure&tag=scala'

build:
	rm -r -f .calva .cpcache .lsp classes build
	mkdir classes
	clj -M:aot
	clj -M:uberjar
	rm -r -f classes

run_server:
	java -jar build/rtsolar_exam.jar &

kill_server:
	fuser -k 8080/tcp