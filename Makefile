.DEFAULT_GOAL := build

pom.xml: deps.edn
	clojure -Spom

.PHONY: build
build: pom.xml
	rm target/check-deps.jar
	clojure -A:jar

.PHONY: build
deploy: build
	clojure -A:deploy
