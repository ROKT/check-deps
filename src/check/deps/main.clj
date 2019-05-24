(ns check.deps.main
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [clojure.tools.deps.alpha :as deps])
  (:import java.io.File
           java.util.Arrays
           [org.owasp.dependencycheck.dependency Dependency Vulnerability]
           org.owasp.dependencycheck.Engine
           org.owasp.dependencycheck.reporting.ReportGenerator
           [org.owasp.dependencycheck.utils Settings Settings$KEYS]))

(defn- ->engine []
  (let [settings (Settings.)]
    (.setString settings Settings$KEYS/SUPPRESSION_FILE "dev/resources/nvd-suppression.xml")
    (Engine. settings)))

;;

(defn update-database! []
  (println "Updating vulnerability database...")
  (.doUpdates ^Engine (->engine))
  (println "Done"))

;;

(defn- deps-map []
  (-> "deps.edn" slurp edn/read-string (deps/resolve-deps nil)))

(defn- jar-files []
  (->> (deps-map)
       vals
       (map (comp io/file first :paths))))

(defn- scan-files! [files]
  (let [^Engine engine (->engine)]
    (println "Scanning" (count files) "files")
    (doseq [^File file files]
      (println "Scanning " (.getCanonicalPath file) "...")
      (.scan engine file))
    engine))

(defn- analyze! [^Engine engine]
  (println "Analyzing...")
  (.analyzeDependencies engine)
  engine)

(defn- write-report! [^Engine engine]
  (let [output-dir "target/nvd"
        output-fmt "ALL"
        db-props (.getDatabaseProperties (.getDatabase engine))
        deps (Arrays/asList (.getDependencies engine))
        analyzers (.getAnalyzers engine)
        settings (.getSettings engine)
        gen (ReportGenerator. "NVD Report" deps analyzers db-props settings)]
    (.write gen ^String output-dir ^String output-fmt)
    (println "Wrote report to" output-dir))
  engine)

(defn- dep->vulnerabilities [^Dependency dep]
  (let [vulns (.getVulnerabilities dep)]
    (for [^Vulnerability v vulns]
      (.getName v))))

(defn- vulnerabilities [^Engine engine]
  (for [^Dependency dep (.getDependencies engine)]
    {:name (.getFileName dep)
     :cves (dep->vulnerabilities dep)}))

(defn- deps-with-vulnerabilities [engine]
  (filter (fn [{:keys [cves]}] (seq cves)) (vulnerabilities engine)))

(defn- deps-without-vulnerabilities [engine]
  (filter (fn [{:keys [cves]}] (empty? cves)) (vulnerabilities engine)))

(defn- print-summary! [engine]
  (println "Deps with no vulnerabilities:")
  (pprint/pprint (sort (map :name (deps-without-vulnerabilities engine))))

  (println "Deps with vulnerabilities:")
  (pprint/pprint (sort-by :name (deps-with-vulnerabilities engine)))
  engine)

(defn- fail-build? [engine]
  (if (seq (deps-with-vulnerabilities engine))
    (System/exit 1)
    (System/exit 0)))

(defn check []
  (update-database!)
  (-> (jar-files)
      scan-files!
      analyze!
      print-summary!
      write-report!))

(defn -main []
  (fail-build? (check)))
