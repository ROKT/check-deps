# check-deps
[![Clojars Project](https://img.shields.io/clojars/v/rokt/check-deps.svg)](https://clojars.org/rokt/check-deps)

Check a clojure.tools.deps project's dependencies using OWASP Dependency Check

## Usage

### Command Line
```bash
$ clj -Sdeps '{:deps {rokt/check-deps {:mvn/version "0.1.1"}}}' -m check.deps.main
```

### As a deps.edn alias
```clojure
  :nvd
  {:extra-deps {rokt/check-deps {:mvn/version "0.1.1"}}
   :main-opts ["-m" "check.deps.main"]}
```
