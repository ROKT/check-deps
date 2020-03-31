.DEFAULT_GOAL := build

pom.xml: deps.edn
	clojure -Spom

.PHONY: build
build: pom.xml
	rm target/check-deps.jar
	clojure -A:jar

.PHONY: install
install: build
	clojure -A:install

.PHONY: deploy
deploy: build
	clojure -A:deploy
