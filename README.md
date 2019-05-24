# check-deps
Check a clojure.tools.deps project's dependencies using OWASP Dependency Check

## Usage

### Command Line
```bash
$ clj -Sdeps '{:deps {rokt/check-deps {:git/url "https://github.com/rokt/check-deps" :sha "<Latest SHA>"}}}' -m check.deps.main
```

### As a deps.edn alias
```clojure
  :nvd
  {:extra-deps {rokt/check-deps {:git/url "https://github.com/rokt/check-deps"
                                 :sha "<Latest SHA>"}}
   :main-opts ["-m" "check.deps.main"]}
```
